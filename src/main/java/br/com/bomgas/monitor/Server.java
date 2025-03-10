package br.com.bomgas.monitor;

import fi.iki.elonen.NanoHTTPD;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;

public class Server extends NanoHTTPD {
    private boolean isRunning = false;
    private JmDNS jmdns;
    private ServiceInfo serviceInfo;


    public Server(int port) throws IOException {
        super(port);
    }

    public void startServer() {
        if (!isRunning) {
            try {
                start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                isRunning = true;
                LogManager.addLog("Servidor iniciado na porta " + getListeningPort());

                LogManager.addLog("Registrando serviço na rede...");

                new Thread(() -> {
                    try {
                        // Create a JmDNS instance
                        jmdns = JmDNS.create(InetAddress.getLocalHost());

                        // Define the service
                        serviceInfo = ServiceInfo.create("_http._tcp.local.", "BomGasServer", 1234, "BomGasServer");
                        jmdns.registerService(serviceInfo);

                        // Wait a bit
                        Thread.sleep(25000);

                        // Unregister all services
                        jmdns.unregisterAllServices();

                    } catch (Exception e) {
                        LogManager.addLog("Erro ao registrar o serviço: " + e.getMessage());
                    }

                }).start();

            } catch (IOException e) {
                LogManager.addLog("Erro ao iniciar o servidor: " + e.getMessage());
            }
        }
    }

    public void stopServer() {
        if (isRunning) {
            try {
                stop();
                if (jmdns != null) {
                    jmdns.unregisterAllServices();
                    try {
                        jmdns.close();
                    } catch (IOException e) {
                        LogManager.addLog("Erro ao fechar o JmDNS: " + e.getMessage());
                    }
                }

                isRunning = false;
                LogManager.addLog("Servidor parado");
            } catch (Exception e) {
                LogManager.addLog("Erro ao parar o servidor: " + e.getMessage());
            }
        }
    }

    public void restartServer() {
        stopServer();
        startServer();
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (uri.startsWith("/number/")) {
            String phoneNumber = uri.substring("/number/".length());

            // Remove o zero inicial, se existir
            if (phoneNumber.startsWith("0")) {
                phoneNumber = phoneNumber.substring(1);
            }

            // Se tiver mais de 11 caracteres, mantém apenas os últimos 11
            if (phoneNumber.length() > 11) {
                phoneNumber = phoneNumber.substring(phoneNumber.length() - 11);
            }

            LogManager.addLog("📞 Chamada recebida de: " + phoneNumber);

            // URL para abrir no Chrome
            String url = "https://portal.gasdelivery.com.br/secure/client/?primary_phone=" + phoneNumber;

            // Comando para abrir uma nova aba no Chrome
            try {
                String[] command;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    command = new String[]{"cmd", "/c", "start chrome --new-tab " + url};
                } else {
                    command = new String[]{"sh", "-c", "google-chrome --new-tab " + url}; // Para Linux/macOS
                }
                Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                LogManager.addLog("⚠️ Erro ao abrir o navegador: " + e.getMessage());
            }

            return newFixedLengthResponse("OK: " + phoneNumber);
        }
        return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Número inválido.");
    }

}

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
            LogManager.addLog("📞 Chamada recebida de: " + phoneNumber);
            if (phoneNumber.matches("\\d{5,15}")) {
                return newFixedLengthResponse("OK: " + phoneNumber);
            }
        }
        return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Número inválido.");
    }


}

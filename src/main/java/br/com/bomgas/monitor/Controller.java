package br.com.bomgas.monitor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    private ListView<String> logListView;

    @FXML
    private TextField ipField;

    @FXML
    private Button startStopButton;

    @FXML
    private Button restartButton;

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void initialize() {
        logListView.setItems(LogManager.getLogs());
    }

    @FXML
    public void saveIP() {
        String newIP = ipField.getText();
        if (newIP.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            ConfigManager.set("server_ip", newIP);
            LogManager.addLog("🌍 IP atualizado para: " + newIP);
        } else {
            LogManager.addLog("⚠️ IP inválido!");
        }
    }

    @FXML
    private void handleStartStop() {
        if (mainApp.isServerRunning()) {
            mainApp.stopServer();
            startStopButton.setText("Start Server");
        } else {
            mainApp.startServer();
            startStopButton.setText("Stop Server");
        }
    }

    @FXML
    private void handleRestart() {
        mainApp.restartServer();
    }
}

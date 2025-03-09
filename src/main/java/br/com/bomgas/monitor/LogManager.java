package br.com.bomgas.monitor;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {

    private static final ObservableList<String> logs = FXCollections.observableArrayList();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void addLog(String message) {
        LocalDateTime now = LocalDateTime.now();
        String timestampedMessage = "[" + now.format(formatter) +  "] " + message;
        Platform.runLater(() -> logs.add(0, timestampedMessage));
    }

    public static ObservableList<String> getLogs() {
        return logs;
    }
}

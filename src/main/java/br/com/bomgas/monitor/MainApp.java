package br.com.bomgas.monitor;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {
    private Server server;

    public static void main(String[] args) {
        launch();
    }


    @Override
    public void start(Stage stage) throws IOException {
        // Pass in the app's main stage, and path to the icon image

        URL iconURL = getClass().getResource("/phone-volume-solid.png");
        FXTrayIcon trayIcon = new FXTrayIcon.Builder(stage, iconURL)
                .menuItem("Restaurar", e -> {
                    Platform.runLater(() -> {
                        stage.show();
                        stage.toFront();
                    });
                })
                .addExitMenuItem("Encerrar")
                .show()
                .build();

        // Modifica o comportamento de minimização
        stage.setOnHiding(event -> {
            if (stage.isIconified()) {
                stage.hide();
                trayIcon.show();
            }
        });

        stage.setOnCloseRequest(event -> {
            event.consume();
            stage.hide();
            trayIcon.show();
        });


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/br/com/supergas/binaserver/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Monitor de Chamadas");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/phone-volume-solid.png")));
        stage.setIconified(true);
        stage.show();


        // Inicia o servidor embutido
        Controller controller = fxmlLoader.getController();
        controller.setMainApp(this);
        server = new Server(8000);
        server.startServer();


    }

    public void startServer() {
        server.startServer();
    }

    public void stopServer() {
        server.stopServer();
    }

    public void restartServer() {
        server.restartServer();
    }

    public boolean isServerRunning() {
        return server.isRunning();
    }
}

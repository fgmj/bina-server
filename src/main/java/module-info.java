module br.com.bomgas.monitor {
    requires javafx.fxml;
    requires nanohttpd;
    requires com.dustinredmond.fxtrayicon;
    requires javafx.controls; // Adiciona nanohttpd ao módulo
    requires javafx.swing;
    requires jmdns;


    opens br.com.bomgas.monitor to javafx.fxml;
    exports br.com.bomgas.monitor;
}

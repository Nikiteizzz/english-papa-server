package Main;

import ServerThread.ServerThread;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;

public class Main extends Application implements Initializable {
    @FXML
    public TextArea logsTextArea;
    Socket socket;
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage stage) throws Exception {
        configureStage(stage);
        Parent root = FXMLLoader.load(getClass().getResource("ViewDescription.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void configureStage(Stage stage) {
        stage.resizableProperty().set(false);
        stage.setTitle("Server");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            ServerThread workThread;
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                logsTextArea.appendText("Сервер успешно запущен!\n");
                Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/english-papa-db", "root", "14122313");
                logsTextArea.appendText("Сервер успешно подключился к базе данных!\n");
                while(true) {
                    socket = serverSocket.accept();
                    logsTextArea.appendText("Пользователь подключился!\n");
                    workThread = new ServerThread(socket, logsTextArea, dbConnection);
                    workThread.start();
                }
            } catch (Exception e) {
                logsTextArea.appendText("Возникла ошибка!\n");
            }
        }).start();
    }
}

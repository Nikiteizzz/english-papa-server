package ServerThread;

import DataManagers.InviteCodeManager;
import DataManagers.LessonsManager;
import DataManagers.StudentsManager;
import DataManagers.UsersManager;
import Models.InviteCode;
import Models.Lesson;
import Models.Student;
import Models.User;
import javafx.scene.control.TextArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServerThread extends Thread {
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    Socket userSocket;
    TextArea logsTextArea;
    Connection connection;
    User user;
    UsersManager usersManager;
    LessonsManager lessonsManager;
    InviteCodeManager inviteCodeManager;
    StudentsManager studentsManager;

    public ServerThread(Socket userSocket, TextArea logsTextArea, Connection connection) throws IOException {
        this.logsTextArea = logsTextArea;
        this.userSocket = userSocket;
        this.connection = connection;
    }

    @Override
    public void run() {
        boolean flag = true;
        try {
            inputStream = new ObjectInputStream(userSocket.getInputStream());
            outputStream = new ObjectOutputStream(userSocket.getOutputStream());
            this.usersManager = new UsersManager(logsTextArea, connection, outputStream);
            this.inviteCodeManager = new InviteCodeManager(logsTextArea, connection, outputStream);
            this.lessonsManager = new LessonsManager(logsTextArea, connection, outputStream);
            this.studentsManager = new StudentsManager(logsTextArea, connection, outputStream);
            String inputString;
            while (flag) {
                inputString = (String) inputStream.readObject();
                logsTextArea.appendText("С пользователя пришло: " + inputString + "\n");
                treatRequest(inputString);
            }
        } catch (Exception e) {
            if (e.getLocalizedMessage() == null) {
                logsTextArea.appendText("Пользователь отключился!\n");
            } else {
                logsTextArea.appendText("Ошибка: " + e.getLocalizedMessage() + "\n");
            }
        }
    }

    private void treatRequest(String request) throws IOException, SQLException {
        String[] keywords = request.split("@");
        switch (keywords[0]) {
            case "login":
                usersManager.tryLogin(keywords[1], keywords[2]);
                break;
            case "get":
                switch (keywords[1]) {
                    case "lessons": lessonsManager.tryGetLessons(keywords[2], keywords[3]); break;
                    case "students": studentsManager.getStudents(); break;
                    case "invite_codes": inviteCodeManager.getInviteCodes(); break;
                    case "users": usersManager.requestUsers(); break;
                }
                break;
            case "update": {
                User user = new User();
                user.fromJsonString(keywords[1]);
                usersManager.tryUpdate(user);
            } break;
            case "registration": {
                User user = new User();
                user.fromJsonString(keywords[1]);
                tryToRegister(user, keywords[2]);
            } break;
            case "add":
                switch (keywords[1]) {
                    case "invite_code": {
                        inviteCodeManager.addNewInviteCode(keywords[2], keywords[3]);
                    } break;
                    case "lesson": {
                        lessonsManager.addLesson(keywords[2]);
                    }
                }break;
            case "delete":
                switch (keywords[1]) {
                    case "invite_code": {
                        inviteCodeManager.deleteInviteCodeFromDB(keywords[2]);
                    }break;
                    case "lesson": {
                        lessonsManager.deleteLessonFromDB(keywords[2]);
                    }
                }break;
            default:
                logsTextArea.appendText("Ошибка запроса!\n");
                outputStream.writeObject("Неизвестный запрос");
        }
    }

    public void tryToRegister(User user, String inviteCode) throws SQLException, IOException {
        if (usersManager.isLoginUnique(user)) {
            InviteCode serverInviteCode = inviteCodeManager.getInviteCode(inviteCode);
            if (serverInviteCode == null) {
                outputStream.writeObject("Код не существует!");
            } else {
                usersManager.addNewUser(user, serverInviteCode.getAdmin());
                inviteCodeManager.deleteInviteCodeFromDB(serverInviteCode.getInviteCode());

            }
        } else {
            outputStream.writeObject("Логин не уникален!");
        }
    }

}

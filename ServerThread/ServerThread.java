package ServerThread;

import Models.Lesson;
import Models.User;
import javafx.scene.control.TextArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServerThread extends Thread {
    DataInputStream inputStream;
    DataOutputStream outputStream;
    Socket userSocket;
    TextArea logsTextArea;
    Connection connection;
    User user;

    public ServerThread(Socket userSocket, TextArea logsTextArea, Connection connection) {
        this.logsTextArea = logsTextArea;
        this.userSocket = userSocket;
        this.connection = connection;
    }

    @Override
    public void run() {
        boolean flag = true;
        try {
            inputStream = new DataInputStream(userSocket.getInputStream());
            outputStream = new DataOutputStream(userSocket.getOutputStream());
            String inputString;
            while (flag) {
                inputString = inputStream.readUTF();
                logsTextArea.appendText("С пользователя пришло: " + inputString + "\n");
                treatRequest(inputString);
            }
        } catch (Exception e) {
            logsTextArea.appendText("Ошибка: " + e.getLocalizedMessage() + "\n");
        }
    }

    private void treatRequest(String request) throws IOException, SQLException {
        String[] keywords = request.split("@");
        switch (keywords[0]) {
            case "login":
                tryLogin(keywords[1], keywords[2]);
                break;
            case "get":
                tryGetLessons(keywords[2], keywords[3]);
                break;
            case "update": {
                User user = new User();
                user.fromJsonString(keywords[1]);
                tryUpdate(user);
            }
            break;
            default:
                logsTextArea.appendText("Ошибка запроса!\n");
        }
    }

    private String getCodedPassword(String password) {
        return String.valueOf(password.hashCode());
    }

    private void tryLogin(String login, String password) throws IOException {
        JSONObject sendJSONObject = new JSONObject();
        ArrayList<User> dbData = new ArrayList<>(0);
        ResultSet resultSet;
        boolean isResultEmpty = true;
        String request = "Select * from users Where login = '" + login + "' and password = '" + getCodedPassword(password) + "'";
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(request);
            User user = new User();
            while (resultSet.next()) {
                isResultEmpty = false;
                user.setLogin(resultSet.getString(3));
                user.setName(resultSet.getString(2));
                user.setSurname(resultSet.getString(6));
                user.setId(resultSet.getLong(1));
                user.setPassword(resultSet.getString(4));
                user.setRole(resultSet.getString(7));
                sendJSONObject = user.toJsonObject();
                outputStream.writeUTF(sendJSONObject.toJSONString());
            }
            if (isResultEmpty) {
                outputStream.writeUTF("");
            }
        } catch (Exception e) {
            logsTextArea.appendText("Ошибка запроса!\nОписание: " + e.getLocalizedMessage() + "\n");
            outputStream.writeUTF("");
        }
    }

    private void tryGetLessons(String id, String date) throws IOException {
        String request = "Select * from lessons where teacherId = " + id + " and day_of_week = '" + date + "'";
        JSONObject sendJSONObject = new JSONObject();
        ArrayList<Lesson> dbData = new ArrayList<>(0);
        ResultSet resultSet;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(request);
            Lesson lesson;
            while (resultSet.next()) {
                lesson = new Lesson();
                lesson.setDayOfWeek(resultSet.getString(2));
                lesson.setNumber(resultSet.getLong(4));
                lesson.setCabinet(resultSet.getLong(7));
                lesson.setLessonName(resultSet.getString(3));
                lesson.setTeacherId(resultSet.getLong(5));
                lesson.setGroup(resultSet.getString(6));
                dbData.add(lesson);
            }
            JSONArray resultsArray = new JSONArray();
            for (Lesson element : dbData) {
                resultsArray.add(element.toJsonObject());
            }
            sendJSONObject.put("lessons", resultsArray);
            System.out.println(sendJSONObject.toJSONString());
            outputStream.writeUTF(sendJSONObject.toJSONString());
        } catch (Exception e) {
            logsTextArea.appendText("Ошибка запроса!\nОписание: " + e.getLocalizedMessage() + "\n");
            outputStream.writeUTF("");
        }
    }

    public void tryUpdate(User user) throws SQLException, IOException {
        String requestString;
        if (user.getAdmin()) {
            requestString = "UPDATE users "
                    + "SET name='" + user.getName() + "', login='" + user.getLogin() + "', password = '" + getCodedPassword(user.getPassword()) +
                    "', isAdmin = '1', surname = '" + user.getSurname() + "', role = '" + user.getRole() + "' " +
                    "where id = " + user.getId();
        } else {
            requestString = "UPDATE users "
                    + "SET name='" + user.getName() + "', login='" + user.getLogin() + "', password = '" + getCodedPassword(user.getPassword()) +
                    "', isAdmin = '0', surname = '" + user.getSurname() + "', role = '" + user.getRole() + "' " +
                    "where id = " + user.getId();
        }
        System.out.println(requestString);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(requestString);
            outputStream.writeUTF("Success");
        } catch (Exception e) {
            logsTextArea.appendText(e.getLocalizedMessage() + "\n");
            outputStream.writeUTF("Ошибка при обновлении!");
        }
    }

    public boolean isLoginUnique(User user) throws SQLException {
        String checkLoginRequest = "Select * from users where login = '" + user.getLogin() + "'";
        ResultSet resultSet;
        Statement statement = connection.createStatement();
        resultSet = statement.executeQuery(checkLoginRequest);
        boolean isLoginUnique = true;
        while(resultSet.next()) {
            isLoginUnique = false;
        }
        return isLoginUnique;
    }

    private String createDBRequest(String[] keywords) {
        return switch (keywords[0]) {
            case "get" -> "SELECT * FROM Lab5.Shops";
            case "add" -> "INSERT INTO Lab5.Shops (name, type, count_of_shops, number_of_employee)\n"
                    + "VALUES ('" + keywords[1] + "', '" + keywords[2] + "', " + keywords[3] + ", " + keywords[4] + ")";
            case "update" -> "UPDATE Lab5.Shops\n"
                    + "SET name='" + keywords[2] + "', type='" + keywords[3] + "', count_of_shops=" + keywords[4] +
                    ", number_of_employee = " + keywords[5] + "\n"
                    + "WHERE id=" + keywords[1] + "";
            case "delete" -> "DELETE FROM Lab5.Shops\n WHERE id=" + keywords[1] + "";
            default -> null;
        };
    }
}

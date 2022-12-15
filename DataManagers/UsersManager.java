package DataManagers;

import Models.InviteCode;
import Models.User;
import javafx.scene.control.TextArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UsersManager {
    ObjectOutputStream outputStream;
    TextArea logsTextArea;
    Connection connection;
    public UsersManager(TextArea logsTextArea, Connection connection, ObjectOutputStream objectOutputStream) throws IOException {
        this.logsTextArea = logsTextArea;
        this.connection = connection;
        this.outputStream = objectOutputStream;
    }

    public void tryLogin(String login, String password) throws IOException {
        JSONObject sendJSONObject = new JSONObject();
        ResultSet resultSet;
        boolean isResultEmpty = true;
        String request = "Select * from users Where login = '" + login + "' and password = '" + password + "'";
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
                user.setAdmin(resultSet.getBoolean(5));
                sendJSONObject = user.toJsonObject();
                outputStream.writeObject(sendJSONObject.toJSONString());
            }
            if (isResultEmpty) {
                outputStream.writeObject("Error");
            }
        } catch (Exception e) {
            logsTextArea.appendText("Ошибка запроса!\nОписание: " + e.getLocalizedMessage() + "\n");
            outputStream.writeObject("Error");
        }
    }

    public void tryUpdate(User user) throws SQLException, IOException {
        String requestString;
        if (user.getAdmin()) {
            requestString = "UPDATE users "
                    + "SET name='" + user.getName() + "', login='" + user.getLogin() + "', password = '" + user.getPassword() +
                    "', isAdmin = '1', surname = '" + user.getSurname() + "', role = '" + user.getRole() + "' " +
                    "where id = " + user.getId();
        } else {
            requestString = "UPDATE users "
                    + "SET name='" + user.getName() + "', login='" + user.getLogin() + "', password = '" + user.getPassword() +
                    "', isAdmin = '0', surname = '" + user.getSurname() + "', role = '" + user.getRole() + "' " +
                    "where id = " + user.getId();
        }
        System.out.println(requestString);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(requestString);
            outputStream.writeObject("Success");
        } catch (Exception e) {
            logsTextArea.appendText(e.getLocalizedMessage() + "\n");
            outputStream.writeObject("Ошибка при обновлении!");
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

    public void addNewUser(User user, boolean isAdmin) throws SQLException {
        String requestString = "Insert into users (name, login, password, isAdmin, surname, role)" +
                " values ('" + user.getName() + "', '" + user.getLogin() + "', '" + user.getPassword() + "', '" +
                (user.getAdmin() ? "1" : "0") + "', '" + user.getSurname() + "', '" + user.getRole() + "')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(requestString);
    }

    private String getCodedPassword(String password) {
        return String.valueOf(password.hashCode());
    }

    public void requestUsers() throws SQLException, IOException {
        String request = "Select * from users";
        ResultSet resultSet;
        JSONObject sendJSONObject = new JSONObject();
        ArrayList<User> dbData = new ArrayList<>(0);
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(request);
            User user;
            while (resultSet.next()) {
                user = new User();
                user.setLogin(resultSet.getString(3));
                user.setName(resultSet.getString(2));
                user.setSurname(resultSet.getString(6));
                user.setId(resultSet.getLong(1));
                user.setPassword(resultSet.getString(4));
                user.setRole(resultSet.getString(7));
                user.setAdmin(resultSet.getBoolean(5));
                dbData.add(user);
            }
            JSONArray resultsArray = new JSONArray();
            for (User element : dbData) {
                resultsArray.add(element.toJsonObject());
            }
            sendJSONObject.put("users", resultsArray);
            System.out.println(sendJSONObject.toJSONString());
            outputStream.writeObject(sendJSONObject.toJSONString());
        } catch (Exception e) {
            logsTextArea.appendText("Ошибка запроса!\nОписание: " + e.getLocalizedMessage() + "\n");
            outputStream.writeObject("Ошибка запроса!");
        }
    }

}

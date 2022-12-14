package DataManagers;

import Models.InviteCode;
import javafx.scene.control.TextArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class InviteCodeManager {
    ObjectOutputStream outputStream;
    TextArea logsTextArea;
    Connection connection;

    public InviteCodeManager(TextArea logsTextArea, Connection connection, ObjectOutputStream objectOutputStream) throws IOException {
        this.logsTextArea = logsTextArea;
        this.connection = connection;
        this.outputStream = objectOutputStream;
    }

    public InviteCode getInviteCode(String code) throws SQLException {
        String requestString = "Select * from invite_codes where invite_code = '" + code + "'";
        ResultSet resultSet;
        Statement statement = connection.createStatement();
        resultSet = statement.executeQuery(requestString);
        while (resultSet.next()) {
            InviteCode inviteCode = new InviteCode();
            inviteCode.setInviteCode(resultSet.getString("invite_code"));
            inviteCode.setAdmin(resultSet.getBoolean("isAdmin"));
            return inviteCode;
        }
        return null;
    }

    public void getInviteCodes() throws IOException {
        String request = "Select * from invite_codes";
        JSONObject sendJSONObject = new JSONObject();
        ArrayList<InviteCode> dbData = new ArrayList<>(0);
        ResultSet resultSet;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(request);
            InviteCode inviteCode;
            while (resultSet.next()) {
                inviteCode = new InviteCode();
                inviteCode.setInviteCode(resultSet.getString("invite_code"));
                inviteCode.setId(resultSet.getLong("id"));
                inviteCode.setAdmin(resultSet.getBoolean("isAdmin"));
                dbData.add(inviteCode);
            }
            JSONArray resultsArray = new JSONArray();
            for (InviteCode element : dbData) {
                resultsArray.add(element.toJsonObject());
            }
            sendJSONObject.put("invite_codes", resultsArray);
            System.out.println(sendJSONObject.toJSONString());
            outputStream.writeObject(sendJSONObject.toJSONString());
        } catch (Exception e) {
            logsTextArea.appendText("Ошибка запроса!\nОписание: " + e.getLocalizedMessage() + "\n");
            outputStream.writeObject("Ошибка запроса!");
        }
    }

    public void deleteInviteCodeFromDB(InviteCode inviteCode) throws SQLException {
        String requestString = "Delete from invite_codes where invite_code  = '" + inviteCode.getInviteCode() + "'";
        Statement statement = connection.createStatement();
        statement.executeUpdate(requestString);
    }
}

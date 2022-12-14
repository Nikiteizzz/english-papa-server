package DataManagers;

import Models.Student;
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

public class StudentsManager {
    ObjectOutputStream outputStream;
    TextArea logsTextArea;
    Connection connection;

    public StudentsManager(TextArea logsTextArea, Connection connection, ObjectOutputStream objectOutputStream) throws IOException {
        this.logsTextArea = logsTextArea;
        this.connection = connection;
        this.outputStream = objectOutputStream;
    }

    public void getStudents() throws SQLException, IOException {
        String request = "Select * from students";
        JSONObject sendJSONObject = new JSONObject();
        ArrayList<Student> dbData = new ArrayList<>(0);
        ResultSet resultSet;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(request);
            Student student;
            while (resultSet.next()) {
                student = new Student();
                student.setName(resultSet.getString("name"));
                student.setSurname(resultSet.getString("surname"));
                student.setGroup(resultSet.getString("group"));
                dbData.add(student);
            }
            JSONArray resultsArray = new JSONArray();
            for (Student element : dbData) {
                resultsArray.add(element.toJsonObject());
            }
            sendJSONObject.put("students", resultsArray);
            outputStream.writeObject(sendJSONObject.toJSONString());
        } catch (Exception e) {
            logsTextArea.appendText("Ошибка запроса!\nОписание: " + e.getLocalizedMessage() + "\n");
            outputStream.writeObject("Ошибка запроса!");
        }
    }
}

package DataManagers;

import Models.Lesson;
import Models.User;
import javafx.scene.control.TextArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class LessonsManager {
    ObjectOutputStream outputStream;
    TextArea logsTextArea;
    Connection connection;

    public LessonsManager(TextArea logsTextArea, Connection connection, ObjectOutputStream objectOutputStream) throws IOException {
        this.logsTextArea = logsTextArea;
        this.connection = connection;
        this.outputStream = objectOutputStream;
    }

    public void tryGetLessons(String id, String date) throws IOException {
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
            outputStream.writeObject(sendJSONObject.toJSONString());
        } catch (Exception e) {
            logsTextArea.appendText("Ошибка запроса!\nОписание: " + e.getLocalizedMessage() + "\n");
            outputStream.writeObject("Ошибка запроса!");
        }
    }
}

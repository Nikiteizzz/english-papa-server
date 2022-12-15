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
import java.sql.SQLException;
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
                lesson.setId(resultSet.getLong(1));
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

    public void addLesson(String lesson) throws SQLException, IOException {
        Lesson lesson1 = new Lesson();
        lesson1.fromJsonString(lesson);
        String dayOfWeek = switch (lesson1.getDayOfWeek()) {
            case "понедельник" -> "MONDAY";
            case "вторник" -> "TUESDAY";
            case "среда" -> "WEDNESDAY";
            case "четверг" -> "THURSDAY";
            case "пятница" -> "FRIDAY";
            case "суббота" -> "SATURDAY";
            default -> "ERROR";
        };
        String request = "insert into lessons (day_of_week, lesson, number, teacherId, groupp, cabinet) values " +
                "('" + dayOfWeek + "', '" + lesson1.getLessonName() + "', '" + lesson1.getNumber() + "', '" +
                lesson1.getTeacherId() + "', '" + lesson1.getGroup() + "', '" + lesson1.getCabinet() + "')";
        System.out.println(request);
        Statement statement = connection.createStatement();
        statement.executeUpdate(request);
        outputStream.writeObject("Success");
    }

    public void deleteLessonFromDB(String id) throws SQLException, IOException {
        String requestString = "Delete from lessons where id  = '" + id + "'";
        Statement statement = connection.createStatement();
        statement.executeUpdate(requestString);
        outputStream.writeObject("Success");
    }
}

package Models;

import Interfaces.Model;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Date;

public class Lesson implements Model {
    String dayOfWeek;
    String lessonName;
    long number;
    long teacherId;
    long cabinet;
    String group;
    long id;
    public Lesson() {}
    public Lesson(String dayOfWeek, String lessonName, long number, long teacherId, long cabinet, String group) {
        this.dayOfWeek = dayOfWeek;
        this.lessonName = lessonName;
        this.number = number;
        this.teacherId = teacherId;
        this.cabinet = cabinet;
        this.group = group;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public long getNumber() {
        return number;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setCabinet(long cabinet) {
        this.cabinet = cabinet;
    }

    public long getCabinet() {
        return cabinet;
    }

    public String getGroup() {
        return group;
    }

    public long getId() {
        return id;
    }

    @Override
    public void fromJsonString(String jsonString) {
        JSONParser parser = new JSONParser();
        //    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            JSONObject object = (JSONObject) parser.parse(jsonString);
            //        String dataToDecode = (String) object.get("date");
            this.dayOfWeek =  (String) object.get("day_of_week");
            this.number = (long) object.get("number");
            this.teacherId= (long) object.get("teacher_id");
            this.lessonName = (String) object.get("lesson_name");
            this.cabinet = (long) object.get("cabinet");
            this.group = (String) object.get("group");
            this.id = (long) object.get("id");
        } catch (Exception e) {
            return;
        }
    }
    @Override
    public JSONObject toJsonObject() {
        JSONObject object = new JSONObject();
        object.put("day_of_week", this.dayOfWeek);
        object.put("number", this.number);
        object.put("teacher_id", this.teacherId);
        object.put("lesson_name", this.lessonName);
        object.put("cabinet", this.cabinet);
        object.put("group", this.group);
        object.put("id", this.id);
        return object;
    }
}

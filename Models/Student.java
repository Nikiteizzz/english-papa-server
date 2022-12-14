package Models;

import Interfaces.Model;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Student implements Model {
    String name;
    String surname;
    String group;
    public Student() {}
    public Student (String name, String surname, String group) {
        this.name = name;
        this.surname = surname;
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void fromJsonString(String jsonString) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject object = (JSONObject) parser.parse(jsonString);
            this.name =  (String) object.get("name");
            this.surname = (String) object.get("surname");
            this.group = (String) object.get("group");
        } catch (Exception e) {
            return;
        }
    }
    @Override
    public JSONObject toJsonObject() {
        JSONObject object = new JSONObject();
        object.put("name", this.name);
        object.put("surname", this.surname);
        object.put("group", this.group);
        return object;
    }
}
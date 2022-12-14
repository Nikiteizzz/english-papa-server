package Models;

import Interfaces.Model;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class InviteCode implements Model {
    String inviteCode;
    boolean isAdmin;
    long id;

    public String getInviteCode() {
        return inviteCode;
    }
    public boolean getAdmin() {
        return isAdmin;
    }

    public long getId() {
        return id;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public void setId(long id) {
        this.id = id;
    }

    public InviteCode() {}
    public InviteCode(String inviteCode, boolean isAdmin, long id) {
        this.inviteCode = inviteCode;
        this.isAdmin = isAdmin;
        this.id = id;
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject object = new JSONObject();
        object.put("id", this.id);
        object.put("isAdmin", this.isAdmin);
        object.put("invite_code", this.inviteCode);
        return object;
    }

    @Override
    public void fromJsonString(String jsonString) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject object = (JSONObject) parser.parse(jsonString);
            this.id = (long) object.get("id");
            this.inviteCode = (String) object.get("invite_code");
            this.isAdmin = (boolean) object.get("isAdmin");
        } catch (Exception e) {
            return;
        }
    }
}

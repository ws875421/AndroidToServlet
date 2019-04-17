package idv.david.jsontransferandroid;


import org.json.JSONException;
import org.json.JSONObject;

public class Team {
    private int id;
    private String name;


    public Team() {
        super();
    }

    public Team(int id, String name) {
        super();
        this.id = id;
        this.name = name;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public JSONObject toJSON() throws JSONException {

        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("name", name);

        return jo;
    }
}

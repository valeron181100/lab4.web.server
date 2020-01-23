package main.java.database.model;

import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class ResultPoint {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "X_COORD")
    private double x;

    @Column(name = "Y_COORD")
    private double y;

    @Column(name = "R_COORD")
    private double r;

    @Column(name = "IS_ENTERED")
    private boolean isEntered;

    public ResultPoint(){
        id = UUID.randomUUID().toString();
    }


    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public boolean isEntered() {
        return isEntered;
    }

    public void setEntered(boolean entered) {
        isEntered = entered;
    }

    public JSONObject getJSON(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", getX());
        jsonObject.put("y", getY());
        jsonObject.put("r", getR());
        jsonObject.put("entered", isEntered());
        return jsonObject;
    }

}

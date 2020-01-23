package main.java.database.model;

import main.java.utilities.StringHasher;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class UserModel implements Serializable {

    public static final String SERVER_SIDE_PASSWORD_PART = "wGvw$R";

    @Id
    @Column(name = "USER_ID")
    private String userid;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String criptPassword;

    @Transient
    private String password;

    public UserModel() {
        userid = UUID.randomUUID().toString();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCriptPassword() {
        return criptPassword;
    }

    public void setCriptPassword(String criptPassword) {
        StringHasher hasher = new StringHasher("MD5");
        this.criptPassword = hasher.getHash(criptPassword + SERVER_SIDE_PASSWORD_PART);
    }
}

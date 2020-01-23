package main.java.services;

import main.java.database.dao.UserModelDAO;
import main.java.database.model.UserModel;
import main.java.utilities.IOUtils;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.persistence.Column;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.registry.infomodel.User;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Stateless
@Path("/auth")
public class AuthService {
    @POST
    @Path("/c")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String createUser(InputStream jsonStream) throws IOException {
        JSONObject jsonObject = new JSONObject(IOUtils.convert(jsonStream, StandardCharsets.UTF_8));
        UserModel userModel = new UserModel();
        userModel.setUserid(UUID.randomUUID().toString());
        userModel.setUsername(jsonObject.get("username").toString());
        userModel.setCriptPassword(jsonObject.get("password").toString());
        UserModelDAO userModelDAO = new UserModelDAO();
        JSONObject response = new JSONObject();
        if(userModelDAO.findUserByUserName(userModel.getUsername()) == null){
            userModelDAO.insertUser(userModel);
            response.put("authStatus", "created");
            return response.toString();
        }
        response.put("authStatus", "exist");
        return response.toString();
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String login(InputStream jsonStream) throws IOException {
        JSONObject jsonObject = new JSONObject(IOUtils.convert(jsonStream, StandardCharsets.UTF_8));
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
        UserModel clientUserModel = new UserModel();
        clientUserModel.setUsername(username);
        clientUserModel.setCriptPassword(password);
        UserModelDAO userModelDAO = new UserModelDAO();
        JSONObject response = new JSONObject();
        UserModel dataUserModel = userModelDAO.findUserByUserName(username);
        if(dataUserModel == null){
            response.put("authStatus", "login-failed");
            response.put("message", "unknown username");
            return response.toString();
        }else{
            if(clientUserModel.getCriptPassword().equals(dataUserModel.getCriptPassword())){
                response.put("authStatus", "ok");
                response.put("message", "logged in");
                response.put("userId", dataUserModel.getUserid());
                return response.toString();
            }else{
                response.put("authStatus", "login-failed");
                response.put("message", "invalid password");
                return response.toString();
            }
        }
    }

    @GET
    @Path("/exists")
    @Produces(MediaType.APPLICATION_JSON)
    public String isUserExists(@QueryParam("username") String username){
        UserModelDAO userModelDAO = new UserModelDAO();
        JSONObject response = new JSONObject();
        if(userModelDAO.findUserByUserName(username) == null){
            response.put("response", false);
            return response.toString();
        }
        response.put("response", true);
        return response.toString();
    }
    @GET
    @Path("/nameof")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUsernameByUserId(@QueryParam("userId") String userId){
        UserModel userModel = new UserModelDAO().findUserByUserId(userId);
        if(userModel != null){
            JSONObject response = new JSONObject();
            response.put("response", "OK");
            response.put("username", userModel.getUsername());
            return response.toString();
        }
        else{
            JSONObject response = new JSONObject();
            response.put("response", "ERROR");
            return response.toString();
        }
    }

}
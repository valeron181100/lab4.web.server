package main.java.services;

import main.java.database.dao.ResultPointDAO;
import main.java.database.dao.UserModelDAO;
import main.java.database.model.ResultPoint;
import main.java.utilities.AreaChecker;
import main.java.utilities.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.json.JsonArray;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Path("/history")
public class HistoryService {

    @POST
    @Path("/c")
    @Produces(MediaType.APPLICATION_JSON)
    public String addPoint(InputStream jsonStream) throws IOException {
        JSONObject jsonObject = new JSONObject(IOUtils.convert(jsonStream, StandardCharsets.UTF_8));
        UserModelDAO userModelDAO = new UserModelDAO();
        if(userModelDAO.findUserByUserId(jsonObject.getString("userId")) != null) {
            ResultPoint resultPoint = new ResultPoint();
            resultPoint.setUserId(jsonObject.getString("userId"));
            resultPoint.setR(jsonObject.getDouble("r"));
            resultPoint.setX(jsonObject.getDouble("x"));
            resultPoint.setY(jsonObject.getDouble("y"));
            resultPoint.setEntered(AreaChecker.isHit(resultPoint.getX(), resultPoint.getY(), resultPoint.getR()));

            ResultPointDAO pointDAO = new ResultPointDAO();

            pointDAO.insertPoint(resultPoint);
            JSONObject response = new JSONObject();
            response.put("response", "OK");
            return response.toString();
        }else{
            JSONObject response = new JSONObject();
            response.put("response", "unknown userId");
            return response.toString();
        }
    }

    @GET
    @Path("{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getHistory(@PathParam("userId") String userId){
        ResultPointDAO pointDAO = new ResultPointDAO();
        List<ResultPoint> allPoints = pointDAO.findAllPoints();
        JSONArray points = new JSONArray();
        for(ResultPoint point : allPoints){
            if(point.getUserId().equals(userId))
                points.put(point.getJSON());
        }
        return points.toString();
    }

    @POST
    @Path("clear/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String clearHistory(@PathParam("userId") String userId){
        ResultPointDAO pointDAO = new ResultPointDAO();
        pointDAO.clearUserHistory(userId);
        JSONObject response = new JSONObject();
        response.put("response", "OK");
        return response.toString();
    }
}

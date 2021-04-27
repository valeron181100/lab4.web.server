package main.java.services;

import main.java.database.DBController;
import main.java.database.JDBCConnector;
import main.java.utilities.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Stateless
@Path("/trading")
public class TradingService {

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String login(InputStream jsonStream) throws IOException {
        JSONObject jsonObject = new JSONObject(IOUtils.convert(jsonStream, StandardCharsets.UTF_8));
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
        JSONObject response = new JSONObject();
        response.put("Hello", "World");
        return response.toString();
    }

    @GET
    @Path("/selling")
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllSellingThings() throws SQLException {
        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        return controller.getSellingThings().toString();
    }

    @GET
    @Path("/is_user_exists")
    @Produces(MediaType.APPLICATION_JSON)
    public String isUserExists(@QueryParam("nickname") String nickname) throws SQLException {
        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        return controller.isUserExists(nickname).toString();
    }

    @GET
    @Path("/inventory")
    @Produces(MediaType.APPLICATION_JSON)
    public String listCustomerInventory(@QueryParam("nickname") String nickname) throws SQLException {
        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        return controller.getCustomerInventoryThings(nickname).toString();
    }

    @GET
    @Path("/contacts")
    @Produces(MediaType.APPLICATION_JSON)
    public String listUserContacts(@QueryParam("nickname") String nickname) throws SQLException {
        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        return controller.getUserContacts(nickname).toString();
    }

    @GET
    @Path("/messages")
    @Produces(MediaType.APPLICATION_JSON)
    public String listUserMessages(@QueryParam("nickname") String nickname, @QueryParam("contact_nickname") String contactNickname) throws SQLException {
        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        return controller.getUserMessages(nickname, contactNickname).toString();
    }

    @POST
    @Path("/messages")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String sendMessage(InputStream jsonStream) throws IOException {
        JSONObject jsonObject = new JSONObject(IOUtils.convert(jsonStream, StandardCharsets.UTF_8));
        String senderNick = jsonObject.get("senderNick").toString();
        String recipientNick = jsonObject.get("recipientNick").toString();
        String content = jsonObject.get("content").toString();
        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        JSONObject response = new JSONObject();
        response.put("res", controller.addMessage(senderNick, recipientNick, content));
        return response.toString();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void register(InputStream jsonStream) throws IOException {
        JSONObject jsonObject = new JSONObject(IOUtils.convert(jsonStream, StandardCharsets.UTF_8));
        String avatar = jsonObject.get("avatar").toString();
        String customerName = jsonObject.get("customerName").toString();
        String customerLastName = jsonObject.get("customerLastName").toString();
        String customerNickname = jsonObject.get("customerNickname").toString();
        int age = jsonObject.getInt("age");
        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        controller.register(avatar, customerNickname, customerName, customerLastName, age);
    }

    @POST
    @Path("/buy")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void buyThing(InputStream jsonStream) throws IOException {
        JSONObject jsonObject = new JSONObject(IOUtils.convert(jsonStream, StandardCharsets.UTF_8));
        String buyer = jsonObject.get("buyer").toString();
        int thingId = jsonObject.getInt("thingId");
        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        controller.buyThing(buyer, thingId);
    }

    @POST
    @Path("/sell")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void sellThing(InputStream jsonStream) throws IOException {
        JSONObject jsonObject = new JSONObject(IOUtils.convert(jsonStream, StandardCharsets.UTF_8));
        String seller = jsonObject.get("seller").toString();
        int thingId = jsonObject.getInt("thingId");
        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        controller.sellThing(seller, thingId);
    }

    @GET
    @Path("/customer")
    @Produces(MediaType.APPLICATION_JSON)
    public String listUserMessages(@QueryParam("nickname") String nickname) throws SQLException {
        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        return controller.getUserInfo(nickname).toString();
    }

    @GET
    @Path("/customer_search")
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllCustomers(@QueryParam("q") String input) throws SQLException {
        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        List<Object> resultList = controller.getAllUsers().toList().stream().filter(element -> {
            System.out.println(element);
            String name = ((HashMap<String, String>)element).get("customerName");
            String lastName = ((HashMap<String, String>)element).get("customerLastName");
            String nickName = ((HashMap<String, String>)element).get("customerNickName");
            System.out.println("\n---------------\n" + name);
            Pattern pattern = Pattern.compile(input.toLowerCase());
            int ctr = 0;
            Matcher matcher = pattern.matcher(name.toLowerCase());
            while (matcher.find()) {
                ctr++;
            }
            Matcher matcher1 = pattern.matcher(lastName.toLowerCase());
            while (matcher1.find()) {
                ctr++;
            }
            Matcher matcher2 = pattern.matcher(nickName.toLowerCase());
            while (matcher2.find()) {
                ctr++;
            }

            return ctr != 0;
        }).collect(Collectors.toList());
        return new JSONArray(resultList).toString();
    }
}
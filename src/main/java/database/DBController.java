package main.java.database;

import main.java.utilities.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class DBController {

    private JDBCConnector connector;

    public DBController(JDBCConnector connector){
        this.connector = connector;
    }

    public JSONArray getSellingThings() throws SQLException {
        Pair<PreparedStatement, ResultSet> pair = connector.execSQLQuery("SELECT * FROM things_view;");
        ResultSet set = pair.getValue();
        JSONArray resultObj = new JSONArray();
        while(set.next()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("thingId", set.getInt("thing_id"));
            jsonObject.put("thingName", set.getString("thing_name"));
            jsonObject.put("rarity", set.getString("rarity"));
            jsonObject.put("characterName", set.getString("character_name"));
            jsonObject.put("price", set.getString("price"));
            resultObj.put(jsonObject);
        }
        pair.getKey().close();
        return resultObj;
    }

    public JSONArray getCustomerInventoryThings(String nickname) throws SQLException {
        Pair<PreparedStatement, ResultSet> pair = connector.execSQLQuery("select * from customer_things('" + nickname + "');");
        ResultSet set = pair.getValue();
        JSONArray resultObj = new JSONArray();
        while(set.next()){
            JSONObject jsonObject = new JSONObject();
            String customerName = set.getString("customer_nick_name");
            jsonObject.put("thingId", set.getInt("thing_id"));
            jsonObject.put("thingName", set.getString("think_name"));
            jsonObject.put("rarity", set.getString("rarity"));
            jsonObject.put("customerName", customerName == null ? "": customerName);
            jsonObject.put("characterName", set.getString("character_name"));
            jsonObject.put("price", set.getString("price"));
            jsonObject.put("isSelling", set.getString("is_selling"));
            resultObj.put(jsonObject);
        }
        pair.getKey().close();
        return resultObj;
    }

    public JSONArray getAllUsers() throws SQLException {
        Pair<PreparedStatement, ResultSet> pair = connector.execSQLQuery("SELECT * FROM customer;");
        ResultSet set = pair.getValue();
        JSONArray resultObj = new JSONArray();
        while(set.next()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("customerLastName", set.getString("customer_last_name"));
            jsonObject.put("customerNickName", set.getString("customer_nick_name"));
            jsonObject.put("age", set.getInt("age"));
            jsonObject.put("customerName", set.getString("customer_name"));
            resultObj.put(jsonObject);
        }
        pair.getKey().close();
        return resultObj;
    }

    public JSONObject getUserInfo(String nickname) throws SQLException {
        Pair<PreparedStatement, ResultSet> pair = connector.execSQLQuery("select * from customer_information('" + nickname + "');");
        ResultSet set = pair.getValue();
        JSONObject jsonObject = new JSONObject();
        if(set.next()){
            jsonObject.put("customerLastName", set.getString("customer_last_name"));
            jsonObject.put("customerNickName", set.getString("customer_nick_name"));
            jsonObject.put("age", set.getInt("age"));
            jsonObject.put("customerName", set.getString("customer_name"));
            jsonObject.put("customerPhoto", set.getString("link_photo"));
            jsonObject.put("rating", set.getString("rating_num"));
            jsonObject.put("balance", set.getString("customer_balance"));
        }
        pair.getKey().close();
        return jsonObject;
    }

    public JSONArray getUserContacts(String userId) throws SQLException {
        Pair<PreparedStatement, ResultSet> pair = connector.execSQLQuery("select * from customer_contacts('" + userId + "');");
        ResultSet set = pair.getValue();
        JSONArray resultObj = new JSONArray();
        while(set.next()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("customerNickName", set.getString("people_nick"));
            resultObj.put(jsonObject);
        }
        pair.getKey().close();
        return resultObj;
    }

    public JSONArray getUserMessages(String userNick, String contactNick) throws SQLException {
        Pair<PreparedStatement, ResultSet> pair = connector
                .execSQLQuery("select * from message_info_sendler_recipient('" + userNick + "', '"+ contactNick +"');");
        ResultSet set = pair.getValue();
        JSONArray resultObj = new JSONArray();
        while(set.next()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("senderNick", set.getString("sender_nick"));
            jsonObject.put("recipientNick", set.getString("recipient_nick"));
            jsonObject.put("content", set.getString("content"));
            jsonObject.put("sendTime", set.getString("send_time"));

            resultObj.put(jsonObject);
        }
        pair.getKey().close();
        return resultObj;
    }

    public boolean addMessage(String senderNick, String recipientNick, String content) {
        String query = String.format("insert into message(sender_nick, recipient_nick, content) values('%s', '%s', '%s');",
                senderNick, recipientNick, content);
        return connector.execSQLUpdate(query);
    }

    public void buyThing(String buyer, int thingId) {
        connector.execSQLUpdate(String.format("select * from buy_thing('%s', %d)", buyer, thingId));
    }

    public void sellThing(String seller, int thingId) {
        connector.execSQLUpdate(String.format("select * from put_up_for_sale('%s', %d)", seller, thingId));
    }

    public JSONObject isUserExists(String nickname) throws SQLException {
        Pair<PreparedStatement, ResultSet> pair = connector.execSQLQuery(
                "select exists(select customer_nick_name from customer where customer_nick_name='" + nickname + "');"
        );
        ResultSet set = pair.getValue();
        JSONObject result = new JSONObject();
        if (set.next()) {
            result.put("exists", set.getBoolean("exists"));
        }else {
            result.put("exists", false);
        }

        return result;
    }

    public void register(String photoLink, String nickName, String name, String lastName, int age) {
        String query = String.format("select * from register('%s','%s','%s','%s',%d)",
                photoLink, name, lastName, nickName, age);
        System.out.println("--------------------------------");
        System.out.println(query);
        System.out.println("--------------------------------");
        connector.execSQLUpdate(query);
    }

    /*public void addCostumeToDB(Costume costume){
        costume.getInsertSQLQueries().forEach(query ->
                connector.execSQLUpdate(query.replace("DEFAULT", String.valueOf(costume.hashCode()))));
    }

    public ArrayList<Pair<Costume,String>> getCostumesFromDB() throws SQLException {
        Pair<PreparedStatement, ResultSet> pair = connector.execSQLQuery("SELECT * FROM costumes;");
        ResultSet set = pair.getValue();
        //Получили id-шники костюмов
        ArrayList<Pair<Integer,String>> costumeKeys = new ArrayList<>();
        while(set.next()){
            costumeKeys.add(new Pair<>(set.getInt("id"), set.getString(DBConst.COSTUMES_USER)));
        }
        pair.getKey().close();

        //Получили костюмы
        ArrayList<Pair<Costume,String>> costumes = new ArrayList<>();
        ArrayList<String> consts = new ArrayList<>(Arrays.asList(DBConst.TOPCLOTHES_TABLE, DBConst.DOWNCLOTHES_TABLE,
                DBConst.HATS_TABLE, DBConst.SHOES_TABLE, DBConst.UNDERWEAR_TABLE));
        for (Pair<Integer,String> mainPair : costumeKeys) {
            Costume costume = new Costume();
            int key = mainPair.getKey();
            String login = mainPair.getValue();
            for (String tableName : consts){
                Pair<PreparedStatement, ResultSet> tablePair = connector.execSQLQuery("SELECT * FROM " + tableName + " WHERE id=" + key +";");
                ResultSet tableSet = tablePair.getValue();
                while(tableSet.next()) {
                    int size = tableSet.getInt("size");
                    Color color = Color.valueOf(tableSet.getString("color"));
                    Material material = Material.valueOf(tableSet.getString("material"));
                    String name = tableSet.getString("name");
                    boolean isForMan = tableSet.getBoolean("is_for_man");
                    switch (tableName) {
                        case DBConst.TOPCLOTHES_TABLE:
                            TopClothes topClothes = new TopClothes(size, color, material, name, isForMan,
                                    tableSet.getInt("hand_sm_length"), tableSet.getBoolean("is_hood"),
                                    tableSet.getInt("growth_sm"));
                            costume.setTopClothes(topClothes);
                            break;
                        case DBConst.DOWNCLOTHES_TABLE:
                            DownClothes downClothes = new DownClothes(size, color, material, name, isForMan,
                                    tableSet.getInt("leg_length_sm"),
                                    tableSet.getInt("diametr_leg_sm"));
                            costume.setDownClothes(downClothes);
                            break;
                        case DBConst.HATS_TABLE:
                            Hat hat= new Hat(size, color, material, name, isForMan,
                                    tableSet.getInt("cylinder_height_sm"),
                                    tableSet.getInt("visor_length_sm"));
                            costume.setHat(hat);
                            break;
                        case DBConst.SHOES_TABLE:
                            Shoes shoes = new Shoes(size, color, material, name, isForMan,
                                    tableSet.getBoolean("is_shoelaces"),
                                    Material.valueOf(tableSet.getString("outsole_material")));
                            costume.setShoes(shoes);
                            break;
                        case DBConst.UNDERWEAR_TABLE:
                            Underwear underwear = new Underwear(size, color, material, name, isForMan,
                                    tableSet.getInt("sex_lvl"));
                            costume.setUnderwear(underwear);
                            break;
                    }
                }
            }
            costumes.add(new Pair<>(costume, login));
        }
        return costumes;
    }*/
}

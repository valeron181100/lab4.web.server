package main.java.services;

import org.json.JSONArray;
import org.json.JSONObject;

public class Test {
    public static void main(String[] args) {
        JSONArray array = new JSONArray();
        array.put(new JSONObject("{'hello': 'again'}"));
        array.put(new JSONObject("{'hello1': 'again1'}"));
        System.out.println((JSONObject)array.toList().get(0));
    }
}

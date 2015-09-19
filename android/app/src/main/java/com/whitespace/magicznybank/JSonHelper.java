package com.whitespace.magicznybank;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jakub on 20.09.2015.
 */
public class JSonHelper {
    public static User parseUserJSon(String json, String login, String pass) throws Exception {
        String id, name, surname, email, tariff;
        Double maxLoan, money, loan;

        try {
            JSONObject obj = new JSONObject(json);

            if(obj.has("error"))
                throw new Exception(obj.getString("error"));

            id = obj.getString("id");
            name = obj.getString("name");
            surname = obj.getString("last_name");
            email = obj.getString("email");
            maxLoan = obj.getDouble("max_loan");
            money = obj.getDouble("money");
            tariff = obj.getString("tariff");
            loan = obj.getDouble("loan");

        } catch (JSONException e) {
            Log.d("KROL", "Error: " + e.getMessage());
            throw new Exception("Server error - missing user data");
        }

        return new User(id, login, pass, name, surname, email, maxLoan, money, tariff, loan);
    }

    public static void addServicesForUser(User user, String servicesJSon, String buyableservicesJSon) throws Exception {
        try {
            JSONArray servicesArr = new JSONArray(servicesJSon);

            for(int i = 0; i < servicesArr.length(); i++) {
                try {
                    String id = servicesArr.getJSONObject(i).getString("id");
                    int tokens = servicesArr.getJSONObject(i).getInt("tokens");
                    double estimate = servicesArr.getJSONObject(i).getDouble("estimate");
                    String name = servicesArr.getJSONObject(i).getString("name");
                    String description = servicesArr.getJSONObject(i).getString("description");

                    Service service = new Service(id, tokens, estimate, name, description);
                    user.services.add(service);
                } catch (JSONException e) {
                    Log.d("KROL", "Error - problem with parsing service.\n" + e.getMessage());
                }
            }

            JSONArray buyableservicesArr = new JSONArray(buyableservicesJSon);

            for(int i = 0; i < buyableservicesArr.length(); i++) {
                try {
                    String id = buyableservicesArr.getJSONObject(i).getString("id");
                    int tokens = buyableservicesArr.getJSONObject(i).getInt("tokens");
                    double estimate = buyableservicesArr.getJSONObject(i).getDouble("estimate");
                    String name = buyableservicesArr.getJSONObject(i).getString("name");
                    String description = buyableservicesArr.getJSONObject(i).getString("description");

                    Service service = new Service(id, tokens, estimate, name, description);
                    user.buyableServices.add(service);
                } catch (JSONException e) {
                    Log.d("KROL", "Error - problem with parsing buyable service.\n" + e.getMessage());
                }
            }
        } catch (JSONException e) {
            Log.d("KROL", "Error: " + e.getMessage());
            throw new Exception("Error while parsing services data");
        }
    }
}

package com.whitespace.magicznybank;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by michal2 on 2015-09-19.
 */
public class AsyncGetUserDataTask extends AsyncTask<Void, Void, Void> {

    private final ApplicationContext appContext;

    AsyncGetUserDataTask(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        User user;

        try {
            String login = "soperkrul";
            String pass = "Dupa.8";

            String userJSon = downloadUserJSon(login, pass);
            if (userJSon.length() == 0)
                throw new Exception("Unknown error - getting user data");

            user = parseUserJSon(userJSon, login, pass);
            if(user == null)
                throw new Exception("Unknown error - parsing user data");

            String servicesJSon = downloadservicesJSon(user);
            if(servicesJSon.length() == 0)
                throw new Exception("Unknown error - getting user services");

            String buyableservicesJSon = downloadBuyableservicesJSon(user);
            if(buyableservicesJSon.length() == 0)
                throw new Exception("Unknown error - getting user services");

            addservicesForUser(user, servicesJSon, buyableservicesJSon);

            CreateOutput(user);
        }
        catch(Exception e) {
            Log.d("KROL", "Error: " + e.getMessage());
            ShowError(e.getMessage());
        }

        return null;
    }

    private void ShowError(final String msg) {
        appContext.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appContext.webView.loadUrl("javascript:ShowError('" + msg + "')");
            }
        });
    }

    private void CreateOutput(final User user) {
        appContext.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appContext.currentUser = user;
                appContext.webView.loadUrl("javascript:FillUserInfo('" + user.id +
                        "', '" + user.name +
                        "', '" + user.surname +
                        "', '" + user.email +
                        "')");


                for (int i = 0; i < user.services.size(); i++) {
                    appContext.webView.loadUrl("javascript:appendService('"
                            + user.services.get(i).name + "', '"
                            + user.services.get(i).description
                            + "', -1)");
                }

                for (int i = 0; i < user.buyableServices.size(); i++) {
                    appContext.webView.loadUrl("javascript:appendService('"
                            + user.buyableServices.get(i).name + "', '"
                            + user.buyableServices.get(i).description + "', "
                            + user.buyableServices.get(i).estimate + ")");
                }
            }
        });
    }

    private String getData(String msg) throws Exception {
        URL url;
        StringBuilder result = new StringBuilder();

        try {
            url = new URL("http://52.19.133.63:4325/?msg=" + msg);

            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = bis.read(buffer)) > 0) {
                String text = new String(buffer, 0, bytesRead);
                result.append(text);
            }
            bis.close();

        } catch (MalformedURLException mue) {
            Log.d("KROL", "Error: " + mue.getMessage());
            throw new Exception("Server connection error");
        } catch (IOException ioe) {
            Log.d("KROL", "Error: " + ioe.getMessage());
            throw new Exception("Server data reading error");
        }

        return result.toString();
    }

    private String downloadUserJSon(String login, String pass) throws Exception {
        return getData("{\"method\":\"user_data\",\"login\":\"" + login + "\",\"pass\":\"" + pass + "\"}");
    }

    private String downloadservicesJSon(User user) throws Exception {
        return getData("{\"method\":\"list_services\"," + user.LoginDataJSon() + "}");
    }

    private String downloadBuyableservicesJSon(User user) throws Exception {
        return getData("{\"method\":\"list_buyable_services\"," + user.LoginDataJSon() + "}");
    }

    private User parseUserJSon(String json, String login, String pass) throws Exception {
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

    private void addservicesForUser(User user, String servicesJSon, String buyableservicesJSon) throws Exception {
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

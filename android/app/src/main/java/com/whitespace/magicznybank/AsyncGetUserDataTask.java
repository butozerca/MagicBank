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

            String operationsJSon = downloadOperationsJSon(user);
            if(operationsJSon.length() == 0)
                throw new Exception("Unknown error - getting user operations");

            String buyableOperationsJSon = downloadBuyableOperationsJSon(user);
            if(buyableOperationsJSon.length() == 0)
                throw new Exception("Unknown error - getting user operations");

            addOperationsForUser(user, operationsJSon, buyableOperationsJSon);

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

                if(appContext.allBankOperations != null) {
                    for (int i = 0; i < appContext.allBankOperations.size(); i++) {
                        if (user.availableOperations.contains(appContext.allBankOperations.get(i).id)) {
                            appContext.webView.loadUrl("javascript:appendOperation('"
                                    + appContext.allBankOperations.get(i).name + "', '"
                                    + appContext.allBankOperations.get(i).description
                                    + "', -1)");
                        } else {
                            appContext.webView.loadUrl("javascript:appendOperation('"
                                    + appContext.allBankOperations.get(i).name + "', '"
                                    + appContext.allBankOperations.get(i).description + "', "
                                    + appContext.allBankOperations.get(i).price + ")");
                        }

                    }
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

    private String downloadOperationsJSon(User user) throws Exception {
        return getData("{\"method\":\"list_services\"," + user.LoginDataJSon() + "}");
    }

    private String downloadBuyableOperationsJSon(User user) throws Exception {
        return getData("{\"method\":\"list_buyable_services\"," + user.LoginDataJSon() + "}");
    }

    private User parseUserJSon(String json, String login, String pass) throws Exception {
        String id, name, surname, email, tariff;
        Double maxLoan, money;

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

        } catch (JSONException e) {
            Log.d("KROL", "Error: " + e.getMessage());
            throw new Exception("Server error - missing user data");
        }

        return new User(id, login, pass, name, surname, email, maxLoan, money, tariff);
    }

    private void addOperationsForUser(User user, String operationsJSon, String userOperationsJSon) throws Exception {
        try {
            JSONArray allOperationsArr = new JSONArray(operationsJSon);
            JSONArray userOperationsArr = new JSONArray(userOperationsJSon);

            for (int i = 0; i < userOperationsArr.length(); i++) {
                int id = userOperationsArr.getJSONObject(i).getInt("id");

                user.availableOperations.add(id);
            }

            appContext.allBankOperations = new ArrayList<>();

            for (int i = 0; i < allOperationsArr.length(); i++) {
                int id = allOperationsArr.getJSONObject(i).getInt("id");
                String name = allOperationsArr.getJSONObject(i).getString("name");
                String description = allOperationsArr.getJSONObject(i).getString("description");
                Double price = allOperationsArr.getJSONObject(i).getDouble("price");

                OperationType operationType = new OperationType(id, name, description, price);

                appContext.allBankOperations.add(operationType);
            }
        } catch (JSONException e) {
            Log.d("KROL", "Error: " + e.getMessage());
            throw new Exception("Error while parsing operations data");
        }
    }
}

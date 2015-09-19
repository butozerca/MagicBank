package com.whitespace.magicznybank;

import android.os.AsyncTask;

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
public class AsyncGetUserDataTask extends AsyncTask<Void, Void, User> {

    private final ApplicationContext appContext;

    AsyncGetUserDataTask(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Override
    protected User doInBackground(Void... arg0) {
        String userJSon = downloadUserJSon();

        User user = null;
        try {
            user = parseUserJSon(userJSon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String operationsJSon = downloadOperationsJSon();
        String userOperationsJSon = downloadUserOperationsJSon(user);

        try {
            addOperationsForUser(user, operationsJSon, userOperationsJSon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    protected void onPostExecute(User user) {
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

    private String downloadUserJSon() {
        return downloadJSon("http://busio.com.pl/hackjamnet/our/user.html");
    }

    private String downloadOperationsJSon() {
        return downloadJSon("http://busio.com.pl/hackjamnet/bank/operations.html");
    }

    private String downloadUserOperationsJSon(User user) {
        return downloadJSon("http://busio.com.pl/hackjamnet/our/userOperations.html");
    }

    private String downloadJSon(String path) {
        URL url;
        StringBuilder result = new StringBuilder();

        try {
            url = new URL(path);

            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = bis.read(buffer)) > 0) {
                String text = new String(buffer, 0, bytesRead);
                result.append(text);
            }
            bis.close();

        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return result.toString();
    }

    private User parseUserJSon(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        int id = obj.getInt("id");
        String name = obj.getString("name");
        String surname = obj.getString("surname");
        String email = obj.getString("email");

        return new User(id, name, surname, email);
    }

    private void addOperationsForUser(User user, String operationsJSon, String userOperationsJSon) throws JSONException {
        JSONArray allOperationsArr = new JSONArray(operationsJSon);
        JSONArray userOperationsArr = new JSONArray(userOperationsJSon);

        for (int i = 0; i < userOperationsArr.length(); i++)
        {
            int id = userOperationsArr.getJSONObject(i).getInt("id");

            user.availableOperations.add(id);
        }

        appContext.allBankOperations = new ArrayList<>();

        for (int i = 0; i < allOperationsArr.length(); i++)
        {
            int id = allOperationsArr.getJSONObject(i).getInt("id");
            String name = allOperationsArr.getJSONObject(i).getString("name");
            String description = allOperationsArr.getJSONObject(i).getString("description");
            Double price = allOperationsArr.getJSONObject(i).getDouble("price");

            OperationType operationType = new OperationType(id, name, description, price);

            appContext.allBankOperations.add(operationType);
        }
    }
}

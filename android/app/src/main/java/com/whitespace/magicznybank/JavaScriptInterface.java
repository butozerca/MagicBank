package com.whitespace.magicznybank;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by Jakub on 19.09.2015.
 */
public class JavaScriptInterface {

    private Activity activity;
    private ApplicationContext appContext;

    public JavaScriptInterface(Activity activiy, ApplicationContext appContext) {
        this.activity = activiy;
        this.appContext = appContext;
    }

    @JavascriptInterface
    public void showToast(String message) {
        Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @JavascriptInterface
    public void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, MainActivity.REQUEST_IMAGE_CAPTURE);
        }
    }

    @JavascriptInterface
    public void readLocation() {
        try {
            if (appContext.locationManager == null)
                return;
            final Location loc = appContext.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    appContext.webView.loadUrl("javascript:initMap(" + loc.getLatitude() +"," + loc.getLongitude() + ")");
                }
            });
            appContext.location = loc;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void readMarkerLocation(String s) {
        String[] q = s.split(" ");
        double lat = Double.valueOf(q[0]);
        double lng = Double.valueOf(q[1]);
        appContext.markerLocation = new Location(LocationManager.GPS_PROVIDER);
        appContext.markerLocation.setLatitude(lat);
        appContext.markerLocation.setLongitude(lng);
    }

    @JavascriptInterface
    public void login(String data) {
        if (data == null || data.length() == 0) {
            WebViewHelper.ShowError("Brak danych logowania");
            return;
        }

        String[] dataTab = data.split(";");
        if(dataTab.length != 2) {
            WebViewHelper.ShowError("Niepoprawna ilosc parametrow logowania");
            return;
        }

        String login = dataTab[0];
        String pass = dataTab[1];

        if(login.length() == 0 || pass.length() == 0) {
            WebViewHelper.ShowError("Niepoprawna dlugosc parametrow logowania");
            return;
        }

        User user;

        try {
            String userJSon = ServerConnectionHelper.downloadUserJSon(login, pass);
            if (userJSon.length() == 0)
                throw new Exception("Unknown error - getting user data");

            user = JSonHelper.parseUserJSon(userJSon, login, pass);
            if(user == null)
                throw new Exception("Unknown error - parsing user data");

            String servicesJSon = ServerConnectionHelper.downloadservicesJSon(user);
            if(servicesJSon.length() == 0)
                throw new Exception("Unknown error - getting user services");

            String buyableservicesJSon = ServerConnectionHelper.downloadBuyableservicesJSon(user);
            if(buyableservicesJSon.length() == 0)
                throw new Exception("Unknown error - getting user services");

            JSonHelper.addServicesForUser(user, servicesJSon, buyableservicesJSon);

            LoginOutput(user);
        }
        catch(Exception e) {
            Log.d("KROL", "Error: " + e.getMessage());
            WebViewHelper.ShowError(e.getMessage());
        }
    }

    private void LoginOutput(User user) {
        appContext.currentUser = user;

        Log.d("KROL", "Login success");

        WebViewHelper.RunJsFunction("FillUserInfo",
                "'" + user.id +
                "', '" + user.name +
                "', '" + user.surname +
                "', '" + user.email +
                "'");

        for (int i = 0; i < user.services.size(); i++) {
            WebViewHelper.RunJsFunction("appendService", "'"
                    + user.services.get(i).name + "', '"
                    + user.services.get(i).description
                    + "', -1");
        }

        for (int i = 0; i < user.buyableServices.size(); i++) {
            WebViewHelper.RunJsFunction("appendService", "'"
                    + user.buyableServices.get(i).name + "', '"
                    + user.buyableServices.get(i).description + "', "
                    + user.buyableServices.get(i).estimate);
        }
    }

}

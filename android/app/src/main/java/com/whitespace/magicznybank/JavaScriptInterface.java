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
            final Location loc = appContext.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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
        appContext.markerLocation = new Location(LocationManager.NETWORK_PROVIDER);
        appContext.markerLocation.setLatitude(lat);
        appContext.markerLocation.setLongitude(lng);
    }

}

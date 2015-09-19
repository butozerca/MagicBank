package com.whitespace.magicznybank;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.webkit.WebView;

import java.util.List;

/**
 * Created by michal2 on 2015-09-19.
 */
public class ApplicationContext {
    public Activity activity;
    public WebView webView;
    public User currentUser;
    public LocationManager locationManager;
    public Location location;

}

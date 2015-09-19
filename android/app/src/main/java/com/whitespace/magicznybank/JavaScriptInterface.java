package com.whitespace.magicznybank;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by Jakub on 19.09.2015.
 */
public class JavaScriptInterface {
    private Activity activity;

    public JavaScriptInterface(Activity activiy) {
        this.activity = activiy;
    }

    @JavascriptInterface
    public void showToast(String message) {
        Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
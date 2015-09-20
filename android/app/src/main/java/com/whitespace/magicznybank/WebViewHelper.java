package com.whitespace.magicznybank;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by Jakub on 20.09.2015.
 */
public class WebViewHelper {
    public static WebView webView;
    public static Activity activity;

    public static void RunJsFunction(final String function, final String parameters) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + function + "(" + parameters + ")");
            }
        });
    }

    public static void LoginError(String msg) {
        RunJsFunction("LoginError", "'" + msg + "'");
    }
}

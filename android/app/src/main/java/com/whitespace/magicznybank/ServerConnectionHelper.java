package com.whitespace.magicznybank;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jakub on 20.09.2015.
 */
public class ServerConnectionHelper {
    public static String downloadUserJSon(String login, String pass) throws Exception {
        return getData("{\"method\":\"user_data\",\"login\":\"" + login + "\",\"pass\":\"" + pass + "\"}");
    }

    public static String downloadservicesJSon(User user) throws Exception {
        return getData("{\"method\":\"list_services\"," + user.LoginDataJSon() + "}");
    }

    public static String downloadBuyableservicesJSon(User user) throws Exception {
        return getData("{\"method\":\"list_buyable_services\"," + user.LoginDataJSon() + "}");
    }

    private static String getData(String msg) throws Exception {
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
}

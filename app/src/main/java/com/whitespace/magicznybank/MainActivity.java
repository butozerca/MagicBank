package com.whitespace.magicznybank;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import org.json.*;

public class MainActivity extends AppCompatActivity {

    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView)findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);

        JavaScriptInterface jsInterface = new JavaScriptInterface(this);
        webView.addJavascriptInterface(jsInterface, "JSInterface");

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {

            }
        });

        webView.loadUrl("file:///android_asset/index.html");

        //webView.loadUrl("javascript:setInfoFromApp('Dupa dupa oraz kupa')");

        try {
            loadUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadUser() throws JSONException {
        /*String userJSon = downloadUserJSon();
        user = parseUserJSon(userJSon);

        webView.loadUrl("javascript:FillUserInfo('" + user.id +
                "', '" + user.name +
                "', '" + user.surname +
                "', '" + user.email +
                "')");*/

        //user = new User(1, "dupa", "dupa", "dupa@dupa.dupa");

        AsyncGetUserDataTask getUserDataTask = new AsyncGetUserDataTask();
        getUserDataTask.execute();
    }


    public class AsyncGetUserDataTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... arg0) {
            String userJSon = downloadUserJSon();
            User user = null;
            try {
                user = parseUserJSon(userJSon);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return user;
        }

        @Override
        protected void onPostExecute(User user) {

            webView.loadUrl("javascript:FillUserInfo('" + user.id +
                    "', '" + user.name +
                    "', '" + user.surname +
                    "', '" + user.email +
                    "')");
        }

        private String downloadUserJSon() {
            return downloadJSon("http://busio.com.pl/hackjamnet/user.html");
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
    }
}

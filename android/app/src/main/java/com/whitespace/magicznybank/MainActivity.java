package com.whitespace.magicznybank;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.ByteArrayOutputStream;

import org.json.*;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    private final ApplicationContext appContext = new ApplicationContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext.activity = this;

        appContext.webView = (WebView)findViewById(R.id.webView);
        appContext.webView = (WebView) findViewById(R.id.webView);

        appContext.webView.getSettings().setJavaScriptEnabled(true);

        JavaScriptInterface jsInterface = new JavaScriptInterface(this, appContext);
        appContext.webView.addJavascriptInterface(jsInterface, "JSInterface");

        appContext.webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {

            }
        });

        appContext.webView.loadUrl("file:///android_asset/index.html");

        //webView.loadUrl("javascript:setInfoFromApp('Dupa dupa oraz kupa')");

        try {
            loadUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            handleLocation();
        } catch (Exception e) {
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
        AsyncGetUserDataTask getUserDataTask = new AsyncGetUserDataTask(appContext);
        getUserDataTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            String image = "data:image/png;base64," + imgageBase64;

            appContext.webView.loadUrl("javascript:SetPicture('" + image + "')");
        }
    }
    private void handleLocation () {
        appContext.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {}
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };
        try {
            appContext.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

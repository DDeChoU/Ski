package com.example.dgl.ski;

import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String userid = "11";//userid can be modified here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);

        setUpMapIfNeeded();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
                .detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        //double x = -33.867,y=151.206;
        //mMap.addMarker(new MarkerOptions().title("Test").snippet("This is a test").position(new LatLng(x,y)));
        Button alarmBtn = (Button)findViewById(R.id.alarmButton);
        Button findBtn = (Button)findViewById(R.id.findButton);

        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAlarm();
                Toast.makeText(getApplicationContext(),"Your ski will be alarming, hope you can find it.",Toast.LENGTH_SHORT).show();

            }
        });
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSkiLocation();
            }
        });
        getSkiLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    private void sendAlarm(){
        String url = "http://ipole.virgin-foundation.ch/updatenotification.php?userid=";
        url+= userid;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpPost = new HttpGet(url);
        HttpResponse httpResponse = null;
        try{
            httpResponse = httpClient.execute(httpPost);
        }
        catch(ClientProtocolException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
       mMap.setMyLocationEnabled(true);
       // getMyLocation();

    }
    private void locate(double x, double y,int type) {
        //give in coordinates and show it on the map
        //type defines whether its me or the ski
        String snippet[] = new String[2];
        snippet[0]="Your ski was here.";
        snippet[1]="Your ski is here.";
        mMap.addMarker(new MarkerOptions().title("Ski").snippet(snippet[type]).position(new LatLng(x, y)));
        if(type==1) mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(x, y), 70));

    }
    // the same to use Google's own location function
   /* private void getMyLocation(){
        // get the manager of location
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);
        // set service information
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // high accuracy
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // low power mode

        String provider = locationManager.getBestProvider(criteria, true); // get GPS information
        Location location = locationManager.getLastKnownLocation(provider); // get through GPS
        locate(location.getLongitude(), location.getLatitude(), 0);

    }*/
    private void getSkiLocation(){
        //get Ski location from the server here
        double x=-12,y=46.95;
        String url = "http://ipole.virgin-foundation.ch/read.php?userid=";
        url+= userid;
        getServerData(url);


    }
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

    }
    private void getServerData(String url) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpPost = new HttpGet(url);
        HttpResponse httpResponse = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject jsonObject=null;

        try {


            httpResponse = httpClient.execute(httpPost);


            //get response and parse the JSON form text
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

            StringBuilder builder = new StringBuilder();
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                builder.append(s);
            }
            String t = builder.toString();

            /*url = "http://api.map.baidu.com/ag/coord/convert?from=0&to=2";//&x=120.0904441&y=30.3056719";
            url += ("&x=" + x + "&y=" + y);
            httpPost = new HttpGet(url);
            httpResponse = httpClient.execute(httpPost);
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            builder = new StringBuilder();
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                builder.append(s);
            }
            jsonObject = new JSONObject(builder.toString());
            Double xNow = Double.valueOf(jsonObject.getString("x"));
            Double yNow = Double.valueOf(jsonObject.getString("y"));
            conversion of coordinates, cannot be used */

            String x[] = new String[100];
            String y[] = new String [100];
            int latest = 0;
            String latestDate = "2150-01-01";
            int counter = 0;
            while(t.indexOf("}")!=t.length()-1) {
                String now = t.substring(0, t.indexOf("}")+1);
                t = t.substring(t.indexOf("}") + 1, t.length());
                JSONObject temp = new JSONObject(now);
                x[counter] = temp.getString("longx");
                y[counter] = temp.getString("laty");
                if (temp.getString("dateupdated").compareTo(latestDate) < 0) {
                    latest = counter;
                    latestDate = temp.getString("dateupdated");
                }
                counter++;

            }
           /* url = "http://api.map.baidu.com/ag/coord/convert?from=0&to=2";//&x=120.0904441&y=30.3056719";

            //String xl = jsonObject.getString("xlast");
            //String yl = jsonObject.getString("ylast");
            url += ("&x=" + x[latest] + "&y=" + y[latest]);
            httpPost = new HttpGet(url);

            httpResponse = httpClient.execute(httpPost);
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            builder = new StringBuilder();
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                builder.append(s);
            }
            jsonObject = new JSONObject(builder.toString());
            double xNow = Double.valueOf(jsonObject.getString("x"));
            double yNow = Double.valueOf(jsonObject.getString("y"));*/
           // Toast.makeText(getApplicationContext(),new String(x[latest]+"\n"+y[latest]),Toast.LENGTH_LONG).show();
            PolylineOptions po = new PolylineOptions().geodesic(true);
            locate(Double.valueOf(x[latest]),Double.valueOf(y[latest]),1);
            po.add(new LatLng(Double.valueOf(x[latest]),Double.valueOf(y[latest])));
            for(int i=0;i<counter;i++){
                if(i==latest) continue;

                locate(Double.valueOf(x[i]), Double.valueOf(y[i]), 0);
                //Toast.makeText(getApplicationContext(),new String(x[i]+"\n"+y[i]),Toast.LENGTH_LONG).show();
                po.add(new LatLng(Double.valueOf(x[i]),Double.valueOf(y[i])));
            }
            mMap.addPolyline(po);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}

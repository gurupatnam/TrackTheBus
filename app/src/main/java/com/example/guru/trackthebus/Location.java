package com.example.guru.trackthebus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;

public class Location extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    GPSTracker gps;
    Context mContext;
    JSONParser jsonParser;
    Button btn;
    SessionManager sessionManager;

    public double latitude, longitude;
    ArrayList<LatLng> markerPoints;
    public static JSONArray zeba;
    private static InputStream is = null;
    private static String json = "";
    public GoogleMap map;
    Marker mp;

    static LatLng sd,ccamp,govt,kids,check,dev,qwert;
    static String distance = "";
    static String duration = "";
    public static String slat,slog,dlat,dlog,slat1,slat2,slat3,sdest3,slat4,sdest4,slat5,sdest5,slat6,sdest6,dselat,dselod,apswe,awesq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mContext=this;
        sessionManager=new SessionManager(getApplicationContext());
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            Toast.makeText(mContext,"You need have granted permission",Toast.LENGTH_SHORT).show();
            gps = new GPSTracker(mContext, Location.this);

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                // \n is for new line
                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        }
       // gps = new GPSTracker(Location.this);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);
        map=fm.getMap();
//        getCurrentLocation();
        //if(gps.canGetLocation()){
          //  String latitude = Double.toString(gps.getLatitude());
            //String longitude = Double.toString(gps.getLongitude());
            //Toast.makeText(getApplicationContext(), (latitude+longitude),LENGTH_LONG).show();

            // \n is for new line
//        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
  //          gps.showSettingsAlert();
    //    }
       /* map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                markerPoints=new ArrayList<LatLng>();

                markerPoints.add(point);

                MarkerOptions options = new MarkerOptions();

                options.position(point);

                //if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
              //  }

                map.addMarker(options);

                if (markerPoints.size() >= 1) {
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(0);
                    Toast.makeText(getApplicationContext(),origin.latitude+"="+origin.longitude,LENGTH_LONG).show();
                }
            }
        });*/

        //for setting directions from source to destination
        ut();
        btn=(Button)findViewById(R.id.button5);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager se=new SessionManager(getApplicationContext());
                se.logoutUser();
            }
        });
        //for getting updated bus location everytime
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                qwert=marker.getPosition();
                LatLng lt=marker.getPosition();
                apswe=String.valueOf(lt.latitude);
                awesq=String.valueOf(lt.longitude);
                //Toast.makeText(getApplicationContext(),lt.toString(),LENGTH_LONG).show();
                zes();
                marker.showInfoWindow();
                return true;
            }
        });
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
           public void onMyLocationChange(android.location.Location location) {
                put();
            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
    }

    //**************************************************************************************************************
    //simplification code for get distance and time
    public void zes(){
        josn josn=new josn();
        josn.execute();
    }
    private class josn extends AsyncTask<String,String,String> {
        //String apswe;

        @Override
        public String doInBackground(String... latLngs) {
            String URL = "https://guruzeba.000webhostapp.com/guruzeba.php";
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL);
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder str = new StringBuilder();
                String strLine;
                while ((strLine = reader.readLine()) != null) {
                    str.append(strLine).append("\n");
                }
                is.close();
                json = str.toString();
            } catch (Exception ignored) {
            }
            return json;
        }
        public void onPostExecute(String String) {
            try {
                JSONObject zeb = new JSONObject(String);
                zeba = zeb.optJSONArray("emp_info");
                for (int i = 0; i < zeba.length(); i++) {
                    HashMap<String, String> map = new HashMap<>();
                    JSONObject zs = zeba.optJSONObject(i);
                    slat = zs.optString("latitude");
                    slog = zs.optString("longitude");
                    String zes=getDirectionsUrl(slat,slog,apswe,awesq);
                    loadTask doload=new loadTask();
                    doload.execute(zes);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    //*****************************************************************************************************
    // Fetches data from url passed
    private class loadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Task parserTask = new Task();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    private class Task extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
            latitude = Double.parseDouble(apswe);
            longitude = Double.parseDouble(awesq);
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {
                //Toast.makeText(getApplicationContext(), (CharSequence) addresses.get(0),Toast.LENGTH_LONG).show();
                System.out.println(addresses.get(0).getLocality());
                String z = addresses.get(0).getLocality();
                String zeba = addresses.get(0).getAddressLine(0);
                //Toast.makeText(getApplicationContext(), zeba, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), z, Toast.LENGTH_LONG).show();
                mMap.addMarker(new MarkerOptions().position(qwert).title(distance+duration+zeba)).showInfoWindow();
            } else {
                Toast.makeText(getApplicationContext(), "cannot get address", Toast.LENGTH_LONG).show();
            }
            // Drawing polyline in the Google Map for the i-th route
            //map.addPolyline(lineOptions);
        }
    }
    //**************************************************************************************************************
    //************************************************************************************************************

    //end of simplification code for distance and time
    //***************************************************************************************************************
    //***************************************************************************************************************
    //getting source location and destination location from web
    public void ut() {
        JsonReadTsk task = new JsonReadTsk();
        task.execute();
    }
    public class JsonReadTsk extends AsyncTask<String, String, String> {
        @Override
        public String doInBackground(String... params) {
            String URL = "https://guruzeba.000webhostapp.com/guruzeba.php";
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL);
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder str = new StringBuilder();
                String strLine;
                while ((strLine = reader.readLine()) != null) {
                    str.append(strLine).append("\n");
                }
                is.close();
                json = str.toString();
            } catch (Exception ignored) {
            }
            return json;
        }

        public void onPostExecute(String String) {
            try {
                JSONObject zeb = new JSONObject(String);
                zeba = zeb.optJSONArray("emp_info");
                for (int i = 0; i < zeba.length(); i++) {
                    JSONObject zs = zeba.optJSONObject(i);
                    dselat=zs.optString("soulat");
                    dselod=zs.optString("soulog");
                    slat = zs.optString("latitude");
                    slog = zs.optString("longitude");
                    dlat = zs.optString("deslat");
                    dlog = zs.optString("deslog");
                    slat1=zs.optString("sor4");
                    slat2=zs.optString("dest4");
                    slat3=zs.optString("ccampsor");
                    sdest3=zs.optString("ccampdest");
                    slat4=zs.optString("sor3");
                    sdest4=zs.optString("dest3");
                    slat5=zs.optString("govsor");
                    sdest5=zs.optString("govdest");
                    slat6=zs.optString("kidsor");
                    sdest6=zs.optString("kiddest");
                    //for setting markers and directions in map
                    LatLng syd = new LatLng(Double.parseDouble(zs.optString("sor4")),Double.parseDouble(zs.optString("dest4")));
                    mMap.addMarker(new MarkerOptions().position(syd));
                    LatLng sydne = new LatLng(Double.parseDouble(zs.optString("ccampsor")),Double.parseDouble(zs.optString("ccampdest")));
                    mMap.addMarker(new MarkerOptions().position(sydne));
                    LatLng sydn = new LatLng(Double.parseDouble(zs.optString("sor3")),Double.parseDouble(zs.optString("dest3")));
                    mMap.addMarker(new MarkerOptions().position(sydn));
                    LatLng syn = new LatLng(Double.parseDouble(zs.optString("govsor")),Double.parseDouble(zs.optString("govdest")));
                    mMap.addMarker(new MarkerOptions().position(syn));
                    LatLng sdn = new LatLng(Double.parseDouble(zs.optString("kidsor")),Double.parseDouble(zs.optString("kiddest")));
                    mMap.addMarker(new MarkerOptions().position(sdn));
                    LatLng sss = new LatLng(Double.parseDouble(zs.optString("deslat")),Double.parseDouble(zs.optString("deslog")));
                    mMap.addMarker(new MarkerOptions().position(sss));
                    String zes=getDirectionsUrl(dselat,dselod,dlat,dlog);
                    DoloadTask doload=new DoloadTask();
                    doload.execute(zes);
                    //GoogleMap Map;
                    LatLng sydney = new LatLng(Double.parseDouble(dselat),Double.parseDouble(dselod));
                    mMap.addMarker(new MarkerOptions().position(sydney));
                    sd=new LatLng(Double.parseDouble(dlat),Double.parseDouble(dlog));
                    ccamp=new LatLng(Double.parseDouble(slat3),Double.parseDouble(sdest3));
                    govt=new LatLng(Double.parseDouble(slat5),Double.parseDouble(sdest5));
                    kids=new LatLng(Double.parseDouble(slat6),Double.parseDouble(sdest6));
                    check=new LatLng(Double.parseDouble(slat4),Double.parseDouble(sdest4));
                    dev=new LatLng(Double.parseDouble(slat1),Double.parseDouble(slat2));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //**********************************************************************************************
    //*******************************************************************************************
        public String getDirectionsUrl(String z,String e,String b,String a) {
        // Origin of route
            //Toast.makeText(getApplicationContext(),z+e,LENGTH_LONG).show();
        String str_origin = "origin="+z+","+e;
        // Destination of route
        String str_dest = "destination="+b+","+a;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    //**************************************************************************************************************
    //************************************************************************************************************
    //*****************************************************************************************************
    // Fetches data from url passed
    private class DoloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            PeTask parserTask = new PeTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    private class PeTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            // Traversing   through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
            //mMap.addMarker(new MarkerOptions().position(ccamp).title(distance+duration)).showInfoWindow();

        }
    }
    //**************************************************************************************************************
    //************************************************************************************************************




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        //LatLng latLng = new LatLng(gps.location.getLatitude(), gps.location.getLongitude());
        LatLng sydney = new LatLng(15.80690810,78.04233167);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
        mMap.addMarker(new MarkerOptions().position(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private void drawMarker(LatLng point) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);
        if (mp != null) {
            mp.remove();
        }

        // Adding marker on the Google Map
        mp=map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(point));
    }
    //for getting updated bus location everytime
    public void put() {
        JsonReadTask task = new JsonReadTask();
        task.execute();
    }

    public class JsonReadTask extends AsyncTask<String, String, String> {


        @Override
        public String doInBackground(String... params) {
            String URL = "https://guruzeba.000webhostapp.com/guruzeba.php";
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL);
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder str = new StringBuilder();
                String strLine;
                while ((strLine = reader.readLine()) != null) {
                    str.append(strLine).append("\n");
                }
                is.close();
                json = str.toString();
            } catch (Exception ignored) {
            }
            return json;
           /* HashMap<String,String> gtr=new HashMap<>();
            gtr=sessionManager.getUserDetails();
            String z=gtr.get("KEY_NAME");
            String g=gtr.get("KEY_PASS");
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("username", z));
            param.add(new BasicNameValuePair("password", g));
            Log.d("request!", "starting");
            JSONObject json = jsonParser.makeHttpRequest(
                    "http://gurupatnam.net23.net/guruzeba.php", "POST", param);
            return json.toString();*/
        }

        public void onPostExecute(String String) {
            try {

                JSONObject zeb = new JSONObject(String);
                zeba = zeb.optJSONArray("emp_info");
                for (int i = 0; i < zeba.length(); i++) {
                    JSONObject zs = zeba.optJSONObject(i);
                    double lt = Double.parseDouble(zs.optString("latitude"));
                    double lg = Double.parseDouble(zs.optString("longitude"));
                    float[] results = new float[1];
                    android.location.Location.distanceBetween(latitude, longitude,
                            lt, lg, results);
                        drawMarker(new LatLng(lt, lg));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void getCurrentLocation() {
        CustomLocationManager.getCustomLocationManager().getCurrentLocation(this, locationValue);
    }

    public LocationValue locationValue = new LocationValue() {

        @Override
        public void getCurrentLocation(android.location.Location location) {
            if(location != null) {
                Log.d("LOCATION", location.getLatitude() + ", " + location.getLongitude());
            }
        }};
}

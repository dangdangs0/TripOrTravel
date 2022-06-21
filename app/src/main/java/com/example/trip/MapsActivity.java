package com.example.trip;

import static com.example.trip.BluetoothActivity.connectedThread;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.libraries.places.compat.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;

import android.os.Handler;
import android.os.ResultReceiver;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.example.trip.service.FetchAddressIntentService;
import com.example.trip.utils.Connections;
import com.example.trip.utils.Constants;
import com.example.trip.utils.PermissionGPS;


//import static com.example.trip.BluetoothActivity.connectedThread;

import android.graphics.Color;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesListener;
import noman.googleplaces.PlacesException;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PlacesListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private LatLng LocationA = new LatLng(35.23, 129.08);

    private static final float DEFAULT_ZOOM = 9.5f;

    private static final long UPDATE_INTERVAL = 500;
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 5;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static boolean gpsFirstOn = true;

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProvider;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location locationgps;
    private ResultReceiver resultReceiver;
    private Marker selectedMarker = null;
    private LatLng currentLatLng;

    private LatLng mOrigin;
    private LatLng mDestination;
    private Polyline mPolyline;
    private ArrayList<LatLng> mMarkerPoints;

    Button searchBtn;
    EditText editText;
    String Area,Sigungu,Tour_spot;

    int height = 100;
    int width = 100;


    private List<Marker> previous_marker = new ArrayList<>();
    int x = 0;
    @SuppressLint("SetTextI18n")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        if (!Connections.checkConnection(this)) {
            Toast.makeText(this, "네트워크 오류입니다. 연결을 확인하세요.", Toast.LENGTH_SHORT).show();
            finish();
        }

        init();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        mMarkerPoints = new ArrayList<>();


        locationgps = new Location("Point A");
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        resultReceiver = new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                String addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                Toast.makeText(getApplicationContext(), addressOutput, Toast.LENGTH_SHORT).show();
            }
        };

        searchBtn = findViewById(R.id.button1);
        editText = findViewById(R.id.editText);

        final InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE) ;

        Area=getIntent().getStringExtra("selectedArea");
        Sigungu=getIntent().getStringExtra("selectedSigungu");
        Tour_spot=getIntent().getStringExtra("selectedTourspot");

        editText.setText(Area+" "+Sigungu+" "+Tour_spot);

        searchBtn.callOnClick();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length() > 0) {
//                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    Location location = getLocationFromAddress(getApplicationContext(), editText.getText().toString());
                    showCurrentLocation(location);
                }
            }
        });



    }

    private Location getLocationFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses;
        Location resLocation = new Location("");
        try {
            addresses = geocoder.getFromLocationName(address, 5);
            if((addresses == null) || (addresses.size() == 0)) {
                return null;
            }
            Address addressLoc = addresses.get(0);

            resLocation.setLatitude(addressLoc.getLatitude());
            resLocation.setLongitude(addressLoc.getLongitude());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resLocation;
    }

    @SuppressLint("SetTextI18n")
    private void init() {

        //위성 플롯액션바
        final FloatingActionButton ft = findViewById(R.id.fbsatelit);
        ft.setOnClickListener(view -> {
            if (map != null) {
                int MapType = map.getMapType();
                if (MapType == 1) {
                    ft.setImageResource(R.drawable.ic_satellite_off);
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    ft.setImageResource(R.drawable.ic_satellite_on);
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
        //gps 플롯액션바
        FloatingActionButton fm = findViewById(R.id.fbgps);
        fm.setOnClickListener(view -> {
            getDeviceLocation(true);
            if (!Geocoder.isPresent()) {
                showSnackbar(R.string.no_geocoder_available, Snackbar.LENGTH_LONG, 0, null);
            } else {
                showAddress();
                setCurrentLocation(locationgps);
            }
        });

        //주변 음식점 플롯액션바
        final FloatingActionButton fd = findViewById(R.id.fbutton);
        fd.setOnClickListener(view -> {
            if (map != null) {
                Location location = getLocationFromAddress(getApplicationContext(), editText.getText().toString());
                if (location != null) {
                    LatLng destination = new LatLng(location.getLatitude(), location.getLongitude());
                    showPlaceInformation(destination);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15.5f));
                }

            }
        });



        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    return;

                currentLatLng = new LatLng(locationgps.getLatitude(), locationgps.getLongitude());

                for (Location locationUpdate : locationResult.getLocations()) {
                    locationgps = locationUpdate;
                    if (gpsFirstOn) {
                        gpsFirstOn = false;
                        getDeviceLocation(true);
                    }
                }

                if(x==0) {
                    setCurrentLocation(locationgps);
                    x++;
                }
            }


        };


        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }


    @SuppressLint("MissingPermission")
    private void getDeviceLocation(final boolean MyLocation) {
        if (!MyLocation)

            if (checkPermission()) {
                if (map != null)
                    map.setMyLocationEnabled(true);

                final Task<Location> locationResult = fusedLocationProvider.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // lastKnownLocation = task.getResult();
                    } else {
                        Log.w(TAG, "getLastLocation:exception", task.getException());

                        showSnackbar(R.string.no_location_detected, Snackbar.LENGTH_LONG, 0, null);

                    }
                });
            } else // !checkPermission()
                Log.d(TAG, "Current location is null. Permission Denied.");
    }

    public void setCurrentLocation(Location location) {
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.5f));
    }

    private void showCurrentLocation(Location location) {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
        String markerTitle = "검색 위치";
        String markerSnippet = editText.getText().toString();


        String msg = "Latitutde : " + curPoint.latitude
                + "\nLongitude : " + curPoint.longitude;
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        //화면 확대, 숫자가 클수록 확대
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(curPoint);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);


        markerOptions.draggable(true);

        BitmapDrawable destination = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_de1);
        Bitmap des = destination.getBitmap();
        Bitmap ic_des = Bitmap.createScaledBitmap(des, width, height, false);

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(ic_des));
        selectedMarker = map.addMarker(markerOptions);
    }

    //gps에서 주소 보여주는 로그
    private void showAddress() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, locationgps);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Connections.checkConnection(this)) {
            new PermissionGPS(this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (Connections.checkConnection(this)) {
            new PermissionGPS(this);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        if (Connections.checkConnection(this)) {
            if (checkPermission())
                fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length <= 0)
                Log.i(TAG, "User interaction was cancelled.");
            else // grantResults.length > 0
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getDeviceLocation(false);
                else
                    showSnackbar(R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE, android.R.string.ok,
                            view -> requestPermission());
        }
    }

    private void showSnackbar(int textStringId, int length, int actionStringId, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), textStringId, length);
        if (listener != null)
            snackbar.setAction(actionStringId, listener);
        snackbar.show();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(LocationA, 15.5f));

        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
//        map.getUiSettings().setCompassEnabled(false);

        // TODO : location
        map.getProjection().getVisibleRegion();

        if (!checkPermission())
            requestPermission();

        getDeviceLocation(false);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // Already two locations
                if(mMarkerPoints.size()>1){
                    mMarkerPoints.clear();
                    map.clear();
                }

                // Adding new item to the ArrayList
                mMarkerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                BitmapDrawable start = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_start);
                BitmapDrawable end = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_end);
                Bitmap s = start.getBitmap();
                Bitmap e = end.getBitmap();
                Bitmap sb = Bitmap.createScaledBitmap(s, width, height, false);
                Bitmap eb = Bitmap.createScaledBitmap(e, width, height, false);

                if(mMarkerPoints.size()==1){
                    options.icon(BitmapDescriptorFactory.fromBitmap(sb));
                }else if(mMarkerPoints.size()==2){
                    options.icon(BitmapDescriptorFactory.fromBitmap(eb));
                }

                // Add new marker to the Google Map Android API V2
                map.addMarker(options);

                // Checks, whether start and end locations are captured
                if(mMarkerPoints.size() >= 2){
                    mOrigin = mMarkerPoints.get(0);
                    mDestination = mMarkerPoints.get(1);
                    drawRoute();
                }


            }
        });
    }

    private void drawRoute(){

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(mOrigin, mDestination);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Key
        String key = "key=" + getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = map.addPolyline(lineOptions);

            }else
                System.out.println("no route");

            //Toast.makeText(getApplicationContext(),String.valueOf(getDistance(mOrigin,mDestination))+"m", Toast.LENGTH_LONG).show();

            ((TextView) findViewById(R.id.tvDistance)).setText(String.valueOf((int)(getDistance(mOrigin,mDestination)))+"m");
            ((TextView) findViewById(R.id.tvTime)).setText(String.valueOf((int)getDistance(mOrigin,mDestination)/66)+"분");


            if(connectedThread!=null){
                if(getDistance(mOrigin,mDestination)<500)
                    connectedThread.write("good");
                else
                    connectedThread.write("bad");
            }
        }
    }

    public double getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);
        return distance;
    }

    @Override
    public void onPlacesFailure(PlacesException e){}

    @Override
    public void onPlacesStart(){}
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            //Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    @Override
    public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {


        BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.drawable.location);
        Bitmap b = bd.getBitmap();
        Bitmap sm = Bitmap.createScaledBitmap(b, width, height, false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (noman.googleplaces.Place place : places){
                    LatLng latLng = new LatLng(place.getLatitude(),place.getLongitude());
                    String markerSnippet = getCurrentAddress(latLng);

                    MarkerOptions mO = new MarkerOptions();
                    mO.position(latLng);
                    mO.title(place.getName());
                    mO.snippet(markerSnippet);
                    mO.icon(BitmapDescriptorFactory.fromBitmap(sm));
                    Marker item = map.addMarker(mO);
                    previous_marker.add(item);
                }
                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);
            }
        });
    }


    @Override
    public void onPlacesFinished(){}

    public void showPlaceInformation(LatLng location)
    {
        String key = getString(R.string.google_maps_key);
        //map.clear();//지도 클리어
        Location l = getLocationFromAddress(getApplicationContext(), editText.getText().toString());

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어
        new NRPlaces.Builder()
                .listener(MapsActivity.this)
                .key(key)
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(500) //500 미터 내에서 검색
                .type(PlaceType.RESTAURANT) //음식점
                .build()
                .execute();
    }


}
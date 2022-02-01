package sorin.umbrasportiva;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class ExecutingActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView text_collected_b, text_evaluated_b, text_activity_b, text_speed_b, text_lat_b, text_long_b, text_accuracy_b, text_distance_b;
    Switch sw_datacollection;
    ImageView img_activity_b;
    private GoogleMap mMap;

    private static final String TAG = "ExecutingActivity";

    FusedLocationProviderClient fusedClient;
    private LocationRequest mRequest;
    private LocationCallback mCallback;
    Location currentLocation;
    List<Location> myLocations;

    private static Context USContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executing);



        text_collected_b = findViewById(R.id.text_collected_b);
        text_evaluated_b = findViewById(R.id.text_evaluated_b);
        text_speed_b = findViewById(R.id.text_speed_b);
        text_lat_b = findViewById(R.id.text_lat_b);
        text_long_b = findViewById(R.id.text_long_b);
        text_accuracy_b = findViewById(R.id.text_accuracy_b);
        text_activity_b = findViewById(R.id.text_activity_b);
        text_distance_b = findViewById(R.id.text_distance_b);
        sw_datacollection = findViewById(R.id.sw_datacollection);

        img_activity_b = findViewById(R.id.img_activity_b);

        USContext = getApplicationContext();
        SQLiteHelper sqLiteHelper = new SQLiteHelper(USContext);

        sw_datacollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_datacollection.isChecked()) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });

        mCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location loc : locationResult.getLocations()) {
                    updateGPS(loc);
                    updateMap();
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showRationale();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE}, 2);
            }
        } else {
            //we already have the permission. Do any location wizardry now
            initLocation();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView_b);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    private void initLocation() {
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        //Initially, get last known location. We can refine this estimate later
        fusedClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    String loc = location.getProvider() + ":Accu:(" + location.getAccuracy() + "). Lat:" + location.getLatitude() + ",Lon:" + location.getLongitude();
                }
            }
        });


        //now for receiving constant location updates:
        createLocRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mRequest);

        //This checks whether the GPS mode (high accuracy,battery saving, device only) is set appropriately for "mRequest". If the current settings cannot fulfil
        //mRequest(the Google Fused Location Provider determines these automatically), then we listen for failutes and show a dialog box for the user to easily
        //change these settings.
        SettingsClient client = LocationServices.getSettingsClient(ExecutingActivity.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(ExecutingActivity.this, 500);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedClient.removeLocationUpdates(mCallback);
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        fusedClient.requestLocationUpdates(mRequest, mCallback, null);
    }

    protected void stopLocationUpdates() {
        fusedClient.removeLocationUpdates(mCallback);
    }


    protected void createLocRequest() {
        mRequest = new LocationRequest();
        mRequest.setInterval(10000);//time in ms; every ~10 seconds
        mRequest.setFastestInterval(5000);
        mRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Thanks bud", Toast.LENGTH_SHORT).show();
                    initLocation();
                } else {
                    Toast.makeText(this, "C'mon man we really need this", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                break;
        }
    }

    private void showRationale() {
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage("Sunt necesare urmatoarele " +
                "permisiuni :)").setPositiveButton("Sure", (dialogInterface, i) ->
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            dialogInterface.dismiss();
        })
                .create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateGPS(Location location) {

        updateUIValues(location);
        currentLocation = location;

        UmbraSportiva myApplication = (UmbraSportiva) getApplicationContext();
        Track mainTrack = myApplication.getMyTrack();

        myLocations = mainTrack.getMyLocations();

        if (mainTrack.getEvaluated() == 0) {
            mainTrack.setStartPoint(location);
        }

        mainTrack.incrementEvaluated();

        if (myLocations.size() > 5) {
            Location lastSavedLocation = myLocations.get(myLocations.size() - 1);
            double distance = lastSavedLocation.distanceTo(currentLocation);
            if (distance > 0) {
                myLocations.add(currentLocation);
                mainTrack.updateTotalDistance(distance);
                mainTrack.updateTrackEnd();
            }
        } else {
            myLocations.add(currentLocation);
        }
    }

    private void updateUIValues(Location location) {

        UmbraSportiva myApplication = (UmbraSportiva) getApplicationContext();
        Track mainTrack = myApplication.getMyTrack();
        myLocations = mainTrack.getMyLocations();


        String[] activities = getResources().getStringArray(R.array.activity_names);
        String[] act_codes = getResources().getStringArray(R.array.activity_code);


        String activityName = null;

        for (int i = 0; i < activities.length; i++) {
            if (act_codes[i].equals(mainTrack.getActivityCode())) {
                activityName = activities[i];
            }
        }

        text_collected_b.setText(String.valueOf(myLocations.size()));
        text_activity_b.setText(String.valueOf(activityName));
        text_evaluated_b.setText(String.valueOf(mainTrack.getEvaluated()));
        text_speed_b.setText(String.valueOf(location.getSpeed()));
        text_lat_b.setText(String.valueOf(location.getLatitude()));
        text_long_b.setText(String.valueOf(location.getLongitude()));
        text_accuracy_b.setText(String.valueOf(location.getAccuracy()));
        text_distance_b.setText(mainTrack.getFormattedDistance());

        String imgResource = mainTrack.getActivityCode();
        img_activity_b.setImageResource(getResources().getIdentifier(imgResource, "drawable", getPackageName()));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void updateMap() {

        runOnUiThread(new Runnable(){
            public void run() {
                UmbraSportiva myApplication = (UmbraSportiva) getApplicationContext();
                Track myTrack = myApplication.getMyTrack();
                List<Location> savedLocations = myTrack.getMyLocations();

                PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.RED); //red color line & size=15
                Polyline routePolyline = null;

                LatLng lastpoint = null;

                for (int i = 0; i < savedLocations.size(); i++) {
                    LatLng latlng = new LatLng(savedLocations.get(i).getLatitude(), savedLocations.get(i).getLongitude());
                    rectLine.add(latlng);
                    lastpoint = latlng;
                }
                //clear the old line
                if (routePolyline != null) {
                    routePolyline.remove();
                }

                routePolyline = mMap.addPolyline(rectLine);

                LatLng startpoint = new LatLng(myTrack.getStartPoint().getLatitude(), myTrack.getStartPoint().getLongitude());

                mMap.addMarker(new MarkerOptions().position(startpoint).title("Marker in Sydney"));
                if (lastpoint != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastpoint, 16.0f));
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Inregistram activitatea?")
                .setMessage("Daca raspundeti nu, datele curente vor fi pierdute")
                .setPositiveButton("Yes", (dialog, which) -> {
                    USContext = getApplicationContext();
                    SQLiteHelper sqLiteHelper = new SQLiteHelper(USContext);

                    UmbraSportiva myApplication = (UmbraSportiva) getApplicationContext();
                    Track myTrack = myApplication.getMyTrack();
                    List<Location> savedLocations = myTrack.getMyLocations();

                    String[] activities = getResources().getStringArray(R.array.activity_names);
                    String[] act_codes = getResources().getStringArray(R.array.activity_code);

                    String activityName = null;

                    for (int i = 0; i < activities.length; i++) {
                        if (act_codes[i].equals(myTrack.getActivityCode())) {
                            activityName = activities[i];
                        }
                    }

                    SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
                    sqLiteHelper.addInfo(db, activityName,
                            myTrack.getActivityCode(),
                            myTrack.getStartEpoch(),
                            myTrack.getEndEpoch(),
                            myTrack.getEpochDuration(),
                            myTrack.getTotalDistance(),
                            0,
                            0,
                            0,
                            0,
                            0,
                            myTrack.jsonTrack()
                    );

                    super.onBackPressed();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> {super.onBackPressed();dialog.dismiss();
                })
                .show();

    }
}

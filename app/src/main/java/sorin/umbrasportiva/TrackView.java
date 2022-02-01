package sorin.umbrasportiva;

import static com.google.android.material.internal.ViewUtils.getContentView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;


public class TrackView extends FragmentActivity implements OnMapReadyCallback {

    private static Context USContext;
    TextView track_view_date;
    private GoogleMap mMap;
    private Track myTrack;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_view);

        setTitle("Vizualizarea activitatii");
        Intent intent = getIntent();
        String trackId = intent.getStringExtra("trackId");

        track_view_date = findViewById(R.id.track_view_date);
        track_view_date.setText("TrackId:" + trackId);

        USContext = getApplicationContext();
        SQLiteHelper sqLiteHelper = new SQLiteHelper(USContext);

        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

        Cursor cursor = sqLiteHelper.getInfo(db);

        myTrack = sqLiteHelper.getTrackById(db, Integer.valueOf(trackId));

        sqLiteHelper.close(db);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView_c);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        updateMap();
    }

    private void updateMap() {

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

//        LatLng startpoint = new LatLng(myTrack.getStartPoint().getLatitude(), myTrack.getStartPoint().getLongitude());

//        mMap.addMarker(new MarkerOptions().position(startpoint).title("Marker in Sydney"));
        if (lastpoint != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastpoint, 16.0f));
        }
    }

}
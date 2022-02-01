package sorin.umbrasportiva;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    ListView list_history;
    private static Context USContext;

    private int mId;
    private int mInputType = 0;
    private String mActivityType = "";
    private String mActivityCode = "";
    private String mStartTime = "";
    private String mEndTime = "";
    private int mDuration = 0;
    private double mDistance = 0.0;
    private double mAveragePace = 0.0;
    private double mAverageSpeed = 0.0;
    private int mCalories = 0;
    private double mClimb = 0.0;
    private int mHeartRate = 0;
    private String mComment = "";
    private int mPrivacy = 0;
    private int mSynced = 0;
    private String mGPS = "";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("Activitati inregistrate");

        updateUIValues();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("Range")
    private void updateUIValues() {
        USContext = getApplicationContext();
        SQLiteHelper sqLiteHelper = new SQLiteHelper(USContext);

        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        Cursor cursor = sqLiteHelper.getInfo(db);

        List<Track> histTracks = new ArrayList<>();

        while (cursor.moveToNext()) {

            // Get info from db
            mId = cursor.getInt(cursor.getColumnIndex(DBSchema.ActivityEntry.ID));
            mActivityType = cursor.getString(cursor.getColumnIndex(DBSchema.ActivityEntry.ACTIVITY_TYPE));
            mActivityCode = cursor.getString(cursor.getColumnIndex(DBSchema.ActivityEntry.ACTIVITY_CODE));
            mStartTime = cursor.getString(cursor.getColumnIndex(DBSchema.ActivityEntry.START_TIME));
            mEndTime = cursor.getString(cursor.getColumnIndex(DBSchema.ActivityEntry.END_TIME));
            mDuration = cursor.getInt(cursor.getColumnIndex(DBSchema.ActivityEntry.DURATION));
            mDistance = cursor.getDouble(cursor.getColumnIndex(DBSchema.ActivityEntry.DISTANCE));
            mAveragePace = cursor.getDouble(cursor.getColumnIndex(DBSchema.ActivityEntry.AVERAGE_PACE));
            mAverageSpeed = cursor.getDouble(cursor.getColumnIndex(DBSchema.ActivityEntry.AVERAGE_SPEED));
            mCalories = cursor.getInt(cursor.getColumnIndex(DBSchema.ActivityEntry.CALORIES));
            mClimb = cursor.getDouble(cursor.getColumnIndex(DBSchema.ActivityEntry.CLIMB));
            mSynced = cursor.getInt(cursor.getColumnIndex(DBSchema.ActivityEntry.SYNCED));
            mGPS = cursor.getString(cursor.getColumnIndex(DBSchema.ActivityEntry.GPS_DATA));

            histTracks.add(new Track(mActivityCode, Long.parseLong(mStartTime), Long.parseLong(mEndTime), mDistance, mDuration, mId));
        }

        sqLiteHelper.close(db);

        list_history = findViewById(R.id.list_history);
        list_history.setAdapter(new ArrayAdapter<Track>(this, android.R.layout.simple_list_item_1, histTracks));
        list_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(HistoryActivity.this, TrackView.class).putExtra("trackId", String.valueOf(histTracks.get(position).getDbid())));
            }
        });

    }

}
package sorin.umbrasportiva;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import sorin.umbrasportiva.DBSchema;

public class SQLiteHelper extends SQLiteOpenHelper {

    // db variables
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "exercise_history.db";
    private static final String CREATION_QUERY = "create table "
            + DBSchema.ActivityEntry.TABLE_NAME + "("
            + DBSchema.ActivityEntry.ID + " integer primary key autoincrement,"
            + DBSchema.ActivityEntry.ACTIVITY_TYPE + " text not null,"
            + DBSchema.ActivityEntry.ACTIVITY_CODE + " text not null,"
            + DBSchema.ActivityEntry.START_TIME + " integer not null,"
            + DBSchema.ActivityEntry.END_TIME + " integer not null,"
            + DBSchema.ActivityEntry.DURATION + " integer not null,"
            + DBSchema.ActivityEntry.DISTANCE + " float,"
            + DBSchema.ActivityEntry.AVERAGE_PACE + " float,"
            + DBSchema.ActivityEntry.AVERAGE_SPEED + " float,"
            + DBSchema.ActivityEntry.CALORIES + " integer,"
            + DBSchema.ActivityEntry.CLIMB + " float,"
            + DBSchema.ActivityEntry.SYNCED + " integer,"
            + DBSchema.ActivityEntry.GPS_DATA + " text" + ")";

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATION_QUERY);
    }

    public void addInfo(SQLiteDatabase db, String mActivityType, String mActivityCode, Long mStartTime, Long mEndTime,
                        Long mDuration, double mDistance, double mAvgPace, double mAvgSpeed, int mCalorie,
                        double mClimb, int mSynced, String mGPS) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBSchema.ActivityEntry.ACTIVITY_TYPE, mActivityType);
        contentValues.put(DBSchema.ActivityEntry.ACTIVITY_CODE, mActivityCode);
        contentValues.put(DBSchema.ActivityEntry.START_TIME, mStartTime);
        contentValues.put(DBSchema.ActivityEntry.END_TIME, mEndTime);
        contentValues.put(DBSchema.ActivityEntry.DURATION, mDuration);
        contentValues.put(DBSchema.ActivityEntry.DISTANCE, mDistance);
        contentValues.put(DBSchema.ActivityEntry.AVERAGE_PACE, mAvgPace);
        contentValues.put(DBSchema.ActivityEntry.AVERAGE_SPEED, mAvgSpeed);
        contentValues.put(DBSchema.ActivityEntry.CALORIES, mCalorie);
        contentValues.put(DBSchema.ActivityEntry.CLIMB, mClimb);
        contentValues.put(DBSchema.ActivityEntry.SYNCED, mSynced);
        contentValues.put(DBSchema.ActivityEntry.GPS_DATA, mGPS);
        db.insert(DBSchema.ActivityEntry.TABLE_NAME, null,contentValues);
    }

    public Cursor getInfo(SQLiteDatabase db) {

//        String[] columns = {DBSchema.ExerciseEntry.ID, DBSchema.ExerciseEntry.INPUT_TYPE, DBSchema.ExerciseEntry.ACTIVITY_TYPE,
//                DBSchema.ExerciseEntry.DATE_TIME, DBSchema.ExerciseEntry.DURATION, DBSchema.ExerciseEntry.DISTANCE,
//                DBSchema.ExerciseEntry.AVERAGE_PACE, DBSchema.ExerciseEntry.AVERAGE_SPEED, DBSchema.ExerciseEntry.CALORIES, DBSchema.ExerciseEntry.CLIMB,
//                DBSchema.ExerciseEntry.HEARTRATE, DBSchema.ExerciseEntry.COMMENT, DBSchema.ExerciseEntry.PRIVACY, DBSchema.ExerciseEntry.GPS_DATA};

        Cursor cursor = db.query(DBSchema.ActivityEntry.TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Track getTrackById(SQLiteDatabase db, Integer id) {
        Cursor cursor = db.query(DBSchema.ActivityEntry.TABLE_NAME, new String[] {DBSchema.ActivityEntry.ACTIVITY_TYPE, DBSchema.ActivityEntry.ACTIVITY_CODE,
                        DBSchema.ActivityEntry.START_TIME, DBSchema.ActivityEntry.END_TIME, DBSchema.ActivityEntry.DURATION,DBSchema.ActivityEntry.DISTANCE,
                        DBSchema.ActivityEntry.AVERAGE_PACE,DBSchema.ActivityEntry.AVERAGE_SPEED,DBSchema.ActivityEntry.CALORIES,
                        DBSchema.ActivityEntry.CLIMB, DBSchema.ActivityEntry.GPS_DATA,DBSchema.ActivityEntry.SYNCED }, DBSchema.ActivityEntry.ID + "=?",
                        new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Track track = new Track(cursor.getString(1), Long.parseLong(cursor.getString(2)), Long.parseLong(cursor.getString(3)), Double.parseDouble(cursor.getString(5)), Integer.parseInt(cursor.getString(4)));

        Gson g = new Gson();
        Type userListType = new TypeToken<ArrayList<Location>>(){}.getType();
        ArrayList<Location> locationArray = g.fromJson(cursor.getString(10), userListType);

        if (locationArray.size() > 0) {
            track.setMyLocations(locationArray);
        }
        return track;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }


    public synchronized void close (SQLiteDatabase db) {
        if (db != null) {
            db.close();
            super.close();
        }
    }

}

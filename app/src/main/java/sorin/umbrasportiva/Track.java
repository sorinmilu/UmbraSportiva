package sorin.umbrasportiva;

import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.ZonedDateTime;
import java.util.TimeZone;

public class Track {
    private List<Location> myLocations;
    private LocalDateTime trackStart;
    private LocalDateTime trackEnd;
    private Location startPoint;
    private String activityCode;
    private Integer totalDuration;
    private Integer dbid = 0;
    private Double totalDistance;
    private int evaluated;

    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    public LocalDateTime getTrackStart() {
        return this.trackStart;
    }

    public LocalDateTime getTrackEnd() {
        return this.trackEnd;
    }

    public void setTrackStart(LocalDateTime trackStart) {
        this.trackStart = trackStart;
    }

    public void setTrackEnd(LocalDateTime trackEnd) {
        this.trackEnd = trackEnd;
    }

    public void setdbid(Integer dbid) {
        this.dbid = dbid;
    }

    public Integer getDbid() {return this.dbid;}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateTrackEnd() {
        this.trackEnd = LocalDateTime.now();
    }

    public Location getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Location startPoint) {
        this.startPoint = startPoint;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public void setEvaluated(int evaluated) {
        this.evaluated = evaluated;
    }

    public int getEvaluated() {
        return this.evaluated;
    }

    public void incrementEvaluated() {
        this.evaluated++;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void restartTrack() {
        this.trackStart = LocalDateTime.now();
        this.trackEnd = LocalDateTime.now();
        this.totalDistance = 0.0;
        this.myLocations = new ArrayList<>();
        this.evaluated = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Track() {
        this.activityCode = "nk";
        this.trackStart = LocalDateTime.now();
        this.trackEnd = LocalDateTime.now();
//        this.startPoint = firstLocation;
        this.totalDistance = 0.0;
        this.myLocations = new ArrayList<>();
        this.evaluated = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Track(String activityCode, long trackStart, long trackEnd, Double totalDistance, Integer totalDuration) {
        this.activityCode = activityCode;
        this.trackStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(trackStart), ZoneId.systemDefault());
        this.trackEnd = LocalDateTime.ofInstant(Instant.ofEpochMilli(trackEnd), ZoneId.systemDefault());
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Track(String activityCode, long trackStart, long trackEnd, Double totalDistance, Integer totalDuration, Integer dbid) {
        this.activityCode = activityCode;
        this.trackStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(trackStart), ZoneId.systemDefault());
        this.trackEnd = LocalDateTime.ofInstant(Instant.ofEpochMilli(trackEnd), ZoneId.systemDefault());
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.dbid = dbid;
    }

    public void updateTotalDistance(Double distance) {
        this.totalDistance += distance;
    }

    public String getFormattedDistance() {
        if (this.totalDistance > 3000) {
            return String.format("%,.2f",this.totalDistance/1000) + " km";
        } else {
            return String.format("%,.2f",this.totalDistance) + " m";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getStartTimeStamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return this.getTrackStart().format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long getStartEpoch() {
        return this.getTrackStart().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long getEndEpoch() {
        return this.getTrackEnd().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long getEpochDuration() {
        return this.getEndEpoch() - this.getStartEpoch();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getHumanDuration() {
        //durata e in secunde
        int realEpoch =  (int) this.getEpochDuration() / 1000;
        int hours =  realEpoch / 3600;
        int minutes =  (realEpoch % 3600) / 60;
        int seconds =  realEpoch % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String jsonTrack() {
        return new Gson().toJson(this.getMyLocations() );
    }

//    public Integer getDurationSeconds() {
//        List<Location> savedLocations = this.getMyLocations();
//        LatLng lastpoint = null;
//        for (int i = 0; i < savedLocations.size(); i++) {
//            LatLng latlng = new LatLng(savedLocations.get(i).getLatitude(), savedLocations.get(i).getLongitude());
//            lastpoint = latlng;
//        }
//    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String toString() {
        return  "Start: " + trackStart +
                "\n" +
                "activitate: '" + activityCode + '\'' +
                "durata:" + this.getHumanDuration() +
                "\n" +
                "distanta: " + this.getFormattedDistance();
    }
}

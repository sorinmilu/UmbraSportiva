package sorin.umbrasportiva;

public final class DBSchema {
    public static abstract class ActivityEntry {

        // db variables
        public static final String TABLE_NAME = "activity";
        public static final String ID = "id";
        public static final String ACTIVITY_TYPE = "activity_type";
        public static final String ACTIVITY_CODE = "activity_code";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String DURATION = "duration";
        public static final String DISTANCE = "distance";
        public static final String AVERAGE_PACE = "average_pace";
        public static final String AVERAGE_SPEED = "average_speed";
        public static final String CALORIES = "cals";
        public static final String CLIMB = "climb";
        public static final String GPS_DATA = "gps_data";
        public static final String SYNCED = "synced";

    }


}

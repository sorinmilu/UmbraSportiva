package sorin.umbrasportiva;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class UmbraSportiva extends Application {
    private static UmbraSportiva singleton;

    private Track myTrack;

    public UmbraSportiva getInstance() {
        return singleton;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCreate() {
        super.onCreate();
        singleton = this;
        myTrack = new Track();
    }

    public Track getMyTrack() {
        return this.myTrack;
    }

}

package sorin.umbrasportiva;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import sorin.umbrasportiva.RecyclerItemClickListener;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        recyclerView=findViewById(R.id.idCourseRV);
        setTitle("Alegeti o activitate");
        // created new array list..
        recyclerDataArrayList=new ArrayList<>();

        String[] activities = getResources().getStringArray(R.array.activity_names);
        String[] act_codes = getResources().getStringArray(R.array.activity_code);

        // added data to array list
        for (int i = 0; i < activities.length; i++) {
            recyclerDataArrayList.add(new RecyclerData(activities[i], getResources().getIdentifier(act_codes[i], "drawable", getPackageName())));
        }

        RecyclerView recyclerView = findViewById(R.id.idCourseRV);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemClick(View view, int position) {
                        String[] act_codes = getResources().getStringArray(R.array.activity_code);
                        UmbraSportiva myApplication = (UmbraSportiva) getApplicationContext();
                        Track myTrack = myApplication.getMyTrack();
                        myTrack.restartTrack();
                        myTrack.setActivityCode(act_codes[position]);
                        startActivity(new Intent(StartActivity.this, ExecutingActivity.class));


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                        String[] act_codes = getResources().getStringArray(R.array.activity_code);
                    }
                })
        );

        // added data from arraylist to adapter class.
        RecyclerViewAdapter adapter=new RecyclerViewAdapter(recyclerDataArrayList,this);

        // setting grid layout manager to implement grid view.
        // in this method '2' represents number of columns to be displayed in grid view.
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);

        // at last set adapter to recycler view.
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
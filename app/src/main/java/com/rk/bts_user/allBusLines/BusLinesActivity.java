package com.rk.bts_user.allBusLines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rk.bts_user.model.BusLine;
import com.rk.bts_user.R;

import java.util.ArrayList;
import java.util.List;

// All bus lines.. fetched form database

public class BusLinesActivity extends AppCompatActivity implements BusLinesListFragment.BusLineSelected {

    TextView tvBusLineSchedule;
    TextView tvLabelBusLine;
    Button btnAddToFavorites;

    static final String MY_BUS_LINES = "com.rk.bts_user.MyBusLines";

    FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dbRefBusLine = fbDatabase.getReference("BusLine");

    BusLine selectedLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_lines);

        tvBusLineSchedule = findViewById(R.id.tvBusLineSchedule);
        tvLabelBusLine = findViewById(R.id.tvLabelBusLine);
        btnAddToFavorites = findViewById(R.id.btnAddToFavorites);

        btnAddToFavorites.setVisibility(View.INVISIBLE);
        tvLabelBusLine.setText("No bus line selected");

        ArrayList<String> busSchedules;

        dbRefBusLine.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> busLines = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()){
                    String listEntry = ds.getValue(BusLine.class).getName();
                    busLines.add(listEntry);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Phone is in portrait mode
        if(findViewById(R.id.layout_portrait_bus_lines) != null){
            FragmentManager manager = this.getSupportFragmentManager();

            manager.beginTransaction()
                    .hide(manager.findFragmentById(R.id.fragBusLineSchedule))
                    .show(manager.findFragmentById(R.id.fragBusLineList))
                    .commit();
        }

        //Phone is in landscape mode
        if(findViewById(R.id.layout_land_bus_lines) != null){
            FragmentManager manager = this.getSupportFragmentManager();

            manager.beginTransaction()
                    .show(manager.findFragmentById(R.id.fragBusLineSchedule))
                    .show(manager.findFragmentById(R.id.fragBusLineList))
                    .commit();
        }

        //Handle 'add to favorites' button
        btnAddToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedLine != null){

                    SharedPreferences.Editor editor = getSharedPreferences(MY_BUS_LINES, MODE_PRIVATE).edit();

                    String key = selectedLine.getName();

                    editor.putString(key, makeNiceText(selectedLine));
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "Persisting to file.", Toast.LENGTH_LONG).show();

                }
                else{

                    Toast.makeText(getApplicationContext(), "Select a line!", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    @Override
    public void onBusLineSelected(BusLine busLine) {

        btnAddToFavorites.setVisibility(View.VISIBLE);

        selectedLine = busLine;

        tvLabelBusLine.setText(busLine.getName());
        tvBusLineSchedule.setText(Html.fromHtml(makeNiceText(busLine)));
        tvBusLineSchedule.setMovementMethod(new ScrollingMovementMethod());
        tvBusLineSchedule.setScrollbarFadingEnabled(false);

        //Phone is in portrait mode, user has clicked on list item!
        if(findViewById(R.id.layout_portrait_bus_lines) != null){
            FragmentManager manager = this.getSupportFragmentManager();

            manager.beginTransaction()
                    .show(manager.findFragmentById(R.id.fragBusLineSchedule))
                    .hide(manager.findFragmentById(R.id.fragBusLineList))
                    .commit();
        }

    }


    private String makeNiceText(BusLine busLine){
        StringBuilder sb = new StringBuilder();
        sb.append("<b>");
        sb.append(getText(R.string.work_days));
        sb.append(":</b>");
        sb.append("<br><br>");

        String[] tokens = busLine.getScheduleWorkDays().split("\n");

        for(String s: tokens){
            sb.append("<b><i>");
            sb.append(s.substring(0, 2));
            sb.append(" </i></b>");
            sb.append(s.substring(2, s.length()));
            sb.append("<br>");
        }

        sb.append("<br><b>");
        sb.append(getText(R.string.saturday));
        sb.append(":</b>");
        sb.append("<br><br>");

        String[] tokensSat = busLine.getScheduleSaturday().split("\n");

        for(String s: tokensSat){
            sb.append("<b><i>");
            sb.append(s.substring(0, 2));
            sb.append(" </i></b>");
            sb.append(s.substring(2, s.length()));
            sb.append("<br>");
        }

        sb.append("<br><b>");
        sb.append(getText(R.string.sunday));
        sb.append(":</b>");
        sb.append("<br><br>");

        String[] tokensSun = busLine.getScheduleSunday().split("\n");

        for(String s: tokensSun){
            sb.append("<b><i>");
            sb.append(s.substring(0, 2));
            sb.append(" </i></b>");
            sb.append(s.substring(2, s.length()));
            sb.append("<br>");
        }

        return sb.toString();
    }
}
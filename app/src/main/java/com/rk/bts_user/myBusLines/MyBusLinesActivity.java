package com.rk.bts_user.myBusLines;

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

import com.rk.bts_user.R;

public class MyBusLinesActivity extends AppCompatActivity implements MyBusLinesListFragment.MyBusLineSelected {

    static final String MY_BUS_LINES = "com.rk.bts_user.MyBusLines";

    TextView tvBusLineName;
    TextView tvBusSchedule;
    Button btnRemoveFromFavorites;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bus_lines);

        tvBusLineName = findViewById(R.id.tvLabelMyBusLine);
        tvBusSchedule = findViewById(R.id.tvMyBusLineSchedule);
        btnRemoveFromFavorites = findViewById(R.id.btnRemoveFromFavorites);

        btnRemoveFromFavorites.setVisibility(View.INVISIBLE);

        tvBusLineName.setText("No bus line selected");

        btnRemoveFromFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tvBusLineName.getText().toString().isEmpty()){
                    SharedPreferences.Editor editor = getSharedPreferences(MY_BUS_LINES, MODE_PRIVATE).edit();
                    editor.remove(tvBusLineName.getText().toString());
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "Removed", Toast.LENGTH_LONG).show();

                    finish();
                    startActivity(getIntent());
                }
                else{
                    Toast.makeText(getApplicationContext(), "Select a line!", Toast.LENGTH_LONG).show();
                }
            }
        });


        //Phone is in portrait mode
        if(findViewById(R.id.layout_portrait_my_bus_lines) != null){
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .show(manager.findFragmentById(R.id.fragMyBusLineList))
                    .hide(manager.findFragmentById(R.id.fragMyBusLineSchedule))
                    .commit();
        }

        //Phone is in landscape mode
        if(findViewById(R.id.layout_land_my_bus_lines) != null){
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .show(manager.findFragmentById(R.id.fragMyBusLineList))
                    .show(manager.findFragmentById(R.id.fragMyBusLineSchedule))
                    .commit();
        }

    }

    @Override
    public void onMyBusLineSelected(String busLineName, String busLineSchedule) {

        btnRemoveFromFavorites.setVisibility(View.VISIBLE);

        tvBusLineName.setText(busLineName);
        tvBusSchedule.setText(Html.fromHtml(busLineSchedule));
        tvBusSchedule.setMovementMethod(new ScrollingMovementMethod());
        tvBusSchedule.setScrollbarFadingEnabled(false);

        //Phone is in portrait mode, user has clicked on list item!
        if(findViewById(R.id.layout_portrait_my_bus_lines) != null){
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .hide(manager.findFragmentById(R.id.fragMyBusLineList))
                    .show(manager.findFragmentById(R.id.fragMyBusLineSchedule))
                    .commit();
        }

    }
}
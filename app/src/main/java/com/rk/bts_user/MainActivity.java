package com.rk.bts_user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rk.bts_user.allBusLines.BusLinesActivity;
import com.rk.bts_user.model.BusLine;
import com.rk.bts_user.myBusLines.MyBusLinesActivity;

public class MainActivity extends AppCompatActivity {

    Button btnMyLines, btnAllLines, btnBusLocation, btnPhotos;

    FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dbRefBusLine = fbDatabase.getReference("BusLine");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMyLines = findViewById(R.id.btnMyLines);
        btnAllLines = findViewById(R.id.btnAllLines);
        btnBusLocation = findViewById(R.id.btnBusLocation);

        btnMyLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MyBusLinesActivity.class);
                startActivity(intent);

            }
        });

        btnAllLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusLinesActivity.class);
                startActivity(intent);
            }
        });

        btnBusLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusLocationActivity.class);
                startActivity(intent);
            }
        });

        btnPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BusLine bl = new BusLine();
                bl.setName("4 LIMAN IV - Z.STANICA");
                bl.setDirection("A");
                bl.setScheduleWorkDays("0430\n" +
                        "0500 18 36 54\n" +
                        "0612 30 48\n" +
                        "0706 21 35 49\n" +
                        "0803 17 31 45 59\n" +
                        "0913 27 41 55\n" +
                        "1009 23 37 51\n" +
                        "1105 19 33 47\n" +
                        "1201 15 29 43 57\n" +
                        "1315 33 51\n" +
                        "1409 27 45\n" +
                        "1503 21 36 50\n" +
                        "1604 18 32 46\n" +
                        "1700 14 28 42 56\n" +
                        "1810 24 38 53\n" +
                        "1911 29 47\n" +
                        "2005 23 41 59\n" +
                        "2117 35 53\n" +
                        "2211 29 47\n" +
                        "2305 23 41\n" +
                        "0000");
                bl.setScheduleSaturday("0430\n" +
                        "0500 27 45\n" +
                        "0603 21 39 55\n" +
                        "0709 23 37 51\n" +
                        "0805 19 33 47\n" +
                        "0901 15 29 43 57\n" +
                        "1011 25 39 53\n" +
                        "1107 21 35 49\n" +
                        "1203 17 31 45 59\n" +
                        "1313 27 41 55\n" +
                        "1409 23 37 51\n" +
                        "1505 19 33 47\n" +
                        "1601 15 29 43 57\n" +
                        "1711 25 39 53\n" +
                        "1807 23 41 49\n" +
                        "1917 35 53\n" +
                        "2011 29 47\n" +
                        "2105 23 41\n" +
                        "2200 20 40\n" +
                        "2300 30\n" +
                        "0000");
                bl.setScheduleSunday("0430\n" +
                        "0500 27 45\n" +
                        "0603 21 39 55\n" +
                        "0709 23 37 51\n" +
                        "0805 19 33 47\n" +
                        "0901 15 29 43 57\n" +
                        "1011 25 39 53\n" +
                        "1107 21 35 49\n" +
                        "1203 17 31 45 59\n" +
                        "1313 27 41 55\n" +
                        "1409 23 37 51\n" +
                        "1505 19 33 47\n" +
                        "1601 15 29 43 57\n" +
                        "1711 25 39 53\n" +
                        "1807 23 41 49\n" +
                        "1917 35 53\n" +
                        "2011 29 47\n" +
                        "2105 23 41\n" +
                        "2200 20 40\n" +
                        "2300 30\n" +
                        "0000");

                dbRefBusLine.child("4A").setValue(bl);
            }
        });
    }
}
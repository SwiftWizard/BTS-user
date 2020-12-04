package com.rk.bts_user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rk.bts_user.model.Bus;
import com.rk.bts_user.model.BusLine;

import java.util.ArrayList;
import java.util.List;

public class BusLocationActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = "BusLocationActivity";

    static final int ERROR_DIALOG = 9901;
    static final String MAP_VIEW_BUNDLE = "";
    private final int UPDATE_INTERVAL = 1000;

    private GoogleMap map;
    List<Marker> markers = new ArrayList<>();

    Handler handler = new Handler();
    Runnable runnable;

    MapView mapView;
    Spinner spinnerCl;

    FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dbRefBusLine = fbDatabase.getReference("BusLine");
    DatabaseReference dbRefBus = fbDatabase.getReference("Bus");

    List<BusLine> busLinesList = new ArrayList<>();
    BusLine selectedLine;
    List<Bus> busesOnSelectedLine = new ArrayList<>();



    private void startLocationsRunnable(){
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                updateBusLocations();
                updateMarkers();
                handler.postDelayed(runnable, UPDATE_INTERVAL); //Recursive call every UPDATE_INTERVAL
            }
        }, UPDATE_INTERVAL);
    }

    private void stopLocationUpdates(){
        handler.removeCallbacks(runnable);
    }

    private void updateBusLocations(){

        //Clear map
        map.clear();
        clearMarkers();

        dbRefBus.addValueEventListener(new ValueEventListener() {
            //Get buses form selected line
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Bus tmp = ds.getValue(Bus.class);
                    if (tmp.isActive() && selectedLine.getName().equals(tmp.getLine())) {
                        busesOnSelectedLine.set(i++, tmp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getBusLocations(){
        dbRefBus.addListenerForSingleValueEvent(new ValueEventListener() {
            //Get buses form selected line
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Bus tmp = ds.getValue(Bus.class);

                    if (tmp.isActive() && selectedLine.getName().equals(tmp.getLine())) {
                        busesOnSelectedLine.add(tmp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateMarkers(){
        if(map != null){
            map.clear();
            clearMarkers();
            if(busesOnSelectedLine != null && !busesOnSelectedLine.isEmpty()){
                for(Bus b : busesOnSelectedLine){
                    MarkerOptions markerOptions = new MarkerOptions().position((new LatLng(Double.parseDouble(b.getLat()), Double.parseDouble(b.getLon()))));
                    markerOptions.title(b.getRegistrationPlate());

                    markers.add(map.addMarker(markerOptions));
                }
            }
        }
    }

    private void clearMarkers(){
        for(Marker m: markers){
            m.remove();
        }
        markers.clear();
    }

    private void setCameraView() {

        // Set a boundary to start
        //Position of Novi Sad 45.267136, 19.833549.

        double bottomBoundary = 45.267136 - .1;
        double leftBoundary = 19.833549- .1;
        double topBoundary = 45.267136 + .1;
        double rightBoundary = 19.833549 + .1;

        LatLngBounds mapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBoundary, 0));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_location);

        spinnerCl = findViewById(R.id.spinnerMapView);

        dbRefBusLine.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> busLinesStringList = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String listEntry = ds.getValue(BusLine.class).getName();
                    busLinesList.add(ds.getValue(BusLine.class));
                    busLinesStringList.add(listEntry);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(BusLocationActivity.this, android.R.layout.simple_spinner_item, busLinesStringList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCl.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE);
        }

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);


        //Get all buses from selected line
        spinnerCl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Set selected line
                selectedLine = busLinesList.get(i);

                //Clear buses on selected line
                busesOnSelectedLine.clear();

                //Remove markers
                clearMarkers();
                map.clear();

                getBusLocations();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE);
        if(mapViewBundle == null){
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mapView.onResume();

        checkGoogleServices();

        startLocationsRunnable();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        clearMarkers();
        setCameraView();
    }

    private boolean checkGoogleServices(){

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());

        if(available == ConnectionResult.SUCCESS){
            //All ok
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //Bummer something is wrong but we can fix it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG);
            dialog.show();
        }
        else{
            //Super bummer something is really wrong
            Toast.makeText(this, "You cant make map requests :(", Toast.LENGTH_LONG).show();
        }

        return false;
    }
}
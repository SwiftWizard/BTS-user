package com.rk.bts_user.allBusLines;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rk.bts_user.model.BusLine;

import java.util.ArrayList;
import java.util.List;


public class BusLinesListFragment extends ListFragment {

    FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dbRefBusLine = fbDatabase.getReference("BusLine");

    List<BusLine> busLines = new ArrayList<>();

    BusLineSelected activity;

    public BusLinesListFragment() {
        // Required empty public constructor
    }

    public interface BusLineSelected{
        void onBusLineSelected(BusLine busLine);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        activity = (BusLineSelected) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbRefBusLine.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> busLinesNames = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()){

                    busLines.add(ds.getValue(BusLine.class));

                    String listEntry = ds.getValue(BusLine.class).getName();
                    busLinesNames.add(listEntry);
                }

                setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, busLinesNames));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Check internet connection", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {

        activity.onBusLineSelected(busLines.get(position));

    }
}
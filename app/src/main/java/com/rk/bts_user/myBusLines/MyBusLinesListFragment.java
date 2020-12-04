package com.rk.bts_user.myBusLines;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rk.bts_user.R;
import com.rk.bts_user.allBusLines.BusLinesListFragment;
import com.rk.bts_user.model.BusLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MyBusLinesListFragment extends ListFragment {

    static final String MY_BUS_LINES = "com.rk.bts_user.MyBusLines";

    SharedPreferences preferences;

    List<String> busLineNames = new ArrayList<>();
    List<String> busLineSchedules = new ArrayList<>();

    public MyBusLinesListFragment() {
        // Required empty public constructor
    }


    MyBusLineSelected activity;


    public interface MyBusLineSelected {
        void onMyBusLineSelected(String busLineName, String busLineSchedule);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        activity = (MyBusLineSelected) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preferences = this.getActivity().getSharedPreferences(MY_BUS_LINES, Context.MODE_PRIVATE);

        Map<String, ?> entries = preferences.getAll();

        for(Map.Entry<String, ?> entry : entries.entrySet()){
            busLineNames.add(entry.getKey());
            busLineSchedules.add((String) entry.getValue());
        }

        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, busLineNames));

    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {

        activity.onMyBusLineSelected(busLineNames.get(position), busLineSchedules.get(position));

    }
}
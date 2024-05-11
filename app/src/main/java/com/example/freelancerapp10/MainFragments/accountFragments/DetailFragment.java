package com.example.freelancerapp10.MainFragments.accountFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.freelancerapp10.R;
import com.example.freelancerapp10.adapters.ProfileRecyclerViewAdapter;

import java.util.ArrayList;


public class DetailFragment extends Fragment {

    View rootView;
    RecyclerView listRecyclerView;
    ArrayList<String> dataModels;
    private ProfileRecyclerViewAdapter adapterList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_applied, container, false);
        //initView();
        listRecyclerView = rootView.findViewById(R.id.recyclerForAccount);
        dataModels = new ArrayList<>();

        dataModels.add("Posted");
        dataModels.add("Applied");
        dataModels.add("Contracts");
        dataModels.add("Order requests");

        listRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterList = new ProfileRecyclerViewAdapter(dataModels, getContext());
        listRecyclerView.setAdapter(adapterList);
        return rootView;

        //wow
    }

}

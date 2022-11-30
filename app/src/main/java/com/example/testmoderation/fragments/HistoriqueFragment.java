package com.example.testmoderation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.testmoderation.R;
import com.example.testmoderation.adapters.CustomAdapter;
import com.example.testmoderation.datamodels.PropositionModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


public class HistoriqueFragment extends Fragment {

    ArrayList<PropositionModel> propositionModels;
    ListView listView;
    private static CustomAdapter adapter;
    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public HistoriqueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historique, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.list);

        propositionModels = getPropositionsListFromLocal();

        Collections.sort(propositionModels,(o1, o2) -> {
            try {
                return parser.parse(o1.getDate()).compareTo(parser.parse(o2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
                return -1;
            }

        });
        Collections.reverse(propositionModels);
        adapter = new CustomAdapter(propositionModels, getActivity().getApplicationContext());

        listView.setAdapter(adapter);

    }

    public ArrayList<PropositionModel> getPropositionsListFromLocal()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = prefs.getString("list_propositions", null);
        Type type = new TypeToken<ArrayList<PropositionModel>>() {}.getType();
        return json == null ? new ArrayList<PropositionModel>() : gson.fromJson(json, type) ;

    }
}
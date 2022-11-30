package com.example.testmoderation.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.testmoderation.R;
import com.example.testmoderation.datamodels.PropositionModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    String myUrl = "https://moderation.logora.fr/predict";

    EditText text_to_send;
    TextView result;
    Button btn_send;
    Button btn_historique;
    ProgressDialog progressDialog;
    PropositionModel propositionModelNow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        text_to_send = view.findViewById(R.id.text_predict);
        btn_send = view.findViewById(R.id.predict_text);
        result = view.findViewById(R.id.textView);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment.MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute();
            }
        });

        btn_historique = view.findViewById(R.id.btn_hitorique);
        btn_historique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_historiqueFragment);
            }
        });
    }


    public class MyAsyncTasks extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("processing results");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String api_result = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    propositionModelNow = new PropositionModel(text_to_send.getText().toString());
                    StringBuilder urlBuilder = new StringBuilder(myUrl);
                    urlBuilder.append("?text=");
                    urlBuilder.append(URLEncoder.encode(text_to_send.getText().toString(), "UTF-8"));

                    url = new URL(urlBuilder.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                    urlConnection.setRequestProperty("Accept", "application/json");


                    urlConnection.connect();

                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);

                    int data = isw.read();

                    while (data != -1) {
                        api_result += (char) data;
                        data = isw.read();
                    }

                    return api_result;


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return api_result;
        }

        @Override
        protected void onPostExecute(String s) {
            // dismiss the progress dialog after receiving data from API
            progressDialog.dismiss();
            try {

                JSONObject result_api = new JSONObject(s);

                Double score = result_api.getJSONObject("prediction").getDouble("0");

                if (score < 0.5) {
                    result.setText("Acceptée");
                    result.setTextColor(getResources().getColor(R.color.green));
                    result.setVisibility(View.VISIBLE);
                    result.postDelayed(new Runnable() {
                        public void run() {
                            result.setVisibility(View.INVISIBLE);
                        }
                    }, 3000);

                    propositionModelNow.setResult("Acceptée");
                    propositionModelNow.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
                    propositionModelNow.setScore(String.valueOf(score));

                    ArrayList<PropositionModel> propositions = getPropositionsListFromLocal();
                    propositions.add(propositionModelNow);
                    SavePropositionsListToLocal(propositions);

                } else {
                    result.setText("Rejetée");
                    result.setTextColor(getResources().getColor(R.color.red));
                    result.setVisibility(View.VISIBLE);
                    result.postDelayed(new Runnable() {
                        public void run() {
                            result.setVisibility(View.INVISIBLE);
                        }
                    }, 3000);
                    propositionModelNow.setResult("Rejetée");
                    propositionModelNow.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
                    propositionModelNow.setScore(String.valueOf(score));

                    ArrayList<PropositionModel> propositions = getPropositionsListFromLocal();
                    propositions.add(propositionModelNow);
                    SavePropositionsListToLocal(propositions);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public ArrayList<PropositionModel> getPropositionsListFromLocal() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = prefs.getString("list_propositions", null);
        Type type = new TypeToken<ArrayList<PropositionModel>>() {
        }.getType();
        return json == null ? new ArrayList<PropositionModel>() : gson.fromJson(json, type);
    }

    public void SavePropositionsListToLocal(ArrayList<PropositionModel> propositionsList) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(propositionsList);
        editor.putString("list_propositions", json);
        editor.apply();
    }
}

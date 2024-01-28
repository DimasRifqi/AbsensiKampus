package com.example.absensi;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class history_absen extends AppCompatActivity {

    private static final String TAG = history_absen.class.getSimpleName();
    private static final String URL = "http://192.168.1.7/Koneksi_Mobile/HistoryAbsen.php";

    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private ArrayList<DataAbsensi> dataList;
    private TextView tvNotFound;
    private RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_absen);

        Toolbar toolbar = findViewById(R.id.toolbarxyz);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(history_absen.this, Home.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.rvHistory);
        tvNotFound = findViewById(R.id.tvNotFound);

        dataList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        recyclerAdapter = new RecyclerAdapter(dataList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchData();
    }

    private void fetchData() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Response: " + response.toString());
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching data : " + error.getMessage(), error);
                        handleErrorResponse(error);
                    }
                }
        );

        // Set a longer timeout
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);
    }

    private void parseData(JSONArray jsonArray) {
        try {
            // Assuming the array is directly returned without being wrapped in an object
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject data = jsonArray.getJSONObject(i);
                DataAbsensi dataAbsensi = new DataAbsensi(
                        data.getString("id_absensi"),
                        data.getString("nama"),
                        data.getString("npm"),
                        data.getString("matakuliah"),
                        data.getString("tanggal"),
                        data.getString("lokasi"),
                        data.getString("keterangan")
                );

                dataList.add(dataAbsensi);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage(), e);
        }

        displayData();
    }



    private void handleErrorResponse(VolleyError error) {
        if (error.networkResponse != null) {
            Log.e(TAG, "Error Response Code: " + error.networkResponse.statusCode);
        }

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(history_absen.this, "Timeout or No Connection", Toast.LENGTH_SHORT).show();
        } else if (error instanceof AuthFailureError) {
            // Handle AuthFailureError
        } else if (error instanceof ServerError || error instanceof NetworkError) {
            // Handle ServerError or NetworkError
        } else {
            Toast.makeText(history_absen.this, "handle Error fetching data", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayData() {
        if (dataList.isEmpty()) {
            tvNotFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNotFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            recyclerAdapter.notifyDataSetChanged();
        }
    }
}

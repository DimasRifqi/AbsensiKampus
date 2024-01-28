package com.example.absensi;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.widget.Toolbar;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



import com.google.android.material.appbar.AppBarLayout;

public class Absensi extends AppCompatActivity {
    EditText etNama, etNPM, inputLokasi, inputTanggal;

    Calendar myCalendar;
    LocationManager locationManager;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi);

        Toolbar toolbar = findViewById(R.id.back_toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Absensi.this, Home.class);
                startActivity(intent);
            }
        });

        // Inisialisasi ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending data...");
        progressDialog.setCancelable(false);

        // MatkulOpsi
        Spinner spinnerOptions = findViewById(R.id.matkulOpsi);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.daftar_matkul, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapter);

        // KeteranganOpsi
        Spinner spinnerOptions2 = findViewById(R.id.KeteranganOpsi);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.daftar_keterangan, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions2.setAdapter(adapter2);

        // Tanggal
        inputTanggal = findViewById(R.id.inputTanggal);
        myCalendar = Calendar.getInstance();
        inputTanggal.setOnClickListener(v -> showDatePickerDialog());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        inputTanggal.setText(sdf.format(myCalendar.getTime()));

        // Lokasi
        inputLokasi = findViewById(R.id.inputLokasi);

        // Inisialisasi LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Button send = findViewById(R.id.btnAbsen);
        etNama = findViewById(R.id.inputNama);
        etNPM = findViewById(R.id.inputNPM);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sNama = etNama.getText().toString();
                String sNpm = etNPM.getText().toString();
                String sSelectedMatkul = spinnerOptions.getSelectedItem().toString();
                String sTanggal = inputTanggal.getText().toString();
                String sLokasi = inputLokasi.getText().toString();
                String sSelectedKeterangan = spinnerOptions2.getSelectedItem().toString();

                if (!sNama.equals("") && !sNpm.equals("")) {
                    CreateDataToServer(sNama, sNpm, sSelectedMatkul, sTanggal, sLokasi, sSelectedKeterangan);
                    Intent loginIntent = new Intent(Absensi.this, history_absen.class);
                    startActivity(loginIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Gagal! Mohon isi semua data!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cek izin lokasi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Mendapatkan lokasi saat ini jika izin telah diberikan
            getLocation();
        } else {
            // Request izin lokasi jika belum diberikan
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    public void CreateDataToServer(final String nama, final String npm, final String matkul, final String tanggal, final String lokasi, final String keterangan) {
        if (checkNetworkConnection()) {
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DB_Absensi.SERVER_ABSENSI_URL,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String resp = jsonObject.getString("server_response");
                                if (resp.equals("[{\"status\":\"OK\"}]")) {
                                    Toast.makeText(getApplicationContext(), "Absensi Berhasil", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error response
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("nama", nama);
                    params.put("npm", npm);
                    params.put("matakuliah", matkul);
                    params.put("tanggal", tanggal);
                    params.put("lokasi", lokasi);
                    params.put("keterangan", keterangan);
                    return params;
                }
            };

            VolleyConnection.getInstance(Absensi.this).addToRequestQueue(stringRequest);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                }
            }, 2000);
        } else {
            Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


//    }

    //tanggal
    private void showDatePickerDialog() {
        // Atur tanggal awal DatePickerDialog sesuai dengan kalender saat ini
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        // Tampilkan DatePickerDialog
        datePickerDialog.show();
    }

    // Listener untuk menangkap tanggal yang dipilih oleh pengguna
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Perbarui kalender dengan tanggal yang dipilih
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Atur format tanggal yang diinginkan pada EditText
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            inputTanggal.setText(sdf.format(myCalendar.getTime()));
        }
    };

    //lokasi
    private void getLocation() {
        // Mendapatkan lokasi dari GPS atau network provider
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Jika lokasi dari GPS tidak tersedia, menggunakan network provider
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            // Menangani pembaruan lokasi
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location loc) {
                    // Update EditText dengan informasi lokasi yang lebih spesifik
                    getAddressFromLocation(loc.getLatitude(), loc.getLongitude());

                    // Hentikan pembaruan lokasi setelah ditemukan
                    locationManager.removeUpdates(this);
                }


            };

            // Minta pembaruan lokasi
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String locationAddress = address.getAddressLine(0); // Informasi lokasi jalan
                String kecamatan = address.getSubLocality(); // Informasi kecamatan
                String kota = address.getLocality(); // Informasi kota

                // Update EditText dengan informasi yang lebih spesifik
                inputLokasi.setText(locationAddress + ", " + kecamatan + ", " + kota);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
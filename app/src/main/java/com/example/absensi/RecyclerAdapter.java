package com.example.absensi;// RecyclerAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.absensi.DataAbsensi;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<DataAbsensi> dataList;

    public RecyclerAdapter(ArrayList<DataAbsensi> dataList) {
        this.dataList = dataList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_absen, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        DataAbsensi dataAbsensi = dataList.get(position);

        // Set the data to the views in your item layout
        holder.tvid.setText(dataAbsensi.getDid());
        holder.tvNama.setText(dataAbsensi.getDnama());
        holder.tvNPM.setText(dataAbsensi.getDnpm());
        holder.tvMatakuliah.setText(dataAbsensi.getDmatakuliah());
        holder.tvTanggal.setText(dataAbsensi.getDtanggal());
        holder.tvLokasi.setText(dataAbsensi.getDlokasi());
        holder.tvKeterangan.setText(dataAbsensi.getDketerangan());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvid, tvNama, tvNPM, tvMatakuliah, tvTanggal, tvLokasi, tvKeterangan;

        public ViewHolder( View itemView) {
            super(itemView);

            // Initialize your TextViews
            tvid = itemView.findViewById(R.id.tvNomor);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvNPM = itemView.findViewById(R.id.tvNPM);
            tvMatakuliah = itemView.findViewById(R.id.tvMatkul);
            tvTanggal = itemView.findViewById(R.id.tvAbsenTime);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
            tvKeterangan = itemView.findViewById(R.id.tvStatusAbsen);
        }
    }
}

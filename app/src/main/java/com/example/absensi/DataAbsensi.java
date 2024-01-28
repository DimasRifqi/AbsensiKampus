package com.example.absensi;

public class DataAbsensi {
    private String Did, Dnama, Dnpm, Dmatakuliah, Dtanggal, Dlokasi, Dketerangan;

    public DataAbsensi(String Did, String Dnama, String Dnpm, String Dmatakuliah, String Dtanggal, String Dlokasi, String Dketerangan)
    {
        this.Did = Did;
        this.Dnama = Dnama;
        this.Dnpm = Dnpm;
        this.Dmatakuliah = Dmatakuliah;
        this.Dtanggal = Dtanggal;
        this.Dlokasi = Dlokasi;
        this.Dketerangan = Dketerangan;
    }

    public String getDid() {
        return Did;
    }

    public void setDid(String did) {
        Did = did;
    }

    public String getDnama() {
        return Dnama;
    }

    public void setDnama(String dnama) {
        Dnama = dnama;
    }

    public String getDnpm() {
        return Dnpm;
    }

    public void setDnpm(String dnpm) {
        Dnpm = dnpm;
    }

    public String getDmatakuliah() {
        return Dmatakuliah;
    }

    public void setDmatakuliah(String dmatakuliah) {
        Dmatakuliah = dmatakuliah;
    }

    public String getDtanggal() {
        return Dtanggal;
    }

    public void setDtanggal(String dtanggal) {
        Dtanggal = dtanggal;
    }

    public String getDlokasi() {
        return Dlokasi;
    }

    public void setDlokasi(String dlokasi) {
        Dlokasi = dlokasi;
    }

    public String getDketerangan() {
        return Dketerangan;
    }

    public void setDketerangan(String dketerangan) {
        Dketerangan = dketerangan;
    }
}

package com.example.findmushroom2;

public class Jamur {
    String nama;
    String ilmiah;
    String status;

    public Jamur(String nama, String ilmiah, String status) {
        this.nama = nama;
        this.ilmiah = ilmiah;
        this.status = status;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getIlmiah() {
        return ilmiah;
    }

    public void setIlmiah(String ilmiah) {
        this.ilmiah = ilmiah;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

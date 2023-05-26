package com.example.findmushroom2;

public class Database {
    static String nama,genus,famili,deskripsi,manfaat;

    public void ambilData(String result){
        if (result.equals("Beracun_AmanitaBisporigera")){
            nama = "Amanita bisporigera";
            genus = "Amanita";
            famili = "Amanitaceae";
            deskripsi = "Amanita bisporigera ini pada 1906 baru dikatakan sebagai spesies baru oleh ahli botani Amerika yang bernama George Francis Atkinson.\nJamur yang satu ini termasuk ke dalam divisi Basidiomycota, dari kelas Agaricomycetes, berada di urutan Agarical, dan termasuk ke dalam keluarga Amanitaceae.";
            manfaat = "";
        }

        if (result.equals("Beracun_AmanitaMuscaria")){
            nama = "";
            genus = "";
            famili = "";
            deskripsi = "";
            manfaat = "";
        }

        if (result.equals("Beracun_AmanitaPhalloides")){
            nama = "";
            genus = "";
            famili = "";
            deskripsi = "";
            manfaat = "";
        }

        if (result.equals("Beracun_AmanitaRubescens")){
            nama = "";
            genus = "";
            famili = "";
            deskripsi = "";
            manfaat = "";
        }

        if (result.equals("JamurKonsumsi_JamurEnoki")){
            nama = "";
            genus = "";
            famili = "";
            deskripsi = "";
            manfaat = "";
        }

        if (result.equals("JamurKonsumsi_JamurKancing")){
            nama = "";
            genus = "";
            famili = "";
            deskripsi = "";
            manfaat = "";
        }

        if (result.equals("JamurKonsumsi_JamurKuping")){
            nama = "";
            genus = "";
            famili = "";
            deskripsi = "";
            manfaat = "";
        }

        if (result.equals("JamurKonsumsi_JamurMaitake")){
            nama = "";
            genus = "";
            famili = "";
            deskripsi = "";
            manfaat = "";
        }

        if (result.equals("JamurKonsumsi_JamurPorcini")){
            nama = "";
            genus = "";
            famili = "";
            deskripsi = "";
            manfaat = "";
        }

        if (result.equals("JamurKonsumsi_JamurTiram")){
            nama = "";
            genus = "";
            famili = "";
            deskripsi = "";
            manfaat = "";
        }
    }
}

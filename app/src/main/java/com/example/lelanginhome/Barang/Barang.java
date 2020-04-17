package com.example.lelanginhome.Barang;

public class Barang {
    private String idBarang, picTips, namaBarang,content;

    public Barang( String picTips,String namaBarang,String content) {
        this.picTips = picTips;
        this.namaBarang = namaBarang;
        this.content = content;
    }

    @Override
    public String toString(){
            return namaBarang;
    }
    public Barang() {}


    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    public String getPicTips() {
        return picTips;
    }

    public void setPicTips(String picTips) {
        this.picTips = picTips;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

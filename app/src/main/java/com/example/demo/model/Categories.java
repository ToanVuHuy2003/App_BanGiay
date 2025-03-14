package com.example.demo.model;

public class Categories {
    private String idHang;
    private String tenHang;

    public Categories() {
        // Constructor rỗng cần thiết cho Firestore
    }

    public Categories(String idHang, String tenHang) {
        this.idHang = idHang;
        this.tenHang = tenHang;
    }

    public String getIdHang() {
        return idHang;
    }

    public String getTenHang() {
        return tenHang;
    }
}

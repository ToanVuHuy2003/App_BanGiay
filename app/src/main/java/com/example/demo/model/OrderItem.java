package com.example.demo.model;

public class OrderItem {
    private String tenSP;
    private int soLuong;
    private int giaTien;

    // Constructor mặc định (cần thiết cho Firestore)
    public OrderItem() {}

    public OrderItem(String tenSP, int soLuong, int giaTien) {
        this.tenSP = tenSP;
        this.soLuong = soLuong;
        this.giaTien = giaTien;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(int giaTien) {
        this.giaTien = giaTien;
    }
}

package com.example.demo.model;

import java.util.List;
import java.util.Map;

public class Order {
    private String idDonHang;
    private String idKH;
    private List<Map<String, Object>> sanPham;
    private int tongTien;
    private String trangThai;

    // Constructor mặc định (cần thiết cho Firestore)
    public Order() {
    }

    // Constructor đầy đủ
    public Order(String idDonHang, String idKH, List<Map<String, Object>> sanPham, int tongTien, String trangThai) {
        this.idDonHang = idDonHang;
        this.idKH = idKH;
        this.sanPham = sanPham;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    // Getter và Setter
    public String getIdDonHang() {
        return idDonHang;
    }

    public void setIdDonHang(String idDonHang) {
        this.idDonHang = idDonHang;
    }

    public String getIdKH() {
        return idKH;
    }

    public void setIdKH(String idKH) {
        this.idKH = idKH;
    }

    public List<Map<String, Object>> getSanPham() {
        return sanPham;
    }

    public void setSanPham(List<Map<String, Object>> sanPham) {
        this.sanPham = sanPham;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}

package com.example.demo.model;

import java.util.List;

public class CartItem {
    private String idGioHang;
    private String idKH; // Thêm trường idKH
    private List<Product> idSP; // Danh sách sản phẩm
    private int soLuong;
    private int tongTien;

    public CartItem() {}

    public CartItem(String idGioHang, String idKH, List<Product> idSP, int soLuong, int tongTien) {
        this.idGioHang = idGioHang;
        this.idKH = idKH;
        this.idSP = idSP;
        this.soLuong = soLuong;
        this.tongTien = tongTien;
    }

    public String getIdGioHang() {
        return idGioHang;
    }

    public void setIdGioHang(String idGioHang) {
        this.idGioHang = idGioHang;
    }

    public String getIdKH() {
        return idKH;
    }

    public void setIdKH(String idKH) {
        this.idKH = idKH;
    }

    public List<Product> getIdSP() {
        return idSP;
    }

    public void setIdSP(List<Product> idSP) {
        this.idSP = idSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }
}

package com.example.demo.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Parcelable {
    private String idSP;
    private String tenSP;
    private int giaTien;
    private String idHang;
    private List<String> idSize; // Chuyển từ String sang List<String>
    private String moTa;
    private String hinhAnh;
    private int soLuong;

    public Product() {
        this.idSize = new ArrayList<>(); // Khởi tạo danh sách để tránh NullPointerException
    }

    public Product(String idSP, String tenSP, int giaTien, String idHang, List<String> idSize, String moTa, String hinhAnh, int soLuong) {
        this.idSP = idSP;
        this.tenSP = tenSP;
        this.giaTien = giaTien;
        this.idHang = idHang;
        this.idSize = idSize;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.soLuong = 1;
    }

    protected Product(Parcel in) {
        idSP = in.readString();
        tenSP = in.readString();
        giaTien = in.readInt();
        idHang = in.readString();
        idSize = in.createStringArrayList(); // Đọc danh sách idSize từ Parcel
        moTa = in.readString();
        hinhAnh = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idSP);
        dest.writeString(tenSP);
        dest.writeInt(giaTien);
        dest.writeString(idHang);
        dest.writeStringList(idSize); // Ghi danh sách idSize vào Parcel
        dest.writeString(moTa);
        dest.writeString(hinhAnh);
    }

    // Getter và Setter
    public String getIdSP() { return idSP; }
    public void setIdSP(String idSP) { this.idSP = idSP; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }

    public int getGiaTien() { return giaTien; }
    public void setGiaTien(int giaTien) { this.giaTien = giaTien; }

    public String getIdHang() { return idHang; }
    public void setIdHang(String idHang) { this.idHang = idHang; }

    public List<String> getIdSize() { return idSize; } // Trả về danh sách idSize
    public void setIdSize(List<String> idSize) { this.idSize = idSize; } // Gán danh sách idSize

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}

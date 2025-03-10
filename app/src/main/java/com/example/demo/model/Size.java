package com.example.demo.model;

public class Size {
    private String idSize;
    private String tenSize;

    public Size() {}

    public Size(String idSize, String tenSize) {
        this.idSize = idSize;
        this.tenSize = tenSize;
    }

    public String getIdSize() { return idSize; }
    public void setIdSize(String idSize) { this.idSize = idSize; }

    public String getTenSize() { return tenSize; }
    public void setTenSize(String tenSize) { this.tenSize = tenSize; }
}
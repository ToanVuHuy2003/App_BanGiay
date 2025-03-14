package com.example.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.demo.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHelper {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Context context;

    public OrderHelper(Context context) {
        this.context = context;
    }

    public void placeOrder(List<Product> cartList, Runnable callback) {
        String userId = getUserId();
        if (userId.isEmpty()) {
            Toast.makeText(context, "Không tìm thấy ID khách hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("DonHang")
                .orderBy("idDonHang")
                .limitToLast(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int newOrderId = 1;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot lastOrder = queryDocumentSnapshots.getDocuments().get(0);
                        String lastId = lastOrder.getString("idDonHang");
                        if (lastId != null) {
                            newOrderId = Integer.parseInt(lastId) + 1;
                        }
                    }

                    String formattedOrderId = String.format("%02d", newOrderId);

                    Map<String, Object> orderData = new HashMap<>();
                    orderData.put("idDonHang", formattedOrderId);
                    orderData.put("idKH", userId);
                    orderData.put("sanPham", convertProductsToMap(cartList));
                    orderData.put("tongTien", calculateTotal(cartList));
                    orderData.put("trangThai", "Đang xử lý");

                    db.collection("DonHang").document(formattedOrderId)
                            .set(orderData)
                            .addOnSuccessListener(aVoid -> {
                                removeItemsFromCart(userId);
                                if (callback != null) {
                                    callback.run(); // Gọi callback sau khi đặt hàng thành công
                                }
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Lỗi khi đặt hàng!", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Lỗi khi lấy ID đơn hàng cuối!", Toast.LENGTH_SHORT).show());
    }

    private String getUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("idKH", "");
    }

    private List<Map<String, Object>> convertProductsToMap(List<Product> cartList) {
        List<Map<String, Object>> productList = new ArrayList<>();
        for (Product product : cartList) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("idSP", product.getIdSP());
            productData.put("tenSP", product.getTenSP());
            productData.put("giaTien", product.getGiaTien());
            productData.put("soLuong", product.getSoLuong());
            productData.put("tongTien", product.getSoLuong() * product.getGiaTien());
            productList.add(productData);
        }
        return productList;
    }

    private int calculateTotal(List<Product> cartList) {
        int total = 0;
        for (Product product : cartList) {
            total += product.getSoLuong() * product.getGiaTien();
        }
        return total;
    }

    private void removeItemsFromCart(String userId) {
        db.collection("GioHang")
                .whereEqualTo("idKH", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        db.collection("GioHang").document(document.getId()).delete();
                    }
                });
    }
}

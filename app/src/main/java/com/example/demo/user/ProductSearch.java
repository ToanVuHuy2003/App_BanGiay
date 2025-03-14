package com.example.demo.user;

import com.example.demo.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ProductSearch {
    private FirebaseFirestore db;

    public ProductSearch() {
        db = FirebaseFirestore.getInstance();
    }

    public void searchProducts(String query, String selectedCategoryId, OnSearchCompleteListener listener) {
        db.collection("SanPham")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> productList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            // Lấy chuỗi đã chuẩn hóa cho tên sản phẩm và query
                            String normalizedProductName = removeDiacritics(product.getTenSP());
                            String normalizedQuery = removeDiacritics(query);

                            // (Tùy chọn) Log để kiểm tra:
                            // Log.d("Search", "Normalized product: " + normalizedProductName + ", normalized query: " + normalizedQuery);

                            if (normalizedProductName.contains(normalizedQuery)) {
                                if (selectedCategoryId == null || selectedCategoryId.equals("all") || product.getIdHang().equals(selectedCategoryId)) {
                                    productList.add(product);
                                }
                            }
                        }
                        listener.onComplete(productList);
                    } else {
                        listener.onError(task.getException());
                    }
                });
    }

    public static String removeDiacritics(String s) {
        if (s == null) {
            return null;
        }
        // Thay thế chữ đ/Đ nếu cần
        s = s.replaceAll("Đ", "D").replaceAll("đ", "d");
        // Chuyển chuỗi sang dạng NFD để tách dấu
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        // Loại bỏ các ký tự dấu
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase().trim();
    }

    public interface OnSearchCompleteListener {
        void onComplete(List<Product> productList);
        void onError(Exception e);
    }
}

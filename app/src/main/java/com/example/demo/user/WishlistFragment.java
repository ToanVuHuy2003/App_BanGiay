package com.example.demo.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.R;
import com.example.demo.adapters.WishlistAdapter;
import com.example.demo.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WishlistFragment extends Fragment {
    private RecyclerView recyclerViewWishlist;
    private WishlistAdapter wishlistAdapter;
    private List<Product> wishlist;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private TextView txtEmptyWishlist;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewWishlist = view.findViewById(R.id.recyclerViewWishlist);
        txtEmptyWishlist = view.findViewById(R.id.txtEmptyWishlist);

        wishlist = new ArrayList<>();
        wishlistAdapter = new WishlistAdapter(getContext(), wishlist);
        recyclerViewWishlist.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewWishlist.setAdapter(wishlistAdapter);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        loadWishlist();
    }

    private void loadWishlist() {
        txtEmptyWishlist.setVisibility(View.GONE); // Ẩn trước khi tải dữ liệu

        String userId = sharedPreferences.getString("idKH", null);
        if (userId == null) {
            txtEmptyWishlist.setVisibility(View.VISIBLE);
            txtEmptyWishlist.setText("Bạn cần đăng nhập để xem danh sách yêu thích.");
            return;
        }

        db.collection("DanhSachYeuThich").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> wishlistData = (List<Map<String, Object>>) documentSnapshot.get("wishlist");
                        if (wishlistData != null && !wishlistData.isEmpty()) {
                            wishlist.clear();
                            for (Map<String, Object> item : wishlistData) {
                                Product product = new Product();
                                product.setIdSP(item.get("idSP").toString());
                                product.setTenSP(item.get("tenSP").toString());
                                product.setGiaTien(Integer.parseInt(item.get("giaTien").toString()));
                                product.setHinhAnh(item.get("hinhAnh").toString());
                                wishlist.add(product);
                            }
                            wishlistAdapter.notifyDataSetChanged();
                            txtEmptyWishlist.setVisibility(View.GONE); // Ẩn nếu có dữ liệu
                        } else {
                            txtEmptyWishlist.setVisibility(View.VISIBLE);
                            txtEmptyWishlist.setText("Danh sách yêu thích của bạn đang trống.");
                        }
                    } else {
                        txtEmptyWishlist.setVisibility(View.VISIBLE);
                        txtEmptyWishlist.setText("Danh sách yêu thích của bạn đang trống.");
                    }
                })
                .addOnFailureListener(e -> {
                    txtEmptyWishlist.setVisibility(View.VISIBLE);
                    txtEmptyWishlist.setText("Lỗi khi tải danh sách yêu thích!");
                    Toast.makeText(getContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                });
    }
}

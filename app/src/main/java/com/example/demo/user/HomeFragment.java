package com.example.demo.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.LoginActivity;
import com.example.demo.R;
import com.example.demo.adapters.ProductAdapter;
import com.example.demo.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewProducts, recyclerViewHotProd;
    private ProductAdapter productAdapter, hotProductAdapter;
    private List<Product> productList, hotProductList;
    private FirebaseFirestore db;
    private ImageView accountIcon;
    private FirebaseAuth auth;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ RecyclerView
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewHotProd = view.findViewById(R.id.recyclerViewHotProd);
        recyclerViewHotProd.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Ánh xạ icon tài khoản
        accountIcon = view.findViewById(R.id.account_icon);
        auth = FirebaseAuth.getInstance();

        // Khởi tạo danh sách
        productList = new ArrayList<>();
        hotProductList = new ArrayList<>();

        productAdapter = new ProductAdapter(getContext(), productList);
        hotProductAdapter = new ProductAdapter(getContext(), hotProductList);

        recyclerViewProducts.setAdapter(productAdapter);
        recyclerViewHotProd.setAdapter(hotProductAdapter);

        db = FirebaseFirestore.getInstance();
        loadNewProducts();
        loadHotProducts();

        // Xử lý sự kiện khi nhấn vào icon tài khoản
        accountIcon.setOnClickListener(v -> showPopupMenu(v));

        return view;
    }

    private void loadNewProducts() {
        db.collection("SanPham")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadHotProducts() {
        db.collection("SanPham")
                .whereEqualTo("idHang", "02")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        hotProductList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            hotProductList.add(product);
                        }
                        hotProductAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_account, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_logout) {
                logoutUser();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void logoutUser() {
        auth.signOut();
        Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

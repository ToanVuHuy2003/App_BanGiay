package com.example.demo.user;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.demo.R;
import com.example.demo.adapters.CategoriesAdapter;
import com.example.demo.adapters.ProductAdapter;
import com.example.demo.model.Categories;
import com.example.demo.model.Product;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ListProFragment extends Fragment {
    private RecyclerView recyclerViewCategories, recyclerViewProducts;
    private CategoriesAdapter categoriesAdapter;
    private ProductAdapter productAdapter;
    private List<Categories> categoriesList;
    private List<Product> productList;
    private FirebaseFirestore db;
    private String selectedCategoryId = null; // Lưu trữ idHang được chọn

    public ListProFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_pro, container, false);

        db = FirebaseFirestore.getInstance();

        // Khởi tạo RecyclerView cho danh mục
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Khởi tạo danh sách và adapter cho danh mục
        categoriesList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(categoriesList, this::onCategorySelected);
        recyclerViewCategories.setAdapter(categoriesAdapter);

        // Load danh mục từ Firestore
        loadCategoriesFromFirestore();

        // Khởi tạo RecyclerView cho sản phẩm
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Khởi tạo danh sách và adapter cho sản phẩm
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerViewProducts.setAdapter(productAdapter);

        // Load tất cả sản phẩm ban đầu
        loadProductsFromFirestore(null);

        return view;
    }

    private void loadCategoriesFromFirestore() {
        CollectionReference categoriesRef = db.collection("HangSX");
        categoriesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categoriesList.clear();
                categoriesList.add(new Categories("all", "Tất cả")); // Thêm danh mục "Tất cả"
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String idHang = document.getString("idHang");
                    String tenHang = document.getString("tenHang");
                    categoriesList.add(new Categories(idHang, tenHang));
                }
                categoriesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadProductsFromFirestore(String idHang) {
        db.collection("SanPham")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            if (idHang == null || idHang.equals("all") || product.getIdHang().equals(idHang)) {
                                productList.add(product);
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void onCategorySelected(String idHang) {
        selectedCategoryId = idHang;
        loadProductsFromFirestore(selectedCategoryId);
    }
}

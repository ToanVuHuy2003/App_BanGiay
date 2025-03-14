package com.example.demo.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.R;
import com.example.demo.adapters.CategoriesAdapter;
import com.example.demo.adapters.ProductAdapter;
import com.example.demo.model.Categories;
import com.example.demo.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private String selectedCategoryId = null;
    private LinearLayout searchContainer;
    private EditText editTextSearch;
    private Button btnSearch;
    private ProductSearch productSearch;

    public ListProFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_pro, container, false);

        db = FirebaseFirestore.getInstance();
        productSearch = new ProductSearch();

        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        AppCompatImageButton btnBackHome = view.findViewById(R.id.btn_backHome);
        ImageView iconSearch = view.findViewById(R.id.icon_search);
        searchContainer = view.findViewById(R.id.search_container);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        btnSearch = view.findViewById(R.id.btnSearch);

        btnBackHome.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.navMenu);
            bottomNavigationView.setSelectedItemId(R.id.mnHome);
        });

        iconSearch.setOnClickListener(v -> {
            if (searchContainer.getVisibility() == View.GONE) {
                searchContainer.setVisibility(View.VISIBLE);
            } else {
                searchContainer.setVisibility(View.GONE);
            }
        });

        btnSearch.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString().trim();
            productSearch.searchProducts(query, selectedCategoryId, new ProductSearch.OnSearchCompleteListener() {
                @Override
                public void onComplete(List<Product> results) {
                    productList.clear();
                    productList.addAll(results);
                    productAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getContext(), "Lỗi khi tìm kiếm!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        categoriesList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(categoriesList, this::onCategorySelected);
        recyclerViewCategories.setAdapter(categoriesAdapter);

        loadCategoriesFromFirestore();

        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerViewProducts.setAdapter(productAdapter);

        loadProductsFromFirestore(null);

        return view;
    }

    private void loadCategoriesFromFirestore() {
        CollectionReference categoriesRef = db.collection("HangSX");
        categoriesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categoriesList.clear();
                categoriesList.add(new Categories("all", "Tất cả"));
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
                            if ((idHang == null || idHang.equals("all") || product.getIdHang().equals(idHang)) &&
                                    (editTextSearch.getText().toString().trim().isEmpty() ||
                                            productSearch.removeDiacritics(product.getTenSP()).contains(productSearch.removeDiacritics(editTextSearch.getText().toString().trim())))) {
                                productList.add(product);
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void onCategorySelected(String idHang) {
        selectedCategoryId = idHang;
        btnSearch.performClick();
    }
}
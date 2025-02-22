package com.example.demo.user;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.demo.R;
import com.example.demo.adapters.ProductAdapter;
import com.example.demo.model.Product;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    public HomeFragment() {
        // Constructor mặc định
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Tạo danh sách sản phẩm
        productList = new ArrayList<>();
        productList.add(new Product("Giày Ultraboost", "đ4.900.000", R.drawable.test_pro));
        productList.add(new Product("Giày Ultraboost", "đ4.900.000", R.drawable.test_pro));
        productList.add(new Product("Giày Ultraboost", "đ4.900.000", R.drawable.test_pro));

        // Khởi tạo adapter và gán cho RecyclerView
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        return view;
    }
}

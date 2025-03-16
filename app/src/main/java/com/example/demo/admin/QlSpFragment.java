package com.example.demo.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.R;
import com.example.demo.adapters.ProductAdminAdapter;
import com.example.demo.model.Product;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QlSpFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdminAdapter adapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private EditText edtSearchProduct;
    private Button btnAddProduct;

    public QlSpFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qlsp, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        edtSearchProduct = view.findViewById(R.id.edtSearch);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productList = new ArrayList<>();
        adapter = new ProductAdminAdapter(getContext(), productList, product -> openEditProduct(product));

        recyclerView.setAdapter(adapter);

        loadProducts();

        edtSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddOrEditProductActivity.class);
            addOrEditProductLauncher.launch(intent); // Thay vì startActivityForResult()
        });

        return view;
    }
    private void loadProducts() {
        CollectionReference productRef = db.collection("SanPham");
        productRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Product product = document.toObject(Product.class);
                    productList.add(product);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Lỗi khi tải sản phẩm!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getTenSP().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adapter.updateList(filteredList);
    }

    private void openEditProduct(Product product) {
        Intent intent = new Intent(getContext(), AddOrEditProductActivity.class);

        // Truyền dữ liệu sản phẩm qua Intent
        intent.putExtra("idSP", product.getIdSP());
        intent.putExtra("tenSP", product.getTenSP());
        intent.putExtra("giaTien", product.getGiaTien());
        intent.putExtra("hinhAnh", product.getHinhAnh());
        intent.putExtra("moTa", product.getMoTa());
        intent.putExtra("idHang", product.getIdHang());
        intent.putStringArrayListExtra("idSize", new ArrayList<>(product.getIdSize()));

        addOrEditProductLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> addOrEditProductLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadProducts(); // Cập nhật danh sách sản phẩm
                }
            });
}

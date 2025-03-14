package com.example.demo.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.demo.R;
import com.example.demo.adapters.CartAdapter;
import com.example.demo.model.Product;
import com.example.demo.utils.OrderHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private List<Product> cartList;
    private TextView txtTotalPrice;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice);

        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartList, txtTotalPrice);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCart.setAdapter(cartAdapter);

        Button btnCheckout = view.findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(v -> {
            OrderHelper orderHelper = new OrderHelper(getContext());
            orderHelper.placeOrder(cartList, () -> {
                cartList.clear();
                cartAdapter.notifyDataSetChanged();
                txtTotalPrice.setText("Tổng tiền: đ0");
            });
        });

        loadCart();
    }

    // Lấy idKH của khách hàng từ SharedPreferences
    private String getUserId() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("idKH", ""); // Lấy idKH, nếu không có trả về ""
    }

    // Load giỏ hàng theo idKH từ Firestore
    private void loadCart() {
        String userId = getUserId();
        if (userId.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy sản phẩm trong giỏ khách hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("GioHang").whereEqualTo("idKH", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartList.clear();
                    int totalPrice = 0;

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        List<Map<String, Object>> idSPList = (List<Map<String, Object>>) document.get("idSP");
                        if (idSPList != null) {
                            for (Map<String, Object> item : idSPList) {
                                String idSP = (String) item.get("idSP");
                                String tenSP = (String) item.get("tenSP");
                                int giaTien = ((Long) item.get("giaTien")).intValue();
                                String hinhAnh = (String) item.get("hinhAnh");
                                int soLuong = ((Long) item.get("soLuong")).intValue();
                                int tongTien = ((Long) item.get("tongTien")).intValue();

                                // Tạo đối tượng Product với thông tin số lượng
                                Product product = new Product(idSP, tenSP, giaTien, "", new ArrayList<>(), "", hinhAnh, soLuong);
                                product.setSoLuong(soLuong);
                                cartList.add(product);
                                totalPrice += tongTien;
                            }
                        }
                    }

                    cartAdapter.notifyDataSetChanged();
                    txtTotalPrice.setText(String.format("Tổng tiền: đ %,d", totalPrice));
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi tải giỏ hàng!", Toast.LENGTH_SHORT).show());
    }
}

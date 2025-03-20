package com.example.demo.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.demo.R;
import com.example.demo.adapters.OrderDetailAdapter;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderDetailAdapter adapter;
    private List<OrderItem> orderItemList;
    private FirebaseFirestore db;
    private TextView txtOrderId, txtTotalPrice, txtOrderStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Ánh xạ view
        recyclerView = findViewById(R.id.recyclerViewProducts);
        txtOrderId = findViewById(R.id.txtOrderId);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtOrderStatus = findViewById(R.id.txtStatus);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderItemList = new ArrayList<>();
        adapter = new OrderDetailAdapter(orderItemList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Nhận idDonHang từ Intent
        String idDonHang = getIntent().getStringExtra("idDonHang");
        if (idDonHang != null) {
            loadOrderDetails(idDonHang);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadOrderDetails(String idDonHang) {
        Log.d("OrderDetail", "Đang tải đơn hàng với ID: " + idDonHang); // In ra log để kiểm tra ID

        db.collection("DonHang").document(idDonHang)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Log.e("OrderDetail", "Không tìm thấy đơn hàng trong Firestore!"); // Ghi log lỗi
                        Toast.makeText(this, "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    Log.d("OrderDetail", "Đơn hàng tồn tại trong Firestore!"); // Xác nhận đơn hàng có tồn tại

                    txtOrderId.setText("Mã đơn: " + documentSnapshot.getId());

                    // Kiểm tra nếu totalPrice là null
                    Long tongTien = documentSnapshot.getLong("tongTien");
                    if (tongTien == null) {
                        Log.e("OrderDetail", "Lỗi: Không lấy được tổng tiền!");
                        tongTien = 0L; // Gán giá trị mặc định tránh crash
                    }
                    txtTotalPrice.setText(String.format("Tổng tiền: đ %,d", tongTien));

                    // Kiểm tra trạng thái đơn hàng
                    String trangThai = documentSnapshot.getString("trangThai");
                    if (trangThai == null) {
                        Log.e("OrderDetail", "Lỗi: Không có trạng thái đơn hàng!");
                        trangThai = "Chưa cập nhật";
                    }
                    txtOrderStatus.setText("Trạng thái: " + trangThai);

                    // Kiểm tra danh sách sản phẩm
                    List<Map<String, Object>> sanPhamList = (List<Map<String, Object>>) documentSnapshot.get("sanPham");
                    orderItemList.clear();

                    if (sanPhamList != null) {
                        for (Map<String, Object> item : sanPhamList) {
                            String tenSP = (String) item.get("tenSP");
                            int soLuong = item.get("soLuong") != null ? ((Long) item.get("soLuong")).intValue() : 0;
                            int giaTien = item.get("giaTien") != null ? ((Long) item.get("giaTien")).intValue() : 0;

                            orderItemList.add(new OrderItem(tenSP, soLuong, giaTien));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("OrderDetail", "Lỗi: Không tìm thấy danh sách sản phẩm!");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OrderDetail", "Lỗi khi tải dữ liệu: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                });
    }
}

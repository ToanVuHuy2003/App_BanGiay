package com.example.demo.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.demo.R;
import com.example.demo.adapters.QlDhAdapter;
import com.example.demo.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class QlDhFragment extends Fragment {

    private RecyclerView recyclerView;
    private QlDhAdapter adapter;
    private List<Order> orderList;
    private FirebaseFirestore db;

    public QlDhFragment() {
        super(R.layout.fragment_qldh);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        adapter = new QlDhAdapter(orderList, this::confirmOrder, this::cancelOrder);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadOrders();
    }

    private void loadOrders() {
        db.collection("DonHang")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Order order = document.toObject(Order.class);
                        order.setIdDonHang(document.getId()); // Đảm bảo idDonHang được set đúng
                        orderList.add(order);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi khi tải đơn hàng!", Toast.LENGTH_SHORT).show()
                );
    }

    private void confirmOrder(Order order) {
        db.collection("DonHang").document(order.getIdDonHang())
                .update("trangThai", "Đã xác nhận")
                .addOnSuccessListener(aVoid -> {
                    order.setTrangThai("Đã xác nhận");
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Đơn hàng đã được xác nhận!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi khi xác nhận đơn hàng!", Toast.LENGTH_SHORT).show()
                );
    }

    private void cancelOrder(Order order) {
        if (!"Đang xử lý".equals(order.getTrangThai())) {
            Toast.makeText(getContext(), "Không thể hủy đơn hàng đã xác nhận!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("DonHang").document(order.getIdDonHang())
                .update("trangThai", "Đã hủy")
                .addOnSuccessListener(aVoid -> {
                    order.setTrangThai("Đã hủy");
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Đơn hàng đã bị hủy!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi khi hủy đơn hàng!", Toast.LENGTH_SHORT).show()
                );
    }
}

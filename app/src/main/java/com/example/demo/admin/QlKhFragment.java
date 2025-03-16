package com.example.demo.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.demo.R;
import com.example.demo.adapters.KhachHangAdapter;
import com.example.demo.model.KhachHang;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class QlKhFragment extends Fragment {
    RecyclerView recyclerView;
    KhachHangAdapter adapter;
    List<KhachHang> khachHangList;
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qlkh, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewKhachHang);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        khachHangList = new ArrayList<>();
        adapter = new KhachHangAdapter(getContext(), khachHangList);
        recyclerView.setAdapter(adapter);

        loadKhachHang();
        return view;
    }

    private void loadKhachHang() {
        db.collection("KhachHang")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        khachHangList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getString("idKH");
                            String name = document.getString("Ten");
                            String email = document.getString("Email");
                            String phone = document.getString("Sdt");
                            String address = document.getString("diaChi");
                            String password = document.getString("password");

                            KhachHang khachHang = new KhachHang(id, email, password, name, phone, address);
                            khachHangList.add(khachHang);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi tải danh sách", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Lỗi: ", task.getException());
                    }
                });
    }
}

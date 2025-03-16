package com.example.demo.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.demo.R;
import com.example.demo.model.KhachHang;
import java.util.List;

public class KhachHangAdapter extends RecyclerView.Adapter<KhachHangAdapter.KhachHangViewHolder> {
    private List<KhachHang> khachHangList;
    private Context context;

    public KhachHangAdapter(Context context, List<KhachHang> khachHangList) {
        this.context = context;
        this.khachHangList = khachHangList;
    }

    @NonNull
    @Override
    public KhachHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_khachhang, parent, false);
        return new KhachHangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhachHangViewHolder holder, int position) {
        KhachHang khachHang = khachHangList.get(position);
        holder.textName.setText(khachHang.getName());
        holder.textEmail.setText(khachHang.getEmail());
        holder.textPhone.setText(khachHang.getPhone());

        // Xử lý khi click vào item
        holder.itemView.setOnClickListener(v -> showDetailDialog(khachHang));
    }

    @Override
    public int getItemCount() {
        return khachHangList.size();
    }

    public static class KhachHangViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textEmail, textPhone;

        public KhachHangViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            textPhone = itemView.findViewById(R.id.textPhone);
        }
    }

    // Hiển thị AlertDialog khi click vào khách hàng
    private void showDetailDialog(KhachHang khachHang) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chi Tiết Khách Hàng");
        builder.setMessage(
                "Tên: " + khachHang.getName() + "\n" +
                        "Email: " + khachHang.getEmail() + "\n" +
                        "SĐT: " + khachHang.getPhone() + "\n" +
                        "Địa Chỉ: " + khachHang.getAddress() + "\n" +
                        "Mật Khẩu: " + khachHang.getPassword() // Hiển thị mật khẩu
        );
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}

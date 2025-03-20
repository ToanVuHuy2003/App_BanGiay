package com.example.demo.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.demo.R;
import com.example.demo.admin.OrderDetailActivity;
import com.example.demo.model.Order;
import java.util.List;

public class QlDhAdapter extends RecyclerView.Adapter<QlDhAdapter.ViewHolder> {

    private List<Order> orderList;
    private OnConfirmOrderListener confirmOrderListener;
    private OnCancelOrderListener cancelOrderListener;

    public interface OnConfirmOrderListener {
        void onConfirm(Order order);
    }

    public interface OnCancelOrderListener {
        void onCancel(Order order);
    }

    public QlDhAdapter(List<Order> orderList, OnConfirmOrderListener confirmOrderListener, OnCancelOrderListener cancelOrderListener) {
        this.orderList = orderList;
        this.confirmOrderListener = confirmOrderListener;
        this.cancelOrderListener = cancelOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.txtOrderId.setText("Mã đơn: " + order.getIdDonHang());
        holder.txtCustomerId.setText("Khách hàng: " + order.getIdKH());
        holder.txtTotalPrice.setText(String.format("Tổng tiền: đ %,d", order.getTongTien()));
        holder.txtStatus.setText("Trạng thái: " + order.getTrangThai());

        // Hiển thị nút Xác nhận đơn hàng
        if ("Đang xử lý".equals(order.getTrangThai())) {
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnConfirm.setOnClickListener(v -> confirmOrderListener.onConfirm(order));

            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> cancelOrderListener.onCancel(order));
        } else {
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        }

        // Xem chi tiết đơn hàng khi nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);
            intent.putExtra("orderId", order.getIdDonHang());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtCustomerId, txtTotalPrice, txtStatus;
        Button btnConfirm, btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtCustomerId = itemView.findViewById(R.id.txtCustomerId);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}

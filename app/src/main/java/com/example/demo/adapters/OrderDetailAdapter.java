    package com.example.demo.adapters;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;
    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;
    import com.example.demo.R;
    import com.example.demo.model.OrderItem;
    import java.util.List;

    public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
        private List<OrderItem> orderItemList;

        public OrderDetailAdapter(List<OrderItem> orderItemList) {
            this.orderItemList = orderItemList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OrderItem item = orderItemList.get(position);
            holder.txtProductName.setText(item.getTenSP());
            holder.txtProductQuantity.setText("Số lượng: " + item.getSoLuong());
            holder.txtProductPrice.setText(String.format("Giá: đ %,d", item.getGiaTien()));
        }

        @Override
        public int getItemCount() {
            return orderItemList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtProductName, txtProductQuantity, txtProductPrice;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtProductName = itemView.findViewById(R.id.txtProductName);
                txtProductQuantity = itemView.findViewById(R.id.txtProductQuantity);
                txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            }
        }
    }

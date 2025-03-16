package com.example.demo.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.R;
import com.example.demo.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminAdapter.ViewHolder> {
    private Context context;
    private List<Product> productList;
    private FirebaseFirestore db;
    private OnProductClickListener listener; // Interface lắng nghe sự kiện click

    // Interface để bắt sự kiện khi nhấn vào nút "Sửa"
    public interface OnProductClickListener {
        void onEditClick(Product product);
    }

    public ProductAdminAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.db = FirebaseFirestore.getInstance();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.txtProductName.setText(product.getTenSP());
        holder.txtProductPrice.setText("Giá: " + product.getGiaTien() + " VND");

        if (product.getHinhAnh() != null && !product.getHinhAnh().isEmpty()) {
            Glide.with(context).load(product.getHinhAnh()).into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.test_pro); // Ảnh mặc định
        }

        // Gọi listener khi nhấn vào nút "Sửa"
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(product);
            }
        });

        // Xóa sản phẩm
        holder.btnDelete.setOnClickListener(v -> showDeleteDialog(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateList(List<Product> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }

    private void showDeleteDialog(Product product) {
        new AlertDialog.Builder(context)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa sản phẩm này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteProduct(product))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProduct(Product product) {
        db.collection("SanPham").document(product.getIdSP())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    productList.remove(product);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi xóa!", Toast.LENGTH_SHORT).show());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductPrice;
        ImageView imgProduct;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductNameAdmin);
            txtProductPrice = itemView.findViewById(R.id.txtProductPriceAdmin);
            imgProduct = itemView.findViewById(R.id.imgProductAdmin);
            btnEdit = itemView.findViewById(R.id.btnEditProduct);
            btnDelete = itemView.findViewById(R.id.btnDeleteProduct);
        }
    }
}

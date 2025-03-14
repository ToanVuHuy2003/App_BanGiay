package com.example.demo.adapters;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {
    private Context context;
    private List<Product> wishlist;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    public WishlistAdapter(Context context, List<Product> wishlist) {
        this.context = context;
        this.wishlist = wishlist;
        this.db = FirebaseFirestore.getInstance();
        this.sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = wishlist.get(position);

        holder.txtProductName.setText(product.getTenSP());
        holder.txtProductPrice.setText(product.getGiaTien() + " VND");
        Glide.with(context).load(product.getHinhAnh()).into(holder.imgProduct);

        // Kiểm tra nếu sản phẩm đã trong wishlist -> đổi icon
        holder.btnAddToWishlist.setImageResource(R.drawable.baseline_favorite_24);

        // Xử lý khi click vào nút yêu thích
        holder.btnAddToWishlist.setOnClickListener(v -> removeFromWishlist(product, position));
    }

    @Override
    public int getItemCount() {
        return wishlist.size();
    }

    private void removeFromWishlist(Product product, int position) {
        String userId = sharedPreferences.getString("idKH", null);
        if (userId == null) {
            Toast.makeText(context, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 Lấy tham chiếu đến danh sách yêu thích trên Firestore
        DocumentReference wishlistRef = db.collection("DanhSachYeuThich").document(userId);

        wishlistRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> wishlistData = (List<Map<String, Object>>) documentSnapshot.get("wishlist");

                if (wishlistData != null) {
                    // 🔹 Xóa sản phẩm khỏi danh sách
                    wishlistData.remove(position); // Đúng cú pháp

                    // 🔹 Cập nhật Firestore
                    wishlistRef.update("wishlist", wishlistData)
                            .addOnSuccessListener(aVoid -> {
                                wishlist.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, wishlist.size());
                                Toast.makeText(context, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi xóa sản phẩm!", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, btnAddToWishlist; // Chuyển btnAddToWishlist thành ImageView
        TextView txtProductName, txtProductPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            btnAddToWishlist = itemView.findViewById(R.id.btnAddToWishlist); // Không đổi
        }
    }
}

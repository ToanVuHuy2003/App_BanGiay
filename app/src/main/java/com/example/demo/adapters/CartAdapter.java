package com.example.demo.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.demo.R;
import com.example.demo.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private List<Product> cartProductList;
    private TextView txtTotalPrice;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CartAdapter(Context context, List<Product> cartProductList, TextView txtTotalPrice) {
        this.context = context;
        this.cartProductList = cartProductList;
        this.txtTotalPrice = txtTotalPrice;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = cartProductList.get(position);

        holder.txtProductName.setText(product.getTenSP());
        holder.txtProductPrice.setText(String.format("đ %,d", product.getGiaTien()));
        holder.txtProductQuantity.setText(String.valueOf(product.getSoLuong()));

        // Load ảnh sản phẩm
        Glide.with(context).load(product.getHinhAnh()).into(holder.imgProduct);

        // Xử lý sự kiện tăng số lượng
        holder.btnIncreaseQuantity.setOnClickListener(v -> updateQuantity(product, 1, holder.txtProductQuantity));

        // Xử lý sự kiện giảm số lượng
        holder.btnDecreaseQuantity.setOnClickListener(v -> updateQuantity(product, -1, holder.txtProductQuantity));

        // Xử lý sự kiện xóa sản phẩm
        holder.btnDeleteProduct.setOnClickListener(v -> deleteProductFromCart(product, position));
    }

    @Override
    public int getItemCount() {
        return cartProductList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, btnIncreaseQuantity, btnDecreaseQuantity, btnDeleteProduct;
        TextView txtProductName, txtProductPrice, txtProductQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtProductQuantity = itemView.findViewById(R.id.txtProductQuantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
            btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
            btnDeleteProduct = itemView.findViewById(R.id.btnDeleteProduct);
        }
    }

    // Hàm cập nhật số lượng sản phẩm trong Firestore
    private void updateQuantity(Product product, int change, TextView txtProductQuantity) {
        String userId = getUserId();
        if (userId.isEmpty()) {
            Toast.makeText(context, "Không tìm thấy ID người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("GioHang").whereEqualTo("idKH", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            List<Map<String, Object>> idSPList = (List<Map<String, Object>>) document.get("idSP");
                            if (idSPList != null) {
                                boolean updated = false;

                                for (Map<String, Object> item : idSPList) {
                                    if (item.get("idSP").equals(product.getIdSP())) {
                                        int currentQuantity = ((Long) item.get("soLuong")).intValue();
                                        int newQuantity = currentQuantity + change;

                                        if (newQuantity < 1) {
                                            Toast.makeText(context, "Số lượng không thể nhỏ hơn 1!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        // Cập nhật số lượng và tổng tiền của sản phẩm
                                        item.put("soLuong", newQuantity);
                                        item.put("tongTien", newQuantity * product.getGiaTien());
                                        updated = true;
                                        break;
                                    }
                                }

                                if (updated) {
                                    // Cập nhật lại danh sách sản phẩm trong giỏ hàng
                                    db.collection("GioHang").document(document.getId())
                                            .update("idSP", idSPList)
                                            .addOnSuccessListener(aVoid -> {
                                                product.setSoLuong(product.getSoLuong() + change);
                                                txtProductQuantity.setText(String.valueOf(product.getSoLuong()));
                                                updateTotalPrice();
                                                Toast.makeText(context, "Cập nhật số lượng thành công!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi cập nhật số lượng!", Toast.LENGTH_SHORT).show());
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi tải giỏ hàng!", Toast.LENGTH_SHORT).show());
    }

    // Hàm xóa sản phẩm khỏi giỏ hàng trên Firestore
    private void deleteProductFromCart(Product product, int position) {
        String userId = getUserId();
        if (userId.isEmpty()) {
            Toast.makeText(context, "Không tìm thấy ID người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("GioHang").whereEqualTo("idKH", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            List<Map<String, Object>> idSPList = (List<Map<String, Object>>) document.get("idSP");
                            if (idSPList != null) {
                                idSPList.removeIf(item -> item.get("idSP").equals(product.getIdSP()));

                                // Nếu giỏ hàng trống, xóa toàn bộ document giỏ hàng
                                if (idSPList.isEmpty()) {
                                    db.collection("GioHang").document(document.getId())
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                cartProductList.remove(position);
                                                notifyItemRemoved(position);
                                                updateTotalPrice();
                                                Toast.makeText(context, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi xóa sản phẩm!", Toast.LENGTH_SHORT).show());
                                } else {
                                    // Cập nhật lại danh sách giỏ hàng
                                    db.collection("GioHang").document(document.getId())
                                            .update("idSP", idSPList)
                                            .addOnSuccessListener(aVoid -> {
                                                cartProductList.remove(position);
                                                notifyItemRemoved(position);
                                                updateTotalPrice();
                                                Toast.makeText(context, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi xóa sản phẩm!", Toast.LENGTH_SHORT).show());
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi tải giỏ hàng!", Toast.LENGTH_SHORT).show());
    }


    private void updateTotalPrice() {
        int totalPrice = 0;
        for (Product product : cartProductList) {
            totalPrice += product.getSoLuong() * product.getGiaTien();
        }
        txtTotalPrice.setText(String.format("Tổng tiền: đ %,d", totalPrice));
    }

    private String getUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("idKH", "");
    }
}

package com.example.demo.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.R;
import com.example.demo.model.Product;
import com.example.demo.user.DetailProFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage;
        Button btnAddToCart;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.txtProductName);
            productPrice = itemView.findViewById(R.id.txtProductPrice);
            productImage = itemView.findViewById(R.id.imgProduct);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getTenSP());
        holder.productPrice.setText(String.format("đ %,d", product.getGiaTien()));

        // Load ảnh bằng Glide
        if (product.getHinhAnh() != null && !product.getHinhAnh().isEmpty()) {
            Glide.with(context)
                    .load(product.getHinhAnh())
                    .into(holder.productImage);
        }

        // Xử lý sự kiện bấm nút "Thêm vào giỏ hàng"
        holder.btnAddToCart.setOnClickListener(v -> {
            addToCart(product);
        });

        // Xử lý sự kiện click vào sản phẩm để xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            DetailProFragment detailFragment = DetailProFragment.newInstance(
                    product.getIdSP(),
                    product.getTenSP(),
                    String.valueOf(product.getGiaTien()),
                    product.getMoTa(),
                    product.getHinhAnh()
            );

            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Hàm thêm sản phẩm vào giỏ hàng trong Firestore
    private void addToCart(Product product) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("idKH", null);

        if (userId == null) {
            Toast.makeText(context, "Vui lòng đăng nhập để thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tham chiếu đến giỏ hàng của người dùng
        DocumentReference cartRef = db.collection("GioHang").document(userId);

        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            List<Map<String, Object>> idSPList;
            if (documentSnapshot.exists()) {
                idSPList = (List<Map<String, Object>>) documentSnapshot.get("idSP");
                if (idSPList == null) {
                    idSPList = new ArrayList<>();
                }
            } else {
                idSPList = new ArrayList<>();
            }

            // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
            boolean productExists = false;
            for (Map<String, Object> item : idSPList) {
                if (item.get("idSP").equals(product.getIdSP())) {
                    int currentQuantity = ((Number) item.get("soLuong")).intValue();
                    int newQuantity = currentQuantity + 1;
                    item.put("soLuong", newQuantity);
                    item.put("tongTien", newQuantity * product.getGiaTien());
                    productExists = true;
                    break;
                }
            }

            // Nếu sản phẩm chưa có, thêm vào danh sách
            if (!productExists) {
                Map<String, Object> newProduct = new HashMap<>();
                newProduct.put("idSP", product.getIdSP());
                newProduct.put("tenSP", product.getTenSP());
                newProduct.put("giaTien", product.getGiaTien());
                newProduct.put("hinhAnh", product.getHinhAnh());
                newProduct.put("soLuong", 1);
                newProduct.put("tongTien", product.getGiaTien());
                idSPList.add(newProduct);
            }

            // Tính tổng số lượng và tổng tiền
            int totalQuantity = 0;
            int totalPrice = 0;
            for (Map<String, Object> item : idSPList) {
                totalQuantity += ((Number) item.get("soLuong")).intValue();
                totalPrice += ((Number) item.get("tongTien")).intValue();
            }

            // Cập nhật giỏ hàng trên Firestore
            Map<String, Object> cartData = new HashMap<>();
            cartData.put("idKH", userId);  // Đảm bảo luôn có idKH
            cartData.put("idSP", idSPList);
            cartData.put("soLuong", totalQuantity);
            cartData.put("tongTien", totalPrice);

            cartRef.set(cartData, SetOptions.merge()) // Merge để tránh mất dữ liệu cũ
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show());
        });
    }
}

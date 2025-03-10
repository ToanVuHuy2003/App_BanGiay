package com.example.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.demo.R;
import com.example.demo.model.Categories;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
    private List<Categories> categoryList;
    private OnCategoryClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION; // Lưu vị trí được chọn

    public interface OnCategoryClickListener {
        void onCategoryClick(String idHang);
    }

    public CategoriesAdapter(List<Categories> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.txtCategoryName);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Categories category = categoryList.get(position);
        holder.categoryName.setText(category.getTenHang());

        // Đổi màu nền khi danh mục được chọn
        if (position == selectedPosition) {
            holder.categoryName.setBackgroundResource(R.drawable.category_background);
        } else {
            holder.categoryName.setBackgroundResource(R.drawable.button_border);
        }

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged(); // Cập nhật giao diện khi danh mục thay đổi
            if (listener != null) {
                listener.onCategoryClick(category.getIdHang());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}

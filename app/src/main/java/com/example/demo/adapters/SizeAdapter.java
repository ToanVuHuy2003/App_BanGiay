package com.example.demo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.demo.R;
import com.example.demo.model.Size;
import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder> {
    private Context context;
    private List<Size> sizeList;

    public SizeAdapter(Context context, List<Size> sizeList) {
        this.context = context;
        this.sizeList = sizeList;
    }

    public static class SizeViewHolder extends RecyclerView.ViewHolder {
        TextView txtSize;

        public SizeViewHolder(View itemView) {
            super(itemView);
            txtSize = itemView.findViewById(R.id.txtSize);
        }
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_size, parent, false);
        return new SizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        Size size = sizeList.get(position);
        holder.txtSize.setText(size.getTenSize());
        // Điều chỉnh khoảng cách giữa các item
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        layoutParams.setMargins(5, 0, 5, 0); // Khoảng cách giữa các item
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return sizeList.size();
    }
}
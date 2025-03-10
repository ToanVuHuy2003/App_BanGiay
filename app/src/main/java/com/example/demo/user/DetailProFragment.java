package com.example.demo.user;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.demo.R;
import com.example.demo.adapters.SizeAdapter;
import com.example.demo.model.Size;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class DetailProFragment extends Fragment {
    private static final String ARG_ID = "idSP";
    private static final String ARG_NAME = "name";
    private static final String ARG_PRICE = "price";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_IMAGE_URL = "imageUrl";

    private String productId;
    private String productName;
    private String productPrice;
    private String productDescription;
    private String productImageUrl;
    private RecyclerView recyclerViewSizes;
    private SizeAdapter sizeAdapter;
    private List<Size> sizeList;
    private FirebaseFirestore db;

    public DetailProFragment() {
        // Required empty public constructor
    }

    public static DetailProFragment newInstance(String idSP, String name, String price, String description, String imageUrl) {
        DetailProFragment fragment = new DetailProFragment();
        Bundle args = new Bundle();
        args.putString("ARG_ID", idSP);
        args.putString("ARG_NAME", name);
        args.putString("ARG_PRICE", price);
        args.putString("ARG_DESCRIPTION", description);
        args.putString("ARG_IMAGE_URL", imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString("ARG_ID");
            productName = getArguments().getString("ARG_NAME");
            productPrice = getArguments().getString("ARG_PRICE");
            productDescription = getArguments().getString("ARG_DESCRIPTION");
            productImageUrl = getArguments().getString("ARG_IMAGE_URL");
        }
        sizeList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_pro, container, false);

        ImageView imgProduct = view.findViewById(R.id.imgProduct);
        TextView txtProductName = view.findViewById(R.id.txtProductName);
        TextView txtProductPrice = view.findViewById(R.id.txtProductPrice);
        TextView txtProductDescription = view.findViewById(R.id.txtProductDescription);
        ImageView btnBack = view.findViewById(R.id.btnBack);
        recyclerViewSizes = view.findViewById(R.id.recyclerViewSizes);

        txtProductName.setText(productName);
        txtProductPrice.setText(String.format("đ%s", productPrice));
        txtProductDescription.setText(productDescription);

        // Load ảnh từ URL
        if (productImageUrl != null && !productImageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(productImageUrl)
                    .into(imgProduct);
        }

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Thiết lập RecyclerView
        sizeAdapter = new SizeAdapter(requireContext(), sizeList);
        recyclerViewSizes.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewSizes.setAdapter(sizeAdapter);

        // Load danh sách size từ Firestore
        loadSizesFromFirestore();

        return view;
    }

    private void loadSizesFromFirestore() {
        db.collection("SanPham").document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> idSizeList = (List<String>) documentSnapshot.get("idSize");

                        if (idSizeList != null && !idSizeList.isEmpty()) {
                            sizeList.clear(); // Xóa danh sách cũ để tránh dữ liệu trùng lặp
                            List<Size> tempSizeList = new ArrayList<>();

                            for (String idSize : idSizeList) {
                                db.collection("LoaiSP").document(idSize)
                                        .get()
                                        .addOnSuccessListener(sizeDoc -> {
                                            if (sizeDoc.exists()) {
                                                String sizeName = sizeDoc.getString("tenSize");
                                                tempSizeList.add(new Size(idSize, sizeName));
                                            }

                                            // Khi tải hết tất cả size, cập nhật RecyclerView
                                            if (tempSizeList.size() == idSizeList.size()) {
                                                sizeList.addAll(tempSizeList);
                                                sizeAdapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}

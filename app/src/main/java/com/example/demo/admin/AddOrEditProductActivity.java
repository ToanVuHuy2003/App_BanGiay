package com.example.demo.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.demo.R;
import com.example.demo.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddOrEditProductActivity extends AppCompatActivity {
    private EditText edtProductName, edtProductPrice, edtProductDescription, edtProductImage;
    private Spinner spinnerHangSX;
    private Button btnChooseSizes, btnSave;
    private TextView txtSelectedSizes;
    private ProgressBar progressBar;
    private ImageView imageViewProduct;
    private FirebaseFirestore db;
    private String productId;
    private List<String> idSizeList = new ArrayList<>();
    private Map<String, String> hangSXMap = new HashMap<>(); // Lưu idHang - tenHang
    private Map<String, String> sizeMap = new HashMap<>();   // Lưu idSize - tenSize
    private boolean[] selectedSizes;
    private String[] sizeArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_product);

        // Ánh xạ view
        edtProductName = findViewById(R.id.edtProductName);
        edtProductPrice = findViewById(R.id.edtProductPrice);
        edtProductDescription = findViewById(R.id.edtProductDescription);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        edtProductImage = findViewById(R.id.edtProductImage);
        spinnerHangSX = findViewById(R.id.spinnerHangSX);
        btnChooseSizes = findViewById(R.id.btnChooseSizes);
        txtSelectedSizes = findViewById(R.id.txtSelectedSizes);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();

        // Load danh sách hãng sản xuất và size
        loadHangSX();
        loadSizeList();

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("idSP")) {
            productId = intent.getStringExtra("idSP");
            edtProductName.setText(intent.getStringExtra("tenSP"));
            edtProductPrice.setText(String.valueOf(intent.getDoubleExtra("giaTien", 0)));
            edtProductDescription.setText(intent.getStringExtra("moTa"));
            edtProductImage.setText(intent.getStringExtra("hinhAnh"));

            String idHang = intent.getStringExtra("idHang");
            ArrayList<String> idSizeList = intent.getStringArrayListExtra("idSize");

            // Đặt giá trị cho Spinner hãng sản xuất
            setSpinnerSelection(spinnerHangSX, idHang);

            // Hiển thị danh sách size đã chọn
            if (idSizeList != null) {
                txtSelectedSizes.setText(TextUtils.join(", ", idSizeList));
            }

            // Nếu cần load thêm dữ liệu từ Firestore
            loadProductData(productId);
        }

        // Mở hộp thoại chọn size
        btnChooseSizes.setOnClickListener(v -> showSizeSelectionDialog());

        // Lưu sản phẩm
        btnSave.setOnClickListener(v -> saveProduct());
    }

    // Hàm hỗ trợ chọn đúng giá trị trong Spinner
    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
    private void loadHangSX() {
        db.collection("HangSX").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> hangNames = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String idHang = doc.getId();
                    String tenHang = doc.getString("tenHang");
                    hangSXMap.put(tenHang, idHang);
                    hangNames.add(tenHang);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hangNames);
                spinnerHangSX.setAdapter(adapter);
            }
        });
    }

    private void loadSizeList() {
        db.collection("LoaiSP").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> sizeNames = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String idSize = doc.getId();
                    String tenSize = doc.getString("tenSize");
                    sizeMap.put(tenSize, idSize);
                    sizeNames.add(tenSize);
                }
                sizeArray = sizeNames.toArray(new String[0]);
                selectedSizes = new boolean[sizeArray.length];
            }
        });
    }

    private void loadProductData(String idSP) {
        db.collection("SanPham").document(idSP).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                edtProductName.setText(documentSnapshot.getString("tenSP"));
                edtProductPrice.setText(String.valueOf(documentSnapshot.getLong("giaTien")));
                edtProductDescription.setText(documentSnapshot.getString("moTa"));
                edtProductImage.setText(documentSnapshot.getString("hinhAnh"));

                // Lấy ImageView
                ImageView imgProduct = findViewById(R.id.imgProductAdmin);

                // Đặt ảnh vào ImageView nếu có link ảnh
                String hinhAnhUrl = documentSnapshot.getString("hinhAnh");
                if (hinhAnhUrl != null && !hinhAnhUrl.isEmpty()) {
                    Glide.with(this)
                            .load(hinhAnhUrl)
                            .into(imageViewProduct);
                }

                // Cập nhật Spinner hãng sản xuất
                String idHang = documentSnapshot.getString("idHang");
                if (idHang != null) {
                    for (Map.Entry<String, String> entry : hangSXMap.entrySet()) {
                        if (entry.getValue().equals(idHang)) {
                            int position = ((ArrayAdapter<String>) spinnerHangSX.getAdapter()).getPosition(entry.getKey());
                            spinnerHangSX.setSelection(position);
                            break;
                        }
                    }
                }

                // Cập nhật danh sách size
                List<String> selectedIds = (List<String>) documentSnapshot.get("idSize");
                if (selectedIds != null) {
                    idSizeList = selectedIds;
                    updateSelectedSizesText();
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi khi tải dữ liệu sản phẩm!", Toast.LENGTH_SHORT).show();
        });
    }


    private void showSizeSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn Size");
        builder.setMultiChoiceItems(sizeArray, selectedSizes, (dialog, which, isChecked) -> selectedSizes[which] = isChecked);
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            idSizeList.clear();
            for (int i = 0; i < selectedSizes.length; i++) {
                if (selectedSizes[i]) {
                    idSizeList.add(sizeMap.get(sizeArray[i]));
                }
            }
            updateSelectedSizesText();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void updateSelectedSizesText() {
        List<String> selectedSizeNames = new ArrayList<>();
        for (String idSize : idSizeList) {
            for (Map.Entry<String, String> entry : sizeMap.entrySet()) {
                if (entry.getValue().equals(idSize)) {
                    selectedSizeNames.add(entry.getKey());
                }
            }
        }
        txtSelectedSizes.setText("Size đã chọn: " + TextUtils.join(", ", selectedSizeNames));
    }

    private void saveProduct() {
        String name = edtProductName.getText().toString().trim();
        String priceStr = edtProductPrice.getText().toString().trim();
        String description = edtProductDescription.getText().toString().trim();
        String imageUrl = edtProductImage.getText().toString().trim();
        String tenHang = spinnerHangSX.getSelectedItem().toString();
        String idHang = hangSXMap.get(tenHang);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(description) || TextUtils.isEmpty(imageUrl) || idSizeList.isEmpty() || idHang == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.parseInt(priceStr);
        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> productData = new HashMap<>();
        productData.put("tenSP", name);
        productData.put("giaTien", price);
        productData.put("moTa", description);
        productData.put("hinhAnh", imageUrl);
        productData.put("idHang", idHang);
        productData.put("idSize", idSizeList);

        if (productId != null) {
            // Cập nhật sản phẩm
            db.collection("SanPham").document(productId).update(productData).addOnSuccessListener(aVoid -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Sản phẩm đã được cập nhật!", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Lỗi khi cập nhật sản phẩm!", Toast.LENGTH_SHORT).show();
            });
        } else {
            // Thêm sản phẩm mới
            db.collection("SanPham").orderBy("idSP").get().addOnSuccessListener(queryDocumentSnapshots -> {
                int newIdSP = 1;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    try {
                        int existingId = Integer.parseInt(doc.getString("idSP"));
                        if (existingId >= newIdSP) {
                            newIdSP = existingId + 1;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                String formattedIdSP = String.format("%02d", newIdSP);
                productData.put("idSP", formattedIdSP);

                db.collection("SanPham").document(formattedIdSP).set(productData).addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Sản phẩm đã được thêm!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Lỗi khi lấy ID sản phẩm!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}

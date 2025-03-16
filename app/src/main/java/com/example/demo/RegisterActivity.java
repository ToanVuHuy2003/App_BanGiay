package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword, edtName, edtPhone, edtAddress;
    private Button btnRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Đăng ký tài khoản trên Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Gửi email xác thực
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verificationTask -> {
                                        if (verificationTask.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Đăng ký thành công! Vui lòng kiểm tra email để xác nhận.",
                                                    Toast.LENGTH_LONG).show();

                                            // Đợi xác thực email trước khi lưu vào Firestore
                                            checkEmailVerification(user, password , name, phone, address);
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(RegisterActivity.this,
                                                    "Gửi email xác thực thất bại!",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this,
                                "Đăng ký thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkEmailVerification(FirebaseUser user, String password, String name, String phone, String address) {
        new Thread(() -> {
            try {
                while (!user.isEmailVerified()) {
                    user.reload();
                    Thread.sleep(3000);
                }
                runOnUiThread(() -> saveUserToFirestore(user.getEmail(), password , name, phone, address));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveUserToFirestore(String email, String password, String name, String phone, String address) {
        db.collection("KhachHang").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int newId = task.getResult().size() + 1; // Lấy số lượng tài khoản hiện có + 1
                String userId = String.format("%02d", newId); // Chuyển thành dạng "01", "02", ...

                // Tạo dữ liệu khách hàng
                Map<String, Object> userData = new HashMap<>();
                userData.put("idKH", userId);
                userData.put("Email", email);
                userData.put("Password", password);
                userData.put("Ten", name);
                userData.put("Sdt", phone);
                userData.put("diaChi", address);

                // Lưu vào Firestore với ID dạng số
                db.collection("KhachHang").document(userId).set(userData)
                        .addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this,
                                    "Xác thực email thành công! Tài khoản đã được tạo.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this,
                                    "Lưu thông tin khách hàng thất bại!",
                                    Toast.LENGTH_SHORT).show();
                        });

            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this,
                        "Lỗi lấy danh sách khách hàng!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

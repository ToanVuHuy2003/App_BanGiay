package com.example.demo.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.demo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import android.view.MenuItem;

public class BottomNavigationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(getItemBottomListener());

        // Mặc định hiển thị trang quản lý khách hàng khi vào activity
        if (savedInstanceState == null) {
            loadFragment(new QlKhFragment());
        }
    }

    private NavigationBarView.OnItemSelectedListener getItemBottomListener() {
        return new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_khachhang) {
                    selectedFragment = new QlKhFragment();
                } else if (itemId == R.id.nav_sanpham) {
                    selectedFragment = new QlSpFragment();
                }/* else if (itemId == R.id.nav_donhang) {
                    selectedFragment = new QlDhFragment();
                }*/

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        };
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }
}

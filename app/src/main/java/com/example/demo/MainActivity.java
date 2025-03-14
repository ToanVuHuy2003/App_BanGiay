package com.example.demo;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.demo.user.CartFragment;
import com.example.demo.user.HomeFragment;
import com.example.demo.user.ListProFragment;
import com.example.demo.user.WishlistFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView mnBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mnBottom = findViewById(R.id.navMenu);

        // Load Fragment
        mnBottom.setOnItemSelectedListener(getItemBottomListener());

        // Load Fragment mặc định (ví dụ HomeFragment)
        loadFragment(new HomeFragment());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return true;
    }

    @NonNull
    private NavigationBarView.OnItemSelectedListener getItemBottomListener() {
        return new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.mnHome) {
                    loadFragment(new HomeFragment());
                    return true;
                } else if (itemId == R.id.mnList) {
                    loadFragment(new ListProFragment());
                    return true;
                } else if (itemId == R.id.mnFavor) {
                    loadFragment(new WishlistFragment());
                    return true;
                }else if (itemId == R.id.mnCart) {
                    loadFragment(new CartFragment());
                    return true;
                }
                return false;
            }
        };
    }

    void loadFragment(Fragment frNew) {
        FragmentTransaction fmTran = getSupportFragmentManager().beginTransaction();
        fmTran.replace(R.id.main_fragment, frNew);
        fmTran.addToBackStack(null);
        fmTran.commit();
    }
}
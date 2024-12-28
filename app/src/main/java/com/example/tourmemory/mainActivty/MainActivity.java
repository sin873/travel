package com.example.tourmemory.mainActivty;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tourmemory.R;
import com.example.tourmemory.data.getdata.RecordDataBank;
import com.example.tourmemory.data.record.RecordData;
import com.example.tourmemory.data.record.RecordManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // 页面管理
    private ViewPager2 viewPager;
    private HomeFragment homeFragment;

    // 数据管理
    RecordManager recordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 检查位置权限
        CheckLocationPermission();

        // 初始化控件
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(fragmentAdapter);

        // 禁用ViewPager的滑动，以防止地图拖动时与导航栏冲突
        viewPager.setUserInputEnabled(false);

        // 设置底部导航
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.item_home)
                viewPager.setCurrentItem(0);
            else if(id == R.id.item_map)
                viewPager.setCurrentItem(1);
            return true;
        });

        // 设置添加记录按钮的按下监听
        ImageButton addButton = findViewById(R.id.imageButton_add_record);
        addButton.setOnClickListener(v -> {
            // 跳转到添加记录页面
            homeFragment.StartAddRecordAct();
        });

        // 加载数据
        recordManager = new RecordManager(RecordDataBank.LoadRecords(this));
        recordManager.setOnRecordChangedListener(new RecordChangedListener());
    }

    public void CheckLocationPermission() {
        // 检查位置权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 请求位置权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 定位权限请求结果
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.location_permission_granted), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class FragmentAdapter extends FragmentStateAdapter {
        private static final int NUM_TABS = 2;
        public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // 根据位置返回对应的Fragment实例
            switch (position) {
                case 0:
                    homeFragment = new HomeFragment(recordManager);
                    return homeFragment;
                case 1:
                    return new MapFragment();
            }
            return homeFragment;
        }

        @Override
        public int getItemCount() {
            return NUM_TABS;
        }
    }

    private class RecordChangedListener implements RecordManager.OnRecordChangedListener{
        @Override
        public void onRecordChanged() {
            // 记录变化时保存数据
            recordManager.saveToBank(MainActivity.this);
        }
        @Override
        public void onRecordChanged(int changedIndex) {
            // 记录变化时保存数据
            recordManager.saveToBank(MainActivity.this);
            // 若为最后一个记录，则跳转到首页
            if(changedIndex == recordManager.size() - 1)
                viewPager.setCurrentItem(0);
        }
    }
}
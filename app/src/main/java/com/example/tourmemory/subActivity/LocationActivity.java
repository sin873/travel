package com.example.tourmemory.subActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tourmemory.R;
import com.example.tourmemory.data.getdata.GetDevPos;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer;
import com.tencent.tencentmap.mapsdk.maps.TextureMapView;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

public class LocationActivity extends AppCompatActivity {
    public static final int CANCEL = 0;
    public static final int ADD = 1;

    // 地图控件
    TextureMapView mapView = null;
    TencentMap tencentMap = null;
    Marker marker = null;
    TencentLocation pickedLocation = null;

    // 控件数据
    private GetDevPos getLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 同意地图隐私协议
        TencentMapInitializer.setAgreePrivacy(true);
        TencentLocationManager.setUserAgreePrivacy(true);

        // 获取并初始化地图控件
        mapView = findViewById(R.id.mapView_sub_act);
        tencentMap = mapView.getMap();
        getLocation = new GetDevPos(this, new PositionGot());

        // 定位按钮
        ImageButton location = findViewById(R.id.imageButton_location_sub_act);
        location.setOnClickListener(view -> {
            // 清除旧标记
            if(marker != null) {
                marker.remove();
            }
            getLocation.LocateOnce();
        });

        // 点击标记，将地图中心移动到点击的位置，并显示位置
        tencentMap.setOnMarkerClickListener(marker -> {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(marker.getPosition());
            tencentMap.moveCamera(cameraUpdate);
            marker.showInfoWindow();
            return false;
        });

        // 设置返回按钮的点击事件
        ImageButton backButton = findViewById(R.id.imageButton_back_sub_act);
        backButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("sate", CANCEL);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // 设置添加按钮的点击事件
        Button addButton = findViewById(R.id.button_add_record_sub_act);
        addButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            // 无标记时返回取消state
            if(marker == null){
                resultIntent.putExtra("sate", CANCEL);
                setResult(RESULT_OK, resultIntent);
            }
            else {
                // 将位置信息返回
                resultIntent.putExtra("sate", ADD);
                resultIntent.putExtra("Latitude", marker.getPosition().latitude);
                resultIntent.putExtra("Longitude", marker.getPosition().longitude);
                resultIntent.putExtra("Address", pickedLocation.getAddress());
                resultIntent.putExtra("City", pickedLocation.getCity());
                setResult(RESULT_OK, resultIntent);
            }
            finish();
        });
    }

    // 定位获取回调
    private class PositionGot implements GetDevPos.CallBack{
        @Override
        public void callback(TencentLocation location) {
            // 获取位置并移动地图
            pickedLocation = location;
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 16);
            tencentMap.moveCamera(cameraUpdate);
            // 添加标记
            MarkerOptions markerOptions = new MarkerOptions(pos);
            markerOptions.title(location.getName());
            marker = tencentMap.addMarker(markerOptions);
            marker.setClickable(true);
            marker.showInfoWindow();
        }
    }

    // 重写onDestroy方法，释放资源
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLocation.StopLocate();
        mapView.onDestroy();
    }
}
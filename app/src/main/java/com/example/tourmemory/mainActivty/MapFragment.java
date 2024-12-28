package com.example.tourmemory.mainActivty;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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

public class MapFragment extends Fragment {
    // 地图控件
    TextureMapView mapView = null;
    TencentMap tencentMap = null;
    Marker selfPosMarker = null;
    Marker clickMarker = null;

    // 控件数据
    private GetDevPos getLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_page, container, false);

        // 同意地图隐私协议
        TencentMapInitializer.setAgreePrivacy(true);
        TencentLocationManager.setUserAgreePrivacy(true);

        // 获取并初始化地图控件
        mapView = rootView.findViewById(R.id.mapView);
        tencentMap = mapView.getMap();
        getLocation = new GetDevPos(getContext(), new PositionGot());

        // 地图样式切换按钮
        ImageButton btn = rootView.findViewById(R.id.imageButton_switch);
        btn.setOnClickListener(view -> {
            if (tencentMap.getMapStyle() == TencentMap.MAP_TYPE_NORMAL) {
                tencentMap.setMapStyle(TencentMap.MAP_TYPE_NIGHT);
            } else {
                tencentMap.setMapStyle(TencentMap.MAP_TYPE_NORMAL);
            }
        });

        // 定位按钮
        ImageButton location = rootView.findViewById(R.id.imageButton_location);
        location.setOnClickListener(view -> {
            // 删除旧标记
            if(selfPosMarker != null) {
                selfPosMarker.remove();
            }
            if(clickMarker!= null) {
                clickMarker.remove();
            }
            getLocation.LocateOnce();
        });

        // 点击地图，添加标记
        tencentMap.setOnMapClickListener(latLng -> {
            if(clickMarker != null) {
                clickMarker.remove();
            }
            clickMarker = tencentMap.addMarker(new MarkerOptions(latLng));
        });

        // 点击标记，将地图中心移动到点击的位置，并显示位置
        tencentMap.setOnMarkerClickListener(marker -> {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(marker.getPosition());
            tencentMap.moveCamera(cameraUpdate);
            marker.showInfoWindow();
            return false;
        });

        // 缩放按钮，仅测试用
        ImageButton zoomInButton = rootView.findViewById(R.id.imageButton_zoom_in);
        ImageButton zoomOutButton = rootView.findViewById(R.id.imageButton_zoom_out);
        zoomInButton.setOnClickListener(view -> {
            CameraUpdate cameraUpdate = CameraUpdateFactory.zoomIn();
            tencentMap.moveCamera(cameraUpdate);
        });

        zoomOutButton.setOnClickListener(view -> {
            CameraUpdate cameraUpdate = CameraUpdateFactory.zoomOut();
            tencentMap.moveCamera(cameraUpdate);
        });

        return rootView;
    }

    // 定位获取回调
    private class PositionGot implements GetDevPos.CallBack{
        @Override
        public void callback(TencentLocation location) {
            // 获取位置并移动地图
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 16);
            tencentMap.moveCamera(cameraUpdate);
            // 添加新标记
            MarkerOptions markerOptions = new MarkerOptions(pos);
            markerOptions.title(location.getName());
            selfPosMarker = tencentMap.addMarker(markerOptions);
            selfPosMarker.setClickable(true);
            selfPosMarker.showInfoWindow();
        }
    }

    // 销毁地图, 停止定位
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        selfPosMarker.remove();
        clickMarker.remove();
        getLocation.StopLocate();
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        getLocation.StartLocate();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        getLocation.StopLocate();
    }
}
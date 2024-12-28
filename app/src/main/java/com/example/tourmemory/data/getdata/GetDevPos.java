package com.example.tourmemory.data.getdata;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.tourmemory.R;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;


public class GetDevPos {
    // 定位监听周期
    private static final int CALLBACK_PERIOD_TIME = 2000;

    private final Context context;
    private final CallBack callBack;
    private final TencentLocationManager locationManager;
    private final TencentLocationListener locationListener;

    // 监听状态
    private boolean isRequested; // 是否需要返回位置
    private boolean isListening; // 是否正在监听

    // 回调接口
    public interface CallBack{
        void callback(TencentLocation location);
    }

    public GetDevPos(Context context, CallBack callBack_) {
        this.context = context;
        callBack = callBack_;
        locationManager = TencentLocationManager.getInstance(context);
        locationListener = new MyLocationListener();
        // 首次定位，进入定位监听
        isRequested = false;
        isListening = false;
        StartLocate();
    }

    public void LocateOnce() {
        // 单次定位，获取一次位置
        Toast.makeText(context, context.getString(R.string.locating), Toast.LENGTH_SHORT).show();
        Log.d("Tencent Map", "LocateOnce");
        isRequested = true;
    }

    public void StartLocate() {
        if(isListening)
            return;
        // 发起定位查询
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setGpsFirst(true);
        request.setInterval(CALLBACK_PERIOD_TIME);
        locationManager.requestLocationUpdates(request, locationListener);
        isListening = true;
        Log.d("Tencent Map Location", "Location Listener Initialized");
    }

    public void StopLocate() {
        // 移除监听器，停止获取定位
        locationManager.removeUpdates(locationListener);
        isListening = false;
        Log.d("Tencent Map Location", "Stop Locating");
    }

    private class MyLocationListener implements TencentLocationListener {
        private int failCount = 0;

        // 位置更新回调
        @Override
        public void onLocationChanged(TencentLocation location, int error, String reason) {
            if (error == TencentLocation.ERROR_OK && isRequested) {
                // 定位成功但位置信息未知时，返回
                if(location.getAddress() == null || location.getAddress().equals("Unknown") && failCount < 3){
                    failCount++;
                    Log.i("Tencent Map Got Unknown Location", "Location: " + location);
                    return;
                }
                // 定位成功，返回位置信息
                isRequested = false;
                failCount = 0;
                Log.i("Tencent Map Location", "Got Location: " + location);

                Toast.makeText(context, context.getString(R.string.locating_successful), Toast.LENGTH_SHORT).show();
                callBack.callback(location);
            }
            else if (!isRequested)
                Log.i("Tencent Map Ready", "Waiting for user to locate");
            else
                Log.i("Tencent Map Error", "error: " + error + ", reason: " + reason);
        }

        // 状态更新回调
        @Override
        public void onStatusUpdate(String name, int state, String desc) {
            String message = "name: " + name + ", state: " + state + ", desc: " + desc;
            Log.i("Tencent Map State", "onStatusUpdate: " + message);
        }
    }
}

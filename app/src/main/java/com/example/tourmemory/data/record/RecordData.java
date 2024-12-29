package com.example.tourmemory.data.record;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.VectorDrawable;

import com.example.tourmemory.R;
import com.example.tourmemory.data.convert.Vector2Bitmap;
import com.example.tourmemory.data.getdata.DateTimeFormatManager;
import com.example.tourmemory.data.getdata.RecordDataBank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecordData implements Serializable {
    // 用户数据
    private String date;
    private String time = "";
    private String address = "";
    private String city = "";
    private String description = "";
    private String tag = "";
    private transient ArrayList<Bitmap> bitmaps = new ArrayList<>();

    // 程序数据
    private double Latitude = 0, Longitude = 0;
    private int tagId = 0;
    private ArrayList<String > picturePathList = new ArrayList<>();

    public RecordData(Context context) {
        // 设置默认数据
        date = DateTimeFormatManager.getCurrentDateTime()[0];
        VectorDrawable vectorDrawable = (VectorDrawable) getDrawable(context, R.drawable.baseline_camera_alt_50);
        bitmaps.add(Vector2Bitmap.convertVectorToBitmap(vectorDrawable, 50, 50));
        description = context.getString(R.string.blank_content);
        tagId = -1;
    }

    public RecordData(Context context, String address, String city , String description,
                      double latitude, double longitude, ArrayList<String> picturePathList, String tag, int tagId) {
        // 创建新的记录，记录当前时间
        String[] dateTime = DateTimeFormatManager.getCurrentDateTime();
        this.date = dateTime[0];
        this.time = dateTime[1];
        this.address = isValid(address)? address: context.getString(R.string.default_address);
        this.city = isValid(city)? city: context.getString(R.string.default_city);
        this.description = isValid(description)? description: context.getString(R.string.default_description);
        Latitude = latitude;
        Longitude = longitude;
        this.picturePathList = picturePathList;
        this.tag = isValid(tag)? tag: context.getString(R.string.default_tag);
        this.tagId = tagId;

        // 根据文件路径刷新图片
        UpdatePicture(context);
    }

    public boolean isValid(String data){
        return !(data == null || data.isEmpty() || data.equals("Unknown"));
    }

    public void UpdatePicture(Context context) {
        bitmaps = new ArrayList<>();
        for (String path : picturePathList) {
            Bitmap bitmap = RecordDataBank.openFileReadBitmap(path, context);
            bitmaps.add(bitmap);
        }
    }

    // setter and getter
    public void setLatLng(double latitude, double longitude) {
        Latitude = latitude;
        Longitude = longitude;
    }

    public void getLatLng(List<Double> latLng) {
        latLng.add(Latitude);
        latLng.add(Longitude);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public Bitmap getBitmap(int i) {
        if (i >= 0 && i < bitmaps.size()) {
            return bitmaps.get(i);
        } else {
            // 处理索引越界的情况，例如返回默认值或抛出自定义异常
            return null; // 或者其他合适的处理方式
        }
    }

    public void setBitmaps(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public String getPicturePath(int i) {
        if (i >= 0 && i < picturePathList.size()) {
            return picturePathList.get(i);
        } else {
            // 处理索引越界的情况，例如返回默认值或抛出自定义异常
            return null; // 或者其他合适的处理方式
        }
    }

    public void setPicturePathList(ArrayList<String> picturePathList) {
        this.picturePathList = picturePathList;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPicCount(){
        return picturePathList.size();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

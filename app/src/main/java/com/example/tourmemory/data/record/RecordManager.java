package com.example.tourmemory.data.record;

import android.content.Context;

import com.example.tourmemory.data.getdata.RecordDataBank;

import java.util.ArrayList;

public class RecordManager{

    // 回调接口，记录变化时调用
    public interface OnRecordChangedListener{
        void onRecordChanged();                 // 清楚、加载、克隆时调用
        void onRecordChanged(int changedIndex); // 增删改时调用
    }

    // 记录变化监听器
    private OnRecordChangedListener onRecordChangedListener;
    // 数据储存
    private final ArrayList<RecordData> recordDataList;

    public RecordManager(ArrayList<RecordData> recordDataList){
        this.recordDataList = recordDataList;
        onRecordChangedListener = null;
    }

    public RecordManager(ArrayList<RecordData> recordDataList, OnRecordChangedListener onRecordChangedListener) {
        this.recordDataList = recordDataList;
        this.onRecordChangedListener = onRecordChangedListener;
    }

    public void setOnRecordChangedListener(OnRecordChangedListener onRecordChangedListener) {
        this.onRecordChangedListener = onRecordChangedListener;
    }

    public RecordData get(int index){
        return recordDataList.get(index);
    }

    // 添加方法
    public boolean add(RecordData recordData){
        recordDataList.add(recordData);
        if(onRecordChangedListener != null)
            onRecordChangedListener.onRecordChanged(size() - 1);
        return false;
    }

    // 删除方法
    public RecordData remove(int index){
        recordDataList.remove(index);
        if(onRecordChangedListener!= null)
            onRecordChangedListener.onRecordChanged(index);
        return null;
    }

    // 清楚方法
    public void clear(){
        recordDataList.clear();
        if(onRecordChangedListener!= null)
            onRecordChangedListener.onRecordChanged();
    }

    // 重设方法
    public boolean reSet(ArrayList<RecordData> recordDataList){
        clear();
        this.recordDataList.addAll(recordDataList);
        if(onRecordChangedListener!= null)
            onRecordChangedListener.onRecordChanged();
        return false;
    }

    // 大小
    public int size(){
        return recordDataList.size();
    }

    // 是否为空
    public boolean isEmpty(){
        return recordDataList.isEmpty();
    }

    public void loadFromBank(Context context){
        recordDataList.clear();
        recordDataList.addAll(RecordDataBank.LoadRecords(context));
        if(onRecordChangedListener!= null)
            onRecordChangedListener.onRecordChanged();
    }

    public void saveToBank(Context context){
        RecordDataBank.SaveRecords(context, recordDataList);
    }
}

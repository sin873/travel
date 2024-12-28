package com.example.tourmemory.data.getdata;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.example.tourmemory.data.record.RecordData;

public class RecordDataBank {
    private static final String DATA_FILE_NAME = "record_bank.data";
    private static final String PICTURE_FILE_DIR = "images";

    public static ArrayList<RecordData> LoadRecords(Context context) {
        ArrayList<RecordData> recordDataBank = new ArrayList<>();
        try{
            // 读取非资源变量
            FileInputStream fileIn = context.openFileInput(DATA_FILE_NAME);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            recordDataBank = (ArrayList<RecordData>) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            Log.d("Serialization", "LoadRecords: " + recordDataBank.size());
        } catch (FileNotFoundException e){
            Log.d("Serialization", "History record bank is empty");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // 根据储存位置打开图片
        if(recordDataBank.isEmpty())
            // 读空时生成默认记录
            recordDataBank.add(new RecordData(context));
        else{
            // 从路径读取图片
            for(int i = 0; i < recordDataBank.size(); ++i){
                RecordData record = recordDataBank.get(i);
                ArrayList<Bitmap> bitmaps = new ArrayList<>();
                for(int j = 0; j < record.getPicCount(); ++j){
                    // 打开文件读取照片
                    String fileName = record.getPicturePath(j);
                    bitmaps.add(openFileReadBitmap(fileName, context));
                }
                record.setBitmaps(bitmaps);
            }
        }
        return recordDataBank;
    }

    public static void SaveRecords(Context context, ArrayList<RecordData> recordDataBank) {
        // 储存图片
        for(int i = 0; i < recordDataBank.size(); ++i) {
            RecordData recordData = recordDataBank.get(i);
            ArrayList<String> savePath = new ArrayList<>();
            for (int j = 0; j < recordData.getPicCount(); ++j) {
                // 储存文件名称
                String imageFileName = PICTURE_FILE_DIR + i + "G" + j + ".png";
                // 将储存位置保存，下次读取的时候复用
                savePath.add(imageFileName);
                // 储存图片到指定路径
                openFileSaveBitmap(recordData.getBitmap(j), imageFileName, context);
            }
            recordData.setPicturePathList(savePath);
        }
        // 储存非资源变量
        try {
            FileOutputStream fileOut = context.openFileOutput(DATA_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(recordDataBank);
            objectOut.close();
            fileOut.close();
            Log.d("Serialization", "SaveRecords: " + recordDataBank.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap openFileReadBitmap(String fileName, Context context){
        // 从文件打开图片
        Bitmap bitmap = null;
        if(fileName.startsWith(PICTURE_FILE_DIR)){
            // 从私有空间读取图片
            try{
                FileInputStream imageIn = context.openFileInput(fileName);
                bitmap = BitmapFactory.decodeStream(imageIn);
                imageIn.close();
                Log.d("Read Image", fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            // 从文件链接读取图片
            bitmap = BitmapFactory.decodeFile(fileName);
        }
        return bitmap;
    }

    public static void openFileSaveBitmap(Bitmap bitmap, String fileName, Context context){
        // 将图片保存到私有空间，png格式
        try{
            FileOutputStream imageOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOut);
            imageOut.close();
            Log.d("Saved Image", fileName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

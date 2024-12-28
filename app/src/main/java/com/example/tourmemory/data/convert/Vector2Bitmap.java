package com.example.tourmemory.data.convert;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;

public class Vector2Bitmap {
    public static Bitmap convertVectorToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            VectorDrawable vectorDrawable = (VectorDrawable) drawable;
            // 创建一个空的Bitmap
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 创建一个Canvas来绘制Bitmap
            Canvas canvas = new Canvas(bitmap);
            // 将VectorDrawable绘制到Canvas上（它会自动缩放以适应Canvas的大小）
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        }
        return bitmap;
    }
}

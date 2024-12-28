package com.example.tourmemory.data.convert;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Uri2Path {
    public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}

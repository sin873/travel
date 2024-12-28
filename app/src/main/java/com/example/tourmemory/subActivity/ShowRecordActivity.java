package com.example.tourmemory.subActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourmemory.R;
import com.example.tourmemory.data.record.RecordData;
import com.example.tourmemory.data.getdata.DateTimeFormatManager;
import com.example.tourmemory.data.getdata.RecordDataBank;

import java.util.ArrayList;

public class ShowRecordActivity extends AppCompatActivity {
    private RecordData recordData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_record);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取当前记录
        Intent intent = getIntent();
        recordData = intent.getSerializableExtra("recordData", RecordData.class);
        assert recordData != null;
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        for (int i = 0; i < recordData.getPicCount(); ++i){
            bitmaps.add(RecordDataBank.openFileReadBitmap(recordData.getPicturePath(i), this));
        }
        recordData.setBitmaps(bitmaps);

        // 设置返回按钮
        ImageView backButton = findViewById(R.id.imageButton_context_back);
        backButton.setOnClickListener(view -> finish());

        // 记录的内容
        TextView contextView = findViewById(R.id.textView_context);
        contextView.setText(recordData.getDescription());

        // 记录的时间
        TextView timeView = findViewById(R.id.textView_date_time);
        String dateTime = DateTimeFormatManager.formatDate(this, recordData.getDate(), true) + recordData.getTime();
        timeView.setText(dateTime);

        // 记录的地点
        TextView addressView = findViewById(R.id.textView_location);
        addressView.setText(recordData.getAddress());

        // 记录的标签
        TextView tagView = findViewById(R.id.textView_load_tag);
        tagView.setText(recordData.getTag());

        // 初始化图片适配器
        RecyclerView recyclerView = findViewById(R.id.recyclerView_show_pictures);
        ImageAdapter imageAdapter = new ImageAdapter();
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void showPicture(int position) {
        // 弹出显示图片的视图
        LayoutInflater inflater = LayoutInflater.from(this);
        View pictureView = inflater.inflate(R.layout.dialog_show_picture, null);

        // 获取图片视图
        ImageView pictureImageView = pictureView.findViewById(R.id.imageView_show_picture);
        pictureImageView.setImageBitmap(recordData.getBitmap(position));

        // 创建并显示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(pictureView)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 图片适配器
    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private class ImageViewHolder extends RecyclerView.ViewHolder {
            private final ImageView imageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView_picture_context);
                // 设置点击事件
                itemView.setOnClickListener(view -> {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        showPicture(position);
                    }
                });
            }
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_show_image_context, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            holder.imageView.setImageBitmap(recordData.getBitmap(position));
        }

        @Override
        public int getItemCount() {
            return recordData.getPicCount();
        }
    }
}
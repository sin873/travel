package com.example.tourmemory.subActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

public class AddRecordActivity extends AppCompatActivity {
    // 控件监听消息定义表
    public static final int CANCEL = 0;
    public static final int ADD = 1;

    // 用户数据
    private final ArrayList<String> fileList = new ArrayList<>();
    private final ArrayList<Bitmap> bitmapList = new ArrayList<>();
    private String description = "";
    private String address = "";
    private String city = "";
    private double latitude, longitude;
    private String tag = "";

    TextInputEditText descriptionEditText;
    private static final int PICK_IMAGE_REQUEST = 1;

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    // 注册一个用于选择位置的活动结果启动器
    private final ActivityResultLauncher<Intent> addRecord =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
                // 回调函数，处理获取的位置
                Intent data = result.getData();
                if(data == null || data.getIntExtra("sate", CANCEL) == CANCEL)
                    return;
                latitude = data.getDoubleExtra("Latitude", 0);
                longitude = data.getDoubleExtra("Longitude", 0);
                address = data.getStringExtra("Address");
                city = data.getStringExtra("City");

                // 更新显示
                ((TextView)findViewById(R.id.textView_postion)).setText(address);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_record);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取传递过来的文件数量
        int imageCounts = getIntent().getIntExtra("imageCounts", 0);
        for (int i = 0; i < imageCounts; ++i) {
            // 获取传递过来的文件路径
            String realUri = getIntent().getStringExtra("" + i);
            Bitmap bitmap = BitmapFactory.decodeFile(realUri);
            // 更新文件列表
            bitmapList.add(bitmap);
            fileList.add(realUri);
        }

        // 设置返回按钮的点击事件
        ImageButton backButton = findViewById(R.id.imageButton_back);
        backButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("sate", CANCEL);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // 设置添加按钮的点击事件
        Button addButton = findViewById(R.id.button_add_record);
        addButton.setOnClickListener(v -> {
            // 获取输入
            description = Objects.requireNonNull(descriptionEditText.getText()).toString();
            // 创建记录数据
            RecordData recordData = new RecordData(this, address, city, description,
                    latitude, longitude, fileList, tag, 0);
            // 返回新增的记录
            Intent resultIntent = new Intent();
            resultIntent.putExtra("sate", ADD);
            resultIntent.putExtra("recordData", recordData);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // 获取输入框的内容
        descriptionEditText = findViewById(R.id.editText_description);
        descriptionEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                description = Objects.requireNonNull(descriptionEditText.getText()).toString();
            }
        });

        // 点击按钮获取定位信息
        ImageButton location = findViewById(R.id.imageButton_add_loc);
        location.setOnClickListener(view -> addRecord.launch(new Intent(this, LocationActivity.class)));

        // 点击按钮添加TAG
        ImageButton addTag = findViewById(R.id.imageButton_add_tag);
        addTag.setOnClickListener(view -> showInputDialog());

        // 初始化recyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView_images);
        // 控件数据
        ImageAdapter imageAdapter = new ImageAdapter();
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放图片资源
        for (Bitmap bitmap : bitmapList) {
            bitmap.recycle();
        }
        bitmapList.clear();
        fileList.clear();
    }

    private void showInputDialog() {
        // 弹出对话框，让用户输入图片描述
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        // 获取对话框中的控件
        TextInputEditText inputEditText = dialogView.findViewById(R.id.editText_input_tag);

        // 创建对话框构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                    // 获取用户输入的描述
                    tag = Objects.requireNonNull(inputEditText.getText()).toString();
                    // 更新显示
                    ((TextView)findViewById(R.id.textView_tag)).setText(tag);
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 图片适配器
    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private class ImageViewHolder extends RecyclerView.ViewHolder {
            private final ImageView imageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView_to_add);
            }
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_show_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            holder.imageView.setImageBitmap(bitmapList.get(position));
        }

        @Override
        public int getItemCount() {
            return bitmapList.size();
        }
    }
}
package com.example.tourmemory.mainActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tourmemory.R;
import com.example.tourmemory.data.record.RecordData;
import com.example.tourmemory.data.convert.Uri2Path;
import com.example.tourmemory.data.getdata.DateTimeFormatManager;
import com.example.tourmemory.data.record.RecordManager;
import com.example.tourmemory.subActivity.AddRecordActivity;
import com.example.tourmemory.subActivity.ShowRecordActivity;

public class HomeFragment extends Fragment {
    // 数据
    private RecordManager recordManager;

    // 控件
    private RcdViewManager rcdViewManager;

    // 注册一个用于添加记录的活动结果启动器
    private final ActivityResultLauncher<Intent> addRecord =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
                // 回调函数，处理新增的记录
                Intent data = result.getData();
                if(data == null || data.getIntExtra("sate", AddRecordActivity.CANCEL) == AddRecordActivity.CANCEL)
                    return;

                // 获取新增的记录，并添加到列表中
                RecordData recordData = data.getSerializableExtra("recordData", RecordData.class);
                assert recordData != null;
                recordData.UpdatePicture(requireContext());
                rcdViewManager.addRecord(recordData);

                // 若新增的记录为第一个记录，则删除默认记录
                if(recordManager.get(0).getTagId() == -1)
                    rcdViewManager.removeRecord(0);

                // 保存新的数据, 已在监听器中实现
                //recordManager.saveToBank(requireContext());
            });

    // 注册一个用于选择媒体文件的活动结果启动器
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(9), uris -> {
                // 回调函数，处理选择的媒体文件
                if (!uris.isEmpty()) {
                    Log.d("PhotoPicker", "Number of items selected: " + uris.size());
                    // 启动记录活动的意图，将文件路径传递给新增记录的活动
                    Intent intent = new Intent(requireActivity(), AddRecordActivity.class);
                    intent.putExtra("imageCounts", uris.size()); // 传递文件数量

                    for (int i = 0; i < uris.size(); ++i) {
                        String realUri = Uri2Path.getRealPathFromURI(requireActivity(), uris.get(i));// 获取文件的真实路径
                        intent.putExtra("" + i, realUri);         // 传递文件路径(uri)
                    }
                    // 启动新增记录的活动
                    addRecord.launch(intent);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    public HomeFragment(RecordManager recordManager) {
        this.recordManager = recordManager;
    }

    // 启动添加记录的活动
    public void StartAddRecordAct(){
        pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    // 启动查看记录的活动
    public void StartShowRecordAct(int position){
        Intent intent = new Intent(requireActivity(), ShowRecordActivity.class);
        intent.putExtra("recordData", recordManager.get(position));
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 初始化视图和控件
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);

        // 创建RcdViewManager实例
        rcdViewManager = new RcdViewManager(rootView, R.id.recyclerView);

        return rootView;
    }

    // 打卡记录视图管理
    public class RcdViewManager {
        private final RecordViewsAdapter recordViewsAdapter;

        public RcdViewManager(View rootView, int layoutId) {
            // 初始化数据
            if (recordManager.isEmpty())
                recordManager.add(new RecordData(rootView.getContext()));
            RecyclerView recyclerView = rootView.findViewById(layoutId);
            recordViewsAdapter = new RecordViewsAdapter();
            // 设置适配器
            recyclerView.setAdapter(recordViewsAdapter);
            // 设置布局管理器
            recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        }

        private class RecordViewHolder extends RecyclerView.ViewHolder {
            public TextView timeTextView;
            public TextView cityTextView;
            public TextView descripTextView;
            public TextView tagTextView;
            public ImageView pictureImageView;

            public RecordViewHolder(@NonNull View itemView) {
                super(itemView);
                // 将视图绑定到ViewHolder的成员变量
                timeTextView = itemView.findViewById(R.id.textView_time);
                descripTextView = itemView.findViewById(R.id.textView_description);
                pictureImageView = itemView.findViewById(R.id.imageView_picture);
                cityTextView = itemView.findViewById(R.id.textView_city);
                tagTextView = itemView.findViewById(R.id.textView_tag);

                // 设置点击事件，点击图片或者描述时启动查看记录的活动
                pictureImageView.setOnClickListener(view -> {
                    // 处理点击事件
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // 启动查看记录的活动
                        StartShowRecordAct(position);
                    }
                });
                descripTextView.setOnClickListener(view -> {
                    // 处理点击事件
                    int position = getBindingAdapterPosition();
                    if (position!= RecyclerView.NO_POSITION) {
                        // 启动查看记录的活动
                        StartShowRecordAct(position);
                    }
                });
            }
        }

        private class RecordViewsAdapter extends RecyclerView.Adapter<RecordViewHolder> {
            @NonNull
            @Override
            public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // 创建视图持有者
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_record, parent, false);
                return new RecordViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
                // 绑定数据到视图持有者
                RecordData recordData = recordManager.get(position);
                String date = DateTimeFormatManager.formatDate(requireContext(), recordData.getDate(), true);
                holder.timeTextView.setText(date);
                holder.descripTextView.setText(recordData.getDescription());
                holder.pictureImageView.setImageBitmap(recordData.getBitmap(0));
                holder.cityTextView.setText(recordData.getCity());
                holder.tagTextView.setText(recordData.getTag());
            }

            @Override
            public int getItemCount() {
                return recordManager.size();
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        public void addRecord(RecordData recordData) {
            recordManager.add(recordData);
            recordViewsAdapter.notifyItemInserted(recordManager.size() - 1);
        }

        public void removeRecord(int position) {
            recordManager.remove(position);
            recordViewsAdapter.notifyItemRemoved(position);
        }
    }
}


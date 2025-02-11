package dev.lyg.cp.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dev.lyg.cp.R;
import dev.lyg.cp.ui.fragment.LeaveFragment;

public class NoteFragment extends Fragment {
    private ImageView qrCode;
    private ImageView leftTrainImage;
    private TextView showTicketsBtn;

    public NoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        qrCode = view.findViewById(R.id.qrCode);
        leftTrainImage = view.findViewById(R.id.left_train);

        showTicketsBtn = view.findViewById(R.id.showTicketsBtn);

        loadImage("https://tgimgbed-f3l.pages.dev/file/1738663611167_httc2.png", qrCode);
        loadImage("https://tgimgbed-f3l.pages.dev/file/1738663622090_spxs2.png", leftTrainImage);

        // 设置点击事件
        showTicketsBtn.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content, new LeaveFragment());
            // 这里的 R.id.main_container 是 MainActivity 里的 Fragment 容器
            transaction.addToBackStack(null); // 添加返回栈，支持返回上一个 Fragment
            transaction.commit();
        });
    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void loadImage(String imageUrl, ImageView imageView) {
        // 检查是否有网络连接
        if (!isNetworkAvailable(getContext())) {
            // 如果没有网络，显示默认图片或错误提示
            imageView.setImageResource(R.drawable.barcode); // 设置没有网络时显示的图片
            Toast.makeText(getContext(), "没有网络连接", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                // 在加载图片之前，检查网络连接
                if (!isNetworkAvailable(getContext())) {
                    // 如果没有网络连接，可以显示占位图或者给出提示
                    getActivity().runOnUiThread(() -> {
                        imageView.setImageResource(R.drawable.barcode); // 显示占位图
                        Toast.makeText(getContext(), "没有网络可用", Toast.LENGTH_SHORT).show(); // 提示用户没有网络
                    });
                    return;
                }

                // 创建 URL 对象
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                // 获取输入流并解析图片
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // 在主线程中更新 UI
                getActivity().runOnUiThread(() -> {
                    imageView.setImageBitmap(bitmap);
                    imageView.setPadding(0, 0, 0, 0);
                });
            } catch (IOException e) {
                e.printStackTrace();
                // 网络请求失败时的处理，可以显示错误提示或占位图
                getActivity().runOnUiThread(() -> {
                    imageView.setImageResource(R.drawable.barcode); // 显示错误图
                    Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show(); // 提示用户加载失败
                });
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note, container, false);
    }
}
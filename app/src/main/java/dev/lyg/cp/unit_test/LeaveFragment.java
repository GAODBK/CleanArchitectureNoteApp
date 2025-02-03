package dev.lyg.cp.unit_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dev.lyg.cp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaveFragment extends Fragment {
    private ImageView imageView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LeaveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeaveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaveFragment newInstance(String param1, String param2) {
        LeaveFragment fragment = new LeaveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.qrCode);
        loadImage("https://raw.githubusercontent.com/GAODBK/lyg-photo-warehouse/refs/heads/main/1%20(23).jpeg");
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void loadImage(String imageUrl) {
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
                getActivity().runOnUiThread(() -> imageView.setImageBitmap(bitmap));
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
        return inflater.inflate(R.layout.fragment_leave, container, false);
    }
}
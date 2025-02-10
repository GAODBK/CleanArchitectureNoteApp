package dev.lyg.cp.unit_test;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dev.lyg.cp.R;

public class LeaveFragment extends Fragment {
    TextView leaveTicketsBtn;

    public LeaveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leaveTicketsBtn = view.findViewById(R.id.leaveTicketsBtn);

        // 设置点击事件
        leaveTicketsBtn.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content, new NoteFragment());
            // 这里的 R.id.main_container 是 MainActivity 里的 Fragment 容器
            transaction.addToBackStack(null); // 添加返回栈，支持返回上一个 Fragment
            transaction.commit();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leave, container, false);
    }
}
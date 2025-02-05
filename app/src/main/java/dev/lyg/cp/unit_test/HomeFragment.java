package dev.lyg.cp.unit_test;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dev.lyg.cp.R;
import dev.lyg.cp.util.DBHelper;
import dev.lyg.cp.util.NoteAdapter;
import dev.lyg.cp.util.Ticket;
import dev.lyg.cp.stacklib.StackLayout;

public class HomeFragment extends Fragment {
    private StackLayout stackLayout;
    private RecyclerView recyclerView;
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private DBHelper dbHelper;
    private NoteAdapter noteAdapter;

    public HomeFragment() {
        // 必需的空公共构造函数
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        stackLayout = view.findViewById(R.id.stacklayout);

        initStackView();

        recyclerView = view.findViewById(R.id.recyclerView);
        dbHelper = new DBHelper(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        noteAdapter = new NoteAdapter(requireContext(), tickets);
        recyclerView.setAdapter(noteAdapter);

        loadTicketsFromDatabase(); // 确保此方法在适配器设置之后调用

        return view;
    }

    public void loadTicketsFromDatabase() {
        Cursor cursor = null;
        try {
            cursor = dbHelper.showData();
            tickets.clear();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int idIndex = cursor.getColumnIndex("id");
                    int trainNumberIndex = cursor.getColumnIndex("train_number");
                    int departureDateIndex = cursor.getColumnIndex("departure_date");
                    int departureTimeIndex = cursor.getColumnIndex("departure_time");
                    int arrivalTimeIndex = cursor.getColumnIndex("arrival_time");
                    int departureStationIndex = cursor.getColumnIndex("departure_station");
                    int arrivalStationIndex = cursor.getColumnIndex("arrival_station");
                    int checkInGateIndex = cursor.getColumnIndex("check_in_gate");
                    int seatNumberIndex = cursor.getColumnIndex("seat_number");
                    int remark1Index = cursor.getColumnIndex("remark1");
                    int remark2Index = cursor.getColumnIndex("remark2");
                    int remark3Index = cursor.getColumnIndex("remark3");
                    int remark4Index = cursor.getColumnIndex("remark4");

                    if (idIndex == -1 || trainNumberIndex == -1 || departureDateIndex == -1 ||
                            departureTimeIndex == -1 || arrivalTimeIndex == -1 || departureStationIndex == -1 ||
                            arrivalStationIndex == -1 || checkInGateIndex == -1 || seatNumberIndex == -1) {
                        Log.e("HomeFragment", "列索引无效");
                        return;
                    }

                    tickets.add(new Ticket(
                            cursor.getInt(idIndex),
                            cursor.getString(trainNumberIndex),
                            cursor.getString(departureDateIndex),
                            cursor.getString(departureTimeIndex),
                            cursor.getString(arrivalTimeIndex),
                            cursor.getString(departureStationIndex),
                            cursor.getString(arrivalStationIndex),
                            cursor.getString(checkInGateIndex),
                            cursor.getString(seatNumberIndex),
                            cursor.getString(remark1Index),
                            cursor.getString(remark2Index),
                            cursor.getString(remark3Index),
                            cursor.getString(remark4Index)
                    ));
                }
            }
        } catch (Exception e) {
            Log.e("HomeFragment", "从数据库加载票证时出错", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private List<String> generateList() {
        List<String> retList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            retList.add("item : " + i);
        }
        return retList;
    }

    private void initStackView() {
        stackLayout.nick = "first stacklayout";
        List<String> datas = generateList();
        stackLayout.setAdapter(new MyAdapter(datas));
        stackLayout.setStatus(StackLayout.COLLAPSE);//折叠
        //stackLayout.setStatus(StackLayout.COLLAPSE);//展开
    }

    class MyAdapter extends StackLayout.Adapter<MyAdapter.CustomViewHolder> {
        private List<String> datas;

        public MyAdapter(List<String> datas) {
            this.datas = datas;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new CustomViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            holder.bindViews(position);
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.item;
        }

        @Override
        public int getItemCount() {
            return this.datas.size();
        }

        class CustomViewHolder extends StackLayout.ViewHolder {
            private final View itemLLt;
            private final TextView tv;
            private final MyAdapter adapter;

            public CustomViewHolder(View itemView, MyAdapter adapter) {
                super(itemView);
                this.adapter = adapter;
                itemLLt = itemView.findViewById(R.id.item_llt);
                tv = itemView.findViewById(R.id.tv);
            }

            public void bindViews(final int position) {
                tv.setText(adapter.datas.get(position));
                itemLLt.setOnClickListener(v -> {
                    if (position == 0) {
                        adapter.getView().switchStatus();
                    } else {
                        Toast.makeText(getContext(), "点击了" + position, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}

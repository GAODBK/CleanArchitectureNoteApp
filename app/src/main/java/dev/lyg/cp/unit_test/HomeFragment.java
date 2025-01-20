package dev.lyg.cp.unit_test;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import dev.lyg.cp.R;
import dev.lyg.cp.util.DBHelper;
import dev.lyg.cp.util.NoteAdapter;
import dev.lyg.cp.util.Ticket;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private DBHelper dbHelper;
    private NoteAdapter noteAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        dbHelper = new DBHelper(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        noteAdapter = new NoteAdapter(requireContext(), tickets);
        recyclerView.setAdapter(noteAdapter);

        loadTicketsFromDatabase(); // 确保此方法在适配器设置之后调用

        return view;
    }

    @SuppressLint({"Range", "NotifyDataSetChanged"})
    private void loadTicketsFromDatabase() {
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

                    // 检查列索引是否有效
                    if (idIndex == -1 || trainNumberIndex == -1 || departureDateIndex == -1 ||
                            departureTimeIndex == -1 || arrivalTimeIndex == -1 || departureStationIndex == -1 ||
                            arrivalStationIndex == -1 || checkInGateIndex == -1 || seatNumberIndex == -1 ||
                            remark1Index == -1 || remark2Index == -1 || remark3Index == -1 || remark4Index == -1) {
                        Log.e("HomeFragment", "Invalid column index");
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
            Log.e("HomeFragment", "Error loading tickets from database", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        noteAdapter.notifyDataSetChanged(); // 更新数据
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}

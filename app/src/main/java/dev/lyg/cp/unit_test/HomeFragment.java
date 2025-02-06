package dev.lyg.cp.unit_test;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dev.lyg.cp.R;
import dev.lyg.cp.util.DBHelper;
import dev.lyg.cp.util.NoteAdapter;
import dev.lyg.cp.util.Ticket;
import dev.lyg.cp.stacklib.StackLayout;

public class HomeFragment extends Fragment {
    private StackLayout stackLayout;
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private DBHelper dbHelper;
    private NoteAdapter noteAdapter;
    private TextView viewAll;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        stackLayout = view.findViewById(R.id.stacklayout);
        viewAll = view.findViewById(R.id.viewAll);
        dbHelper = new DBHelper(requireContext());

        noteAdapter = new NoteAdapter(requireContext(), tickets);
        stackLayout.setAdapter(noteAdapter);
        stackLayout.setStatus(StackLayout.COLLAPSE); // 默认折叠

        loadTicketsFromDatabase(); // 先加载数据

        viewAll.setOnClickListener(v -> stackLayout.switchStatus());
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
            Log.e("HomeFragment", "数据库加载错误", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        noteAdapter.notifyChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}

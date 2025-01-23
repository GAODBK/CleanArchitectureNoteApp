package dev.lyg.cp.unit_test;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Locale;

import dev.lyg.cp.R;
import dev.lyg.cp.util.DBHelper;

public class SearchFragment extends Fragment {

    DBHelper dbHelper;
    ImageView leftImage, rightImage, dateImage;
    EditText leftEditText, rightEditText, dateEditText, departureStationEditText, arrivalStationEditText, trainnNumberEditText, checkEditText, seatEditText, remark1EditText, remark2EditText, remark3EditText, remark4EditText;
    MaterialButton btn_add;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearhFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_searh, container, false);
    }

    @SuppressLint("CutPasteId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DBHelper(SearchFragment.this.getContext());

        btn_add = view.findViewById(R.id.btn_add);

        leftImage = view.findViewById(R.id.departureTime);
        rightImage = view.findViewById(R.id.arrivalTime);
        dateImage = view.findViewById(R.id.dateImage);

        leftEditText = view.findViewById(R.id.departureTimeEditText);
        rightEditText = view.findViewById(R.id.arrivalTimeEditText);
        dateEditText = view.findViewById(R.id.dateEdit);

        trainnNumberEditText = view.findViewById(R.id.train_number);
        departureStationEditText = view.findViewById(R.id.departureSiteEditText);
        arrivalStationEditText = view.findViewById(R.id.arrivalSiteEditText);
        checkEditText = view.findViewById(R.id.check_in_gate);
        seatEditText = view.findViewById(R.id.seat_number);

        remark1EditText = view.findViewById(R.id.remark1);
        remark2EditText = view.findViewById(R.id.remark2);
        remark3EditText = view.findViewById(R.id.remark3);
        remark4EditText = view.findViewById(R.id.remark4);

        dateImage.setOnClickListener(v -> showDatePicker());
        rightImage.setOnClickListener(v -> showTimePicker(rightEditText));
        leftImage.setOnClickListener(v -> showTimePicker(leftEditText));

        btn_add.setOnClickListener(v -> {

            if (leftEditText.length() > 0 && rightEditText.length() > 0 &&
                    dateEditText.length() > 0 && trainnNumberEditText.length() > 0 &&
                    checkEditText.length() > 0 && seatEditText.length() > 0 &&
                    departureStationEditText.length() > 0 && arrivalStationEditText.length() > 0
            ) {

                long id = dbHelper.insertTicket(

                        trainnNumberEditText.getText().toString(),
                        dateEditText.getText().toString(),
                        leftEditText.getText().toString(),
                        rightEditText.getText().toString(),
                        departureStationEditText.getText().toString(),
                        arrivalStationEditText.getText().toString(),
                        checkEditText.getText().toString(),
                        seatEditText.getText().toString(),
                        remark1EditText.getText().toString(),
                        remark2EditText.getText().toString(),
                        remark3EditText.getText().toString(),
                        remark4EditText.getText().toString()
                );
                if (id > 0) {
                    Log.d("Database", "数据添加成功，ID: " + id);
                    Toast.makeText(
                            SearchFragment.this.getContext(),
                            "数据添加成功",
                            Toast.LENGTH_SHORT
                    ).show();
                    clearAllFields();
                } else {
                    Log.e("Database", "工单插入失败，原因：" + trainnNumberEditText.getText().toString());
                    Toast.makeText(
                            SearchFragment.this.getContext(),
                            "数据添加失败",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            } else if (leftEditText.length() == 0 || rightEditText.length() == 0 ||
                    dateEditText.length() == 0 || trainnNumberEditText.length() == 0 ||
                    departureStationEditText.length() == 0 || arrivalStationEditText.length() == 0) {
                Toast.makeText(requireContext(), "请填写所有必填字段", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }


    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    String formattedDate = new SimpleDateFormat(
                            "MM月dd日",
                            Locale.getDefault()).format(selectedDate.getTime()
                    );
                    dateEditText.setText(formattedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }


    private void showTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, selectedHour, selectedMinute) -> {

                    String formattedTime = String.format(
                            Locale.getDefault(),
                            "%02d:%02d", selectedHour,
                            selectedMinute
                    );
                    editText.setText(formattedTime);
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }


    private void clearAllFields() {
        leftEditText.setText("");
        rightEditText.setText("");
        dateEditText.setText("");
        trainnNumberEditText.setText("");
        departureStationEditText.setText("");
        arrivalStationEditText.setText("");
        checkEditText.setText("");
        seatEditText.setText("");
        remark1EditText.setText("");
        remark2EditText.setText("");
        remark3EditText.setText("");
        remark4EditText.setText("");
    }
}
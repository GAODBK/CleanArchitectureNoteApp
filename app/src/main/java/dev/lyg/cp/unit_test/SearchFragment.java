package dev.lyg.cp.unit_test;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.lyg.cp.R;
import dev.lyg.cp.alert.ViewLoading;
import dev.lyg.cp.alert.WheelView;
import dev.lyg.cp.util.DBHelper;
import dev.lyg.cp.util.FormatUtils;
import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class SearchFragment extends Fragment {
    private LinearLayout dateImage, leftImage, rightImage;
    private DBHelper dbHelper;
    private EditText trainnNumberEditText, seatEditText, remark1EditText, remark2EditText, remark3EditText, remark4EditText;
    private MaterialButton btn_add;
    private TextView dateEditText, rightEditText, leftEditText, departureStationEditText, arrivalStationEditText, checkEditText;
    private boolean isRequestInProgress = false;
    // 修改成员变量声明
    private int index1 = -1; // selectView1 的索引
    private int index2 = -1; // selectView2 的索引
    private List<String> menuItems1 = new ArrayList<>(); // selectView1 的数据
    private List<String> menuItems2 = new ArrayList<>(); // selectView2 的数据
    private List<String> stationNameItems1 = new ArrayList<>();
    private List<String> stationNameItems2 = new ArrayList<>();
    private List<String> arriveTimeItems1 = new ArrayList<>();
    private List<String> arriveTimeItems2 = new ArrayList<>();
    private List<String> startTimeItems1 = new ArrayList<>();
    private List<String> startTimeItems2 = new ArrayList<>();
    private String[] selectedItemCopy = {"", "", "", ""}; // 被选中站点名，列车到达站点时间，列车发车时间，列车下一站到达时间

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_searh, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
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

        getTrainNo(departureStationEditText, 1);
        getTrainNo(arrivalStationEditText, 2);

        btn_add.setOnClickListener(v -> {

            // String trainNo = getArguments().getString("trainNo");
            // int id = Intent.getIntentOld().getIntExtra("id", 0);

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
                    Log.d("Database",
                            "工单插入失败，原因：" + trainnNumberEditText.getText().toString()
                    );
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

    private void showTicketGate() {
        if (trainnNumberEditText.getText().length() == 0 || dateEditText.getText().length() == 0) {
            Toast.makeText(requireContext(), "请先填写车次和日期", Toast.LENGTH_SHORT).show();
            return;
        }
        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient();

        // 构造请求 URL
        String url = "https://mobile.12306.cn/weixin/wxcore/getPlatform" +
                "?trainCode=" + trainnNumberEditText.getText().toString() +
                "&stationName=" + departureStationEditText.getText().toString() +
                "&stationCode=NIW&date=" + dateEditText.getText().toString();

        // 创建请求对象
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 发送请求并异步获取响应
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Request", "网络请求失败: " + e.getMessage(), e);
                getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "请求失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    try {
                        // 解析响应 JSON
                        String responseBody = response.body().string();

                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // 提取检票口信息
                        JSONObject data = jsonResponse.getJSONObject("data");
                        String result = data.getString("result");
                        Log.d("Request", "Network response: " + result);

                        // 使用正则提取检票口信息（15A、15B）
                        String ticketGateInfo = FormatUtils.extractTicketGateInfo(result);

                        // 在 UI 线程更新 TextView 显示检票口信息
                        getActivity().runOnUiThread(() -> checkEditText.setText(ticketGateInfo));
                    } catch (Exception e) {
                        Log.d("Request2", "解析数据失败" + e);
                        getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "解析数据失败", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e("Request", "响应中的错误: " + response.code());
                    getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "请求失败，状态码" + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void getTrainNo(TextView selectView, int lor) {

        selectView.setOnClickListener(v -> {
            if (trainnNumberEditText.getText().length() == 0 || dateEditText.getText().length() == 0) {
                Toast.makeText(requireContext(), "请先填写车次和日期", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("getTrainNo: ", trainnNumberEditText.getText() + " " + FormatUtils.convertDateToSimpleFormat(dateEditText.getText().toString()));

            String trainCode = trainnNumberEditText.getText().toString(); //"K1247";
            String date = FormatUtils.convertDateToSimpleFormat(dateEditText.getText().toString());//"20250212";

            OkHttpClient client = new OkHttpClient();

            // Step 1: 获取 train_no
            String url1 = "https://search.12306.cn/search/v1/train/search?keyword=" + trainCode + "&date=" + date;
            Request request1 = new Request.Builder().url(url1).build();

            client.newCall(request1).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("TrainData", "获取 train_no 失败", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        JsonObject json = new Gson().fromJson(responseBody, JsonObject.class);

                        if (json != null && json.has("data")) {
                            JsonArray dataArray = json.getAsJsonArray("data");
                            if (dataArray.size() > 0) {
                                JsonObject trainData = dataArray.get(0).getAsJsonObject();
                                String trainNo = trainData.get("train_no").getAsString();
                                String trainDate = trainData.get("date").getAsString();
                                Log.d("TrainData", "trainNo: " + trainNo + " trainDate: " + trainDate);

                                demonstrateStationStation(trainNo, trainDate, selectView, client, lor);
                            }
                        }
                    }
                }
            });
        });
    }

    private void demonstrateStationStation(String trainNo, String trainDate, TextView selectView, OkHttpClient client, int lor) {
        if (isRequestInProgress) return; // 如果已经进行了，请防止进一步的要求

        if (trainNo.length() == 0 || trainDate.length() == 0) {
            Toast.makeText(requireContext(), "请输入车次和日期", Toast.LENGTH_SHORT).show();
            return;
        }

        isRequestInProgress = true;

        // 重置当前LOR的列表数据
        if (lor == 1) {
            menuItems1.clear();
            stationNameItems1.clear();
            arriveTimeItems1.clear();
            startTimeItems1.clear();
        } else {
            menuItems2.clear();
            stationNameItems2.clear();
            arriveTimeItems2.clear();
            startTimeItems2.clear();
        }

        // Format the date
        String formattedDate = FormatUtils.convertSimpleDateToDateFormat(trainDate);

        String url2 = "https://kyfw.12306.cn/otn/queryTrainInfo/query?leftTicketDTO.train_no=" +
                trainNo + "&leftTicketDTO.train_date=" + formattedDate + "&rand_code=";
        Request request2 = new Request.Builder().url(url2).build();

        client.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isRequestInProgress = false; // Mark as complete
                Log.e("Request2", "获取站点信息失败", e);
                getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "请求失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                isRequestInProgress = false; // Mark as complete

                if (!response.isSuccessful()) {
                    Log.e("Request2", "请求失败，状态码：" + response.code());
                    return;
                }

                String responseBody = response.body().string();
                Log.d("Response2", responseBody);

                try {
                    JsonObject json = new Gson().fromJson(responseBody, JsonObject.class);
                    if (json != null && json.has("data")) {
                        JsonArray stationDataArray = json
                                .getAsJsonObject("data")
                                .getAsJsonArray("data");

                        if (stationDataArray != null) {
                            int startIndex = (lor == 1) ? 0 : (index1 + 1);

                            for (int i = startIndex; i < stationDataArray.size(); i++) {
                                JsonObject station = stationDataArray.get(i).getAsJsonObject();
                                String stationName = station.get("station_name").getAsString();
                                String arriveTime = station.get("arrive_time").getAsString();
                                String startTime = station.get("start_time").getAsString();

                                if (lor == 1) {
                                    stationNameItems1.add(stationName);
                                    startTimeItems1.add(startTime);
                                    arriveTimeItems1.add(arriveTime);
                                    menuItems1.add(String.format("%s %s到站 - %s发车", stationName, arriveTime, startTime));
                                } else {
                                    stationNameItems2.add(stationName);
                                    startTimeItems2.add(startTime);
                                    arriveTimeItems2.add(arriveTime);
                                    menuItems2.add(String.format("%s %s到站 - %s发车", stationName, arriveTime, startTime));
                                }
                            }

                            getActivity().runOnUiThread(() -> showScrollableMenu(selectView, lor));
                        }
                    }
                } catch (Exception e) {
                    Log.e("Request2", "解析 JSON 数据失败", e);
                }
            }
        });
    }

    private void showScrollableMenu(TextView selectView, int lor) {
        ViewLoading.dismiss(getContext());

        // 加载自定义弹窗布局
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_edit_dialog, null);
        WheelView wv = dialogView.findViewById(R.id.wheel_view_wv);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);
        TextView tvEnsure = dialogView.findViewById(R.id.tv_ensure);

        // 设置数据
        List<String> currentMenuItems = (lor == 1) ? menuItems1 : menuItems2;
        List<String> currentStationNames = (lor == 1) ? stationNameItems1 : stationNameItems2;
        List<String> currentArriveTime = (lor == 1) ? arriveTimeItems1 : arriveTimeItems2;
        List<String> currentStartTime = (lor == 1) ? startTimeItems1 : startTimeItems2;

        //判断currentMenuItems为空
        if (currentMenuItems.size() == 0) {
            Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_SHORT).show();
            return;
        }
        wv.setItems(currentMenuItems);

        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                if (lor == 1) {
                    index1 = selectedIndex - 2;
                    selectedItemCopy[0] = currentStationNames.get(selectedIndex - 2);
                    selectedItemCopy[2] = currentStartTime.get(selectedIndex - 2);
                } else {
                    selectedItemCopy[0] = currentStationNames.get(selectedIndex - 2);
                    selectedItemCopy[1] = currentArriveTime.get(selectedIndex - 2);
                }
            }
        });

        // 创建 AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_cart_et_balance);


        // 取消按钮
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        // 确认按钮
        tvEnsure.setOnClickListener(v -> {
            if (selectedItemCopy[0] != null) {
                selectView.setText(selectedItemCopy[0]);
                if (lor == 1) {
                    leftEditText.setText(selectedItemCopy[2]);
                } else if (lor == 2) {
                    rightEditText.setText(selectedItemCopy[1]);
                }
                showTicketGate();
            } else {
                Log.d("ScrollableMenu", "未选择任何项");
            }
            dialog.dismiss();
        });

        // 显示对话框
        dialog.show();
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
                            "yyyy-MM-dd",
                            Locale.getDefault()).format(selectedDate.getTime()
                    );
                    dateEditText.setText(formattedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void showTimePicker(TextView editText) {
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
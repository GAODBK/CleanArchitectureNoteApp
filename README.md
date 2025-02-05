```java
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

                    // 检查列索引是否有效
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
        noteAdapter.notifyDataSetChanged(); // 更新数据
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
        for (int i = 0; i < 20; i++) {
            retList.add("item : " + i);
        }
        return retList;
    }

    private void initStackView() {
        stackLayout.nick = "first stacklayout";
        List<String> datas = generateList();
        stackLayout.setAdapter(new MyAdapter(datas));
        stackLayout.setStatus(StackLayout.COLLAPSE);
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

```

# 1. 安卓 Spinner 填充数据

在安卓开发中，可以通过以下步骤将接口返回的数据解析并填充到 `Spinner` 组件中。

---

### **1. 发起网络请求**
使用 `OkHttp` 或 `Retrofit` 库进行网络请求，以下是 `Retrofit` 示例代码。

#### 添加 Retrofit 依赖
在 `build.gradle` 中添加依赖：
```gradle
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
```

#### 定义接口服务
```java
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TrainService {
    @GET("search/v1/train/search")
    Call<TrainResponse> getTrainData(@Query("keyword") String keyword, @Query("date") String date);

    @GET("otn/queryTrainInfo/query")
    Call<TrainInfoResponse> getTrainInfo(@Query("leftTicketDTO.train_no") String trainNo,
                                         @Query("leftTicketDTO.train_date") String trainDate,
                                         @Query("rand_code") String randCode);
}
```

---

### **2. 定义数据模型**
根据返回的 JSON 数据，创建对应的 Java 类。

#### TrainResponse
```java
import java.util.List;

public class TrainResponse {
    public List<TrainData> data;
    public boolean status;
    public String errorMsg;

    public static class TrainData {
        public String date;
        public String from_station;
        public String station_train_code;
        public String to_station;
        public String total_num;
        public String train_no;
    }
}
```

#### TrainInfoResponse
```java
import java.util.List;

public class TrainInfoResponse {
    public boolean status;
    public int httpstatus;
    public TrainInfo data;

    public static class TrainInfo {
        public List<TrainStation> data;
    }

    public static class TrainStation {
        public String arrive_day_str;
        public String station_name;
        public String start_time;
    }
}
```

---

### **3. 发起请求并解析数据**
创建 `Retrofit` 实例，并从接口获取数据。

#### Retrofit 实例
```java
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://search.12306.cn/")
    .addConverterFactory(GsonConverterFactory.create())
    .build();

TrainService trainService = retrofit.create(TrainService.class);
```

#### 发起请求并填充到 Spinner
```java
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.spinner);

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://kyfw.12306.cn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        TrainService trainService = retrofit.create(TrainService.class);

        // 第一步：获取 train_no 和 date
        trainService.getTrainData("K1247", "20250127").enqueue(new Callback<TrainResponse>() {
            @Override
            public void onResponse(Call<TrainResponse> call, Response<TrainResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String trainNo = response.body().data.get(0).train_no;
                    String date = response.body().data.get(0).date;

                    // 第二步：获取详细站点信息
                    trainService.getTrainInfo(trainNo, date, "").enqueue(new Callback<TrainInfoResponse>() {
                        @Override
                        public void onResponse(Call<TrainInfoResponse> call, Response<TrainInfoResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<String> stationNames = new ArrayList<>();
                                for (TrainInfoResponse.TrainStation station : response.body().data.data) {
                                    stationNames.add(station.start_time + " - " + station.station_name);
                                }

                                // 设置 Spinner 数据
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    MainActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    stationNames
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onFailure(Call<TrainInfoResponse> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<TrainResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
```

---

### **4. 布局文件**
创建 Spinner 组件。

#### `activity_main.xml`
```xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <Spinner
        android:id="@+id/spinner"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:spinnerMode="dropdown" />
</LinearLayout>
```

---

### **效果**
1. Spinner 显示格式为：`start_time - station_name`。
2. 数据自动从接口加载，点击 Spinner 查看各站点时间和名称。





# 2. 根据 `date` 和 `time` 进行排序

你的日期格式是 `MM月dd日`，时间格式是 `HH:mm`，这种存储格式对排序有一定挑战。由于日期中没有年份部分，直接按字符串排序可能会导致错误的结果（例如，`12月01日` 会排在 `01月01日` 前面）。为了解决这个问题，建议将日期和时间存储为可排序的格式，比如 ISO 8601 标准的日期时间格式（`yyyy-MM-dd HH:mm`）。

以下是如何解决和处理这种情况的步骤：

------

### **解决方案**

#### 1. **调整存储格式**

将日期和时间存储为标准化格式，如 `yyyy-MM-dd HH:mm`，并使用 `SQLite` 的内置排序能力。

#### **存储日期和时间**：

在保存数据时，格式化为以下标准格式：

```java
String formattedDateTime = new SimpleDateFormat(
    "yyyy-MM-dd HH:mm",
    Locale.getDefault()
).format(selectedDate.getTime());
```

将 `formattedDateTime` 存入数据库的单一字段（如 `departure_datetime`）。

#### **查询和排序**：

你可以直接用 SQL 排序：

```sql
SELECT * FROM tickets ORDER BY departure_datetime ASC;
```

#### **显示日期和时间**：

读取数据后，将日期和时间转换回你想要的格式（如 `MM月dd日` 和 `HH:mm`）：

```java
SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
SimpleDateFormat dateOutputFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
SimpleDateFormat timeOutputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

Date date = inputFormat.parse(departureDateTime);
String displayDate = dateOutputFormat.format(date);
String displayTime = timeOutputFormat.format(date);
```

------

#### 2. **如果无法调整存储格式**

如果必须使用当前的 `MM月dd日` 和 `HH:mm` 格式，可以在查询时使用字符串解析进行排序。

修改查询语句，使用 `SQLite` 的 `strftime` 函数来解析和排序。

```sql
SELECT * FROM tickets 
ORDER BY 
    strftime('%m', departure_date) ASC, 
    strftime('%d', departure_date) ASC, 
    strftime('%H:%M', departure_time) ASC;
```

------

### **完整代码示例**

#### **保存数据时：**

```java
String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        .format(selectedDate.getTime());
String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
String formattedDateTime = formattedDate + " " + formattedTime;

// 保存到数据库时
contentValues.put("departure_datetime", formattedDateTime);
```

#### **查询数据时：**

```java
public Cursor showData() {
    SQLiteDatabase db = this.getReadableDatabase();

    // 按日期和时间排序
    return db.rawQuery("SELECT * FROM tickets ORDER BY departure_datetime ASC", null);
}
```

#### **显示数据时：**

```java
Cursor cursor = dbHelper.showData();
while (cursor.moveToNext()) {
    String departureDateTime = cursor.getString(cursor.getColumnIndex("departure_datetime"));

    // 转换为显示格式
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    SimpleDateFormat dateOutputFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
    SimpleDateFormat timeOutputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    try {
        Date date = inputFormat.parse(departureDateTime);
        String displayDate = dateOutputFormat.format(date);
        String displayTime = timeOutputFormat.format(date);

        // 用 displayDate 和 displayTime 显示
        Log.d("Data", "日期: " + displayDate + " 时间: " + displayTime);
    } catch (ParseException e) {
        e.printStackTrace();
    }
}
cursor.close();
```

------

### **优点**

- 使用标准化的日期时间格式，排序更高效且不易出错。
- 显示时可以灵活转换为所需格式。
- 在未来需要支持复杂日期时间操作（如筛选时间范围）时，更容易实现。

### **劣势**

- 需要对已有存储的数据进行格式迁移。如果已有数据需要兼容，建议批量更新数据库中的日期格式：从 `MM月dd日` 转换为 `yyyy-MM-dd` 格式。





# 3. 下拉组件

我来帮你实现这个下拉选择组件。在Android中，我们可以使用Spinner或自定义的下拉选择器来实现。这里我展示使用自定义布局的方式：

:select_layout.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@drawable/select_background"
    android:padding="12dp"
    android:clickable="true"
    android:focusable="true">

    <!-- 左侧图标 -->
    <ImageView
        android:id="@+id/ivIcon"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:src="@drawable/ic_document"
        android:tint="#666666"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"/>

    <!-- 标题文本 -->
    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="选择知识库"
        android:textColor="#333333"
        android:textSize="16sp"
        android:layout_marginStart="12dp"
        android:layout_marginEnd="12dp"
        app:layout_constraintStart_toEndOf="@id/ivIcon"
        app:layout_constraintEnd_toStartOf="@id/ivArrow"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"/>

    <!-- 右侧箭头图标 -->
    <ImageView
        android:id="@+id/ivArrow"
        android:layout_width="20dp"
        android:layout_height="20dp"
        android:src="@drawable/ic_arrow_down"
        android:tint="#999999"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

背景drawable：:drawable/select_background.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="#1A000000">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="#FFFFFF"/>
            <corners android:radius="8dp"/>
            <stroke 
                android:width="1dp"
                android:color="#E0E0E0"/>
        </shape>
    </item>
</ripple>
```

箭头图标：:drawable/ic_arrow_down.xml

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#000000"
        android:pathData="M7.41,8.59L12,13.17l4.59,-4.58L18,10l-6,6 -6,-6 1.41,-1.41z"/>
</vector>
```

文档图标：:drawable/ic_document.xml

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#000000"
        android:pathData="M14,2H6C4.9,2 4,2.9 4,4V20C4,21.1 4.9,22 6,22H18C19.1,22 20,21.1 20,20V8L14,2zM16,18H8V16H16V18zM16,14H8V12H16V14zM13,9V3.5L18.5,9H13z"/>
</vector>
```

对应的Java代码：

```java
public class SelectView extends ConstraintLayout {
    private ImageView ivIcon;
    private TextView tvTitle;
    private ImageView ivArrow;
    
    public SelectView(Context context) {
        super(context);
        init(context);
    }

    public SelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.select_layout, this, true);
        
        ivIcon = findViewById(R.id.ivIcon);
        tvTitle = findViewById(R.id.tvTitle);
        ivArrow = findViewById(R.id.ivArrow);

        // 设置点击事件
        setOnClickListener(v -> showDropdown());
    }

    private void showDropdown() {
        // 实现下拉菜单逻辑
        PopupMenu popup = new PopupMenu(getContext(), this);
        // 添加菜单项
        popup.getMenu().add("选项1");
        popup.getMenu().add("选项2");
        popup.getMenu().add("选项3");
        
        // 设置选择监听
        popup.setOnMenuItemClickListener(item -> {
            tvTitle.setText(item.getTitle());
            return true;
        });
        
        popup.show();
    }
}
```

使用方式：

```xml
<com.example.app.SelectView
    android:id="@+id/selectView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="16dp"/>
```

在安卓项目中，文件的存放位置通常需要按照其用途和 Android 的最佳实践来组织。根据你提到的需求，以下是一些建议：

### 1. **存储 JSON 文件**
JSON 文件通常用来保存配置数据或内容数据，可以放在 `assets` 文件夹中，或者 `res/raw` 文件夹中。具体选择哪个目录，取决于你是否需要在运行时修改这些文件。

#### 使用 `assets` 文件夹：
- **路径**：`src/main/assets/`
- **优点**：你可以存储任何文件，并且在运行时可以通过 `AssetManager` 访问。
- **示例**：将 `data.json` 存储在 `assets` 文件夹下
  - 路径：`src/main/assets/data.json`

#### 使用 `res/raw` 文件夹：
- **路径**：`src/main/res/raw/`
- **优点**：资源文件可以通过 `Resources` 类直接访问，适用于不需要频繁修改的文件。
- **示例**：将 `data.json` 存储在 `res/raw` 文件夹下
  - 路径：`src/main/res/raw/data.json`

# 4. **存储 `readme.md` 文件的资源（图片、视频、字体、CSS 文件）**

如果你希望将图片、视频、字体和 CSS 文件存储在项目中，并且它们是与 `readme.md` 文件相关的资源，建议使用以下结构：

#### 图片文件
图片可以放在 `res/drawable` 文件夹中，通常在 Android 项目中，所有图片资源都放在这个目录。

- **路径**：`src/main/res/drawable/`
- **命名规则**：图片文件的命名应为小写字母，使用下划线分隔单词，例如 `image_1.png`，`logo.jpg`。

#### 视频文件
视频文件一般放在 `res/raw` 文件夹中，因为它通常需要被直接读取并播放。

- **路径**：`src/main/res/raw/`
- **命名规则**：视频文件的命名通常使用小写字母和下划线分隔，例如 `video_sample.mp4`。

#### 字体文件
字体文件可以放在 `res/font` 文件夹中，通常用于应用的自定义字体。

- **路径**：`src/main/res/font/`
- **命名规则**：字体文件的命名也建议使用小写字母和下划线分隔，例如 `custom_font.ttf`。

#### CSS 文件
CSS 文件并不是 Android 原生支持的资源类型，但你可以将它们放在 `assets` 文件夹中，然后通过 WebView 或其他方式使用它们。

- **路径**：`src/main/assets/`
- **命名规则**：文件命名建议使用小写字母和下划线，例如 `styles.css`。

### 3. **项目资源结构示例**
以下是一个可能的项目资源结构示例：

```
app/
├── src/
│   └── main/
│       ├── assets/
│       │   ├── data.json
│       │   ├── readme.md
│       │   ├── images/
│       │   │   ├── image_1.png
│       │   │   └── logo.jpg
│       │   └── styles.css
│       ├── res/
│       │   ├── drawable/
│       │   │   ├── image_1.png
│       │   │   └── logo.jpg
│       │   ├── raw/
│       │   │   ├── video_sample.mp4
│       │   │   └── audio_sample.mp3
│       │   ├── font/
│       │   │   └── custom_font.ttf
```

### 4. **如何加载 JSON、图片、视频等资源**

#### 加载 JSON 文件：
```java
// 从 assets 文件夹中加载 JSON 文件
AssetManager assetManager = getAssets();
InputStream inputStream = assetManager.open("data.json");

// 如果是从 raw 文件夹中加载：
Resources res = getResources();
InputStream inputStream = res.openRawResource(R.raw.data);
```

#### 加载图片：
```java
ImageView imageView = findViewById(R.id.imageView);
imageView.setImageResource(R.drawable.image_1); // 从 drawable 中加载
```

#### 加载视频：
```java
VideoView videoView = findViewById(R.id.videoView);
Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_sample);
videoView.setVideoURI(videoUri);
videoView.start();
```

#### 加载自定义字体：
```java
TextView textView = findViewById(R.id.textView);
Typeface typeface = ResourcesCompat.getFont(this, R.font.custom_font);
textView.setTypeface(typeface);
```

### 总结：
- JSON 文件可以放在 `assets` 或 `res/raw` 文件夹中。
- 图片、视频、字体和 CSS 文件可以根据它们的用途存储在 `res/drawable`、`res/raw`、`res/font` 和 `assets` 文件夹中。
- 图片文件、字体文件和 CSS 文件可以通过相应的资源访问方式（如 `ImageView`、`Typeface`、`WebView`）加载。





明白了，问题出现在数据的格式化过程中，`city_name` 有时可能会包含 `|||`，导致解析时出错。这是由于在每组数据中，有些字段可能没有正确处理，导致数据被错误地拆分或者多余的分隔符被包含。

为了解决这个问题，我们需要更精确地处理每组数据，并确保每个字段正确拆分。以下是修改后的代码，确保 `city_name` 和其他字段没有多余的 `|||`。

### 解决方案

我们可以在处理数据时，首先去除多余的分隔符或空白字符，并确保字段被正确拆分。这里的解决方法是去掉无用的分隔符和空格，同时通过条件判断来处理缺失的数据。

### 1. 修改后的 Shell 脚本（CSV 和 JSON 格式）

```bash
#!/bin/bash

# 输入文件，假设数据存储在 station_name.js 中 https://kyfw.12306.cn/otn/resources/js/framework/station_name.js 
file="station_name.js"

# 提取数据
data=$(sed 's/var station_names =//g' "$file" | sed 's/;//g')

# 清空或创建 CSV 文件
csv_file="stations.csv"
json_file="stations.json"

echo "code,station_name,station_code,station_en_name,station_id,station_status,city_code,city_name" > "$csv_file"
echo "[" > "$json_file"

first=true
echo "$data" | tr '@' '\n' | while read -r line; do
  if [[ -n "$line" ]]; then
    # 去掉行尾的多余字符 ||| 和空格
    line=$(echo "$line" | sed 's/|*$//')

    # 分割字段并处理
    IFS='|' read -r code station_name station_code station_en_name station_id station_status city_code city_name <<< "$line"

    # 处理缺失的字段（例如 city_name 可能为空或包含多余的字符）
    if [[ -z "$city_name" ]]; then
      city_name="未知"
    fi

    # 如果是第一条数据，去掉前面的逗号
    if [[ "$first" == true ]]; then
      first=false
    else
      echo "," >> "$json_file"
    fi

    # 将每条记录写入 JSON 文件
    echo "  {
      \"code\": \"$code\",
      \"station_name\": \"$station_name\",
      \"station_code\": \"$station_code\",
      \"station_en_name\": \"$station_en_name\",
      \"station_id\": \"$station_id\",
      \"station_status\": \"$station_status\",
      \"city_code\": \"$city_code\",
      \"city_name\": \"$city_name\"
    }" >> "$json_file"

    # 将每条记录写入 CSV 文件
    echo "$code,$station_name,$station_code,$station_en_name,$station_id,$station_status,$city_code,$city_name" >> "$csv_file"
  fi
done

echo "]" >> "$json_file"

echo "CSV 文件已生成：$csv_file"
echo "JSON 文件已生成：$json_file"
```

### 修改说明：

1. **去除无用的 `|||`**：
   - 在每一行数据处理前使用 `sed 's/|*$//'`，移除字符串末尾的多余分隔符。
2. **字段缺失处理**：
   - 对 `city_name` 进行了空值判断，如果 `city_name` 为空，则赋值为 `"未知"`。可以根据需要修改为其他默认值。
3. **CSV 和 JSON 输出**：
   - 在输出 CSV 和 JSON 时，确保每一条数据都能正确处理，避免因为分隔符问题导致文件格式错误。

### 执行结果：

#### CSV 文件：

```
code,station_name,station_code,station_en_name,station_id,station_status,city_code,city_name
bjb,北京北,VAP,beijingbei,bjb,0,0357,北京
bjd,北京东,BOP,beijingdong,bjd,1,0357,北京
bji,北京,BJP,beijing,bj,2,0357,北京
zzx,资中西,ZZE,zizhongxi,zzx,3420,1712,内江
```

#### JSON 文件：

```json
[
  {
    "code": "bjb",
    "station_name": "北京北",
    "station_code": "VAP",
    "station_en_name": "beijingbei",
    "station_id": "bjb",
    "station_status": "0",
    "city_code": "0357",
    "city_name": "北京"
  },
  {
    "code": "bjd",
    "station_name": "北京东",
    "station_code": "BOP",
    "station_en_name": "beijingdong",
    "station_id": "bjd",
    "station_status": "1",
    "city_code": "0357",
    "city_name": "北京"
  },
  {
    "code": "zzx",
    "station_name": "资中西",
    "station_code": "ZZE",
    "station_en_name": "zizhongxi",
    "station_id": "zzx",
    "station_status": "3420",
    "city_code": "1712",
    "city_name": "内江"
  }
]
```

### 总结：

- **问题**：数据中可能会有多余的 `|||` 或缺失的字段，导致解析错误。
- **解决方案**：去除尾部多余的分隔符，并对缺失的字段进行处理（例如设置默认值）。
- **结果**：生成了正确格式的 CSV 和 JSON 文件，其中没有多余的分隔符或错误的字段值。

希望这能解决你的问题，如果还有其他问题，随时告诉我！

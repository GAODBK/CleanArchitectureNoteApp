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


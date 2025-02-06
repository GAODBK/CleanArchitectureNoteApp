```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto" android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.cardview.widget.CardView android:layout_width="match_parent"
        android:layout_height="wrap_content" android:layout_marginBottom="16dp"
        app:cardCornerRadius="16dp">

        <LinearLayout android:id="@+id/item_llt" android:layout_width="match_parent"
            android:layout_height="wrap_content" android:orientation="vertical">

            <!-- 航班信息部分（上半部分） -->
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
                android:background="@drawable/top_bg" android:padding="16dp">

                <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                    android:layout_weight="1" android:orientation="vertical">

                    <TextView android:layout_width="wrap_content"
                        android:layout_height="wrap_content" android:text="NYC"
                        android:textColor="#FFFFFF" android:textSize="20sp"
                        android:textStyle="bold" />

                    <TextView android:id="@+id/departure_station"
                        android:layout_width="wrap_content" android:layout_height="wrap_content"
                        android:text="New-York" android:textColor="#FFFFFF"
                        android:textSize="14sp" />
                </LinearLayout>

                <TextView android:id="@+id/departure_date" android:layout_width="wrap_content"
                    android:layout_height="wrap_content" android:text="8H 30M"
                    android:textColor="#FFFFFF" />

                <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                    android:layout_weight="1" android:gravity="end" android:orientation="vertical">

                    <TextView android:layout_width="wrap_content"
                        android:layout_height="wrap_content" android:text="LDN"
                        android:textColor="#FFFFFF" android:textSize="20sp"
                        android:textStyle="bold" />

                    <TextView android:id="@+id/arrival_station" android:layout_width="wrap_content"
                        android:layout_height="wrap_content" android:text="London"
                        android:textColor="#FFFFFF" android:textSize="14sp" />
                </LinearLayout>
            </LinearLayout>

            <!-- 航班详细信息（下半部分） -->
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
                android:background="@drawable/bottom_bg" android:padding="16dp">

                <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                    android:layout_weight="1" android:orientation="vertical">

                    <TextView android:id="@+id/tvGateNumber" android:layout_width="wrap_content"
                        android:layout_height="wrap_content" android:text="1 May"
                        android:textColor="#FFFFFF" android:textSize="16sp" />

                    <TextView android:layout_width="wrap_content"
                        android:layout_height="wrap_content" android:text="Date"
                        android:textColor="#FFFFFF" android:textSize="12sp" />
                </LinearLayout>

                <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                    android:layout_weight="1" android:gravity="center"
                    android:orientation="vertical">

                    <TextView android:id="@+id/departure_time" android:layout_width="wrap_content"
                        android:layout_height="wrap_content" android:text="08:00 AM"
                        android:textColor="#FFFFFF" android:textSize="16sp" />

                    <TextView android:id="@+id/tv" android:layout_width="wrap_content"
                        android:layout_height="wrap_content" android:text="出发时间"
                        android:textColor="#FFFFFF" android:textSize="12sp" />
                </LinearLayout>

                <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                    android:layout_weight="1" android:gravity="end" android:orientation="vertical">

                    <TextView android:id="@+id/tvSeatNumber" android:layout_width="wrap_content"
                        android:layout_height="wrap_content" android:text="23"
                        android:textColor="#FFFFFF" android:textSize="16sp" />

                    <TextView android:layout_width="wrap_content"
                        android:layout_height="wrap_content" android:text="Number"
                        android:textColor="#FFFFFF" android:textSize="12sp" />
                </LinearLayout>
            </LinearLayout>
        </LinearLayout>
    </androidx.cardview.widget.CardView>
</FrameLayout>
```

drawable/top_bg.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <!-- 设置上部颜色 -->
    <solid android:color="#5C6BC0" />
    <corners android:topLeftRadius="16dp" android:topRightRadius="16dp"
        android:bottomLeftRadius="0dp" android:bottomRightRadius="0dp" />
</shape>
```

drawable/bottom_bg.xml

```xml

<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <!-- 设置下部颜色 -->
    <solid android:color="#FF7043" />
    <corners android:bottomLeftRadius="16dp" android:bottomRightRadius="16dp"
        android:topLeftRadius="0dp" android:topRightRadius="0dp" />
</shape>
```

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

[模仿ssm架构的Android SQLite APP 开发](https://www.youtube.com/watch?v=H0Wm3jdG8Ys&t=346s)
[Android端实现仿IOS通知栏的折叠/展开效果的组件](https://github.com/itlwy/StackDrawer/blob/master/app/src/main/java/com/lwy/myapplication/NestingStackActivity.java)
[ 仿音乐播放器锁屏页面，以及通知栏8.0适配和自定义大小图样式适配 ](https://github.com/GAODBK/LockDemo/blob/master/app/src/main/java/com/ztk/demo/lockdemo/service/PlayService.java)

## License

   	Copyright 2025 gaodbk

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

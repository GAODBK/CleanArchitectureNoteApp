```
yourpackage/
├── activities/      # Activity 相关
│   ├── MainActivity.java         # 负责 BottomNavigationView 绑定和 Fragment 切换
│   ├── LockActivity.java         # 仿音乐锁屏页面（横滑退出）
├── fragments/       # Fragment 相关
│   ├── HomeFragment.java        # 主页，显示车次列表，支持折叠/展开效果
│   ├── EnterFragment.java       # 录入车次信息并存入数据库
├── database/        # 数据库相关（SQLite）
│   ├── DBHelper.java            # SQLiteOpenHelper，管理数据库表的创建和升级
│   ├── TrainDAO.java            # 操作数据库的 DAO 类，包含增删改查方法
├── models/          # 数据模型
│   └── Train.java              # 车次信息的数据模型
├── network/         # 网络请求
│   ├── ApiService.java        # 负责请求远程 API 获取车次信息
├── services/        # 后台服务
│   ├── PlayService.java       # 负责播放相关任务（如锁屏音乐）
├── utils/           # 工具类
│   ├── NotificationUtil.java  # 发送通知（如火车出发前提醒）
│   ├── AndroidWorkaround.java # 适配 Android 设备，处理 UI 兼容性问题
│   ├── HintTextView.java      # 自定义可变色的提示文本
│   ├── SlidingFinishLayout.java # 横滑退出页面的自定义 View
├── widgets/         # 自定义 UI 组件
│   ├── StackScrollView.java   # 自定义滚动组件，实现 iOS 通知折叠效果
│   ├── StackLayout.java       # 折叠/展开视图的容器
│   ├── IScrollSubscription.java # 滚动订阅接口
│   ├── IScrollListener.java    # 滚动监听接口
│   ├── WeekDatePicker.java    # 自定义周日期选择器
│   ├── StationPicker.java     # 自定义站点选择器

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

[12306车站列表](https://github.com/T0lja/12306TicketInquiry/blob/master/src/main/java/dev/tolja/data/Station.java)

[ 一个易于使用的 Android 图表库，带有动画。 ](https://github.com/HackPlan/AndroidCharts)

[使用 Fan Layout Manager，您可以实现水平列表，其项目像风扇叶片一样移动 ](https://github.com/jiang111/Awesome-RecyclerView-LayoutManager/blob/master/README.md)

[在足球](https://crazysunj.com/gnz-yingchao)

[轮播指示器、3D旋转和倒影效果](https://github.com/crazysunj/CardSlideView?tab=readme-ov-file)

[周日期选择器](https://github.com/AriesHoo/NCalendar/blob/master/ncalendar/src/main/java/com/necer/ncalendar/calendar/WeekCalendar.java)

[双线选择器](https://github.com/CNCoderX/WheelView/blob/master/library/src/main/java/com/cncoderx/wheelview/WheelScroller.java)

[形状弹窗](https://github.com/andyxialm/ColorDialog/blob/master/ColorDialog/src/main/java/cn/refactor/lib/colordialog/util/DisplayUtil.java)

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

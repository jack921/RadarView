# RadarView


属性 | 解释
---|---
broad_text_size | 雷达图角的文字大小
interval_text_size | 雷达图区间提示文字大小
circle_hold_textSize | 雷达数据点文字大小
broad_color | 雷达图边的颜色
broad_color_text | 雷达图角的文字颜色
circle_hold_color | 雷达数据点文字颜色
corner_hold_color |  雷达数据点颜色
interval_text_color | 雷达图区间提示文字颜色
mark_broad_color | 数据区域的颜色
mark_color | row 2 col 数据区域边的颜色

使用事例如下：

```
<com.example.jack.radarview.RadarView
    android:id="@+id/radarView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:broad_text_size="14sp"
    app:interval_text_size="9sp"
    app:circle_hold_textSize="12sp"
    app:broad_color="#20B2AA"
    app:broad_color_text="#2F4F4F"
    app:circle_hold_color="#BC8F8F"
    app:corner_hold_color="#1E90FF"
    app:interval_text_color="#696969"
    app:mark_broad_color="#32CD32"
    app:mark_color="#7CFC00"/>
```

效果图如下：

![效果图.gif](https://upload-images.jianshu.io/upload_images/925576-1f38fa9513fd3e04.gif?imageMogr2/auto-orient/strip)



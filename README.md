# Screenshare Demo APK

`Screenshare` 是一个 AAOS Demo 应用（Java 版本），用于模拟以下命令并完成跨应用按屏启动：

```bash
am start -n <package_name>/<activity_path> --display <display_id>
```

## 功能

- 输入 `package_name`、`activity_path`、`display_id` 后点击 **启动投屏**。
- 点击 **取消投屏（拉回主屏）** 后，将目标应用拉回主屏（`display 0`）。

## 运行要求

- 建议 Android 15 / AAOS 环境。
- 车机场景建议以 `system/priv-app` 方式预装并使用平台签名（与量产策略一致）。

## 项目结构

- `app/src/main/java/com/example/screenshare/MainActivity.java`: 核心逻辑。
- `app/src/main/res/layout/activity_main.xml`: 两按钮三输入框 UI。
- `app/src/main/AndroidManifest.xml`: 应用清单。

## 注意事项

- 目标应用需支持被拉起到副屏或虚拟屏。
- 系统多显示策略需允许对应 display 启动。
- “拉回主屏”是通过重新在 `display 0` 启动目标 Activity 实现。

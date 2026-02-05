### 全局模式用法（跨 Activity 右上角提示）

全局模式会在 **整个 App 所有页面** 的右上角显示网络状态提示条（无网络、飞行模式、Wi‑Fi 信号弱、使用移动网络等），不需要在每个 Activity 手动调用。

#### 1. 在 Application 中初始化

```kotlin
import android.app.Application
import com.lff.networkmonitor.NetworkMonitorOverlay

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Start global network monitor overlay
        NetworkMonitorOverlay.startGlobal(this)
    }
}
```

在 `AndroidManifest.xml` 中配置你的 Application：

```xml
<application
    android:name=".MyApp"
    ... >
    ...
</application>
```

#### 2. 不需要在每个 Activity 手动调用

使用全局模式后，无需再在 Activity 里写：

```kotlin
override fun onStart() {
    super.onStart()
    NetworkMonitorOverlay.start(this)
}

override fun onStop() {
    super.onStop()
    NetworkMonitorOverlay.stop()
}
```

只要 App 在前台，当前页面右上角会自动根据网络状态显示或隐藏提示条。

#### 3. 可选：在运行时关闭全局提示

如果需要在某些场景下临时关闭全局提示（例如退出登录或某些特殊模式），可以调用：

```kotlin
NetworkMonitorOverlay.stopGlobal(application)
```

需要重新开启时再次调用：

```kotlin
NetworkMonitorOverlay.startGlobal(application)
```

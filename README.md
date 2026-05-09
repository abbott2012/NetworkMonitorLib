# NetworkMonitorLib

在项目中引用 v2.2.0。

## 1. 添加 JitPack 仓库

在 `settings.gradle.kts` 或根 `build.gradle` 中确保有 JitPack 仓库：

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

## 2. 添加依赖

在 app 模块 `build.gradle.kts` 中添加：

```kotlin
dependencies {
    implementation("com.github.abbott2012:NetworkMonitorLib:2.2.0")
}
```

然后 Sync Project 并编译。第一次可能稍微久一点，因为 JitPack 会远程构建。

## 3. 全局模式用法

全局模式会在整个 App 所有页面的最上层显示网络状态提示条，不需要在每个 Activity 手动调用。

### 在 Application 中初始化

```kotlin
import android.app.Application
import com.lff.networkmonitor.NetworkMonitorOverlay

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
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

### 可选：在运行时关闭全局提示

```kotlin
NetworkMonitorOverlay.stopGlobal(application)
```

需要重新开启时再次调用：

```kotlin
NetworkMonitorOverlay.startGlobal(application)
```

## 4. 单 Activity 用法

如果不需要全局模式，也可以在单个页面中手动开启：

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

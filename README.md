一、在项目中引用 v2.0.0

1. settings.gradle.kts （或根 build.gradle）确保有 JitPack 仓库：
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```
2. app 模块 build.gradle.kts 添加依赖（改成 v2.0.0）：
```
dependencies {
    implementation("com.github.abbott2012:NetworkMonitorLib:v2.0.0")
}
```
然后 Sync Project → 编译，第一次可能稍微久一点（JitPack 会远程构建）。
 Step 3. 在 Activity 中一行开启
在页面的 onStart() 或 onResume() ：

override fun onStart() {
    super.onStart()
    NetworkMonitorOverlay.start(this)
}
在 onStop() 或 onDestroy() 中关闭：

override fun onStop() {
    super.onStop()
    NetworkMonitorOverlay.stop()
}

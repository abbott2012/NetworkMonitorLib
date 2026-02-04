Add it in your settings.gradle.kts at the end of repositories:

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("") }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation("com.github.abbott2012:NetworkMonitorLib:Tag")
	}
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

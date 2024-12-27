# 预设开发指南

Launcher具有高度自定义的特性\
现在让我们来学习预设的开发

## 要求
- 一台能够流畅运行[Android Studio](https://developer.android.google.cn/studio?hl=zh-cn)的电脑
- 或者一台能够运行[AndroidIDE](https://m.androidide.com/)的手机

## 开始之前
Launcher的预设系统基于Android View实现\
这需要您拥有Android开发基础\
若您还是一个Android开发小白 浏览本篇文章时可能会比较吃力

## 创建项目
打开您的IDE，创建一个Android应用项目\
您可以自由配置您的应用包名等信息\
静待Gradle Sync完成...\
恭喜🎉您已经成功创建了您的第一个预设项目

## 配置预设必要信息
在Gradle中引入预设库,记得引入完Sync一下\
`build.gradle.kts`
```kotlin
...
dependencies {
    ...
    implementation(files("./libs/watchface-dev-utils.aar"))
    implementation("com.blankj:utilcodex:1.31.1")
}
...
```
接下来在app/src/main/java/<packageName>下创建一个新的类，名字随意，这里使用`WatchFaceImpl`\
`WatchFaceImpl.java`
```java
public class WatchFaceImpl extends WatchFace {
    /**
    * 注意一定要添加这个单参数的构造方法
    * 这个构造方法用于启动器创建预设
    */
    public WatchFaceImpl(Context context){  
        super(context);
    }
}
```
这里我们让WatchFaceImpl继承自WatchFace这个类，这个类将会在后文进行解释，这里不过多赘述\
接下来在AndroidManifest.xml中添加如下代码\
`AndroidManifest.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest
    ...>

    <application
        ...>
        <activity
            android:name=".StubActivity"
            android:exported="true">
            <!--这个Activity声明仅作占位用，可不创建，仅用于启动器寻找预设-->
            <intent-filter>
                <action
                    android:name="com.lazytong.launcher.WATCHFACE" />
                <category
                    android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>
    </application>
    ...
</manifest>
```
最后创建app/src/main/res/xml/watchface_config.xml添加如下代码\
`watchface_config.xml`
```xml
<config
    <!--预设的预览图片-->
    preview="@drawable/preview"
    <!--预设的名称-->
    name="<watchFaceName>"
    <!--预设入口类-->
    watchface="<包名>.WatchFaceImpl"
    <!--预设设置Activity(这里先不设置)-->
    settings_activity=""
/>
```
## 小结
到这里你已经搭建好了预设项目的基本框架

<br />
<p
    align="right">下一步：
</p>
<div align="right" >
<a href="">使用View构建您的预设</a>
<br />
<a href="">使用Canvas构建您的预设</a>
</div>
# 我大概知道如何搭建当前项目的环境了
1. 去下载gradle 7.0.2,毕竟是这个项目运行要求
2. 准备Oracle JDK 11,不能使用jdk17
3. 反正下方是我的搭建安卓开发步骤，想运行这个项目需要将下方的6.1.1换成7.0.2

# jdk，gradle 下载
1. 在`Oracle `官网，注册账号，下载`jdk8`or`jdk11`

> 不用下太高级的版本，因为需要注意  Gradle版本与Java版本的对应关系
>

![](https://cdn.nlark.com/yuque/0/2024/png/35479677/1709268847963-36252499-e52d-4328-92cf-7639e71296de.png)

2. 去`gradle 官网`下载，由于我看不懂别人的解释，直接跟着他们的版本号 进行操作 下载，故下载的`gradle6.1.1`
3. 配置`GRADLE_USER_HOME`环境

```java
GRADLE_USER_HOME
D:\environment\gradle-6.1.1
```

# 下载 as 与SDK
Android studio 在官网 下载 不难，

+ 不过 接着就要下载 AndroidSDK 了，这玩意 要连接 Google 的（故：启动clash，准备 20个G 的国外流量 和 一个小时的时间（网速越好，下载越快））；
+ 若能正确的下载完SDK，通过`设置`>`SDK Manager`查看SDK的下载内容，
    - 这个AndroidSDK在电脑D盘占了9.8G，C盘也是差不多 这么大的内存消耗
    - 但大概率 在 `SDK Tools` 页面缺少了 `Command-line Tools` 这个工具，等待下载一下（这个是为了 flutter 的开发做准备，flutter 需要这个东西）

![](https://cdn.nlark.com/yuque/0/2024/png/35479677/1709303842955-9cb548a9-e9ae-425b-877b-03f1224310f5.png)

![](https://cdn.nlark.com/yuque/0/2024/png/35479677/1709269701296-405fd25a-e59b-4806-803c-8451f6b13051.png)

![](https://cdn.nlark.com/yuque/0/2024/png/35479677/1709269726167-e722a2c7-5c85-4ef9-9f82-3d6724c2b29b.png)

# 打开一个安卓项目
![](https://cdn.nlark.com/yuque/0/2024/png/35479677/1709270196028-9ace049d-e160-4540-8321-ffde4ed97b6d.png)

### 先用较快的手速，关闭底部的自动下载
在2024年，下载Android studio 新建一个项目  程序设计为默认下载 gradle8.3.0，而且还TM不好找 下方的两个文件



> 安卓开发克隆github上的项目到本地后
>
> 替换APP下的build.gradlle
>
> 和project下的build.gradle
>
> 将setting.gradle替换
>



## 打开`build.gradle`文件
修改版本号，项目刚下载下来时，版本号是：<font style="color:#6aab73;background-color:#1e1f22;">.build:gradle:3.5.1'</font>，修改为<font style="color:#6aab73;background-color:#1e1f22;">.build:gradle:4.0.1'</font>

```java
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
repositories {
    google()
    jcenter()

}
dependencies {
classpath 'com.android.tools.build:gradle:4.0.1'
//        classpath 'com.android.tools.build:gradle:6.1.1'
}
// NOTE: Do not place your application dependencies here; they belong
// in the individual module build.gradle files

}

allprojects {
    repositories {
        google()
        jcenter()

    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

<details class="lake-collapse"><summary id="ufb8eea8f"><span class="ne-text">这是我看的 对于 gradle 版本号 在 as 的解释</span></summary><p id="ud70725a0" class="ne-p"><span class="ne-text">在后续的操作中，我会解释我对这个的设置（偏转的解释）</span></p><p id="uafe97ee3" class="ne-p"><img src="https://cdn.nlark.com/yuque/0/2024/png/35479677/1709269393691-4662630c-53f4-4595-b472-8e1ee11583b2.png" width="543.2" id="ud14e1005" class="ne-image"></p><div id="Rg9d1" class="ne-bookmark"><a href="https://stackoverflow.com/questions/56016265/could-not-find-com-android-tools-buildgradle5-1-1" target="_blank">https://stackoverflow.com/questions/56016265/could-not-find-com-android-tools-buildgradle5-1-1</a></div></details>
## 打开`gradle-wrapper.properties`文件
1. 配置 `GRADLE_USER_HOME`环境
2. 注释掉 下载 网址链接，如果是在第一步没有下gradle，那在这一步需要下载

```java
#Fri Mar 01 10:58:54 CST 2024
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
# distributionUrl=https\://services.gradle.org/distributions/gradle-6.1.1-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

<details class="lake-collapse"><summary id="u26309e2e"><span class="ne-text" style="font-size: 16px">gradle 询问大佬 给的解释</span></summary><p id="u90c426ae" class="ne-p"><img src="https://cdn.nlark.com/yuque/0/2024/webp/35479677/1709302859513-f6fe855e-9855-40f9-a09a-54207a66a4e2.webp" width="2250" id="u488ea4e8" class="ne-image"></p><p id="u2a9ff2d9" class="ne-p"><span class="ne-text">指定</span><code class="ne-code"><span class="ne-text">gradle wrapper</span></code><span class="ne-text">的版本号（的下载版本）</span></p><p id="ua497b6b1" class="ne-p"><br></p><p id="uc9269583" class="ne-p"><span class="ne-text">指定</span><code class="ne-code"><span class="ne-text">gradle插件</span></code><span class="ne-text">的版本号。如果你使用外置的gradle，只需要更新外置gradle的版本号就可以了</span></p><p id="u7b3d9a58" class="ne-p"><img src="https://cdn.nlark.com/yuque/0/2024/webp/35479677/1709302896058-9a9534c6-d603-4d17-9a0f-c3ab81c71c37.webp" width="2172" id="ud571c399" class="ne-image"></p></details>
## 调用下载到本地的Java与gradle
使用自己下载好 的`gradle`与`Java`进行环境配置，省一点翻墙流量，

![](https://cdn.nlark.com/yuque/0/2024/png/35479677/1709271368825-64c28e76-321a-458d-96ba-3de9d40a29c2.png)

![](https://cdn.nlark.com/yuque/0/2024/png/35479677/1709271736684-2cd27bd9-73e4-4caf-bd22-8ba672c3c563.png)

![](https://cdn.nlark.com/yuque/0/2024/png/35479677/1709272039413-7cf4dbef-f09f-49e2-a117-37d9a22fe53f.png)

## 可以使用命令行与as 启动项目了
下载了模拟器，会看到模拟器的名字，已经出现，先启动模拟器，在启动项目，构建程序，出现在模拟器里

![](https://cdn.nlark.com/yuque/0/2024/png/35479677/1709272149383-8a53b600-f17c-490e-8a83-d6801b99ce83.png)

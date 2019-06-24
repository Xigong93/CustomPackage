# 优雅的实现微信分享/支付，组件化
比如微信分享，就需要生成一个“${applicationId}.wxapi.WXEntryActivity”,这个操作在多包名或者是组件化的项目中非常蛋疼，就可以用这个方式来实现解耦
## 有什么用？

例如这个类，全类名是`aaa.WxEntryActivity`
```java
package aaa;
 
import pokercc.android.custompakcage.CustomPackage;

@CustomPackage("bbb")
public class WxEntryActivity  {
}

```

在编译的时候会生成一个`bbb.WxEntryActivity`的类
```java
package bbb;
/* 
* create by CustomPackageProcessor don't modify!! 
*/
public class WxEntryActivity  extends aaa.WxEntryActivity{
}
```



是不是你就可以通过这种方式，解决`${applicationId}.wxapi.WXEntryActivity`的问题呢？

## 怎么用？

### 1. WxEntryActivity 在app module

1. 引入gradle 依赖
```gradle
def custom_package_version = "0.1.2"
implementation "pokercc.android.custompackage:annotations:$custom_package_version"
annotationProcessor "pokercc.android.custompackage:compiler:$custom_package_version"
```
2. 在类上添加CustomPackage注解
```java
@CustomPackage(BuildConfig.APPLICATION_ID+".wxapi.WXEntryActivity")
public class WxEntryActivity extends Activity {
}
```
3. androidManifest.xml中activity 声明
```XML
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="pokercc.android.custompackage.share_lib">

    <application>
        <activity android:name="${$applicationId}.wxapi.WXEntryActivity" />
    </application>
</manifest>

```

嗯，你就可以跑跑看了。
### 2. WxEntryActivity 在library module
``使用的是注解生成器的方式，所以这个module，不能打成aar，只能随主工程一起编译``

这个时候，进行完上面的操作有一个问题，BuildConfig.APPLICATION_ID 在library module 中，可不是你想要的那个包名哦，它是library 的appid，而不是app module 的appid。 怎么解决呢？
#### a. 在root build.gradle 上定义一个变量
```groovy
// in root build.gradle
buildscript {
    ext {
        application_id="xxx"
    }
    ...
}
...
```groovy
// 在library module ，通过
buildConfigField 'String', 'WECHAT_PACKAGE_NAME_PREFIX', "\"$rootproject.ext.application_id\""
manifestPlaceholders['WECHAT_PACKAGE_NAME_PREFIX'] = rootproject.ext.application_id
// 这样在library 中，既可以访问到BuilcConfig.WECHAT_PACKAGE_NAME_PREFIX这个常量
```
#### b. 直接给library module 中定义一个变量，传递包名

* 在app module build.gradle中
```groovy
// 设置微信分享的包名,share_lib 是分享module 的名字
findProject(":share_lib").ext.WECHAT_PACKAGE_NAME_PREFIX = appId
```
* 在library module build.gradle 中，进行参数检查
```groovy
try {
    println "WECHAT_PACKAGE_NAME_PREFIX:$WECHAT_PACKAGE_NAME_PREFIX"
} catch (Exception e) {
    throw new RuntimeException("必须设置 WECHAT_PACKAGE_NAME_PREFIX 才能编译成功,比如 这样设置 在引用这个库前设置 findProject('$project.name').ext.WECHAT_PACKAGE_NAME_PREFIX='xx' ", e)
}
```
我比较推荐第二种方式，因为声明的更加明确一些，详细请参考demo
## 原理：
通过AnnotationProcessor 生成了一个指定包名的子类。
所以源class ,只能被public 修饰，不能被final修饰，且不能是内部类,不是包含抽象方法，不能是接口

传送门 https://github.com/pokercc/CustomPackage

有兄弟留言发现还有更简单的方案实现，使用activity-alias,传送门 https://blog.csdn.net/happyjie1988/article/details/78677255  
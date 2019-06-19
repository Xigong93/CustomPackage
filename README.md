# 自定义某个class 的包名，用来做微信分享回调activity的解耦和模块的组件化
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

即可测试运行
### 2. WxEntryActivity 在library module
``使用的是注解生成器的方式，所以这个module，不能打成aar，只能随主工程一起编译``

这个时候，有一个问题？编译的时候，library module 需要app module 的包名，其他的操作同上面
两种方式把app module 的包名，传递到library module
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

* 在app module 中
```groovy
// 设置微信分享的包名
findProject(":share_lib").ext.WECHAT_PACKAGE_NAME_PREFIX = appId
```
* 在library module 中，进行参数检查
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
所以源class ,只能被public 修饰，不能被final修饰，且不能是内部类

```
传送门 https://github.com/pokercc/CustomPackage
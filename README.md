# 自定义某个class 的包名，用来做微信分享回调activity的解耦和模块的组件化
比如微信分享，就需要生成一个“${applicationId}.wxapi.WXEntryActivity”,这个操作在多包名或者是组件化的项目中非常蛋疼，就可以用这个方式来实现解耦
## 怎么用
1. 引入gradle 依赖
```gradle
    def custom_package_version = "0.1.2"
    implementation "pokercc.android.custompackage:annotations:$custom_package_version"
    annotationProcessor "pokercc.android.custompackage:compiler:$custom_package_version"
```
2. 在需要特殊包名的类上，添加CustomPackage注解
```java
// value 为生成的类package
@CustomPackage(BuildConfig.WECHAT_PACKAGE_NAME_PREFIX + ".api")
public class WxEntryActivity extends Activity {
}
```
3. 对于androidManifest.xml中activity 定义，只需要使用android gradle application plugins 提供的方式注入即可
```XML
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="pokercc.android.custompackage.share_lib">

    <application>
        <activity android:name="${WECHAT_PACKAGE_NAME_PREFIX}.wxapi.WXEntryActivity" />
    </application>
</manifest>

```

即可测试运行

## 原理：
通过AnnotationProcessor 生成了一个指定包名的子类。
所以源class ,只能被public 修饰，不能被final修饰，且不能是内部类

例如这个类
```java
package aaa;
 
import pokercc.android.custompakcage.CustomPackage;

@CustomPackage("bbb")
public class WxEntryActivity  {
}

```
这个类的全类名是`aaa.WxEntryActivity`
在编译的时候会生成一个`bbb.WxEntryActivity`的类
```java
package bbb;
/* 
* create by CustomPackageProcessor don't modify!! 
*/
public class WxEntryActivity  extends aaa.WxEntryActivity{
}

```
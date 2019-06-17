package pokercc.android.custompackage.share_lib;

import android.app.Activity;

import pokercc.android.custompakcage.CustomPackage;

/**
 * 这个包名需要是主工程的
 */
@CustomPackage(BuildConfig.WECHAT_PACKAGE_NAME_PREFIX + ".api")
public class WxEntryActivity extends Activity {
}

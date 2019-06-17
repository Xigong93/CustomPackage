import org.junit.Test;

import haha.WxEntryActivity;
import pokercc.android.custompackage.demo.BuildConfig;

import static org.junit.Assert.*;


public class WxEntryActivityTest {

    @Test
    public void customPackageClassExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName(BuildConfig.APPLICATION_ID + "." + WxEntryActivity.class.getSimpleName());
        assertNotNull(clazz);
    }
}
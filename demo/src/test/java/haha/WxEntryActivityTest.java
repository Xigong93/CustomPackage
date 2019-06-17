package haha;

import org.junit.Test;

import pokercc.android.custompackage.demo.BuildConfig;

import static org.junit.Assert.assertNotNull;


public class WxEntryActivityTest {

    @Test
    public void customPackageClassExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName(BuildConfig.APPLICATION_ID + "." + WxEntryActivity.class.getSimpleName());
        assertNotNull(clazz);
    }
}
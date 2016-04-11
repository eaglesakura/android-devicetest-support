package com.eaglesakura.android.devicetest;

import org.junit.Rule;

import android.app.Activity;
import android.app.Application;
import android.support.test.rule.ActivityTestRule;

public abstract class DeviceActivityTestCase<ActivityClass extends Activity, AppClass extends Application> extends DeviceTestCase<AppClass> {

    @Rule
    public final ActivityTestRule<ActivityClass> mRule;

    protected DeviceActivityTestCase(Class<ActivityClass> clazz) {
        mRule = new ActivityTestRule<>(clazz);
    }
}

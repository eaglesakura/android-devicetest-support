package com.eaglesakura.android.devicetest;

import org.junit.Rule;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.test.rule.ActivityTestRule;

import static org.junit.Assert.*;

public abstract class DeviceActivityTestCase<ActivityClass extends Activity, AppClass extends Application> extends DeviceTestCase<AppClass> {
    @Rule
    public final ActivityTestRule<ActivityClass> mRule;

    ActivityClass mActivity;

    protected DeviceActivityTestCase(Class<ActivityClass> clazz) {
        mRule = new ActivityTestRule<>(clazz, false, false);
    }

    @Override
    public void onSetup() {
        super.onSetup();
        assertNull(mActivity);
    }

    protected ActivityClass getActivity() {
        return getActivity(null);
    }

    protected ActivityClass getActivity(@Nullable Intent startIntent) {
        if (mActivity == null) {
            mActivity = mRule.launchActivity(startIntent);
            assertNotNull(mActivity);
        }
        return mActivity;
    }
}

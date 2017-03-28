package com.eaglesakura.android.devicetest;

import com.eaglesakura.android.devicetest.scenario.ScenarioContext;
import com.eaglesakura.lambda.Action0;

import org.junit.Rule;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.app.AppCompatActivity;

@SuppressLint("NewApi")
public abstract class DeviceActivityTestCase<ActivityClass extends AppCompatActivity, AppClass extends Application> extends DeviceTestCase<AppClass> {

    private static final String TAG = "ActivityTest";

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

    public void runOnUi(Action0 action) {
        ScenarioContext.runOnUi(action);
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

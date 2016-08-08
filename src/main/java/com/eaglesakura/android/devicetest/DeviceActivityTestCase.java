package com.eaglesakura.android.devicetest;

import com.eaglesakura.android.devicetest.scenario.ActivityScenario;
import com.eaglesakura.android.devicetest.validator.ActivityValidator;
import com.eaglesakura.android.devicetest.validator.FragmentValidator;

import org.junit.Rule;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

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

    protected ActivityValidator validate(Activity activity) {
        return new ActivityValidator((AppCompatActivity) activity);
    }

    protected FragmentValidator validate(Activity activity, Class<? extends Fragment>... fragments) {
        FragmentValidator result = null;
        for (Class<? extends Fragment> clazz : fragments) {
            if (result == null) {
                result = new ActivityValidator((AppCompatActivity) activity).fragmentWithClass(clazz);
            } else {
                result = result.fragmentWithClass(clazz);
            }
        }
        return result;
    }

    /**
     * UIテストを開始する
     */
    protected ActivityScenario<ActivityClass> newScenario() {
        return new ActivityScenario<>(getActivity());
    }

    protected void finishActivity() throws Throwable {
        ActivityClass activity = mActivity;
        if (activity != null) {
            mRule.runOnUiThread(() -> {
                activity.finish();
            });
            mActivity = null;
        }
    }
}

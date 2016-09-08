package com.eaglesakura.android.devicetest;

import com.eaglesakura.android.devicetest.scenario.ActivityScenario;
import com.eaglesakura.android.devicetest.validator.ActivityValidator;
import com.eaglesakura.android.devicetest.validator.BaseUiValidator;
import com.eaglesakura.android.devicetest.validator.FragmentValidator;
import com.eaglesakura.util.CollectionUtil;
import com.eaglesakura.util.LogUtil;
import com.eaglesakura.util.ReflectionUtil;
import com.eaglesakura.util.StringUtil;

import org.junit.Rule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public abstract class DeviceActivityTestCase<ActivityClass extends AppCompatActivity, AppClass extends Application> extends DeviceTestCase<AppClass> {

    private static final String TAG = "ActivityTest";

    @Rule
    public final ActivityTestRule<ActivityClass> mRule;

    ActivityClass mActivity;

    /**
     * 起動中のActivityスタック
     */
    List<Activity> mActivityStack = new ArrayList<>();

    protected DeviceActivityTestCase(Class<ActivityClass> clazz) {
        mRule = new ActivityTestRule<>(clazz, false, false);
    }


    @Override
    public void onSetup() {
        super.onSetup();
        getApplication().unregisterActivityLifecycleCallbacks(mActivityCallback);
        getApplication().registerActivityLifecycleCallbacks(mActivityCallback);
        assertNull(mActivity);
    }

    /**
     * 生成されたActivityStackを取得する
     */
    public List<Activity> getActivityStack() {
        return mActivityStack;
    }

    /**
     * アプリ内の最上位Activityを取得する
     */
    public Activity getTopActivity() {
        return mActivityStack.get(0);
    }

    public <T extends Activity> T getTopActivity(Class<T> clazz) {
        return (T) getTopActivity();
    }

    public void assertTopActivity(Class<? extends AppCompatActivity> clazz) {
        if (!ReflectionUtil.instanceOf(getTopActivity(), clazz)) {
            fail(StringUtil.format("Activity[%s] != instance [%s]", getTopActivity().toString(), clazz.getName()));
        }
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

    protected ActivityValidator validate(AppCompatActivity activity) {
        return new ActivityValidator(activity);
    }

    /**
     * 指定したclassを検索する
     */
    protected <T extends Fragment> T findFragment(Class<T> clazz) {
        return (T) BaseUiValidator.findFragmentByClass(getTopActivity(AppCompatActivity.class).getSupportFragmentManager().getFragments(), clazz);
    }

    protected <T extends View> T findView(Fragment fragment, Class<T> clazz, @IdRes int resId) {
        return (T) fragment.getView().findViewById(resId);
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
     * 現在のTopActivityでシナリオを開始する
     */
    protected <T extends AppCompatActivity> ActivityScenario<T> newScenario(Class<T> clazz) {
        return new ActivityScenario<>(getTopActivity(clazz));
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

    private final Application.ActivityLifecycleCallbacks mActivityCallback = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            LogUtil.out(TAG, "Lifecycle onActivityCreated[%s]", activity.toString());
        }

        @Override
        public void onActivityStarted(Activity activity) {
            LogUtil.out(TAG, "Lifecycle onActivityStarted[%s]", activity.toString());
        }

        @Override
        public void onActivityResumed(Activity activity) {
            // ActivityをTopに移動する
            mActivityStack.remove(activity);
            mActivityStack.add(0, activity);

            LogUtil.out(TAG, "Lifecycle onActivityResumed[%s]", activity.toString());
        }

        @Override
        public void onActivityPaused(Activity activity) {
            LogUtil.out(TAG, "Lifecycle onActivityPaused[%s]", activity.toString());
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            LogUtil.out(TAG, "Lifecycle onActivitySaveInstanceState[%s]", activity.toString());
        }

        @Override
        public void onActivityStopped(Activity activity) {
            LogUtil.out(TAG, "Lifecycle onActivityStopped[%s]", activity.toString());
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivityStack.remove(activity);
            LogUtil.out(TAG, "Lifecycle onActivityDestroyed[%s]", activity.toString());
        }
    };
}

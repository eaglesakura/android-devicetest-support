package com.eaglesakura.android.devicetest;

import com.eaglesakura.android.devicetest.scenario.ActivityScenario;
import com.eaglesakura.android.devicetest.validator.ActivityValidator;
import com.eaglesakura.android.devicetest.validator.BaseUiValidator;
import com.eaglesakura.android.devicetest.validator.FragmentValidator;
import com.eaglesakura.android.util.ViewUtil;
import com.eaglesakura.lambda.Action0;
import com.eaglesakura.lambda.Matcher1;
import com.eaglesakura.util.LogUtil;
import com.eaglesakura.util.ReflectionUtil;
import com.eaglesakura.util.StringUtil;
import com.eaglesakura.util.Util;

import org.junit.Rule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.internal.runner.junit4.statement.UiThreadStatement;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
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


    /**
     * デバイス制御
     */
    UiDevice mDevice;

    protected DeviceActivityTestCase(Class<ActivityClass> clazz) {
        mRule = new ActivityTestRule<>(clazz, false, false);
    }

    @Override
    public void onSetup() {
        super.onSetup();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

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

    public void runOnUi(Action0 action) {
        try {
            UiThreadStatement.runOnUiThread(() -> {
                try {
                    action.action();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
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

    List<View> getRootViewList() {
        try {
            Class WindowManagerGlobal = Class.forName("android.view.WindowManagerGlobal");
            Object WindowManagerGlobal_instance = WindowManagerGlobal.getMethod("getInstance").invoke(WindowManagerGlobal);

            Field WindowManagerGlobal_mViews = WindowManagerGlobal.getDeclaredField("mViews");
            WindowManagerGlobal_mViews.setAccessible(true);
            List<View> views = (List<View>) WindowManagerGlobal_mViews.get(WindowManagerGlobal_instance);
            views = new ArrayList<>(views); // 直接削除するとFrameworkの不整合が起きるので、データをクローンしておく

            // 見えていないDecorView（隠れているActivity等）は除外する
            Iterator<View> iterator = views.iterator();
            while (iterator.hasNext()) {
                View next = iterator.next();
                if (next.getVisibility() != View.VISIBLE) {
                    iterator.remove();
                }
            }

            return views;
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
            throw new Error();
        }
    }

    View findViewByMatcher(Matcher1<View> matcher) {
        for (View root : getRootViewList()) {
            View find = ViewUtil.findViewByMatcher(root, matcher);
            if (find != null) {
                return find;
            }
        }
        fail("Not found view");
        throw new Error();
    }

    View findViewByText(String text) {
        return findViewByMatcher(it -> {
            if (it instanceof TextView) {
                return ((TextView) it).getText().toString().toUpperCase().equals(text.toUpperCase());
            } else {
                return false;
            }
        });
    }

    /**
     * 指定したテキストを探し、その位置をタップする
     */
    public void clickWithText(String text) {
        clickWith(findViewByText(text));
    }

    /**
     * 指定したテキストを探し、その位置をタップする
     */
    public void clickWithText(@StringRes int resId) {
        clickWithText(getContext().getString(resId));
    }

    /**
     * 指定した経路のViewを踏む
     */
    public void clickWithId(@IdRes int... idList) {
        // 確定したViewを踏む
        for (View view : getRootViewList()) {
            View find = ViewUtil.findViewById(view, idList);
            if (find != null) {
                clickWith(find);
                return;
            }
        }
        fail();
    }

    /**
     * 指定した条件にマッチするView位置をクリックするｓ
     */
    public void clickWithMatcher(Matcher1<View> matcher) {
        View find = findViewByMatcher(matcher);
        assertNotNull(find);
        clickWith(find);
    }

    public void pressBack() {
        mDevice.pressBack();
        sleep(250);
    }

    /**
     * 指定したViewを探し、その位置をタップする
     *
     * MEMO: http://malta-yamato.hatenablog.com/entry/2016/07/30/135055
     */
    public void clickWith(View view) {
        assertNotNull(view);

        Rect area = new Rect();
        runOnUi(() -> {
            int[] viewInWindow = new int[2];
            int[] viewOnScreen = new int[2];
            int[] windowOnScreen = new int[2];

            view.getLocationInWindow(viewInWindow);
            view.getLocationOnScreen(viewOnScreen);
            windowOnScreen[0] = viewOnScreen[0] - viewInWindow[0];
            windowOnScreen[1] = viewOnScreen[1] - viewInWindow[1];

            view.getGlobalVisibleRect(area);
            area.offset(windowOnScreen[0], windowOnScreen[1]);
        });
        mDevice.click(area.centerX(), area.centerY());
        Log.d("UiTest", StringUtil.format("Click %s pos[%d, %d]", area.toString(), area.centerX(), area.centerY()));
        Util.sleep(1000);
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

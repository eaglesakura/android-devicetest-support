package com.eaglesakura.android.devicetest.scenario;

import com.eaglesakura.android.devicetest.validator.BaseUiValidator;
import com.eaglesakura.android.util.ViewUtil;
import com.eaglesakura.lambda.Action0;
import com.eaglesakura.lambda.Matcher1;
import com.eaglesakura.thread.Holder;
import com.eaglesakura.util.LogUtil;
import com.eaglesakura.util.ReflectionUtil;
import com.eaglesakura.util.StringUtil;
import com.eaglesakura.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;

/**
 * シナリオテストを実行する
 */
@SuppressLint("NewApi")
public class ScenarioContext {
    private static ScenarioContext sInstance;

    private static final String TAG = "ScenarioTest";

    static Handler sUiHandler = new Handler(Looper.getMainLooper());

    /**
     * デバイス制御
     */
    static UiDevice sDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    /**
     * 起動中のActivityスタック
     */
    static List<Activity> sActivityStack = new ArrayList<>();

    private ScenarioContext() {
    }

    public static Application getContext() {
        return (Application) InstrumentationRegistry.getTargetContext().getApplicationContext();
    }

    /**
     * UnitTest開始時に呼び出す
     */
    public static void onSetup() {
        sActivityStack.clear();

        getContext().unregisterActivityLifecycleCallbacks(sActivityCallback);
        getContext().registerActivityLifecycleCallbacks(sActivityCallback);
    }

    /**
     * UnitTest終了時に呼び出す
     */
    public static void onShutdown() {
        getContext().unregisterActivityLifecycleCallbacks(sActivityCallback);
        sActivityStack.clear();
    }

    /**
     * 現在最上位にあるActivityを取得する
     */
    public static Activity getTopActivity() {
        assertFalse(sActivityStack.isEmpty());
        return sActivityStack.get(0);
    }

    public static void assertTopActivity(Class<? extends AppCompatActivity> clazz) {
        if (!ReflectionUtil.instanceOf(getTopActivity(), clazz)) {
            fail(StringUtil.format("Activity[%s] != instance [%s]", getTopActivity().toString(), clazz.getName()));
        }
    }

    public static void runOnUi(Action0 action) {
        try {
            Holder holder = new Holder();
            sUiHandler.post(() -> {
                try {
                    action.action();
                    holder.set(new Object());
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
            holder.getWithWait(1000 * 60);
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 現在最上位にあるActivityを取得する
     */
    public static <T extends Activity> T getTopActivity(Class<T> clazz) {
        assertFalse(sActivityStack.isEmpty());
        return (T) sActivityStack.get(0);
    }

    private static List<View> getRootViewList() {
        try {
            Class WindowManagerGlobal = Class.forName("android.view.WindowManagerGlobal");
            Object WindowManagerGlobal_instance = WindowManagerGlobal.getMethod("getInstance").invoke(WindowManagerGlobal);

            Field WindowManagerGlobal_mViews = WindowManagerGlobal.getDeclaredField("mViews");
            WindowManagerGlobal_mViews.setAccessible(true);
            List<View> views = (List<View>) WindowManagerGlobal_mViews.get(WindowManagerGlobal_instance);
            List<View> result = new ArrayList<>(); // 必要なViewだけを返す

            // 見えていないDecorView（隠れているActivity等）は除外する
            for (View view : views) {
                if (view.getVisibility() == View.VISIBLE) {
                    result.add(0, view);    // 手前にあるViewを優先して探索させる
                }
            }
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Error();
        }
    }

    public static <T extends Fragment> T findFragment(Class<T> clazz) {
        return BaseUiValidator.findFragmentByClass(getTopActivity(AppCompatActivity.class).getSupportFragmentManager().getFragments(), clazz);
    }

    public static View findViewByMatcher(Matcher1<View> matcher) {
        for (View root : getRootViewList()) {
            View find = ViewUtil.findViewByMatcher(root, matcher);
            if (find != null) {
                return find;
            }
        }
        return null;
    }


    public static View findViewByText(String text) {
        return findViewByMatcher(it -> {
            if (it instanceof TextView) {
//                LogUtil.out("FindText", "CHECK[%s] FIND[%s]", ((TextView) it).getText().toString().toUpperCase(), text.toUpperCase());
                return ((TextView) it).getText().toString().toUpperCase().equals(text.toUpperCase());
            } else {
                return false;
            }
        });
    }

    /**
     * 指定した経路のViewを踏む
     */
    public static View findViewById(@IdRes int... idList) {
        // 確定したViewを踏む
        for (View view : getRootViewList()) {
            View find = ViewUtil.findViewById(view, idList);
            if (find != null) {
                return find;
            }
        }
        return null;
    }

    public static UiScenario pressBack() {
        sDevice.pressBack();
        return new UiScenario(null).shortStep();
    }

    /**
     * 指定したViewを探し、その位置をタップする
     *
     * MEMO: http://malta-yamato.hatenablog.com/entry/2016/07/30/135055
     */
    static void clickWith(View view) {
        clickWith(view, 0.5, 0.5);
    }

    /**
     * 指定したViewを探し、その位置をタップする
     *
     * MEMO: http://malta-yamato.hatenablog.com/entry/2016/07/30/135055
     */
    static void clickWith(View view, double u, double v) {
        clickWith(view, u, v, 250);
    }

    /**
     * 指定したViewを探し、その位置をタップする
     *
     * MEMO: http://malta-yamato.hatenablog.com/entry/2016/07/30/135055
     */
    static void clickWith(View view, double u, double v, long sleepTimeMs) {
        assertNotNull(view);
        Rect area = ViewUtil.getScreenArea(view);
        int clickX = area.left + (int) (u * area.width());
        int clickY = area.top + (int) (v * area.height());
        sDevice.click(clickX, clickY);
        Log.d("UiTest", StringUtil.format("Click %s pos[%d, %d]", area.toString(), clickX, clickY));
        Util.sleep(sleepTimeMs);
    }

    static final Application.ActivityLifecycleCallbacks sActivityCallback = new Application.ActivityLifecycleCallbacks() {
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
            sActivityStack.remove(activity);
            sActivityStack.add(0, activity);

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
            sActivityStack.remove(activity);
            LogUtil.out(TAG, "Lifecycle onActivityDestroyed[%s]", activity.toString());
        }
    };
}

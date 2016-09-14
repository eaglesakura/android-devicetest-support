package com.eaglesakura.android.devicetest.scenario;

import com.eaglesakura.android.device.display.DisplayInfo;
import com.eaglesakura.android.devicetest.validator.BaseUiValidator;
import com.eaglesakura.android.util.ViewUtil;
import com.eaglesakura.lambda.Action1;
import com.eaglesakura.lambda.Matcher1;
import com.eaglesakura.math.Vector2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import static com.eaglesakura.android.devicetest.scenario.ScenarioContext.getTopActivity;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Viewに関するアクションを行う
 */
public class UiScenario extends BaseScenario<UiScenario> {
    View mView;

    UiScenario(View current) {
        mView = current;
    }

    @Override
    public Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    public UiScenario childId(@IdRes int... idList) {
        View child = ViewUtil.findViewById(mView, idList);
        assertNotNull(child);
        return new UiScenario(child);
    }

    public UiScenario childText(String text) {
        return child(it -> {
            if (it instanceof TextView) {
                return ((TextView) it).getText().toString().toUpperCase().equals(text.toUpperCase());
            } else {
                return false;
            }
        });
    }

    public UiScenario childText(@StringRes int resId) {
        return childText(getContext().getString(resId));
    }

    public UiScenario child(Matcher1<View> matcher) {
        View child = ViewUtil.findViewByMatcher(mView, matcher);
        assertNotNull(child);
        return new UiScenario(child);
    }

    /**
     * クリックを行う
     */
    public UiScenario click() {
        ScenarioContext.clickWith(mView);
        return this;
    }

    /**
     * クリックを行う
     */
    public UiScenario click(double u, double v) {
        ScenarioContext.clickWith(mView, u, v);
        return this;
    }

    /**
     * 指定されたView範囲内をランダムに叩く
     *
     * @param num クリック回数
     */
    public UiScenario monkeyClick(int num) {
        return monkeyClick(num, 30, 60);
    }

    /**
     * 指定されたView範囲内をランダムに叩く
     *
     * @param num            クリック回数
     * @param minSleepTimeMs クリック毎の最小スリープ時間
     * @param maxSleepTimeMs クリック毎の最大スリープ時間
     */
    public UiScenario monkeyClick(int num, long minSleepTimeMs, long maxSleepTimeMs) {
        for (int i = 0; i < num; ++i) {
            ScenarioContext.clickWith(mView, Math.random(), Math.random(), 0);
            sleep(minSleepTimeMs + (int) ((maxSleepTimeMs - minSleepTimeMs) * Math.random()));
        }
        return this;
    }

    /**
     * 指定されたView範囲内をランダムにダブルクリックする
     *
     * @param num            クリック回数
     * @param minSleepTimeMs クリック毎の最小スリープ時間
     * @param maxSleepTimeMs クリック毎の最大スリープ時間
     */
    public UiScenario monkeyDoublesClick(int num, long minSleepTimeMs, long maxSleepTimeMs) {
        for (int i = 0; i < num; ++i) {
            double u = Math.random();
            double v = Math.random();
            ScenarioContext.clickWith(mView, u, v, 30 + (int) (Math.random() * 15.0));
            ScenarioContext.clickWith(mView, u, v, 0);
            sleep(minSleepTimeMs + (int) ((maxSleepTimeMs - minSleepTimeMs) * Math.random()));
        }
        return this;
    }

    public View get() {
        return mView;
    }

    /**
     * 画面内にViewが存在している
     */
    public UiScenario inDisplay() {
        assertNotNull(mView);
        Rect area = ViewUtil.getScreenArea(mView);
        DisplayInfo info = new DisplayInfo(getContext());
        assertTrue(
                area.intersect(0, 0, info.getWidthPixel(), info.getHeightPixel())
        );
        return this;
    }

    public UiScenario check(Action1<View> action) {
        try {
            action.action(mView);
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
        return this;
    }

    public UiScenario notNull() {
        assertNotNull(mView);
        return this;
    }

    public <T extends View> UiScenario check(Class<T> clazz, Action1<T> action) {
        try {
            action.action((T) mView);
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
        return this;
    }

    public UiScenario checkOnUi(Action1<View> action) {
        ScenarioContext.runOnUi(() -> {
            action.action(mView);
        });
        return this;
    }

    public <T extends View> UiScenario checkOnUi(Class<? extends View> clazz, Action1<T> action) {
        ScenarioContext.runOnUi(() -> {
            action.action((T) mView);
        });
        return this;
    }

    /**
     * 右から左へ指を動かす
     */
    public UiScenario swipeRightToLeft() {
        Rect area = ViewUtil.getScreenArea(mView);
        Vector2 move = new Vector2(area.width() * 0.15f, area.height() * 0.15f);

        SwipeBuilder
                .fromPosition(area.right - (int) move.x, area.centerY())
                .nextPosition(area.left + (int) move.x, area.centerY())
                .execute();
        return this;
    }

    /**
     * 左から右へ指を動かす
     */
    public UiScenario swipeLeftToRight() {
        Rect area = ViewUtil.getScreenArea(mView);
        Vector2 move = new Vector2(area.width() * 0.15f, area.height() * 0.15f);

        SwipeBuilder
                .fromPosition(area.left + (int) move.x, area.centerY())
                .nextPosition(area.right - (int) move.x, area.centerY())
                .execute();
        return this;
    }

    /**
     * 下から上へ動かす
     */
    public UiScenario swipeBottomToTop() {
        Rect area = ViewUtil.getScreenArea(mView);
        Vector2 move = new Vector2(area.width() * 0.15f, area.height() * 0.15f);

        SwipeBuilder
                .fromPosition(area.centerX(), area.bottom - (int) move.y)
                .nextPosition(area.centerX(), area.top + (int) move.y)
                .execute();
        return this;
    }

    /**
     * 上から下へ動かす
     */
    public UiScenario swipeTopToBottom() {
        Rect area = ViewUtil.getScreenArea(mView);
        Vector2 move = new Vector2(area.width() * 0.15f, area.height() * 0.15f);

        SwipeBuilder
                .fromPosition(area.centerX(), area.top + (int) move.y)
                .nextPosition(area.centerX(), area.bottom - (int) move.x)
                .execute();
        return this;
    }

    public static UiScenario fromMatcher(Matcher1<View> finder) {
        return new UiScenario(ScenarioContext.findViewByMatcher(finder));
    }

    public static UiScenario fromText(String text) {
        return new UiScenario(ScenarioContext.findViewByText(text));
    }

    public static UiScenario fromText(@StringRes int resId) {
        return fromText(ScenarioContext.getContext().getString(resId));
    }

    public static UiScenario fromId(@IdRes int... resId) {
        return new UiScenario(ScenarioContext.findViewById(resId));
    }

    public static UiScenario from(View view) {
        return new UiScenario(view);
    }

    /**
     * Fragmentのアクションを行う
     *
     * UI無しのFragmentの場合、親Fragmentを追いかけてViewを取り出す。
     */
    public static UiScenario from(Fragment fragment) {
        View view = fragment.getView();
        while (view == null) {
            if (fragment.getParentFragment() != null) {
                fragment = fragment.getParentFragment();
                view = fragment.getView();
            } else {
                return from(fragment.getActivity());
            }
        }

        assertNotNull(view);
        return new UiScenario(view);
    }

    /**
     * Activity全体のアクションを行う
     */
    public static UiScenario from(Activity activity) {
        return from(activity.getWindow().getDecorView());
    }

    public static UiScenario from(Activity activity, Class<? extends Fragment> fragmentClass) {
        return from(BaseUiValidator.findFragmentByClass(((AppCompatActivity) activity).getSupportFragmentManager().getFragments(), fragmentClass));
    }

    public static UiScenario from(Class<? extends Fragment> fragmentClass) {
        return from(BaseUiValidator.findFragmentByClass(getTopActivity(AppCompatActivity.class).getSupportFragmentManager().getFragments(), fragmentClass));
    }

    public static UiScenario clickFromId(@IdRes int resId) {
        return fromId(resId).click();
    }

    public static UiScenario clickFromText(@StringRes int resId) {
        return fromText(resId).click();
    }


    public static UiScenario clickFromText(String text) {
        return fromText(text).click();
    }
}

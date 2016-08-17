package com.eaglesakura.android.devicetest.scenario;

import com.eaglesakura.util.Util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import static org.junit.Assert.assertNotNull;

/**
 * シナリオ実行を行う
 */
public class ActivityScenario<T extends Activity> extends BaseScenario<ActivityScenario<T>> {
    T mActivity;

    long mDefaultSleepTime = 500;

    public ActivityScenario(T activity) {
        mActivity = activity;
        assertNotNull(activity);
    }

    public <V extends View> ViewScenario<T, V> viewWith(V view) {
        return new ViewScenario<>(
                this,
                Espresso.onView(new BaseMatcher<View>() {
                    @Override
                    public boolean matches(Object item) {
                        return view.equals(item);
                    }

                    @Override
                    public void describeTo(Description description) {

                    }
                })
        );
    }

    public <V extends View> ViewScenario<T, V> viewWithId(Class<V> clazz, @IdRes int resId) {
        return new ViewScenario(
                this,
                Espresso.onView(ViewMatchers.withId(resId))
        );
    }

    public <V extends View> ViewScenario<T, V> viewWithId(@IdRes int resId) {
        return new ViewScenario(
                this,
                Espresso.onView(ViewMatchers.withId(resId))
        );
    }

    public ActivityScenario<T> pressBack() {
        Espresso.pressBack();
        Util.sleep(mDefaultSleepTime);
        return this;
    }

    public ActivityScenario<T> sleep(long ms) {
        Util.sleep(ms);
        return this;
    }

}

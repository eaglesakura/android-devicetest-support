package com.eaglesakura.android.devicetest.scenario;

import com.eaglesakura.android.devicetest.validator.BaseUiValidator;
import com.eaglesakura.lambda.ResultAction1;
import com.eaglesakura.util.StringUtil;
import com.eaglesakura.util.Util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * シナリオ実行を行う
 */
public class EspressoActivityScenario<T extends AppCompatActivity> extends BaseScenario<EspressoActivityScenario<T>> {
    T mActivity;

    long mDefaultSleepTime = 500;

    public EspressoActivityScenario(T activity) {
        mActivity = activity;
        assertNotNull(activity);
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    public <V extends View, F extends Fragment> EspressoViewScenario<T, V> viewWith(ResultAction1<F, V> viewFinder, Class<F> clazz) {
        Fragment fragmentByClass = BaseUiValidator.findFragmentByClass(mActivity.getSupportFragmentManager().getFragments(), clazz);
        if (fragmentByClass == null) {
            fail(StringUtil.format("Fragment not found[%s]", clazz.getName()));
        }

        try {
            return viewWith(viewFinder.action((F) fragmentByClass));
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
            throw new Error(e);
        }
    }

    public <V extends View> EspressoViewScenario<T, V> viewWith(V view) {
        return new EspressoViewScenario<>(
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

    public <V extends View> EspressoViewScenario<T, V> viewWithId(Class<V> clazz, @IdRes int resId) {
        return new EspressoViewScenario(
                this,
                Espresso.onView(ViewMatchers.withId(resId))
        );
    }

    public <V extends View> EspressoViewScenario<T, V> viewWithId(@IdRes int resId) {
        return new EspressoViewScenario(
                this,
                Espresso.onView(ViewMatchers.withId(resId))
        );
    }

    public EspressoActivityScenario<T> sleep(long ms) {
        Util.sleep(ms);
        return this;
    }

}

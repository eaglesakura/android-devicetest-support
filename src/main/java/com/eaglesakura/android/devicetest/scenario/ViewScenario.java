package com.eaglesakura.android.devicetest.scenario;

import com.eaglesakura.lambda.Action1;

import android.app.Activity;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.view.View;

import static org.junit.Assert.fail;

/**
 * View関連の動作シナリオ
 */
public class ViewScenario<ActivityClass extends Activity, ViewClass extends View> {
    ActivityScenario<ActivityClass> mActivityScenario;

    ViewInteraction mViewInteraction;

    public ViewScenario(ActivityScenario<ActivityClass> activityScenario, ViewInteraction viewInteraction) {
        mActivityScenario = activityScenario;
        mViewInteraction = viewInteraction;
    }

    public ViewScenario<ActivityClass, ViewClass> click() {
        mViewInteraction.perform(ViewActions.click());
        return this;
    }

    public <T extends View> ViewScenario<ActivityClass, ViewClass> check(Action1<ViewClass> action) {
        mViewInteraction.check((view, noViewFoundException) -> {
            try {
                action.action((ViewClass) view);
            } catch (Throwable e) {
                e.printStackTrace();
                fail();
            }
        });
        return this;
    }

    /**
     * 次のシナリオへ映る
     */
    public ActivityScenario<ActivityClass> doneView() {
        return mActivityScenario;
    }
}

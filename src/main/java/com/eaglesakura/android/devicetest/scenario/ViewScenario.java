package com.eaglesakura.android.devicetest.scenario;

import com.eaglesakura.lambda.Action1;

import android.content.Context;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static org.junit.Assert.fail;

/**
 * View関連の動作シナリオ
 */
public class ViewScenario<ActivityClass extends AppCompatActivity, ViewClass extends View> extends BaseScenario<ViewScenario<ActivityClass, ViewClass>> {
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

    @Override
    public Context getContext() {
        return mActivityScenario.mActivity;
    }

    public ViewScenario<ActivityClass, ViewClass> perform(ViewAction action) {
        mViewInteraction.perform(action);
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

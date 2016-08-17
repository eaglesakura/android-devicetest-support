package com.eaglesakura.android.devicetest.validator;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity関連のデータのバリデーションを行う
 */
public class ActivityValidator extends BaseUiValidator<AppCompatActivity, ActivityValidator> {
    AppCompatActivity mActivity;

    public ActivityValidator(AppCompatActivity activity) {
        mActivity = activity;
    }

    @Override
    protected FragmentManager getFragmentManager() {
        return mActivity.getSupportFragmentManager();
    }

    @Override
    protected ActivityValidator getActivityValidator() {
        return this;
    }

    @Override
    protected AppCompatActivity getTarget() {
        return mActivity;
    }
}

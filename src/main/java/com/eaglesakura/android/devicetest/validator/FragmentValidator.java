package com.eaglesakura.android.devicetest.validator;

import com.eaglesakura.lambda.Action1;

import org.junit.Assert;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Fragment関連のvalidateを行う
 */
public class FragmentValidator extends BaseUiValidator<Fragment> {
    ActivityValidator mParent;

    Fragment mFragment;

    public FragmentValidator(ActivityValidator parent, Fragment fragment) {
        mParent = parent;
        mFragment = fragment;
    }

    @Override
    protected FragmentManager getFragmentManager() {
        return mFragment.getChildFragmentManager();
    }

    @Override
    protected ActivityValidator getActivityValidator() {
        return mParent;
    }

    @Override
    protected Fragment getTarget() {
        return mFragment;
    }

    public <FragmentClass extends Fragment> FragmentValidator check(Class<FragmentClass> clazz, Action1<FragmentClass> action) {
        try {
            action.action((FragmentClass) mFragment);
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail();
        }
        return this;
    }

    public ActivityValidator doneFragment() {
        return mParent;
    }

}

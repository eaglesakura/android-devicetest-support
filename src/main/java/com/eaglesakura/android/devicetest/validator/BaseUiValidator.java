package com.eaglesakura.android.devicetest.validator;

import com.eaglesakura.util.StringUtil;
import com.eaglesakura.util.Util;

import org.junit.Assert;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;


public abstract class BaseUiValidator<T, Self> {
    protected abstract FragmentManager getFragmentManager();

    protected abstract ActivityValidator getActivityValidator();

    protected abstract T getTarget();

    protected Self self() {
        return (Self) this;
    }

    /**
     * 指定したClassにマッチするFragmentを検索する
     */
    public FragmentValidator fragmentWithClass(Class<? extends Fragment> clazz) {
        Fragment fragment = findFragmentByClass(getFragmentManager().getFragments(), clazz);
        if (fragment == null) {
            Assert.fail(StringUtil.format("obj[%s] not found [%s]", getTarget().toString(), clazz.getName()));
        }

        return new FragmentValidator(getActivityValidator(), fragment);
    }


    public Self sleep(long sleepTimeMs) {
        Util.sleep(sleepTimeMs);
        return self();
    }

    public Self shortStep() {
        return sleep(250);
    }

    public Self step() {
        return sleep(500);
    }

    public Self longStep() {
        return sleep(1000);
    }

    /**
     * 指定したclassのFragmentを検索する
     */
    @Nullable
    public static <T extends Fragment> T findFragmentByClass(List<Fragment> fragments, Class<T> clazz) {
        if (fragments == null) {
            return null;
        }

        for (Fragment frag : fragments) {
            if (frag == null) {
                continue;
            }

            if (frag.getClass().equals(clazz)) {
                return (T) frag;
            }

            Fragment check = findFragmentByClass(frag.getChildFragmentManager().getFragments(), clazz);
            if (check != null) {
                return (T) check;
            }
        }

        return null;
    }
}

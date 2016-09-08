package com.eaglesakura.android.devicetest.scenario;

import com.eaglesakura.android.util.AndroidUtil;
import com.eaglesakura.util.Util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public abstract class BaseScenario<Self> {
    final Handler mUiHandler = new Handler(Looper.getMainLooper());

    Self self() {
        return (Self) this;
    }

    public abstract Context getContext();

    public Self sleep(long timeMs) {
        Util.sleep(timeMs);
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

    public Self requestUserOperation(String message) {
        AndroidUtil.playDefaultNotification(getContext());
        try {
            AndroidUtil.vibrate(getContext(), 500);
        } catch (Throwable e) {

        }
        mUiHandler.post(() -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        });
        return self();
    }
}

package com.eaglesakura.android.devicetest.scenario;

import com.eaglesakura.util.Util;

public class BaseScenario<Self> {
    Self self() {
        return (Self) this;
    }

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
}

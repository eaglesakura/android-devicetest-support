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
}

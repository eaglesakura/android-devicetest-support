package com.eaglesakura.android.devicetest;

import org.junit.Rule;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.ServiceTestRule;

public abstract class DeviceServiceTestCase<AppClass extends Application> extends DeviceTestCase<AppClass> {

    @Rule
    public final ServiceTestRule mRule = new ServiceTestRule();

    protected DeviceServiceTestCase() {
    }
}

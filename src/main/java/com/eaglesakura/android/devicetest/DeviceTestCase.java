package com.eaglesakura.android.devicetest;

import com.eaglesakura.util.IOUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.hamcrest.core.Is;

import android.app.Application;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import java.io.File;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public abstract class DeviceTestCase<AppClass extends Application> {

    private Context mContext;

    private File mCacheDirectory;

    private Thread mTestingThread;


    @Before
    public void onSetup() {
        mContext = InstrumentationRegistry.getContext();
        assertNotNull(mContext);

        mTestingThread = Thread.currentThread();
        mCacheDirectory = TestUtil.getCacheDirectory(getContext());

        assertNotNull(mTestingThread);
        assertNotNull(mCacheDirectory);
    }

    @After
    public void onShutdown() {
    }

    public AppClass getApplication() {
        return (AppClass) getContext();
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * UnitTest側のContext(assets等）へのアクセスを行う
     */
    public Context getTestContext() {
        return InstrumentationRegistry.getContext();
    }

    public File getCacheDirectory() {
        return mCacheDirectory;
    }

    public Thread getTestingThread() {
        return mTestingThread;
    }

    public static org.hamcrest.Matcher<Boolean> isTrue() {
        return Is.is(true);
    }

    public void cleanCache() {
        IOUtil.cleanDirectory(mCacheDirectory);
    }
}

package com.eaglesakura.android.devicetest;

import com.eaglesakura.util.IOUtil;
import com.eaglesakura.util.LogUtil;

import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;

public abstract class ModuleTestCase extends AndroidTestCase {

    private File mCacheDirectory;

    private Thread mTestingThread;

    protected String LOG_TAG = getClass().getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mTestingThread = Thread.currentThread();
        LogUtil.setLogger(new LogUtil.AndroidLogger(Log.class).setStackInfo(true));
        mCacheDirectory = TestUtil.getCacheDirectory(getContext());
    }

    public Thread getTestingThread() {
        return mTestingThread;
    }

    /**
     * UnitTest用のスレッドで実行されている場合はtrue
     */
    public boolean isTestingThread() {
        return Thread.currentThread().equals(mTestingThread);
    }

    public File getCacheDirectory() {
        return mCacheDirectory;
    }

    public void cleanCache() {
        IOUtil.cleanDirectory(mCacheDirectory);
    }
}

package com.eaglesakura.android.devicetest;

import com.eaglesakura.util.IOUtil;

import android.test.AndroidTestCase;

import java.io.File;

@Deprecated
public abstract class ModuleTestCase extends AndroidTestCase {

    private File mCacheDirectory;

    private Thread mTestingThread;

    protected String LOG_TAG = getClass().getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mTestingThread = Thread.currentThread();
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

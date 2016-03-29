package com.eaglesakura.android.devicetest;

import com.eaglesakura.util.IOUtil;
import com.eaglesakura.util.LogUtil;

import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;

public abstract class ModuleTestCase extends AndroidTestCase {

    private File mCacheDirectory;

    private Thread mTestingThread;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mTestingThread = Thread.currentThread();

        final String TAG = getClass().getSimpleName();
        LogUtil.setLogger(
                new LogUtil.Logger() {
                    @Override
                    public void out(int level, String tag, String msg) {
                        try {
                            StackTraceElement[] trace = new Exception().getStackTrace();
                            StackTraceElement elem = trace[Math.min(trace.length - 1, 3)];
                            Log.i(TAG, String.format("%s[%d] : %s", elem.getFileName(), elem.getLineNumber(), msg));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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

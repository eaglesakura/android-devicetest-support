package com.eaglesakura.android.devicetest;

import com.eaglesakura.android.device.external.StorageInfo;
import com.eaglesakura.util.IOUtil;
import com.eaglesakura.util.LogUtil;

import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestUtil {

    private static final Date gInitDate = new Date();

    private static File gCacheRoot;

    private static File gCacheDirectory;

    public static synchronized File getCacheDirectory(Context context) {
        if (gCacheRoot == null) {
            gCacheRoot = new File(StorageInfo.getExternalStorageRoot(context), "junit");
        }

        if (gCacheDirectory == null) {
            SimpleDateFormat DEFAULT_FORMATTER = new SimpleDateFormat("yyyy-MM-dd-HHmmssSSSS");
            gCacheDirectory =
                    new File(gCacheRoot,
                            String.format("%s", DEFAULT_FORMATTER.format(gInitDate))
                    );
            IOUtil.mkdirs(gCacheDirectory);
            LogUtil.out("ModuleTest", "Cache Directory -> %s", gCacheDirectory.getAbsolutePath());
        }

        return gCacheDirectory;
    }
}

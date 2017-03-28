package com.eaglesakura.android.devicetest.util;

import com.eaglesakura.android.device.external.Storage;
import com.eaglesakura.util.IOUtil;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeviceTestUtil {

    private static final Date gInitDate = new Date();

    private static File gCacheRoot;

    private static File gCacheDirectory;

    public static synchronized File getCacheDirectory(Context context) {
        if (gCacheRoot == null) {
            gCacheRoot = new File(Storage.getExternalDataStorage(context).getPath(), "junit");
        }

        if (gCacheDirectory == null) {
            SimpleDateFormat DEFAULT_FORMATTER = new SimpleDateFormat("yyyy-MM-dd-HHmmssSSSS");
            gCacheDirectory =
                    new File(gCacheRoot,
                            String.format("%s", DEFAULT_FORMATTER.format(gInitDate))
                    );
            IOUtil.mkdirs(gCacheDirectory);
            Log.d("ModuleTest", "Cache Directory -> :: " + gCacheDirectory.getAbsolutePath());
        }

        return gCacheDirectory;
    }
}

package au.com.ahbeard.sleepsense.services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import au.com.ahbeard.sleepsense.services.log.SSLog;

/**
 * Created by luisramos on 7/09/2016.
 */
public class DebugEmailService {

    private static final int BUFFER = 80000;
    private static final String ZIP_TMP_DIR = Environment.getExternalStorageDirectory() + "/.temp";
    private static final String ZIP_FILE_NAME = ZIP_TMP_DIR + "/sleepsense-android.zip";

    public static Intent getDebugEmailIntent(final Context context) {
        String logFilePath = SSLog.getLogFilePath();

        File tmpDir = new File(ZIP_TMP_DIR);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        File zipFile = zip(ZIP_FILE_NAME, logFilePath);
        if (zipFile == null) {
            return null;
        }

        Uri zipUri = Uri.fromFile(zipFile);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"luis@mentallyfriendly.com"});
        emailIntent.putExtra(Intent.EXTRA_STREAM, zipUri);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sleepsense Android app.zip");
        return emailIntent;
    }

    private static File zip(String zipFileName, String... files) {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < files.length; i++) {
//                Log.v("Compress", "Adding: " + files[i]);
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new File(zipFileName);
    }
}

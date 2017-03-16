package au.com.ahbeard.sleepsense.services;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import au.com.ahbeard.sleepsense.BuildConfig;
import au.com.ahbeard.sleepsense.services.log.SSLog;

/**
 * Created by luisramos on 7/09/2016.
 */
public class DebugEmailService {

	private static final int BUFFER = 80000;
	private static final String ZIP_TMP_DIR = Environment.getExternalStorageDirectory() + "/.temp";
	private static final String ZIP_FILE_NAME = ZIP_TMP_DIR + "/sleepsense-android.zip";
	private static final String DEVICE_INFO_DIR = Environment.getExternalStorageDirectory() + "/au.com.ahbeard.sleepsense.staging/";
	private static final String DEVICE_INFO_FILE_NAME = "DeviceInfo.txt";
	private static final String DEVICE_INFO_PATH = DEVICE_INFO_DIR + DEVICE_INFO_FILE_NAME;
	private static final int MAX_ZIP_SIZE = 25_165_824; //24MB in B, leaving some extra space
	private static final String DEV_EMAIL = BuildConfig.DEBUG ? "sabbib@mentallyfriendly.com" : "luis@mentallyfriendly.com";

	private static int ERROR_CODE = 0;


	/***
	 *
	 * ERROR_CODE = 110
	 *      Occurs when the log file are bigger than 24MB and exceeds the email attachment limits
	 * ERROR_CODE = 111
	 *      Occurs due to FileNotFoundException
	 * ERROR_CODE = 112
	 *      Occurs due to IOException
	 * ERROR_CODE = 1
	 *      Occurrence unknown
	 *
	 * @return errorMessage with the specified error
	 *
	 */
	private static int getErrorCode() {
		return (ERROR_CODE != 0) ? ERROR_CODE : 1 ;
	}

	public interface OnIntentCreatedCallback {
		void onIntentCreated(Intent intent);
	}

	public static boolean getDebugEmailIntent(final Context context, final OnIntentCreatedCallback callback){
		final ProgressDialog progressDialog = createAndShowProgressDialog(context);

		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				final Uri uri = createZipFile();

				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						//TODO check for errors on the uri
						if(uri != null) {
							callback.onIntentCreated(createIntent(uri));
							progressDialog.dismiss();
						}else {
							new AlertDialog.Builder(context)
									.setTitle("Error!")
									.setMessage("Error creating log files\nErrorCode: " + getErrorCode())
									.show();
						}
					}
				});
			}
		});
		return true;
	}

	private static Intent createIntent(Uri zipUri) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("vnd.android.cursor.dir/email");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{DEV_EMAIL});
		emailIntent.putExtra(Intent.EXTRA_STREAM, zipUri);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "A.H. Beard App Support");
		return emailIntent;
	}

	private static Uri createZipFile() {
		String logFilePath = SSLog.getLogFilePath();

		File tmpDir = new File(ZIP_TMP_DIR);
		if (!tmpDir.exists()) {
			tmpDir.mkdirs();
		}

		writeDeviceInfoToFile();

		File zipFile = zip(ZIP_FILE_NAME, logFilePath, DEVICE_INFO_PATH);
		if (zipFile == null) {

			return null;
		}

		if(isZipSendable(zipFile)) {
			SSLog.d("FEEDBACK \nFILE SIZE: \n" + zipFile.length());
			return Uri.fromFile(zipFile);
		}
		else {
			//FIXME warn user of error
			setErrorCode(110);
			SSLog.d("ZIPPING--- to big");
			return null;
		}
	}

	private static ProgressDialog createAndShowProgressDialog(Context context) {
		ProgressDialog zipProgress = new ProgressDialog(context);
		zipProgress.setMessage("Creating zip file...");
		zipProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		zipProgress.show();
		return zipProgress;
	}

	private static boolean isZipSendable(File zipFile) { return zipFile.length() < MAX_ZIP_SIZE; }

	private static String getDeviceInfo() {
		String deviceInfo = "MODEL: " + Build.MODEL;
		deviceInfo += "\nOS VERSION RELEASE: " + Build.VERSION.RELEASE + "\nSDK_INT: " + Build.VERSION.SDK_INT;
		deviceInfo += "\nAPP V_NAME: " + BuildConfig.VERSION_NAME + "\nAPP V_CODE: " + BuildConfig.VERSION_CODE;
		return deviceInfo;
	}

	private static void writeDeviceInfoToFile() {
		String deviceInfo = getDeviceInfo();

		File path = new File(DEVICE_INFO_DIR);

		if(!path.exists()) path.mkdir();
		final File file = new File(path, DEVICE_INFO_FILE_NAME);

		try{
			boolean created = file.createNewFile();
			if(!created) {
				SSLog.d("FEEDBACK \nFILE HAS CREATED: \n" + file.createNewFile());
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(fos);
				osw.append(deviceInfo);
				osw.flush();
				osw.close();
			}
		}catch (FileNotFoundException fnfe){
			setErrorCode(111);
			//FIXME Show user that error happened
			SSLog.d("FEEDBACK \nFNFE: " + fnfe.toString() + "\nGIVEN DIR: " + file.getAbsolutePath()
					+ "\nGIVEN PATH DIR: " + file.getAbsolutePath() + "\n" + DEVICE_INFO_DIR);
		}catch (IOException ioe){
			setErrorCode(112);
			//FIXME Show user that error happened
			SSLog.d("FEEDBACK \nIOE: " + ioe.toString() + "\nGIVEN FILE DIR: " + file.getAbsolutePath()
					+ "\nGIVEN PATH DIR: " + file.getAbsolutePath() + "\n" + DEVICE_INFO_DIR);
		}
	}

	private static File zip(String zipFileName, String... files) {
		try {
			BufferedInputStream origin;
			FileOutputStream dest = new FileOutputStream(zipFileName);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			byte data[] = new byte[BUFFER];

			for (String file : files) {
//                Log.v("Compress", "Adding: " + files[i]);
				FileInputStream fi = new FileInputStream(file);
				origin = new BufferedInputStream(fi, BUFFER);

				ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
				out.putNextEntry(entry);
				int count;

				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			SSLog.d("FEEDBACK \nException: " + e.toString() );
			e.printStackTrace();
			return null;
		}
		return new File(zipFileName);
	}

	private static void setErrorCode(int code){
		ERROR_CODE = code;
	}

	public static void cleanUp() {
		new File(ZIP_FILE_NAME).delete();
		new File(DEVICE_INFO_PATH).delete();
	}

}

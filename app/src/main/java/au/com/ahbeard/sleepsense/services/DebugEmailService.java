package au.com.ahbeard.sleepsense.services;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

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
	private static String DEVICE_INFO_DIR = Environment.getExternalStorageDirectory() + "/au.com.ahbeard.sleepsense.staging/";
	private static int MAX_ZIP_SIZE = 25_165_824; //24MB in B, leaving some extra space

	static Uri zipUri;

	private static void showProgressDialog(final Context context){
		final ProgressDialog zipProgress = new ProgressDialog(context);
		zipProgress.setMessage("Zipping Log Files");
		zipProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		zipProgress.setIndeterminate(false);
		zipProgress.setProgress(0);
		zipProgress.show();

		final byte totalTime = 100;
		final Thread zipProgressThread = new Thread(){
			@Override
			public void run() {
				byte progressCompletion = 0;
				while (progressCompletion < totalTime){
					try {
						sleep(100);
						progressCompletion += 10;
						zipProgress.setProgress(progressCompletion);
					}catch (Exception e) { e.printStackTrace(); }
				}
				zipUri = getZipFileFromUri(context);
				zipProgress.dismiss();
			}
		};

		zipProgressThread.start();
	}

	public static Intent getDebugEmailIntent(final Context context) {
		showProgressDialog(context);
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("vnd.android.cursor.dir/email");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sabbib@mentallyfriendly.com"});
		emailIntent.putExtra(Intent.EXTRA_STREAM, zipUri);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sleepsense Android app.zip");
		return emailIntent;
	}


	private static Uri getZipFileFromUri(final Context context){
		String logFilePath = SSLog.getLogFilePath();

		File tmpDir = new File(ZIP_TMP_DIR);
		if (!tmpDir.exists()) {
			tmpDir.mkdirs();
		}
		writeToFile(getDeviceInfo());

		File zipFile = zip(ZIP_FILE_NAME, logFilePath, DEVICE_INFO_DIR);
		if (zipFile == null) {
			return null;
		}

		if(isZipSendable(zipFile)) SSLog.d("FEEDBACK \nFILE SIZE: \n" + zipFile.length());
		else new AlertDialog.Builder(context).setTitle("File Size Error").setMessage("Log files are too big. Unable to send").show();

		return  Uri.fromFile(zipFile);
	}

	private static boolean isZipSendable(File zipFile) { return zipFile.length() < MAX_ZIP_SIZE; }


	private static void writeToFile(String deviceInfo){
		File path = new File(DEVICE_INFO_DIR);

		if(!path.exists()) path.mkdir();
		final File file = new File(path, "DeviceInfo.txt");

		try{
			SSLog.d("FEEDBACK \nFILE HAS CREATED: \n" + file.createNewFile());
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.append(deviceInfo);
			osw.flush();
			osw.close();
		}catch (FileNotFoundException fnfe){
			SSLog.d("FEEDBACK \nFNFE: " + fnfe.toString() + "\nGIVEN DIR: " + file.getAbsolutePath()
					+ "\nGIVEN PATH DIR: " + file.getAbsolutePath() + "\n" + DEVICE_INFO_DIR);
		}catch (IOException ioe){
			SSLog.d("FEEDBACK \nIOE: " + ioe.toString() + "\nGIVEN FILE DIR: " + file.getAbsolutePath()
					+ "\nGIVEN PATH DIR: " + file.getAbsolutePath() + "\n" + DEVICE_INFO_DIR);

		}
	}

	private static String getDeviceInfo() {
		String deviceInfo = "MODEL: " + Build.MODEL;
		deviceInfo += "\nOS VERSION RELEASE: " + Build.VERSION.RELEASE + "\nSDK_INT: " + Build.VERSION.SDK_INT;
		deviceInfo += "\nAPP V_NAME: " + BuildConfig.VERSION_NAME + "\nAPP V_CODE: " + BuildConfig.VERSION_CODE;
		return deviceInfo;
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
			SSLog.d("FEEDBACK \nException: " + e.toString() );
			e.printStackTrace();
			return null;
		}
		return new File(zipFileName);
	}
}

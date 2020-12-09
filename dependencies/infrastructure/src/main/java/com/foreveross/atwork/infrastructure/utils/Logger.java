package com.foreveross.atwork.infrastructure.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author Reyzhang22@gmail.com
 * 日志记录类，将日志记录进文本文件
 *
 */
@SuppressLint("SimpleDateFormat")
public class Logger {


	// 注意发布正式版时需要更改此配置项以不写日志
	public static boolean IS_PRODUCTION = false;
	
	private static String FILE_ROOT_PATH = "";
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private static File logFile;

	private static PrintWriter pw = null;

	private static int LOG_SAVE_DAYS = 7;

	private static long lastCheckDeletedTime = -1;

	public static void d(String tag, String msg) {
		if (IS_PRODUCTION) {
			return;
		}
		
		tag = tag != null ? tag : "log 的时候 tag 为空";
		msg = msg != null ? msg : "log 的时候 msg 为空";
		android.util.Log.d(tag, msg);
//		log2File(tag, msg,null);
	}

	public static void i(String tag, String msg) {
		if (IS_PRODUCTION) {
			return;
		}
		
		tag = tag != null ? tag : "log 的时候 tag 为空";
		msg = msg != null ? msg : "log 的时候 msg 为空";
		android.util.Log.i(tag, msg);
//		log2File(tag, msg,null);
	}

	public static void v(String tag, String msg) {
		if (IS_PRODUCTION) {
			return;
		}
		
		tag = tag != null ? tag : "log 的时候 tag 为空";
		msg = msg != null ? msg : "log 的时候 msg 为空";
		android.util.Log.v(tag, msg);
//		log2File(tag, msg,null);
	}
	
	public static void e(String tag, String msg) {
		if (IS_PRODUCTION) {
			return;
		}
		
		tag = tag != null ? tag : "log 的时候 tag 为空";
		msg = msg != null ? msg : "log 的时候 msg 为空";
		android.util.Log.e(tag, msg);
		log2File(tag, msg,null);
	}

	public static void e(String tag, String msg, Exception e) {
		if (IS_PRODUCTION) {
			return;
		}
		
		tag = tag != null ? tag : "log 的时候 tag 为空";
		msg = msg != null ? msg : "log 的时候 msg 为空";
		e.printStackTrace();
		if (e != null) {
			msg = msg + Log.getStackTraceString(e);
		}
		log2File(tag, msg,null);
	}

	public static void w(String tag, String msg) {
		if (IS_PRODUCTION) {
			return;
		}
		
		tag = tag != null ? tag : "log 的时候 tag 为空";
		msg = msg != null ? msg : "log 的时候 msg 为空";
		android.util.Log.w(tag, msg);
		log2File(tag, msg,null);
	}

	public static ExecutorService getExecutorService() {
		return executorService;
	}

	public static void log2File(final String tag, final String msg, final Exception e) {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					if (pw == null) {
						if (logFile == null) {
							File dir = new File(FILE_ROOT_PATH);
							if (!dir.exists()) {
								dir.mkdirs();
							}

							String currentDateFileName = TimeUtil.getStringForMillis(TimeUtil.getCurrentTimeInMillis(), "yyyy-MM-dd");

							logFile = new File(String.format("%s/%s", FILE_ROOT_PATH, "log " + currentDateFileName + ".txt"));
						}
						pw = new PrintWriter(new FileWriter(logFile, true));
					}
					String _text = String.format("%s : ~ %s $ %s \n", sdf.format(new Date()), tag, msg);
					pw.println(_text);
					if (e != null) e.printStackTrace(pw);
					pw.flush();

				} catch (Exception e) {
					e.printStackTrace();
					if (pw != null) {
						pw.close();
					}
					pw = null;
					logFile = null;
				}
			}
		});
	}


	public static void cleanLogs() {

		long currentTime = System.currentTimeMillis();
		long checkGap = 24 * 60 * 60 * 1000L;
		if(checkGap >= currentTime - lastCheckDeletedTime) {
			return;
		}

		lastCheckDeletedTime = currentTime;

		executorService.submit(new Runnable() {
			@Override
			public void run() {
				File dir = new File(FILE_ROOT_PATH);
				if(dir.exists() && dir.isDirectory()) {
					File logFiles[] = dir.listFiles();
					for(File logFile : logFiles) {

						if(logFile.exists() && logFile.lastModified() < TimeUtil.getTimeInMillisDaysBefore(LOG_SAVE_DAYS)) {
							logFile.delete();


							LogUtil.e("cleanLogs delete -> " + logFile.getPath());

						}
					}

				}

				FileUtil.deleteFile(getTempLogPath(), true);
				FileUtil.deleteFile(getTempZipPath(), true);

			}
		});


	}

	@NonNull
	public static List<String> getLogFiles(long beginTime, long endTime) {
		List<String> legalPathList = new ArrayList<>();
		File logRootFile = new File(FILE_ROOT_PATH);

		if(logRootFile.exists() && logRootFile.isDirectory()) {
			File logFiles[] = logRootFile.listFiles();
			for(File logFile : logFiles) {

				if(getTempLogPath().contains(logFile.getPath())) {
					continue;
				}

				if(getTempZipPath().contains(logFile.getPath())) {
					continue;
				}

				if(-1 == beginTime && -1 == endTime) {
					legalPathList.add(logFile.getPath());

				} else if(beginTime < logFile.lastModified() && endTime > logFile.lastModified()) {
					legalPathList.add(logFile.getPath());
				}


			}

		}

		return legalPathList;
	}

	public static void zipLogs(final long beginTime, final long endTime, final OnZipResultListener onZipResultListener) {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					FileUtil.deleteFile(getTempLogPath(), true);
					FileUtil.deleteFile(getTempZipPath(), true);

					String logZipTempSpacePath = getTempLogPath();
					File logZipTempSpace = new File(logZipTempSpacePath);
					if (!logZipTempSpace.exists()) {
						logZipTempSpace.mkdirs();
					}

					List<String> logNeedCopyToTempSpace = getLogFiles(beginTime, endTime);

					if(ListUtil.isEmpty(logNeedCopyToTempSpace)) {
						LogUtil.e("压缩不执行 符合条件的日志为空");

						onZipResultListener.success(null);
						return;
					}

					for (String logPath : logNeedCopyToTempSpace) {
						FileUtil.copyFile(logPath, logZipTempSpacePath + FileUtil.getName(logPath));
					}


					String logZipTargetPath = getTempZipPath();

					ZipUtil.zipFolder(logZipTempSpacePath, logZipTargetPath);
					onZipResultListener.success(logZipTargetPath);

					LogUtil.e("压缩成功 地址为: " + logZipTargetPath);

				} catch (Exception e) {
					e.printStackTrace();
					onZipResultListener.fail();
					LogUtil.e("压缩失败");

				}
			}
		});

	}

	@NotNull
	private static String getTempZipPath() {
		return FILE_ROOT_PATH + File.separator + "tempZip" + File.separator + "log.zip";
	}

	@NotNull
	private static String getTempLogPath() {
		return FILE_ROOT_PATH + File.separator + "temp" + File.separator;
	}

	public static void setLogFilePath(String filePath) {
		FILE_ROOT_PATH = filePath;
	}

	public interface OnZipResultListener {
		void success(@Nullable String path);
		void fail();
	}
}

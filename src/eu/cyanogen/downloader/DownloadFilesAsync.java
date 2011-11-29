package eu.cyanogen.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class DownloadFilesAsync extends AsyncTask<String, Integer, Integer> {

	private int fileSize;
	private downloader context;
	private String downloadPath;
	private File downloadedFile;
	private boolean resumeDownload;
	//Notification part
		private Notification notification;
		private NotificationManager notificationManager;
		private PendingIntent pendingIntent;
		
	public DownloadFilesAsync(downloader c,String downPath) {
		context = c;
		downloadPath = downPath;
		resumeDownload = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("resumeDownload", false);
	}
	
	@Override
	protected Integer doInBackground(String... arg0) {
		int size=0;
		for(String s : arg0)
		{
			URL url;
			try {
				url = new URL(s);
				URLConnection uc = url.openConnection();				
				
				downloadedFile = new File(Environment.getExternalStorageDirectory()+downloadPath,url.getFile().substring(url.getFile().lastIndexOf("/")+1));
				if(downloadedFile.exists()&&resumeDownload)
					uc.setRequestProperty("Range", "bytes="+downloadedFile.length()+"-");
				
				uc.connect();
				fileSize = uc.getContentLength();
				
				if(fileSize<0)
				{
					context.runOnUiThread(new Runnable() {
						@Override public void run() {
							context.displayMessage("Unable to get correct file information, please retry later");
						}
					});
					break;
				}
				
				Log.v("DownloadedFile","Current filesize ("+downloadedFile.getAbsolutePath()+") : "+downloadedFile.length()+" Final filesize :  "+fileSize);
				if(fileSize==downloadedFile.length()&&fileSize>0)
				{
					context.runOnUiThread(new Runnable() {
						@Override public void run() {
							context.displayMessage("This file has already been download");
						}
					});
					break;
				}

				FileOutputStream os;
				if(resumeDownload)
				{
					os = new FileOutputStream(downloadedFile,true);
					Log.v("DownloadedFile","Resuming download");
				}
				else
				{
					os = new FileOutputStream(downloadedFile);
					Log.v("DownloadedFile","Not Resuming download");
				}	
				InputStream is = uc.getInputStream();
				
				createOnGoingNotification();
				
				byte[] buffer = new byte[1024];
		        int len = 0;
		        long lastLength=downloadedFile.length(),curLength=downloadedFile.length();
		        while ((len = is.read(buffer)) != -1) {
		            os.write(buffer, 0, len);
		            curLength+=len;
		            if(curLength>(lastLength+1048576/*1024*1024*/))
		            {//to not spam to notification process, only update it on mega update
		            	publishProgress((int)(((float)curLength/fileSize)*100));
		            	lastLength = curLength;
		            }
		        }
		        os.flush();
		        os.close();
		        is.close();
			} catch (Exception e) {
				Log.v("DownloadFilesAsync",e.getMessage());
			}
		}
		return size;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		if(notificationManager!=null&&notification!=null)
		{
			notification.contentView.setProgressBar(R.id.status_progress, 100, values[0], false);
			notificationManager.notify(42, notification);
		}
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		if(notificationManager!=null)
		{
	        notificationManager.cancel(42);//remove notification once the download finished
	        Notification notDL = new Notification(R.drawable.icon, "Finished ROM Download", System.currentTimeMillis());
	        notDL.setLatestEventInfo(context, "CyanogenMod Downloader", "File "+downloadedFile.getAbsolutePath()+" has been downloaded", pendingIntent);
	        notificationManager.notify(2, notDL);
		}
	}

	private void createOnGoingNotification()
	{
		Intent intent = new Intent(context, downloader.class);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    // configure the ongoing event
        notification = new Notification(R.drawable.icon, "Downloading a ROM", System.currentTimeMillis());
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notification.contentView = new RemoteViews(context.getPackageName(), R.layout.download_progress);
        notification.contentIntent = pendingIntent;
        notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.icon);
        notification.contentView.setTextViewText(R.id.status_text, "Downloading "+downloadedFile.getName());
        notification.contentView.setProgressBar(R.id.status_progress, 100, 0, false);
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(42, notification);
	}
}

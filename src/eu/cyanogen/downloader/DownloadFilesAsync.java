package eu.cyanogen.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

public class DownloadFilesAsync extends AsyncTask<String, Integer, Integer> {

	private int fileSize;
	private Context context;
	private String downloadPath;
	private File downloadedFile;
	//Notification part
		private Notification notification;
		private NotificationManager notificationManager;
		private PendingIntent pendingIntent;
		
	public DownloadFilesAsync(Context c,String downPath) {
		context = c;
		downloadPath = downPath;
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
				if(downloadedFile.exists())
					uc.setRequestProperty("Range", "bytes="+downloadedFile.length()+"-");
				
				FileOutputStream os = new FileOutputStream(downloadedFile);
				uc.connect();
				fileSize = uc.getContentLength();
				InputStream is = uc.getInputStream();
				
				createOnGoingNotification();
				
				byte[] buffer = new byte[1024];
		        int len = 0;
		        long lastLength=downloadedFile.length(),curLength=downloadedFile.length();
		        while ((len = is.read(buffer)) != -1) {
		            os.write(buffer, 0, len);
		            curLength+=len;
		            if(curLength>(lastLength+1048576/*1024*1024*/))
		            {
		            	publishProgress((int)(curLength/fileSize*100));
		            	lastLength = curLength;
		            }
		        }
		        os.close();
		        is.close();
			} catch (Exception e) {
				Log.v("DownloadFilesAsync",e.getMessage());
				e.printStackTrace();
			}
		}
		return size;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		Log.v("UpdatingProgress","Current value : "+values[0]+"/100 max : "+fileSize);

		if(notificationManager!=null&&notification!=null)
		{
			notification.contentView.setProgressBar(R.id.status_progress, (int) values[0], fileSize, false);
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

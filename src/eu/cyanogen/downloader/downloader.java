package eu.cyanogen.downloader;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class downloader extends Activity implements OnSharedPreferenceChangeListener {
    /** Called when the activity is first created. */
	
	ListView displayData;
	private static String URL="http://download.cyanogenmod.com";
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private String url;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        displayData=(ListView)findViewById(R.id.displayData);
        /*Set click*/
        displayData.setOnItemClickListener(new OnItemClickListener(){
			@Override
        	@SuppressWarnings("unchecked")
         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
        		final HashMap<String, String> map = (HashMap<String, String>) displayData.getItemAtPosition(position);
        		AlertDialog.Builder adb = new AlertDialog.Builder(downloader.this);
        		adb.setTitle("Download this version ?");
        		adb.setMessage("Your choice : "+map.get("name")+"\n"+map.get("size")+"\n"+map.get("date"));
        		adb.setPositiveButton("Ok", new OnClickListener() {@Override public void onClick(DialogInterface arg0, int arg1) {downloader(map.get("link"));}});
        		adb.setNegativeButton("Cancel", null);
        		adb.show();
        	}
         });
        
        /*Get preferences and display data*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        url=prefs.getString("downloadUrl",URL)+prefs.getString("phoneType","/");
        retrieveInformation();
        startChecker();
    }
    /**
     * By default check update every hours
     */
    private void startChecker()
    {
    	scheduler.schedule(new Runnable(){public void run(){checker();}},60*60,TimeUnit.SECONDS);
    }
    private void checker()
    {
    	/*Notification*/
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon, "A new version of cyanogen is available", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, downloader.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(getApplicationContext(), "Cyanogen Update", "Version VERSION is now out for PHONE and NIGHTLY", contentIntent);
        mNotificationManager.notify(1, notification);
    }
    private void downloader(String url)
    {
    	String urlD = PreferenceManager.getDefaultSharedPreferences(this).getString("downloadUrl",URL)+url;
    }
    private void retrieveInformation()
    {
    	ParseData pd = ParseData.getInstance(this, url,true);
        SimpleAdapter mSchedule = new SimpleAdapter (downloader.this.getBaseContext(), pd.retrieveDownloadsList(), R.layout.list, new String[] {"name", "date", "size","type"}, new int[] {R.id.name,R.id.date,R.id.size,R.id.type});
        displayData.setAdapter(mSchedule);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	menu.add(Menu.NONE,0,Menu.NONE,"Reload").setAlphabeticShortcut('r');
    	menu.add(Menu.NONE,1,Menu.NONE,"Preferences").setAlphabeticShortcut('p');
    	return(super.onCreateOptionsMenu(menu));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
	    	case 0:
	    		retrieveInformation();
	    		return true;
	    	case 1:
	    		startActivity(new Intent(getBaseContext(),EditPreferences.class));
	    		return true;
    	}
		return super.onOptionsItemSelected(item);
    }
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		if(arg1.equals("downloadUrl"))
		{
			url=arg0.getString("downloadUrl",URL);
		}
		if(arg1.equals("phoneType"))
		{
			url=arg0.getString("downloadUrl",URL)+arg0.getString("phoneType","/");
		}
	}
}
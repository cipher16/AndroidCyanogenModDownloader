package eu.cyanogen.downloader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class downloader extends Activity implements OnSharedPreferenceChangeListener {
    /** Called when the activity is first created. */
	
	ListView displayData;
	private static String URL="http://download.cyanogenmod.com";
	private String url;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        displayData=(ListView)findViewById(R.id.displayData);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        url=prefs.getString("downloadUrl",URL);
        retrieveInformation();
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
	}
}
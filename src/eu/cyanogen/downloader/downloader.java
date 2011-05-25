package eu.cyanogen.downloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class downloader extends Activity {
    /** Called when the activity is first created. */
	ListView displayData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        displayData=(ListView)findViewById(R.id.displayData);

        ParseData pd = new ParseData(this, "");
        SimpleAdapter mSchedule = new SimpleAdapter (downloader.this.getBaseContext(), pd.retrieveDownloadsList(), R.layout.list, new String[] {"name", "date", "size","type"}, new int[] {R.id.name,R.id.date,R.id.size,R.id.type});
        displayData.setAdapter(mSchedule);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	menu.add(Menu.NONE,0,Menu.NONE,"Preferences").setIcon(R.drawable.icon).setAlphabeticShortcut('p');
    	return(super.onCreateOptionsMenu(menu));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
	    	case 0:
	    		startActivity(new Intent(getBaseContext(),EditPreferences.class));
	    		return(true);
    	}
		return(super.onOptionsItemSelected(item));
    }
}
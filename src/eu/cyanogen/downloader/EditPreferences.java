package eu.cyanogen.downloader;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class EditPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

// This doesn't work need to parse data by hand ....
//		ArrayList<CharSequence> entries = new ArrayList<CharSequence>();
//		ArrayList<CharSequence> values = new ArrayList<CharSequence>();
//		ArrayList<HashMap<String,String>> phonesType = ParseData.getInstance().retrievePhonesList();
//		for (HashMap<String, String> hashMap : phonesType) {
//			entries.add(hashMap.get("name"));
//			values.add(hashMap.get("link"));
//		}
//		try{
//			ListPreference customPref = (ListPreference)findPreference("phoneType");
//			if(customPref!=null)
//			{
//				customPref.setEntries(entries.toArray(new CharSequence[entries.size()]));
//				customPref.setEntryValues(values.toArray(new CharSequence[values.size()]));
//			}
//		}catch(Exception e){
//			Log.v("Exception EditPreferences",e.toString());
//		}
		addPreferencesFromResource(R.xml.preferences);
	}

}

package eu.cyanogen.downloader;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class EditPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CharSequence[] entries = new CharSequence[40];
		CharSequence[] values = new CharSequence[40];
		int i=0;
		ArrayList<HashMap<String,String>> phonesType = ParseData.getInstance().retrievePhonesList();
		for (HashMap<String, String> hashMap : phonesType) {
			entries[i]=hashMap.get("name");
			values[i]=hashMap.get("link");
			i++;
		}
		try{
			ListPreference customPref = (ListPreference) findPreference("phoneType");
			customPref.setEntries(entries);
			customPref.setEntryValues(values);
		}catch(Exception e){
			ParseData.getInstance().displayToast("EditPrefError : "+e.toString());
		}
		addPreferencesFromResource(R.xml.preferences);
	}
}

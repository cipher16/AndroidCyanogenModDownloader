package eu.cyanogen.downloader;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class PhonePreference extends ListPreference {

	public PhonePreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		ArrayList<HashMap<String, String>> ar = ParseData.getInstance().retrievePhonesList();
		String[] phonesName=new String[ar.size()+1];
		String[] phonesUrl=new String[ar.size()+1];
		
		int i=0;
		phonesName[i]="All";
		phonesUrl[i++]="/";
		for (HashMap<String,String> phone : ar) {
			phonesName[i]=phone.get("name");
			phonesUrl[i++]=phone.get("link");
		}
		setEntries(phonesName);
		setEntryValues(phonesUrl);
		
	}

}

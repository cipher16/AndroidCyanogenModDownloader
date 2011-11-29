package eu.cyanogen.downloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import android.app.Activity;
import android.widget.Toast;

public class ParseData {
	private TagNode tn;
	private Activity activity;
	private static ParseData instance;
	private ParseData(Activity a,String url) {
		HtmlCleaner hc = new HtmlCleaner();
		activity=a;
		try {
			tn = hc.clean(new java.net.URL(url));//when we have to parse specific url
		} catch (Exception e) {}
	}
	/**
	 * Provided url must be complete
	 * @param a
	 * @param url
	 * @param renew
	 * @return
	 */
	public static ParseData getInstance(Activity a,String url,boolean renew)
	{
		if(instance==null|renew)
			instance=new ParseData(a, url);
		return instance;
	}
	
	public static ParseData getInstance()
	{
		return instance;
	}
	
	public List<HashMap<String,String>> retrieveDownloadsList()
	{
		ArrayList<HashMap<String,String>> types= new ArrayList<HashMap<String,String>>();
		HashMap<String, String> tmp;
		try {
			for (Object o : tn.evaluateXPath("//table//tbody//tr[position()>1]")) {//get after headers
				tmp=new HashMap<String, String>();
				tmp.put("name", ((TagNode) o).getElementsByName("td", false)[0].getElementsByName("a", false)[0].getText().toString());
				tmp.put("type", "Type : "+((TagNode) o).getElementsByName("td", false)[1].getText().toString());
				tmp.put("link", ((TagNode) o).getElementsByName("td", false)[2].getElementsByName("a", false)[0].getAttributeByName("href"));
				tmp.put("fileN", tmp.get("link").substring(tmp.get("link").lastIndexOf("/")+1));
				tmp.put("file", "File : "+tmp.get("fileN"));
				tmp.put("md5", ((TagNode) o).getElementsByName("td", false)[2].getElementsByName("small", false)[0].getText().toString());
				tmp.put("size", "Size : "+((TagNode) o).getElementsByName("td", false)[3].getElementsByName("small", false)[0].getText().toString());
				tmp.put("date", "Date : "+((TagNode) o).getElementsByName("td", false)[4].getElementsByName("small", false)[0].getText().toString());
				types.add(tmp);
			}
		} catch (Exception e) {displayToast("DownloadList : "+e.getMessage()+e.toString());}
		return types;
	}
	
	public List<HashMap<String,String>> retrieveTypesList()
	{
		ArrayList<HashMap<String,String>> types= new ArrayList<HashMap<String,String>>();
		HashMap<String, String> tmp;
		try {
			for (Object o : tn.evaluateXPath("//nav[@id='navigation']//ul//li[@class='bullet'][position()<4]")) {
				tmp=new HashMap<String, String>();
				tmp.put("name", ((TagNode) o).getElementsByName("a", false)[0].getText().toString());
				tmp.put("link", ((TagNode) o).getElementsByName("a", false)[0].getAttributeByName("href"));
				types.add(tmp);
			}
		} catch (Exception e) {displayToast("TypeList "+e.getMessage());}
		return types;
	}
	public ArrayList<HashMap<String, String>> retrievePhonesList()
	{
		ArrayList<HashMap<String,String>> phones= new ArrayList<HashMap<String,String>>();
		HashMap<String, String> tmp;
		try {

			for (Object o : tn.evaluateXPath("//nav[@id='navigation']//ul//li[@class='bullet'][position()>3]")) {
				tmp=new HashMap<String, String>();
				tmp.put("name", ((TagNode) o).getElementsByName("a", false)[0].getText().toString());
				tmp.put("link", ((TagNode) o).getElementsByName("a", false)[0].getAttributeByName("href"));
				phones.add(tmp);
			}
		} catch (Exception e) {displayToast("PhoneList "+e.getMessage());}
		return phones;
	}
	public void displayToast(String message)
	{
		Toast toast=Toast.makeText(activity, message, Toast.LENGTH_LONG);  
		toast.show();
	}
}

package task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class SendTask extends AsyncTask<String, Void, Void>{

	//FIELDS
	private final String USER_AGENT = "Mozilla/5.0";
	private static final String URL = "http://wpinholland.azurewebsites.net/API/Messages";
	private String title = new String();
	private String text = new String();

	//CONSTRUCTOR
	public SendTask()
	{

	}

	//METHODS
	@Override
	protected Void doInBackground(String ... message) {

		this.title = message[0];
		this.text = message[1];

		try 
		{
			//Retrieve parameters from AddMessageActivity
			sendPost(title, text);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	protected Void onPostExecute()
	{
		return null;
	}

	private void sendPost(String title, String text) throws Exception {

		//HttpPost request has to be done
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL);

		//Add header
		post.setHeader("User-Agent", USER_AGENT);

		//Add all paramaters to a list with NameValuePairs
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("title", title));
		urlParameters.add(new BasicNameValuePair("text", text));
		//			urlParameters.add(new BasicNameValuePair("image", ));

		//HttpPost request has to be sent as an UrlEncodedFormEntity
		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		//Returns the response of the HttpClient object using the HttpPost request
		HttpResponse response = client.execute(post);

		//Retrieves the content of the entity of the HttpResponse object and puts it in a BufferedReader object
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) 
		{
			result.append(line);
		}
	}
}

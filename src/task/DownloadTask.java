package task;

import interfaces.OnTaskCompleted;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pojo.Messages;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class DownloadTask extends AsyncTask<String, Void, Messages> 
{
	//FIELDS
	private OnTaskCompleted listener;

	//CONSTRUCTOR
	public DownloadTask()
	{

	}

	public DownloadTask(OnTaskCompleted listener) {
		this.listener = listener;
	}

	//METHODS
	@Override
	protected Messages doInBackground(String... urls)
	{
		Messages messages = new Messages(); 

		//Parameters comes from the execute() call. 
		//urls[0] is the url to download the messages from.
		try 
		{	
			messages = downloadUrl(urls[0]);
		} 
		catch (IOException e) 
		{
			return null;
		}
		return messages;
	}

	@Override
	protected void onPostExecute(Messages result) {
		//Pass result back to MainActivity via callback function.		
		listener.onTaskCompleted(result);
	}

	private Messages downloadUrl(String myurl) throws IOException, JsonSyntaxException 
	{
		InputStream is = null;
		BufferedReader reader = null;

		try 
		{
			URL url = new URL(myurl);

			//Obtain a new HttpURLConnection by calling URL.openConnection() and casting the result to HttpURLConnection.
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
			conn.connect();
			int rc = conn.getResponseCode();
			StringBuilder sb = null;

			//When HttpURLConnection is good, the messages can be retrieved
			if (rc == HttpURLConnection.HTTP_OK) 
			{ 
				String line = null; 
				// Initialize the reader
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				sb = new StringBuilder(); 		        

				while ((line = reader.readLine()) != null) 
				{
					sb.append(line + '\n');
				}      	
			}

			Messages messages = null;
			try 
			{
				Gson gson = null;
				gson = new Gson();
				// Convert the reader to Messages class
				messages = gson.fromJson(sb.toString(), Messages.class);
			}
			finally
			{
			}
			return messages;
		}

		// Makes sure that the InputStream is closed after the application is finished using it. } 
		finally 
		{
			if (is != null) 
			{
				is.close(); 
			}
			if (reader != null) {
				reader.close();
			}
		}
	}
}

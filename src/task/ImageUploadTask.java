package task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import pojo.Image;
import util.MultipartEntity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

/*
 * Follows the tutorial from http://www.codepool.biz/tech-frontier/android/take-a-photo-from-android-camera-and-upload-it-to-a-remote-php-server.html
 * Code is available on https://github.com/DynamsoftRD/JavaHTTPUpload  
 */

public class ImageUploadTask extends AsyncTask<Bitmap, Void, Image>
{
	// FIELDS
	private static final String URL = "http://wpinholland.azurewebsites.net/API/Images";
	private String response = new String();
	private Image image = null;

	// METHODS
	protected Image doInBackground(Bitmap... bitmaps)
	{
		if (bitmaps[0] == null)
			return null;

		Bitmap bitmap = bitmaps[0];
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		// Compress Bitmap object to ByteArrayOutputStream object
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert
																	// Bitmap to
																	// ByteArrayOutputStream
		InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert
																			// ByteArrayOutputStream
																			// to
																			// ByteArrayInputStream

		DefaultHttpClient httpclient = new DefaultHttpClient();
		try
		{
			HttpPost httppost = new HttpPost(URL); // server

			// Set up the MultipartEntity object, so image can be sent to server
			// with converted InputStream object of image
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("image", System.currentTimeMillis() + ".jpg", in);

			// Hands entity to the HTTP POST request
			httppost.setEntity(reqEntity);

			try
			{
				// Execute the DefaultHttpClient and retrieve the response from
				// server via ResponseHandler object
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				response = httpclient.execute(httppost, responseHandler);
			}
			catch (ClientProtocolException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				if (response != null)
				{
					// Deserialize Json from server to Image object
					image = new Gson().fromJson(response, Image.class);

					Log.i("FinalExercise",
							"ID: " + image.ID + ". ImageUrl: " + image.ImageUrl);
				}
			}
			finally
			{
			}
		}
		finally
		{
		}

		if (in != null)
		{
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		if (stream != null)
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return image;
	}

	@Override
	protected void onPostExecute(Image result)
	{
		super.onPostExecute(result);
	}
}

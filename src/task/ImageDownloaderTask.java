package task;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

	//FIELDS
	private final WeakReference<ImageView> imageViewReference;

	//CONSTRUCTOR
	public ImageDownloaderTask(ImageView imageView) {
		imageViewReference = new WeakReference<ImageView>(imageView);
	}

	//METHODS
	@Override
	// Actual download method, run in the task thread
	protected Bitmap doInBackground(String... params) {
		// params comes from the execute() call: params[0] is the url.
		return downloadBitmap(params[0]);
	}

	@Override
	// Once the image is downloaded, associate it to the imageView
	protected void onPostExecute(Bitmap bitmap) 
	{
		if (isCancelled()) 
		{
			bitmap = null;
		}
		if (imageViewReference != null) 
		{
			ImageView imageView = (ImageView) imageViewReference.get();
			if (imageView != null) 
			{
				if (bitmap != null) 
				{
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	static Bitmap downloadBitmap(String url) 
	{
		//Provide useragent
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");

		//Make HttpGet-request object 
		final HttpGet getRequest = new HttpGet(url);
		try 
		{
			//Executes the request and put it in the HttpResponse object
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) 
			{
//				Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}

			//Obtains the entity from HttpResponse and put it in HttpEntity object
			final HttpEntity entity = response.getEntity();
			if (entity != null) 
			{
				InputStream inputStream = null;
				try {
					//Receive the returned Inputstream object from getContent() of the HttpEntity object
					inputStream = entity.getContent();
					//Decode the inputStream as a Bitmap object
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					return bitmap;
				} 
				finally 
				{
					if (inputStream != null) 
					{
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} 
		catch (Exception e) 
		{
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
//			Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
		} 
		finally 
		{
			if (client != null) 
			{
				client.close();
			}
		}
		return null;
	}
}

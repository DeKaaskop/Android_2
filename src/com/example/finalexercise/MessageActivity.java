package com.example.finalexercise;

import task.ImageDownloaderTask;
import util.CheckNetworkConnection;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends Activity {

	//FIELDS
	private String title = new String();
	private String text = new String();

	//METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		TextView textViewTitle = (TextView)findViewById(R.id.message_textview_title);
		TextView textViewTimeStamp = (TextView)findViewById(R.id.message_textview_timestamp);
		TextView textViewText = (TextView)findViewById(R.id.message_textview_text);
		ImageView imageViewImage = (ImageView)findViewById(R.id.message_imageview);

		//Get intent that started this activity
		Intent intent = getIntent();

		title = intent.getStringExtra("title");
		text= intent.getStringExtra("text");

		//Now the extended data that has been added to the intent can be retrieved
		textViewTitle.setText(title);
		textViewTimeStamp.setText(intent.getStringExtra("timestamp"));
		textViewText.setText(text);
		String imageURL = intent.getStringExtra("image");

		//When the imageURL has been filled before, the asynchronous ImageDownloaderTask will retrieve the belonging image from it 
		if(imageURL != null)
		{
			if(CheckNetworkConnection.isInternetAvailable(this))
			{
				ImageDownloaderTask mImageDownloaderTask = new ImageDownloaderTask(imageViewImage);
				mImageDownloaderTask.execute(imageURL);
			}
			else
			{ 	
				//Display error
				Toast.makeText(getApplicationContext(),this.getString(R.string.no_connection),Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message, menu);
		return true;
	}

	//--
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_share:
		{
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, title + " " + text + ".");
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
		}
		default: 
		{
			return super.onOptionsItemSelected(item);
		}
		}

	}
}

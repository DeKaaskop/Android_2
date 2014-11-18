package com.example.finalexercise;

import java.io.File;

import task.SendTask;
import util.CheckNetworkConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddMessageActivity extends Activity {

	//TODO Strings
	//TODO Bonusfeatures

	//FIELDS
	private SendTask task;
	private String title = new String();
	private String text = new String();
	private Uri mImageCaptureUri;
	private ImageView mImageView;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;

	//METHODS
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_message);
		task = new SendTask();
		final String [] items           = new String [] {this.getString(R.string.add_message_from_camera), this.getString(R.string.add_message_from_sd_card)};
		ArrayAdapter<String> adapter  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);

		AlertDialog.Builder builder     = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.add_message_title2));
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int item ) {
				if (item == 0) {
					Intent intent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File file        = new File(Environment.getExternalStorageDirectory(),
							"tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
					mImageCaptureUri = Uri.fromFile(file);

					try {
						intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
						intent.putExtra("return-data", true);

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (Exception e) {
						e.printStackTrace();
					}

					dialog.cancel();
				} else {
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(Intent.createChooser(intent, AddMessageActivity.this.getString(R.string.add_message_complete_action_using)), PICK_FROM_FILE);
				}
			}
		} );

		final AlertDialog dialog = builder.create();

		mImageView = (ImageView) findViewById(R.id.add_message_imageview);

		((Button) findViewById(R.id.pick_image_button)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;

		Bitmap bitmap   = null;
		String path     = "";

		if (requestCode == PICK_FROM_FILE) {
			mImageCaptureUri = data.getData();
			path = getRealPathFromURI(mImageCaptureUri); //from Gallery

			if (path == null)
				path = mImageCaptureUri.getPath(); //from File Manager

			if (path != null)
				bitmap  = BitmapFactory.decodeFile(path);
		} else {
			path    = mImageCaptureUri.getPath();
			bitmap  = BitmapFactory.decodeFile(path);
		}

		mImageView.setImageBitmap(bitmap);
	}

	public String getRealPathFromURI(Uri contentUri) {
		String [] proj      = {MediaStore.Images.Media.DATA};
		Cursor cursor       = getContentResolver().query( contentUri, proj, null, null,null);

		if (cursor == null) return null;

		int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	public void onButtonPostListener(View v)
	{
		//Retrieve texts from userinput
		EditText inputTitle = (EditText)findViewById(R.id.add_message_edittext_title);
		EditText inputText = (EditText)findViewById(R.id.add_message_edittext_text);
		title = inputTitle.getText().toString();
		text = inputText.getText().toString();

		//Checks whether user has provided valid input
		if(checkInput())
		{	
			if(CheckNetworkConnection.isInternetAvailable(this))
			{
				//User provided input will then be sent via asynchronous SendTask 
				task.execute(title, text);

				//When input has been sent, user will be navigated back to MainActivity
				//However the flag for the intent will be set to FLAG_ACTIVITY_CLEAR_TOP
				//This way user can't go back to AddMessageActivity
				Intent intent = getIntent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			else
			{ 	
				//Display error
				Toast.makeText(getApplicationContext(),this.getString(R.string.no_connection),Toast.LENGTH_SHORT).show();
			}
		}
	}

	public Boolean checkInput()
	{	
		//Check if there's a input 
		if(title.isEmpty() || title == "" || text.isEmpty() || text == "")
		{
			if((title.isEmpty() || title == "") & (text.isEmpty() || text == ""))
			{
				Toast.makeText(getApplicationContext(), this.getString(R.string.add_message_fill_both), Toast.LENGTH_SHORT).show();
			}
			else if(title.isEmpty() || title == "")
			{
				Toast.makeText(getApplicationContext(), this.getString(R.string.add_message_fill_title), Toast.LENGTH_SHORT).show();
			}
			else if(text.isEmpty() || text == "")
			{
				Toast.makeText(getApplicationContext(), this.getString(R.string.add_message_fill_text), Toast.LENGTH_SHORT).show();
			}
			return false;
		}
		return true;
	}
}

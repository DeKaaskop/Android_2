package com.example.finalexercise;

import interfaces.OnTaskCompleted;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pojo.Message;
import pojo.Messages;
import task.DownloadTask;
import util.CheckNetworkConnection;
import adapter.CustomAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener, OnScrollListener, OnTaskCompleted {

	//FIELDS
	private final static String URL_MESSAGES = "http://wpinholland.azurewebsites.net/API/Messages/";
	private DownloadTask task; 
	private ListView mListView;
	private Boolean isFirstLoad;
	public Boolean flag; //Is used to load batch of 20 messages WITH ID only once, until the task is completed
	private CustomAdapter mCustomAdapter;
	private Messages messageList = new Messages();
	private Boolean flag2 = true; //Is used for showing the Toast message only once when there's no internetconnection

	//METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mListView = (ListView)findViewById(R.id.listView);

		//Put listeners in place for interactions with the list
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);

		//Check if there's a networkconnection
		if(CheckNetworkConnection.isInternetAvailable(this))
		{
			//Fetch data
			task = new DownloadTask(this);
			isFirstLoad = true;
			flag = true;
			getMessages();
		}
		else
		{ 	
			//Display error
			Toast.makeText(getApplicationContext(), this.getString(R.string.no_connection),Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_add:
		{
			Intent intent = new Intent(MainActivity.this, AddMessageActivity.class);
			startActivity(intent);
		}
		default: 
		{
			return super.onOptionsItemSelected(item);
		}
		}
	}

	//FROM ONSCROLLLISTENER interface
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) 
	{
		if(mCustomAdapter != null)
		{
			if(firstVisibleItem >= totalItemCount-20)
			{
				//Only load 20 messages once each time
				if(flag)
				{
					if(CheckNetworkConnection.isInternetAvailable(this))
					{
						flag = false;
						flag2 = true;
						getMessages();
					}
					else
					{ 	
						if(flag2)
						{
							//Display error
							Toast.makeText(getApplicationContext(), this.getString(R.string.no_connection),Toast.LENGTH_SHORT).show();
							flag2 = false;
						}
					}
				}
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) 
	{

	}

	//FROM ONTASKCOMPLETED interface
	@Override
	public void onTaskCompleted(Messages messages) {

		if(isFirstLoad)
		{
			isFirstLoad = false;
			this.messageList = messages;

			//Use CustomAdapter to fill ListView
			mCustomAdapter = new CustomAdapter(this, messageList.messagesList);
			mListView.setAdapter(mCustomAdapter);
		}
		else
		{
			this.messageList.messagesList.addAll(messages.messagesList);
			mCustomAdapter.notifyDataSetChanged();
			flag = true;
		}
	}

	//FROM ONITEMCLICKLISTENER interface
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
	{
		//Retrieve message which is clicked upon
		Object o = mListView.getItemAtPosition(position);
		Intent intent = new Intent(MainActivity.this, MessageActivity.class);

		//Format the date to be shown in the preferred way
		Message mess = (Message)o;
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.GERMANY);

		//Pass them along with the navigation
		intent.putExtra("title", mess.Title);
		intent.putExtra("timestamp", format.format(new Date(mess.Timestamp * 1000L)));
		intent.putExtra("text", mess.Text);
		intent.putExtra("image", mess.ImageUrl);

		MainActivity.this.startActivity(intent);
	}

	//Retrieve 20 Messages each time
	public void getMessages()
	{
		//isFirstLoad is used to load the first batch of 20 messages without an ID
		if(isFirstLoad)
		{
			task.execute(URL_MESSAGES);
		}
		else
		{
			//Pass ID of the latest message in the batch of 20 messages to the asynchronous DownLoadTask
			int nr = messageList.messagesList.size();
			Message m = messageList.messagesList.get(nr-1);
			task = new DownloadTask(this);
			task.execute(URL_MESSAGES + m.ID);
		}
	}
}

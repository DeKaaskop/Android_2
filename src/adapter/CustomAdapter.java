package adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import pojo.Message;
import task.ImageDownloaderTask;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalexercise.R;


public class CustomAdapter extends BaseAdapter {
	
	//FIELDS
	private ArrayList<Message> messages; 
	private LayoutInflater mInflater;
	
	//CONSTRUCTOR
	public CustomAdapter(Context context, ArrayList<Message> mess) 
	{ 
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		messages = mess;
	}

	//METHODS
	public int getCount() 
	{ 
		return messages.size();
	}

	public Message getItem(int position) 
	{ 
		return messages.get(position);
	}

	public long getItemId(int position) 
	{ 
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) 
	{ 
		//ViewHolder pattern for optimization, object is only created once from ViewHolder class
		//then is being reused every time convertView already exists. Hence the first if-statement
		Message node = messages.get(position);
		ViewHolder holder = null;
		if(convertView == null) 
		{
			convertView = mInflater.inflate(R.layout.listitem, null);

			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.li_image); 
			holder.title = (TextView) convertView.findViewById(R.id.li_title);  
			holder.timestamp = (TextView)convertView.findViewById(R.id.li_timestamp);
		} 
		else 
		{
			holder = (ViewHolder) convertView.getTag(); 
			holder.image.setVisibility(4);
		}

		holder.title.setText(node.Title);

		//Convert retrieved timestamp to preferred String representation
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.GERMANY);
		holder.timestamp.setText(format.format(new Date(node.Timestamp*1000L)));
		
		//When node.ImageURL had a URL then the image will be loaded via ansynchronous ImageDownloaderTask
		if(node.ImageUrl != null)
		{
			new ImageDownloaderTask(holder.image).execute(node.ImageUrl);
			holder.image.setVisibility(0);
		}

		convertView.setTag(holder);
		return convertView;	
	}
	
	//STATIC NESTED CLASS
	static class ViewHolder 
	{ 
		TextView title; 
		ImageView image;
		TextView timestamp;

		int position;
	}
}

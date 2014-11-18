package pojo;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class Messages {

	@SerializedName("Messages")
	public ArrayList<Message> messagesList;
	
	public Messages()
	{
		messagesList = new ArrayList<Message>();
	}
	
}

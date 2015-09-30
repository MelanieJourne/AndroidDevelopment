package com.example.contactmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends Activity {
	
	//useful string to describe the database
	private static final String table_name = "contact";
	private static final String column_id = "ID";
	private static final String column_name ="NAME";
	private static final String column_phone ="PHONENUMBER";
	private static final String column_mail = "EMAIL";
	
	//private fields to use the database
	private SQLiteDatabase sdb;
	private ContactDBOpenHelper cdb;
	
	//UI components
	private ListView list_view;
	private Button b_add;
	//Contact Array for the list view
	private ArrayList<Contact> array_list;
	private ArrayAdapter<Contact> array_adaptater;
	//swipe detector
	SwipeDetector swipeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        
        //Initialize the database
        cdb = new ContactDBOpenHelper(this, table_name+".db", null, 1);
        sdb = cdb.getReadableDatabase();
        
        //Initialize the view
        b_add = (Button) findViewById(R.id.b_add);
        list_view = (ListView) findViewById(R.id.listview);
        //Initialize the array
        //generate an array list with the content of BD
        array_list = new ArrayList<Contact>();
        updateArrayList();
        // create an array adapter for al_strings and set it on the listview
        array_adaptater = new ArrayAdapter<Contact>(this, android.R.layout.simple_list_item_1,
				array_list);
		list_view.setAdapter(array_adaptater);
		//Initialize the list item listener
		swipeDetector = new SwipeDetector();
		initializeOnItemListener();
        //Initialize the button listener
        b_add.setOnClickListener(new OnClickListener() {
        	// launch the second activity while the first waits for a result
        	public void onClick(View v) {
        		Intent intent = new Intent(ListActivity.this, AddContactActivity.class);
        		startActivityForResult(intent, 42);
        	}
        });
        
     
    }
    
    
    //function to update the content of the list view
    public void updateArrayList()
    {
    	// the columns that we wish to retrieve from the tables
    	String[] columns = {column_id, column_name, column_phone,column_mail};
    	// where clause of the query. 
    	String where = null;
    	// arguments to provide to the where clause
    	String where_args[] = null;
    	// group by clause of the query. 
    	String group_by = null;
    	// having clause of the query. 
    	String having = null;
    	// order by clause of the query. 
    	String order_by = null;
    	// run the query. this will give us a cursor into the database
    	// that will enable us to change the table row that we are working with
    	Cursor c = sdb.query(table_name, columns, where, where_args, group_by, 
    	having, order_by);
    	String text; 
    	c.moveToFirst();
    	//Add every element in the list view when the activity is lunched
    	//Add only the new one when the result from the second activity is obtained
    	for(int i = 0; i < c.getCount(); i++)
    	{
    		//don't add the element which are already there
    		if(i > list_view.getCount() - 1)
    		{
    			/*text = "N°"+ c.getInt(0) + " | Name: " + c.getString(1) + "\n"
    	    			+"Phone Number: "+ c.getString(2) + "\n"
    	    			+"Email: "+ c.getString(3);
    	    		array_list.add(text);*/
				array_list.add(new Contact(c.getString(1),"0"+c.getString(2),c.getString(3)));
    		}
    		c.moveToNext();
    	}
   	
    }

	public void initializeOnItemListener()
	{
		list_view.setOnTouchListener(swipeDetector);
		list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {

				//do the thing you like
				//define : a simple onclick => open details
				//define : a swipe RL => sms
				//define : a swipe LR => call
				Contact contact = (Contact) parent.getAdapter().getItem(position);

				if(swipeDetector.swipeDetected()) {
					if(swipeDetector.getAction() == SwipeDetector.Action.RL) {
						//make a box to write a message appear and then send text
						alertBox(contact.getPhoneNumber());

					} else if(swipeDetector.getAction() == SwipeDetector.Action.LR){
						makeCall(contact.getPhoneNumber());
					}else{
						//on a listview up & bottom detection are not well detected

					}
				}
				else{
					//simple touch

				}
				//Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
			}
		});
	}

	public void sendSms(String phoneNumber,String message)
	{
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(phoneNumber, null, message, null, null);
	}

	public void makeCall(String phoneNumber)
	{
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
		startActivity(intent);
	}

	public void alertBox(final String phoneNumber)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		final EditText edittext= new EditText(ListActivity.this);
		alert.setMessage("Enter Your Message");
		alert.setTitle("SMS");

		alert.setView(edittext);

		alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//What ever you want to do with the value
				String value = edittext.getText().toString();
				sendSms(phoneNumber,value);

			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// what ever you want to do with No option.
			}
		});

		alert.show();
	}
    
	protected void onActivityResult(int request, int result, Intent data) {
		// check the request code for the intent and if the result was ok. 
		if(request == 42 & result == RESULT_OK) {
			//Update the array list
			updateArrayList();
			//Display the changement
			array_adaptater.notifyDataSetChanged();
		}
	}


    
    
}

package com.example.contactmanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddContactActivity extends Activity {
	
	//useful string to describe the database
	private static final String table_name = "contact";
	private static final String column_name ="NAME";
	private static final String column_phone ="PHONENUMBER";
	private static final String column_mail = "EMAIL";
	
	//private fields to use the database
	private SQLiteDatabase sdb;
	private ContactDBOpenHelper cdb;
	
	//Ui component
	private Button button_add;
	private EditText et_name;
	private EditText et_phone;
	private EditText et_mail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		
        //Initialize the database
        cdb = new ContactDBOpenHelper(this, table_name+".db", null, 1);
        sdb = cdb.getWritableDatabase();
        
        //Initialize views
        button_add = (Button) findViewById(R.id.button_add);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_mail = (EditText) findViewById(R.id.et_mail);
		
		
		button_add.setOnClickListener(new OnClickListener() {
			// overridden on click method to return a result to the starter of this activity
			public void onClick(View v) {
				String name = et_name.getText().toString();
				String phone = et_phone.getText().toString();
				String mail = et_mail.getText().toString();
				//make verifications so you can't add a empty contact
				if( name.equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please fill your Name", 
							   Toast.LENGTH_LONG).show();
				}
				else if( phone.equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please fill your Phone Number", 
							   Toast.LENGTH_LONG).show();
				}
				else if( mail.equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please fill your Email", 
							   Toast.LENGTH_LONG).show();
				}
				else if(phone.length() != 10 || !isInteger(phone) || phone.charAt(0)!= '0')
				{
					Toast.makeText(getApplicationContext(), "Invalid Phone Number",
							Toast.LENGTH_LONG).show();
				}
				else{
					//send back the result and finish this activity
					Intent result = new Intent(Intent.ACTION_VIEW);
					try{
						addTupleToDatabase(name, phone, mail);
						setResult(RESULT_OK, result);
						finish();
					}
					catch(Exception e)
					{
						setResult(RESULT_CANCELED, result);
						finish();
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_contact, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    //Useful function to add a tuple to the database (write it only once)
    public void addTupleToDatabase(String name, String phone, String mail)
    {    	
    	ContentValues cv = new ContentValues();
    	cv.put(column_name, name);
    	cv.put(column_phone, phone);
    	cv.put(column_mail, mail);
    	sdb.insert(table_name,null,cv);
    }

	//useful function to check is the phoneNumber entered is correct
	public boolean isInteger( String input )
	{
		try
		{
			Integer.parseInt( input );
			return true;
		}
		catch( Exception e)
		{
			return false;
		}
	}
	

}

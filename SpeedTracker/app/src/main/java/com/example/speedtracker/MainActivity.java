package com.example.speedtracker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{
	
	private String tag = "main activity";
	//private fields of the class
	//UI
	private TextView tv_time, tv_speed, tv_current, tv_overall;
	private Button b_track;
	private TraceView traceview;
	//locationManager
	private LocationManager lm;
	private Location previous_location;
	//ArrayList
	private ArrayList<Double> tracks;
	//useful var
	boolean isTrackActive;
	double current_average;
	double overall_average;
	double speed;
	double time;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //pull all the component from the xml
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_speed = (TextView) findViewById(R.id.tv_speed);
        tv_current = (TextView) findViewById(R.id.tv_current);
        tv_overall = (TextView) findViewById(R.id.tv_overall);
        b_track = (Button) findViewById(R.id.b_track);
        traceview = (TraceView) findViewById(R.id.traceview);
        //init the array list & attach it to the trace view
        tracks = new ArrayList<Double>();
        traceview.setTrace(tracks);
        //init the location service
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //previous_location = new Location();
        //var
        isTrackActive = false;
        //listener
        b_track.setOnClickListener(this);
        addLocationListener();
    }


	@Override
	public void onClick(View v) {
		
		if(isTrackActive)
		{
			b_track.setText(R.string.start_tracking);
			stopTracking();
		}
		else{
			b_track.setText(R.string.stop_tracking);
			startTracking();
		}		
	}
	
	public void startTracking()
	{
		//clear the previous location object
		previous_location = null;
		//reset all values
		isTrackActive = true;
		current_average = 0;
		overall_average = 0;
		time =0; 
		speed = 0;
		tracks = new ArrayList<Double>();
		//reset the view
		traceview.reset();
		//Also Clear the TraceView so
		updateUI();
	}
	
	public void stopTracking()
	{
		isTrackActive = false;
	}
	
	public void updateUI()
	{
		//update each element of the TraceView
		traceview.setTrace(tracks);
		traceview.updateCurrentAverage(current_average);
		traceview.updateOverallAverage(overall_average);
		traceview.update();
		//update the textviews
		tv_time.setText("Time : "+ time);
		tv_speed.setText("Speed : "+ speed);
		tv_current.setText("Current Speed Average : "+ current_average);
		tv_overall.setText("Overall Speed Average : "+ overall_average);				
	}
	
	public void computeShortAverage()
	{
		//TODO : check sur papier si ça a du sens
		if(tracks.size() == 0)
			return;
		else if(tracks.size()==1)
			current_average = tracks.get(0);
		else if(tracks.size() < 20)
		{
			double temp =0;
			for(int i = 0; i < tracks.size(); i++)
				temp += tracks.get(i);
			current_average = temp/tracks.size();
		}
		else
		{
			//we need to take only the last 20
			double temp =0;
			for(int i = 0; i < 20; i++)
				temp += tracks.get(tracks.size() -1 - i);
			current_average = temp/20;
		}
	}
	
	public void computeOverallAverage()
	{
		if(tracks.size() == 0)
			return;
		else if(tracks.size()<20)
			overall_average = current_average;
		else
		{
			//there are N element in the list
			//the last average was compute on N-1 element so ponderation
			//add the new element & divide by N
			double temp = overall_average*(tracks.size()-1) + tracks.get(tracks.size()-1);
			overall_average = temp/tracks.size();
		}

	}
	
	public void computeSpeed(Location previous, Location current)
	{
		double distance = previous.distanceTo(current);
		speed = distance*3.6;
		//don't divide by the time because it update every second
	}
	
	// private method that will add a location listener to the location manager
    private void addLocationListener() {
    	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 0, new  LocationListener() {
    				@Override
    				public void onLocationChanged(Location location) {
    					
    					if(isTrackActive)
    					{
    						Log.i(tag,"in on location change");

    						//get speed : on emulator getSpeed() ==0
    						//speed = location.getSpeed();
    						if(previous_location != null)
    						{
    							computeSpeed(previous_location,location);
    							Log.i(tag,"speed : "+speed);
    							//add speed to the list
    							tracks.add(speed);
    							//compute new short average (20)
    							computeShortAverage();
    							//compute new overall average
    							computeOverallAverage();
    						}
    						//add time
    						time += 1;
    						//update UI
    						updateUI();
    						//previous location
    						previous_location = location;
    					}
    				}
    				@Override
    				public void onProviderDisabled(String provider) {
    					// if GPS has been disabled then update one of textview to reflect this
    					if(provider == LocationManager.GPS_PROVIDER) {
    						tv_time.setText(R.string.gps_disable);
    						tv_speed.setText(R.string.gps_disable);
    						tv_current.setText(R.string.gps_disable);
    						tv_overall.setText(R.string.gps_disable);
    					}
    				}
    				@Override
    				public void onProviderEnabled(String provider) {
    					// if there is a last known location then set it on the
    					//textviews
    					if(provider == LocationManager.GPS_PROVIDER) {
    						tv_time.setText(R.id.tv_time);
    						tv_speed.setText(R.id.tv_speed);
    						tv_current.setText(R.id.tv_current);
    						tv_overall.setText(R.id.tv_overall);
    					}
    				}
    				@Override
    				public void onStatusChanged(String provider, int status, Bundle extras) {
    				}
    	});
    }
	
	
}

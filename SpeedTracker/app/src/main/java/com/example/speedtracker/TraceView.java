package com.example.speedtracker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TraceView extends View{
	
	private final String tag = "traceview";
	
	private ArrayList<Double> trace_data;
	private double overall_average;
	private double current_average;
	private int width =0,height =0,size=0;
	private double minSpeed =0,maxSpeed =0.2, pixelsize_vertically=0, pixelsize_horizontally=0;
	double last_track, last_y = -1;
	double point, orangeline_origin,redline_origin,greyline_origin;
	private Paint grey, red, green,orange;
	int grey_line = 10;

	public TraceView(Context context) {
		
		super(context);

		init();
	}
	
	// constructor that takes in a context and also a list of attributes
	// that were set through XML
	public TraceView(Context c, AttributeSet as) {
		super(c, as);
		init();
	}
	
	// constructor that take in a context, attribute set and also a default
	// style in case the view is to be styled in a certain way
	public TraceView(Context c, AttributeSet as, int default_style) {
		super(c, as, default_style);
		init();
	}
	
	// refactored init method as most of this code is shared by all the
	// constructors
	private void init() {
		
		trace_data = new ArrayList<Double>();
		overall_average = 0;
		current_average =0;
		//SET BACK GROUND
		this.setBackgroundColor(0xFF000000);
		//initcolor
		initColor();
	}
	
	public void initColor()
	{
		//paint declaration
		red = new Paint(Paint.ANTI_ALIAS_FLAG);
		green = new Paint(Paint.ANTI_ALIAS_FLAG);
		grey = new Paint(Paint.ANTI_ALIAS_FLAG);;
		orange = new Paint(Paint.ANTI_ALIAS_FLAG);
		red.setColor(0xFFFF0000);
		green.setColor(0xFF00FF00);
		grey.setColor(0xFF808080);
		orange.setColor(0xFFFFA500);
	}
	
	public void reset()
	{
		trace_data = new ArrayList<Double>();
		overall_average = 0;
		current_average =0;
		minSpeed =0;
		maxSpeed =0.2;
		pixelsize_vertically=0;
		pixelsize_horizontally=0;		
		last_track=0;
		grey_line = 10;
		last_y = -1;
	}
	
	// a method for attaching an array list of GPS trace data
	public void setTrace(ArrayList<Double> trace_data)
	{
		this.trace_data = trace_data;
	}
	
	//updates the overall average speed to be shown on the trace 
	//param : overall average speed computed in MainActivity
	public void updateOverallAverage(double overall_average) 
	{
		this.overall_average= overall_average;
	}
	
	//updates the current average speed to be shown on the trace 
	//param : current average speed computed in MainActivity
	public void updateCurrentAverage(double current_average)
	{
		this.current_average = current_average;
	}
	
	public void update()
	{
		if(!trace_data.isEmpty())
		{
			last_track = trace_data.get(trace_data.size()-1);
			
			//determine if the max and min speed have changed
			if(last_track > maxSpeed) 
				maxSpeed = last_track;
			else if(last_track < minSpeed)
				minSpeed = last_track;
			
			//recalculate how much pixel there must be for a single second
			//Vertically
			pixelsize_vertically= size/maxSpeed;
			//horizontally
			pixelsize_horizontally = size/trace_data.size();

			//force the display update
			invalidate();
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
			// call the superclass method
			super.onDraw(canvas);
			Log.i(tag,"On draw");

			canvas.save();
			
			if(trace_data.size() > 1)
			{
				//draw current average = orange
				orangeline_origin = size - current_average*pixelsize_vertically;
				canvas.drawLine(0, (float)orangeline_origin, 
						size, (float)orangeline_origin,
						orange);
				//draw overall average = rouge
				redline_origin = size - overall_average*pixelsize_vertically;
				canvas.drawLine(0,(float)redline_origin, 
						size,(float)redline_origin,
						red);
				//draw grey lines
				while(grey_line<maxSpeed)
				{
					greyline_origin = size - grey_line*pixelsize_vertically;
					canvas.drawLine(0, (float) greyline_origin, size, (float) greyline_origin, grey);
					grey_line += 10;
				}
				grey_line = 10;
			}
			
			//draw each point
			canvas.translate(5,0);
			for(double x : trace_data)
			{	
				//TODO
				point = size - x*pixelsize_vertically;
				canvas.drawCircle(0,(float)point,5,green);
				//canvas.translate((float) pixelsize_horizontally,0);
				
				if(last_y != -1)
				{
					//dessiner la ligne
					canvas.drawLine(-(float)pixelsize_horizontally, (float)last_y, 0, (float)point, green);
				}
				canvas.translate((float) pixelsize_horizontally,0);
				
				last_y = point; 
			}
			canvas.restore();

	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		width = getMeasuredWidth();
		height = getMeasuredHeight();

		
		if (width > height) {
			size = height;
		} else {
			size = width;
		}

		setMeasuredDimension(size, size);
	}

}

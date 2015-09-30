package com.example.minesweeper;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MineCustomView extends View {
	
	//grid
	private boolean minegrid[][]; //to know where the mine are
	private int statecell[][]; //to know if the cell is covered,uncovered or marked
	private int numberofminearound[][]; //to store the number of mine around a cell at the beggining of the game
	//state
	private final int STATE_COVER = 0;
	private final int STATE_UNCOVER = 1;
	private final int STATE_MARKED = 2;
	//differents paints : color & egde
	private Paint black, grey, white,red, green, blue,yellow, edge;
	//shape
	private Rect square;
	//dimension the grid
	int min = 0;
	int max = 10;
	//useful var
	int minenumber = 0;
	int minetotal = 20;
	float x, y;
	float text = 30;
	int current_i, current_j;
	float width,height,size,sizetotal;
	boolean has_lost = false;
	
	
	// default constructor for the class that takes in a context
	public MineCustomView(Context c) {
		super(c);
		init();
	}
	
	// constructor that takes in a context and also a list of attributes
	// that were set through XML
	public MineCustomView(Context c, AttributeSet as) {
		super(c, as);
		init();
	}
	
	// constructor that take in a context, attribute set and also a default
	// style in case the view is to be styled in a certian way
	public MineCustomView(Context c, AttributeSet as, int default_style) {
		super(c, as, default_style);
		init();
	}
	
	// refactored init method as most of this code is shared by all the
	// constructors
	private void init() {
		
		//initialize variable
		minegrid = new boolean[max][max];
		numberofminearound = new int[max][max];
		statecell= new int[max][max];
		
		//paint declaration
		red = new Paint(Paint.ANTI_ALIAS_FLAG);
		green = new Paint(Paint.ANTI_ALIAS_FLAG);
		blue = new Paint(Paint.ANTI_ALIAS_FLAG);
		black = new Paint(Paint.ANTI_ALIAS_FLAG);
		grey = new Paint(Paint.ANTI_ALIAS_FLAG);
		white = new Paint(Paint.ANTI_ALIAS_FLAG);
		yellow = new Paint(Paint.ANTI_ALIAS_FLAG);
		red.setColor(0xFFFF0000);
		green.setColor(0xFF00FF00);
		blue.setColor(0xFF0000FF);
		black.setColor(0xFF000000);
		white.setColor(0xFFFFFFFF);
		grey.setColor(0xFF808080);
		yellow.setColor(0xFFFFFF00);
		red.setTextSize(text);
		green.setTextSize(text);
		blue.setTextSize(text);
		black.setTextSize(text);
		grey.setTextSize(text);
		white.setTextSize(text);
		yellow.setTextSize(text);
		

		// initialise the the edge
		edge = new Paint();
		edge.setColor(Color.GRAY);
		edge.setStrokeWidth(5);        
		edge.setStyle(Paint.Style.STROKE); 
						
		//put the set for a brand new game
		resetGame();
		
		
	}
	
	// public method that needs to be overridden to draw the contents of this  widget
	public void onDraw(Canvas canvas) {
		// call the superclass method
		super.onDraw(canvas);
		         
		canvas.save();
		//for each cell, check the state and do the treatment
		//if covered, draw it in black. if marked, draw it in yellow
		//if uncovered,  check if it's a mine and do the treatment
		for(int i =0; i<max; i++)
		{
			for(int j =0; j<max; j++)
			{	
				if(statecell[i][j]==STATE_COVER)
					canvas.drawRect(square, black);
				else if(statecell[i][j]==STATE_MARKED)
					canvas.drawRect(square,yellow);
				else{
					if(minegrid[i][j])
					{
						canvas.drawRect(square,red);
						canvas.drawText("M", size/2, size/2, black);
					}
					else{
						canvas.drawRect(square,grey);
						canvas.drawText(numberofminearound[i][j]+"", size/2, size/2,defineColor(numberofminearound[i][j]));			
					}
				}
				//as soon as we finish a cell, draw the border and translate to draw another cell
				canvas.drawRect(square,edge);
				canvas.translate(size,0);
			}
			//as soon as we finish a line, go back the origins, add the the height so we can draw another line
			canvas.translate(-size*max,size);
		}
		canvas.restore();
	}
	
	// public method that needs to be overridden to handle the touches from a user
	public boolean onTouchEvent(MotionEvent event) {
		
		//check if the game must be reset or not
		if(has_lost)
		{
			resetGame();
			invalidate();
			return true;
		}
				
		// determine what kind of touch event we have
		if(event.getActionMasked() == MotionEvent.ACTION_DOWN) {

			//get the coordinate of the event
			x = event.getX();
			y = event.getY();
			//verify that these coordinates are in the area of the grid
			if(x<size*max && y<size*max)
			{
				current_i = (int)y/(int)size;
				current_j=(int)x/(int)size;
				//do the treatment : uncovered the cell only if its not marked
				if(statecell[current_i][current_j]!=STATE_MARKED)
				{
					statecell[current_i][current_j] = STATE_UNCOVER;
					//if mine, user has lost, else re-compute if some cell must be marked
					if(minegrid[current_i][current_j])
						has_lost = true;
					else
					{
						updateAllEmpty();
						updateMarked();
						//optionnal
						if(checkForVictory())
						{//dosomething	
							Log.i("Minesweeper","Victory");
						}
					}
				}
				//update the view
				invalidate();
				return true;
			}
		}
		return false;
	}
	
	//Function to reset the game, decomposed in 3 simple fonction with precise goal
	public void resetGame()
	{
		has_lost = false;
		resetBoard();
		setRandomMine();
		computeNumberofMineAround();
		invalidate();
	}
	
	//Function to reset the board : put all the cell in cover state, cancel the mine & the number of mine attached to each cell
	public void resetBoard()
	{
		minenumber=0;
		for(int i=min;i<max;i++)
		{
			for(int j =min; j<max;j++)
			{	
				minegrid[i][j]=false;
				numberofminearound[i][j]=0;
				statecell[i][j]=STATE_COVER;
			}
		}
	}
	
	//Function to set the 20 mines
	public void setRandomMine()
	{		
		Random rand;
		int i;
		int j;
		while(minenumber < minetotal)
		{
			rand = new Random();
			i = rand.nextInt(9);
			j = rand.nextInt(9);
			if(!minegrid[i][j])
			{
				minegrid[i][j] = true;
				minenumber++;
			}				
		}
	}
	
	//Function to set the numer of mine around each cell
	public void computeNumberofMineAround()
	{
		int number_of_mine = 0;
		for (int i = 0; i < max; i++) { 
	        for (int j = 0; j < max; j++) {
	        	
	        	if(i-1>= min)
	        	{
	        		if(minegrid[i-1][j])
	        			number_of_mine++;
	        		if(j-1>=min && minegrid[i-1][j-1])
	        			number_of_mine++;
	        		if(j+1<max && minegrid[i-1][j+1])
	        			number_of_mine++;
	        	}
	        	if( i + 1 < max)
	        	{
	        		if(minegrid[i+1][j])
	        			number_of_mine++;
	        		if(j-1>=min && minegrid[i+1][j-1])
	        			number_of_mine++;
	        		if(j+1<max && minegrid[i+1][j+1])
	        			number_of_mine++;
	        	}
	        	if( j-1>= min && minegrid[i][j-1])
	        		number_of_mine++;
	        	if( j+1 < max && minegrid[i][j+1])
	        		number_of_mine++;

	            numberofminearound[i][j] = number_of_mine;
	            number_of_mine =0;
	        }
		}
	}
	
	//Function to update the cell depending or not if they must be marked
	public void updateMarked()
	{
		for(int i=min;i<max;i++)
		{
			for(int j =min; j<max;j++)
			{	
				if(statecell[i][j]==STATE_UNCOVER) {
					checkForMarked(i, j, numberofminearound[i][j]);
				}
			}
		}
	}

	public void updateEmpty()
	{
		for(int i=min;i<max;i++)
		{
			for(int j =min; j<max;j++)
			{
				if(statecell[i][j] == STATE_UNCOVER && numberofminearound[i][j] == 0)
					discoverAround(i,j);

			}
		}

	}

	public void updateAllEmpty()
	{
		for(int i = 0; i <6; i++)
			updateEmpty();
	}
	
	//Function to compare the number of mine indicated in the cell and the number of mine left around
	public void checkForMarked(int i, int j, int numberofbomb)
	{
		int numberofcover = computeNumberofUncoveredCellAround(i, j);
		if(numberofcover <= numberofbomb)
		{
			markCell(i,j);
		}
	}
	
	//Function to determine the number of uncovered cell around the cell [i,j]
	public int computeNumberofUncoveredCellAround(int i, int j)
	{
		int number_of_cells = 0;
	        	if(i-1>= min)
	        	{
	        		if(statecell[i-1][j]==STATE_COVER || statecell[i-1][j]==STATE_MARKED )
	        			number_of_cells++;
	        		if(j-1>=min && (statecell[i-1][j-1]==STATE_COVER||statecell[i-1][j-1]==STATE_MARKED))
	        			number_of_cells++;
	        		if(j+1<max && (statecell[i-1][j+1]==STATE_COVER||statecell[i-1][j+1]==STATE_MARKED))
	        			number_of_cells++;
	        	}
	        	if( i + 1 < max)
	        	{
	        		if(statecell[i+1][j]==STATE_COVER || statecell[i+1][j]==STATE_MARKED)
	        			number_of_cells++;
	        		if(j-1>=min && (statecell[i+1][j-1]==STATE_COVER||statecell[i+1][j-1]==STATE_MARKED))
	        			number_of_cells++;
	        		if(j+1<max && (statecell[i+1][j+1]==STATE_COVER||statecell[i+1][j+1]==STATE_MARKED))
	        			number_of_cells++;
	        	}
	        	if( j-1>= min && (statecell[i][j-1]==STATE_COVER||statecell[i][j-1]==STATE_MARKED))
	        		number_of_cells++;
	        	if( j+1 < max && (statecell[i][j+1]==STATE_COVER||statecell[i][j+1]==STATE_MARKED))
	        		number_of_cells++;
	       
	       return number_of_cells;
	}	

	//Function to marked cell
	//very similar to computeNumberofUncoveredCellAround 
	//because it's called only when we know that the number of cell covered <= number of bomb around
	//so the cell left who are cover are bomb
	public void markCell(int i, int j)
	{

		if(i-1>= min)
    	{
    		if(statecell[i-1][j]==STATE_COVER)
    			statecell[i-1][j]=STATE_MARKED;
    		if(j-1>=min && statecell[i-1][j-1]==STATE_COVER)
    			statecell[i-1][j-1]=STATE_MARKED;
    		if(j+1<max && statecell[i-1][j+1]==STATE_COVER)
    			statecell[i-1][j+1]=STATE_MARKED;
    	}
    	if( i + 1 < max)
    	{
    		if(statecell[i+1][j]==STATE_COVER)
    			statecell[i+1][j]=STATE_MARKED;
    		if(j-1>=min && statecell[i+1][j-1]==STATE_COVER)
    			statecell[i+1][j-1]=STATE_MARKED;
    		if(j+1<max && statecell[i+1][j+1]==STATE_COVER)
    			statecell[i+1][j+1]=STATE_MARKED;
    	}
    	if( j-1>= min && statecell[i][j-1]==STATE_COVER)
    		statecell[i][j-1]=STATE_MARKED;
    	if( j+1 < max && statecell[i][j+1]==STATE_COVER)
    		statecell[i][j+1]=STATE_MARKED;
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		sizetotal = 0;
		size =0;
		width = getMeasuredWidth();
		height = getMeasuredHeight();

		if (width > height) {
			sizetotal = height;
		} else {
			sizetotal = width;
		}
		size= sizetotal/10;
		square = new Rect(0, 0,(int) size,(int) size);
		setMeasuredDimension((int)sizetotal, (int)sizetotal);
	}
	 
	//Useful function to get the color of the text depending of the number of mine around the cell
	//Optimisation purpose
	 public Paint defineColor(int numberOfMines)
	 {
		 switch(numberOfMines)
			{
				case 0 :
					return grey;

				case 1 :
					return blue;
					
				case 2 :
					return green;

				case 3 :
					return yellow;

				default:
					return red;

			}
	 }
	 
	 //Optionnal 
	 public boolean checkForVictory()
	 {
			for(int i=min;i<max;i++)
			{
				for(int j =min; j<max;j++)
				{	
					if(statecell[i][j] == STATE_COVER)
						return false;
				}
			}
			return true;
	 }

	//to discover all the cell around the empty one
	public void discoverAround(int i, int j)
	{
		if(i-1>= min)
		{
			statecell[i-1][j]=STATE_UNCOVER;
			if(j-1>=min){
				statecell[i-1][j-1]=STATE_UNCOVER;
			}
			if(j+1<max) {
				statecell[i - 1][j + 1] = STATE_UNCOVER;
			}
		}
		if( i + 1 < max)
		{
			statecell[i+1][j]=STATE_UNCOVER;
			if(j-1>=min) {
				statecell[i + 1][j - 1] = STATE_UNCOVER;
			}
			if(j+1<max) {
				statecell[i + 1][j + 1] = STATE_UNCOVER;
			}
		}
		if( j-1>= min) {
			statecell[i][j - 1] = STATE_UNCOVER;
		}
		if( j+1 < max) {
			statecell[i][j + 1] = STATE_UNCOVER;
		}
	}


}

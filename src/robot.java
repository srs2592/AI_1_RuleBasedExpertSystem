import java.util.*;

import javafx.animation.Animation.Status;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;



public class robot extends Application{
	
	//*******************************************************//
	private double sceneWidth = 744;
    private double sceneHeight = 744;
  
    Group root = new Group();

    static int dummy=0;
    static int n = 31;
    static int m = 31;
    
    static int moves=0;

    double gridWidth = sceneWidth / n;
    double gridHeight = sceneHeight / m;

    static MyNode[][] playfield = new MyNode[n][m];
    static Queue<Integer> queuex = new LinkedList<Integer>();
   	static Queue<Integer> queuey = new LinkedList<Integer>();
   	
   	static Queue<Integer> queueAx = new LinkedList<Integer>();
   	static Queue<Integer> queueACx = new LinkedList<Integer>();
   	static Queue<Integer> queueACy = new LinkedList<Integer>();
	//*********************************************************//
	
	static boolean reached=false;//Flag for reaching at destination
	
	//Starting Point
	static int startx;
	static int starty;
	//End Point
	static int endx;
	static int endy;
	//Current Point
	static int currentx;
	static int currenty;
	
	//Stacks for tracking path
	static Stack <Integer> pathX = new Stack<Integer>();
	static Stack <Integer> pathY = new Stack<Integer>();
	//
	static Stack <Integer> blackListx = new Stack<Integer>();
	static Stack <Integer> blackListy = new Stack<Integer>();
	
	static int obs;
	static int[][] realArray = new int[n][n];
	static int[][] robArray = new int[n][n];
	
	static double faceToward;
	static double angle;
	static double constant = (Math.PI/4);
	static double c = (180/Math.PI);
	
	static Status rotation = null;
	
	static String[][] opText = new String [200][2];
	static int oi=0, oj=0;
	
	
	public static void takeInputs(){
		
		opText[oi][0] = "ACTION";
		opText[oi][1] = "at (X, Y)";
		oi++;
		
		Scanner scan = new Scanner(System.in);
		Scanner scan1 = new Scanner(System.in);
		Scanner scan2 = new Scanner(System.in);
		Scanner scan3 = new Scanner(System.in);
		Scanner scan4 = new Scanner(System.in);
		
		
		int pObs;
		
		System.out.println("Its a 30 by 30 matrix, \nPlease enter percentage of obstacle you want/n For Ex: 10 or 30 or 50");
		pObs = scan.nextInt();
		
		System.out.println("Enter X-coordinate of Start");
		startx = scan1.nextInt();
		
		System.out.println("Enter Y-coordinate of Start");
		starty = scan2.nextInt();
		
		System.out.println("Enter X-coordinate of End");
		endx = scan3.nextInt();
		
		System.out.println("Enter Y-coordinate of End");
		endy = scan4.nextInt();
		
		//Current Point
		currentx = startx;
		currenty = starty;
		
		opText[oi][0] = "Start";
		opText[oi][1] = "("+Integer.toString(startx)+", "+Integer.toString(starty)+")";
		oi++;
		
		obs = (int) ((float) ((float)pObs/(float)100) * n *n);
		
	}
	
	
	public static void createMesh(){
		
		System.out.println("Please Enter n for 'nxn' matrix");
		System.out.println("Please Enter % of obstacles");
		
		//For obstacles
		Random num = new Random();
		
		for(int i=0; i<31; i++){
			for(int j=0; j<31; j++){
				
				if(i==0 || i==30)
					realArray[i][j]=8;
				else if(j==0 || j==30)
					realArray[i][j]=8;
				else
					realArray[i][j]= 1;
				
			}
		}
		
		
		for(int i=0; i<obs; i++){
			int p =1+ num.nextInt(29);
			int q =1+ num.nextInt(29);
			
			if((p==startx && q==starty) || (p==endx && q==endy))
				realArray[p][q]=1;
			else
				realArray[p][q]= 0;
		}
		
		for(int i=0; i<31; i++){
			for(int j=0; j<31; j++){
				System.out.print(realArray[i][j]+" ");
			}
			System.out.println("");
		}
		
	}

	public static void main(String[] args) {
		
		takeInputs();
		
		
		//Initializing the Robot Array to all zeros except start and end points
		for(int i=0; i<31; i++){
			for(int j=0; j<31; j++){
				if((i==startx && j==starty) || (i==endx && j==endy))
					robArray[i][j]=1;
				else
					robArray[i][j]=0;
			}
		}
		
		////////////////////////////////////////////////
		
		createMesh();

		///////////////////////////////////////////////
		
		//Step 0: To look for end point relative to present position and take the turn
		directFace();			
		
		//////////////////////////////*******Robot Recursive Logic Starts*******/////////////////////////
		StartAgain:
		while(reached!=true){			
			
			//Step 1: Check left diagonal and update, Check Straight and update, Check Right and update
				int x0=currentx,y0=currenty;
				int count0=0,count1=0,count2=0;
				
				//Straight line checking (1)
				while((realArray[x0][y0]==1))
				{
					x0= x0 + (int) Math.round(Math.sin(faceToward));
					y0= y0 + (int) Math.round(Math.cos(faceToward));
					if(x0<31 && y0<31){
						robArray[x0][y0]=realArray[x0][y0];
						count0++;
					}
					
					
					if((realArray[x0][y0]==8))
						break;
					
				} //Here index array out of bounds can happen
				
				
				int x1=currentx, y1=currenty;
				//Left line checking (2)
				while(realArray[x1][y1]==1){
					x1= x1 + (int) Math.round(Math.sin(faceToward+constant));
					y1= y1 + (int) Math.round(Math.cos(faceToward+constant));
					if(x1<31 && y1<31){
						
						robArray[x1][y1]=realArray[x1][y1];
						count1++;
					}
					
					if((realArray[x0][y0]==8))
						break;
				} //Here index array out of bounds can happen
				
				int x2=currentx, y2=currenty;
				//Right line checking (3)
				while(realArray[x2][y2]==1){
					x2= x2 + (int) Math.round(Math.sin(faceToward-constant));
					y2= y2 + (int) Math.round(Math.cos(faceToward-constant));
					if(x2<31 && y2<31){
						robArray[x2][y2]=realArray[x2][y2];
						count2++;
					}
					
					if((realArray[x0][y0]==8))
						break;
				} //Here index array out of bounds can happen
				//System.out.println("Entered here ");
				
			//Step 2: If (1), (2) & (3) blocked
				if((count0==1)&&(count1==1)&&(count2==1)){
					System.out.println("Entered block all 3");
					int xR=currentx;
					xR = xR + (int) Math.round(Math.sin(faceToward-(2*constant)));
					int yR=currenty;
					yR = yR + (int) Math.round(Math.cos(faceToward-(2*constant)));
					
					int xL=currentx;
					xL = xL + (int) Math.round(Math.sin(faceToward+(2*constant)));
					int yL=currenty;
					yL = yL + (int) Math.round(Math.cos(faceToward+(2*constant)));
					
					if(robArray[xL][yL]==0 && robArray[xR][yR]==0){
						//call backtrack method start from step 1    **********IMP********
						if(!pathX.empty() && !pathY.empty()){
							
							blackListx.push(currentx);
							blackListy.push(currenty);
							
							
							int m = pathX.peek();
							int n = pathY.peek();
							
							currentx = m;
							currenty = n;
							
							
							moves++;
							checkOI();
							opText[oi][0] = "Move";
							opText[oi][1] = "("+Integer.toString(currentx)+", "+Integer.toString(currenty)+")";
							oi++;
							
							//directFace();
							
						}
						else if(pathX.empty() && pathY.empty()){//for initial case, just change the direction
							faceToward = faceToward + constant;
							moves++;
							checkOI();
							opText[oi][0] = "Rotate to";
							opText[oi][1] = Integer.toString((int)(angle*c))+" Degrees";
							oi++;
						}
						System.out.println("Entered backtrack");
						//And go to step 1
						continue StartAgain;
					}
						
					else if(robArray[xL][yL]!=0 && robArray[xR][yR]==0){
						faceToward=faceToward+(2*constant);              //remember to count moves here
						moves=moves+2;	
						checkOI();
						opText[oi][0] = "Rotate (2 Moves)";
						opText[oi][1] = Integer.toString((int)(angle*c))+" Degrees";
						oi++;
					}
					else if(robArray[xR][yR]!=0 && robArray[xL][yL]==0){
						faceToward=faceToward-(2*constant);
						moves=moves+2;
						checkOI();
						opText[oi][0] = "Rotate (2 Moves)";
						opText[oi][1] = Integer.toString((int)(angle*c))+" Degrees";
						oi++;
					}
					else if(robArray[xL][yL]!=0 && robArray[xR][yR]!=0){
						faceToward=faceToward+(2*constant);
						moves=moves+2;
						checkOI();
						opText[oi][0] = "Rotate (2 Moves)";
						opText[oi][1] = Integer.toString((int)(angle*c))+" Degrees";
						oi++;
					}
						
				}
				
			//Step 3: If one of the (1), (2) & (3) is open then call MOVE method	
				if(count0>1){
					
					move();
				}
				else if(count1>1){
					faceToward = faceToward + constant;
					moves++;
					checkOI();
					opText[oi][0] = "Rotate to";
					opText[oi][1] = Integer.toString((int)(angle*c))+" Degrees";
					oi++;
					
					//Move in faceToward direction i.e. in left
					move();
				}
				else if(count2>1){
					faceToward = faceToward - constant;
					moves++;
					checkOI();
					opText[oi][0] = "Rotate to";
					opText[oi][1] = Integer.toString((int)(angle*c))+" Degrees";
					oi++;
					
					//Move in faceToward direction i.e. in right
					move();
				}
				else {
					//Move in faceToward direction					
					move();
				}
		
				
		}		
		
		if(reached ==true){
			launch();
			
			opText[oi][0] = "TOTAL MOVES =";
			opText[oi][1] = Integer.toString(moves);
			oi++;
			
			writeFile f = new writeFile(opText,"Moves");
			System.out.println("**************Total Number of MOVES required are************ "+moves);
			
			System.exit(1);
		}
		
		
		/////////////////////////*******Robot Recursive Logic Ends*******////////////////////////////////////
	}
	
	//Move method which moves in faceToward direction and again changes facToward direction wrt destination
	public static void move(){
		
		checkOI();
		
		double tempAngle = faceToward;
		
			pathX.push(currentx);
			pathY.push(currenty);
			queuex.add(currentx);
			queuey.add(currenty);
		
		
		angle = angleConvert(faceToward);
		queueAx.add((int)(angle*c));
		queueACx.add(currentx);
		queueACy.add(currenty);
		
		
		currentx = currentx + (int) Math.round(Math.sin(faceToward));
		currenty = currenty + (int) Math.round(Math.cos(faceToward));
		moves++;
		System.out.print("CurrentX after="+currentx+", ");
		System.out.print("CurrentY after="+currenty);
		System.out.println("");
		System.out.println("FaceToward "+faceToward*c);
		
		checkOI();
		opText[oi][0] = "Move";
		opText[oi][1] = "("+Integer.toString(currentx)+", "+Integer.toString(currenty)+")";
		oi++;
		
		//Step 0 again to be on track
		
		directFace();//remember to update moves if face chenges
		
		if(tempAngle!=faceToward){
			queueAx.add((int)(angle*c));
			queueACx.add(currentx);
			queueACy.add(currenty);
			moves++;
			
			opText[oi][0] = "Rotate to";
			opText[oi][1] = Integer.toString((int)(angle*c))+" Degrees";
			oi++;
			
			}
		
		if(currentx==endx && currenty==endy){
			
			opText[oi][0] = "Its End";
			opText[oi][1] = "("+Integer.toString(endx)+", "+Integer.toString(endy)+")";
			oi++;
			
			reached = true;//flag raised
			queuex.add(currentx);
			queuey.add(currenty);
		}
		
		//remember to count moves
	
		//animate();
}
	
	private static void checkOI() {
		if(oi>=195){
			System.out.println("\nOOPS\n****Robot or Destination is blocked by obstacles****\nPlease restart\nSorry for Inconvenience");
			System.exit(1);
		}
		
	}


	//directFace method changes the face direction of the robot towards destination
	public static void directFace(){
		
		int dirx = endx-currentx;
		int diry = endy-currenty;		
		
		
		if(dirx>0 && diry>0){
			faceToward=constant;
			//angle = -constant;
		}
		else if(dirx>0 && diry==0){
			faceToward=2*constant;
			//angle=0;
		}
		else if(dirx>0 && diry<0){
			faceToward=3*constant;
			//angle=constant;
		}
		else if(dirx==0 && diry<0){
			faceToward=4*constant;
			//angle=2*constant;
		}
		else if(dirx<0 && diry<0){
			faceToward=5*constant;
			//angle=3*constant;
		}
		else if(dirx<0 && diry==0){
			faceToward=6*constant;
			//angle=4*constant;
		}
		else if(dirx<0 && diry>0){
			faceToward=7*constant;
			//angle=-3*constant;
		}
		else if(dirx==0 && diry>0){
			faceToward=0;
			//angle=-2*constant;
		}
		
		
		
	}
	
	public static double angleConvert(double face){
		double refinedAng = faceToward;
		
		if(faceToward==constant)
			refinedAng = constant;
		else if(faceToward==2*constant)
			refinedAng=0;
		else if(faceToward==3*constant || faceToward==-5*constant)
			refinedAng=-constant;
		else if(faceToward==4*constant || faceToward==-4*constant)
			refinedAng=-2*constant;
		else if(faceToward==5*constant || faceToward==-3*constant)
			refinedAng=-3*constant;
		else if(faceToward==6*constant || faceToward==-2*constant)
			refinedAng=-4*constant;
		else if(faceToward==7*constant || faceToward==-constant)
			refinedAng=3*constant;
		else if(faceToward==0 || faceToward==8*constant || faceToward==-8*constant)
			refinedAng=2*constant;
		
		
		return refinedAng;
	}

	//**********************************************************************************************************//
	@Override
    public void start(Stage primaryStage) {

       
        // initialize playfield
        for( int i=0; i < n; i++) {
            for( int j=0; j < m; j++) {

                MyNode node = new MyNode("",Color.LIGHTBLUE,i,j,1);

                // create Start
                if( i == startx &&  j == starty) {
                    node = new Bug( "Bot", Color.BLACK, i, j, 0);
                }
                // create Destination
                else if( i == endx && j == endy) {
                    node = new Food( "End", Color.LAWNGREEN, i, j, 1);
                }
                // create obstacle
                else if( realArray[i][j]==0 || realArray[i][j]==8) {
                    node = new Obstacle( "X", Color.INDIANRED, i, j, 1);
                }
                // add node to group
                if( node != null) {
                    root.getChildren().add(node);
                    // add to playfield for further reference using an array
                    playfield[i][j] = node;
                }


            }
        }

        Scene scene = new Scene( root, sceneWidth, sceneHeight);

        primaryStage.setScene(scene);
        primaryStage.show();

        
        animate();
       
    }
    
    public void animate(){
	  
    	int ai = queuex.poll();
    	int aj = queuey.poll();
    	int bi = queuex.peek();
    	int bj = queuey.peek();
    	int temp = 0;
    	int t1,cnt=0;
    	int t2, flag=1;
    	RotateTransition [] rt = new RotateTransition [5];
    	//rt = null;
    	if(queueACx.contains(ai) && queueACy.contains(aj)){
    		t1 = queueACx.peek();
    		t2 = queueACy.peek();
    		while(ai==t1 && aj==t2 && flag==1 ){
    			if(queueAx.size()==1 && flag==1){
	    		temp = queueAx.peek();
	    		t1 = queueACx.peek();
	    		t2 = queueACy.peek();
	    		flag=0;
    			}
    			else{
    				temp = queueAx.poll();
    	    		t1 = queueACx.poll();
    	    		t2 = queueACy.poll();
    			}
    			
    			rt[cnt] = animateRotation(temp, ai, aj);
	    		cnt++;
	    		    		
	    		t1 = queueACx.peek();
	    		t2 = queueACy.peek();
    		}
    		
    	}

        MyNode nodeA = playfield[ai][aj];
        nodeA.toFront();

        MyNode nodeB = playfield[bi][bj];
        nodeB.toFront();
        // swap on array to keep array consistent	        
        
        playfield[ai][aj] = nodeB;
        playfield[bi][bj] = nodeA;	        
         
        // A -> B
        Path pathA = new Path();
        pathA.getElements().add (new MoveTo ( nodeA.getTranslateX() + nodeA.getBoundsInParent().getWidth() / 2.0, nodeA.getTranslateY() + nodeA.getBoundsInParent().getHeight() / 2.0));
        pathA.getElements().add (new LineTo( nodeB.getTranslateX() + nodeB.getBoundsInParent().getWidth() / 2.0, nodeB.getTranslateY() + nodeB.getBoundsInParent().getHeight() / 2.0));

        PathTransition pathTransitionA = new PathTransition(); 
        pathTransitionA.setDuration(Duration.millis(1000));
        pathTransitionA.setNode( nodeA);
        pathTransitionA.setPath(pathA);

        //pathTransitionA.play();
        
        // B -> A
        Path pathB = new Path();
        pathB.getElements().add (new MoveTo ( nodeB.getTranslateX() + nodeB.getBoundsInParent().getWidth() / 2.0, nodeB.getTranslateY() + nodeB.getBoundsInParent().getHeight() / 2.0));
        pathB.getElements().add (new LineTo( nodeA.getTranslateX() + nodeA.getBoundsInParent().getWidth() / 2.0, nodeA.getTranslateY() + nodeA.getBoundsInParent().getHeight() / 2.0));

        PathTransition pathTransitionB = new PathTransition(); 
        pathTransitionB.setDuration(Duration.millis(100));
        pathTransitionB.setNode( nodeB);
        pathTransitionB.setPath(pathB);

       // pathTransitionB.play();
        
        SequentialTransition sqt = new SequentialTransition();
        
        int dum=0;
        while(dum<=cnt){
        	if(rt[dum]!=null)
        		sqt.getChildren().add(rt[dum]);
        dum++;
        }
        sqt.getChildren().add(pathTransitionA);
        sqt.setCycleCount(1);
        sqt.setAutoReverse(true);
        
        root.getChildren().add(new MyNode( "", Color.YELLOW, ai, aj, 1));
        
        sqt.play();
       	
        
        pathTransitionA.setOnFinished( new EventHandler<ActionEvent>() {

           @Override
           public void handle(ActionEvent event) {

               if(pathTransitionA.getStatus() == Status.RUNNING)
                   return;

               if(queuex.size()>1 && queuey.size()>1)
            	   animate();
           }
       });

        pathTransitionB.setOnFinished( new EventHandler<ActionEvent>() {

           @Override
           public void handle(ActionEvent event) {

               if(pathTransitionB.getStatus() == Status.RUNNING)
                   return;
               
               if(queuex.size()>1 && queuey.size()>1)
            	   animate();
           }
       });        
       
   }
    
    public static RotateTransition animateRotation(int angle, int ai, int aj){
    	
    	MyNode nodeA = playfield[ai][aj];
    	RotateTransition rt = new RotateTransition(Duration.millis(500), nodeA);
    	rt.stop();
        double frmAngl = rt.getFromAngle();
        System.out.println("Angle toAngle="+angle);	
        
        rt.setToAngle(angle);
        rt.setCycleCount(1);
        rt.setAutoReverse(true);
        
        return rt;
        /*
        rt.play();
        rotation = rt.getStatus();
        
        rt.setOnFinished(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				if(rt.getStatus() == Status.RUNNING)
					try {
						event.wait(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}//return;
				
			}});
        
    	*/
    }
    
    
	   
    
    private class Food extends MyNode {
        public Food(String name, Color color, double x, double y, int typo) {
            super(name, color, x, y, typo);
        }
    }

    private class Obstacle extends MyNode {
        public Obstacle(String name, Color color, double x, double y, int typo) {
            super(name, color, x, y, typo);
        }
    }

    private class Bug extends MyNode {
        public Bug(String name, Color color, double x, double y, int typo) {
            super(name, color, x, y, typo);
        }
    }

    private class MyNode extends StackPane {

        public MyNode( String name, Color color, double x, double y, int typo) {

        	// create label
            Label label = new Label( name);
            if(typo==1){
        	// create rectangle
            Rectangle rectangle = new Rectangle(gridWidth, gridHeight);
            rectangle.setStroke( color.BLACK);
            rectangle.setFill( color.deriveColor(1, 1, 1, 0.7));

            // set position
            setTranslateX( x * gridWidth);
            setTranslateY( y * gridHeight);

            getChildren().addAll( rectangle, label);
            }
            else if(typo==0){
            	Polygon triangle = new Polygon((startx*24), (starty*24), ((startx)*24), ((starty+1)*24),((startx+1)*24), ((starty*24)+12));
                triangle.setFill(Color.BLACK);
                triangle.setStroke(Color.BLUE);
                triangle.setStrokeWidth(1);
                
                setTranslateX( x * gridWidth);
                setTranslateY( y * gridHeight);

                getChildren().addAll( triangle, label);
            	
            }

        }

    }

	//**********************************************************************************************************//
	
	
}

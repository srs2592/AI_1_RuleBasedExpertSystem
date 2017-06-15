import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javafx.animation.Animation.Status;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
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


public class roboGraphics extends Application{

	 	private double sceneWidth = 744;
	    private double sceneHeight = 744;
	  
		/////////////////
		int startx = robot.startx;
		int starty = robot.starty;
		int endx = robot.endx;
		int endy = robot.endy;
		/////////////////

	    private int n = 31;
	    private int m = 31;
	    
	    int [][] realArray = robot.realArray;
	    

	    double gridWidth = sceneWidth / n;
	    double gridHeight = sceneHeight / m;

	    MyNode[][] playfield = new MyNode[n][m];
	
	    @Override
	    public void start(Stage primaryStage) {

	        Group root = new Group();

	        // initialize playfield
	        for( int i=0; i < n; i++) {
	            for( int j=0; j < m; j++) {

	                MyNode node = new MyNode("",Color.LIGHTBLUE,i,j,1);

	                // create Start
	                if( i == startx &&  j == starty) {
	                    node = new Bug( "Bot", Color.GREEN, i, j, 0);
	                }
	                // create Destination
	                else if( i == endx && j == endy) {
	                    node = new Food( "End", Color.ORANGE, i, j, 1);
	                }
	                // create obstacle
	                else if( realArray[i][j]==0 || realArray[i][j]==8) {
	                    node = new Obstacle( "o", Color.GRAY, i, j, 1);
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

	        System.out.println("Done till here");
	       
	    }
	    
	    public void animate(){
    	  
	    	int bi = robot.pathX.pop();
	    	int bj = robot.pathY.pop();
	    	int ai = robot.pathX.peek();
	    	int aj = robot.pathY.peek();
	    	

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

	        pathTransitionA.play();
	        
	        // B -> A
	        Path pathB = new Path();
	        pathB.getElements().add (new MoveTo ( nodeB.getTranslateX() + nodeB.getBoundsInParent().getWidth() / 2.0, nodeB.getTranslateY() + nodeB.getBoundsInParent().getHeight() / 2.0));
	        pathB.getElements().add (new LineTo( nodeA.getTranslateX() + nodeA.getBoundsInParent().getWidth() / 2.0, nodeA.getTranslateY() + nodeA.getBoundsInParent().getHeight() / 2.0));

	        PathTransition pathTransitionB = new PathTransition(); 
	        pathTransitionB.setDuration(Duration.millis(1000));
	        pathTransitionB.setNode( nodeB);
	        pathTransitionB.setPath(pathB);

	       
	        pathTransitionB.play();
	        
	        
	        pathTransitionA.setOnFinished( new EventHandler<ActionEvent>() {

	           @Override
	           public void handle(ActionEvent event) {

	               if( pathTransitionB.getStatus() == Status.RUNNING)
	                   return;

	               animate();
	           }
	       });

	        pathTransitionB.setOnFinished( new EventHandler<ActionEvent>() {

	           @Override
	           public void handle(ActionEvent event) {

	               if( pathTransitionA.getStatus() == Status.RUNNING)
	                   return;
	               
	               animate();
	           }
	       });        
	       
	   }
	    
	    public void animateRotation(int angle, int ai, int aj){
	    	MyNode nodeA = playfield[ai][aj];
	    	RotateTransition rt = new RotateTransition(Duration.millis(500), nodeA);
	        double frmAngl = rt.getFromAngle();
	        	
	        rt.setToAngle(angle);
	        rt.setCycleCount(1);
	        rt.setAutoReverse(true);
	    
	        rt.play();
	    	
	    }
	    
	    
	    public void roboGraphics(){
	    	launch();
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
	            Rectangle rectangle = new Rectangle( gridWidth, gridHeight);
	            rectangle.setStroke( color.BLACK);
	            rectangle.setFill( color.deriveColor(1, 1, 1, 0.7));

	            // set position
	            setTranslateX( x * gridWidth);
	            setTranslateY( y * gridHeight);

	            getChildren().addAll( rectangle, label);
	            }
	            else if(typo==0){
	            	Polygon triangle = new Polygon((startx*24), (starty*24), ((startx+1)*24), (starty*24),((startx*24)+12), ((startx+1)*24));
	                triangle.setFill(Color.CYAN);
	                triangle.setStroke(Color.BLUE);
	                triangle.setStrokeWidth(1);
	                
	                setTranslateX( x * gridWidth);
	                setTranslateY( y * gridHeight);

	                getChildren().addAll( triangle, label);
	            	
	            }

	        }

	    }

	    
	    
}

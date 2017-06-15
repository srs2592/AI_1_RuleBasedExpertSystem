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

public class NodeDemo extends Application {

    private double sceneWidth = 512;
    private double sceneHeight = 512;
    
    
    static Queue<Integer> queuex = new LinkedList<Integer>();
	static Queue<Integer> queuey = new LinkedList<Integer>();
	
	/////////////////
	
	/////////////////

    private int n = 10;
    private int m = 10;

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
                if( i == 0 && j == 0) {

                    node = new Bug( "Bot", Color.GREEN, i, j, 0);
                    
                }
                // create Destination
                else if( i == 5 && j == 5) {

                    node = new Food( "End", Color.ORANGE, i, j, 1);
                }
                // create obstacle
                else if( i == 3 && j == 3) {

                    node = new Obstacle( "Obstacle", Color.GRAY, i, j, 1);
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

        primaryStage.setScene( scene);
        primaryStage.show();


		/////////////////////////////
		queuex.add(0); queuey.add(0);
		queuex.add(1); queuey.add(1);
		queuex.add(2); queuey.add(2);
		queuex.add(2); queuey.add(3);
		queuex.add(2); queuey.add(4);
		queuex.add(3); queuey.add(4);
		queuex.add(4); queuey.add(4);
		queuex.add(5); queuey.add(5);
		/////////////////////////////
        // move bug

        animate();
        
    }

    private void animate() {

    	  	
    	
        int ai = queuex.poll();
        int aj = queuey.poll();

        int bi = queuex.peek();
        int bj = queuey.peek();

        

        MyNode nodeA = playfield[ai][aj];
        nodeA.toFront();

        MyNode nodeB = playfield[bi][bj];
        nodeB.toFront();

        // swap on array to keep array consistent
        
        
        playfield[ai][aj] = nodeB;
        playfield[bi][bj] = nodeA;
        
        int dirx = bi-ai;
        int diry = bj-aj;
        
        RotateTransition rt = new RotateTransition(Duration.millis(500), nodeA);
        double frmAngl = rt.getFromAngle();
        if(dirx==1 && diry==1){
        //RotateTransition rt = new RotateTransition(Duration.millis(1000), nodeA);
        	
        rt.setToAngle(-45);
        rt.setCycleCount(1);
        rt.setAutoReverse(true);
    
        rt.play();
        }
        else if(dirx==0 && diry==1){
           // RotateTransition rt = new RotateTransition(Duration.millis(1000), nodeA);
            rt.setToAngle(0);
            rt.setCycleCount(1);
            rt.setAutoReverse(true);
        
            rt.play();
            }
        else if(dirx==1 && diry==0){
            //RotateTransition rt = new RotateTransition(Duration.millis(1000), nodeA);
            rt.setToAngle(-90);
            rt.setCycleCount(1);
            rt.setAutoReverse(true);
        
            rt.play();
            }
        
       
         
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

        //if(queuex.size()>1 && queuey.size()>1)
        //pathTransitionB.play();
        
        
        pathTransitionA.setOnFinished( new EventHandler<ActionEvent>() {

           @Override
           public void handle(ActionEvent event) {

               if( pathTransitionB.getStatus() == Status.RUNNING)
                   return;

               if(queuex.size()>1 && queuey.size()>1)
               animate();
           }
       });

        pathTransitionB.setOnFinished( new EventHandler<ActionEvent>() {

           @Override
           public void handle(ActionEvent event) {

               if( pathTransitionA.getStatus() == Status.RUNNING)
                   return;
               
               if(queuex.size()>1 && queuey.size()>1)
               animate();
           }
       });        
        
   }

    public void NodeDemo() {//Stack<Integer> path

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
            	Polygon triangle = new Polygon(0, 0, 50, 0, 25, 50);
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
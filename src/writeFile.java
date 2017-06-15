
import java.io.*;
import java.util.*;
import java.lang.*;


public class writeFile {
	
	private Formatter k;
	String newline = System.getProperty("line.separator");
	
	public writeFile(String[][] arrayOP, String z){
		try{
			k= new Formatter(z+"Output.txt");
		}
		catch(Exception e){
			System.err.println(e);
		}
		k.format("%s  Details"+newline+ newline,z);
		k.format("Our Bot reached the Destination!!"+ newline+ newline);
		
	for(int m=0;m<arrayOP.length;m++){
		for(int n=0;n<arrayOP[m].length;n++){
			if(arrayOP[m][n]!=null){
				k.format(" %s\t\t", arrayOP[m][n]);
			}
		}
		k.format(newline);
	}
		
	k.close();
	
	}
	

}

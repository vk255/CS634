/*
 *
 * Venkata Harish K Kajur
 * Professor Wang
 * CS 634-852
 * April 5, 2014
 *
 */

import java.util.*;
import java.io.*;

/*
 *
 * Node class
 * Creates a Node with x, y and z values
 * Creates a Node with x and y if only two parameters
 * Sets a index for each node to keep track of them
 *
 */

class Node{

	public int x, y, z;

	public int index;

	public Node(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}	

	public Node(int x, int y){
		this.x = x;
		this.y = y;	
		this.z = -1;
	}

    
    /*
     * Set the x y and z values
     */

   	private void setX(int x) { this.x = x; }
   	private void setY(int y) { this.y = y; }
   	private void setZ(int z) { this.z = z; }

   	/*
   	 * Get the x y and z values
   	 */

	public int getX() { return x; }
	public int getY() { return y; }
	public int getZ() { return z; }
	
	public void setIndex(int ind)	{ this.index = ind; }
   	public int getIndex()		{ return index; }
		
	public boolean is3D() { return (this.z == -1) ? false : true ; }

	public String toString() { 
		return 	(this.z == -1) ? 
		"(" + x + ", " + y + ")" : 
		"(" + x + ", " + y + ", " + z + ")"; 
	}

}


class Cluster {

    public static final String NEWLINE = System.getProperty("line.separator");

    public Node[] list;

	public Cluster(Node... n){

		int size = n.length;
	
		list = new Node[size];
	
		for(int i = 0; i < size; i++)
			list[i] = n[i];
	
	}

	public void merge(Cluster b){
		
		int sizeA = this.getSize();
		int sizeB = b.getSize();

		int sizeOfTemp = sizeA + sizeB;

		Node array[] = new Node[sizeOfTemp];

		int i, j;

		for(i = 0; i < sizeA; i++)
			array[i] = this.list[i];
		
		for(j = i, i = 0; j < sizeOfTemp; j++)
			array[j] = b.list[i++];

		this.list = array;	

	}

    public double[] getCenter(){
       
        double sumX, sumY;

        double[] temp = null;
        
        int size = this.getSize();

        if(list[0].is3D()){
       
            double sumZ;

            sumX = 0.0;
            sumY = 0.0;
            sumZ = 0.0;
        
            for(int i = 0; i < size; i++)
            {
                sumX = sumX + list[i].getX();        
                sumY = sumY + list[i].getY();        
                sumZ = sumZ + list[i].getZ();        
            }

            temp = new double[]{ 0, 0, 0 };

            temp[0] = sumX / size;
            temp[1] = sumY / size;
            temp[2] = sumZ / size;
            
        }
        else {
            
            sumX = 0.0;
            sumY = 0.0;
           
            for(int i = 0; i < size; i++)
            {
                sumX = sumX + list[i].getX();        
                sumY = sumY + list[i].getY();        
            }

            temp = new double[]{ 0, 0 };

            temp[0] = sumX / size;
            temp[1] = sumY / size;

        }

        return temp;

    }

	public boolean checkIfNodeExists(int index){
		
		for(Node n: list){
			if(n.getIndex() == index)
				return true;
		}
		
		return false;
	}
	
    public int getSize(){ return list.length; } 
	
	public String toString() {

		String temp = "";
	
		int i = 0;	
		
		for(Node n: list) {
		
			if( (i+1) % 10 == 0)	
				temp = temp + n.toString() + NEWLINE;
			else
				temp = temp + n.toString() + " ";
			i++;	
		
        }
	
		return temp;	
	}
}

	
public class Agglomerative {

	public static LinkedList<Cluster> clusters; 

	public static double[][] getDistances(int dim, Node[] array){

		int size = array.length;

		double[][] distances = new double[size][size];

		if(dim == 2){

			for(int i = 0; i < size; i++){
				for(int j = 0; j < size; j++){
					if(i != j){
						distances[i][j] = (Math.sqrt(
						(Math.pow((array[i].getX() 
						- array[j].getX()), 2)
					     	+ Math.pow((array[i].getY() 
						- array[j].getY()), 2))));	
					}
				}
			}
		} else {

			for(int i = 0; i < size; i++){
				for(int j = 0; j < size; j++){
					if(i != j){

						distances[i][j] = (Math.sqrt(
						(Math.pow((array[i].getX() 
						- array[j].getX()), 2)
					     	+ Math.pow((array[i].getY() 
						- array[j].getY()), 2)
						+ Math.pow((array[i].getZ() 
						- array[j].getZ()), 2))));	
					}
				}
			}

		}

		return distances;

	}
	
	public static int[] getShortestDistance
			   (double[][] distances, 
			   boolean[][] isMarked){
		
		double minimum = 300000000000.0;
		int row = 0, column = 1;

		int size = distances.length;

		for(int i = 0; i < size - 1; i++){
	
			for(int j = i + 1; j < size; j++){

				if(distances[i][j] < minimum 
					&& !(isMarked[i][j])){
					minimum = distances[i][j];
					row = i;
					column = j;
				}
			}

		}

		isMarked[row][column] = true;
		
		int[] arr = { row, column };
		
		return arr;
	
	}
   
    public static int[] getFarthestDistance
			   (double[][] distances, 
			   boolean[][] isMarked){
		
		double maximum = -1.0;
		int row = 0, column = 1;

		int size = distances.length;

		for(int i = 0; i < size - 1; i++){
	
			for(int j = i + 1; j < size; j++){

				if(distances[i][j] > maximum 
					&& !(isMarked[i][j])){
					maximum = distances[i][j];
					row = i;
					column = j;
				}
			}

		}

		isMarked[row][column] = true;
		
		int[] arr = { row, column };
		
		return arr;
	
	}


	public static double round(double a){ 
		return  (int)(a * 100)/100.0; 
	 }

	public static void distanceBasedOutlier
				(double p, 
				double D){


	}

	public static Cluster getCluster(int ind){

		Cluster tmp = null;

		int size = clusters.size();
		
        for(int i = 0; i < size; i++){

			tmp = clusters.get(i);
			
            if(tmp.checkIfNodeExists(ind))
				break;
		}

		return tmp;
	}
	
	public static void main(String[] args){

		if(args.length == 0){
			System.err.println("Error: File Argument missing");
			System.exit(0);
		}

		try{

			clusters = new LinkedList<Cluster>();
			
			List<Node> initialArr = new ArrayList<Node>();
	
			String file = args[0];
			
			FileReader fs = new FileReader(file);
			
			BufferedReader br = new BufferedReader(fs);

			String line = null;
	        int ind = 0;	

			while( (line = br.readLine()) != null){

				String[] coordinates = line.split("\\s+");
			
				int x = Integer.parseInt(coordinates[0]);
				int y = Integer.parseInt(coordinates[1]);
				
				Node n = null;	

				if(coordinates.length == 2)
					n = new Node(x, y);
				else{
					int z = Integer.parseInt(coordinates[2]);	
					n = new Node(x, y, z);
				}
             
                n.setIndex(ind++);	
				initialArr.add(n);	
			}

			Node temp = initialArr.get(0);
			
			Node[] startArray = new Node[initialArr.size()];
			boolean[][] isMarked = new boolean[startArray.length][startArray.length];

			for(int i = 0; i < initialArr.size(); i++){
				startArray[i] = initialArr.get(i);
				clusters.add(new Cluster(startArray[i]));
			}

			initialArr.clear();
		
            double[][] initialDistances = null;

			if(temp.is3D()){
				
				// Do 3-Dimensional Node Distance calculations
				System.out.println("It is 3D");
				initialDistances = getDistances(3, startArray);

			} else {
			
				// Do 2-Dimensional Node Distance calculations
				System.out.println("It is 2D");
				initialDistances = getDistances(2, startArray);
			
            } 
		   
            Scanner Scan = new Scanner(System.in);  
            
            System.out.print("Enter number of clusters K: ");
            
            int k = Scan.nextInt();

                while(clusters.size() > k){
				
                    int[] shortDistance = getShortestDistance(initialDistances, isMarked);
				
				    int row = shortDistance[0];
    				int column = shortDistance[1];
		
                    Cluster c1 = getCluster(row);
		    		Cluster c2 = getCluster(column); 
                  

                    /*
                     * The reason I am doing a shallow check
                     * is because they will only end up pointing to
                     * the same cluster
                     * Because two clusters can't have the same nodes 
                     * inside them
                     */

                    if(c1 == c2) {
                        continue;
                    }
				
			    	c1.merge(c2);
			    	clusters.remove(c2);
					
		        
              }		

              System.out.println("Cluster List:");

              int i = 0;

              for(Cluster c: clusters){
                    System.out.println("Cluster C" + i++);      
                    System.out.println(c);
              }
		} catch(FileNotFoundException file){
			System.err.println(file); 
		} catch(IOException ex){
			System.err.println("Error: IOException");
		}
	}
    
    /*
     *
     * Method printBool() takes a Two-Dimensional Array as argument
     * The method goes through each element in the array 
     * Prints the value inside them
     *
     */

	public static void printBool(boolean[][] b){

		int rowSize = b.length;
		int columnSize = b[0].length;

		for(int i = 0; i < rowSize; i++){
			for(int j = 0; j < columnSize; j++){
				System.out.print(b[i][j] + " ");
			}
			System.out.println();
		}
	}
	public static void printDistance(double[][] distances){

		int rowSize = distances.length;
		int columnSize = distances[0].length;

		for(int i = 0; i < rowSize; i++){
			for(int j = 0; j < columnSize; j++){
				System.out.print(round(distances[i][j]) + " ");
			}
			System.out.println();
		}
	}
} 

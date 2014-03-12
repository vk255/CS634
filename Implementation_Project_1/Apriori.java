import java.io.*;
import java.util.*;

public class Apriori {

	public static ArrayList<String> transactions;
	public static ArrayList<String> items;
	public static ArrayList<Integer> count;
	public static ArrayList<String> list;
	public static ArrayList<ArrayList<String>> secondList;
	public static ArrayList<ArrayList<String>> resultSet;
	public static ArrayList<Integer> resultCount;
	public static ArrayList<Double> resultSupport;
	public static ArrayList<Double> resultConfidence;
	public static ArrayList<ArrayList<String>> eliminatedSet;
	public static int[] secCount;
	public static int[] countLeft;

	public static double support;
	public static double confidence;

	public enum States{
		START_STATE,
		TRANSACTION_STATE,
		TRANSACTION_SPACE_STATE,
		ITEMS_STATE,
		ITEMS_COMMA_STATE
	}

	public static void parseLine(String line){

		States state = States.START_STATE;

		StringBuffer tmpTransaction = new StringBuffer();
		StringBuffer tmpItem = new StringBuffer();
		
		int size = line.length();

		char ch = '\0';

		for(int i = 0; i < size; i++){

			switch(state){
				case START_STATE: 
					ch = line.charAt(i);
					
					if(Character.isLetterOrDigit(ch)){
						tmpTransaction.append(ch);
						state = States.TRANSACTION_STATE;
					}

					break;

				case TRANSACTION_STATE:
					ch = line.charAt(i);
					
					if(Character.isWhitespace(ch)){
						transactions.add(tmpTransaction.toString());
						tmpTransaction = new StringBuffer();
						state = States.TRANSACTION_SPACE_STATE;
					} else {
						tmpTransaction.append(ch);
					}

					break;

				case TRANSACTION_SPACE_STATE:
					ch = line.charAt(i);
					
					if(Character.isLetterOrDigit(ch)){
						tmpItem.append(ch);
						state = States.ITEMS_STATE;
					}

					break;

				case ITEMS_STATE:
					ch = line.charAt(i);
					
					if(ch == ','){
						//System.out.print(tmpItem.toString() + " ");
						items.add(tmpItem.toString());
						tmpItem = new StringBuffer();
						state = States.ITEMS_COMMA_STATE;
					}
					else if(i == (size-1)){
						tmpItem.append(ch);
						//System.out.print(tmpItem.toString() + " ");
						items.add(tmpItem.toString());
					}
					else {
						tmpItem.append(ch);
					}

					break;

				case ITEMS_COMMA_STATE:
					ch = line.charAt(i);
					
					if(Character.isLetterOrDigit(ch)){
						tmpItem.append(ch);
						state = States.ITEMS_STATE;
					}

					break;
			}
		}
	}

	public static boolean parse(String line, String... match){
		States state = States.START_STATE;

		StringBuffer tmpTransaction = new StringBuffer();
		StringBuffer tmpItem = new StringBuffer();
		
		boolean[] flag = new boolean[match.length];

		int size = line.length();

		char ch = '\0';

		for(int i = 0; i < size; i++){

			switch(state){
				case START_STATE: 
					ch = line.charAt(i);
					
					if(Character.isLetterOrDigit(ch)){
						tmpTransaction.append(ch);
						state = States.TRANSACTION_STATE;
					}

					break;

				case TRANSACTION_STATE:
					ch = line.charAt(i);
					
					if(Character.isWhitespace(ch)){
						tmpTransaction = new StringBuffer();
						state = States.TRANSACTION_SPACE_STATE;
					} else {
						tmpTransaction.append(ch);
					}

					break;

				case TRANSACTION_SPACE_STATE:
					ch = line.charAt(i);
					
					if(Character.isLetterOrDigit(ch)){
						tmpItem.append(ch);
						state = States.ITEMS_STATE;
					}

					break;

				case ITEMS_STATE:
					ch = line.charAt(i);
					
					if(ch == ','){
						for(int index = 0; index < match.length; index++)
						{
							if(tmpItem.toString().equals(match[index]))
								flag[index] = true;
						}
						tmpItem = new StringBuffer();
						state = States.ITEMS_COMMA_STATE;
					}
					else if(i == (size - 1)){
						tmpItem.append(ch);
						for(int index = 0; index < match.length; index++)
						{
							if(tmpItem.toString().equals(match[index]))
								flag[index] = true;
						}
					}
					else {
						tmpItem.append(ch);
					}

					break;

				case ITEMS_COMMA_STATE:
					ch = line.charAt(i);
					
					if(Character.isLetterOrDigit(ch)){
						tmpItem.append(ch);
						state = States.ITEMS_STATE;
					}

					break;
			}
		}

		for(int i = 0; i < flag.length; i++){
			if(flag[i] == false)
				return false;
		}

		return true;
	}

	public static void getCountTrim(ArrayList<String> list){
		
		ArrayList<String> temp = new ArrayList<String>();

		String cur = null, next = null;

		int cnt = 1;

		for(int i = 0; i < list.size(); i++){
			
			cur = list.get(i);
				
			if(i+1 < list.size())
				next = list.get(i+1);
			else 
				next = null;
			
			if(!cur.equals(next)){
				count.add(cnt);
				temp.add(cur);
				cnt = 1;
			}
			else{
				cnt++;
			}
		}

		items = temp;
	}

	public static void checkSupport(){

		int size = items.size();

		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<Integer> tempCount = new ArrayList<Integer>();

		int numberOfTransactions = transactions.size();

		for(int i = 0; i < size; i++){

			double tempSupport = (double)(count.get(i))/numberOfTransactions;

			if(tempSupport >= support){
				temp.add(items.get(i));
				tempCount.add(count.get(i));
			}
		}

		items = temp;
		count = tempCount;
	}

	public static void secondSupport(double support){

		int numberOfTransactions = transactions.size();

		ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
		int[] tempCount;

		for(int i = 0; i < secCount.length; i++){

			if(secCount[i] != 0){
				double tempSupport = (double) secCount[i]/numberOfTransactions;

				if(tempSupport >= support){
					temp.add(secondList.get(i));
					resultSet.add(secondList.get(i));
					resultCount.add(secCount[i]);
					resultSupport.add(tempSupport);
				} else{
					eliminatedSet.add(secondList.get(i));
				}
			}
		}

		tempCount = new int[temp.size()];
		int j = 0;

		for(int i = 0; i < secCount.length; i++){

			if(secCount[i] != 0){

				double tempSupport = (double) secCount[i]/numberOfTransactions;

				if(tempSupport >= support){
					tempCount[j++] = secCount[i];
				}
			}
		}

		secondList = temp;
		secCount = tempCount;
	}

	public static void getConfidence(){

		int size = resultSet.size();

		ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
		//ArrayList<Integer> tempCount = new ArrayList<Integer>();
		ArrayList<Double> tmpSupport = new ArrayList<Double>();
		ArrayList<Double> tmpConfidence = new ArrayList<Double>();

		//System.out.println("The size is " + size);
		for(int i = 0; i < size; i++){

			double tempConfidence = (double)(resultCount.get(i))/(countLeft[i]);

			//System.out.println("The temp confidence " + tempConfidence);
			if(tempConfidence >= confidence){
				temp.add(resultSet.get(i));
				//tempCount.add(count.get(i));
				tmpSupport.add(resultSupport.get(i));
				tmpConfidence.add(tempConfidence);
			}
		}

		resultSet = temp;
		//resultCount = tempCount;
	 	resultSupport = resultSupport;
		resultConfidence = tmpConfidence;
	}


	public static boolean checkIfEliminated(ArrayList<String> s){

		//boolean flags[] = new boolean[s.size()];
		//int i = 0;
		for(ArrayList<String> s1 : eliminatedSet){
			if(s.containsAll(s1))
				return true;
		}

		return false;
	}

	/** Start of Processing Subsets **/

	public static void processSubsets(String[] set, int k) {
		secondList = new ArrayList<ArrayList<String>>();
	    String[] subset = new String[k];
	    processLargerSubsets(set, subset, 0, 0);
	}

	public static void processLargerSubsets(String[] set, String[] subset, int subsetSize, int nextIndex) {
        if (subsetSize == subset.length) {
            process(subset);
        } else {
            for (int j = nextIndex; j < set.length; j++) {
                subset[subsetSize] = set[j];
                processLargerSubsets(set, subset, subsetSize + 1, j + 1);
            }
        }
    }

    public static void process(String[] subset) {
 
        for(int i = 0; i < subset.length; i++){
            list.add(subset[i]);
        }
        secondList.add(list);
        list = new ArrayList<String>();
    }

	/** End of Processing Subsets **/


	public static void main(String[] args)
	{
		try{

			transactions = new ArrayList<String>();
			items = new ArrayList<String>();
			count = new ArrayList<Integer>();
			list = new ArrayList<String>();
			secondList = new ArrayList<ArrayList<String>>();
			eliminatedSet = new ArrayList<ArrayList<String>>();
			resultSet  = new ArrayList<ArrayList<String>>();
			resultCount  = new ArrayList<Integer>();
			resultSupport = new ArrayList<Double>();

			if(args.length == 0){
				System.out.println("Error: Need to have one argument for the file name");
				System.exit(0);
			}else if(args.length > 1){
				System.out.println("Error: More arguments than required. Only need one argument");
				System.exit(0);
			}

			File f = new File(args[0]);

			BufferedReader br = new BufferedReader(new FileReader(f));

			Scanner scan = new Scanner(System.in);

			System.out.print("Enter the support needed for this database: ");
			support = scan.nextDouble();

			System.out.print("Enter the confidence needed for this database: ");
			confidence = scan.nextDouble();
			
			String line = null;
			int lines = 0;
			while ( (line = br.readLine()) != null){
				parseLine(line);
				lines++;
			}
		
			Collections.sort(items);
			getCountTrim(items);


			System.out.println("---------------------------------");
			ListStringIntegerPrint(items, count);
			System.out.println("---------------------------------");
			System.out.println("After checking support");
			checkSupport();
			System.out.println("---------------------------------");
			ListStringIntegerPrint(items, count);
			System.out.println("---------------------------------");
			

			// Transforming the items array into Object
			// Then copying that into String array
			// So the process Subsets will work properly

			int kth = 2;

			while(true){

				Object[] objArray = items.toArray();
				String[] temp = Arrays.asList(objArray).toArray(new String[objArray.length]);

				processSubsets(temp, kth);

				br = new BufferedReader(new FileReader(f));

				secCount = new int[secondList.size()];

				for(int i = 0; i < secCount.length; i++){
					secCount[i] = 0;
				}

				// This loop will get me all the subset of the required size

				while( (line = br.readLine()) != null){
					
					int i = 0;

					for(ArrayList<String> s: secondList){
						
						objArray = s.toArray();
						temp = Arrays.asList(objArray).toArray(new String[objArray.length]);
						
						boolean flag = parse(line, temp);
						
						if(flag == true)
							secCount[i] = secCount[i] + 1;
						
						i++;
					}
				}


				ArrayList<ArrayList<String>> myTemp = new ArrayList<ArrayList<String>>();
				int[] tmpCnt = new int[secCount.length];

				if(eliminatedSet.size() > 0){
				
					int i = 0;
					int j = 0;
					for(ArrayList<String> s : secondList){
						
						boolean flag = checkIfEliminated(s);

						if(!flag){
							myTemp.add(s);
							tmpCnt[j++] = secCount[i];
						}
						else
							eliminatedSet.add(s);
						i++;
					}
					
					secondList = myTemp;
					secCount = tmpCnt;
				}

				if(secondList.size() == 0)
					break;
				
				
				System.out.println(kth + " subset");
				System.out.println("---------------------------------");
				ListStringIntegerArrPrint(secondList, secCount);
				System.out.println("---------------------------------");
				System.out.println("After checking support");
				secondSupport(support);
				System.out.println("---------------------------------");
				ListStringIntegerArrPrint(secondList, secCount);
				System.out.println("---------------------------------");
				
				kth++;
			}

			System.out.println("Result Set:\tNumbOfTransactionsXY\tSupport\t\tNumbOfTransactionsX");
			System.out.println("---------------------------------");
			String[][][] set = new String[resultSet.size()][2][];

			int i = 0;

			for(ArrayList<String> s: resultSet){

				set[i][0] = new String[s.size()-1];
				set[i][1] = new String[1];

				for(int j = 0; j < s.size(); j++){
					if(j != (s.size() - 1))
						set[i][0][j] = s.get(j);
					else
						set[i][1][0] = s.get(j);
				}
				i++;
			}

			// X -> Y
			// I am getting all the values of X

			br = new BufferedReader(new FileReader(f));

			countLeft = new int[set.length];

			while((line = br.readLine()) != null){

				for(int index = 0; index < set.length; index++){
					boolean flag = parse(line, set[index][0]);

					if(flag == true){
						countLeft[index] = countLeft[index]+1;
					}
				}
			}

			for(i = 0; i < resultSet.size(); i++){
				System.out.println(resultSet.get(i) + "\t" + resultCount.get(i) + "\t" + resultSupport.get(i) + " " + countLeft[i]);
			}

			getConfidence();
			System.out.println("---------------------------------");
			System.out.println("X -> Y");
			System.out.println("Final Results:\tSupport\tConfidence");
			System.out.println("---------------------------------");

			i=0;

			for(ArrayList<String> s1 : resultSet){

				for(int j = 0; j < s1.size(); j++){
					if(j == (s1.size() - 1))
						System.out.print("  => " + s1.get(j));
					else
						System.out.print(s1.get(j) + " ");
				}
				System.out.println("\t\t" + resultSupport.get(i) + "\t" + resultConfidence.get(i));
			}
			System.out.println("---------------------------------");
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void ListStringIntegerPrint(ArrayList<String> list, ArrayList<Integer> count){

		int size = list.size();

		System.out.println("Itemset\tsup");
		System.out.println("---------------------------------");
		for(int i = 0; i < size; i++){
			System.out.print(list.get(i) + "\t");
			System.out.println(count.get(i));
		}
	}

	public static void ListStringIntegerArrPrint(ArrayList<ArrayList<String>> list, int[] count){

		int size = list.size();

		System.out.println("Itemset\t\tsup");
		System.out.println("---------------------------------");
		for(int i = 0; i < size; i++){
			System.out.print(list.get(i) + "\t");
			System.out.println(count[i]);
		}
	}

}
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class GenerateCoding {

	static final int SORTING_MAX = 10;
	
	static final String defaultProbabilitiesFile = System.getProperty("user.home") + "/output/probs.txt";
	static final String defaultOutputFile = System.getProperty("user.home") + "/output/codings.txt";
	
	public static void main(String[] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		
		long startTime = System.currentTimeMillis();
		
		System.out.println("Enter the probabilities file > ");
		String probabilitesFileName = sc.nextLine();
		
		if(probabilitesFileName.isEmpty()){
			probabilitesFileName = defaultProbabilitiesFile;
		}
		
		File probsFile = new File(probabilitesFileName);
		if(!probsFile.exists()){
			System.out.println("THAT FILE DOEST EXIST");
			sc.close();
			return;
		}
		
		System.out.println("Enter the output file > ");
		String outputFileName = sc.nextLine();
		sc.close();
		
		if(outputFileName.isEmpty()){
			outputFileName = defaultOutputFile;
		}
		
		File outputFile = new File(outputFileName);
		outputFile.createNewFile();
		
		BufferedReader br = new BufferedReader(new FileReader(probsFile));
		HashMap<String, Double> probs = new HashMap<>();
		
		System.out.println("Reading Probabilities...");
		String line= br.readLine();
		int ngramSize = line.split(" ~:~ ")[0].length();
		while(line != null){
			String[] split = line.split(" ~:~ ");
			probs.put(split[0],Double.parseDouble(split[1]));
			line = br.readLine();
		}
		
		br.close();
		
		long initialSize = probs.entrySet().size();
		int milestone = 0;
		long foo = 0;
		System.out.println();
		System.out.println();
		System.out.println("Constructing List of Symbols...");
		System.out.println("--------------------------------------------------");
		
		PriorityQueue<Node> nodes =  new PriorityQueue<>(probs.entrySet().size(), new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return o1.probability.compareTo(o2.probability);
			}
		});
		
		for(Entry<String, Double> entry : probs.entrySet()){
			foo++;
			if(100.0*foo/initialSize > milestone){
				milestone += 2;
				System.out.print("*");
			}
			nodes.add(new Node(entry.getKey(), entry.getValue()));
		}
		
		System.out.println();
		System.out.println();
		System.out.println("Constructing tree...");
		initialSize = nodes.size();
		milestone = 100;
		System.out.println("--------------------------------------------------");
		while(nodes.size() > 1){
			if(100.0 * nodes.size()/initialSize < milestone){
				System.out.print("*");
				milestone -= 2;
			}
			
			//combine the 2 lowest value nodes
			Node node1 = nodes.poll();
			Node node2 = nodes.poll();
			Node c = new Node(node1,node2);
			//add the new node back to the list
			nodes.add(c);
		}
		
		System.out.println();
		System.out.println();
		System.out.println("Determining codes...");
		milestone = 0;
		System.out.println("--------------------------------------------------");
		
		
		//at this point, the list will only contain one node: the root of the tree 
		Node root =  nodes.poll();
		root.bitString = "";
		
		ArrayList<Node> huffmanCodesList = new ArrayList<>();
		HashMap<String, String> huffmanCodesMap = new HashMap<>();
		
		Stack<Node> stack = new Stack<>();
		stack.push(root);
		
		while(!stack.isEmpty()){
			
			if(100.0*huffmanCodesList.size()/initialSize > milestone){
				System.out.print("*");
				milestone += 2;
			}
			
			Node current = stack.pop();
			
			if(current.orig){
				huffmanCodesList.add(current);
				huffmanCodesMap.put(current.symbol, current.bitString);
			} else{
				current.left.bitString = current.bitString.concat("0");
				current.right.bitString = current.bitString.concat("1");
				stack.push(current.right);
				stack.push(current.left);
				
			}
		}
		
		System.out.println();
		System.out.println();
		
		if(ngramSize <= SORTING_MAX){
			System.out.println("Sorting...");
			
			Collections.sort(huffmanCodesList, new Comparator<Node>() {

				@Override
				public int compare(Node o1, Node o2) {
					return o1.bitString.length() - o2.bitString.length();
				}
			});
			
			
			System.out.println();
			System.out.println();
		}
		
		System.out.println("Writing to output file...");
		milestone = 0;
		foo = 0;
		System.out.println("--------------------------------------------------");
		
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

		for(Node n :  huffmanCodesList){
			foo++;
			if(100.0 *foo/huffmanCodesList.size() > milestone){
				System.out.print("*");
				milestone += 2;
			}
			String s = n.symbol + " ~:~ " + n.bitString + "\n";
			bw.write(s);
		}
		
		System.out.println();
		System.out.println();
		
		bw.flush();
		bw.close();
		sc.close();
		
		
		long endTime = System.currentTimeMillis();
		System.out.println("Time elapsed: " + (endTime-startTime)/1000.0 + " seconds");
		
	}
	
	
	private static class Node{
		Node left;
		Node right;
		String symbol;
		String bitString;
		boolean orig;
		Double probability;
		
		public Node(Node l, Node r){
			this.left = l;
			this.right = r;
			probability = l.probability + r.probability;
			orig = false;
		}
		public Node(String c, Double v){
			this.probability = v;
			this.symbol = c;
			orig = true;
		}
	}
}

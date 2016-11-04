import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
	
	public static void main(String[] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		
		long startTime = System.currentTimeMillis();
		
		System.out.println("Enter the directory to search > ");
		String searchDir = sc.nextLine();
		File inputDir = new File(searchDir);
		if(!inputDir.exists() && ! inputDir.isDirectory()){
			System.out.println("THAT ISNT A DIRECTORY");
			sc.close();
			return;
		}
		
		
		ArrayList<File> textFiles = new ArrayList<>();
		
		for(String f : inputDir.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith("proc.txt");
					}})
		){
			textFiles.add(new File(inputDir, f));
			
		}
		
		
		HashMap<String, Long> count = new HashMap<>();
		int total = 0;
		
		System.out.println("Enter the n-gram size > ");
		Integer ngramSize = Integer.parseInt(sc.nextLine());
		
		
		System.out.println("Enter the path of the output file > ");
		File outputFile = new File(sc.nextLine());
		outputFile.createNewFile();
		
		
		
		for(File textFile : textFiles){
			System.out.println("Reading " + textFile.getName());
			
			BufferedReader br = new BufferedReader(new FileReader(textFile));
			String line = br.readLine();
			for(int i = 0; i<line.length() - ngramSize+1; i++){
				String gram = line.substring(i, i+ngramSize);
				long prev = count.getOrDefault(gram, 0l);
				prev++;
				count.put(gram, prev);
				total++;
			}
			
			br.close();
		}
		
		System.out.println();
		System.out.println("Calculating Probabilities...");
		
		double initialSize = count.entrySet().size();
		int milestone = 0;
		int foo = 0;
		System.out.println("--------------------------------------------------");
	
		HashMap<String,Double> probs = new HashMap<String, Double>();
		for(Entry<String, Long> entry : count.entrySet()){
			foo++;
			if(100.0*foo/initialSize > milestone){
				milestone += 2;
				System.out.print("*");
			}
			probs.put(entry.getKey(), entry.getValue().doubleValue()/total);
		}
		
		System.out.println();
		System.out.println();
		
		
		initialSize = count.entrySet().size();
		milestone = 0;
		foo = 0;
		System.out.println("Constructing List of Symbols...");
		System.out.println("--------------------------------------------------");
		
		PriorityQueue<Node> nodes =  new PriorityQueue<>(count.entrySet().size(), new Comparator<Node>() {
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
			String s = n.symbol + "~:~" + n.bitString + "\n";
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

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

public class GenerateCoding {

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
		for(File textFile : textFiles){
			System.out.println("Reading " + textFile.getName());
			
			BufferedReader br = new BufferedReader(new FileReader(textFile));
			String line = br.readLine();
			for(int i = 0; i<line.length() - ngramSize; i++){
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
		System.out.println("Calculating Huffman Codes...");
		System.out.println("--------------------------------------------------");
		
		PriorityQueue<Node> nodes =  new PriorityQueue<>(count.entrySet().size(), new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return o1.v.compareTo(o2.v);
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
		
		//at this point, the list will only contain one node: the root of the tree 
		Node root =  nodes.poll();
		ArrayList<Result> pairs = new ArrayList<Result>();
		
		milestone = 0;
		System.out.println("--------------------------------------------------");
		
		//this will go through the tree, deleting nodes one at a time, and returning a 'Result' which
		//contains the huffman code of any leaf node when it is deleted
		while(!root.deleted ){
			Result res = getNode(root, "");
			if(res != null){
				//it was a leaf node, so we should track its huffman code
				pairs.add(res);
				if(100.0*pairs.size()/initialSize > milestone){
					System.out.print("*");
					milestone += 2;
				}
			}
		}
		
		System.out.println();
		System.out.println();
		System.out.println("Writing to output file...");
		//sort the results for readability (shortest code first)
		Collections.sort(pairs, new Comparator<Result>() {

			@Override
			public int compare(Result o1, Result o2) {
				return o1.bitString.length() - o2.bitString.length();
			}
		});
//		
//		PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));
//		for(Result p :  pairs){
//			String s = p.symbol + "~:~" + p.bitString;
//			pw.println(s);
//		}
//		pw.flush();

		sc.close();
		
		
		long endTime = System.currentTimeMillis();
		System.out.println("Time elapsed: " + (endTime-startTime)/1000.0 + " seconds");
		
	}
	
	
	private static Result getNode(Node n, String s){
		if(n.l != null && !n.l.deleted){
			s = s.concat("0");
			return getNode(n.l, s);
		} if(n.r != null && !n.r.deleted){
			s = s.concat("1");
			return getNode(n.r, s);
		}
		if(n.orig){
			n.deleted = true;
			return new Result(n.c, s);
		}
		
		n.deleted = true;
		return null;
	}
	
	private static class Result{
		public String symbol;
		public String bitString;
		public Result(String symbol, String bitString){
			this.bitString = bitString;
			this.symbol = symbol;
		}
	}
	
	private static class Node{
		Node l;
		Node r;
		String c;
		boolean orig;
		boolean deleted = false;
		Double v;
		public Node(Node l, Node r){
			this.l = l;
			this.r = r;
			v = l.v + r.v;
			orig = false;
		}
		public Node(String c, Double v){
			this.v = v;
			this.c = c;
			orig = true;
		}
	}
}

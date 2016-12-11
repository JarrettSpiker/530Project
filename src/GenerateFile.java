import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

/*
* Create a file with specified character length based on input probabilities
*/
public class GenerateFile {

	public static final String defaultProbsDir = System.getProperty("user.home") + "/genOutput";
	public static final String defaultOuptutFile = System.getProperty("user.home") + "/genOutput/sampleFile.txt";
	
	public static void main(String[] args) throws Exception {
		//get ouput file location and name
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the name of the probabilities directory > ");
		String probFilesName = sc.nextLine();
		if(probFilesName.isEmpty()){
			probFilesName = defaultProbsDir;
		}
		File probsDir = new File(probFilesName);
		if(!probsDir.exists() || !probsDir.isDirectory()){
			System.out.println("THIS IS NOT A Directory");
			sc.close();
			return;
		}
		
		System.out.println("Enter the name of the output file > ");
		String outputFileName = sc.nextLine();
		if(outputFileName.isEmpty()){
			outputFileName = defaultOuptutFile;
		}
		File outputFile = new File(outputFileName);
		outputFile.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		
		//get the number of characters for the new file
		System.out.println("Enter the number of characters in the generated file > ");
		long fileSize = Long.parseLong(sc.nextLine());
		
		//max n-gram size used to generate the file
		System.out.println("Enter the maximum n-gram size > ");
		int maxNgramSize = Integer.parseInt(sc.nextLine());
		//close system input
		sc.close();
		
		Random random = new Random();
		
		//Determining the first letter
		System.out.println("Determining the first prefix...");
		System.out.println("--------------------------------------------------");
		String firstPrefix = "";
		long target = maxNgramSize;
		long current = 0;
		int milestone = 0;
		
		
		for(int i = 1; i< maxNgramSize; i++ ){
			current++;
			//for printing out progress bar
			if(100.0*current/target > milestone){
				milestone += 2;
				System.out.print("*****");
			}
			
			//read probabilities from file and based on the probability of the letter choose the next letter
			File iFile = new File(probsDir, "probs" + i +".txt");
			BufferedReader iBr = new BufferedReader(new FileReader(iFile));
			ArrayList<Pair> choices = new ArrayList<>();
			String line = null;
			double total = 0.0;
			while((line = iBr.readLine())!= null){
				String[] split = line.split(" ~:~ ");
				if(split[0].startsWith(firstPrefix)){
					choices.add(new Pair(split[0], Double.parseDouble(split[1])));
					total+=Double.parseDouble(split[1]);
				}
			}
			for(Pair p : choices){
				p.prob = p.prob/total;
			}
			double r = random.nextDouble();
			double count = choices.get(0).prob;
			int j = 1;
			while(count<r){
				count+=choices.get(j).prob;
				j++;
			}
			firstPrefix = choices.get(j-1).s;
			iBr.close();
			
		}
		
		System.out.println();
		System.out.println();
		System.out.println("Reading the probabilities of max n-grams...");
		Map<String, Double> probs = new HashMap<String, Double>();
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(new File(probsDir, "probs" + maxNgramSize + ".txt")));
		while((line = br.readLine()) != null){
			String[] split = line.split(" ~:~ ");
			probs.put(split[0], Double.parseDouble(split[1]));
		}
		br.close();
		
		
		//probabilities of the first x letter 
		System.out.println();
		System.out.println("Constructing prefix tree.....");
		System.out.println("--------------------------------------------------");
		target = probs.size();
		current = 0;
		milestone = 0;
		
		Map<String, ArrayList<TreeItem>> prefixTree = new HashMap<>();
		if(maxNgramSize > 1){
			for(Entry<String, Double> e : probs.entrySet()){
				current++;
				if(100.0*current/target > milestone){
					milestone += 2;
					System.out.print("*");
				}
				String prefix = e.getKey().substring(0, maxNgramSize-1);
				char c = e.getKey().charAt(maxNgramSize-1);
				if(!prefixTree.containsKey(prefix)){
					prefixTree.put(prefix, new ArrayList<>());
				} 
				prefixTree.get(prefix).add(new TreeItem(e.getValue(), c));
			}
		} else{
			ArrayList<TreeItem> emptyPrefixList = new ArrayList<>();
			for(Entry<String, Double> e : probs.entrySet()){
				current++;
				if(100.0*current/target > milestone){
					milestone += 2;
					System.out.print("*");
				}
				emptyPrefixList.add(new TreeItem(e.getValue(), e.getKey().charAt(0)));
			}
			prefixTree.put("", emptyPrefixList);
		}
		System.out.println();
		System.out.println();
		
		//probabilities of following the frist x letter
		System.out.println("Calculating intermediate probabilities...");
		System.out.println("--------------------------------------------------");
		milestone = 0;
		current = 0;
		target = prefixTree.size();
		for(Entry<String, ArrayList<TreeItem>> e : prefixTree.entrySet()){
			current++;
			if(100.0*current/target > milestone){
				milestone += 2;
				System.out.print("*");
			}
			
			ArrayList<TreeItem> prefixList = e.getValue();
			double total = 0.0;
			for(TreeItem item : prefixList){
				total += item.prob;
			}
			for(TreeItem item : prefixList){
				item.prob = item.prob/total;
			}
			double temp = 0.0;
			for(TreeItem item : prefixList){
				item.prob = item.prob + temp;
				temp = item.prob;
			}
		}
		
		System.out.println();
		System.out.println();
		
		
		System.out.println("Writing the output file...");
		System.out.println("--------------------------------------------------");
		milestone = 0;
		
		//create file based on the probabilities
		bw.write(firstPrefix);
		long currentLength = firstPrefix.length();
		String currentPrefix = firstPrefix;
		while(currentLength < fileSize){
			if(100.0*currentLength/fileSize > milestone){
				milestone += 2;
				System.out.print("*");
			}
			ArrayList<TreeItem> options = prefixTree.get(currentPrefix);
			double r = random.nextDouble();
			int j = 1;
			double count = options.get(0).prob;
			while(count<r){
				count+=options.get(j).prob;
				j++;
			}
			char c = options.get(j-1).c;
			bw.write(c);
			currentLength++;
			if(currentLength %50 == 0){
				//bw.newLine();
				bw.flush();
			}
			if(maxNgramSize > 1){
				currentPrefix = currentPrefix.substring(1) + c;
			}
		}
		bw.flush();
		
		bw.close();
	}

	private static class Pair{
		double prob;
		final String s;
		public Pair(String s, double prob){
			this.prob = prob;
			this.s = s;
		}
		
		public String toString(){
			return s + " : " + prob;
		}
	}
	
	private static class TreeItem{
		double prob;
		final char c;
		public TreeItem(double prob, char c){
			this.prob = prob;
			this.c = c;
		}
		public String toString(){
			return c + " : " + prob;
		}
	}
}

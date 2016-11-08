import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;




public class CreateDist {	


	static final String defaultOutputDir = System.getProperty("user.home") + "/output/generated/";
	
	public static void main(String[] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Enter the output directory name: ");
		String outputFile = sc.nextLine();
		
		if(outputFile.isEmpty()){
			outputFile = defaultOutputDir;
		}
		
		
		System.out.println("Enter the ngram range: ");
		int nGramRange = Integer.parseInt(sc.nextLine());
		
		System.out.println("Enter the factor: ");
		double factor = Double.parseDouble(sc.nextLine());
		
		sc.close();
		
		ArrayList<String> alphabet = new ArrayList<>();
		alphabet.add("a");
		alphabet.add("b");
		alphabet.add("c");
		alphabet.add("d");
//		alphabet.add("e");
//		alphabet.add("f"); 
//		alphabet.add("g");
		
		
		double quotient = 0;
		
		HashMap<String, Double> weights = new HashMap<>();
		
		ArrayList<Double> initialWeights = new ArrayList<>();
		for(int i =0; i<alphabet.size(); i++){
			Double w = 1.0/Math.pow(i+1, factor);
			initialWeights.add(w);
			quotient += w;
		}
		
		for(int i = 0; i< alphabet.size(); i++){
			weights.put(alphabet.get(i), quotient/initialWeights.get(i));
		} 
		
		
		ArrayList<String> previousSet = new ArrayList<>();
		previousSet.add("");
		weights.put("", 0.0);
		
		for(int size = 1; size<= nGramRange; size++){
			System.out.println("On " + size + "-grams");
			ArrayList<String> newSet = new ArrayList<>();
			double totalWeight = 0;
			for(String previous : previousSet){
				for(String ch : alphabet){
					if(previous.endsWith(ch)){
						continue;
					}
					//System.out.println("On: "+ previous +  ch);
					double weight = weights.get(ch) + weights.get(previous);
					double weightOfCh =  weights.get(ch);
					for(int i = 0; i<previous.length(); i++){
						weight += (weights.get(String.valueOf(previous.charAt(i))) - weightOfCh)/Math.pow(2, i+1);
					}
					if(weight <= 0){
						continue;
					}
					totalWeight += weight;
					weights.put(previous + ch, weight);
					newSet.add(previous + ch);
				}
			}
			
			File f = new File(outputFile + "/probs" + size +".txt");
			f.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for(String s : newSet){
				bw.write(s + ":" + weights.get(s)/totalWeight);
				bw.write('\n');
			}
			bw.flush();
			bw.close();
			
			previousSet = newSet;
			
		}
		
		
		
		System.out.println("Done");
		
		
	}
}

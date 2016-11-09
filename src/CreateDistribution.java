import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CreateDistribution {	
	public static void main(String[] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the output directory name: ");
		String outputFile = sc.nextLine();
		System.out.println("Enter the alphabet size: ");
		int alphSize = Integer.parseInt(sc.nextLine());
		System.out.println("Enter the ngram range: ");
		int nGramRange = Integer.parseInt(sc.nextLine());
		System.out.println("Enter the factor: ");
		double factor = Double.parseDouble(sc.nextLine());
		
		
		sc.close();
		ArrayList<String> alphabet = new ArrayList<>();
		for(int i = 97; i<97+alphSize; i++){
			alphabet.add(String.valueOf((char)i));
		}
		
		
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
			ArrayList<String> newSet = new ArrayList<>();
			
			double totalWeight = 0;
			
			if(size != 1){
				System.out.println("On " + size + "-grams");
				for(String previous : previousSet){
					int index = alphabet.indexOf(String.valueOf(previous.charAt(previous.length()-1)));
					boolean goUp = index<alphabet.size()/2;
					for(int i = index; goUp ? i < alphabet.size() : i >= 0 ; i = goUp ? i+1 : i-1){
						if(i == index){
							continue;
						}
						String ch = alphabet.get(i);
						double weight = weights.get(ch) + weights.get(previous);
						double weightOfCh =  weights.get(ch);
						for(int j = 0; j<previous.length(); j++){
							weight += (weights.get(String.valueOf(previous.charAt(j))) - weightOfCh)/Math.pow(2, j+1);
						}
						totalWeight += weight;
						weights.put(previous + ch, weight);
						newSet.add(previous + ch);
					}
				}
			}
			else{
				for(String ch : alphabet){
					totalWeight += weights.get(ch);
					newSet.add(ch);
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

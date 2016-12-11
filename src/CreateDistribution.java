import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/*
* Generate a language with a set alphabet size and factor,
* create for this language a set of n-gram based on the 
* probability of the language 
*/
public class CreateDistribution {	
	
	public static final String defaultOutputDir = System.getProperty("user.home") + "/genOutput";
	
	public static void main(String[] args) throws Exception{
		//get output directory
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the output directory name: ");
		String outputFile = sc.nextLine();
		if(outputFile.isEmpty()){
			outputFile = defaultOutputDir;
		}
		
		//get alphabet size, n-gram range from 1 - value intered and the entropy of the distribution
		System.out.println("Enter the alphabet size: ");
		int alphSize = Integer.parseInt(sc.nextLine());
		System.out.println("Enter the ngram range: ");
		int nGramRange = Integer.parseInt(sc.nextLine());
		System.out.println("Enter the factor: ");
		double factor = Double.parseDouble(sc.nextLine());
		
		//close system input
		sc.close();
		
		//the alphabet will be a to z based on the number of letters that is inputted
		ArrayList<String> alphabet = new ArrayList<>();
		for(int i = 97; i<97+alphSize; i++){
			alphabet.add(String.valueOf((char)i));
		}
		
		//total weight in a way
		double quotient = 0;
		
		//mapping of character/word and its weight
		HashMap<String, Double> weights = new HashMap<>();
		
		//inital weight of single letters
		ArrayList<Double> initialWeights = new ArrayList<>();
		for(int i =0; i<alphabet.size(); i++){
			Double w = 1.0/Math.pow(i+1, factor);
			initialWeights.add(w);
			quotient += w;
		}
		
		//input the final weight, so total of weight is 1
		for(int i = 0; i< alphabet.size(); i++){
			weights.put(alphabet.get(i), quotient/initialWeights.get(i));
		} 
		
		//
		ArrayList<String> previousSet = new ArrayList<>();
		previousSet.add("");
		weights.put("", 0.0);
		
		//generate weights for all n-gram range
		for(int size = 1; size<= nGramRange; size++){
			ArrayList<String> newSet = new ArrayList<>();
			
			double totalWeight = 0;
			
			//generate weight of nth-gram based on n-gram - 1
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
						double weight = weights.get(ch) * weights.get(previous);
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
			
			
			//wright out to file
			File f = new File(outputFile + "/probs" + size +".txt");
			f.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for(String s : newSet){
				bw.write(s + " ~:~ " + weights.get(s)/totalWeight);
				bw.write('\n');
			}
			bw.flush();
			bw.close();
			
			previousSet = newSet;
			
		}
		
		System.out.println("Done");
		
	}
}

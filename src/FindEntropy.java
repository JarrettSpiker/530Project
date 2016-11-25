import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class FindEntropy {

	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Enter the name of the probabilities file> ");
		String inputFile1Name = sc.nextLine();
		File inputFile1 = new File(inputFile1Name);
		if(!inputFile1.exists()){
			sc.close();
			System.out.println("THIS FILE DOESNT EXIST");
			return;
		}
		sc.close();
		
		HashMap<String, Double> probs = new HashMap<>();
		
		BufferedReader br = new BufferedReader(new FileReader(inputFile1));
		String line;
		while((line = br.readLine()) != null){
			String[] split = line.split(" ~:~ ");
			probs.put(split[0], Double.parseDouble(split[1]));
		}
		br.close();
		
		
		double entropy = 0.0;
		for(Entry<String,Double> e :  probs.entrySet()){
			entropy -= e.getValue() * (Math.log(e.getValue())/Math.log(2));
		}
		System.out.println(entropy);
	}

}

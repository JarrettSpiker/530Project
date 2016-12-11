import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
/*
* calculate the statical distance between two probabilities
*/
public class FindStatisticalDistance {

	public static void main(String[] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		
		//get first probabilities file
		System.out.println("Enter the name of the first probabilities > ");
		String inputFile1Name = sc.nextLine();
		File inputFile1 = new File(inputFile1Name);
		if(!inputFile1.exists()){
			sc.close();
			System.out.println("THIS FILE DOESNT EXIST");
			return;
		}
		
		//get second probabilities file
		System.out.println("Enter the name of the second probabilities > ");
		String inputFile2Name = sc.nextLine();
		File inputFile2 = new File(inputFile2Name);
		if(!inputFile2.exists()){
			sc.close();
			System.out.println("THIS FILE DOESNT EXIST");
			return;
		}
		//close system input
		sc.close();
		
		
		HashMap<String, Double> probsP = new HashMap<>();
		HashMap<String, Double> probsQ = new HashMap<>();
		
		System.out.println();
		System.out.println("Reading the first probabilities");
		System.out.println();
		
		//read probabilities from first file
		BufferedReader br = new BufferedReader(new FileReader(inputFile1));
		String line;
		while((line = br.readLine()) != null){
			String[] split = line.split(" ~:~ ");
			probsP.put(split[0], Double.parseDouble(split[1]));
		}
		//close first file
		br.close();
		
		System.out.println("Reading the second probabilities");
		System.out.println();
		
		//read probabilities from second file
		br = new BufferedReader(new FileReader(inputFile2));
		while((line = br.readLine()) != null){
			String[] split = line.split(" ~:~ ");
			probsQ.put(split[0], Double.parseDouble(split[1]));
		}
		//close second file
		br.close();
		
		
		//This uses the Jensen-Shannon distribution
		
		System.out.println();
		System.out.println("Calculating M = (P+Q)/2");
		long initialSize = probsP.size() + probsQ.size();
		int milestone = 0;
		long foo = 0;
		
		HashMap<String, Double> probsM = new HashMap<>();
		
		//print out first half of progress bar
		System.out.println("--------------------------------------------------");
		for(Entry<String, Double> p : probsP.entrySet()){
			foo++;
			if(100.0*foo/initialSize > milestone){
				milestone += 2;
				System.out.print("*");
			}
			probsM.put(p.getKey(), (probsQ.getOrDefault(p.getKey(), 0.0) + p.getValue())/2);
		}
		
		//print the second half of progress bar
		for(Entry<String, Double> q : probsQ.entrySet()){
			foo++;
			if(100.0*foo/initialSize > milestone){
				milestone += 2;
				System.out.print("*");
			}
			if(!probsM.containsKey(q.getKey())){
				probsM.put(q.getKey(), (probsP.getOrDefault(q.getKey(), 0.0) + q.getValue())/2);
			}
		}
		
		System.out.println();
		System.out.println();
		double dPM = 0.0;
		
		System.out.println("Calculating D(P||M)");
		initialSize = probsM.size();
		milestone = 0;
		foo = 0;
		
		System.out.println("--------------------------------------------------");
		for(Entry<String, Double> m : probsM.entrySet()){
			foo++;
			if(100.0*foo/initialSize > milestone){
				milestone += 2;
				System.out.print("*");
			}
			
			dPM +=logDistance(probsP.getOrDefault(m.getKey(), 0.0), m.getValue());
		}
		
		System.out.println();
		System.out.println();
		double dQM = 0.0;
		
		System.out.println("Calculating D(Q||M)");
		initialSize = probsM.size();
		milestone = 0;
		foo = 0;
		
		System.out.println("--------------------------------------------------");
		for(Entry<String, Double> m : probsM.entrySet()){
			foo++;
			if(100.0*foo/initialSize > milestone){
				milestone += 2;
				System.out.print("*");
			}
			
			dQM +=logDistance(probsQ.getOrDefault(m.getKey(), 0.0), m.getValue());
		}
		
		System.out.println();
		System.out.println();
		System.out.println("Calculating JSD(P||Q)");
		double jsd = (0.5 * dPM) + (0.5 * dQM);
		
		
		System.out.println(jsd);
		
	}
	
	//calculate the distance between two inputs
	private static double logDistance(double p, double q){
		if(p==0){
			return 0;
		}
		double a = Math.log(p) / Math.log(2);
		double b = Math.log(q) / Math.log(2);
		return p * (a/b);
	}
	
}

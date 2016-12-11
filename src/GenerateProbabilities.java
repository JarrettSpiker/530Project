import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;

/*
* create probabilties based on english text files,
* takes a directory of text files, output a single probabilties
* file based on the contents of the file for the n-gram size the
* user inputed
*/
public class GenerateProbabilities {
	static final String defaultSearchDir = System.getProperty("user.home") + "/books/";
	static final String defaultOutputFile = System.getProperty("user.home") + "/output/";
	
	public static void main(String[] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		
		long startTime = System.currentTimeMillis();
		
		//get directory holding the text files
		System.out.println("Enter the directory to search > ");
		String searchDir = sc.nextLine();
		
		if(searchDir.isEmpty()){
			searchDir = defaultSearchDir;
		}
		
		
		File inputDir = new File(searchDir);
		if(!inputDir.exists() && ! inputDir.isDirectory()){
			System.out.println("THAT ISNT A DIRECTORY");
			sc.close();
			return;
		}
		
		
		ArrayList<File> textFiles = new ArrayList<>();
		//find all text files in this directory
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
		//get n-gram size
		System.out.println("Enter the n-gram size > ");
		Integer ngramSize = Integer.parseInt(sc.nextLine());
		
		//get output file
		System.out.println("Enter the path of the output file > ");
		
		String outputDir = sc.nextLine();
		sc.close();
		if(outputDir.isEmpty()){
			outputDir = defaultOutputFile + "probs" + ngramSize + ".txt";
		}
		
		
		File outputFile = new File(outputDir);
		outputFile.createNewFile();
		
		
		//count the ocurence of all n-gram texts
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
		
		//finding probabilties based on total input
		HashMap<String,Double> probs = new HashMap<String, Double>();
		for(Entry<String, Long> entry : count.entrySet()){
			foo++;
			//for printing out progress bar
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
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		
		//write to file
		System.out.println("Writing probabilties to file...");
		System.out.println("--------------------------------------------------");
		for(Entry<String, Double> entry : probs.entrySet()){
			foo++;
			if(100.0*foo/initialSize > milestone){
				milestone += 2;
				System.out.print("*");
			}
			bw.write(entry.getKey() + " ~:~ " + entry.getValue().toString());
			bw.write('\n');
		}
		bw.flush();
		bw.close();
		
		
		long endTime = System.currentTimeMillis();
		System.out.println();
		System.out.println();
		System.out.println("Time elapsed: " + (endTime-startTime)/1000.0 + " seconds");
	}
}

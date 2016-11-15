import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class FindCodeLength {
	public static void main(String[] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Enter the file to encode > ");
		String inputFileName = sc.nextLine();
		File inputFile = new File(inputFileName);
		if(!inputFile.exists() && !inputFile.isFile()){
			System.out.println("THIS IS NOT A FILE");
			sc.close();
			return;
		}
		
		System.out.println("Enter the name of the encoding file > ");
		String encodingFileName = sc.nextLine();
		File encodingFile = new File(encodingFileName);
		if(!encodingFile.exists() && !encodingFile.isFile()){
			System.out.println("THIS IS NOT A FILE");
			sc.close();
			return;
		}
		sc.close();
		
		System.out.println("Reading the encodings...");
		BufferedReader br = new BufferedReader(new FileReader(encodingFile));
		String line =  null;
		int ngramSize = 0;
		int longestEncodingSize = 0;
		
		Map<String, String> encodings = new HashMap<>();
		while((line = br.readLine()) != null){
			String[] split = line.split(" ~:~ ");
			ngramSize = split[0].length();
			encodings.put(split[0], split[1]);
			longestEncodingSize = Math.max(longestEncodingSize, split[1].length());
		}
		br.close();
		
		System.out.println("Determining the encoding length...");
		br = new BufferedReader(new FileReader(inputFile));
		String contents = br.readLine();
		
		long encodingLength = 0; 
		long numMissing = 0;
		while(contents.length() >= ngramSize){
			String toEncode  = contents.substring(0, ngramSize);
			contents = contents.substring(ngramSize);
			if(encodings.containsKey(toEncode)){
				encodingLength += encodings.get(toEncode).length();
			} else{
				numMissing++;
			}
		}
		
		br.close();
		if(contents.length() > 0){
			//find the worst possible case for the encoding of the remaining letters
			int worst = -1;
			for(Entry<String,String> encoding : encodings.entrySet()){
				if(encoding.getKey().startsWith(contents)){
					worst = Math.max(worst, encoding.getValue().length());
				}
			}
			
			if(worst == -1) {
				numMissing++;
			} else{
				encodingLength += worst;
			}
		}
		
		//account for codes which we did not have
		if(numMissing > 0){
			encodingLength += ((Math.log(numMissing)/Math.log(2)) + longestEncodingSize) * numMissing;
		}
		
		System.out.println(encodingLength);
	}
}

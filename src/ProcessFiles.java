import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ProcessFiles {

	public static void main(String[] args) throws IOException{
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Enter the directory to search > ");
		String searchDir = sc.nextLine();
		File inputDir = new File(searchDir);
		if(!inputDir.exists() && ! inputDir.isDirectory()){
			System.out.println("THAT ISNT A DIRECTORY");
			sc.close();
			return;
		}
		
		int numChars = 26;
		System.out.println("Enter the number of characters to use. Max 26. Defaults to 26 > ");
		String in =sc.nextLine();
		if(!in.isEmpty()){
			numChars = Integer.parseInt(in);
		}
		
		boolean includeSpaces = false;
		System.out.println("Use spaces (y/N)? > ");
		in =sc.nextLine();
		if(!in.isEmpty()){
			includeSpaces = in.trim().equals("y");
		}
		
		ArrayList<File> textFiles = new ArrayList<>();
		
		for(String f : inputDir.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".txt") && !name.endsWith("proc.txt");
					}})
		){
			textFiles.add(new File(inputDir, f));
			
		}
		
		for(File textFile : textFiles){
			System.out.println("Processing " + textFile.getName());
			
			File destFile = new File(textFile.getAbsolutePath().replace(".txt", ".proc.txt"));
			destFile.createNewFile();
			
			System.out.println("Writting to " + destFile.getName());
			
			BufferedReader br = new BufferedReader(new FileReader(textFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(destFile));
			Integer c =  null;
			int i = 0;
//			for(int j = 0; j<30; j++){
//				br.readLine();
//			}
			boolean doubleSpace = false;
			while((c = br.read()) != -1){
				if(c==9 || c == 10){
					c = 32;
				}
				if((c == 32 && includeSpaces) || (c >= 65 && c< 65 + numChars) || (c >= 97 && c< 97 + numChars) ){
					if(c == 32 && doubleSpace){
						continue;
					}
					if(c == 32){
						doubleSpace = true;
					} else{
						doubleSpace = false;
					}
					Character character = Character.toLowerCase((char)c.intValue());
					bw.write(character);
					i++;
					//System.out.print(character);
					if(i%100 == 0){
						bw.flush();
					}
				}
			}
			bw.flush();
			
			br.close();
			bw.close();
			
		}
		
		
		sc.close();
	}
	
}

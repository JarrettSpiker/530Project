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
			for(int j = 0; j<30; j++){
				br.readLine();
			}
			boolean doubleSpace = false;
			while((c = br.read()) != -1){
				if(c==9 || c == 10){
					c = 32;
				}
				if(c == 32 || (c >= 65 && c<= 90) || (c >= 97 && c<= 122) ){
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

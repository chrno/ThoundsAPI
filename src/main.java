import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.thounds.thoundsapi.utils.Base64Encoder;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String prova = Base64Encoder.Encode("/home/chrno/01_The_Hell_Song.mp3");
			 				    // Create file 
				    FileWriter fstream = new FileWriter("/home/chrno/out.txt");
				        BufferedWriter out = new BufferedWriter(fstream);
				    out.write(prova);
				    //Close the output stream
				    out.close();
				    
			System.out.println("finito");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

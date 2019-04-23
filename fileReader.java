import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class fileReader{
	private static BufferedReader br;
	
	public static boolean init(String filename) {
		try {
			br=new BufferedReader(new FileReader(filename));
			return true;
		}
		catch (IOException e) {
			System.err.format("IOException: %s%n", e);
			return false;
		}
	}
	public static void closeFile() {
		try {
			br.close();
		}
		catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
	}
	public static String readLine() {
		try {
			String line=br.readLine();
			return line;
		}
		catch (IOException e) {
			System.err.format("IOException: %s%n", e);
			return null;
		}
	}
}
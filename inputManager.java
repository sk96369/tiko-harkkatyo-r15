import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class inputManager{
	
	private static final Scanner reader=initScanner();
	private static final String ERRMSG1="Virheellinen syöte.\n";
	private static final String ERRMSG2="Virhe järjestelmässä.\n";
	
	private static Scanner initScanner() {
		Scanner sc=new Scanner(System.in);
		return sc;
	}
	private static void forceExit(Exception e) {
		System.out.print(ERRMSG2);
		e.printStackTrace();
		System.exit(1);
	}
	public static Integer readInt() {
		Integer value=null;
		boolean inputOk=false;
		
		do {
			try {
				value=Integer.parseInt(reader.nextLine());
				inputOk=true;
			}
			catch(NumberFormatException e) {
				System.out.print(ERRMSG1);
				inputOk=false;
			}
			catch(Exception e) {
				forceExit(e);
			}
		}while(!inputOk);
		
		return value;
	}
	public static Boolean readBoolean() {
		boolean value=false;
		boolean inputOk=false;
		
		do {
			try {
				value=Boolean.parseBoolean(reader.nextLine());
				inputOk=true;
			}
			catch(NumberFormatException e) {
				System.out.print(ERRMSG1);
				inputOk=false;
			}
			catch(Exception e) {
				forceExit(e);
			}
		}while(!inputOk);
		
		return value;
	}
	public static String readString() {
		String value="";
		boolean inputOk=false;
		
		do {
			try {
				value=reader.nextLine();
				inputOk=value.length()>0;
				
				if(!inputOk)System.out.print(ERRMSG1);
			}
			catch(Exception e) {
				forceExit(e);
			}
		}while(!inputOk);
		
		return value;
	}
	public static Date readDate() {
		Date value=null;
		boolean inputOk=false;
		
		do {
			try {
				String s=reader.nextLine();
				value=new SimpleDateFormat("yyyy-dd-MM").parse(s);
				inputOk=true;
			}
			catch(ParseException e) {
				System.out.print(ERRMSG1);
				inputOk=false;
			}
			catch(Exception e) {
				forceExit(e);
			}
			
		}while(!inputOk);
		
		return value;
	}
}
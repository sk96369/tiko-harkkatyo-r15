
public final class typeCaster{
	
	public static Integer toInt(String s) {
		try {
			int value=Integer.parseInt(s);
			return value;		
		}
		catch(NumberFormatException e) {
			return null;
		}
	}
	public static Boolean toBoolean(String s) {
		try {
			boolean value=Boolean.parseBoolean(s);
			return value;		
		}
		catch(NumberFormatException e) {
			return null;
		}
	}
}
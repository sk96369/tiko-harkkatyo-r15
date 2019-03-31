import java.sql.*;

public class ht2019{
	
	private static final String PROTOKOLLA = "jdbc:postgresql:";
	private static final String PALVELIN = "dbstud2.sis.uta.fi";
	private static final int PORTTI = 5432;
	private static final String TIETOKANTA = "tiko2019r15"; 
	private static final String KAYTTAJA = "OMA PPT";
	private static final String SALASANA = "OMA SALASANA";
	
	public static void main(String args[]) {
		Connection con = null;
		
		try {
			con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
			System.out.println("yhteys avattu");
		}
		catch(SQLException exc){
			System.out.println("tapahtui virhe: "+exc.getMessage());
		}
		
		
		
		
		
		
		if(con!=null) {
			try{
				System.out.println("suljetaan yhteys");
				con.close();
			}
			catch(SQLException exc){
				System.out.println("Virhe yhteyden sulkemisessa");
				return;
			}
		}
	}
	
}

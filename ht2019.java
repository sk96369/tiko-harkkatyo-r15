import java.sql.*;
import java.util.*;

public class ht2019{
	
	private static final String PROTOKOLLA = "jdbc:postgresql:";
	private static final String PALVELIN = "dbstud2.sis.uta.fi";
	private static final int PORTTI = 5432;
	private static final String TIETOKANTA = "tiko2019r15"; 
	private static final String KAYTTAJA = "tp427552";
	private static final String SALASANA = "tuomas";
	

	//Metodi satunnaisen kyselyn tulostamiseen
	public static void tulostaKysely(Connection con, String kysely) {
		try {
			PreparedStatement pst=con.prepareStatement(kysely);
			ResultSet rs=pst.executeQuery();
			ResultSetMetaData meta=rs.getMetaData();
			int colCount=meta.getColumnCount();
			while(rs.next()){
				for(int i=1; i<=colCount; i++) {
					System.out.print(rs.getString(i)+"; ");
				}
				System.out.println();
			}
			pst.close();
			rs.close();
		}
		catch(SQLException exc) {
			System.out.println("tapahtui virhe: "+exc.getMessage());
		}
	}
	//Metodi uuden asiakkaan lisäämiseksi tietokantaan.
	public void lisaaUusiAsiakas(Connection con){
		//Kysellään asiakkaan tiedot
		Scanner sc=new Scanner(System.in);
		System.out.print("Asiakkaan tunnus:");
		Integer atunnus=typeCaster.toInt(sc.nextLine());	//Huom! Voisi myös generoida automaattisesti.
		System.out.print("Asiakkaan nimi:");
		String animi=sc.nextLine();
		System.out.print("Asiakkaan osoite:");
		String aosoite=sc.nextLine();
		
		if(atunnus!=null) {
			try {
				//Lisätään uusi asiakas "asiakas"-tauluun
				PreparedStatement pst=con.prepareStatement("INSERT INTO asiakas(asiakasid, nimi, laskutusosoite) VALUES"
														  + "(?,?,?)");
				pst.setInt(1, atunnus);
				pst.setString(2, animi);
				pst.setString(3, aosoite);
				pst.executeUpdate();
				pst.close();
				System.out.println("Uusi asiakas lisätty.");
			}
			catch(SQLException exc) {
				System.out.println("tapahtui virhe: "+exc.getMessage());
			}
		}
		sc.close();
		
	}
	//Metodi työkohteen lisäämiseksi asiakkaalle
	public static void lisaaTyokohde(Connection con){
		Scanner sc=new Scanner(System.in);
		//Tulostetaan kannassa olevien asiakkaiden tiedot
		tulostaKysely(con, "SELECT*FROM asiakas");
		//Kysellään työkohteeseen liittyvät tiedot
		System.out.print("Työkohteen omistajan tunnus:");
		Integer atunnus=typeCaster.toInt(sc.nextLine());
		System.out.print("Työkohteen tunnus:");			
		Integer tktunnus=typeCaster.toInt(sc.nextLine());	//Huom! Voisi myös generoida automaattisesti.
		System.out.print("Työkohteen nimi:");
		String tknimi=sc.nextLine();
		System.out.print("Työkohteen osoite:");
		String tkosoite=sc.nextLine();
		System.out.print("Kotitalousvähennys? true/false:");
		Boolean kvk=typeCaster.toBoolean(sc.nextLine());
		
		if(atunnus!=null && tktunnus!=null && kvk!=null) {
			try {
				//Lisätään uusi työkohde
				PreparedStatement pst=con.prepareStatement("INSERT INTO työkohde(kohdeid, asiakasid, nimi, osoite, kvkelpoinen) VALUES"
												   +"(?,?,?,?,?)");
				pst.setInt(1,  tktunnus);
				pst.setInt(2, atunnus);
				pst.setString(3, tknimi);
				pst.setString(4, tkosoite);
				pst.setBoolean(5, kvk);
				pst.executeUpdate();
				pst.close();
				System.out.println("Uusi työkohde lisätty.");
				
			}
			catch(SQLException exc) {
				System.out.println("tapahtui virhe: "+exc.getMessage());
			}
		}
		sc.close();
		
	}
	//Metodi tuntiyöiden lisäämiseksi työkohteelle.
	public static void lisaaTuntityosuorite(Connection con) {
		//Tulostetaan työkohteiden tiedot ruudulle
		Scanner sc=new Scanner(System.in);
		String kysely="SELECT a.nimi as asiakas, t.kohdeid, t.nimi as kohde, t.osoite "+
				"FROM asiakas as a, työkohde as t "+
				"where a.asiakasid=t.asiakasid";
		System.out.println("Työkohteiden tiedot:");
		tulostaKysely(con, kysely);
		//Kysytään käyttäjältä työkohteeseen ja tuntityöhön liittyvät tiedot.
		System.out.print("Työkohteen tunnus:");
		Integer tkID=typeCaster.toInt(sc.nextLine());				//tkID=työkohteen tunnus
		System.out.print("Työn tyyppi (Suunnittelu/Työ/Aputyö):");
		String ttyyppi=sc.nextLine();								//ttyyppi=tuntityön tyyppi
		System.out.print("Työn määrä (tunteina):");
		Integer tunnit=typeCaster.toInt(sc.nextLine());				//Työtuntien määrä
		if(tkID!=null && tunnit!=null) {
			try {
				int suoriteID;
				//Suoritetaan kysely, jossa tarkastetaan, onko työkohteella vielä yhtään tuntityösuoritteita
				PreparedStatement pst=con.prepareStatement("SELECT suorite.suoriteid "+
												   "FROM suorite WHERE suorite.kohdeid=? and suorite.suoritetyyppi=true");
				pst.setInt(1, tkID);
				ResultSet rs=pst.executeQuery();
				//Jos aikaisempia tuntityösuoritteita löytyy...
				if(rs.next()) {
					//Haetaan aiemman kyselyn resultsetistä suoritteen tunnus.
					suoriteID=rs.getInt(1);
					//Suoritetaan päivitys suoritetuntityöt-tauluun. tuntityölisäys()-funktion toteutus määritelty PLfunktiot.sql tiedostossa
					CallableStatement cst=con.prepareCall("select tuntityölisäys(?,?,?)");
					cst.setInt(1, suoriteID);
					cst.setString(2, ttyyppi);
					cst.setInt(3, tunnit);
					cst.execute();
					cst.close();
				}
				//Jos aikaisempia tuntityösuoritteita ei ole...
				else {
					//Luodaan uusi suorite kys. työkohteelle.
					//Uusi suoriteID luodaan generoimalla satunnainen luku väliltä [100, 999]. Pitää tehdä parempi versio myöhemmin.
					suoriteID=(int)(Math.random()*900)+100;
					//Lisätään uusi tuntityösuoritteista kirjaa pitävä rivi työkohteelle "suorite"-tauluun
					pst=con.prepareStatement("INSERT INTO suorite VALUES (?,?,?)");
					pst.setInt(1, suoriteID);
					pst.setInt(2, tkID);
					pst.setBoolean(3, true);//true --> Kyseessä tuntityösuorite.
					pst.executeUpdate();
					
					//Lisätään uusi tuntityösuorite "suoritetuntityöt"-tauluun
					pst=con.prepareStatement("INSERT INTO suoritetuntityöt VALUES (?,?,?)");
					pst.setInt(1, suoriteID);
					pst.setString(2, ttyyppi);
					pst.setInt(3, tunnit);
					pst.executeUpdate();
				}
				
				rs.close();
				pst.close();
				
			}
			catch(SQLException exc){
				System.out.println("tapahtui virhe: "+exc.getMessage());
			}
		}
		sc.close();
	}
	//KESKENERÄINEN
	public static void lisaaTarvike(Connection con, int suoriteID) {
		Scanner sc=new Scanner(System.in);
		String kysely="SELECT tarvikeid, nimi, varastotilanne FROM tarvike WHERE varastotilanne>0";
		System.out.println("Tarvikkeet varastossa:");
		tulostaKysely(con, kysely);
	}
	
	public static Connection avaaYhteys() {
		try {
			Connection con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
			System.out.println("yhteys avattu");
			return con;
		}
		catch(SQLException exc){
			System.out.println("tapahtui virhe: "+exc.getMessage());
			System.out.println("Suljetaan ohjelma");
			System.exit(0);
		}
		return null;
		
	}
	public static void suljeYhteys(Connection con) {
		if(con!=null) {
			try{
				System.out.println("suljetaan yhteys");
				con.close();
			}
			catch(SQLException exc){
				System.out.println("Virhe yhteyden sulkemisessa");
			}
		}
	}
	public static void main(String args[]) {
		Connection con = avaaYhteys();
		//lisaaTyokohde(con);
		//lisaaTuntityosuorite(con);
		
		
		suljeYhteys(con);
	}
	
}

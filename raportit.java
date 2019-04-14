import java.sql.*;
import java.util.*;
import java.util.HashMap;

public class raportit {
	
	public static void main (String[] args) {
		
	}
	
	public void muodostaTuntityolasku (Connection con, int kohdeid){
		
		int asiakasid = null;
		String asiakasNimi = "";
		String laskutusosoite = "";
		String kohdeOsoite = "";
		boolean kvkelpoinen = null;
		int suoriteid = null;
		HashMap<String, Integer> tyonMaarat = new HashMap<String, Integer>();
		HashMap<String, Integer> tyonHinnat = new HashMap<String, Integer>();
		//int tyonMaarat[] = new int[3]; //indeksi 0=Suunnittelu, 1=Työ, 2=Aputyö
		//int tyonHinnat[] = new int[3]; //indeksi 0=Suunnittelu, 1=Työ, 2=Aputyö
		String tarvikkeet[];
		
		
		//Hakee tiedot työkohte -taulusta kohdeid:n perusteella.
		PreparedStatement tyokohdePst = con.prepareStatement("SELECT asiakasid, osoite, kvkelpoinen FROM työkohde WHERE kohdeid = ?");
		tyokohdePst.setInt(1, kohdeid);
		ResultSet tyokohdeRs = tyokohdePst.executeQuery();
		while (tyokohdeRs.next()){
			asiakasid = tyokohdeRs.getString("asiakasid");
			kohdeOsoite = tyokohdeRs.getString("osoite");
			kvkelpoinen = tyokohdeRs.getBoolean("kvkelpoinen");
		}
		
		//Hakee tiedot asiakas -taulusta asiakasid:n perusteella
		PreparedStatement asiakasPst = con.prepareStatement("SELECT asiakasid, nimi, laskutusosoite FROM asiakas WHERE asiakasid = ?");
		asiakasPst.setInt(1, asiakasid);
		ResultSet asiakasRs = asiakasPst.executeQuery();
		
		//Hakee suoriteid:n suorite -taulusta kohdeid:n perusteella. 
		PreparedStatement suoritePst = con.prepareStatement("SELECT suoriteid FROM suorite WHERE kohdeid = ? AND suoritetyyppi = true");
		suoritePst.setInt(1, kohdeid);
		ResultSet suoriteRs = suoritePst.executeQuery();
		while (suoriteRs.next()){
			suoriteid = suoriteRs.getInt("suoriteid");
		}
		
		//Hakee työsuoritteeseen käytetyt tunnit
		PreparedStatement suoritetuntityotPst = con.prepareStatement("SELECT tyyppi, määrä FROM suoritetuntityöt WHERE suoriteid = ?");
		suoritetuntityotPst.setInt(1, suoriteid);
		ResultSet suoritetuntityotRs = suoritetuntityotPst.executeQuery();
		while (suoritetuntityotRs.next()){
			String tyonTyyppi = suoritetuntityotRs.getString("tyyppi");
			int tyonMaara = suoritetuntityotRs.getInt("määrä");
			if(tyonTyyppi.isEqual("Suunnittelu")){
				//tyonMaarat[0] += tyonMaara;
				tyonMaarat.put("Suunnittelu", tyonMaara)
			} else if(tyonTyyppi.isEqual("Työ")){
				//tyonMaarat[1] += tyonMaara;
				tyonMaarat.put("Työ", tyonMaara)
			}else if(tyonTyyppi.isEqual("Aputyö")){
				//tyonMaarat[2] += tyonMaara;
				tyonMaarat.put("Aputyö", tyonMaara)
			}
		}
		
		//Hakee erilaisten töiden yksikköhinnat
		PreparedStatement tuntityotPst = con.prepareStatement("SELECT tyyppi, hinta FROM tuntityöt");
		ResultSet tuntityotRs = suoritePst.executeQuery();
		while (tuntityotRs.next()){
			String tyonTyyppi = tuntityotRs.getString("tyyppi");
			int tyonHinta = tuntityotRs.getInt("hinta");
			if(tyonTyyppi.isEqual("Suunnittelu")){
				//tyonHinnat[0] += tyonHinta;
				tyonHinnat.put("Suunnittelu", tyonHinta)
			} else if(tyonTyyppi.isEqual("Työ")){
				//tyonHinnat[1] += tyonHinta;
				tyonHinnat.put("Työ", tyonHinta)
			}else if(tyonTyyppi.isEqual("Aputyö")){
				//tyonHinnat[2] += tyonHinta;
				tyonHinnat.put("Aputyö", tyonHinta)
			}
		}
		
		//Hakee työsuoritteeseen käytetyt tarvikkeet
		PreparedStatement tarvikePst = con.prepareStatement("SELECT suoritetarvike.määrä, tarvike.tarvikeid, tarvike.nimi, tarvike.yksikkö, tarvike.myyntihinta, tarvike.alv FROM suoritetarvike, tarvike WHERE suoritetarvike.suoriteid = ? AND suoritetarvike.tarvikeid = tarvike.tarvikeid");
		tarvikePst.setInt(1, suoriteid);
		ResultSet tarvikeRs = tarvikePst.executeQuery();
		tarvikkeet = new String[tarvikeRs.getFetchSize()]; //Haetaan rivien määrä ja tehdään oikean kokoinen taulu.
		//tuotenro | kuvaus | määrä | yksikkö | yksikköhinta € | alv % | alv-osuus € | yhteensä € |
		int i = 0;
		while(tarvikeRs.next()){
			tarvikkeet[i] = (Integer.toString(tarvikeRs.getInt("tarvikeid")) + tarvikeRs.getString("nimi") + Integer.toString(tarvikeRs.getInt("määrä")) + tarvikeRs.getString("yksikkö") + ;
		}
		//https://docs.oracle.com/javase/tutorial/uiswing/components/table.html
		//Miten saisi tarvike ja tunti listauksista taisaisen taulukon?
		
			
		
	}
}



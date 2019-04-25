/* Luokka syötteen lukemiseen ja tulosteen tulostamiseen*/

import java.io.*;

public class UI
{	
	boolean kaynnissa;
	Connection con;
	
	/* Rakennin, joka julistaa käyttöliittymän olevan
	Käynnissä. */
	public UI(Connection c)
	{
		kaynnissa = true;
		con = c;
		
		System.out.println("Tmi Sähtkötärskyn tietokannanhallintaohjelma");
		System.out.println("Komennolla \"help\" saat listan ohjelman tunnistamista komennoista.");
	}
	
	// Ajometodi, kutsuu syötteenlukumetodia kunnes kaynnissa-attribuutti epätosi
	public void aja()
	{
		while(kaynnissa)
		{
			System.out.print("Syötä komento:");
			try
			{
				lueSyote();
			}
			catch(IllegalArgumentException e)
			{
				System.out.println(e.getMessage());
				System.out.println("Komennolla \"help\" saat listan ohjelman tunnistamista komennoista.");
			}
		}
	}
	
	/* Metodi, joka kutsuu inputManagerin readString-metodia
	ja kutsuu readStringin palauttamalla merkkijonolla syötteenkäsittelymetodia*/
	private void lueSyote() throws IllegalArgumentException
	{
		String syote = inputManager.readString();
		try
		{
			kasitteleSyote(syote);
		}
		catch(IllegalArgumentException e)
		{
			throw e;
		}
	}
	
	/* Metodi, joka päättelee syötteen perusteella mikä
	toiminto suoritetaan */
	private void kasitteleSyote(String s) throws IllegalArgumentException
	{
		String kysely = "";
		int tunnus = 0;
		switch(s)
		{
			// Pelkällä Enterin painalluksella tulostaa tyhjän rivin
			case "":
				System.out.println("");
				break;
			case "kysely":
				System.out.println("Syötä SQL-kysely:");
				System.out.print("psql=>");
				kysely = inputManager.readString();
				ht2019.tulostaKysely(con, kysely + "\n");
				break;
			case "lisää työkohde":
				ht2019.lisaaTyokohde(con);
				break;
			case "lisää asiakas":
				ht2019.lisaaUusiAsiakas(con);
				break;
			case "lisää urakkasopimus":
				ht2019.lisaaUrakkasopimusTietokantaan(con);
				break;
			case "lisää tarvike":
				System.out.println("Syötä suoritteen tunnus: [numero 0-*]");
				tunnus = inputManager.readInt();
				ht2019.lisaaTarvikeSuoritteeseen(con, tunnus);
				break;
			case "lisää tuntityösuorite":
				ht2019.lisaaTuntityosuorite(con);
				break;
			case "luo lasku":
				System.out.println("Syötä tuntityölaskun kohteen tunnus: ");
				tunnus = inputManager.readInt();
				ht2019.muodostaTuntityolasku(con, tunnus, null, 1);
				break;
			case "arvioi hinta":
				System.out.println("Syötä arvioitavan kohteen tunnus:");
				System.out.print("ID = ");
				tunnus = inputManager.readInt();
				ht2019.muodostaHintaArvio(con, tunnus);
				break;
			case "help":
				tulostaOhjeet();
				break;
			case "luo muistutuslaskut":
				System.out.println("Haetaan erääntyneet laskut...");
				int[] eraantyneidenTunnukset = ht2019.haeEraantyneetLaskut(con);
				if(eraantyneidenTunnukset != null)
				{
					System.out.println("Erääntyneitä laskuja ei löytynyt.");
				}
				else
				{
					System.out.println("Löydettiin " + eraantyneidenTunnukset.length + " erääntynyttä laskua, luodaan muistutuslaskut...");
					for(int index = 0;index < eraantyneidenTunnukset.length;index++)
					{
						
					}
					ht2019.muodostaTuntityolasku(con, 
				}
				break;
			case "lopeta":
				System.out.println("Suljetaan ohjelma.");
				kaynnissa = false;
				break;
			default:
				// Jos komentoa ei tunnisteta, heitetään virheilmoitus
				throw new IllegalArgumentException("Syötettyä komentoa ei tunnistettu.");
		}
	}
	
	/* Metodi, joka tulostaa jokaisen sen tunnistaman komennon */
	private void tulostaOhjeet()
	{
		System.out.println("lisää työkohde\nlisää asiakas\nlisää urakkasopimus\nlisää tarvike\nlisää tuntityösuorite\nluo lasku\nluo muistutuslaskut\nkysely\nlopeta");
	}
}

CREATE TABLE asiakas(
asiakasid INT NOT NULL,					--Asiakkaan tunnus
nimi VARCHAR(50) NOT NULL,				--Asiakkaan nimi
laskutusosoite VARCHAR(50) NOT NULL,	--Asiakkaan laskutusosoite
PRIMARY KEY(asiakasid));


CREATE TABLE työkohde(
kohdeid INT NOT NULL,					--Kohteen tunnus
asiakasid INT NOT NULL,					--Asiakkaan tunnus
nimi VARCHAR(50) NOT NULL,				--Kohteen nimi
osoite VARCHAR(50) NOT NULL,			--Kohteen osoite
kvkelpoinen BOOLEAN NOT NULL,			--Onko kohde kotitalousvähennyskelpoinen. True=on, False=ei ole
PRIMARY KEY(kohdeid),
FOREIGN KEY(asiakasid) REFERENCES asiakas);


CREATE TABLE suorite(					--Työsuoitetta kuvaava taulu
suoriteid INT NOT NULL,					--Työsuoritteen tunnus
kohdeid INT NOT NULL,					--Kohteen tunnus
PRIMARY KEY(suoriteid),
FOREIGN KEY(kohdeid) REFERENCES työkohde);


CREATE TABLE tarvike(
tarvikeid INT NOT NULL,					--Tarvikkeen tunnus
nimi VARCHAR(50) NOT NULL,				--Tarvikkeen nimi
sohinta NUMERIC(3, 2) NOT NULL,			--Tarvikkeen sisäänostohinta
yksikkö VARCHAR(50) NOT NULL,			--Tarvikkeiden määrän yksikkö. Kpl/metri jne...
varastotilanne INT NOT NULL,			--Tarvikkeen varastotilanne. Tavaraa varastossa varastotilanne*yksikkö määrä
PRIMARY KEY(tarvikeid));


CREATE TABLE suoritetarvike(			
tarvikeid INT NOT NULL,					--Tarvikkeen tunnus
suoriteid INT NOT NULL,					--Suoritteen tunnus
määrä INT NOT NULL,						--Montako tarviketta suoritteeseen kuuluu
PRIMARY KEY(tarvikeid, suoriteid),
FOREIGN KEY(tarvikeid) REFERENCES tarvike,
FOREIGN KEY(suoriteid) REFERENCES suorite);


CREATE TABLE tuntityöt(
tyyppi CHAR(50) NOT NULL,				--Tuntityö tyyppi, suunnittelu/työ/aputyö
hinta NUMERIC(3, 2) NOT NULL,			--Työn tuntikohtainen hinta
PRIMARY KEY(tyyppi));


CREATE TABLE suoritetuntityöt(
suoriteid INT NOT NULL,					--Työsuoritteen tunnus
tyyppi CHAR(50) NOT NULL,				--Tuntityön tyyppi
määrä INT NOT NULL,						--Montako tuntia töitä kuuluu suoritteeseen
PRIMARY KEY(suoriteid, tyyppi),
FOREIGN KEY(suoriteid) REFERENCES suorite,
FOREIGN KEY(tyyppi) REFERENCES tuntityöt);


CREATE TABLE urakkasopimus(				--Urakkapohjaista työtä kuvaava taulu
urakkaid INT NOT NULL,					--Urakan tunnus
suoriteid INT NOT NULL,					--Työsuoritteen tunnus
laskueräsuuruus INT NOT NULL,
laskuerälkm INT NOT NULL,
laskueriäjäljellä INT NOT NULL,
PRIMARY KEY(urakkaid),
FOREIGN KEY(suoriteid) REFERENCES suorite);


CREATE TABLE urakkatyöt(				--Taulu kuvaa tietoa siitä, mitä töitä urakkasopimukseen kuuluu
urakkaid INT NOT NULL,					--Urakan tunnus
tyyppi CHAR(50) NOT NULL,				--Tuntityön tyyppi
määrä INT NOT NULL,						--Montako tuntia kys. työtä kuuluu urakkasopimukseen
PRIMARY KEY(urakkaid, tyyppi),
FOREIGN KEY(urakkaid) REFERENCES urakkasopimus,
FOREIGN KEY(tyyppi) REFERENCES tuntityöt);


CREATE TABLE lasku(
laskuid INT NOT NULL,					--Laskun tunnus
suoriteid INT NOT NULL,					--Suoritteen tunnus
lähetyspvm DATE NOT NULL,
eräpvm DATE NOT NULL,
maksettu BOOLEAN NOT NULL,				--Onko lasku maksettu. True=on, False=ei ole
moneskolasku INT NOT NULL,				--Monesko lasku on kyseessä
edeltävälasku INT NOT NULL,				--Viittaus mahdolliseen edeltäjälaskuun
PRIMARY KEY(laskuid),
FOREIGN KEY(suoriteid) REFERENCES suorite,
FOREIGN KEY(edeltävälasku) REFERENCES lasku);

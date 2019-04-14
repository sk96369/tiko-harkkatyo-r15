
CREATE TABLE asiakas(
asiakasid INT NOT NULL,					--Asiakkaan tunnus
nimi VARCHAR(50) NOT NULL,				--Asiakkaan nimi
laskutusosoite VARCHAR(50) NOT NULL,	--Asiakkaan laskutusosoite
PRIMARY KEY(asiakasid));

INSERT INTO asiakas VALUES
(11, 'Ville Viima', 'Tammelankatu 55'),
(22, 'Jussi Joki', 'Vellamonkatu 10'),
(33, 'Pekka Puska', 'Käypytie 13'),
(44, 'Jenni Joki', 'Kannaksenkatu 43');

CREATE TABLE työkohde(
kohdeid INT NOT NULL,					--Kohteen tunnus
asiakasid INT NOT NULL,					--Asiakkaan tunnus
nimi VARCHAR(50) NOT NULL,				--Kohteen nimi
osoite VARCHAR(50) NOT NULL,			--Kohteen osoite
kvkelpoinen BOOLEAN NOT NULL,			--Onko kohde kotitalousvähennyskelpoinen. True=on, False=ei ole
PRIMARY KEY(kohdeid),
FOREIGN KEY(asiakasid) REFERENCES asiakas);

INSERT INTO työkohde VALUES
(111, 11, 'Oma asunto', 'Tammelankatu 55', true),
(112, 11, 'Varastohalli', 'Mattilankatu 10', false),
(221, 22, 'Kesämökki', 'Leppästentie 88', true),
(331, 33, 'Oma asunto', 'Käpytie 13', true),
(441, 44, 'Isovanhempien asunto', 'Kannaksenkatu 44', true);

CREATE TABLE suorite(					--Työsuoitetta kuvaava taulu
suoriteid INT NOT NULL,					--Työsuoritteen tunnus
kohdeid INT NOT NULL,					--Kohteen tunnus
suoritetyyppi BOOLEAN NOT NULL,			--Suoritteen tyyppi, tuntityö=1, urakkatyö=0. (Lisätty kyselyjen helpottamiseksi)
PRIMARY KEY(suoriteid),
FOREIGN KEY(kohdeid) REFERENCES työkohde);

INSERT INTO suorite VALUES				--Testiarvoja
(777, 111, true),
(888, 112, true);


CREATE TABLE tarvike(
tarvikeid INT NOT NULL,					--Tarvikkeen tunnus
nimi VARCHAR(50) NOT NULL,				--Tarvikkeen nimi
sohinta NUMERIC(10, 2) NOT NULL,			--Tarvikkeen sisäänostohinta
yksikkö VARCHAR(50) NOT NULL,			--Tarvikkeiden määrän yksikkö. Kpl/metri jne...
varastotilanne INT NOT NULL CHECK(varastotilanne >=0),			--Tarvikkeen varastotilanne. Tavaraa varastossa varastotilanne*yksikkö määrä
myyntihinta NUMERIC(10, 2) NOT NULL, --Tarvikkeen myyntihinta (sis. alv)
alv INT NOT NULL, --Tarvikkeen arvonlisäveroprosentti
PRIMARY KEY(tarvikeid));

INSERT INTO tarvike VALUES
(100, 'ABB Jussi uppokytkin', 19.90, 'kpl', 20, 24.90, 24),
(101, 'ABB Jussi uppopistorasia', 12.90, 'kpl', 20, 15.90, 24),
(102, 'ABB nysä M16 20kpl', 7.99, 'kpl', 10, 9.99, 24),
(103, 'MCMK maakaapeli', 1.5, 'metri', 200, 1.9, 24),
(104, 'MMJ 3x1,5 asennuskaapeli', 0.77, 'metri', 100, 0.99, 24),
(105, 'Yleissulake 10A 500V', 0.40, 'kpl', 50, 0.50, 24),
(106, 'ABB Jussi pinta 6 -kytkin', 8.99, 'kpl', 20, 11.99, 24),
(107, 'Airam Naulakiinnike TC 10-14', 0.08, 'kpl', 1000, 0.10, 24),
(108, 'Sähköasennusten perusteet', 19.99, 'kpl', 10, 24.99, 10); --kirja

CREATE TABLE suoritetarvike(			
tarvikeid INT NOT NULL,					--Tarvikkeen tunnus
suoriteid INT NOT NULL,					--Suoritteen tunnus
määrä INT NOT NULL,						--Montako tarviketta suoritteeseen kuuluu
PRIMARY KEY(tarvikeid, suoriteid),
FOREIGN KEY(tarvikeid) REFERENCES tarvike,
FOREIGN KEY(suoriteid) REFERENCES suorite);

INSERT INTO suoritetarvike VALUES (106, 200, 6);

CREATE TABLE tuntityöt(
tyyppi CHAR(50) NOT NULL,				--Tuntityö tyyppi, suunnittelu/työ/aputyö
hinta NUMERIC(10, 2) NOT NULL,			--Työn tuntikohtainen hinta
PRIMARY KEY(tyyppi));

INSERT INTO tuntityöt VALUES
('Suunnittelu', 55.0),
('Työ', 45.0),
('Aputyö', 35.0);


CREATE TABLE suoritetuntityöt(
suoriteid INT NOT NULL,					--Työsuoritteen tunnus
tyyppi CHAR(50) NOT NULL,				--Tuntityön tyyppi
määrä INT NOT NULL,						--Montako tuntia töitä kuuluu suoritteeseen
PRIMARY KEY(suoriteid, tyyppi),
FOREIGN KEY(suoriteid) REFERENCES suorite,
FOREIGN KEY(tyyppi) REFERENCES tuntityöt);

INSERT INTO suoritetuntityöt VALUES		--Testiarvoja
(777, 'Suunnittelu', 2),
(777, 'Työ', 20),
(888, 'Työ', 10);


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
edeltävälasku INT,				--Viittaus mahdolliseen edeltäjälaskuun
PRIMARY KEY(laskuid),
UNIQUE(suoriteid, moneskolasku),--Yksilöidään suoriteid,moneskolasku-yhdiste. 
FOREIGN KEY(suoriteid) REFERENCES suorite,
FOREIGN KEY(edeltävälasku) REFERENCES lasku,
CHECK(eräpvm > lähetyspvm));

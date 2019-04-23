--Funktio 1, Käytetään lisaaTuntityosuorite()-metodin yhteydessä. Funktio on luotu jo tietokantaan.
--PL/psql-funktio tuntitöiden lisäämiseksi suoritteeseen.
--Parametrit: sid=suoriteid, tyyppi_=tuntityön tyyppi, määrä_=työtuntien määrä
CREATE OR REPLACE FUNCTION tuntityölisäys(sid integer, tyyppi_ text, määrä_ integer)--ok
RETURNS void AS $$
BEGIN
	--Jos avaimella (sid, tyyppi_)-määritelty rivi jo olemassa
	IF (select count(*) from suoritetuntityöt where tyyppi=tyyppi_ and suoriteid=sid)=1 then
		--Päivitetään kys. rivin työtunnit. (eli lisätään tunteihin määrä_-tuntia)
		update suoritetuntityöt set määrä=määrä::int + määrä_ where tyyppi=tyyppi_ and suoriteid=sid;
	ELSE
		--Uusi tuntityösuorite --> Lisätään uusi rivi normaalisti tauluun.
		insert into suoritetuntityöt values (sid, tyyppi_, määrä_);
	END IF;
END
$$ LANGUAGE plpgsql;

--Funktio 2 , kts Funktio 1
CREATE OR REPLACE FUNCTION tarvikelisäys(tid integer, sid integer, määrä_ integer)--ok
RETURNS void AS $$
BEGIN
	IF (select count(*) from suoritetarvike where tarvikeid=tid and suoriteid=sid)=1 then
		update suoritetarvike set määrä=määrä::int + määrä_ where tarvikeid=tid and suoriteid=sid;
	ELSE
		insert into suoritetarvike values (tid, sid, määrä_);
	END IF;
END
$$ LANGUAGE plpgsql;

--Funktio 3, Hakee suurimman arvon taulun tbl sarakkeesta col
CREATE OR REPLACE FUNCTION maxValue(tbl text, col text, out res integer)--ok
as $$
begin
	execute format('select max(%s) from %s', col, tbl)
	into res;
end
$$ LANGUAGE plpgsql;
--Funktio 4, kts triggeri 4. 
CREATE OR REPLACE FUNCTION lisäätarvike()--ok
RETURNS TRIGGER AS
$BODY$
BEGIN
	if exists (select 1 from tarvike where tarvikeid=new.tarvikeid) then
		update tarvike set sohinta=new.sohinta, myyntihinta=new.myyntihinta where tarvikeid=new.tarvikeid;
		RETURN NULL;
	else
		RETURN NEW;
	end if;
END
$BODY$
LANGUAGE plpgsql;
--Funktio 5, kts triggeri 5.
CREATE OR REPLACE FUNCTION poistatarvike()--ok
RETURNS TRIGGER AS
$BODY$
BEGIN
	if exists (select 1 from suoritetarvike where tarvikeid=old.tarvikeid) or old.varastotilanne>0 then
		RETURN NULL;
	else
		RETURN OLD;
	end if;
END
$BODY$
LANGUAGE plpgsql;
--Funktio 6, kts triggero 3.
CREATE OR REPLACE FUNCTION lisääTarvikeHistoriaan()--ok
RETURNS TRIGGER AS
$BODY$
BEGIN
	if exists (select 1 from tarvikehistoria where tarvikeid=old.tarvikeid) then
		RETURN NULL;
	else
		RETURN NEW;
	end if;
END
$BODY$
LANGUAGE plpgsql;
--Funktio 7, kts triggeri 1.
CREATE OR REPLACE FUNCTION lisääTarvikeHistoriaan2()--ok
RETURNS TRIGGER AS
$BODY$
BEGIN
	INSERT INTO tarvikehistoria VALUES(old.tarvikeid, old.nimi, old.sohinta, old.yksikkö, old.myyntihinta, old.alv);
	RETURN NULL;
END
$BODY$
LANGUAGE plpgsql;

--Triggeri 1
CREATE TRIGGER lisää_poistettu_tarvike_historiaan
AFTER DELETE ON tarvike
FOR EACH ROW
EXECUTE PROCEDURE lisääTarvikeHistoriaan2();

--Triggeri 3
CREATE TRIGGER onko_tuote_historiassa
BEFORE INSERT ON tarvikehistoria
FOR EACH ROW
EXECUTE PROCEDURE lisääTarvikeHistoriaan();
--Triggeri 4
CREATE TRIGGER onko_tuote_taulussa
BEFORE INSERT ON tarvike
FOR EACH ROW
EXECUTE PROCEDURE lisäätarvike();
--Triggeri 5
CREATE TRIGGER voiko_tuotteen_poistaa
BEFORE DELETE ON tarvike
FOR EACH ROW
EXECUTE PROCEDURE poistatarvike();
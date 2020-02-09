# Fander App

Dies ist eine Webapp um gemeinsam Mittagessen zu bestellen. Als Datenbank wird MongoDB verwendet.

## Eclipse

Ant-Target "eclipse" der Datei build.xml ausf�hren. Anschlie�end Workspace aktualisieren.

## Bauen

Ant-Target "installDist" der Datei build.xml ausf�hren. Es gibt weitere Targets zum Bauen und Pushen eines Docker Images.

## Erstinbetriebnahme

AppConfig.properties anlegen. Unter Key "port" den Port eintragen, unter der die
Fander App laufen soll (Default 4040). Unter "host" eintragen unter welchem Protokoll, Hostnamen
und Port die Anwendung von au�en erreichbar ist (Beispiel: "http://die-domain.de:4040").
Unter Key "dbname" den Namen der MongoDB-Datenbank eintragen. Unter "dbhost" muss der MongoDB-Host, ggf. mit Port,
angegeben werden (Beispiel: "die-domain.de"). In der AppConfig sollte der Key password-hash-repeats.v0 mit einem geheimen Wert
gr��er 7000 (und gew�hnlich kleiner 10000) konfiguriert werden. Anschlie�end kann die Anwendung mittels FanderApp.java gestartet
werden und im Browser aufgerufen werden.

### Start mit leerer Datenbank

In den AppConfig.properties den Developer-Login unter Keys "developer" und "user-manager" eintragen. Dann mit diesem Login
und einem beliebigen nicht-leeren Kennwort einloggen. Da noch keine User in der Datenbank sind, wird der Login zugelassen. Anschlie�end
in der Benutzerverwaltung den eigenen Benutzer inkl. Kennwort anlegen.

### Anwendungseinstellungen

Danach weitere Benutzer anlegen und die Anwendungseinstellungen anpassen. Dort ist insbesondere der Anzeigename vom Ansprechpartner
und die URL der Mittagskarte einzutragen.

## Login vs. Anzeigename

Der Login ist nur zum Einloggen da. Der Anzeigename (intern: user) wird in der Anwendung angezeigt
und in der Datenbank als Benutzer-Id verwendet. Beide d�rfen jeweils nur einmal im System vorkommen.

## Rollen

Developer: Wird in der AppConfig.properties �ber Key "developer" festgelegt. Es k�nnen mehrere Benutzer als Developer definiert werden
(Beispiel: "developer=Waldemar,Chantal"). Es ist der Anzeigename des Benutzers zu verwenden. Developer k�nnen alle Wochen sehen und eine
Woche l�schen.

User-Manager: Wird in der AppConfig �ber Key "user-manager" definiert. Mehrere m�glich. User-Manager k�nnen Anwendungseinstellungen �ndern
und die Benutzerverwaltung benutzen.

Ansprechpartner: Wird unter Anwendungseinstellungen konfiguriert. Dort ist der Anzeigename des Benutzers einzutragen. Es sollte im System
genau einen Ansprechpartner geben. Der Ansprechpartner kann die Woche starten, die Bestellung schlie�en, die Anrufseite verwenden und
sieht Summen und die Druckseite.

Ein Benutzer kann mehrere Rollen haben. Eine Rolle umfasst keine anderen Rollen.

## REST API

Die Fander App bietet auch eine REST API. Dar�ber l�sst sich 'Unsere Karte' abfragen und eine Bestellung absenden. Mittels REST API
lie�e sich bspw. eine Smartphone App (iOS: Ahmet, Android: Dennis) anbinden. Die Klasse RestApiTest testet die Schnittstelle.

Im Erfolgsfall ist der HTTP Response Status im Bereich 200 bis 299. Im Fehlerfall wird Status 500 und eine JSON Fehlermeldung geliefert.

### Anmelden

POST /rest/login

Mit dem Login kann man mittels Login und Kennwort einen User Token erhalten. Wenn der Client einen g�ltigen User Token hat, braucht dieser
eigentlich keine An-/Abmeldung durchzuf�hren.

Request Body: JSON mit login, password, forceNewToken (bool).

Response (im Erfolgsfall): JSON mit token

### Abmelden

GET /rest/logout?ut=TOKEN

L�scht den User Token.

Response (im Erfolgsfall): JSON mit text="ok"

### Unsere Karte

GET /rest/unsere-karte?ut=TOKEN

Man muss sich vom User-Manager f�r seinen User den REST API Access Token geben lassen. Diesen muss man als Query Parameter "ut" beim
REST API Request angeben. R�ckgabe: UnsereKarteJSON mit den Unterobjekten TagJSON und Gericht.

Response (im Erfolgsfall): Unsere Karte

### Bestellung absenden

POST /rest/bestellen/STARTDATUM?ut=TOKEN

Request Body: Limit (optional), Gericht ID Liste (alle Gerichte, die der User f�r die angegebene Woche bestellen m�chte)

Response (im Erfolgsfall): Unsere Karte

STARTDATUM und die Gericht IDs bekommt man aus dem Unsere Karte Aufruf.

### Ich m�chte nicht bestellen

GET /rest/nicht-bestellen/STARTDATUM?ut=TOKEN

R�ckg�ngig machen: GET /rest/nicht-bestellen/STARTDATUM?ut=TOKEN&undo=1

STARTDATUM ist ein Parameter im Format JJJJ-MM-TT. Den Wert bekommt man vom Unsere Karte Aufruf.

Response (im Erfolgsfall): JSON mit text="ok"

### �ffentliche Notiz

GET /rest/public-note?ut=TOKEN&all=1

Die Option all ist optional. Beim Weglassen wird nur die eigene �ffentliche Notiz geliefert.

Response: JSON mit public notes (+user +timestamp)

POST /rest/public-note?ut=TOKEN&text=TEXT

Eigene �ffentliche Notiz schreiben

Response (im Erfolgsfall): JSON mit text="ok"

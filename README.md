# Fander App

Dies ist eine Webapp um gemeinsam Mittagessen zu bestellen. Als Datenbank wird MongoDB verwendet.

## Eclipse

Ant-Target "eclipse" der Datei build.xml ausführen. Anschließend Workspace aktualisieren.

## Bauen

Ant-Target "installDist" der Datei build.xml ausführen. Es gibt weitere Targets zum Bauen und Pushen eines Docker Images.

## Erstinbetriebnahme

AppConfig.properties anlegen. Unter Key "port" den Port eintragen, unter der die
Fander App laufen soll (Default 4040). Unter "host" eintragen unter welchem Protokoll, Hostnamen
und Port die Anwendung von außen erreichbar ist (Beispiel: "http://die-domain.de:4040").
Unter Key "dbname" den Namen der MongoDB-Datenbank eintragen. Unter "dbhost" muss der MongoDB-Host, ggf. mit Port,
angegeben werden (Beispiel: "die-domain.de"). In der AppConfig sollte der Key password-hash-repeats.v0 mit einem geheimen Wert
größer 7000 (und gewöhnlich kleiner 10000) konfiguriert werden. Anschließend kann die Anwendung mittels FanderApp.java gestartet
werden und im Browser aufgerufen werden.

### Start mit leerer Datenbank

In den AppConfig.properties den Developer-Login unter Keys "developer" und "user-manager" eintragen. Dann mit diesem Login
und einem beliebigen nicht-leeren Kennwort einloggen. Da noch keine User in der Datenbank sind, wird der Login zugelassen. Anschließend
in der Benutzerverwaltung den eigenen Benutzer inkl. Kennwort anlegen.

### Anwendungseinstellungen

Danach weitere Benutzer anlegen und die Anwendungseinstellungen anpassen. Dort ist insbesondere der Anzeigename vom Ansprechpartner
und die URL der Mittagskarte einzutragen.

## Login vs. Anzeigename

Der Login ist nur zum Einloggen da. Der Anzeigename (intern: user) wird in der Anwendung angezeigt
und in der Datenbank als Benutzer-Id verwendet. Beide dürfen jeweils nur einmal im System vorkommen.

## Rollen

Developer: Wird in der AppConfig.properties über Key "developer" festgelegt. Es können mehrere Benutzer als Developer definiert werden
(Beispiel: "developer=Waldemar,Chantal"). Es ist der Anzeigename des Benutzers zu verwenden. Developer können alle Wochen sehen und eine
Woche löschen.

User-Manager: Wird in der AppConfig über Key "user-manager" definiert. Mehrere möglich. User-Manager können Anwendungseinstellungen ändern
und die Benutzerverwaltung benutzen.

Ansprechpartner: Wird unter Anwendungseinstellungen konfiguriert. Dort ist der Anzeigename des Benutzers einzutragen. Es sollte im System
genau einen Ansprechpartner geben. Der Ansprechpartner kann die Woche starten, die Bestellung schließen, die Anrufseite verwenden und
sieht Summen und die Druckseite.

Ein Benutzer kann mehrere Rollen haben. Eine Rolle umfasst keine anderen Rollen.

## REST API

Die Fander App bietet auch eine REST API. Darüber lässt sich 'Unsere Karte' abfragen und eine Bestellung absenden. Mittels REST API
ließe sich bspw. eine Smartphone App (iOS: Ahmet, Android: Dennis) anbinden. Die Klasse RestApiTest testet die Schnittstelle.

Im Erfolgsfall ist der HTTP Response Status im Bereich 200 bis 299. Im Fehlerfall wird Status 500 und eine JSON Fehlermeldung geliefert.

### Anmelden

POST /rest/login

Mit dem Login kann man mittels Login und Kennwort einen User Token erhalten. Wenn der Client einen gültigen User Token hat, braucht dieser
eigentlich keine An-/Abmeldung durchzuführen.

Request Body: JSON mit login, password, forceNewToken (bool).

Response (im Erfolgsfall): JSON mit token

### Abmelden

GET /rest/logout?ut=TOKEN

Löscht den User Token.

Response (im Erfolgsfall): JSON mit text="ok"

### Unsere Karte

GET /rest/unsere-karte?ut=TOKEN

Man muss sich vom User-Manager für seinen User den REST API Access Token geben lassen. Diesen muss man als Query Parameter "ut" beim
REST API Request angeben. Rückgabe: UnsereKarteJSON mit den Unterobjekten TagJSON und Gericht.

Response (im Erfolgsfall): Unsere Karte

### Bestellung absenden

POST /rest/bestellen/STARTDATUM?ut=TOKEN

Request Body: Limit (optional), Gericht ID Liste (alle Gerichte, die der User für die angegebene Woche bestellen möchte)

Response (im Erfolgsfall): Unsere Karte

STARTDATUM und die Gericht IDs bekommt man aus dem Unsere Karte Aufruf.

### Ich möchte nicht bestellen

GET /rest/nicht-bestellen/STARTDATUM?ut=TOKEN

Rückgängig machen: GET /rest/nicht-bestellen/STARTDATUM?ut=TOKEN&undo=1

STARTDATUM ist ein Parameter im Format JJJJ-MM-TT. Den Wert bekommt man vom Unsere Karte Aufruf.

Response (im Erfolgsfall): JSON mit text="ok"

### Öffentliche Notiz

GET /rest/public-note?ut=TOKEN&all=1

Die Option all ist optional. Beim Weglassen wird nur die eigene öffentliche Notiz geliefert.

Response: JSON mit public notes (+user +timestamp)

POST /rest/public-note?ut=TOKEN&text=TEXT

Eigene öffentliche Notiz schreiben

Response (im Erfolgsfall): JSON mit text="ok"

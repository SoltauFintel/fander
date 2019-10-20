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
angegeben werden (Beispiel: "die-domain.de"). Anschließend kann die Anwendung mittels FanderApp.java gestartet werden
und im Browser aufgerufen werden.

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

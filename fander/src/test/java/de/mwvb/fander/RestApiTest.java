package de.mwvb.fander;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pmw.tinylog.Level;

import com.google.gson.Gson;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.dao.UserDAO;
import de.mwvb.fander.dao.WocheDAO;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.User;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.rest.BestellungRequestJSON;
import de.mwvb.fander.rest.LoginRequestJSON;
import de.mwvb.fander.rest.LoginResponseJSON;
import de.mwvb.fander.rest.TagJSON;
import de.mwvb.fander.rest.UnsereKarteJSON;
import de.mwvb.fander.service.FanderService;
import de.mwvb.maja.mongo.AbstractDAO;
import de.mwvb.maja.mongo.Database;
import de.mwvb.maja.rest.RestCaller;
import de.mwvb.maja.rest.RestException;

public class RestApiTest {
    private static final int PORT = 4040;
    private static final String MONGODB_HOST = "192.168.99.100";
    private static final String LOGIN = "test";
    private static final String KENNWORT = "geheim";
    private static final String USER = "Testuser";
    private static final String STARTDATUM = "2019-01-07";
    private Woche woche;
    
    @BeforeClass
    public static void open() {
        new FanderApp() {
            @Override
            protected void initDatabase() {
                AbstractDAO.database = new Database(MONGODB_HOST, "fander_RestApiTest", "", "", Woche.class);
            }
            
            protected Level getDefaultLoggingLevel() {
                return Level.DEBUG;
            }
        }.start(FanderApp.VERSION);
    }
    
    /** Jeder Testcase fängt mit einer leeren Datenbank an, die nur einen User enthält. */
    @Before
    public void cleanup() {
        new UserDAO().clear();
        User user = new User();
        user.setId(LOGIN);
        user.setLogin(LOGIN);
        user.setKennwort(KENNWORT);
        user.setUser(USER);
        new UserService().save(user);
        
        new WocheDAO().clear();
    }
    
    @AfterClass
    public static void close() {
        AbstractDAO.database.close();
    }
    
    @Test
    public void loginLogout() throws IOException {
        // Login
        LoginRequestJSON r = new LoginRequestJSON();
        r.setForceNewToken(true);
        r.setLogin(LOGIN);
        r.setPassword(KENNWORT);
        RestCaller client = new RestCaller();
        String json = client.post("http://localhost:" + PORT + "/rest/login", r);
        LoginResponseJSON a = new Gson().fromJson(json, LoginResponseJSON.class);
        Assert.assertTrue("Login gescheitert\r\n" + json, a != null && a.getToken() != null && !a.getToken().isEmpty());
        
        // Logout
        client = new RestCaller();
        json = client.get("http://localhost:" + PORT + "/rest/logout?ut=" + a.getToken());
        Assert.assertTrue("Logout gescheitert\r\n" + json, json.contains("ok"));
        // Token ist nun gelöscht
        
        // erneuter Login
        r.setForceNewToken(false); // Auch bei false wird ein neuer Token generiert, wenn zuvor keiner gesetzt war.
        json = client.post("http://localhost:" + PORT + "/rest/login", r);
        LoginResponseJSON aa = new Gson().fromJson(json, LoginResponseJSON.class);
        Assert.assertNotEquals("Neuer Token muss generiert worden sein!", a.getToken(), aa.getToken());
    }

    @Test
    public void falscherLogin() throws IOException {
        // Test
        LoginRequestJSON r = new LoginRequestJSON();
        r.setLogin("Quark");
        r.setPassword(KENNWORT);
        RestCaller client = new RestCaller();
        try {
            client.post("http://localhost:" + PORT + "/rest/login", r);
            Assert.fail("RestException expected");
        } catch (RestException e) {
            // Verify
            Assert.assertEquals(500, e.getStatus());
            Assert.assertTrue(e.getMessage().contains("E1"));
        }
    }

    @Test
    public void falschesKennwort() throws IOException {
        // Test
        LoginRequestJSON r = new LoginRequestJSON();
        r.setLogin(LOGIN);
        r.setPassword("Quark");
        RestCaller client = new RestCaller();
        try {
            client.post("http://localhost:" + PORT + "/rest/login", r);
            Assert.fail("RestException expected");
        } catch (RestException e) {
            // Verify
            Assert.assertEquals(500, e.getStatus());
            Assert.assertTrue(e.getMessage().contains("E3"));
        }
    }
    
    @Test
    public void restApiGesperrt() throws IOException {
        // Prepare
        UserService sv = new UserService();
        User user = sv.byId(LOGIN);
        user.setToken("-");
        sv.save(user);
        
        // Test
        LoginRequestJSON r = new LoginRequestJSON();
        r.setForceNewToken(true);
        r.setLogin(LOGIN);
        r.setPassword(KENNWORT);
        RestCaller client = new RestCaller();
        try {
            client.post("http://localhost:" + PORT + "/rest/login", r);
            Assert.fail("RestException expected");
        } catch (RestException e) {
            // Verify
            Assert.assertEquals(500, e.getStatus());
            Assert.assertTrue(e.getMessage().contains("E4"));
        }
    }
    
    @Test
    public void userGesperrt() throws IOException {
        // Prepare
        UserService sv = new UserService();
        User user = sv.byId(LOGIN);
        user.setAktiv(false);
        sv.save(user);
        
        // Test
        LoginRequestJSON r = new LoginRequestJSON();
        r.setForceNewToken(true);
        r.setLogin(LOGIN);
        r.setPassword(KENNWORT);
        RestCaller client = new RestCaller();
        try {
            client.post("http://localhost:" + PORT + "/rest/login", r);
            Assert.fail("RestException expected");
        } catch (RestException e) {
            // Verify
            Assert.assertEquals(500, e.getStatus());
            Assert.assertTrue(e.getMessage().contains("E2"));
        }
    }
    
    @Test
    public void ichMoechteNichtBestellen() throws IOException {
        // Prepare
        String token = prepare();
        
        // Test
        RestCaller client = new RestCaller();
        String json = client.get("http://localhost:" + PORT + "/rest/nicht-bestellen/" + STARTDATUM + "?ut=" + token);

        // Verify
        Assert.assertTrue(json.contains("ok"));
        json = client.get("http://localhost:" + PORT + "/rest/unsere-karte?ut=" + token);
        UnsereKarteJSON uk = new Gson().fromJson(json, UnsereKarteJSON.class);
        Assert.assertTrue(uk.isMoechteNichtsBestellen());
    }

    @Test
    public void ichMoechteDochBestellen() throws IOException {
        // Prepare
        String token = prepare();
        RestCaller client = new RestCaller();
        client.get("http://localhost:" + PORT + "/rest/nicht-bestellen/" + STARTDATUM + "?ut=" + token);
        
        // Test
        String json = client.get("http://localhost:" + PORT + "/rest/nicht-bestellen/" + STARTDATUM + "?undo=1&ut=" + token);
        
        // Verify
        Assert.assertTrue(json.contains("ok"));
        json = client.get("http://localhost:" + PORT + "/rest/unsere-karte?ut=" + token);
        UnsereKarteJSON uk = new Gson().fromJson(json, UnsereKarteJSON.class);
        Assert.assertFalse(uk.isMoechteNichtsBestellen());
    }
    
    @Test
    public void bestellen() throws IOException {
        // Prepare
        String token = prepare();
        RestCaller client = new RestCaller();
        String g1 = woche.getTage().get(0).getGerichte().get(1).getId(); // Montag, 2. Gericht
        String g2 = woche.getTage().get(2).getGerichte().get(0).getId(); // Mittwoch, 1. Gericht
        
        // Test
        BestellungRequestJSON b = new BestellungRequestJSON();
        ArrayList<String> gerichte = new ArrayList<>();
        gerichte.add(g1);
        gerichte.add(g2);
        b.setGerichte(gerichte);
        String json = client.post("http://localhost:" + PORT + "/rest/bestellen/" + STARTDATUM + "?ut=" + token, b);
        UnsereKarteJSON uk = new Gson().fromJson(json, UnsereKarteJSON.class);
        
        // Verify
        Assert.assertTrue(uk.getLimit() == null || uk.getLimit().isEmpty());
        Assert.assertFalse(uk.isMoechteNichtsBestellen());
        Assert.assertFalse(uk.isGeschlossen());
        boolean g1ok = false, g2ok = false;
        for (TagJSON tag : uk.getTage()) {
            for (Gericht g : tag.getGerichte()) {
                if (g.isBestellt()) {
                    if (g1.equals(g.getId()) && tag.getTagNummer() == 1) {
                        if (g1ok) {
                            Assert.fail("g1ok schon true?");
                        } else {
                            g1ok = true;
                            Assert.assertEquals(USER, g.getNamen());
                        }
                    } else if (g2.equals(g.getId()) && tag.getTagNummer() == 3) {
                        if (g2ok) {
                            Assert.fail("g2ok schon true?");
                        } else {
                            g2ok = true;
                            Assert.assertEquals(USER, g.getNamen());
                        }
                    } else {
                        Assert.fail("Gericht " + g.getId() + " darf nicht bestellt sein! Tag: " + tag.getTag());
                    }
                }
            }
        }
        Assert.assertTrue("Gericht 1 nicht bestellt!", g1ok);
        Assert.assertTrue("Gericht 2 nicht bestellt!", g2ok);
    }

    private String prepare() throws IOException {
        LoginRequestJSON r = new LoginRequestJSON();
        r.setForceNewToken(true);
        r.setLogin(LOGIN);
        r.setPassword(KENNWORT);
        RestCaller client = new RestCaller();
        String json = client.post("http://localhost:" + PORT + "/rest/login", r);
        LoginResponseJSON a = new Gson().fromJson(json, LoginResponseJSON.class);
        woche = new FanderService().createNeueWocheForTest(STARTDATUM);
        return a.getToken();
    }
}

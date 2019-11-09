package de.mwvb.fander;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.dao.UserDAO;
import de.mwvb.fander.dao.WocheDAO;
import de.mwvb.fander.model.User;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.rest.LoginRequestJSON;
import de.mwvb.fander.rest.LoginResponseJSON;
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
    
    @BeforeClass
    public static void open() {
        new FanderApp() {
            @Override
            protected void initDatabase() {
                AbstractDAO.database = new Database(MONGODB_HOST, "fander_RestApiTest", "", "", Woche.class);
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

    private String prepare() throws IOException {
        LoginRequestJSON r = new LoginRequestJSON();
        r.setForceNewToken(true);
        r.setLogin(LOGIN);
        r.setPassword(KENNWORT);
        RestCaller client = new RestCaller();
        String json = client.post("http://localhost:" + PORT + "/rest/login", r);
        LoginResponseJSON a = new Gson().fromJson(json, LoginResponseJSON.class);
        new FanderService().createNeueWocheForTest(STARTDATUM);
        return a.getToken();
    }
}

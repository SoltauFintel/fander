package de.mwvb.fander.rest;

public class LoginRequestJSON {
    private String login;
    private String password;
    private boolean forceNewToken = false;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isForceNewToken() {
        return forceNewToken;
    }

    public void setForceNewToken(boolean forceNewToken) {
        this.forceNewToken = forceNewToken;
    }
}

package de.mwvb.fander.rest;

import com.google.gson.Gson;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SJsonAction;

public class LoginREST extends SJsonAction<LoginResponseJSON> {
    // TODO Testcase für Login/Logout

    @Override
    protected LoginResponseJSON work() {
        LoginRequestJSON loginRequest = new Gson().fromJson(req.body(), LoginRequestJSON.class);
        return new UserService().login(loginRequest);
    }
}

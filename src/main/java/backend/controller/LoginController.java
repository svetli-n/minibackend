package backend.controller;

import backend.model.Authenticator;
import backend.controller.validator.GetRequestValidator;
import backend.controller.validator.PositiveIntegerValidator;
import backend.controller.validator.ValidationException;
import com.sun.net.httpserver.HttpExchange;

import java.util.Optional;

public class LoginController <T> extends BaseController {

    private Authenticator authenticator;

    public LoginController(long expireInSeconds) { this.authenticator = new Authenticator(expireInSeconds); }

    public void login(HttpExchange httpExchange, String[] path) {
        if (path.length < 3) {
            throw new ControllerException();
        }
        String userIdString = path[path.length-2];
        try{
            GetRequestValidator.validate(httpExchange);
            T userId = (T)PositiveIntegerValidator.validate(userIdString);
            String sessionId = authenticator.login(userId);
            sendResponse(httpExchange, 200, sessionId);
        }
        catch (ValidationException e) {
            throw new ControllerException();
        }
    }

    public T ensureLogin(HttpExchange httpExchange) throws Throwable {
        String sessionkey = Optional.ofNullable(getQueryPartForKey(httpExchange, "sessionkey"))
                .orElseThrow(() -> new ControllerException());
        T userId = (T) authenticator.getUserId(sessionkey)
                .orElseThrow(() -> new ControllerException());
        if (!authenticator.isLoggedIn(sessionkey)) {
            throw new ControllerException();
        }
        return userId;
    }

    public void removeExpiredSessions() {
        authenticator.removeExpiredSessions();
    }
}

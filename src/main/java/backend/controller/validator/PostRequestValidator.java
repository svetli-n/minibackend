package backend.controller.validator;

import com.sun.net.httpserver.HttpExchange;

public class PostRequestValidator {

    public static void validate(HttpExchange httpExchange) {
        if (!httpExchange.getRequestMethod().equals("POST")) {
            throw new ValidationException();
        }
    }
}

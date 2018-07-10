package backend.controller.validator;

import com.sun.net.httpserver.HttpExchange;

public class GetRequestValidator {

    public static void validate(HttpExchange httpExchange) {
        if (!httpExchange.getRequestMethod().equals("GET")) {
            throw new ValidationException();
        }
    }
}

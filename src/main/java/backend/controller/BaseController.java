package backend.controller;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

// TODO Use template pattern: validate -> process -> respond?
public class BaseController {

    // TODO merge writeResponseHeaders and writeResponseBody?
    public static void sendResponse(HttpExchange httpExchange, int statusCode, String responseMessage) {
        writeResponseHeaders(httpExchange, statusCode, responseMessage);
        writeResponseBody(httpExchange, responseMessage);
    }

    private static Map<String, String> queryToMap(String queryString) {
        Map<String, String> query = new HashMap<>();
        for (String param: queryString.split("&")) {
            String[] parts = param.split("=");
            if (parts.length == 2) {
                query.put(parts[0], parts[1]);
            } else if (parts.length == 1) {
                query.put(parts[0], "");
            }
        }
        return query;
    }

    public static String getQueryPartForKey(HttpExchange httpExchange, String key) {
        Map<String, String> queryMap = queryToMap(httpExchange.getRequestURI().getQuery());
        return queryMap.get(key);
    }

    private static void writeResponseHeaders(HttpExchange httpExchange, int statusCode, String responseMessage) {
        try {
            httpExchange.sendResponseHeaders(statusCode, responseMessage.getBytes().length);
        } catch (IOException e) {
            throw new ControllerException();
        }
    }

    private static void writeResponseBody(HttpExchange httpExchange, String responseMessage) {
        try (OutputStream os = httpExchange.getResponseBody();) {
            os.write(responseMessage.getBytes());
        } catch (IOException e) {
            throw new ControllerException();
        }
    }
}

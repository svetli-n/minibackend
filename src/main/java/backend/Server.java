package backend;

import backend.controller.ControllerException;
import backend.controller.LoginController;
import backend.controller.ScoreController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.newCachedThreadPool;

class Server <T> {
    //TODO have it as setting
    private static long exipreTime = 10*60;
    //TODO have it as setting?
    private static int firstN = 15;
    private static LoginController loginController = new LoginController(exipreTime);
    private static ScoreController scoreController = new ScoreController();
    ScheduledExecutorService bgExecutor = Executors.newScheduledThreadPool(1);


    public Server() {
        bgExecutor.schedule(
            () -> loginController.removeExpiredSessions(), 1, TimeUnit.HOURS
        );
    }

    HttpServer getServer(String hostname, int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        HttpServer httpServer = HttpServer.create(address, 0);
        // TODO can be newFixedThreadPool() instead, for predictable memory footprint
        Executor executor = newCachedThreadPool();
        httpServer.setExecutor(executor);
        httpServer.createContext("/", httpExchange -> {
            String[] path = httpExchange.getRequestURI().getPath().split("/");
            if (path.length < 1) {
                loginController.sendResponse(httpExchange, 400, "");
            }
            String action = path[path.length-1];
            // TODO Implement routing with e.g. Decorator pattern like Spring MVC
            try {
                switch (action) {
                    case "login":
                        loginController.login(httpExchange, path);
                        break;
                    case "highscorelist":
                        scoreController.getScores(httpExchange, path, firstN);
                        break;
                    case "score":
                        T userId = (T) loginController.ensureLogin(httpExchange);
                        scoreController.addScore(httpExchange, path, userId);
                        break;
                    default:
                        loginController.sendResponse(httpExchange, 400, "");
                        break;
                }
            } catch (ControllerException e) {
                loginController.sendResponse(httpExchange, 400, "");
            } catch (Throwable throwable) {
                loginController.sendResponse(httpExchange, 400, "");
            }
        });
        return httpServer;
    }
}

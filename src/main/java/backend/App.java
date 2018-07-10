package backend;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        //TODO have it as setting
        int port = 8081;
        //TODO have it as setting
        String hostname = "localhost";

        Server<Integer> server = new Server<>();
        HttpServer httpServer = server.getServer(hostname, port);
        httpServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            httpServer.stop(1);
            server.bgExecutor.shutdownNow();
        }));
    }
}

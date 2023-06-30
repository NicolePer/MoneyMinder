package at.nicoleperak.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {
        try {
            InetAddress inet = InetAddress.getByName("localhost");
            InetSocketAddress addr = new InetSocketAddress(inet, 4712);

            HttpServer server = HttpServer.create(addr, 0);
            server.createContext("/", new Handler());

            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            System.out.println("server started - press any key to terminate");
            System.in.read();
            System.out.println("server terminated");
            server.stop(0);
            ((ExecutorService) server.getExecutor()).shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

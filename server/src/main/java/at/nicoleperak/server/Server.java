package at.nicoleperak.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sun.net.httpserver.HttpServer.create;

@SuppressWarnings("CallToPrintStackTrace")
public class Server {

    /**
     * Starts the HTTP server and initializes schedulers.
     *
     * @param args Command line arguments (not used).
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
        try {
            InetAddress inet = InetAddress.getByName("localhost");
            InetSocketAddress address = new InetSocketAddress(inet, 4712);

            HttpServer server = create(address, 0);
            server.createContext("/", new EndpointsHandler());

            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            System.out.println("server started - press enter to terminate");
            try (RecurringTransactionsExecutorService executor = new RecurringTransactionsExecutorService()) {
                executor.executeOutstandingRecurringTransactionOrders();
                executor.scheduleService();
                System.in.read();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("server terminated");
            server.stop(0);
            ((ExecutorService) server.getExecutor()).shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

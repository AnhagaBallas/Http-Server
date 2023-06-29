import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static ExecutorService executorService = Executors.newFixedThreadPool(64);

    public void start() {
        try (final var serverSocket = new ServerSocket(9999)) {
            while (!serverSocket.isClosed()) {
                var socket = serverSocket.accept();
                executorService.execute(new RunnableForServer(socket));
            }
            executorService.shutdown();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

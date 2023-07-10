import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
      private static final String GET = "GET";
    private static final String POST = "POST";
    static ExecutorService executorService = Executors.newFixedThreadPool(64);
    private static Map<String, Handler> getMap = new HashMap<>();
    private static Map<String, Handler> postMap = new HashMap<>();

    public static Map<String, Handler> getGetMap() {
        return getMap;
    }

    public static Map<String, Handler> getPostMap() {
        return postMap;
    }

    public void addHandler(String method, String path, Handler handler) {
        if (method.equals(POST)) {
            postMap.put(path, handler);
        } else if (method.equals(GET)) {
            getMap.put(path, handler);
        }
    }

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

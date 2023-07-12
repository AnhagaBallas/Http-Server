import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final String GET = "GET";
    private static final String POST = "POST";
    static ExecutorService executorService = Executors.newFixedThreadPool(64);
    private static ConcurrentHashMap<String, Map<String, Handler>> mapForHandler = new ConcurrentHashMap();
    private static Server instance = null;

    private Server() {
        mapForHandler.put(POST, new HashMap<String, Handler>());
        mapForHandler.put(GET, new HashMap<String, Handler>());
    }

    public static Server getInstance() {
        {
            if (instance == null)
                instance = new Server();
            return instance;
        }

    }

    public static ConcurrentHashMap<String, Map<String, Handler>> getMapForHandler() {
        return mapForHandler;
    }

    public static void setMapForHandler(ConcurrentHashMap<String, Map<String, Handler>> mapForHandler) {
        Server.mapForHandler = mapForHandler;
    }

    public void addHandler(String method, String path, Handler handler) {
        if (method.equals(POST)) {
            mapForHandler.get(POST).put(path, handler);
        } else if (method.equals(GET)) {
            mapForHandler.get(GET).put(path, handler);
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

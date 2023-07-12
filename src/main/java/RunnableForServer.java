import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class RunnableForServer implements Runnable {
    final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    final private Socket socket;

    public RunnableForServer(Socket socket) {
        this.socket = socket;
    }

    private static String pathWithNoParametersMethod(String path) {
        String pathWithNoParameters = " ";
        for (int i = 0; i < path.length(); i++) {
            int start = 0;
            if (path.charAt(i) == '?') {
                start = i;
                pathWithNoParameters = path.substring(0, start);
                break;
            } else {
                pathWithNoParameters = path;
            }
        }
        return pathWithNoParameters;
    }

    @Override
    public void run() {
        try (final var in = new BufferedInputStream(socket.getInputStream());
             final var out = new BufferedOutputStream(socket.getOutputStream())) {
            Request request = new Request(in, out);
            final var path = request.getPath();
            final var pathWithNoparmeters = pathWithNoParametersMethod(path);
            System.out.println(pathWithNoparmeters);
            if (Server.getMapForHandler().get(request.getMethod()).containsKey(pathWithNoparmeters)) {
                Server.getMapForHandler().get(request.getMethod()).get(pathWithNoparmeters).handle(request, out);
            } else {
                if (!validPaths.contains(pathWithNoparmeters)) {
                    out.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.flush();
                    socket.close();
                }

                final var filePath = Path.of(".", "public", pathWithNoparmeters);
                final var mimeType = Files.probeContentType(filePath);

                // special case for classic
                if (pathWithNoparmeters.equals("/classic.html")) {
                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    out.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.write(content);
                    out.flush();
                    socket.close();
                }

                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}


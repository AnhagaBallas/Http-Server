import java.io.IOException;

public class Request {
    private String method;
    private String path;

    public Request(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public static Request createRequest(String requestLine) throws IOException {
        final var parts = requestLine.split(" ");
        var path = parts[1];
        var method = parts[0];
        return new Request(method, path);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

}

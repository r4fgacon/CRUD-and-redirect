import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;


public class App {
    public static void main(String[] args) throws Exception {
        // create a server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // set routes
        server.createContext("/students", new StudentController());
        server.setExecutor(null); // creates a default executor

        // start listening
        server.start();
    }
}
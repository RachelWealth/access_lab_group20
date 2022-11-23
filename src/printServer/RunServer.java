package printServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;

public class RunServer {

    private static IPrintServer server;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        // encrypt all plain passwords
        //encryptPasswords();

        // initialize the printserver object
        server = new PrintServer();

        registerServerObject(server);

        System.out.println("Server is started!");
    }


    private static void registerServerObject(IPrintServer server) throws IOException {

        FileReader fReader = new FileReader("project.config");
        BufferedReader bReader = new BufferedReader(fReader);
        String port = null;
        String name = null;
        port = server.readConfig("port");
        name = server.readConfig("name");

        Registry registry = java.rmi.registry.LocateRegistry.createRegistry(Integer.parseInt(port));
        registry.rebind(name, server);

    }

}

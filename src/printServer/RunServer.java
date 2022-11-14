package printServer;

import accesscontrol.Permission;
import accesscontrol.Role;
import accesscontrol.User;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class RunServer {

    public static final String PLAIN_PASSWORD_FILE_NAME = "plain_passwords.txt";
    public static final String ENCRYPTED_PASSWORD_FILE_NAME = "enc_passwords.txt";
    private static final String PERMISSIONS_FILE = "permissions.txt";
    private static final String ROLE_FILE = "roles.txt";
    private static final String ROLE_PERMISSIONS_FILE = "role_permissions.txt";
    private static IPrintServer server;
    private static List<Role> roles;
    private static Registry registry;


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        // encrypt all plain passwords
        encryptPasswords();

        // initialize the printserver object
        server = new PrintServer();

        initRolesPermissions();
        
        // initialize users and their roles
        initUsers();
        initRegistry();

        System.out.println("Server is started!");
    }

    private static void initRolesPermissions() throws IOException {
        //init permissions
        initPermissions();

        //init roles
        initRoles();

        // assign permissions to roles
        assignPermissionsToRole();
    }

    private static void assignPermissionsToRole() throws IOException {
        FileReader fin = new FileReader(ROLE_PERMISSIONS_FILE);
        BufferedReader bin = new BufferedReader(fin);
        String line;
        while((line = bin.readLine()) != null) {
            Role.Roles role = Role.Roles.valueOf(line.split(":")[0]);
            Role role1 = server.getRoleByName(role);

            Permission.Permissions permission = Permission.Permissions.valueOf(line.split(":")[1]);
            Permission permission1 = server.getPermissionByName(permission);

            role1.addPermissions(permission1);
        }
        bin.close();
        fin.close();
    }

    private static void initRoles() throws IOException {
        FileReader fin = new FileReader(ROLE_FILE);
        BufferedReader bin = new BufferedReader(fin);
        String line;
        while((line = bin.readLine()) != null) {
            Integer id = Integer.valueOf(line.split(":")[0]);
            Role.Roles role = Role.Roles.valueOf(line.split(":")[1]);
            Role role1 = new Role(id, role);
            server.addRole(role1);
        }
        bin.close();
        fin.close();
    }

    private static void initPermissions() throws IOException {
        FileReader fin = new FileReader(PERMISSIONS_FILE);
        BufferedReader bin = new BufferedReader(fin);
        String line;
        while((line = bin.readLine()) != null) {
            Integer id = Integer.valueOf(line.split(":")[0]);
            Permission.Permissions permission = Permission.Permissions.valueOf(line.split(":")[1]);
            Permission permission1 = new Permission(id, permission);
            server.addPermission(permission1);
        }
        bin.close();
        fin.close();
    }

    private static void initUsers() throws IOException {
        FileReader fin = new FileReader(ENCRYPTED_PASSWORD_FILE_NAME);
        BufferedReader bin = new BufferedReader(fin);
        String line;
        while((line = bin.readLine()) != null) {
            String user = line.split(":")[0];
            String pass = line.split(":")[1];
            Role userRole = null;
            Role.Roles role = Role.Roles.valueOf(line.split(":")[2]);
            userRole = server.getRoles().get(role);
            User userObj = new User(user, pass, userRole);
            server.addUser(userObj);
        }
        fin.close();
    }

    private static void initRegistry() throws RemoteException, FileNotFoundException {
        FileReader fReader = new FileReader("project.config");
        BufferedReader bReader = new BufferedReader(fReader);
        String port, name;
        port = server.readConfig("port");
        name = server.readConfig("name");
        registry = java.rmi.registry.LocateRegistry.createRegistry(Integer.parseInt(port));
        registry.rebind(name, server);
    }

    private static void encryptPasswords() throws IOException, NoSuchAlgorithmException {
        FileReader fin = new FileReader(PLAIN_PASSWORD_FILE_NAME);
        BufferedReader bin = new BufferedReader(fin);
        PrintWriter pwriter = new PrintWriter(ENCRYPTED_PASSWORD_FILE_NAME);
        String line;
        while((line = bin.readLine()) != null) {
            String user = line.split(":")[0];
            String pass = line.split(":")[1];
            String role = line.split(":")[2];
            String encPass = PasswordEncrypter.getEncryptedPassword(pass);
            pwriter.println(user+":"+encPass+":"+role);
        }
        pwriter.close();
        fin.close();
    }

}

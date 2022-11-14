package printServer;

import accesscontrol.AccessControl;
import accesscontrol.Permission;
import accesscontrol.Role;
import accesscontrol.User;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Map;

public class PrintServer extends UnicastRemoteObject implements IPrintServer, Serializable {

    boolean isServerStarted = false;
    public int queueNo = 1;
    private LinkedList<PrinterQueue> printQueue;
    private AccessControl accessControl;

    public PrintServer() throws RemoteException {
        super();
        printQueue = new LinkedList<>();
        accessControl = new AccessControl();
    }


    @Override
    public void print(String filename, String printer) throws RemoteException {
        try {
            if (isStarted()) {
                PrinterQueue job = new PrinterQueue(filename, printer, queueNo);
                printQueue.add(job);
                queueNo++;
                System.out.println("print() invoked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String queue(String printer) {
        String queue = "";
        try {
            if (isStarted()) {

                for(PrinterQueue pq: printQueue) {
                    queue += pq + "\n";
                }
            }
            System.out.println("queue() invoked");
            return queue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queue;
    }

    @Override
    public void topQueue(String printer, int job) {
        try {
            if (isStarted()) {
                PrinterQueue printingJobMoved = null;
                for(PrinterQueue pq : printQueue) {
                    if(pq.queueNo == job) {
                        printingJobMoved = pq;
                        printQueue.remove(pq);
                        break;
                    }
                }
                printQueue.addFirst(printingJobMoved);
                System.out.println("topQueue() invoked");
            }

        } catch (IndexOutOfBoundsException ie) {
            System.out.println("Invalid JobNo.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        isServerStarted = true;
        System.out.println("start() invoked");

    }

    @Override
    public void stop() {
        isServerStarted = false;
        System.out.println("stop() invoked");

    }

    @Override
    public void restart() {
        isServerStarted = false;
        printQueue.clear();
        isServerStarted = true;
//        System.out.println("Printer is restarted");
        System.out.println("restart() invoked");
    }

    @Override
    public boolean status(String printer) {
        try {
            System.out.println("status() invoked");
            return isStarted();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String readConfig(String parameter) {
        try {
            FileReader fReader = new FileReader("project.config");
            BufferedReader bReader = new BufferedReader(fReader);
            String line;
            String configVal = null;
            while ((line = bReader.readLine()) != null) {
                if (line.split("=")[0].equalsIgnoreCase(parameter)) {
                    configVal = line.split("=")[1];
                    break;
                }
            }
            fReader.close();
            bReader.close();
            System.out.println("readConfig() invoked");
            return configVal;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setConfig(String parameter, String value) {
//        try {
//            PrintWriter pWriter = new PrintWriter(new BufferedWriter(new FileWriter("project.config", true)));
//            pWriter.println(parameter+"="+value);
//            pWriter.close();
            System.out.println("setConfig() invoked");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean isAuthorized(User user, Permission permission) throws RemoteException {
        return user.isAllowed(permission);
    }

    @Override
    public User isAuthenticated(String username, String password) {
        FileReader fReader = null;
        try {
            fReader = new FileReader("enc_passwords.txt");
            BufferedReader bReader = new BufferedReader(fReader);
            String line;
            while ((line = bReader.readLine()) != null) {
                if(line.contains(username)) {
                    String correctEncPassword = line.split(":")[1];
                    if(correctEncPassword.equals(PasswordEncrypter.getEncryptedPassword(password))) {
                        return accessControl.getUserByUsername(username);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public boolean isStarted() throws Exception {
//        if(!isServerStarted) {
//            throw new Exception("Printer not started!");
//        }
//        return isServerStarted;
//    }

    @Override
    public boolean isStarted() {
        if(!isServerStarted) {
            System.out.println("\t\t\t\t\t" + "Printer not started!");
            System.out.println("\t\t\t\t\t\t" +"\033[3m(For any invocation status:\033[0m");
            System.out.println("\t\t\t\t\t\t" + "\033[3m'Printer must be started at least once\033[0m");
            System.out.println("\t\t\t\t\t\t" + "\033[3m by any other user than 'ORDINARY USER')\033[0m");
        }
        return isServerStarted;
    }

    @Override
    public Permission getPermissionByName(Permission.Permissions permission) throws RemoteException {
        return accessControl.getPermissionByName(permission);
    }

    @Override
    public Role getRoleByName(Role.Roles role) throws RemoteException {
        return accessControl.getRoleByName(role);
    }

    @Override
    public void addUser(User userObj) throws RemoteException {
        accessControl.addUser(userObj);
    }

    @Override
    public void addPermission(Permission permission) throws RemoteException {
        accessControl.addPermission(permission);
    }

    @Override
    public void addRole(Role role) throws RemoteException {
        accessControl.addRole(role);
    }

    @Override
    public Map<Role.Roles, Role> getRoles() throws RemoteException {
        return accessControl.getRoles();
    }


    public class PrinterQueue {
        public String fileName;
        public String printer;
        public int queueNo;

        public PrinterQueue(String fileName, String printer, int queueNo) {
            this.fileName = fileName;
            this.printer = printer;
            this.queueNo = queueNo;
        }


        public String toString() {
            return (this.queueNo + "\t\t\t" + this.fileName + "\t\t\t" + this.printer + "\n");
        }
    }
}

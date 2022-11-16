package printServer;

import utils.userDatabase;
import utils.sessionManager;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;

public class PrintServer  extends UnicastRemoteObject implements IPrintServer {

    boolean isServerStarted = false;
    //boolean isServerStarted = true;
    public int queueNo = 1;
    private LinkedList<PrinterQueue> printQueue = new LinkedList<PrinterQueue>();

    private final userDatabase udb = new userDatabase();

    private static sessionManager seMan = new sessionManager();

    public PrintServer() throws RemoteException {
        super();
    }

    @Override
    public void print(String filename, String printer, UUID token) throws RemoteException {
        try {
            if (isStarted()) {
                if (!seMan.isSessionValid(token)) {
                    System.out.println("session expired");
                    return;
                }
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
    public String queue(String printer, UUID token) {
        String queue = "";
        try {
            if (isStarted()) {
                if (!sessionManager.isSessionValid(token)) {
                    System.out.println("session expired");
                    return null;
                }
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
    public void topQueue(String printer, int job, UUID token) {
        try {
            if (isStarted()) {
                if (!sessionManager.isSessionValid(token)) {
                    System.out.println("session expired");
                    return;
                }
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
    public UUID start(String user) {
        UUID token = sessionManager.generateSession(user);
        isServerStarted = true;
        System.out.println("start() invoked");
        return token;

    }

    @Override
    public void stop( UUID token) {
        if (!sessionManager.isSessionValid(token)) {
            System.out.println("session expired");
            return;
        }
        isServerStarted = false;
        System.out.println("stop() invoked");

    }

    @Override
    public void restart( UUID token) {
        if (!sessionManager.isSessionValid(token)) {
            System.out.println("session expired");
            return;
        }
        isServerStarted = false;
        printQueue.clear();
        isServerStarted = true;
//        System.out.println("Printer is restarted");
        System.out.println("restart() invoked");
    }

    @Override
    public boolean status(String printer, UUID token) {
        //TODO is running
        try {
            if (!sessionManager.isSessionValid(token)) {
                System.out.println("session expired");
                return false;
            }
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
    public boolean isAuthorized(String username, String password) throws SQLException, NoSuchAlgorithmException {
        ResultSet rs = udb.search(username);
        while (rs.next()){
//            System.out.println(rs.getString("pw"));
//            System.out.println(rs.getString("salt"));
//            System.out.println(PasswordEncrypter.getEncryptedPassword(password, rs.getString("salt")));
            if(Objects.equals(rs.getString("pw"), PasswordEncrypter.getEncryptedPassword(password, rs.getString("salt")))){
                return true;
            }
        }
        return false;
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
            System.out.println("\t" + "Printer not started!");
            System.out.println("Please START the printer to see method invocation status"+"\n");
        }
        return isServerStarted;
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

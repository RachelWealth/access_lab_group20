package printServer;

import Models.Const;
import Models.User;
import utils.DBManagerProTwo;
import utils.sessionManager;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PrintServer extends UnicastRemoteObject implements IPrintServer {

    private static sessionManager seMan = new sessionManager();
    private final DBManagerProTwo udb = new DBManagerProTwo();
    //boolean isServerStarted = true;
    public int queueNo = 1;
    boolean isServerStarted = false;
    boolean isPermission = false;
    User u = new User();
    private LinkedList<PrinterQueue> printQueue = new LinkedList<PrinterQueue>();

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
                isPermission = isPermit(Const.getPrint());
                if (!isPermission) {
                    System.out.println("No permission");
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
                isPermission = isPermit(Const.getQueue());
                if (!isPermission) {
                    System.out.println("No permission");
                    return "\0";
                }
                for (PrinterQueue pq : printQueue) {
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
                isPermission = isPermit(Const.getTopQueue());
                if (!isPermission) {
                    System.out.println("No permission");
                    return;
                }
                PrinterQueue printingJobMoved = null;
                for (PrinterQueue pq : printQueue) {
                    if (pq.queueNo == job) {
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
        isServerStarted = isPermit(Const.getStart());
        isPermission = isServerStarted;
        System.out.println("start() invoked");
        return token;

    }

    @Override
    public void stop(UUID token) {
        if (!sessionManager.isSessionValid(token)) {
            System.out.println("session expired");
            return;
        }
        isPermission = isPermit(Const.getStop());
        if (!isPermission) {
            System.out.println("No permission");
            return;
        }
        isServerStarted = false;
        u.userName = null;
        u.userPermision = null;
        System.out.println("stop() invoked");

    }

    @Override
    public void restart(UUID token) {
        if (!sessionManager.isSessionValid(token)) {
            System.out.println("session expired");
            return;
        }
        isPermission = isPermit(Const.getRestart());
        if (!isPermission) {
            System.out.println("No permission");
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
            isPermission = isPermit(Const.getStatus());
            if (!isPermission) {
                System.out.println("No permission");
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
    public String readConfig(String parameter, UUID... token) {
        if ((token.length != 0) && (!sessionManager.isSessionValid(token[0]))) {
            System.out.println("session expired");
            return "\0";
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setConfig(String parameter, String value, UUID... token) {

        if ((token.length != 0) && (!sessionManager.isSessionValid(token[0]))) {
            System.out.println("session expired");
            return;
        }
        try {
            List<String> lines;
            File file = new File("project.config");
            lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
            List<String> newLines = new ArrayList<String>();
            for (String line : lines) {
                if (line.contains(parameter)) {
                    String[] vals = line.split("=");
                    newLines.add(vals[0] + "=" + String.valueOf(value));
                } else {
                    newLines.add(line);
                }
            }
            Files.write(file.toPath(), newLines, Charset.defaultCharset());
            System.out.println("setConfig() invoked");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAuthorized(String username, String password) throws SQLException, NoSuchAlgorithmException {
        ResultSet rs = udb.search(username);
        while (rs.next()) {
//            System.out.println(rs.getString("pw"));
//            System.out.println(rs.getString("salt"));
//            System.out.println(PasswordEncrypter.getEncryptedPassword(password, rs.getString("salt")));
            if (Objects.equals(rs.getString("password"), PasswordEncrypter.getEncryptedPassword(password, rs.getString("salt")))) {
                DBManagerProTwo dbtwo = new DBManagerProTwo();
                ResultSet rt = dbtwo.searchPermission(username);
                Set<String> set = new HashSet<>();
                while (rt.next()) {
                    String[] pers = rt.getString("roleAccess").split(",");
                    set.addAll(Arrays.asList(pers));

                }
                String[] permission = (String[]) set.toArray(new String[set.size()]);
                u.userPermision = permission;
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean isStarted() {
        if (!isServerStarted) {
            System.out.println("\t" + "Printer not started!");
            System.out.println("Please START the printer to see method invocation status" + "\n");
        }
        return isServerStarted;
    }

    private boolean isPermit(String operation) {
        int permit = Arrays.binarySearch(u.userPermision, operation);
        return permit >= 0;
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

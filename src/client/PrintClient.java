package client;

import printServer.IPrintServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;
import java.util.Scanner;
import java.util.UUID;

public class PrintClient {
    public static String username;
    private static Scanner scanner = new Scanner(System.in);
    private static IPrintServer printServer;
    private static UUID token;

    public static void main(String[] args) {
        connectToRemotePrintServer();
        showMenu();
    }

    private static void connectToRemotePrintServer() {
        try {
            FileReader fReader = new FileReader("project.config");
            BufferedReader bReader = new BufferedReader(fReader);
            String host = null, port = null, name = null;
            String line;
            while ((line = bReader.readLine()) != null) {
                if (line.contains("host")) {
                    host = line.split("=")[1];
                }
                if (line.contains("port")) {
                    port = line.split("=")[1];
                }
                if (line.contains("name")) {
                    name = line.split("=")[1];
                }
            }
            printServer = (IPrintServer) Naming.lookup("rmi://" + host + ":" + port + "/" + name);
            fReader.close();
            bReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String authenticateUser() throws Exception {
        System.out.println("Enter UserName");
        String user = scanner.nextLine();
        System.out.println("Enter Password");
        String pass = scanner.nextLine();
        if (printServer.isAuthorized(user, pass)) {
            username = user;
            System.out.println("Authenticated!");
            return user;
        }

        throw new Exception("Invalid credentials!");
    }

    public static void showMenu() {

        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.out.println("\n\n\n\n======= Print Client ==========");
            System.out.println("Select from following options:");
            System.out.println("1 Start the printer");
            System.out.println("2 Stop the printer");
            System.out.println("3 Restart the printer");
            System.out.println("4 Print a file");
            System.out.println("5 Queue");
            System.out.println("6 TopQueue");
            System.out.println("7 Read Configuration");
            System.out.println("8 Set Configuration");
            System.out.println("9 Check status");
            System.out.println("10 Exit");
            System.out.println("11 Change user");
            System.out.println("Select option: ");
            String input = scanner.nextLine();

            try {
                switch (input) {
                    case "1":
                        username = authenticateUser();
                        token = printServer.start(username);
                        if (printServer.isStarted()) System.out.println("Printer has been started");
                        else System.out.println("No permission");
                        break;

                    case "2":

                        printServer.stop(token);
                        if (printServer.getIsPermission()) {
                        System.out.println("No permission");
                        break;
                    }
                        System.out.println("Printer is stopped");
                        break;

                    case "3":

                        printServer.restart(token);
                        if (printServer.getIsPermission()) {
                            System.out.println("No permission");
                            break;
                        }
                        System.out.println("Printer is restarted");
                        break;

                    case "4":
                        if (printServer.isStarted()) {

                            System.out.println("Enter File Name");
                            System.out.println("(e.g.  'mounika.txt' or 'chandni.doc')");
                            String file = scanner.nextLine();
                            System.out.println("Enter Printer Number");
                            System.out.println("e.g. choose any between '1' or '10'");
                            String printer = scanner.nextLine();

                            printServer.print(file, printer, token);
                            if (printServer.getIsPermission()) {
                                System.out.println("No permission");
                                break;
                            }
                            System.out.println("Successfully Queued...");
                            System.out.println("view Queue status");

                        } else {
                            System.out.println("\t" + "Printer not started!");
                            System.out.println("Please START the printer to see method invocation status" + "\n");
                        }
                        break;

                    case "5":
                        if (printServer.isStarted()) {

                            System.out.println("Enter Printer Number");
                            System.out.println("e.g. choose the same printer number selected previously");
                            String printer1 = scanner.nextLine();
                            System.out.println("JobNumber\t\tFileName\t\tPrinter");
                            System.out.println(printServer.queue(printer1, token));
                            if (printServer.getIsPermission()) {
                                System.out.println("No permission");
                                break;
                            }
                        } else {
                            System.out.println("\t" + "Printer not started!");
                            System.out.println("Please START the printer to see method invocation status" + "\n");
                        }
                        break;

                    case "6":
                        if (printServer.isStarted()) {

                            System.out.println("Enter Printer Number");
                            System.out.println("e.g. choose any between '1' or '10'");
                            String printerName = scanner.nextLine();
                            System.out.println("Enter job number");
                            String jobString = scanner.nextLine();
                            printServer.topQueue(printerName, Integer.valueOf(jobString), token);
                            if (printServer.getIsPermission()) {
                                System.out.println("No permission");
                                break;
                            }
                            System.out.println("Job scheduled");
                        } else {
                            System.out.println("\t" + "Printer not started!");
                            System.out.println("Please START the printer to see method invocation status" + "\n");
                        }
                        break;

                    case "7":
                        if (printServer.isStarted()) {
                            if (printServer.getIsPermission()) {
                                System.out.println("No permission");
                                break;
                            }
                            System.out.println("Enter parameter");
                            System.out.println("e.g. choose 'port', 'host' or 'name'");
                            String parameter = scanner.nextLine();
                            System.out.println(printServer.readConfig(parameter, token));
                        }else {
                            System.out.println("\t" + "Printer not started!");
                            System.out.println("Please START the printer to see method invocation status" + "\n");
                        }
                        break;

                    case "8":
                        if (printServer.isStarted()) {

                            System.out.println("Enter parameter");
                            System.out.println("e.g. choose 'port', 'host' or 'name'");
                            String param = scanner.nextLine();
                            System.out.println("Enter parameter value");
                            String value = scanner.nextLine();
                            printServer.setConfig(param, value, token);
                            if (printServer.getIsPermission()) {
                                System.out.println("No permission");
                                break;
                            }
                        } else {
                            System.out.println("\t" + "Printer not started!");
                            System.out.println("Please START the printer to see method invocation status" + "\n");
                        }
                        break;

                    case "9":

                        if (printServer.isStarted()) {

                            System.out.println("Enter Printer Number");
                            System.out.println("e.g. choose any between '1' or '10'");
                            String printerName = scanner.nextLine();

                            if (printServer.status(printerName, token)) {

                                System.out.println("Printer Status: " + "ONLINE" + "\n");
                            } else {
                                System.out.println("no permission or session expired");
                            }
                        } else {
                            System.out.println("\t" + "Printer not started!");
                            System.out.println("Please START the printer to see method invocation status" + "\n");
                        }
                        break;

                    case "10":
                        System.exit(0);
                    case "11":
                        if (printServer.isStarted()) {
                            username = authenticateUser();

                        }else {
                            System.out.println("\t" + "Printer not started!");
                            System.out.println("Please START the printer to see method invocation status" + "\n");
                        }
                        break;


                    default:
                        System.out.println("Invalid option selection!");
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}

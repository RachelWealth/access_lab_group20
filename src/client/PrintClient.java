package client;

import accesscontrol.*;
import printServer.IPrintServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;
import java.util.Scanner;

public class PrintServerClient {
    private static Scanner scanner = new Scanner(System.in);
    private static IPrintServer printServer;
    private static User user;

    public static void main(String[] args) throws Exception {
        connectToRemotePrintServer();
        user = authenticateUser();
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
            printServer = (IPrintServer) Naming.lookup("rmi://"+host+":"+port+"/"+name);
            fReader.close();
            bReader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User authenticateUser() throws Exception {
        System.out.println("Enter UserName");
        String user = scanner.nextLine();
        System.out.println("Enter Password");
        String pass = scanner.nextLine();

        User userobj = printServer.isAuthenticated(user, pass);
        if (userobj != null) {
            return userobj;
        }
        throw new Exception("Invalid credentials!");
    }

    public static boolean authorizeUser(Permission.Permissions permission) throws Exception {
        Permission permission1 = printServer.getPermissionByName(permission);
        if (printServer.isAuthorized(user, permission1)) {
            return true;
        }
        throw new Exception("User does not have the permission!");
    }

    public static void printManagerMenu() {
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
    }

    public static void printTechnicianMenu() {
        System.out.println("1 Start the printer");
        System.out.println("2 Stop the printer");
        System.out.println("3 Restart the printer");
        System.out.println("7 Read Configuration");
        System.out.println("8 Set Configuration");
        System.out.println("9 Check status");
        System.out.println("10 Exit");
    }

    public static void printPowerUserMenu() {
        System.out.println("3 Restart the printer");
        System.out.println("4 Print a file");
        System.out.println("5 Queue");
        System.out.println("6 TopQueue");
        System.out.println("10 Exit");
    }

    public static void printUserMenu() {
        System.out.println("4 Print a file");
        System.out.println("5 Queue");
        System.out.println("10 Exit");
    }

    public static void showMenu() {

        Role role = user.getRole();

        while (true) {
        	try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            System.out.println("\n\n\n\n======= Print Client ==========");
            System.out.println("Select from following options:");

            switch (role.getName()) {
                case MANAGER:
                    printManagerMenu();
                    break;

                case SERVICE_TECHNICIAN:
                    printTechnicianMenu();
                    break;

                case POWER_USER:
                    printPowerUserMenu();
                    break;

                case ORDINARY_USER:
                    printUserMenu();
                    break;

            }

            System.out.println("Select option: ");
            String input = scanner.nextLine();

            try {
                switch(input) {
                    case "1":
                        if(authorizeUser(Permission.Permissions.START)) {
                            printServer.start();
                            System.out.println("Printer has been started");
                        } else {
                            System.out.println("User is not authorized to perform this action");
                        }
                        break;

                    case "2":
                        if(authorizeUser(Permission.Permissions.STOP)){
                            printServer.stop();
                            System.out.println("Printer is stopped");
                        } else {
                            System.out.println("User is not authorized to perform this action");
                        }
                        break;

                    case "3":
                        if(authorizeUser(Permission.Permissions.RESTART)) {
                            printServer.restart();
                            System.out.println("Printer is restarted");
                        } else {
                            System.out.println("User is not authorized to perform this action");
                        }
                        break;

                    case "4":
                        if(authorizeUser(Permission.Permissions.PRINT)) {
                            System.out.println("Enter File Name");
                            System.out.println("(e.g.  'abc.txt' or 'xyz.doc')");
                            String file = scanner.nextLine();
                            System.out.println("Enter Printer Number");
                            System.out.println("(e.g. choose any between '10' or '20')");
                            String printer = scanner.nextLine();
                            printServer.print(file, printer);
                            System.out.println("...Successfully Placed in Queue");
                            System.out.println("(check QUEUE to see status)");
                        }  else {
                            System.out.println("User is not authorized to perform this action");
                        }
                        break;

                    case "5":
                        if(authorizeUser(Permission.Permissions.QUEUE)) {
                            if (printServer.isStarted()) {
                                System.out.println("Enter Printer Number");
                                System.out.println("(e.g. choose the number previously used in OPTION :: 4)");
                                String printer1 = scanner.nextLine();
                                System.out.println("JobNo\t\tFileName\t\tPrinter");
                                System.out.println(printServer.queue(printer1));
                            }
                        } else {
                            System.out.println("User is not authorized to perform this action");
                        }
                        break;

                    case "6":
                        if(authorizeUser(Permission.Permissions.TOP_QUEUE)) {
                            if (printServer.isStarted()) {
                                System.out.println("Enter Printer Number");
                                System.out.println("(e.g. choose any between '10' or '20')");
                                String printerName = scanner.nextLine();
                                System.out.println("Enter job number");
                                String jobString = scanner.nextLine();
                                printServer.topQueue(printerName, Integer.valueOf(jobString));
                                System.out.println("Job scheduled");
                            }
                        }  else {
                            System.out.println("User is not authorized to perform this action");
                        }
                        break;

                    case "7":
                        if(authorizeUser(Permission.Permissions.READ_CONFIG)) {
                            System.out.println("Enter parameter");
                            System.out.println("(e.g. choose 'port', 'host' or 'name')");
                            String parameter = scanner.nextLine();
                            System.out.println(printServer.readConfig(parameter));
                        } else {
                            System.out.println("User is not authorized to perform this action");
                        }
                        break;

                    case "8":
                        if(authorizeUser(Permission.Permissions.WRITE_CONFIG)) {
                            System.out.println("Enter parameter");
                            System.out.println("(e.g. choose 'port', 'host' or 'name')");
                            String param = scanner.nextLine();
                            System.out.println("Enter parameter value");
                            String value = scanner.nextLine();
                            printServer.setConfig(param, value);
                        }
                        break;

                    case "9":
                        if(authorizeUser(Permission.Permissions.STATUS)) {
                            //                        if(printServer.isStarted()) {
                            System.out.println("Enter Printer Number");
                            System.out.println("(e.g. choose any between '10' or '20')");
                            String printerName = scanner.nextLine();

                            if (printServer.status(printerName)) {
                                System.out.println("Printer Status: " + "ONLINE");
                            } else {
                                System.out.println("Printer Status: " + "OFFLINE");
                            }
                            //                        }
                        } else {
                            System.out.println("User is not authorized to perform this action");
                        }
                        break;

                    case "10":
                        System.exit(0);

                    default:
                        System.out.println("Invalid option selection!");
                }
            } catch(Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}

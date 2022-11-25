package printServer;

import utils.DBManagerProOne;
import utils.DBManagerProTwo;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Scanner;

public class redeploy_ACL {
    DBManagerProOne dbhelper = new DBManagerProOne();
    private static final Scanner scanner = new Scanner(System.in);

    public String inputPermission(){
        System.out.println("Please choose its permission(like 1,2,3...)");
        System.out.println("print:1");
        System.out.println("queue:2");
        System.out.println("topQueue:3");
        System.out.println("start:4");
        System.out.println("stop:5");
        System.out.println("restart:6");
        System.out.println("status:7");
        System.out.println("readConfig:8");
        System.out.println("setConfig:9");
        return scanner.nextLine();
    }
//    private boolean checkPemission(){
//
//    }

    public String inputName(){
        System.out.println("Please input the name employee:");
        return scanner.nextLine();
    }

    public void deploy() throws SQLException, NoSuchAlgorithmException {
        System.out.println("Please choose your operation:(use 1,2,3...)");
        System.out.println("add");
        System.out.println("delete");
        System.out.println("replace");
        String ope = scanner.nextLine();
        String name = null;

        switch (ope){
            case "add":
                name=this.inputName();
                String permission = this.inputPermission();
                //TODO check permission legality
                System.out.println("password");
                String pw = scanner.nextLine();
                dbhelper.insertUser(name,pw,permission);
                break;
            case "delete":
                name = this.inputName();
                dbhelper.deleteUser(name);
                break;
            case "replace":
                System.out.println("Please input the old worker name");
                String oldname = scanner.nextLine();
                name = this.inputName();
                dbhelper.workerReplace(oldname,name);
                break;
            default:
                System.out.println("Wrong operation");
                break;
        }

    }
    public static void main(String[] args) throws NoSuchAlgorithmException, SQLException, IOException {
        redeploy_ACL rd = new redeploy_ACL();
        int flag = 1;
        while(flag==1){
            rd.deploy();
            System.out.println("Please input what you want to do(input 1,2):");
            System.out.println("1. continue");
            System.out.println("2. exit");
            String flag0 = scanner.nextLine();
            if((flag0!="1") || (flag0 !="2")) {
                System.out.println("Please input 1 or 2");
            }
            else{
                flag = Integer.parseInt(flag0);
            }
        }
    }

}

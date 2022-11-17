package utils;

import java.io.*;
import java.util.*;

/**
 * This class implement access control list, and use public file to realize the storage of list
 * @author Yingli Duan
 * @version 1.1
 */
public class DBManagerProOne {
    static Dictionary<String, String> dict = new Hashtable<>();
    public DBManagerProOne(){
        dict.put("print","1");
        dict.put("queue","2");
        dict.put("topQueue","3");
        dict.put("start","4");
        dict.put("stop","5");
        dict.put("restart","6");
        dict.put("status","7");
        dict.put("readConfig","8");
        dict.put("setConfig","9");
    }
    public static void initial() throws IOException {
        try{
            FileWriter writer = new FileWriter("permission",false);
            writer.write("Alice:1,2,3,4,5,6,7,8,9\n");
            writer.write("Bob:4,5,6,7,8,9\n");
            writer.write("Cecilia:1,2,3,6\n");
            writer.write("David:1,2\n");
            writer.write("Erica:1,2\n");
            writer.write("Fres:1,2\n");
            writer.write("George:1,2\n");
            writer.flush();
            writer.close();
        }catch (Exception ignored){
            System.out.println("write failed");
        }
    }

    /**
     *
     * @param name: username
     * @param operator: function name
     * @return boolean
     * @throws IOException
     */
    public boolean searchPermission(String name, String operator) throws IOException {
        String ope = dict.get(operator);
        File file = new File("permission");
        BufferedReader reader = new BufferedReader(new FileReader("permission"));
        String str;
        while((str = reader.readLine())!=null){
            if(str.contains(name)){
                 String[] strs = str.split(":");
                 String[] opes = strs[1].split(",");
                 for(String op : opes){
                     if(Objects.equals(op, ope)){
                         return true;
                     }
                 }
            }
        }
        return false;
    }
}






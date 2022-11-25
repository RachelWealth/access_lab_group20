# access_lab_group20

step of running programme:
1. Perform functions of print server:
> RunServer -> PrintClient

2. Add, delete or change the user of a position:
> redeploy_RBAC
> redeploy_ACL

username, password, role

        dbhelper.insertRole("administrator","1,2,3,4,5,6,7,8,9");
        dbhelper.insertRole("service","1,2,4,5,6");
        dbhelper.insertRole("technician","1,2,7,8,9");
        dbhelper.insertRole("pUser","1,2,3,6");
        dbhelper.insertRole("user","1,2");

        dbhelper.insertUser("Alice","000","administrator");
        dbhelper.insertUser("Bob","001","service,technician");
        dbhelper.insertUser("Cecilia","010","pUser");
        dbhelper.insertUser("David","011","user");
        dbhelper.insertUser("Erica","100",null);
        dbhelper.insertUser("Fres","101","user");
        dbhelper.insertUser("George","110",null);

Permission number:
```
void print(String filename, String printer, UUID token) throws RemoteException; // 1 
String queue(String printer, UUID token) throws RemoteException; // 2 
void topQueue(String printer, int job, UUID token) throws RemoteException; // 3 
UUID start(String user) throws RemoteException; // 4
void stop(UUID token) throws RemoteException; // 5 
void restart(UUID token) throws RemoteException; // 6 
boolean status(String printer, UUID token)  throws RemoteException; // 7 
String readConfig(String parameter, UUID... token) throws RemoteException; // 8 
void setConfig(String parameter, String value, UUID... token) throws RemoteException; //
```

### access policy file:
ACL : \access_lab_group20\permission

RBAC: \access_lab_group20\src\db\access.db


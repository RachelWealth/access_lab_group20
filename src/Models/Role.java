/***
 * This is the model of a role
 * @author Yingli Duan
 * @version 1.1
 */
package Models;

public class Role {
    public String role;
    public String access;
    Role(String role, String access){
        this.role = role;
        this.access = access;
    }
}

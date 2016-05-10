/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Testing.RTI;

/**
 *
 * @author admin
 */
// MODIFIER PRIVATE NOT ALLOWED HERE
// -> CLASSLOADER ALWAYS WILL HAVE ACCESS TO ANY CLASS
public class JavaSecurityIssue {
    private String PRIVATE_KEY = "UNINITIALIZED";
    private JavaSecurityIssue(){
        PRIVATE_KEY = "##Y0U_1337_H@Xx0r!##";
    }
    public String getKey(){
        return "NOT ALLOWED";
    }
    
}

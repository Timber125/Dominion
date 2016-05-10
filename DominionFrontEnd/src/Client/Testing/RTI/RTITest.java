/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Testing.RTI;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class RTITest {
    public static void main (String[] args) throws IllegalArgumentException, InvocationTargetException{
        String classToLoad = "DummyObject";
        NonStaticRTIer rti = new NonStaticRTIer();
        DummyObject o = rti.getInstance("Client.Testing.RTI." + classToLoad, DummyObject.class);
        //System.out.println(o.key);
        
        // Funny cybersecurity issue: 
        // Check the JavaSecurityIssue class
        //JavaSecurityIssue oops = rti.getInstance("Client.Testing.RTI." + "JavaSecurityIssue", JavaSecurityIssue.class);
        Class oops = null;
        try {
            oops = RTITest.class.getClassLoader().loadClass("Client.Testing.RTI.JavaSecurityIssue");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RTITest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Create oops via constructor
        Object jsi;
        try {
            jsi = (Object) oops.getClass().getConstructors()[0].newInstance();
            
        } catch (InstantiationException ex) {
            Logger.getLogger(RTITest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(RTITest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Field privatekey = oops.getClass().getFields()[0];
        // get the first field that is declared in the class
        // Then try to extract the bytes that represent the value of this field in a new object
        try {
            Object value = privatekey.get(oops);
            String extracted = (String) value;
            System.out.println("Extracted private key:[" + extracted + "]");
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(RTITest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(RTITest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

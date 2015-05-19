/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servicetransfertfichier.receive;

import java.io.*;
import java.net.*;
import java.util.logging.*;

/**
 *
 * @author p1412480
 */
public class Receive {
    
    private DatagramSocket ds;
    private DatagramPacket dps, dpr;
    private InetAddress ad;
    private int port = 69;
    private FileInputStream f;
      
    public int receiveFile(String localName, String remoteName, String address){
        
        try {
            ad = InetAddress.getByName(address);
            ds = new DatagramSocket();
            f = new FileInputStream(localName);
        }
        catch (UnknownHostException ex)     {Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);}
        catch (SocketException ex)          {Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);}
        catch (FileNotFoundException ex)    {Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);}
        
        
        return 0;
    }
    
}

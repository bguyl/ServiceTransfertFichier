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
    private boolean isReceiving;
      
    public int receiveFile(String localName, String remoteName, String address){
        
        try {
            ad = InetAddress.getByName(address);
            ds = new DatagramSocket();
            f = new FileInputStream(localName);
        }
        catch (UnknownHostException ex)     {Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);}
        catch (SocketException ex)          {Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);}
        catch (FileNotFoundException ex)    {Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);}
        
        /*Header*/
        byte[] msg = new byte[50];
        msg[0] = 0x00;
        msg[1] = 0x01;
        int lgt = remoteName.getBytes().length;
        for(int i = 2; i < lgt+2; i++)
            msg[i] = remoteName.getBytes()[i-2];
        msg[2+lgt] = 0x00;
        int lgtMode = "octet".getBytes().length;
        for(int i = 3+lgt; i < lgtMode+3+lgt; i++)
            msg[i] = "octet".getBytes()[i-2];
        msg[lgtMode+3+lgt] = 0x00;
        /*Fin header*/
        
        dps = new DatagramPacket(msg, msg.length, ad, port);
        byte[] data = new byte[516];
        dpr = new DatagramPacket(data, data.length);
        
        try {
            ds.send(dps);
            ds.receive(dpr);
        }
        catch (IOException ex) {Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);}
        
        int nbBlock = 0;
        while(isReceiving){
            if(data[0] != 0x00 || data[1] != 0x03){
                System.err.println("Echec de la réception");
                return 1;
            }
//            if(nbBlock != data)

            
        }
        
        
        return 0;
    }
    
}

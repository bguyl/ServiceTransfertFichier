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
    private FileOutputStream f;
    private boolean isReceiving = true;
      
    public int receiveFile(String localName, String remoteName, String address){
        
        try {
            ad = InetAddress.getByName(address);
            ds = new DatagramSocket();
            f = new FileOutputStream(localName);
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
        for(int i = 3+lgt; i < lgtMode+3+lgt ; i++)
            msg[i] = "octet".getBytes()[i-lgt-3];
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
        
        int nbBlock = 1;
       
        while(isReceiving){
            
            port = dpr.getPort();
            
            if(data[0] != 0x00 || data[1] != 0x03){
                System.err.println("Echec de la réception");
                return 1;
            }
            
            //TODO : Gérer les n°blocks au delà de 127. Num bloc est seulement data[3], data[2] toujours vide !
            else if(nbBlock != data[3]){
                System.out.println("Bloc déjà reçu :"+nbBlock+" data :"+data[3]);
                sendAck(data[3]);
                data = new byte[516];
                dpr = new DatagramPacket(data, data.length);
                try {ds.receive(dpr);}
                catch (IOException ex) {
                    Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            else {
                //TODO : Lever l'entête
                try { f.write(dpr.getData()); }
                catch (IOException ex) {
                    Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(data[3]);
                sendAck(data[3]);
                nbBlock = data[3]+1;
                System.out.println(dpr.getLength());
                if (dpr.getLength() < 516){
                    isReceiving = false;
                    System.out.println("Bloc d'arrêt");
                }
                else{
                    System.out.println("Bloc 512 : On continue");
                    data = new byte[516];
                    dpr = new DatagramPacket(data, data.length);
                    try {ds.receive(dpr);}
                    catch (IOException ex) {
                        Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
        }
        
        return 0;
    }
    
    public void sendAck(int numBlock){
        byte[] ack = new byte[4];
        ack[0] = 0x00;
        ack[1] = 0x04;
        ack[2] = 0x00;
        ack[3] = (byte)numBlock;
        
        dps = new DatagramPacket(ack, ack.length, ad, port);
        
        try { ds.send(dps); }
        catch (IOException ex) {
            Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

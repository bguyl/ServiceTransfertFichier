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
    byte nb2, nb3;
      
    public int receiveFile(String localName, String remoteName, String address){
        
        try {
            ad = InetAddress.getByName(address);
            ds = new DatagramSocket();
            f = new FileOutputStream(localName);
        }
        catch (UnknownHostException ex)     {System.out.println("Hôte injoignable"); return -3;}
        catch (SocketException ex)          {System.out.println("Port déjà utilisé"); return -2;}
        catch (FileNotFoundException ex)    {System.out.println("Fichier introuvable"); return -1;}
        
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
        catch (IOException ex) { System.out.println("Echec de la connexion"); return -1;}
        
        nb2 = 0; nb3 = 1;
       
        while(isReceiving){
            
            port = dpr.getPort();
            
            if(data[0] != 0x00 || data[1] != 0x03){
                System.err.println("Echec de la réception, code d'erreur :"+data[1]);
                return 1;
            }

            else if(nb2 != data[2] || nb3 != data[3]){
                System.out.println("Bloc déjà reçu :"+nb2+" "+nb3+" data :"+data[2]+";"+data[3]);
                sendAck(nb2, nb3);
                data = new byte[516];
                dpr = new DatagramPacket(data, data.length);
                try {ds.receive(dpr);}
                catch (IOException ex) { System.out.println("Echec de la connexion"); return -1;}
            }
            
            else {
                try { f.write(dpr.getData(), 4, dpr.getData().length - 4); }
                catch (IOException ex) { System.out.println("Echec lors de l'écriture du fichier"); return -5;}
                sendAck(nb2, nb3);
                incrementNbBlock();
                if (dpr.getLength() < 516){
                    isReceiving = false;
                    System.out.println("Reçu !");
                }
                else{
                    data = new byte[516];
                    dpr = new DatagramPacket(data, data.length);
                    try {ds.receive(dpr);}
                    catch (IOException ex) { System.out.println("Echec de la connexion"); return -1;}
                }
            }
        }
        try { f.close(); } catch (IOException e) { System.out.println("Echec lors de la fermeture du fichier"); return -5; }
        ds.close();
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
        catch (IOException ex) { System.out.println("Echec de la connexion");}
    }

    public void sendAck(byte a, byte b){
        byte[] ack = new byte[4];
        ack[0] = 0x00;
        ack[1] = 0x04;
        ack[2] = a;
        ack[3] = b;

        dps = new DatagramPacket(ack, ack.length, ad, port);

        try { ds.send(dps); }
        catch (IOException ex) { System.out.println("Echec de la connexion");}
    }

    public void incrementNbBlock(){
        if(nb3 == -1) {
            nb2++;
            nb3 = 0;
        }
        else
            nb3++;
    }
}

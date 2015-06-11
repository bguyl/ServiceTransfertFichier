/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servicetransfertfichier;

import servicetransfertfichier.Send.SendFile;

/**
 *
 * @author p1412480
 */
public class ServiceTransfertFichier {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SendFile Envoi;
        Envoi = new SendFile(); 
        int n = Envoi.sendFile("url.jpg", "134.214.117.86");
        System.out.println(n);
    }
    
}


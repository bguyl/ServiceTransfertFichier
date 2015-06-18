/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servicetransfertfichier.send;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 *
 * @author p1206264
 */
public class Send {
    
    public Send(){
        System.out.println("Construction du Send");
    }
    
    public int sendFile(String nomFichier, String Adresse){
        
        try{
            int j = 0;
            int premierbyte = 0;
            int deuxiemebyte =0;
            FileInputStream fichier = new FileInputStream(new File(nomFichier));

            DatagramSocket dS = new DatagramSocket(1151);
            InetAddress ad = InetAddress.getByName(Adresse);
                       
            /*********************Création du WRQ*****************************/
            byte[] Data = new byte[512];
            int i;
            Data[0] = 0;
            Data[1] = 2;
            
            for(i=0;i<nomFichier.length();i++){
                Data[2+i] = nomFichier.getBytes()[i];
            }
            
            //On met un octet à 0
            Data[2+nomFichier.length()]=0;

            String mode = "octet";
            for(i=0;i<mode.length();i++){
                Data[2+nomFichier.length()+1+i] = mode.getBytes()[i];
            }


            Data[2+nomFichier.length()+mode.length()+1] = 0;

            
            DatagramPacket dP = new DatagramPacket(Data, Data.length, ad, 69);
            dS.send(dP);
            
            //Reception de l'ACK
            byte[] Data2 = new byte[512];
            DatagramPacket dPr = new DatagramPacket(Data2,Data2.length);
            dS.receive(dPr);
            
            if(Data[1] == 5){
                System.out.println("Erreur serveur");
                return 1;
            }
            
            int n;
            int numBloc = 1;
            byte[] Message = new byte[512];
            while((n=fichier.read(Message)) >=0){
                
                byte[] Data3 = new byte[516];
                Data3[0]=0;
                Data3[1]=3;
                if(numBloc>255){
                    premierbyte = numBloc/256;
                    deuxiemebyte = numBloc-premierbyte*256;
                    Data3[2]=(byte)premierbyte;
                    Data3[3]=(byte)deuxiemebyte;
                    System.out.println("Envoi du packet : " + numBloc + " = " + Data3[2] + " + " + Data3[3]);
                }else{
                    premierbyte = 0;
                    deuxiemebyte = numBloc;
                    Data3[2]=0;
                    Data3[3]=(byte)numBloc;
                    System.out.println("Envoi du packet : " + numBloc + " = " + Data3[2] + " + " + Data3[3]);
                }
                /*Data3[2]=0;
                Data3[3]=1;*/
                for(j=0;j<Message.length;j++){
                    Data3[4+j] = Message[j];
                }
                dP = new DatagramPacket(Data3, n+4, ad, dPr.getPort());
                
                do{
                    dS.send(dP);
                    dS.receive(dPr);
                    //System.out.println(dPr.getData()[0] +"/"+ dPr.getData()[1] +"/"+ dPr.getData()[2] +"/"+ dPr.getData()[3]);
                }while(dPr.getData()[0] != 0 || dPr.getData()[1] != 4 || dPr.getData()[2] != (byte)premierbyte || dPr.getData()[3] != (byte)deuxiemebyte);
                numBloc ++;
            }
            
            if(n == 512){ //Si le dernier packet est de longueur 512
                byte[] DataFin = new byte[516];
                DataFin[0]=0;
                DataFin[1]=3;
                deuxiemebyte++;
                if(deuxiemebyte == 0){
                    premierbyte++;
                }
                dP = new DatagramPacket(DataFin, 4, ad, dPr.getPort());
                do{
                    dS.send(dP);
                    dS.receive(dPr);
                }while(dPr.getData()[0] != 0 || dPr.getData()[1] != 4 || dPr.getData()[2] != (byte)premierbyte || dPr.getData()[3] != (byte)deuxiemebyte);
            }
            
            fichier.close();
            dS.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Fichier non trouvé");
            return -1;
        }
        catch(SocketException e){
            System.out.println("Port déjà utilisé");
            return -2;
        }
        catch(UnknownHostException e){
            System.out.println("Serveur distant ne répond pas");
            return -3;
        }
        catch(IOException e){
            System.out.println("Erreur lors de la lecture du fichier");
            return -4;
        }

        return 0;
    }
}

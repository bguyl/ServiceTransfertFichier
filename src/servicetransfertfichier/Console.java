package servicetransfertfichier;

import servicetransfertfichier.receive.Receive;
import servicetransfertfichier.send.Send;

import java.util.Scanner;

/**
 * Created by Guyl.B on 08/06/15.
 */

public class Console {

    private static String command;
    private static Scanner scanner = new Scanner(System.in);
    private static boolean isRunning = true;
    private static String[] args;
    private static Send send;
    private static Receive receive;

    public static void run(){

        send = new Send();
        receive = new Receive();

        System.out.println("\n<===== Bienvenue sur STF ! =====>\n");
        onHelp();

        while(isRunning){
            System.out.print("$ ");
            command = scanner.nextLine();
            if      (command.matches("quit(.*)"))   { onQuit(); }
            else if (command.matches("help(.*)"))   { onHelp(); }
            else if (command.matches("put(.*)"))    { onPut(); }
            else if (command.matches("get(.*)"))    { onGet(); }
            else { System.out.println("Commande non reconnue"); }
        }
    }

    private static void onGet(){
        args = command.split(" ");
        if(args.length < 4) {
            System.out.print("Erreur: Trop peu d'arguments\n" +
                    "Requis: 3 - Passés: " + (args.length - 1) + "\n" +
                    "Usage : get <addresse serveur> <fichier distant> <fichier local>\n");
        }
        //int n = receive.receiveFile(args[1], args[2], args[3]);
    }

    private static void onPut(){
        args = command.split(" ");
        if(args.length < 3) {
            System.out.print("Erreur: Trop peu d'arguments\n" +
                    "Requis: 2 - Passés: " + (args.length - 1) + "\n" +
                    "Usage : put <addresse serveur> <fichier local>\n");
        }
        int n = send.sendFile(args[2], args[1]);
    }

    private static void onHelp(){
        System.out.print("get : Récupère le fichier demandé\n"+
            "put : Envois le fichier local sur le serveur\n"+
            "quit : Ferme le programme\n"+
            "help : Liste les commandes du programme\n");
    }


    private static void onQuit(){ isRunning = false; }

}

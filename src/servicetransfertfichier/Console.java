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
        while(isRunning){
            command = scanner.nextLine();

            if      (command.equals("quit")){ onQuit(command); }
            else if(command.matches("put *")){ onPut(command); }
            else if(command.matches("get *")){ onGet(command); }
            else{ System.out.println("Commande non reconnue"); }
        }
    }

    private static void onGet(String cmd){
        System.out.println("Let's get !");
        args = command.split(" ");
        System.out.println("Let's split !");

        for(int i = 0; i < args.length; i++)
            System.out.println(args[i]);

        int n = receive.receiveFile(args[1], args[2], "localhost");
    }

    private static void onPut(String cmd){
        System.out.println("Let's put !");
        args = cmd.split(" ");
        System.out.println("Let's split !");

        for(int i = 0; i < args.length; i++)
            System.out.println(args[i]);

        int n = send.sendFile(args[1], "192.168.1.42");
        System.out.println(n);
    }

    private static void onQuit(String cmd){
        isRunning = false;
    }

}

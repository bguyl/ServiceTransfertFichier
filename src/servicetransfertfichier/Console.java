package servicetransfertfichier;

import java.util.Scanner;
import java.util.regex.*;

/**
 * Created by Guyl.B on 08/06/15.
 */

public class Console {

    private static String command;;
    private static Scanner scanner = new Scanner(System.in);
    private static boolean isRunning = true;
    private static String[] args;

    public static void run(){
        while(isRunning){
            command = scanner.nextLine();

            if(command.equals("quit")){
                isRunning = false;
            }
            else if(command.matches("put *")){
                command.split(" ");
                System.out.println("Let's put !");
            }
            else if(command.matches("get *")){
                command.split(" ");

                System.out.println("Let's get !");
            }
        }
    }

}

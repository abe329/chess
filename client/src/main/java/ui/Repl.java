package ui;

import java.util.Scanner;
import static java.awt.Color.BLUE;
import model.*;

public class Repl {
    private Client client;


    public Repl(String serverUrl) {
        this.client = new ExternalClient(serverUrl);
    }

    public void run() {
        System.out.println("\u2003 Welcome to 240 Chess. Type Help to get started. \u2003");
        // System.out.print(client1.help());
        Scanner scanner = new Scanner(System.in);

        // var result = "";
        while (true) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                var transition = client.eval(line);

                System.out.print(transition.message());

                if (transition.nextClient() != null) { client = transition.nextClient(); }

                if (transition instanceof QuitTransition) { break; }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}

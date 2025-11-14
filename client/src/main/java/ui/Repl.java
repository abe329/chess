package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;
import model.*;

// Still need to connect this to main I think

public class Repl {
    private Client client;

    public Repl(String serverUrl) {
        this.client = new ExternalClient(serverUrl);
    }

    public void run() {
        System.out.println("♖ Welcome to Abe's Chess Project! Type \"help\" to get started. ♖");
        Scanner scanner = new Scanner(System.in);

        // var result = "";
        while (true) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                var transition = client.eval(line);
                System.out.print(transition.message());

                if (transition.nextClient() != null) { client = transition.nextClient(); }
                if (transition.nextClient() instanceof QuitClient) { break; }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN); //Figured out how to use EscapeSequences here
    }
}

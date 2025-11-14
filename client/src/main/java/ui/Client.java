package ui;

public interface Client {
    ClientStateTransition eval(String input) throws ClientException;
    String help();
}

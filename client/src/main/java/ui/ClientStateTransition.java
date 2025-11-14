package ui;

public record ClientStateTransition(String message, Client nextClient) {
    public static ClientStateTransition stay(String message) {
        return new ClientStateTransition(message, null);
    }

    public static ClientStateTransition switchTo(String message, Client next) {
        return new ClientStateTransition(message, next);
    }

    public static ClientStateTransition quit(String message) {
        return new ClientStateTransition(message, new QuitClient());
    }
}

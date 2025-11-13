package ui;

import com.google.gson.Gson;

public class ClientException extends Exception {

    private int statusCode = 0;

    public ClientException(String message) {
        super(message);
    }

    public ClientException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static ClientException fromJson(String json) {
        try {
            var gson = new Gson();
            var error = gson.fromJson(json, ErrorMessage.class);
            return new ClientException(error.message());
        } catch (Exception ex) {
            return new ClientException("Unknown error from server.");
        }
    }

    public static String fromHttpStatusCode(int status) {
        return switch (status) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 500 -> "Internal Server Error";
            default -> "Unexpected server error (" + status + ")";
        };
    }

    private record ErrorMessage(String message) {}
}


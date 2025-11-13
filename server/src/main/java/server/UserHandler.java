package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.*;
import model.requestsandresults.EmptyResult;
import model.requestsandresults.LoginRequest;
import model.requestsandresults.RegisterRequest;
import model.requestsandresults.UserResult;


public class UserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {
            RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
            UserResult result = userService.register(req);

            var body = gson.toJson(result);
            ctx.status(200);
            ctx.json(body);

        } catch (ServiceException e) {
            ErrorResponse.sendError(ctx, e.getMessage());

        } catch (Exception e) {
            ErrorResponse.sendError(ctx, e.getMessage(), 500);
        }
    }

    public void login(Context ctx) {
        try {
            LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
            UserResult result = userService.login(req);

            var body = gson.toJson(result);
            ctx.status(200);
            ctx.json(body);

        } catch (ServiceException e) {
            ErrorResponse.sendError(ctx, e.getMessage());
        } catch (Exception e) {
            ErrorResponse.sendError(ctx, e.getMessage(), 500);
        }
    }
    public void logout(Context ctx) {
        try {
            // System.out.println("Headers: " + ctx.headerMap());
            String authToken = ctx.header("Authorization");
            // System.out.println("Auth token: " + authToken);
            EmptyResult result = userService.logout(authToken);

            var body = gson.toJson(result);
            ctx.status(200);
            ctx.json(body);

        } catch (ServiceException e) {
            ErrorResponse.sendError(ctx, e.getMessage());
        } catch (Exception e) {
            ErrorResponse.sendError(ctx, e.getMessage(), 500);
        }
    }
}

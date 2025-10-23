package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.AdminService;
import service.requestsandresults.EmptyResult;
import service.ServiceException;

public class AdminHandler {
    private final AdminService adminService;
    private final Gson gson = new Gson();

    public AdminHandler(AdminService adminService) {
        this.adminService = adminService;
    }

    public void clear(Context ctx) {
        try {
            EmptyResult result = adminService.clear();

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

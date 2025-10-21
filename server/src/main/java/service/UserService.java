package service;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService() {
        this.dataAccess = new MemoryDataAccess(); // Probably CCHANGE THIS LATER to database implementation
    }
    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws ServiceException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new ServiceException("Error: bad request");
        }
        try {
            if (dataAccess.getUser(request.username()) != null) {
                throw new ServiceException("Error: already taken");
            }
            UserData user = new UserData(request.username(), request.password(), request.email());
            dataAccess.createUser(user);

            String token = UUID.randomUUID().toString();
            AuthData auth = new AuthData(token, request.username());
            dataAccess.createAuth(auth);

            return new RegisterResult(request.username(), token);
        } catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }
}

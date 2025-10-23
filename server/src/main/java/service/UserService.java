package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.requestsandresults.EmptyResult;
import service.requestsandresults.LoginRequest;
import service.requestsandresults.RegisterRequest;
import service.requestsandresults.UserResult;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService() {
        this.dataAccess = new MemoryDataAccess(); // Probably CCHANGE THIS LATER to database implementation
    }
    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserResult register(RegisterRequest request) throws ServiceException {
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

            return new UserResult(request.username(), token);
        } catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }

    public UserResult login(LoginRequest request) throws ServiceException {
        if (request.username() == null || request.password() == null) {
            throw new ServiceException("Error: bad request");
        }
        try {
            UserData user = dataAccess.getUser(request.username());

            if (user == null) {
                throw new ServiceException("Error: unauthorized");
            }
            if (!user.password().equals(request.password())) {
                throw new ServiceException("Error: unauthorized");
            }

            String token = UUID.randomUUID().toString();
            AuthData auth = new AuthData(token, request.username());
            dataAccess.createAuth(auth);

            return new UserResult(request.username(), token);
        } catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }

    public EmptyResult logout(String authToken) throws ServiceException {
        try {
            AuthData auth = dataAccess.getAuth(authToken);
            // System.out.println("Auth found? " + auth);
            if (auth == null) {
                throw new ServiceException("Error: unauthorized");
            }

            dataAccess.deleteAuth(authToken);
            return new EmptyResult();

        } catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }
}

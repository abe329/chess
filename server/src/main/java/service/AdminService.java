package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.requestsandresults.EmptyResult;

public class AdminService {
    private final DataAccess dataAccess;

    public AdminService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public EmptyResult clear() throws DataAccessException {
        dataAccess.clear();
        return new EmptyResult();
    }
}

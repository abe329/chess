package service;

import dataAccess.DataAccess;
import service.RequestAndResults.*;

public class AdminService {
    private final DataAccess dataAccess;

    public AdminService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public EmptyResult clear() throws ServiceException {
        dataAccess.clear();
        return new EmptyResult();
    }
}

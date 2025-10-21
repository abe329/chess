package dataAccess;

import model.AuthData;
import model.UserData;
// 나중에 optional 라는 도서관 써야할 수도있다

public interface DataAccess {
    void clear();
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuthToken(String authToken) throws DataAccessException;

    // Keep sticking more mini interfaces in to this MEGA interface
}

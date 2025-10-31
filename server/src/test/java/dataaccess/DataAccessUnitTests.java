package dataaccess;

import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessUnitTests {
    @Test
    public void testCreateUser_Positive() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();

        // Ensure table is clear
        dao.clear();
        UserData user = new UserData("abe", "mypassword", "a@gmail.com");
        dao.createUser(user);
        // Retrieve the user from the database
        UserData retrievedUser = dao.getUser("abe"); // implement getUser to fetch UserData by username
        assertNotNull(retrievedUser, "User should exist in database after creation");

        // Check username and email
        assertEquals("abe", retrievedUser.username());
        assertEquals("a@gmail.com", retrievedUser.email());

        // Check that password matches the hashed value
        assertTrue(BCrypt.checkpw("mypassword", retrievedUser.password()),
                "Stored password should match original password when verified");
    }

}

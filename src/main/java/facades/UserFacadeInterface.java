package facades;

import entities.User;
import errorhandling.AuthenticationException;
import java.util.List;

/**
 *
 * @author Camilla
 */
public interface UserFacadeInterface {
        
    public User getVeryfiedUser(String userEmail, String password) throws AuthenticationException;
    
    public User getUserByID(int ID);
    
    public User getUserByEmail(String email);
    
    public List<User> getUsersByPhone(String phone);
    
    public List<User> getUsersByHobby(String HobbyName);
}

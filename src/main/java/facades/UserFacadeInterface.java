package facades;

import entities.User;
import errorhandling.AuthenticationException;
import errorhandling.NotFoundException;
import java.util.List;

/**
 *
 * @author Camilla
 */
public interface UserFacadeInterface {
        
    public User getVeryfiedUser(String userEmail, String password) throws AuthenticationException;
    
    public User getUserByID(int ID)  throws NotFoundException;
    
    public User getUserByEmail(String email)  throws NotFoundException;
    
    public List<User> getUsersByPhone(String phone)  throws NotFoundException;
    
    public List<User> getUsersByHobby(String HobbyName)  throws NotFoundException;
}

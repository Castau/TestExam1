package facades;

import dataTransferObjects.UserDTO;
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
    
    public UserDTO getUserByID(int ID) throws NotFoundException;
    
    public UserDTO getUserByEmail(String email) throws NotFoundException;
    
    public List<UserDTO> getUsersByPhone(String phone) throws NotFoundException;
    
    public List<UserDTO> getUsersByHobby(String HobbyName) throws NotFoundException;
}

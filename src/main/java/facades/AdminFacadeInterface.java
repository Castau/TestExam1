package facades;

import dataTransferObjects.UserDTO;
import entities.Hobby;
import entities.User;
import errorhandling.NotFoundException;

/**
 *
 * @author Camilla
 */
public interface AdminFacadeInterface {
    
    public Hobby addHobby(Hobby hobby);
    
    public Hobby editHobby(int id, String name, String description);
    
    public Hobby deleteHobby(int id) throws NotFoundException;
    
    public UserDTO addUser(User user);
    
    public UserDTO editUser(UserDTO userDto);
    
    public UserDTO deleteUser(int id);
}

package facades;

import entities.Hobby;
import entities.User;
import errorhandling.NotFoundException;

/**
 *
 * @author Camilla
 */
public interface AdminFacadeInterface {
    
    public Hobby addHobby(Hobby hobby);
    
    public Hobby editHobby(Hobby hobby);
    
    public Hobby deleteHobby(int id) throws NotFoundException;
    
    public User addUser(User user);
    
    public User editUser();
    
    public User deleteUser();
}

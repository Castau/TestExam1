package facades;

import entities.Hobby;
import entities.User;

/**
 *
 * @author Camilla
 */
public interface AdminFacadeInterface {
    
    public int addHobby();
    
    public Hobby editHobby();
    
    public int deleteHobby();
    
    public int addUser();
    
    public User editUser();
    
    public int deleteUser();
}

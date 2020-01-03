package facades;

import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import errorhandling.AuthenticationException;
import java.util.List;
import javax.persistence.NoResultException;

/**
 * 
 * @author Camilla
 */
public class UserFacadeImpl implements UserFacadeInterface{

    private static EntityManagerFactory emf;
    private static UserFacadeImpl instance;

    private UserFacadeImpl() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacadeImpl getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacadeImpl();
        }
        return instance;
    }

    @Override
    public User getVeryfiedUser(String userEmail, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.createNamedQuery("User.getByEmail", User.class).setParameter("email", userEmail).getSingleResult();
            if (user == null || !user.verifyPasswordNonHashed(password)) {
                throw new AuthenticationException("Invalid user email or password");
            }
        } catch (NoResultException e) {
            throw new AuthenticationException("Email does not exist in database");

        } finally {
            em.close();
        }
        return user;
    }

    @Override
    public User getUserByID(int ID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User getUserByEmail(String email) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<User> getUsersByPhone(String phone) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<User> getUsersByHobby(String HobbyName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    

}

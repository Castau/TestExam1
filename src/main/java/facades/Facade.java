package facades;

import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import errorhandling.AuthenticationException;
import javax.persistence.NoResultException;

/**
 * @author lam@cphbusiness.dk
 */
public class Facade {

    private static EntityManagerFactory emf;
    private static Facade instance;

    private Facade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static Facade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new Facade();
        }
        return instance;
    }

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

}

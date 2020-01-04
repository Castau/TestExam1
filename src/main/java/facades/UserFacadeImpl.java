package facades;

import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import errorhandling.AuthenticationException;
import errorhandling.NotFoundException;
import java.util.List;
import javax.persistence.NoResultException;

/**
 *
 * @author Camilla
 */
public class UserFacadeImpl implements UserFacadeInterface {

    private static EntityManagerFactory emf;
    private static UserFacadeImpl instance;

    private UserFacadeImpl() {
    }

    public static UserFacadeImpl getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacadeImpl();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
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
    public User getUserByID(int ID) throws NotFoundException {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, ID);
            em.getTransaction().commit();
            if (user != null) {
                return user;
            } else {
                throw new NotFoundException("No person with provided id found");
            }
        } finally {
            em.close();
        }
    }

    @Override
    public User getUserByEmail(String email) throws NotFoundException {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.createNamedQuery("User.getByEmail", User.class).setParameter("email", email).getSingleResult();
            em.getTransaction().commit();
            if (user != null) {
                return user;
            } else {
                throw new NotFoundException("No person with provided email found");
            }
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> getUsersByPhone(String phone) throws NotFoundException {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<User> users = em.createNamedQuery("User.getByPhone", User.class).setParameter("phone", phone).getResultList();
            em.getTransaction().commit();
            if (users != null && !users.isEmpty()) {
                return users;
            } else {
                throw new NotFoundException("No person with provided phone number found");
            }
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> getUsersByHobby(String HobbyName) throws NotFoundException {
         EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<User> users = em.createNamedQuery("User.getByHobby", User.class).setParameter("name", HobbyName).getResultList();
            em.getTransaction().commit();
            if (users != null && !users.isEmpty()) {
                return users;
            } else {
                throw new NotFoundException("No person with provided hobby found");
            }
        } finally {
            em.close();
        }
    }
}

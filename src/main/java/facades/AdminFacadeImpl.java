package facades;

import entities.Hobby;
import entities.User;
import errorhandling.NotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Camilla
 */
public class AdminFacadeImpl implements AdminFacadeInterface {

    private static EntityManagerFactory emf;
    private static AdminFacadeImpl instance;

    private AdminFacadeImpl() {
    }

    public static AdminFacadeImpl getAdminFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new AdminFacadeImpl();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Hobby addHobby(Hobby hobby) {
        if (hobby != null && hobby.getHobbyName() != null && !hobby.getHobbyName().isEmpty() && hobby.getHobbyDescription() != null && !hobby.getHobbyDescription().isEmpty() ) {
            EntityManager em = getEntityManager();
            try {
                em.getTransaction().begin();
                em.merge(hobby);
                em.getTransaction().commit();
                return hobby;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Rollback of transaction addHobby");
            } finally {
                em.close();
            }
        } else {
            throw new IllegalArgumentException("Input error");
        }
    }

    @Override
    public Hobby editHobby(Hobby hobby) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(hobby);
            em.getTransaction().commit();
            return hobby;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new IllegalArgumentException("Rollback of transaction addHobby");
        } finally {
            em.close();
        }
    }

    @Override
    public Hobby deleteHobby(int id) throws NotFoundException {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Hobby hobby = em.find(Hobby.class, id);
            em.remove(hobby);
            em.getTransaction().commit();
            return hobby;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new NotFoundException("Could not delete, provided id does not exist");
        } finally {
            em.close();
        }
    }

    @Override
    public User addUser(User user) {
        if (user != null && user.getHobbies() != null && !user.getHobbies().isEmpty() 
                && user.getAddress() != null && user.getUserEmail() != null 
                && !user.getUserEmail().isEmpty() && user.getUserFirstName() != null 
                && !user.getUserFirstName().isEmpty() && user.getUserLastName() != null 
                && !user.getUserLastName().isEmpty() && user.getUserPass() != null 
                && !user.getUserPass().isEmpty() && user.getUserPhone() != null 
                && !user.getUserPhone().isEmpty()) {
            EntityManager em = getEntityManager();
            try {
                em.getTransaction().begin();
                em.merge(user);
                em.getTransaction().commit();
                return user;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Rollback of transaction addUser");
            } finally {
                em.close();
            }
        } else {
            throw new IllegalArgumentException("Input error");
        }
    }

    @Override
    public User editUser() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User deleteUser() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

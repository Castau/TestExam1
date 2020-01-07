package facades;

import dataTransferObjects.UserDTO;
import entities.Hobby;
import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import errorhandling.AuthenticationException;
import errorhandling.NotFoundException;
import java.util.ArrayList;
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
    public UserDTO getUserByID(int ID) throws NotFoundException {
        EntityManager em = getEntityManager();
        try {
            User user = em.find(User.class, ID);
            if (user != null) {
                return new UserDTO(user);
            } else {
                throw new NotFoundException("No person with provided id found");
            }
        } finally {
            em.close();
        }
    }

    @Override
    public UserDTO getUserByEmail(String email) throws NotFoundException {
        EntityManager em = getEntityManager();
        try {
            User user = em.createNamedQuery("User.getByEmail", User.class).setParameter("email", email).getSingleResult();
            if (user != null) {
                return new UserDTO(user);
            } else {
                throw new NotFoundException("No person with provided email found");
            }
        } finally {
            em.close();
        }
    }

    @Override
    public List<UserDTO> getUsersByPhone(String phone) throws NotFoundException {
        EntityManager em = getEntityManager();
        try {
            List<User> users = em.createNamedQuery("User.getByPhone", User.class).setParameter("phone", phone).getResultList();
            if (users != null && !users.isEmpty()) {
                List<UserDTO> userDTOs = new ArrayList<>();
                users.forEach((user) -> {
                    userDTOs.add(new UserDTO(user));
                });
                return userDTOs;
            } else {
                throw new NotFoundException("No person with provided phone number found");
            }
        } finally {
            em.close();
        }
    }

    @Override
    public List<UserDTO> getUsersByHobby(String HobbyName) throws NotFoundException {
        EntityManager em = getEntityManager();
        try {
            List<User> users = em.createNamedQuery("User.getByHobby", User.class).setParameter("name", HobbyName).getResultList();
            if (users != null && !users.isEmpty()) {
                List<UserDTO> userDTOs = new ArrayList<>();
                users.forEach((user) -> {
                    userDTOs.add(new UserDTO(user));
                });
                return userDTOs;
            } else {
                throw new NotFoundException("No person with provided hobby found");
            }
        } finally {
            em.close();
        }
    }
    
     @Override
    public List<UserDTO> getAllUsers() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<User> users = em.createNamedQuery("User.all", User.class).getResultList();
            List<UserDTO> userDtos = new ArrayList();
            for(User user : users){
                userDtos.add(new UserDTO(user.getUserID(), user.getUserFirstName(), user.getUserLastName(), user.getUserEmail(), user.getUserPhone()));
            }
            em.getTransaction().commit();
            return userDtos;
        } finally {
            em.close();
        }
    }
}
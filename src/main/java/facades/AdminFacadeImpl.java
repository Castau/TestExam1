package facades;

import dataTransferObjects.UserDTO;
import entities.Address;
import entities.Hobby;
import entities.User;
import errorhandling.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

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
        if (hobby != null && hobby.getHobbyName() != null && !hobby.getHobbyName().isEmpty() && hobby.getHobbyDescription() != null && !hobby.getHobbyDescription().isEmpty()) {
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
    public Hobby editHobby(int id, String name, String description) {
        if (name != null && !name.isEmpty() && description != null && !description.isEmpty()) {
            EntityManager em = getEntityManager();
            try {
                em.getTransaction().begin();
                Hobby hobby = em.find(Hobby.class, id);
                hobby.setHobbyName(name);
                hobby.setHobbyDescription(description);
                em.merge(hobby);
                em.getTransaction().commit();
                return hobby;
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Rollback of transaction editHobby");
            } finally {
                em.close();
            }
        } else {
            throw new IllegalArgumentException("Input error");
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

    private List<Address> checkDublicateAddress(Address address, EntityManager em) {
        List<Address> addressList = em.createNamedQuery("Address.specific", Address.class).setParameter("street", address.getStreet())
                .setParameter("city", address.getCity()).setParameter("zipcode", address.getZipcode()).getResultList();
        return addressList;
    }

    @Override
    public UserDTO addUser(User user) {
        if (user != null && user.getAddress() != null
                && user.getHobbies() != null && !user.getHobbies().isEmpty()
                && user.getUserEmail() != null && !user.getUserEmail().isEmpty()
                && user.getUserFirstName() != null && !user.getUserFirstName().isEmpty()
                && user.getUserLastName() != null && !user.getUserLastName().isEmpty()
                && user.getUserPhone() != null && !user.getUserPhone().isEmpty()
                && user.getUserPass() != null && !user.getUserPass().isEmpty()) {

            EntityManager em = getEntityManager();
            try {
                em.getTransaction().begin();
                List<Address> addressList = checkDublicateAddress(user.getAddress(), em);
                if (!addressList.isEmpty()) {
                    user.setAddress(addressList.get(0));
                } else {
                    em.persist(user.getAddress());
                }
                em.persist(user);
                em.getTransaction().commit();
                return new UserDTO(em.find(User.class, user.getUserID()));
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Rollback of transaction addUser" + e.getMessage());
            } finally {
                em.close();
            }
        } else {
            throw new IllegalArgumentException("Input error");
        }
    }

    @Override
    public UserDTO editUser(UserDTO userDto) {
        if (userDto != null && userDto.getHobbies() != null && !userDto.getHobbies().isEmpty()
                && userDto.getAddress() != null && userDto.getEmail() != null
                && !userDto.getEmail().isEmpty() && userDto.getFirstName() != null
                && !userDto.getFirstName().isEmpty() && userDto.getLastName() != null
                && !userDto.getLastName().isEmpty() && userDto.getPhone() != null
                && !userDto.getPhone().isEmpty()) {

            EntityManager em = getEntityManager();
            try {
                User user = em.find(User.class, userDto.getID());
                List<Address> addressList = checkDublicateAddress(userDto.getAddress(), em);
                user.setUserFirstName(userDto.getFirstName());
                user.setUserLastName(userDto.getLastName());
                user.setUserPhone(userDto.getPhone());
                user.setHobbies(userDto.getHobbies());

                em.getTransaction().begin();
                if (!addressList.isEmpty()) {
                    user.setAddress(addressList.get(0));
                } else {
                    user.setAddress(userDto.getAddress());
                    em.persist(user.getAddress());
                }
                em.merge(user);
                em.getTransaction().commit();
                return new UserDTO(em.find(User.class, userDto.getID()));
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Rollback of transaction editUser" + e);
            } finally {
                em.close();
            }
        } else {
            throw new IllegalArgumentException("Input error");
        }
    }

    @Override
    public UserDTO deleteUser(int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            em.remove(user);
            em.getTransaction().commit();
            return new UserDTO(user.getUserID(), user.getUserFirstName(), user.getUserLastName(), user.getUserEmail(), user.getUserPhone());
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new IllegalArgumentException("Rollback of transaction deleteUser" + e);
        } finally {
            em.close();
        }
    }

}

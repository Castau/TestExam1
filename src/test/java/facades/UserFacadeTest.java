package facades;

import utils.EMF_Creator;
import entities.Role;
import entities.User;
import errorhandling.AuthenticationException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//@Disabled
public class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;

    // USERS
    private static User user;
    private static User admin;
    private static User both;

    // ROLES
    private static Role userRole;
    private static Role adminRole;

    // NON-HASHED PASSWORDS
    private static final String userPass = "user";
    private static final String adminPass = "admin";
    private static final String bothPass = "both";

    public UserFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        // SET UP CONNECTION AND FACADE
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        facade = UserFacade.getUserFacade(emf);

        // SET UP USERS
        user = new User("userFirst", "userLast", "00000000", "user@mail.dk", userPass);
        admin = new User("adminFirst", "adminLast", "11111111", "admin@mail.dk", adminPass);
        both = new User("bothFirst", "bothLast", "22222222", "both@mail.dk", bothPass);
        
        // SET UP ROLES
        userRole = new Role("user");
        adminRole = new Role("admin");

        // ADD ROLES.
        user.addRole(userRole);
        admin.addRole(adminRole);
        both.addRole(userRole);
        both.addRole(adminRole);
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        // ADD ENTITIES TO DATABASE BEFORE EACH TEST 
        em.getTransaction().begin();
        em.persist(userRole);
        em.persist(adminRole);
        em.persist(user);
        em.persist(admin);
        em.persist(both);
        em.getTransaction().commit();
    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetFacade() {
        assertNotNull(facade);
    }

    @Test
    public void testVerifyUsers() throws AuthenticationException {
        User expected = user;
        String userEmail = expected.getUserEmail();
        User actual = facade.getVeryfiedUser(userEmail, userPass);
        assertEquals(expected, actual);

        expected = admin;
        userEmail = expected.getUserEmail();
        actual = facade.getVeryfiedUser(userEmail, adminPass);
        assertEquals(expected, actual);

        expected = both;
        userEmail = expected.getUserEmail();
        actual = facade.getVeryfiedUser(userEmail, bothPass);
        assertEquals(expected, actual);
    }

    @Test
    public void testVerifyUsersWrong() throws Exception {
        Assertions.assertThrows(AuthenticationException.class, () -> {
            facade.getVeryfiedUser("WRONG", "WRONG");
        });
    }
}

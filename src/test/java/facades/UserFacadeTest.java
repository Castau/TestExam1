package facades;

import utils.EMF_Creator;
import entities.Role;
import entities.User;
import errorhandling.AuthenticationException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.WebApplicationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
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
        user = new User("user", userPass);
        admin = new User("admin", adminPass);
        both = new User("both", bothPass);

        // SET UP ROLES
        userRole = new Role("user");
        adminRole = new Role("admin");

        // ADD ROLES.
        user.addRole(userRole);
        admin.addRole(adminRole);
        both.addRole(userRole);
        both.addRole(adminRole);
    }

    @AfterAll
    public static void tearDownClass() {
        // Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    // TODO -- Make sure to change the script below to use YOUR OWN entity class
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

        System.out.println("USER HASH CHECK: " + user.getUserPass());
        System.out.println("ADMIN HASH CHECK: " + admin.getUserPass());
        System.out.println("BOTH HASH CHECK: " + both.getUserPass());
        System.out.println("Created TEST Users");
    }

    @AfterEach
    public void tearDown() {
        // Remove any data after each test was run
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Error in tearDown FacadeExampleTest. Message: " + e.getMessage());
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
        // Arrange 
        User expected = user;
        // Act
        String username = expected.getUserName();
        User actual = facade.getVeryfiedUser(username, userPass);
        // Assert
        assertEquals(expected, actual);

        // Repeat for Admin
        expected = admin;
        // Act
        username = expected.getUserName();
        actual = facade.getVeryfiedUser(username, adminPass);
        // Assert
        assertEquals(expected, actual);

        // Repeat for Both
        expected = both;
        // Act
        username = expected.getUserName();
        actual = facade.getVeryfiedUser(username, bothPass);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testVerifyUsersWrong() throws Exception {
        Assertions.assertThrows(AuthenticationException.class, () -> {
            facade.getVeryfiedUser("WRONG", "WRONG");
        });
    }
}

package facades;

import dataTransferObjects.UserDTO;
import entities.Hobby;
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
public class UserFacadeImplTest {

    private static EntityManagerFactory emf;
    private static UserFacadeInterface facade;

    private static User user;
    private static User admin;
    private static User both;

    private static Role userRole;
    private static Role adminRole;

    private static final String userPass = "user";
    private static final String adminPass = "admin";
    private static final String bothPass = "both";
    
    private static Hobby hobby1;
    private static Hobby hobby2;
    private static Hobby hobby3;
    private static Hobby hobby4;
    
    public UserFacadeImplTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        facade = UserFacadeImpl.getUserFacade(emf);

        user = new User("userFirst", "userLast", "00000000", "user@mail.dk", userPass);
        admin = new User("adminFirst", "adminLast", "11111111", "admin@mail.dk", adminPass);
        both = new User("bothFirst", "bothLast", "22222222", "both@mail.dk", bothPass);
        
        userRole = new Role("user");
        adminRole = new Role("admin");

        user.addRole(userRole);
        admin.addRole(adminRole);
        both.addRole(userRole);
        both.addRole(adminRole);
        
        hobby1 = new Hobby("Fiskeri", "Til havs");
        hobby2 = new Hobby("Litteratur", "Om sand");
        hobby3 = new Hobby("Fyrstedømmer", "I Transylvanien");
        hobby4 = new Hobby("Jagt", "Kun sneglejagt");
        
        user.addHobby(hobby1);
        user.addHobby(hobby2);
        admin.addHobby(hobby2);
        admin.addHobby(hobby3);
        both.addHobby(hobby2);
        both.addHobby(hobby4);
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(userRole);
        em.persist(adminRole);
        em.persist(hobby1);
        em.persist(hobby2);
        em.persist(hobby3);
        em.persist(hobby4);
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
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
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
    
    @Test
    public void testGetUserByID() throws Exception {
        UserDTO expected = new UserDTO(user);
        int userID = expected.getID();
        UserDTO actual = facade.getUserByID(userID);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testGetUserByEmail() throws Exception {
        UserDTO expected = new UserDTO(user);
        String userEmail = expected.getEmail();
        UserDTO actual = facade.getUserByEmail(userEmail);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testGetUsersByPhone() throws Exception {
        UserDTO expected = new UserDTO(user);
        String userPhone = expected.getPhone();
        UserDTO actual = facade.getUsersByPhone(userPhone).get(0);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testGetUsersByHobby() throws Exception {
        UserDTO expected = new UserDTO(user);
        String hobbyName = expected.getHobbies().get(0).getHobbyName();
        UserDTO actual = facade.getUsersByHobby(hobbyName).get(0);
        assertEquals(expected, actual);
    }
}

package facades;

import dataTransferObjects.UserDTO;
import entities.Address;
import entities.Hobby;
import entities.Role;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import utils.EMF_Creator;

/**
 *
 * @author Camilla
 */
public class AdminFacadeImplTest {

    private static EntityManagerFactory emf;
    private static AdminFacadeImpl facade;
    private static HobbyFacadeImpl HOBBYfacade;
    private static UserFacadeImpl USERfacade;

    private static User user_1TesterFirst;
    private static User admin_2TesterFirst;
    private static User both_3TesterFirst;
    private static User user_4TesterFirst;
    private static User admin_5TesterFirst;
    private static User both_6TesterFirst;

    private static Address testaddress1;
    private static Address testaddress2;
    private static Address testaddress3;
    private static Address testaddress4;

    private static Role userRole;
    private static Role adminRole;

    private static final String userPass = "user";
    private static final String adminPass = "admin";
    private static final String bothPass = "both";

    private static Hobby hobby1;
    private static Hobby hobby2;
    private static Hobby hobby3;
    private static Hobby hobby4;

    public AdminFacadeImplTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);
        facade = AdminFacadeImpl.getAdminFacade(emf);
        HOBBYfacade = HobbyFacadeImpl.getHobbyFacade(emf);
        USERfacade = UserFacadeImpl.getUserFacade(emf);

        user_1TesterFirst = new User("1TesterFirst", "1TesterLast", "11111111", "1TesterFirst@mail.dk", userPass);
        admin_2TesterFirst = new User("2TesterFirst", "2TesterLast", "22222222", "2TesterFirst@mail.dk", adminPass);
        both_3TesterFirst = new User("3TesterFirst", "3TesterLast", "33333333", "3TesterFirst@mail.dk", bothPass);
        user_4TesterFirst = new User("4TesterFirst", "4TesterLast", "44444444", "4TesterFirst@mail.dk", userPass);
        admin_5TesterFirst = new User("5TesterFirst", "5TesterLast", "55555555", "5TesterFirst@mail.dk", adminPass);
        both_6TesterFirst = new User("6TesterFirst", "6TesterLast", "66666666", "6TesterFirst@mail.dk", bothPass);

        testaddress1 = new Address("Street 1", "CityOne", "1111");
        testaddress2 = new Address("Street 2", "CityTwo", "1111");
        testaddress3 = new Address("Street 3", "CityOne", "1111");
        testaddress4 = new Address("Street 4", "CityThree", "1111");

        user_1TesterFirst.setAddress(testaddress1);
        admin_2TesterFirst.setAddress(testaddress1);
        both_3TesterFirst.setAddress(testaddress1);
        user_4TesterFirst.setAddress(testaddress2);
        admin_5TesterFirst.setAddress(testaddress3);
        both_6TesterFirst.setAddress(testaddress4);

        userRole = new Role("user");
        adminRole = new Role("admin");

        user_1TesterFirst.addRole(userRole);
        admin_2TesterFirst.addRole(adminRole);
        both_3TesterFirst.addRole(userRole);
        both_3TesterFirst.addRole(adminRole);
        user_4TesterFirst.addRole(userRole);
        admin_5TesterFirst.addRole(adminRole);
        both_6TesterFirst.addRole(userRole);
        both_6TesterFirst.addRole(adminRole);

        hobby1 = new Hobby("Fiskeri", "Til havs");
        hobby2 = new Hobby("Litteratur", "Om sand");
        hobby3 = new Hobby("Fyrstedømmer", "I Transylvanien");
        hobby4 = new Hobby("Jagt", "Kun sneglejagt");

        user_1TesterFirst.addHobby(hobby1);
        admin_2TesterFirst.addHobby(hobby2);
        both_3TesterFirst.addHobby(hobby2);
        user_4TesterFirst.addHobby(hobby3);
        admin_5TesterFirst.addHobby(hobby2);
        both_6TesterFirst.addHobby(hobby4);
        user_1TesterFirst.addHobby(hobby3);
        admin_2TesterFirst.addHobby(hobby4);
        both_3TesterFirst.addHobby(hobby3);
        user_4TesterFirst.addHobby(hobby2);
        admin_5TesterFirst.addHobby(hobby1);
        both_6TesterFirst.addHobby(hobby3);
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
        em.persist(testaddress1);
        em.persist(testaddress2);
        em.persist(testaddress3);
        em.persist(testaddress4);
        em.persist(user_1TesterFirst);
        em.persist(admin_2TesterFirst);
        em.persist(both_3TesterFirst);
        em.persist(user_4TesterFirst);
        em.persist(admin_5TesterFirst);
        em.persist(both_6TesterFirst);
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
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    @Test
    public void testAddHobby() {
        Hobby expResult = new Hobby("ADD HOBBY", "TEST");
        Hobby result = facade.addHobby(expResult);
        assertEquals(expResult, result);
    }

    @Test
    public void testEditHobby() {
        int idToEdit = HOBBYfacade.getAllHobbies().get(0).getHobbyID();
        Hobby expResult = new Hobby();
        expResult.setHobbyName("TESTnameEDIT");
        expResult.setHobbyDescription("TESTdescriptionEDIT");
        Hobby result = facade.editHobby(idToEdit, "TESTnameEDIT", "TESTdescriptionEDIT");
        assertEquals(expResult, result);
    }

    @Test
    public void testDeleteHobby() throws Exception {
        int idToDelete = HOBBYfacade.getAllHobbies().get(0).getHobbyID();
        Hobby expResult = HOBBYfacade.getAllHobbies().get(0);
        Hobby result = facade.deleteHobby(idToDelete);
        assertEquals(expResult, result);
    }

    @Test
    public void testAddUser() {
        User user = new User("AddTestFirst", "AddTestLast", "00000000", "AddTest@mail.dk", "AddTestPass");
        user.setAddress(testaddress1);
        user.addHobby(hobby1);
        UserDTO expResult = new UserDTO(user);
        UserDTO result = facade.addUser(user);
        assertEquals(expResult, result);
    }

    @Test
    public void testAddUserNewAddress() {
        User user = new User("AddTestFirst", "AddTestLast", "00000000", "AddTest@mail.dk", "AddTestPass");
        user.setAddress(new Address("Street 0", "CityZero", "0000"));
        user.addHobby(hobby1);
        UserDTO expResult = new UserDTO(user);
        UserDTO result = facade.addUser(user);
        assertEquals(expResult, result);
    }

    @Test
    public void testAddUserChangeHobby() {
        User user = new User("AddTestFirst", "AddTestLast", "00000000", "AddTest@mail.dk", "AddTestPass");
        user.setAddress(new Address("Street 0", "CityZero", "0000"));
        user.addHobby(hobby2);
        UserDTO expResult = new UserDTO(user);
        UserDTO result = facade.addUser(user);
        assertEquals(expResult, result);
    }

    @Test
    public void testEditUser() {
        List<Hobby> newHobbies = new ArrayList();
        newHobbies.add(hobby3);
        UserDTO expResult = new UserDTO(user_1TesterFirst);
        expResult.setFirstName("editFirst");
        expResult.setLastName("editLast");
        expResult.setPhone("99999999");
        expResult.setAddress(testaddress4);
        expResult.setHobbies(newHobbies);
        UserDTO result = facade.editUser(expResult);
        assertEquals(expResult, result);
    }

    @Test
    public void testEditUserNewAddress() {
        List<Hobby> newHobbies = new ArrayList();
        newHobbies.add(hobby3);
        UserDTO expResult = new UserDTO(user_1TesterFirst);
        expResult.setFirstName("editFirst");
        expResult.setLastName("editLast");
        expResult.setPhone("99999999");
        expResult.setAddress(new Address("EditStreet 4", "EditCityThree", "9999"));
        expResult.setHobbies(newHobbies);
        UserDTO result = facade.editUser(expResult);
        assertEquals(expResult, result);
    }

    @Test
    public void testEditUserDublicateAddress() {
        List<Hobby> newHobbies = new ArrayList();
        newHobbies.add(hobby3);
        UserDTO expResult = new UserDTO(user_1TesterFirst);
        expResult.setFirstName("editFirst");
        expResult.setLastName("editLast");
        expResult.setPhone("99999999");
        expResult.setAddress(new Address("Street 1", "CityOne", "1111"));
        expResult.setHobbies(newHobbies);
        UserDTO result = facade.editUser(expResult);
        assertEquals(expResult, result);
    }

    @Test
    public void testEditUserAddHobby() {
        UserDTO expResult = new UserDTO(user_1TesterFirst);
        expResult.setFirstName("editFirst");
        expResult.setLastName("editLast");
        expResult.setPhone("99999999");
        expResult.setAddress(new Address("Street 1", "CityOne", "1111"));
        expResult.getHobbies().add(hobby4);
        UserDTO result = facade.editUser(expResult);
        assertEquals(expResult, result);
    }

    @Test
    public void testDeleteUser() {
        int idToDelete = USERfacade.getAllUsers().get(0).getID();
        UserDTO expResult = USERfacade.getAllUsers().get(0);
        UserDTO result = facade.deleteUser(idToDelete);
        assertEquals(expResult, result);
    }

}

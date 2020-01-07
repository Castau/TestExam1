package rest;

import dataTransferObjects.UserDTO;
import entities.Address;
import entities.Hobby;
import entities.Role;
import entities.User;
import errorhandling.NotFoundException;
import facades.AdminFacadeImpl;
import facades.HobbyFacadeImpl;
import facades.UserFacadeImpl;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;

@Path("DGS")
public class DGS_Resource {

    // CHANGE TO TEST
    private static EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static AdminFacadeImpl ADMINfacade = AdminFacadeImpl.getAdminFacade(EMF);
    private static HobbyFacadeImpl HOBBYfacade = HobbyFacadeImpl.getHobbyFacade(EMF);
    private static UserFacadeImpl USERfacade = UserFacadeImpl.getUserFacade(EMF);

    @Context
    SecurityContext securityContext;

//    ALL
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {
        EntityManager em = EMF.createEntityManager();
        List<UserDTO> users = USERfacade.getAllUsers();
        return "{\"msg\": \"Amount of users: " + users.size() + "\"}";
    }

//    USERS
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("hello")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("id/{userid}")
    @RolesAllowed("user")
    public UserDTO getUserByID(@PathParam("userid") int userid) throws NotFoundException {
        UserDTO userbyid = USERfacade.getUserByID(userid);
        return userbyid;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("email")
    @RolesAllowed("user")
    public UserDTO getUserByEmail(@PathParam("useremail") String useremail) throws NotFoundException {
        UserDTO userbyemail = USERfacade.getUserByEmail(useremail);
        return userbyemail;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("phone")
    @RolesAllowed("user")
    public List<UserDTO> getUserByPhone(@PathParam("userphone") String userphone) throws NotFoundException {
        List<UserDTO> usersbyphone = USERfacade.getUsersByPhone(userphone);
        return usersbyphone;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("hobby")
    @RolesAllowed("user")
    public List<UserDTO> getUserByHobby(@PathParam("userhobby") String userhobby) throws NotFoundException {
        List<UserDTO> usersbyhobby = USERfacade.getUsersByHobby(userhobby);
        return usersbyhobby;
    }

//    ADMINS
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin/allusers")
    @RolesAllowed("admin")
    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = USERfacade.getAllUsers();
        return users;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("admin/addhobby")
    @RolesAllowed("admin")
    public Hobby addHobby(Hobby hobby) {
        Hobby added = ADMINfacade.addHobby(hobby);
        return added;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("admin/edithobby")
    @RolesAllowed("admin")
    public Hobby editHobby(Hobby hobby) {
        Hobby edited = ADMINfacade.editHobby(hobby.getHobbyID(), hobby.getHobbyName(), hobby.getHobbyDescription());
        return edited;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("admin/deletehobby")
    @RolesAllowed("admin")
    public Hobby deleteHobby(int id) throws NotFoundException {
        Hobby deleted = ADMINfacade.deleteHobby(id);
        return deleted;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("admin/adduser")
    @RolesAllowed("admin")
    public UserDTO addUser(User user) {
        UserDTO added = ADMINfacade.addUser(user);
        return added;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("admin/edituser")
    @RolesAllowed("admin")
    public UserDTO editUser(UserDTO userDto) {
        UserDTO edited = ADMINfacade.editUser(userDto);
        return edited;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("admin/deleteuser")
    @RolesAllowed("admin")
    public UserDTO deleteUser(int id) throws NotFoundException {
        UserDTO deleted = ADMINfacade.deleteUser(id);
        return deleted;
    }

//    BOTH
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("both")
    @RolesAllowed({"admin", "user"})
    public String getMultipleRoles() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin OR user, but not a nobody) User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("hobbies")
    @RolesAllowed({"admin", "user"})
    public List<Hobby> getAllHobbies() {
        List<Hobby> hobbies = HOBBYfacade.getAllHobbies();
        return hobbies;
    }

//    DEPRECATED: DONT THINK THIS IS POSSIBLE. Only accessible by a super-user
//    (that holds both admin & user)
    @Deprecated
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("bothrestricted")
    @RolesAllowed(value = {"admin, user"})
    public String getBothRoles() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (superuser) User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("data")
    public String data() {
        String userPass = "user";
        String adminPass = "admin";
        String bothPass = "both";

        User user_1TesterFirst = new User("1TesterFirst", "1TesterLast", "11111111", "1TesterFirst@mail.dk", userPass);
        User admin_2TesterFirst = new User("2TesterFirst", "2TesterLast", "22222222", "2TesterFirst@mail.dk", adminPass);
        User both_3TesterFirst = new User("3TesterFirst", "3TesterLast", "33333333", "3TesterFirst@mail.dk", bothPass);
        User user_4TesterFirst = new User("4TesterFirst", "4TesterLast", "44444444", "4TesterFirst@mail.dk", userPass);
        User admin_5TesterFirst = new User("5TesterFirst", "5TesterLast", "55555555", "5TesterFirst@mail.dk", adminPass);
        User both_6TesterFirst = new User("6TesterFirst", "6TesterLast", "66666666", "6TesterFirst@mail.dk", bothPass);

        Address testaddress1 = new Address("Street 1", "CityOne", "1111");
        Address testaddress2 = new Address("Street 2", "CityTwo", "1111");
        Address testaddress3 = new Address("Street 3", "CityOne", "1111");
        Address testaddress4 = new Address("Street 4", "CityThree", "1111");

        user_1TesterFirst.setAddress(testaddress1);
        admin_2TesterFirst.setAddress(testaddress1);
        both_3TesterFirst.setAddress(testaddress1);
        user_4TesterFirst.setAddress(testaddress2);
        admin_5TesterFirst.setAddress(testaddress3);
        both_6TesterFirst.setAddress(testaddress4);

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");

        user_1TesterFirst.addRole(userRole);
        admin_2TesterFirst.addRole(adminRole);
        both_3TesterFirst.addRole(userRole);
        both_3TesterFirst.addRole(adminRole);
        user_4TesterFirst.addRole(userRole);
        admin_5TesterFirst.addRole(adminRole);
        both_6TesterFirst.addRole(userRole);
        both_6TesterFirst.addRole(adminRole);

        Hobby hobby1 = new Hobby("Fiskeri", "Til havs");
        Hobby hobby2 = new Hobby("Litteratur", "Om sand");
        Hobby hobby3 = new Hobby("Fyrstedømmer", "I Transylvanien");
        Hobby hobby4 = new Hobby("Jagt", "Kun sneglejagt");

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

        EntityManager em = EMF.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.getTransaction().commit();

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

        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return "{\"msg\": \"Data Created\"}";
    }
}

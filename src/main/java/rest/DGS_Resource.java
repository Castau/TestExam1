package rest;

import dataTransferObjects.UserDTO;
import entities.Hobby;
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
        return "Amount of users [" + users.size() + "]";
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
    @Path("id")
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
}

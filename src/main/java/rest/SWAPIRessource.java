package rest;

import com.google.gson.Gson;
import entities.User;
import facades.ApiFacade;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;

/**
 * @author lam@cphbusiness.dk
 */
@Path("info")
public class SWAPIRessource {

    private ApiFacade facade = new ApiFacade();
    private static EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("five")
    @RolesAllowed({"admin", "user"})
    public Map getDataFrom5Endpoints() throws Exception {
        return facade.allApiData();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            List<User> users = em.createQuery("select user from User user").getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }
    
    /**
     * The idea is here, that this endpoint is only available to registered users (and admins)
     * While the allUsers (/all) endpoint is available to anyone
     * @return 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("both")
    @RolesAllowed({"admin", "user"})
    public String getMultipleRoles() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin OR user, but not a nobody) User: " + thisuser + "\"}";
    }
    
    /**
     * DEPRECATED: DONT THINK THIS IS POSSIBLE.
     * Only accessible by a super-user (that holds both admin & user)
     * @return 
     */
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

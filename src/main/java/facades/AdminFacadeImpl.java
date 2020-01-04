package facades;

import entities.Hobby;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Camilla
 */
public class AdminFacadeImpl implements AdminFacadeInterface{

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
    public int addHobby() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hobby editHobby() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int deleteHobby() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int addUser() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User editUser() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int deleteUser() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

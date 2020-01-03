package facades;

import entities.Hobby;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Camilla
 */
public class HobbyFacadeImpl implements HobbyFacadeInterface {

    private static EntityManagerFactory emf;
    private static HobbyFacadeImpl instance;

    private HobbyFacadeImpl() {
    }

    public static HobbyFacadeImpl getHobbyFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HobbyFacadeImpl();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public List<Hobby> getAllHobbies() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<Hobby> hobbies = em.createNamedQuery("Hobby.all", Hobby.class).getResultList();
            em.getTransaction().commit();
            return hobbies;
        } finally {
            em.close();
        }
    }

}

package facades;

import entities.Hobby;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

/**
 *
 * @author Camilla
 */

public class HobbyFacadeImplTest {
    
    private static EntityManagerFactory emf;
    private static HobbyFacadeImpl facade;
    
    public HobbyFacadeImplTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        facade = HobbyFacadeImpl.getHobbyFacade(emf);
    }
    
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        List<Hobby> hobbies = new ArrayList<>();
        hobbies.add(new Hobby("Fiskeri", "Til havs"));
        hobbies.add(new Hobby("Jagt", "Kun sneglejagt"));
        hobbies.add(new Hobby("Litteratur", "Om sand"));
        hobbies.add(new Hobby("Fyrstedømmer", "I Transylvanien"));

        try {
            for (Hobby h : hobbies) {
                em.getTransaction().begin();
                em.persist(h);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetAllHobbies() {
        List<Hobby> exp = new ArrayList<>();
        exp.add(new Hobby("Fiskeri", "Til havs"));
        exp.add(new Hobby("Jagt", "Kun sneglejagt"));
        exp.add(new Hobby("Litteratur", "Om sand"));
        exp.add(new Hobby("Fyrstedømmer", "I Transylvanien"));
        List<Hobby> res = facade.getAllHobbies();
        assertEquals(exp, res);
    }
    
}

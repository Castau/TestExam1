/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import entities.User;
import errorhandling.NotFoundException;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

/**
 *
 * @author Camilla
 */
public class DELETE {
    public static void main(String[] args) throws NotFoundException {
        EntityManagerFactory _emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.NONE);
        UserFacadeInterface facade = UserFacadeImpl.getUserFacade(_emf);
        User user = facade.getUserByID(65);
        System.out.println("HOBBY BY USER ");
        System.out.println(user.getHobbies().get(0).getHobbyName());
        
        List<User> users = facade.getUsersByHobby("Fiskeri");
        System.out.println("USERs BY HOBBY ");
        System.out.println(users.get(0).getUserFirstName());
       
    }
}

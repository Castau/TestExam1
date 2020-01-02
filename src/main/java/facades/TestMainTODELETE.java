package facades;

import com.google.gson.Gson;

/**
 *
 * @author Camilla
 */
public class TestMainTODELETE {
    public static void main(String[] args) throws Exception {
        ApiFacade facade = new ApiFacade();
        Gson gson = new Gson();
        System.out.println(gson.toJson(facade.allApiData()));
        
        
    }
}

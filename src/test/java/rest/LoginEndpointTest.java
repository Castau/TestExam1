package rest;

import entities.User;
import entities.Role;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

//@Disabled
public class LoginEndpointTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.CREATE);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            User user = new User("userFirst", "userLast", "00000000", "user@mail.dk", "user");
            user.addRole(userRole);
            User admin = new User("adminFirst", "adminLast", "11111111", "admin@mail.dk", "admin");
            admin.addRole(adminRole);
            User both = new User("bothFirst", "bothLast", "22222222", "both@mail.dk", "both");
            both.addRole(userRole);
            both.addRole(adminRole);
            User noRoles = new User("noRolesFirst", "noRolesLast", "33333333", "noRoles@mail.dk", "noRoles"); //no role connected
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(both);
            em.persist(noRoles);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String role, String password) {
        String json = String.format("{userEmail: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                .when().post("/login")
                .then()
                .extract().path("token");
        System.out.println("TOKEN ---> " + securityToken);
    }

    private void logOut() {
        securityToken = null;
    }

    @Test
    public void serverIsRunning() {
        System.out.println("Testing is server UP");
        given().when().get("/info").then().statusCode(200);
    }

    @Test
    public void testFiveSwapi() {
        login("admin@mail.dk", "admin");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/five")
                .then()
                .statusCode(200);
//                .body("people", equalTo("{\"count\":87,\"next\":\"https://swapi.co/api/people/?page=2\",\"previous\":null,\"results\":[{\"name\":\"Luke Skywalker\",\"height\":\"172\",\"mass\":\"77\",\"hair_color\":\"blond\",\"skin_color\":\"fair\",\"eye_color\":\"blue\",\"birth_year\":\"19BBY\",\"gender\":\"male\",\"homeworld\":\"https://swapi.co/api/planets/1/\",\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/3/\",\"https://swapi.co/api/films/1/\",\"https://swapi.co/api/films/7/\"],\"species\":[\"https://swapi.co/api/species/1/\"],\"vehicles\":[\"https://swapi.co/api/vehicles/14/\",\"https://swapi.co/api/vehicles/30/\"],\"starships\":[\"https://swapi.co/api/starships/12/\",\"https://swapi.co/api/starships/22/\"],\"created\":\"2014-12-09T13:50:51.644000Z\",\"edited\":\"2014-12-20T21:17:56.891000Z\",\"url\":\"https://swapi.co/api/people/1/\"},{\"name\":\"C-3PO\",\"height\":\"167\",\"mass\":\"75\",\"hair_color\":\"n/a\",\"skin_color\":\"gold\",\"eye_color\":\"yellow\",\"birth_year\":\"112BBY\",\"gender\":\"n/a\",\"homeworld\":\"https://swapi.co/api/planets/1/\",\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/5/\",\"https://swapi.co/api/films/4/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/3/\",\"https://swapi.co/api/films/1/\"],\"species\":[\"https://swapi.co/api/species/2/\"],\"vehicles\":[],\"starships\":[],\"created\":\"2014-12-10T15:10:51.357000Z\",\"edited\":\"2014-12-20T21:17:50.309000Z\",\"url\":\"https://swapi.co/api/people/2/\"},{\"name\":\"R2-D2\",\"height\":\"96\",\"mass\":\"32\",\"hair_color\":\"n/a\",\"skin_color\":\"white, blue\",\"eye_color\":\"red\",\"birth_year\":\"33BBY\",\"gender\":\"n/a\",\"homeworld\":\"https://swapi.co/api/planets/8/\",\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/5/\",\"https://swapi.co/api/films/4/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/3/\",\"https://swapi.co/api/films/1/\",\"https://swapi.co/api/films/7/\"],\"species\":[\"https://swapi.co/api/species/2/\"],\"vehicles\":[],\"starships\":[],\"created\":\"2014-12-10T15:11:50.376000Z\",\"edited\":\"2014-12-20T21:17:50.311000Z\",\"url\":\"https://swapi.co/api/people/3/\"},{\"name\":\"Darth Vader\",\"height\":\"202\",\"mass\":\"136\",\"hair_color\":\"none\",\"skin_color\":\"white\",\"eye_color\":\"yellow\",\"birth_year\":\"41.9BBY\",\"gender\":\"male\",\"homeworld\":\"https://swapi.co/api/planets/1/\",\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/3/\",\"https://swapi.co/api/films/1/\"],\"species\":[\"https://swapi.co/api/species/1/\"],\"vehicles\":[],\"starships\":[\"https://swapi.co/api/starships/13/\"],\"created\":\"2014-12-10T15:18:20.704000Z\",\"edited\":\"2014-12-20T21:17:50.313000Z\",\"url\":\"https://swapi.co/api/people/4/\"},{\"name\":\"Leia Organa\",\"height\":\"150\",\"mass\":\"49\",\"hair_color\":\"brown\",\"skin_color\":\"light\",\"eye_color\":\"brown\",\"birth_year\":\"19BBY\",\"gender\":\"female\",\"homeworld\":\"https://swapi.co/api/planets/2/\",\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/3/\",\"https://swapi.co/api/films/1/\",\"https://swapi.co/api/films/7/\"],\"species\":[\"https://swapi.co/api/species/1/\"],\"vehicles\":[\"https://swapi.co/api/vehicles/30/\"],\"starships\":[],\"created\":\"2014-12-10T15:20:09.791000Z\",\"edited\":\"2014-12-20T21:17:50.315000Z\",\"url\":\"https://swapi.co/api/people/5/\"},{\"name\":\"Owen Lars\",\"height\":\"178\",\"mass\":\"120\",\"hair_color\":\"brown, grey\",\"skin_color\":\"light\",\"eye_color\":\"blue\",\"birth_year\":\"52BBY\",\"gender\":\"male\",\"homeworld\":\"https://swapi.co/api/planets/1/\",\"films\":[\"https://swapi.co/api/films/5/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/1/\"],\"species\":[\"https://swapi.co/api/species/1/\"],\"vehicles\":[],\"starships\":[],\"created\":\"2014-12-10T15:52:14.024000Z\",\"edited\":\"2014-12-20T21:17:50.317000Z\",\"url\":\"https://swapi.co/api/people/6/\"},{\"name\":\"Beru Whitesun lars\",\"height\":\"165\",\"mass\":\"75\",\"hair_color\":\"brown\",\"skin_color\":\"light\",\"eye_color\":\"blue\",\"birth_year\":\"47BBY\",\"gender\":\"female\",\"homeworld\":\"https://swapi.co/api/planets/1/\",\"films\":[\"https://swapi.co/api/films/5/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/1/\"],\"species\":[\"https://swapi.co/api/species/1/\"],\"vehicles\":[],\"starships\":[],\"created\":\"2014-12-10T15:53:41.121000Z\",\"edited\":\"2014-12-20T21:17:50.319000Z\",\"url\":\"https://swapi.co/api/people/7/\"},{\"name\":\"R5-D4\",\"height\":\"97\",\"mass\":\"32\",\"hair_color\":\"n/a\",\"skin_color\":\"white, red\",\"eye_color\":\"red\",\"birth_year\":\"unknown\",\"gender\":\"n/a\",\"homeworld\":\"https://swapi.co/api/planets/1/\",\"films\":[\"https://swapi.co/api/films/1/\"],\"species\":[\"https://swapi.co/api/species/2/\"],\"vehicles\":[],\"starships\":[],\"created\":\"2014-12-10T15:57:50.959000Z\",\"edited\":\"2014-12-20T21:17:50.321000Z\",\"url\":\"https://swapi.co/api/people/8/\"},{\"name\":\"Biggs Darklighter\",\"height\":\"183\",\"mass\":\"84\",\"hair_color\":\"black\",\"skin_color\":\"light\",\"eye_color\":\"brown\",\"birth_year\":\"24BBY\",\"gender\":\"male\",\"homeworld\":\"https://swapi.co/api/planets/1/\",\"films\":[\"https://swapi.co/api/films/1/\"],\"species\":[\"https://swapi.co/api/species/1/\"],\"vehicles\":[],\"starships\":[\"https://swapi.co/api/starships/12/\"],\"created\":\"2014-12-10T15:59:50.509000Z\",\"edited\":\"2014-12-20T21:17:50.323000Z\",\"url\":\"https://swapi.co/api/people/9/\"},{\"name\":\"Obi-Wan Kenobi\",\"height\":\"182\",\"mass\":\"77\",\"hair_color\":\"auburn, white\",\"skin_color\":\"fair\",\"eye_color\":\"blue-gray\",\"birth_year\":\"57BBY\",\"gender\":\"male\",\"homeworld\":\"https://swapi.co/api/planets/20/\",\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/5/\",\"https://swapi.co/api/films/4/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/3/\",\"https://swapi.co/api/films/1/\"],\"species\":[\"https://swapi.co/api/species/1/\"],\"vehicles\":[\"https://swapi.co/api/vehicles/38/\"],\"starships\":[\"https://swapi.co/api/starships/48/\",\"https://swapi.co/api/starships/59/\",\"https://swapi.co/api/starships/64/\",\"https://swapi.co/api/starships/65/\",\"https://swapi.co/api/starships/74/\"],\"created\":\"2014-12-10T16:16:29.192000Z\",\"edited\":\"2014-12-20T21:17:50.325000Z\",\"url\":\"https://swapi.co/api/people/10/\"}]}"))
//                .and().body("planets", equalTo("{\"count\":61,\"next\":\"https://swapi.co/api/planets/?page=2\",\"previous\":null,\"results\":[{\"name\":\"Alderaan\",\"rotation_period\":\"24\",\"orbital_period\":\"364\",\"diameter\":\"12500\",\"climate\":\"temperate\",\"gravity\":\"1 standard\",\"terrain\":\"grasslands, mountains\",\"surface_water\":\"40\",\"population\":\"2000000000\",\"residents\":[\"https://swapi.co/api/people/5/\",\"https://swapi.co/api/people/68/\",\"https://swapi.co/api/people/81/\"],\"films\":[\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-10T11:35:48.479000Z\",\"edited\":\"2014-12-20T20:58:18.420000Z\",\"url\":\"https://swapi.co/api/planets/2/\"},{\"name\":\"Yavin IV\",\"rotation_period\":\"24\",\"orbital_period\":\"4818\",\"diameter\":\"10200\",\"climate\":\"temperate, tropical\",\"gravity\":\"1 standard\",\"terrain\":\"jungle, rainforests\",\"surface_water\":\"8\",\"population\":\"1000\",\"residents\":[],\"films\":[\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-10T11:37:19.144000Z\",\"edited\":\"2014-12-20T20:58:18.421000Z\",\"url\":\"https://swapi.co/api/planets/3/\"},{\"name\":\"Hoth\",\"rotation_period\":\"23\",\"orbital_period\":\"549\",\"diameter\":\"7200\",\"climate\":\"frozen\",\"gravity\":\"1.1 standard\",\"terrain\":\"tundra, ice caves, mountain ranges\",\"surface_water\":\"100\",\"population\":\"unknown\",\"residents\":[],\"films\":[\"https://swapi.co/api/films/2/\"],\"created\":\"2014-12-10T11:39:13.934000Z\",\"edited\":\"2014-12-20T20:58:18.423000Z\",\"url\":\"https://swapi.co/api/planets/4/\"},{\"name\":\"Dagobah\",\"rotation_period\":\"23\",\"orbital_period\":\"341\",\"diameter\":\"8900\",\"climate\":\"murky\",\"gravity\":\"N/A\",\"terrain\":\"swamp, jungles\",\"surface_water\":\"8\",\"population\":\"unknown\",\"residents\":[],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-10T11:42:22.590000Z\",\"edited\":\"2014-12-20T20:58:18.425000Z\",\"url\":\"https://swapi.co/api/planets/5/\"},{\"name\":\"Bespin\",\"rotation_period\":\"12\",\"orbital_period\":\"5110\",\"diameter\":\"118000\",\"climate\":\"temperate\",\"gravity\":\"1.5 (surface), 1 standard (Cloud City)\",\"terrain\":\"gas giant\",\"surface_water\":\"0\",\"population\":\"6000000\",\"residents\":[\"https://swapi.co/api/people/26/\"],\"films\":[\"https://swapi.co/api/films/2/\"],\"created\":\"2014-12-10T11:43:55.240000Z\",\"edited\":\"2014-12-20T20:58:18.427000Z\",\"url\":\"https://swapi.co/api/planets/6/\"},{\"name\":\"Endor\",\"rotation_period\":\"18\",\"orbital_period\":\"402\",\"diameter\":\"4900\",\"climate\":\"temperate\",\"gravity\":\"0.85 standard\",\"terrain\":\"forests, mountains, lakes\",\"surface_water\":\"8\",\"population\":\"30000000\",\"residents\":[\"https://swapi.co/api/people/30/\"],\"films\":[\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-10T11:50:29.349000Z\",\"edited\":\"2014-12-20T20:58:18.429000Z\",\"url\":\"https://swapi.co/api/planets/7/\"},{\"name\":\"Naboo\",\"rotation_period\":\"26\",\"orbital_period\":\"312\",\"diameter\":\"12120\",\"climate\":\"temperate\",\"gravity\":\"1 standard\",\"terrain\":\"grassy hills, swamps, forests, mountains\",\"surface_water\":\"12\",\"population\":\"4500000000\",\"residents\":[\"https://swapi.co/api/people/3/\",\"https://swapi.co/api/people/21/\",\"https://swapi.co/api/people/36/\",\"https://swapi.co/api/people/37/\",\"https://swapi.co/api/people/38/\",\"https://swapi.co/api/people/39/\",\"https://swapi.co/api/people/42/\",\"https://swapi.co/api/people/60/\",\"https://swapi.co/api/people/61/\",\"https://swapi.co/api/people/66/\",\"https://swapi.co/api/people/35/\"],\"films\":[\"https://swapi.co/api/films/5/\",\"https://swapi.co/api/films/4/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-10T11:52:31.066000Z\",\"edited\":\"2014-12-20T20:58:18.430000Z\",\"url\":\"https://swapi.co/api/planets/8/\"},{\"name\":\"Coruscant\",\"rotation_period\":\"24\",\"orbital_period\":\"368\",\"diameter\":\"12240\",\"climate\":\"temperate\",\"gravity\":\"1 standard\",\"terrain\":\"cityscape, mountains\",\"surface_water\":\"unknown\",\"population\":\"1000000000000\",\"residents\":[\"https://swapi.co/api/people/34/\",\"https://swapi.co/api/people/55/\",\"https://swapi.co/api/people/74/\"],\"films\":[\"https://swapi.co/api/films/5/\",\"https://swapi.co/api/films/4/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-10T11:54:13.921000Z\",\"edited\":\"2014-12-20T20:58:18.432000Z\",\"url\":\"https://swapi.co/api/planets/9/\"},{\"name\":\"Kamino\",\"rotation_period\":\"27\",\"orbital_period\":\"463\",\"diameter\":\"19720\",\"climate\":\"temperate\",\"gravity\":\"1 standard\",\"terrain\":\"ocean\",\"surface_water\":\"100\",\"population\":\"1000000000\",\"residents\":[\"https://swapi.co/api/people/22/\",\"https://swapi.co/api/people/72/\",\"https://swapi.co/api/people/73/\"],\"films\":[\"https://swapi.co/api/films/5/\"],\"created\":\"2014-12-10T12:45:06.577000Z\",\"edited\":\"2014-12-20T20:58:18.434000Z\",\"url\":\"https://swapi.co/api/planets/10/\"},{\"name\":\"Geonosis\",\"rotation_period\":\"30\",\"orbital_period\":\"256\",\"diameter\":\"11370\",\"climate\":\"temperate, arid\",\"gravity\":\"0.9 standard\",\"terrain\":\"rock, desert, mountain, barren\",\"surface_water\":\"5\",\"population\":\"100000000000\",\"residents\":[\"https://swapi.co/api/people/63/\"],\"films\":[\"https://swapi.co/api/films/5/\"],\"created\":\"2014-12-10T12:47:22.350000Z\",\"edited\":\"2014-12-20T20:58:18.437000Z\",\"url\":\"https://swapi.co/api/planets/11/\"}]}"))
//                .and().body("species", equalTo("{\"count\":37,\"next\":\"https://swapi.co/api/species/?page=2\",\"previous\":null,\"results\":[{\"name\":\"Hutt\",\"classification\":\"gastropod\",\"designation\":\"sentient\",\"average_height\":\"300\",\"skin_colors\":\"green, brown, tan\",\"hair_colors\":\"n/a\",\"eye_colors\":\"yellow, red\",\"average_lifespan\":\"1000\",\"homeworld\":\"https://swapi.co/api/planets/24/\",\"language\":\"Huttese\",\"people\":[\"https://swapi.co/api/people/16/\"],\"films\":[\"https://swapi.co/api/films/3/\",\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-10T17:12:50.410000Z\",\"edited\":\"2014-12-20T21:36:42.146000Z\",\"url\":\"https://swapi.co/api/species/5/\"},{\"name\":\"Yoda's species\",\"classification\":\"mammal\",\"designation\":\"sentient\",\"average_height\":\"66\",\"skin_colors\":\"green, yellow\",\"hair_colors\":\"brown, white\",\"eye_colors\":\"brown, green, yellow\",\"average_lifespan\":\"900\",\"homeworld\":\"https://swapi.co/api/planets/28/\",\"language\":\"Galactic basic\",\"people\":[\"https://swapi.co/api/people/20/\"],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/5/\",\"https://swapi.co/api/films/4/\",\"https://swapi.co/api/films/6/\",\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-15T12:27:22.877000Z\",\"edited\":\"2014-12-20T21:36:42.148000Z\",\"url\":\"https://swapi.co/api/species/6/\"},{\"name\":\"Trandoshan\",\"classification\":\"reptile\",\"designation\":\"sentient\",\"average_height\":\"200\",\"skin_colors\":\"brown, green\",\"hair_colors\":\"none\",\"eye_colors\":\"yellow, orange\",\"average_lifespan\":\"unknown\",\"homeworld\":\"https://swapi.co/api/planets/29/\",\"language\":\"Dosh\",\"people\":[\"https://swapi.co/api/people/24/\"],\"films\":[\"https://swapi.co/api/films/2/\"],\"created\":\"2014-12-15T13:07:47.704000Z\",\"edited\":\"2014-12-20T21:36:42.151000Z\",\"url\":\"https://swapi.co/api/species/7/\"},{\"name\":\"Mon Calamari\",\"classification\":\"amphibian\",\"designation\":\"sentient\",\"average_height\":\"160\",\"skin_colors\":\"red, blue, brown, magenta\",\"hair_colors\":\"none\",\"eye_colors\":\"yellow\",\"average_lifespan\":\"unknown\",\"homeworld\":\"https://swapi.co/api/planets/31/\",\"language\":\"Mon Calamarian\",\"people\":[\"https://swapi.co/api/people/27/\"],\"films\":[\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-18T11:09:52.263000Z\",\"edited\":\"2014-12-20T21:36:42.153000Z\",\"url\":\"https://swapi.co/api/species/8/\"},{\"name\":\"Ewok\",\"classification\":\"mammal\",\"designation\":\"sentient\",\"average_height\":\"100\",\"skin_colors\":\"brown\",\"hair_colors\":\"white, brown, black\",\"eye_colors\":\"orange, brown\",\"average_lifespan\":\"unknown\",\"homeworld\":\"https://swapi.co/api/planets/7/\",\"language\":\"Ewokese\",\"people\":[\"https://swapi.co/api/people/30/\"],\"films\":[\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-18T11:22:00.285000Z\",\"edited\":\"2014-12-20T21:36:42.155000Z\",\"url\":\"https://swapi.co/api/species/9/\"},{\"name\":\"Sullustan\",\"classification\":\"mammal\",\"designation\":\"sentient\",\"average_height\":\"180\",\"skin_colors\":\"pale\",\"hair_colors\":\"none\",\"eye_colors\":\"black\",\"average_lifespan\":\"unknown\",\"homeworld\":\"https://swapi.co/api/planets/33/\",\"language\":\"Sullutese\",\"people\":[\"https://swapi.co/api/people/31/\"],\"films\":[\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-18T11:26:20.103000Z\",\"edited\":\"2014-12-20T21:36:42.157000Z\",\"url\":\"https://swapi.co/api/species/10/\"},{\"name\":\"Neimodian\",\"classification\":\"unknown\",\"designation\":\"sentient\",\"average_height\":\"180\",\"skin_colors\":\"grey, green\",\"hair_colors\":\"none\",\"eye_colors\":\"red, pink\",\"average_lifespan\":\"unknown\",\"homeworld\":\"https://swapi.co/api/planets/18/\",\"language\":\"Neimoidia\",\"people\":[\"https://swapi.co/api/people/33/\"],\"films\":[\"https://swapi.co/api/films/4/\"],\"created\":\"2014-12-19T17:07:31.319000Z\",\"edited\":\"2014-12-20T21:36:42.160000Z\",\"url\":\"https://swapi.co/api/species/11/\"},{\"name\":\"Gungan\",\"classification\":\"amphibian\",\"designation\":\"sentient\",\"average_height\":\"190\",\"skin_colors\":\"brown, green\",\"hair_colors\":\"none\",\"eye_colors\":\"orange\",\"average_lifespan\":\"unknown\",\"homeworld\":\"https://swapi.co/api/planets/8/\",\"language\":\"Gungan basic\",\"people\":[\"https://swapi.co/api/people/36/\",\"https://swapi.co/api/people/37/\",\"https://swapi.co/api/people/38/\"],\"films\":[\"https://swapi.co/api/films/5/\",\"https://swapi.co/api/films/4/\"],\"created\":\"2014-12-19T17:30:37.341000Z\",\"edited\":\"2014-12-20T21:36:42.163000Z\",\"url\":\"https://swapi.co/api/species/12/\"},{\"name\":\"Toydarian\",\"classification\":\"mammal\",\"designation\":\"sentient\",\"average_height\":\"120\",\"skin_colors\":\"blue, green, grey\",\"hair_colors\":\"none\",\"eye_colors\":\"yellow\",\"average_lifespan\":\"91\",\"homeworld\":\"https://swapi.co/api/planets/34/\",\"language\":\"Toydarian\",\"people\":[\"https://swapi.co/api/people/40/\"],\"films\":[\"https://swapi.co/api/films/5/\",\"https://swapi.co/api/films/4/\"],\"created\":\"2014-12-19T17:48:56.893000Z\",\"edited\":\"2014-12-20T21:36:42.165000Z\",\"url\":\"https://swapi.co/api/species/13/\"},{\"name\":\"Dug\",\"classification\":\"mammal\",\"designation\":\"sentient\",\"average_height\":\"100\",\"skin_colors\":\"brown, purple, grey, red\",\"hair_colors\":\"none\",\"eye_colors\":\"yellow, blue\",\"average_lifespan\":\"unknown\",\"homeworld\":\"https://swapi.co/api/planets/35/\",\"language\":\"Dugese\",\"people\":[\"https://swapi.co/api/people/41/\"],\"films\":[\"https://swapi.co/api/films/4/\"],\"created\":\"2014-12-19T17:53:11.214000Z\",\"edited\":\"2014-12-20T21:36:42.167000Z\",\"url\":\"https://swapi.co/api/species/14/\"}]}"))
//                .and().body("starships", equalTo("{\"count\":37,\"next\":\"https://swapi.co/api/starships/?page=2\",\"previous\":null,\"results\":[{\"name\":\"Executor\",\"model\":\"Executor-class star dreadnought\",\"manufacturer\":\"Kuat Drive Yards, Fondor Shipyards\",\"cost_in_credits\":\"1143350000\",\"length\":\"19000\",\"max_atmosphering_speed\":\"n/a\",\"crew\":\"279144\",\"passengers\":\"38000\",\"cargo_capacity\":\"250000000\",\"consumables\":\"6 years\",\"hyperdrive_rating\":\"2.0\",\"MGLT\":\"40\",\"starship_class\":\"Star dreadnought\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-15T12:31:42.547000Z\",\"edited\":\"2017-04-19T10:56:06.685592Z\",\"url\":\"https://swapi.co/api/starships/15/\"},{\"name\":\"Sentinel-class landing craft\",\"model\":\"Sentinel-class landing craft\",\"manufacturer\":\"Sienar Fleet Systems, Cyngus Spaceworks\",\"cost_in_credits\":\"240000\",\"length\":\"38\",\"max_atmosphering_speed\":\"1000\",\"crew\":\"5\",\"passengers\":\"75\",\"cargo_capacity\":\"180000\",\"consumables\":\"1 month\",\"hyperdrive_rating\":\"1.0\",\"MGLT\":\"70\",\"starship_class\":\"landing craft\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-10T15:48:00.586000Z\",\"edited\":\"2014-12-22T17:35:44.431407Z\",\"url\":\"https://swapi.co/api/starships/5/\"},{\"name\":\"Death Star\",\"model\":\"DS-1 Orbital Battle Station\",\"manufacturer\":\"Imperial Department of Military Research, Sienar Fleet Systems\",\"cost_in_credits\":\"1000000000000\",\"length\":\"120000\",\"max_atmosphering_speed\":\"n/a\",\"crew\":\"342953\",\"passengers\":\"843342\",\"cargo_capacity\":\"1000000000000\",\"consumables\":\"3 years\",\"hyperdrive_rating\":\"4.0\",\"MGLT\":\"10\",\"starship_class\":\"Deep Space Mobile Battlestation\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-10T16:36:50.509000Z\",\"edited\":\"2014-12-22T17:35:44.452589Z\",\"url\":\"https://swapi.co/api/starships/9/\"},{\"name\":\"Millennium Falcon\",\"model\":\"YT-1300 light freighter\",\"manufacturer\":\"Corellian Engineering Corporation\",\"cost_in_credits\":\"100000\",\"length\":\"34.37\",\"max_atmosphering_speed\":\"1050\",\"crew\":\"4\",\"passengers\":\"6\",\"cargo_capacity\":\"100000\",\"consumables\":\"2 months\",\"hyperdrive_rating\":\"0.5\",\"MGLT\":\"75\",\"starship_class\":\"Light freighter\",\"pilots\":[\"https://swapi.co/api/people/13/\",\"https://swapi.co/api/people/14/\",\"https://swapi.co/api/people/25/\",\"https://swapi.co/api/people/31/\"],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/7/\",\"https://swapi.co/api/films/3/\",\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-10T16:59:45.094000Z\",\"edited\":\"2014-12-22T17:35:44.464156Z\",\"url\":\"https://swapi.co/api/starships/10/\"},{\"name\":\"Y-wing\",\"model\":\"BTL Y-wing\",\"manufacturer\":\"Koensayr Manufacturing\",\"cost_in_credits\":\"134999\",\"length\":\"14\",\"max_atmosphering_speed\":\"1000km\",\"crew\":\"2\",\"passengers\":\"0\",\"cargo_capacity\":\"110\",\"consumables\":\"1 week\",\"hyperdrive_rating\":\"1.0\",\"MGLT\":\"80\",\"starship_class\":\"assault starfighter\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/3/\",\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-12T11:00:39.817000Z\",\"edited\":\"2014-12-22T17:35:44.479706Z\",\"url\":\"https://swapi.co/api/starships/11/\"},{\"name\":\"X-wing\",\"model\":\"T-65 X-wing\",\"manufacturer\":\"Incom Corporation\",\"cost_in_credits\":\"149999\",\"length\":\"12.5\",\"max_atmosphering_speed\":\"1050\",\"crew\":\"1\",\"passengers\":\"0\",\"cargo_capacity\":\"110\",\"consumables\":\"1 week\",\"hyperdrive_rating\":\"1.0\",\"MGLT\":\"100\",\"starship_class\":\"Starfighter\",\"pilots\":[\"https://swapi.co/api/people/1/\",\"https://swapi.co/api/people/9/\",\"https://swapi.co/api/people/18/\",\"https://swapi.co/api/people/19/\"],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/3/\",\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-12T11:19:05.340000Z\",\"edited\":\"2014-12-22T17:35:44.491233Z\",\"url\":\"https://swapi.co/api/starships/12/\"},{\"name\":\"TIE Advanced x1\",\"model\":\"Twin Ion Engine Advanced x1\",\"manufacturer\":\"Sienar Fleet Systems\",\"cost_in_credits\":\"unknown\",\"length\":\"9.2\",\"max_atmosphering_speed\":\"1200\",\"crew\":\"1\",\"passengers\":\"0\",\"cargo_capacity\":\"150\",\"consumables\":\"5 days\",\"hyperdrive_rating\":\"1.0\",\"MGLT\":\"105\",\"starship_class\":\"Starfighter\",\"pilots\":[\"https://swapi.co/api/people/4/\"],\"films\":[\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-12T11:21:32.991000Z\",\"edited\":\"2014-12-22T17:35:44.549047Z\",\"url\":\"https://swapi.co/api/starships/13/\"},{\"name\":\"Slave 1\",\"model\":\"Firespray-31-class patrol and attack\",\"manufacturer\":\"Kuat Systems Engineering\",\"cost_in_credits\":\"unknown\",\"length\":\"21.5\",\"max_atmosphering_speed\":\"1000\",\"crew\":\"1\",\"passengers\":\"6\",\"cargo_capacity\":\"70000\",\"consumables\":\"1 month\",\"hyperdrive_rating\":\"3.0\",\"MGLT\":\"70\",\"starship_class\":\"Patrol craft\",\"pilots\":[\"https://swapi.co/api/people/22/\"],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/5/\"],\"created\":\"2014-12-15T13:00:56.332000Z\",\"edited\":\"2014-12-22T17:35:44.716273Z\",\"url\":\"https://swapi.co/api/starships/21/\"},{\"name\":\"Imperial shuttle\",\"model\":\"Lambda-class T-4a shuttle\",\"manufacturer\":\"Sienar Fleet Systems\",\"cost_in_credits\":\"240000\",\"length\":\"20\",\"max_atmosphering_speed\":\"850\",\"crew\":\"6\",\"passengers\":\"20\",\"cargo_capacity\":\"80000\",\"consumables\":\"2 months\",\"hyperdrive_rating\":\"1.0\",\"MGLT\":\"50\",\"starship_class\":\"Armed government transport\",\"pilots\":[\"https://swapi.co/api/people/1/\",\"https://swapi.co/api/people/13/\",\"https://swapi.co/api/people/14/\"],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-15T13:04:47.235000Z\",\"edited\":\"2014-12-22T17:35:44.795405Z\",\"url\":\"https://swapi.co/api/starships/22/\"},{\"name\":\"EF76 Nebulon-B escort frigate\",\"model\":\"EF76 Nebulon-B escort frigate\",\"manufacturer\":\"Kuat Drive Yards\",\"cost_in_credits\":\"8500000\",\"length\":\"300\",\"max_atmosphering_speed\":\"800\",\"crew\":\"854\",\"passengers\":\"75\",\"cargo_capacity\":\"6000000\",\"consumables\":\"2 years\",\"hyperdrive_rating\":\"2.0\",\"MGLT\":\"40\",\"starship_class\":\"Escort ship\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-15T13:06:30.813000Z\",\"edited\":\"2014-12-22T17:35:44.848329Z\",\"url\":\"https://swapi.co/api/starships/23/\"}]}"))
//                .and().body("vehicles", equalTo("{\"count\":39,\"next\":\"https://swapi.co/api/vehicles/?page=2\",\"previous\":null,\"results\":[{\"name\":\"Sand Crawler\",\"model\":\"Digger Crawler\",\"manufacturer\":\"Corellia Mining Corporation\",\"cost_in_credits\":\"150000\",\"length\":\"36.8\",\"max_atmosphering_speed\":\"30\",\"crew\":\"46\",\"passengers\":\"30\",\"cargo_capacity\":\"50000\",\"consumables\":\"2 months\",\"vehicle_class\":\"wheeled\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/5/\",\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-10T15:36:25.724000Z\",\"edited\":\"2014-12-22T18:21:15.523587Z\",\"url\":\"https://swapi.co/api/vehicles/4/\"},{\"name\":\"T-16 skyhopper\",\"model\":\"T-16 skyhopper\",\"manufacturer\":\"Incom Corporation\",\"cost_in_credits\":\"14500\",\"length\":\"10.4\",\"max_atmosphering_speed\":\"1200\",\"crew\":\"1\",\"passengers\":\"1\",\"cargo_capacity\":\"50\",\"consumables\":\"0\",\"vehicle_class\":\"repulsorcraft\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-10T16:01:52.434000Z\",\"edited\":\"2014-12-22T18:21:15.552614Z\",\"url\":\"https://swapi.co/api/vehicles/6/\"},{\"name\":\"X-34 landspeeder\",\"model\":\"X-34 landspeeder\",\"manufacturer\":\"SoroSuub Corporation\",\"cost_in_credits\":\"10550\",\"length\":\"3.4\",\"max_atmosphering_speed\":\"250\",\"crew\":\"1\",\"passengers\":\"1\",\"cargo_capacity\":\"5\",\"consumables\":\"unknown\",\"vehicle_class\":\"repulsorcraft\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-10T16:13:52.586000Z\",\"edited\":\"2014-12-22T18:21:15.583700Z\",\"url\":\"https://swapi.co/api/vehicles/7/\"},{\"name\":\"TIE/LN starfighter\",\"model\":\"Twin Ion Engine/Ln Starfighter\",\"manufacturer\":\"Sienar Fleet Systems\",\"cost_in_credits\":\"unknown\",\"length\":\"6.4\",\"max_atmosphering_speed\":\"1200\",\"crew\":\"1\",\"passengers\":\"0\",\"cargo_capacity\":\"65\",\"consumables\":\"2 days\",\"vehicle_class\":\"starfighter\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/3/\",\"https://swapi.co/api/films/1/\"],\"created\":\"2014-12-10T16:33:52.860000Z\",\"edited\":\"2014-12-22T18:21:15.606149Z\",\"url\":\"https://swapi.co/api/vehicles/8/\"},{\"name\":\"Snowspeeder\",\"model\":\"t-47 airspeeder\",\"manufacturer\":\"Incom corporation\",\"cost_in_credits\":\"unknown\",\"length\":\"4.5\",\"max_atmosphering_speed\":\"650\",\"crew\":\"2\",\"passengers\":\"0\",\"cargo_capacity\":\"10\",\"consumables\":\"none\",\"vehicle_class\":\"airspeeder\",\"pilots\":[\"https://swapi.co/api/people/1/\",\"https://swapi.co/api/people/18/\"],\"films\":[\"https://swapi.co/api/films/2/\"],\"created\":\"2014-12-15T12:22:12Z\",\"edited\":\"2014-12-22T18:21:15.623033Z\",\"url\":\"https://swapi.co/api/vehicles/14/\"},{\"name\":\"TIE bomber\",\"model\":\"TIE/sa bomber\",\"manufacturer\":\"Sienar Fleet Systems\",\"cost_in_credits\":\"unknown\",\"length\":\"7.8\",\"max_atmosphering_speed\":\"850\",\"crew\":\"1\",\"passengers\":\"0\",\"cargo_capacity\":\"none\",\"consumables\":\"2 days\",\"vehicle_class\":\"space/planetary bomber\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-15T12:33:15.838000Z\",\"edited\":\"2014-12-22T18:21:15.667730Z\",\"url\":\"https://swapi.co/api/vehicles/16/\"},{\"name\":\"AT-AT\",\"model\":\"All Terrain Armored Transport\",\"manufacturer\":\"Kuat Drive Yards, Imperial Department of Military Research\",\"cost_in_credits\":\"unknown\",\"length\":\"20\",\"max_atmosphering_speed\":\"60\",\"crew\":\"5\",\"passengers\":\"40\",\"cargo_capacity\":\"1000\",\"consumables\":\"unknown\",\"vehicle_class\":\"assault walker\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-15T12:38:25.937000Z\",\"edited\":\"2014-12-22T18:21:15.714673Z\",\"url\":\"https://swapi.co/api/vehicles/18/\"},{\"name\":\"AT-ST\",\"model\":\"All Terrain Scout Transport\",\"manufacturer\":\"Kuat Drive Yards, Imperial Department of Military Research\",\"cost_in_credits\":\"unknown\",\"length\":\"2\",\"max_atmosphering_speed\":\"90\",\"crew\":\"2\",\"passengers\":\"0\",\"cargo_capacity\":\"200\",\"consumables\":\"none\",\"vehicle_class\":\"walker\",\"pilots\":[\"https://swapi.co/api/people/13/\"],\"films\":[\"https://swapi.co/api/films/2/\",\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-15T12:46:42.384000Z\",\"edited\":\"2014-12-22T18:21:15.761584Z\",\"url\":\"https://swapi.co/api/vehicles/19/\"},{\"name\":\"Storm IV Twin-Pod cloud car\",\"model\":\"Storm IV Twin-Pod\",\"manufacturer\":\"Bespin Motors\",\"cost_in_credits\":\"75000\",\"length\":\"7\",\"max_atmosphering_speed\":\"1500\",\"crew\":\"2\",\"passengers\":\"0\",\"cargo_capacity\":\"10\",\"consumables\":\"1 day\",\"vehicle_class\":\"repulsorcraft\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/2/\"],\"created\":\"2014-12-15T12:58:50.530000Z\",\"edited\":\"2014-12-22T18:21:15.783232Z\",\"url\":\"https://swapi.co/api/vehicles/20/\"},{\"name\":\"Sail barge\",\"model\":\"Modified Luxury Sail Barge\",\"manufacturer\":\"Ubrikkian Industries Custom Vehicle Division\",\"cost_in_credits\":\"285000\",\"length\":\"30\",\"max_atmosphering_speed\":\"100\",\"crew\":\"26\",\"passengers\":\"500\",\"cargo_capacity\":\"2000000\",\"consumables\":\"Live food tanks\",\"vehicle_class\":\"sail barge\",\"pilots\":[],\"films\":[\"https://swapi.co/api/films/3/\"],\"created\":\"2014-12-18T10:44:14.217000Z\",\"edited\":\"2014-12-22T18:21:15.807906Z\",\"url\":\"https://swapi.co/api/vehicles/24/\"}]}"));
    }

    @Test
    public void testRestNoAuthenticationRequired() {
        given()
                .contentType("application/json")
                .when()
                .get("/info").then()
                .statusCode(200)
                .body("msg", equalTo("Hello anonymous"));
    }

    @Test
    public void testRestForAdmin() {
        login("admin@mail.dk", "admin");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: admin@mail.dk"));
    }

    @Test
    public void testRestForUser() {
        login("user@mail.dk", "user");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: user@mail.dk"));
    }

    @Test
    public void testAutorizedUserCannotAccesAdminPage() {
        login("user@mail.dk", "user");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then()
                .statusCode(401);
    }

    @Test
    public void testAutorizedAdminCannotAccesUserPage() {
        login("admin@mail.dk", "admin");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user").then()
                .statusCode(401);
    }

    @Test
    public void testRestForMultiRole1() {
        login("both@mail.dk", "both");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: both@mail.dk"));
    }

    @Test
    public void testRestForMultiRole2() {
        login("both@mail.dk", "both");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: both@mail.dk"));
    }

    @Test
    public void userNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/info/user").then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", equalTo("Not authenticated - do login"));
    }

    @Test
    public void adminNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/info/user").then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", equalTo("Not authenticated - do login"));
    }

    /**
     * Testing 'getMultipleRoles' endpoint. Meant to allow registered users only
     *
     * @RolesAllowed({"admin", "user"})
     */
    @Test
    public void testMultipleRolesEndpoint_user() {
        login("user@mail.dk", "user");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/both").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin OR user, but not a nobody) User: user@mail.dk"));
    }

    /**
     * Testing 'getMultipleRoles' endpoint. Meant to allow registered users only
     *
     * @RolesAllowed({"admin", "user"})
     */
    @Test
    public void testMultipleRolesEndpoint_admin() {
        login("admin@mail.dk", "admin");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/both").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin OR user, but not a nobody) User: admin@mail.dk"));
    }

    /**
     * Testing 'getMultipleRoles' endpoint. Meant to allow registered users only
     *
     * @RolesAllowed({"admin", "user"})
     */
    @Test
    public void testMultipleRolesEndpoint_nobody() {
        given()
                .contentType("application/json")
                .when()
                .get("/info/both").then().log().body()
                .statusCode(403)
                .body("message", equalTo("Not authenticated - do login"));
    }
}

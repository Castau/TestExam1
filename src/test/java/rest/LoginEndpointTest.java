package rest;

import entities.Address;
import entities.Hobby;
import entities.User;
import entities.Role;
import facades.AdminFacadeImpl;
import facades.HobbyFacadeImpl;
import facades.UserFacadeImpl;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

public class LoginEndpointTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

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

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        EMF_Creator.startREST_TestWithDB();
        httpServer = startServer();
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;

        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);

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

    @AfterAll
    public static void closeTestServer() {
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
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

    private static String securityToken;

    private static void login(String email, String password) {
        String json = String.format("{userEmail: \"%s\", password: \"%s\"}", email, password);
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
        given().when().get("/DGS").then().statusCode(200);
    }

    @Test
    public void testRestNoAuthenticationRequired() {
        given()
                .contentType("application/json")
                .when()
                .get("/DGS").then()
                .statusCode(200)
                .body("msg", equalTo("Hello anonymous"));
    }

    @Test
    public void testRestForAdmin() {
        login("2TesterFirst@mail.dk", "admin");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/admin").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: 2TesterFirst@mail.dk"));
    }

    @Test
    public void testRestForUser() {
        login("1TesterFirst@mail.dk", "user");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/hello").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: 1TesterFirst@mail.dk"));
    }

    @Test
    public void testAutorizedUserCannotAccesAdminPage() {
        login("1TesterFirst@mail.dk", "user");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/admin").then()
                .statusCode(401);
    }

    @Test
    public void testAutorizedAdminCannotAccesUserPage() {
        login("2TesterFirst@mail.dk", "admin");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/hello").then()
                .statusCode(401);
    }

    @Test
    public void testRestForMultiRole1() {
        login("3TesterFirst@mail.dk", "both");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/admin").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: 3TesterFirst@mail.dk"));
    }

    @Test
    public void testRestForMultiRole2() {
        login("3TesterFirst@mail.dk", "both");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/hello").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: 3TesterFirst@mail.dk"));
    }

    @Test
    public void userNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/DGS/hello").then()
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
                .get("/DGS/hello").then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", equalTo("Not authenticated - do login"));
    }

    @Test
    public void testMultipleRolesEndpoint_user() {
        login("1TesterFirst@mail.dk", "user");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/both").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin OR user, but not a nobody) User: 1TesterFirst@mail.dk"));
    }

    @Test
    public void testMultipleRolesEndpoint_admin() {
        login("2TesterFirst@mail.dk", "admin");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/both").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin OR user, but not a nobody) User: 2TesterFirst@mail.dk"));
    }

    @Test
    public void testMultipleRolesEndpoint_nobody() {
        given()
                .contentType("application/json")
                .when()
                .get("/DGS/both").then().log().body()
                .statusCode(403)
                .body("message", equalTo("Not authenticated - do login"));
    }
}

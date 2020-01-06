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
        login("admin@mail.dk", "admin");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/admin").then()
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
                .get("/DGS/hello").then()
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
                .get("/DGS/admin").then()
                .statusCode(401);
    }

    @Test
    public void testAutorizedAdminCannotAccesUserPage() {
        login("admin@mail.dk", "admin");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/hello").then()
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
                .get("/DGS/admin").then()
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
                .get("/DGS/hello").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: both@mail.dk"));
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
        login("user@mail.dk", "user");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/both").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin OR user, but not a nobody) User: user@mail.dk"));
    }

    @Test
    public void testMultipleRolesEndpoint_admin() {
        login("admin@mail.dk", "admin");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/DGS/both").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin OR user, but not a nobody) User: admin@mail.dk"));
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

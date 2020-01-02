package security;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import facades.Facade;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entities.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import errorhandling.AuthenticationException;
import errorhandling.GenericExceptionMapper;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

@Path("login")
public class LoginEndpoint {

  public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30 min
  private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
  public static final Facade USER_FACADE = Facade.getUserFacade(EMF);
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response login(String jsonString) throws AuthenticationException {
    JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
    String userEmail = json.get("userEmail").getAsString();
    String password = json.get("password").getAsString();

    try {
      User user = USER_FACADE.getVeryfiedUser(userEmail, password);
      String token = createToken(userEmail, user.getRolesAsStrings());
      JsonObject responseJson = new JsonObject();
      responseJson.addProperty("userEmail", userEmail);
      responseJson.addProperty("token", token);
      List<String> roles = user.getRolesAsStrings();
      responseJson.addProperty("roles", new Gson().toJson(roles));
      return Response.ok(new Gson().toJson(responseJson)).build();

    } catch (JOSEException | AuthenticationException ex) {
      if (ex instanceof AuthenticationException) {
        throw (AuthenticationException) ex;
      }
      Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
    }
    throw new AuthenticationException("Invalid user email or password! Please try again");
  }

  private String createToken(String userEmail, List<String> roles) throws JOSEException {

    StringBuilder res = new StringBuilder();
    for (String string : roles) {
      res.append(string);
      res.append(",");
    }
    String rolesAsString = res.length() > 0 ? res.substring(0, res.length() - 1) : "";
    String issuer = "TestExam1";

    JWSSigner signer = new MACSigner(SharedSecret.getSharedKey());
    Date date = new Date();
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(userEmail)
            .claim("userEmail", userEmail)
            .claim("roles", rolesAsString)
            .claim("issuer", issuer)
            .issueTime(date)
            .expirationTime(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
            .build();
    
    JWSHeader.Builder builder = new JWSHeader.Builder(JWSAlgorithm.HS256); //sets 'alg':'256'
    builder.type(JOSEObjectType.JWT); //sets 'typ':'JWT'
    JWSHeader header = builder.build();
    SignedJWT signedJWT = new SignedJWT(header, claimsSet);
    
    signedJWT.sign(signer);
    return signedJWT.serialize();

  }
}

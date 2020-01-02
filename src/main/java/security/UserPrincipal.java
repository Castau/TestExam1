package security;

import entities.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserPrincipal implements Principal {

  
    private String userEmail;
    private List<String> roles = new ArrayList<>();

    /* Create a UserPrincipal, given the Entity class User*/
    public UserPrincipal(User user) {
        this.userEmail = user.getUserEmail();
        this.roles = user.getRolesAsStrings();

    }

    public UserPrincipal(String useremail, String[] roles) {
        super();
        this.userEmail = useremail;
        this.roles = Arrays.asList(roles);
    }

    @Override
    public String getName() {
        return userEmail;
    }

    public boolean isUserInRole(String role) {
        return this.roles.contains(role);
    }
}

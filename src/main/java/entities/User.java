package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = "User.deleteAllRows", query = "DELETE FROM User"), 
    @NamedQuery(name = "User.getByEmail", query = "SELECT u FROM User u Where u.userEmail= :email")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_ID")
    private int userID;

    @Basic(optional = false)
    @NotNull
    @Column(name = "user_firstname", length = 25)
    private String userFirstName;

    @Basic(optional = false)
    @NotNull
    @Column(name = "user_lastname", length = 25)
    private String userLastName;

    @Basic(optional = false)
    @NotNull
    @Column(name = "user_phone")
    private String userPhone;

    @Basic(optional = false)
    @NotNull
    @Column(name = "user_email", unique = true)
    private String userEmail;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "user_pass")
    private String userPass;

    @JoinTable(name = "user_roles", joinColumns = {
        @JoinColumn(name = "user_email", referencedColumnName = "user_email")}, inverseJoinColumns = {
        @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
    @ManyToMany
    private List<Role> roleList = new ArrayList();

    public List<String> getRolesAsStrings() {
        if (roleList.isEmpty()) {
            return null;
        }
        List<String> rolesAsStrings = new ArrayList();
        for (Role role : roleList) {
            rolesAsStrings.add(role.getRoleName());
        }
        return rolesAsStrings;
    }

    public User() {
    }

    public boolean verifyPasswordNonHashed(String pw) {
        return (BCrypt.checkpw(pw, userPass));
    }

    public boolean verifyHashedPW(String pw) {
        return (pw.equals(this.userPass));
    }

    public User(String userFirstName, String userLastName, String userPhone, String userEmail, String userPass) {
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserPass() {
        return this.userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public void addRole(Role userRole) {
        roleList.add(userRole);
    }

    public int getUserID() {
        return userID;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.userFirstName);
        hash = 53 * hash + Objects.hashCode(this.userLastName);
        hash = 53 * hash + Objects.hashCode(this.userPhone);
        hash = 53 * hash + Objects.hashCode(this.userEmail);
        hash = 53 * hash + Objects.hashCode(this.userPass);
        hash = 53 * hash + Objects.hashCode(this.roleList);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.userFirstName, other.userFirstName)) {
            return false;
        }
        if (!Objects.equals(this.userLastName, other.userLastName)) {
            return false;
        }
        if (!Objects.equals(this.userPhone, other.userPhone)) {
            return false;
        }
        if (!Objects.equals(this.userEmail, other.userEmail)) {
            return false;
        }
        if (!Objects.equals(this.userPass, other.userPass)) {
            return false;
        }
        if (!Objects.equals(this.roleList, other.roleList)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "userID = " + userID + ", userFirstName = " + userFirstName + ", userLastName = " + userLastName + ", userPhone = " + userPhone + ", userEmail = " + userEmail + ", userPass = " + userPass + ", roleList = " + roleList;
    }

}

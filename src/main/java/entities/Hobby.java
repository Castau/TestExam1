package entities;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Camilla
 */

@Entity
@Table(name = "hobbies")
@NamedQueries({
    @NamedQuery(name = "Hobby.deleteAllRows", query = "DELETE FROM Hobby"), 
    @NamedQuery(name = "Hobby.all", query = "SELECT h FROM Hobby h")})

public class Hobby implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "hobby_ID")
    private int hobbyID;

    @Basic(optional = false)
    @NotNull
    @Column(name = "hobby_name", length = 40, unique = true)
    private String hobbyName;

    @Basic(optional = false)
    @NotNull
    @Column(name = "hobby_description", length = 50)
    private String hobbyDescription;
    
    @ManyToMany
    private List<User> persons;

    public Hobby() {
    }

    public Hobby(String hobbyName, String hobbyDescription) {
        this.hobbyName = hobbyName;
        this.hobbyDescription = hobbyDescription;
    }

    public int getHobbyID() {
        return hobbyID;
    }

    public String getHobbyName() {
        return hobbyName;
    }

    public void setHobbyName(String hobbyName) {
        this.hobbyName = hobbyName;
    }

    public String getHobbyDescription() {
        return hobbyDescription;
    }

    public void setHobbyDescription(String hobbyDescription) {
        this.hobbyDescription = hobbyDescription;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.hobbyName);
        hash = 97 * hash + Objects.hashCode(this.hobbyDescription);
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
        final Hobby other = (Hobby) obj;
        if (!Objects.equals(this.hobbyName, other.hobbyName)) {
            return false;
        }
        if (!Objects.equals(this.hobbyDescription, other.hobbyDescription)) {
            return false;
        }
        return true;
    }
    
    
}


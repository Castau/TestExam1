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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Camilla
 */

@Entity
@Table(name = "addresses")
//@NamedQueries({
//    @NamedQuery(name = "Hobby.deleteAllRows", query = "DELETE FROM Hobby"), 
//    @NamedQuery(name = "Hobby.all", query = "SELECT h FROM Hobby h")})
public class Address implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "address_ID")
    private int addressID;

    @Basic(optional = false)
    @NotNull
    @Column(name = "street", length = 40, unique = true)
    private String street;

    @Basic(optional = false)
    @NotNull
    @Column(name = "city", length = 25)
    private String city;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "zipcode", length = 4)
    private int zipcode;
    
    @OneToMany(mappedBy = "address")
    private List<User> persons;

    public Address() {
    }

    public Address(String street, String city, int zipcode, List<User> persons) {
        this.street = street;
        this.city = city;
        this.zipcode = zipcode;
        this.persons = persons;
    }

    public int getAddressID() {
        return addressID;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public List<User> getPersons() {
        return persons;
    }

    public void setPersons(List<User> persons) {
        this.persons = persons;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.street);
        hash = 23 * hash + Objects.hashCode(this.city);
        hash = 23 * hash + this.zipcode;
        hash = 23 * hash + Objects.hashCode(this.persons);
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
        final Address other = (Address) obj;
        if (this.zipcode != other.zipcode) {
            return false;
        }
        if (!Objects.equals(this.street, other.street)) {
            return false;
        }
        if (!Objects.equals(this.city, other.city)) {
            return false;
        }
        if (!Objects.equals(this.persons, other.persons)) {
            return false;
        }
        return true;
    }
}

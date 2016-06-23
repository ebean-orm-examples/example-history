package org.example.domain;

import com.avaje.ebean.annotation.History;
import org.example.domain.finder.AddressFinder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Address entity bean.
 */
@History
@Entity
@Table(name = "o_address")
public class Address extends BaseModel {

  /**
   * Convenience Finder for 'active record' style.
   */
  public static final AddressFinder find = new AddressFinder();

  @Size(max = 100)
  String line1;

  @Size(max = 100)
  String line2;

  @Size(max = 100)
  String city;

  @ManyToOne
  Country country;

  /**
   * Create a copy of the address. Used to provide a 'snapshot' of 
   * the shippingAddress for a give order.
   */
  public Address createCopy() {
    Address copy = new Address();
    copy.setLine1(line1);
    copy.setLine2(line2);
    copy.setCity(city);
    copy.setCountry(country);
    return copy;
  }
  
  public String toString() {
    return id + ", " + line1 + ", " + city;
  }

  /**
   * Return line 1.
   */
  public String getLine1() {
    return line1;
  }

  /**
   * Set line 1.
   */
  public void setLine1(String line1) {
    this.line1 = line1;
  }

  /**
   * Return line 2.
   */
  public String getLine2() {
    return line2;
  }

  /**
   * Set line 2.
   */
  public void setLine2(String line2) {
    this.line2 = line2;
  }

  /**
   * Return city.
   */
  public String getCity() {
    return city;
  }

  /**
   * Set city.
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * Return country.
   */
  public Country getCountry() {
    return country;
  }

  /**
   * Set country.
   */
  public void setCountry(Country country) {
    this.country = country;
  }

}

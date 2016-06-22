package org.example.domain;

import com.avaje.ebean.annotation.Cache;
import org.example.domain.finder.CountryFinder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Country entity bean.
 */
@Cache
@Entity
@Table(name = "country")
public class Country {

  /**
   * Convenience Finder for 'active record' style.
   */
  public static final CountryFinder find = new CountryFinder();

  @Id
  @Size(max = 2)
  String code;

  @Size(max = 60)
  String name;

  public String toString() {
    return code;
  }

  /**
   * Return code.
   */
  public String getCode() {
    return code;
  }

  /**
   * Set code.
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * Return name.
   */
  public String getName() {
    return name;
  }

  /**
   * Set name.
   */
  public void setName(String name) {
    this.name = name;
  }


}

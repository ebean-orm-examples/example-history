package org.example.domain.finder;

import com.avaje.ebean.Finder;
import org.example.domain.Customer;
import org.example.domain.query.QCustomer;

/**
 * Add finder methods here.
 */
public class CustomerFinder extends Finder<Long,Customer> {

  public CustomerFinder() {
    super(Customer.class);
  }

  /**
   * Find by name equal (case insensitive).
   */
  public Customer byName(String name) {
    return where().name.ieq(name).findUnique();
  }

  public QCustomer where() {
    return new QCustomer(db());
  }
}

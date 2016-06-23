package org.example.domain;

import org.junit.Ignore;
import org.junit.Test;

public class CustomerTest {

  @Ignore
  @Test
  public void updateJim() {

    Customer customer = new Customer();
    customer.setName("fred");
    customer.setComments("Flower power");
    customer.save();

//    Customer jim = Customer.find.byName("fred");
//    jim.setComments("MySql stuff here");
////    jim.setRegistered(LocalDate.now());
////    Address billingAddress = new Address();
//    Address billingAddress = jim.getBillingAddress();
//    billingAddress.setLine1("Zappo");
//    billingAddress.setCity("Zack");
//    jim.setBillingAddress(billingAddress);
//    jim.save();
  }


}
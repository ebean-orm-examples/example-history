package org.example.domain;

import com.avaje.ebean.DelegateEbeanServer;
import com.avaje.ebean.MockiEbean;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

public class CustomerTest {

  @Ignore
  @Test
  public void updateJim() {

    Customer jim = Customer.find.byName("jim");
    jim.setComments("It's life jim!");
    jim.setRegistered(LocalDate.now());
    Address billingAddress = new Address();
    billingAddress.setLine1("Muse way");
    billingAddress.setCity("BlackStar");
    jim.setBillingAddress(billingAddress);
    jim.save();
  }

  @Ignore
  @Test
  public void insert() {


    DelegateEbeanServer mock = new DelegateEbeanServer();
    mock.withPersisting(true);

    MockiEbean.runWithMock(mock, () -> {

      Customer customer = new Customer();
      customer.setName("jim");
      //customer.setComments("first comment");

      customer.save();

      //customer.setComments("second comment");
      customer.save();

    });

    assertThat(mock.capturedBeans.save).hasSize(2);

  }

}
package org.example.domain;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ValuePair;
import com.avaje.ebean.Version;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class CustomerFindByIdAsAtTest {

  @Test
  public void findJim() {


    Timestamp asOf = parse("2016-06-22T14:40:19.573+12:00");

    Customer jim =
        Customer.find.where()
            .asOf(asOf)
            .fetch("billingAddress")
            .where().name.eq("jim")
            .findUnique();

    // invoke lazy loading ... asOf propagated
    jim.getBillingAddress().getLine1();

    System.out.println("Customer> "+jim);
  }

  @Test
  public void exampleFindVersions() {

    Timestamp start = parse("2016-06-22T14:40:19.000+12:00");
      Timestamp end = parse("2016-06-22T16:19:19.000+12:00");


    List<Version<Customer>> versions = Customer.find
        .query()
        .setId(21)
        .findVersionsBetween(start, end);

    for (Version<Customer> version : versions) {
      Customer bean = version.getBean();
      Map<String, ValuePair> diff = version.getDiff();
      Timestamp versionStart = version.getStart();
      Timestamp versionEnd = version.getEnd();
      System.out.println("BEAN: " + bean + " DIFF:" + diff + " START:" + versionStart + " END:" + versionEnd);
    }
  }

  private Timestamp parse(String dateTime) {
    OffsetDateTime as = OffsetDateTime.parse(dateTime);
    return new Timestamp(as.toInstant().toEpochMilli());
  }


  @Test
  public void dummyForDDLGeneration() {

    Ebean.getDefaultServer();
  }


}

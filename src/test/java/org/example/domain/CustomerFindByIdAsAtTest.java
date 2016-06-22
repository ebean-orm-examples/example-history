package org.example.domain;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ValuePair;
import com.avaje.ebean.Version;
import org.avaje.ebeantest.LoggedSql;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerFindByIdAsAtTest {


  @Test
  public void findJim() {

    OffsetDateTime as = OffsetDateTime.parse("2016-06-22T14:40:19.573+12:00");
    //OffsetDateTime as = OffsetDateTime.parse("2016-06-22T14:42:19.573+12:00");

    Timestamp asOf = new Timestamp(as.toInstant().toEpochMilli());

    Customer customer =
        Customer.find.query()
            .asOf(asOf)
            //.fetch("billingAddress")
            .where().eq("name", "jim")
            .findUnique();

    customer.getBillingAddress().getLine1();

    System.out.println("Jim: "+customer);
  }


  @Test
  public void exampleFindVersions() {


    Timestamp start = of("2016-06-22T14:40:19.573+12:00");
    Timestamp end = of("2016-06-22T14:42:19.573+12:00");

    List<Version<Customer>> versions = Customer.find
        .query()
        .setId(21)
        .findVersionsBetween(start, end);

    for (Version<Customer> version : versions) {
      Customer bean = version.getBean();
      Map<String, ValuePair> diff = version.getDiff();
      Timestamp versionStart = version.getStart();
      Timestamp versionEnd = version.getEnd();
      System.out.println("bean: " + bean + " diff:" + diff + " start:" + versionStart + " end:" + versionEnd);
    }
  }

  private Timestamp of(String dateTime) {
    OffsetDateTime as = OffsetDateTime.parse(dateTime);
    return new Timestamp(as.toInstant().toEpochMilli());
  }


  @Test
  public void dummyForDDLGeneration() {

    Ebean.getDefaultServer();
  }




  //@Ignore
  @Test
  public void oracle_test_queryAsOf() {

    long epochMilli = OffsetDateTime.now().minusMinutes(3).toInstant().toEpochMilli();
    Timestamp asOf = new Timestamp(epochMilli);

    LoggedSql.start();

    Customer customer =
        Customer.find.query()
            .asOf(asOf)
            .fetch("billingAddress")
            .where().eq("name", "jim")
            .findUnique();

    List<String> loggedSql = LoggedSql.stop();
    assertThat(loggedSql).hasSize(1);

    String sqlSelect = loggedSql.get(0);

    assertThat(sqlSelect.contains(" from customer as of TIMESTAMP ? t0 ")).isTrue();
    assertThat(sqlSelect.contains(" join o_address as of TIMESTAMP ? t1 ")).isTrue();

    System.out.println("customer: " +customer);
  }

  @Test
  public void test_queryAsOf() {

    long epochMilli = OffsetDateTime.now().minusMinutes(3).toInstant().toEpochMilli();
    Timestamp asOf = new Timestamp(epochMilli);

    LoggedSql.start();

    Customer customer =
        Customer.find.where()
            .asOf(asOf)
            .fetch("billingAddress")
            .name.eq("jim")
            .findUnique();

    List<String> loggedSql = LoggedSql.stop();
    assertThat(loggedSql).hasSize(1);

    String sqlSelect = loggedSql.get(0);

    assertThat(sqlSelect.contains(" from customer_with_history t0 ")).isTrue();
    assertThat(sqlSelect.contains(" join o_address_with_history t1 ")).isTrue();
    assertThat(sqlSelect.contains(" t0.sys_period @> ?::timestamptz and t1.sys_period @> ?::timestamptz; ")).isTrue();
    assertThat(sqlSelect.contains(" --bind(jim asOf ")).isTrue();


    System.out.println("customer: " +customer);

  }


  @Test
  public void test_queryAsOf_withSubsequentLazyLoad() {

    long epochMilli = OffsetDateTime.now().minusHours(0).toInstant().toEpochMilli();
    Timestamp asOf = new Timestamp(epochMilli);

    LoggedSql.start();

    Customer customer =
        Customer.find.query()
            .where().eq("name", "jack")
            .asOf(asOf)
            .findUnique();

    System.out.println("customer: " +customer);

    Address billingAddress = customer.getBillingAddress();
    // invoke lazy loading
    billingAddress.getCity();

    List<String> loggedSql = LoggedSql.stop();
    assertThat(loggedSql).hasSize(2);

    String sqlSelect1 = loggedSql.get(0);

    assertThat(sqlSelect1.contains(" from customer_with_history t0 ")).isTrue();
    assertThat(sqlSelect1.contains(" --bind(jack asOf ")).isTrue();
    assertThat(sqlSelect1.contains(" t0.sys_period @> ?::timestamptz")).isTrue();

    assertThat(!sqlSelect1.contains(" join o_address_with_history t1 ")).isTrue();
    assertThat(!sqlSelect1.contains(" t1.sys_period @> ?::timestamptz ")).isTrue();

    String sqlSelect2 = loggedSql.get(1);

    assertThat(sqlSelect2.contains(" from o_address_with_history t0 ")).isTrue();
    assertThat(sqlSelect2.contains(" t0.sys_period @> ?::timestamptz")).isTrue();
    assertThat(sqlSelect2.contains(" asOf ")).isTrue();

  }

  @Test
  public void test_noAsOf() {

    long epochMilli = OffsetDateTime.now().minusHours(3).toInstant().toEpochMilli();
    Timestamp asOf = new Timestamp(epochMilli);

    LoggedSql.start();

    Customer customer =
        Customer.find.query()
            .fetch("billingAddress")
            .where().eq("name", "jim")
            .findUnique();

    List<String> loggedSql = LoggedSql.stop();
    assertThat(loggedSql).hasSize(1);

    String sqlSelect = loggedSql.get(0);

    assertThat(sqlSelect.contains(" from customer t0 ")).isTrue();
    assertThat(sqlSelect.contains(" join o_address t1 ")).isTrue();
    assertThat(!sqlSelect.contains(" t0.sys_period @> ?::timestamptz and t1.sys_period @> ?::timestamptz; ")).isTrue();
    assertThat(!sqlSelect.contains(" --bind(jim asOf ")).isTrue();

    System.out.println("customer: " +customer);

  }


}

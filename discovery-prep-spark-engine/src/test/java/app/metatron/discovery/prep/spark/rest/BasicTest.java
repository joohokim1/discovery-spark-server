package app.metatron.discovery.prep.spark.rest;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class BasicTest {

  @Test
  public void testRename() {
    List<String> ruleStrings = new ArrayList();

    ruleStrings.add("rename col: _c0 to: new_colname");

    String dsUri = TestUtil.getResourcePath("csv/crime.csv");
    String ssUri = "/tmp/dataprep/snapshots/crime.snapshot.csv";

    TestUtil.testFileToCsv(dsUri, ruleStrings, ssUri);
  }

  @Test
  public void testHeader() {
    List<String> ruleStrings = new ArrayList();

    ruleStrings.add("header rownum: 1");
    ruleStrings.add("rename col: `Date` to: `DT`");

    String dsUri = TestUtil.getResourcePath("csv/crime.csv");
    String ssUri = "/tmp/dataprep/snapshots/crime.snapshot.csv";

    TestUtil.testFileToCsv(dsUri, ruleStrings, ssUri);
  }

  @Test
  public void testWeirdHeader() {
    List<String> ruleStrings = new ArrayList();

    ruleStrings.add("header rownum: 5");

    String dsUri = TestUtil.getResourcePath("csv/crime.csv");
    String ssUri = "/tmp/dataprep/snapshots/crime.snapshot.csv";

    TestUtil.testFileToCsv(dsUri, ruleStrings, ssUri);
  }

  @Test
  public void testDrop() {
    List<String> ruleStrings = new ArrayList();

    ruleStrings.add("header");
    ruleStrings.add("drop col: `Date`, `Location`");

    String dsUri = TestUtil.getResourcePath("csv/crime.csv");
    String ssUri = "/tmp/dataprep/snapshots/crime.snapshot.csv";

    TestUtil.testFileToCsv(dsUri, ruleStrings, ssUri);
  }

  @Test
  public void testKeep() {
    List<String> ruleStrings = new ArrayList();

    ruleStrings.add("header");
    ruleStrings.add("keep row: `Location` == 'NY'");

    String dsUri = TestUtil.getResourcePath("csv/crime.csv");
    String ssUri = "/tmp/dataprep/snapshots/crime.snapshot.csv";

    TestUtil.testFileToCsv(dsUri, ruleStrings, ssUri);
  }

  @Test
  public void testDelete() {
    List<String> ruleStrings = new ArrayList();

    ruleStrings.add("header");
    ruleStrings.add("delete row: `Location` == 'NY' || `Location` == 'CA' || `Location` == 'US'");

    String dsUri = TestUtil.getResourcePath("csv/crime.csv");
    String ssUri = "/tmp/dataprep/snapshots/crime.snapshot.csv";

    TestUtil.testFileToCsv(dsUri, ruleStrings, ssUri);
  }

  //  @Test
  public void testLargeFile() {
    List<String> ruleStrings = new ArrayList();

    ruleStrings.add("rename col: _c0 to: new_colname");

    String dsUri = "/tmp/dataprep/uploads/bigfile.csv";
    String ssUri = "/tmp/snapshots/bigfile.snapshot.csv";

    TestUtil.testFileToCsv(dsUri, ruleStrings, ssUri);
  }

  //  @Test
  public void testDocker() {
    List<String> ruleStrings = new ArrayList();

    ruleStrings.add("header rownum: 1");
    ruleStrings.add("rename col: `Date` to: `DT`");

    String dsUri = "/tmp/dataprep/uploads/crime.csv";
    String ssUri = "/tmp/dataprep/snapshots/crime.snapshot.csv";

    TestUtil.testFileToCsv(dsUri, ruleStrings, ssUri);
  }
}


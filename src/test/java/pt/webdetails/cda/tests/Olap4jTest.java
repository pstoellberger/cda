package pt.webdetails.cda.tests;

import java.io.File;
import java.io.OutputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.webdetails.cda.CdaBoot;
import pt.webdetails.cda.CdaEngine;
import pt.webdetails.cda.cache.IQueryCache;
import pt.webdetails.cda.cache.TableCacheKey;
import pt.webdetails.cda.dataaccess.Olap4JDataAccess;
import pt.webdetails.cda.query.QueryOptions;
import pt.webdetails.cda.settings.CdaSettings;
import pt.webdetails.cda.settings.SettingsManager;

/**
 * Created by IntelliJ IDEA.
 * User: pedro
 * Date: Feb 15, 2010
 * Time: 7:53:13 PM
 */
public class Olap4jTest extends TestCase
{

  private static final Log logger = LogFactory.getLog(Olap4jTest.class);

  public Olap4jTest()
  {
    super();
  }

  public Olap4jTest(final String name)
  {
    super(name);
  }


  protected void setUp() throws Exception
  {

    CdaBoot.getInstance().start();

    super.setUp();
  }


  public void testOlap4jQuery() throws Exception
  {

    OutputStream out = System.out;

    logger.info("Building CDA settings from sample file");

    final SettingsManager settingsManager = SettingsManager.getInstance();
    URL file = this.getClass().getResource("sample-olap4j.cda");
    File f = new File(file.toURI());
    final CdaSettings cdaSettings = settingsManager.parseSettingsFile(f.getAbsolutePath());
    logger.debug("Doing query on Cda - Initializing CdaEngine");
    final CdaEngine engine = CdaEngine.getInstance();

    final QueryOptions queryOptions = new QueryOptions();
    queryOptions.setDataAccessId("2");
    queryOptions.setOutputType("json");
    queryOptions.addParameter("status", "Shipped");

    logger.info("Doing query");
    engine.doQuery(out, cdaSettings, queryOptions);
    logger.info("\nChecking cache");
    boolean hasCash = false;
    
    String query = ((Olap4JDataAccess) cdaSettings.getDataAccess("2")).getQuery();
    IQueryCache cache = CdaEngine.getInstance().getEnvironment().getQueryCache();
    logger.info("Cache cleared!");
    cache.clearCache();
    engine.doQuery(out, cdaSettings, queryOptions);
    engine.doQuery(out, cdaSettings, queryOptions);
    for (TableCacheKey key : cache.getKeys()) {
	   assertEquals(key.getQuery(), query);
	   logger.info("Found query in cache! Query:" + query);
	   hasCash = true;
    }
    assertTrue(hasCash);
    logger.info("Found Query in Cache!");

  }
}

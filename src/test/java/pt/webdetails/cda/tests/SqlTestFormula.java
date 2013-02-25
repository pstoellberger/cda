package pt.webdetails.cda.tests;

import java.io.File;
import java.io.OutputStream;
import java.util.Calendar;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;

import pt.webdetails.cda.CdaBoot;
import pt.webdetails.cda.CdaEngine;
import pt.webdetails.cda.connections.UnsupportedConnectionException;
import pt.webdetails.cda.dataaccess.QueryException;
import pt.webdetails.cda.dataaccess.UnsupportedDataAccessException;
import pt.webdetails.cda.exporter.ExporterException;
import pt.webdetails.cda.exporter.UnsupportedExporterException;
import pt.webdetails.cda.query.QueryOptions;
import pt.webdetails.cda.settings.CdaSettings;
import pt.webdetails.cda.settings.SettingsManager;
import pt.webdetails.cda.settings.UnknownDataAccessException;

public class SqlTestFormula extends TestCase {
	
  private static final Log logger = LogFactory.getLog(SqlTest.class);

  public SqlTestFormula()
  {
    super();
  }

  public SqlTestFormula(final String name)
  {
    super(name);
  }


  protected void setUp() throws Exception
  {

    CdaBoot.getInstance().start();

    super.setUp();
  }


  public void testFormulaCacheSql() throws ExporterException, UnknownDataAccessException, UnsupportedExporterException, QueryException, UnsupportedConnectionException, DocumentException, UnsupportedDataAccessException
  {
    // Define an outputStream
    OutputStream out = System.out;

    logger.info("Building CDA settings from sample file");

    final SettingsManager settingsManager = SettingsManager.getInstance();

    final File settingsFile = new File("test/pt/webdetails/cda/tests/sample-sql-formula.cda");
    final CdaSettings cdaSettings = settingsManager.parseSettingsFile(settingsFile.getAbsolutePath());
    logger.debug("Doing query on Cda - Initializing CdaEngine");
    final CdaEngine engine = CdaEngine.getInstance();

    QueryOptions queryOptions = new QueryOptions();
    queryOptions.setDataAccessId("1");
    queryOptions.addParameter("orderDate", "${TODAY()}");
    queryOptions.setOutputType("csv");

    logger.info("Doing first query --> TODAY()");
    engine.doQuery(out, cdaSettings, queryOptions);
    
    logger.info("Doing query with different parameters");
    queryOptions = new QueryOptions();
    queryOptions.setDataAccessId("1");
    queryOptions.addParameter("orderDate", "${DATE(2004;1;1)}");
    engine.doQuery(out, cdaSettings, queryOptions);

    // Querying 2nd time to test cache (formula translated before cache check)
    logger.info("Doing query using manual TODAY - Cache should be used");
    queryOptions = new QueryOptions();
    queryOptions.setDataAccessId("1");
    Calendar cal = Calendar.getInstance();
    queryOptions.addParameter("orderDate", "${DATE(" + cal.get(Calendar.YEAR) + ";" + (cal.get(Calendar.MONTH) + 1) + ";" + cal.get(Calendar.DAY_OF_MONTH) + ")}");
    engine.doQuery(out, cdaSettings, queryOptions);

  }
	
	
}

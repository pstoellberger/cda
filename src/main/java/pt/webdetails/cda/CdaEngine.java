/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cda;

import java.io.OutputStream;
<<<<<<< HEAD:src/main/java/pt/webdetails/cda/CdaEngine.java
=======
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
>>>>>>> 687890e0d88d29c401da2763b897fbd7b04b78d0:src/pt/webdetails/cda/CdaEngine.java

import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

import pt.webdetails.cda.dataaccess.QueryException;
import pt.webdetails.cda.discovery.DiscoveryOptions;
import pt.webdetails.cda.exporter.ExporterEngine;
import pt.webdetails.cda.exporter.ExporterException;
import pt.webdetails.cda.exporter.UnsupportedExporterException;
import pt.webdetails.cda.query.QueryOptions;
import pt.webdetails.cda.settings.CdaSettings;
import pt.webdetails.cda.settings.UnknownDataAccessException;

/**
 * Main engine class that will answer to calls
 * <p/>
 * Created by IntelliJ IDEA.
 * User: pedro
 * Date: Feb 2, 2010
 * Time: 2:24:16 PM
 */
public class CdaEngine
{

  private static final Log logger = LogFactory.getLog(CdaEngine.class);
  private static CdaEngine _instance;
  

  private ICdaEnvironment environment;


  private Map<UUID, QueryOptions> wrappedQueries = new HashMap<UUID, QueryOptions>();


  protected CdaEngine(ICdaEnvironment environment)
  {
    logger.info("Initializing CdaEngine");
	this.environment = environment;
    init();

  }

  public QueryOptions unwrapQuery(String uuid) throws UnknownDataAccessException, QueryException, UnsupportedExporterException, ExporterException
  {
    return wrappedQueries.remove(UUID.fromString(uuid));
  }

  public String wrapQuery(
      final OutputStream out,
      final CdaSettings cdaSettings,
      final QueryOptions queryOptions)
  {
    UUID uuid = UUID.randomUUID();
    wrappedQueries.put(uuid, queryOptions);
    return uuid.toString();
  }

  public void doQuery(final OutputStream out,
          final CdaSettings cdaSettings,
          final QueryOptions queryOptions) throws UnknownDataAccessException, QueryException, UnsupportedExporterException, ExporterException
  {

    logger.debug("Doing query on CdaSettings [ " + cdaSettings.getId() + " (" + queryOptions.getDataAccessId() + ")]");

    TableModel tableModel = cdaSettings.getDataAccess(queryOptions.getDataAccessId()).doQuery(queryOptions);

    // Handle the exports

    ExporterEngine.getInstance().getExporter(queryOptions.getOutputType(), queryOptions.getExtraSettings()).export(out, tableModel);

  }


  public void listQueries(final OutputStream out,
          final CdaSettings cdaSettings,
          final DiscoveryOptions discoveryOptions) throws UnsupportedExporterException, ExporterException
  {

    logger.debug("Getting list of queries on CdaSettings [ " + cdaSettings.getId() + ")]");


    final TableModel tableModel = cdaSettings.listQueries(discoveryOptions);

    // Handle the exports

    ExporterEngine.getInstance().getExporter(discoveryOptions.getOutputType()).export(out, tableModel);

  }


  public void listParameters(final OutputStream out,
          final CdaSettings cdaSettings,
          final DiscoveryOptions discoveryOptions) throws UnknownDataAccessException, UnsupportedExporterException, ExporterException
  {

    logger.debug("Getting list of queries on CdaSettings [ " + cdaSettings.getId() + ")]");


    final TableModel tableModel = cdaSettings.getDataAccess(discoveryOptions.getDataAccessId()).listParameters(discoveryOptions);

    // Handle the exports

    ExporterEngine.getInstance().getExporter(discoveryOptions.getOutputType()).export(out, tableModel);


  }

  private void init()
  {

    // Start ClassicEngineBoot
    CdaBoot.getInstance().start();
    ClassicEngineBoot.getInstance().start();


  }


  public static synchronized CdaEngine getInstance()
  {
    if (_instance == null)
    {
      ICdaEnvironment env = new DefaultCdaEnvironment();
      _instance = new CdaEngine(env);
    }
    return _instance;
  }
  
  public ICdaEnvironment getEnvironment() {
	  return environment;
  }
  
}

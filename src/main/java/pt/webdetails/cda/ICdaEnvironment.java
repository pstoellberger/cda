package pt.webdetails.cda;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractNamedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdConnectionProvider;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import pt.webdetails.cda.cache.IQueryCache;
import pt.webdetails.cda.connections.kettle.TransFromFileConnectionInfo;
import pt.webdetails.cda.connections.mondrian.MondrianConnectionInfo;
import pt.webdetails.cda.connections.mondrian.MondrianJndiConnectionInfo;
import pt.webdetails.cda.connections.sql.SqlJndiConnectionInfo;
import pt.webdetails.cda.events.IEventPublisher;

public interface ICdaEnvironment {

	public KettleTransformationProducer getKettleTransformationProducer(
			TransFromFileConnectionInfo connectionInfo, String query);

	public CubeFileProvider getCubeFileProvider(
			AbstractNamedMDXDataFactory mdxDataFactory,
			MondrianConnectionInfo mondrianConnectionInfo);

	public PmdConnectionProvider getPmdConnectionProvider();

	public ConnectionProvider getJndiConnectionProvider(
			SqlJndiConnectionInfo connectionInfo);

	public DataSourceProvider getMondrianJndiDatasourceProvider(
			MondrianJndiConnectionInfo connectionInfo);

	public ReportEnvironmentDataRow getReportEnvironmentDataRow(
			Configuration configuration);

	public IQueryCache getQueryCache();

	public String computeMondrianRole(String catalog);

	public String getCdaConfigFile(String fileName);

	public FormulaContext getFormulaContext();

	public ResourceKey createKey(ResourceManager resourceManager, String id)
			throws ResourceKeyCreationException;

	public Long getCdaFileLastModified(String id);

	public Properties getCdaComponents();

	public String getPath(String templateName);

	public List<URL> getComponentsFiles();
  
  
  public IEventPublisher getEventPublisher();

}
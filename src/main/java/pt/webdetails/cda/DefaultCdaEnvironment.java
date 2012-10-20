package pt.webdetails.cda;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.table.TableModel;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.core.states.datarow.EmptyTableModel;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromFileProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractNamedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DefaultCubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.JndiDataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdConnectionProvider;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import pt.webdetails.cda.cache.EHCacheQueryCache;
import pt.webdetails.cda.cache.IQueryCache;
import pt.webdetails.cda.connections.kettle.TransFromFileConnectionInfo;
import pt.webdetails.cda.connections.mondrian.MondrianConnectionInfo;
import pt.webdetails.cda.connections.mondrian.MondrianJndiConnectionInfo;
import pt.webdetails.cda.connections.sql.SqlJndiConnectionInfo;
import pt.webdetails.cda.discovery.DiscoveryOptions;
import pt.webdetails.cda.exporter.ExporterEngine;

public class DefaultCdaEnvironment implements ICdaEnvironment {

	private static Log logger = LogFactory.getLog(DefaultCdaEnvironment.class);
	
	
	public DefaultCdaEnvironment() {
		// PENTAHO
		// SolutionReposHelper.setSolutionRepositoryThreadVariable(PentahoSystem.get(ISolutionRepository.class, PentahoSessionHolder.getSession()));
	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getKettleTransformationProducer(pt.webdetails.cda.connections.kettle.TransFromFileConnectionInfo, java.lang.String)
	 */
	public KettleTransformationProducer getKettleTransformationProducer(
			TransFromFileConnectionInfo connectionInfo, String query) {
	      return new KettleTransFromFileProducer("",
	              connectionInfo.getTransformationFile(),
	              query, null, null, connectionInfo.getDefinedArgumentNames(),
	              connectionInfo.getDefinedVariableNames());
//	    PENTAHO
//	    return new PentahoKettleTransFromFileProducer("",
//	            connectionInfo.getTransformationFile(),
//	            query, null, null, connectionInfo.getDefinedArgumentNames(),
//	            connectionInfo.getDefinedVariableNames());

	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getCubeFileProvider(org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractNamedMDXDataFactory, pt.webdetails.cda.connections.mondrian.MondrianConnectionInfo)
	 */
	public CubeFileProvider getCubeFileProvider(
			AbstractNamedMDXDataFactory mdxDataFactory,
			MondrianConnectionInfo mondrianConnectionInfo) {

	    return new DefaultCubeFileProvider(mondrianConnectionInfo.getCatalog());
	    
//		PENTAHO
//	      mdxDataFactory.setCubeFileProvider(new PentahoCubeFileProvider(mondrianConnectionInfo.getCatalog()));
//	      try
//	      {
//	        mdxDataFactory.setMondrianConnectionProvider((MondrianConnectionProvider) PentahoSystem.getObjectFactory().get(PentahoMondrianConnectionProvider.class, "MondrianConnectionProvider", null));
//	      }
//	      catch (ObjectFactoryException e)
//	      {//couldn't get object
//	        mdxDataFactory.setMondrianConnectionProvider(new PentahoMondrianConnectionProvider());
//	      }


	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getPmdConnectionProvider()
	 */
	public PmdConnectionProvider getPmdConnectionProvider() {
		return new PmdConnectionProvider();
//		PENTAHO
//		return new new PentahoPmdConnectionProvider();

	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getJndiConnectionProvider(pt.webdetails.cda.connections.sql.SqlJndiConnectionInfo)
	 */
	public ConnectionProvider getJndiConnectionProvider(SqlJndiConnectionInfo connectionInfo) {

	      final JndiConnectionProvider provider = new JndiConnectionProvider();
	      provider.setConnectionPath(connectionInfo.getJndi());
	      provider.setUsername(connectionInfo.getUser());
	      provider.setPassword(connectionInfo.getPass());
	      return provider;
//	      PENTAHO
//	      final PentahoJndiDatasourceConnectionProvider provider = new PentahoJndiDatasourceConnectionProvider();
//	      provider.setJndiName(connectionInfo.getJndi());
//	      provider.setUsername(connectionInfo.getUser());
//	      provider.setPassword(connectionInfo.getPass());
//	      return provider;
	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getMondrianJndiDatasourceProvider(pt.webdetails.cda.connections.mondrian.MondrianJndiConnectionInfo)
	 */
	public DataSourceProvider getMondrianJndiDatasourceProvider(
			MondrianJndiConnectionInfo connectionInfo) {

		return new JndiDataSourceProvider(connectionInfo.getJndi());
	    
//		PENTAHO
//	      return new PentahoMondrianDataSourceProvider(connectionInfo.getJndi());
	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getReportEnvironmentDataRow(org.pentaho.reporting.libraries.base.config.Configuration)
	 */
	public ReportEnvironmentDataRow getReportEnvironmentDataRow(
			Configuration configuration) {

		return new ReportEnvironmentDataRow(new DefaultReportEnvironment(configuration));
        
//		PENTAHO
        //TODO:testing, TEMP
//        // Make sure we have the env. correctly inited
//        if (SolutionReposHelper.getSolutionRepositoryThreadVariable() == null && PentahoSystem.getObjectFactory().objectDefined(ISolutionRepository.class.getSimpleName()))
//        {
//          threadVarSet = true;
//          SolutionReposHelper.setSolutionRepositoryThreadVariable(PentahoSystem.get(ISolutionRepository.class, PentahoSessionHolder.getSession()));
//        }
//          environmentDataRow = new ReportEnvironmentDataRow(new PentahoReportEnvironment(configuration));
     
	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getQueryCache()
	 */
	public IQueryCache getQueryCache() {
		// TODO we should make this a property in cda.properties and use reflection to get the cache
		return new EHCacheQueryCache();
		
//		PENTAHO
//	      try {
//	          cache = PluginUtils.getPluginBean("cda.", IQueryCache.class);
//	        } catch (Exception e) {
//	          logger.error(e.getMessage());
//	        }
//	        if(cache == null){
//	          //fallback
//	          cache = new EHCacheQueryCache(); 
//	        }

	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#computeMondrianRole(java.lang.String)
	 */
	public synchronized String computeMondrianRole(String catalog) {
		// TODO add something for standalone? this should be pluggable!
		return "";

//		  if (PentahoSystem.getObjectFactory().objectDefined(MDXConnection.MDX_CONNECTION_MAPPER_KEY)) {
//			  final IConnectionUserRoleMapper mondrianUserRoleMapper =
//				  PentahoSystem.get(IConnectionUserRoleMapper.class, MDXConnection.MDX_CONNECTION_MAPPER_KEY, null);
//
//			  final String[] validMondrianRolesForUser =
//				  mondrianUserRoleMapper.mapConnectionRoles(PentahoSessionHolder.getSession(), "solution:" + catalog.replaceAll("solution/",""));
//			  if ((validMondrianRolesForUser != null) && (validMondrianRolesForUser.length > 0))
//			  {
//				  final StringBuffer buff = new StringBuffer();
//				  for (int i = 0; i < validMondrianRolesForUser.length; i++)
//				  {
//					  final String aRole = validMondrianRolesForUser[i];
//					  // According to http://mondrian.pentaho.org/documentation/configuration.php
//					  // double-comma escapes a comma
//					  if (i > 0)
//					  {
//						  buff.append(",");
//					  }
//					  buff.append(aRole.replaceAll(",", ",,"));
//				  }
//				  logger.debug("Assembled role: " + buff.toString() + " for catalog: " + catalog);
//				  return buff.toString();
//			  }

		
	}

	
	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getCdaConfigFile(java.lang.String)
	 */
	public String getCdaConfigFile(String fileName) {
		
		URL cfgFile = CdaBoot.class.getResource(fileName);
		return  cfgFile.toExternalForm();
		
//		PENTAHO
//		private  final String PLUGIN_PATH = "system/" + CdaContentGenerator.PLUGIN_NAME + "/";
//		PentahoSystem.getApplicationContext().getSolutionPath(PLUGIN_PATH + cacheConfigFile);
		
		
	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getFormulaContext()
	 */
	public FormulaContext getFormulaContext() {
		return new DefaultFormulaContext();
//		pentaho
//		return new CdaSessionFormulaContext(PentahoSessionHolder.getSession())
		
	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#createKey(org.pentaho.reporting.libraries.resourceloader.ResourceManager, java.lang.String)
	 */
	public ResourceKey createKey(ResourceManager resourceManager, String id) throws ResourceKeyCreationException {
	          File settingsFile = new File(id);
	          return resourceManager.createKey(settingsFile);
//	          PENTAHO
//	          final HashMap<String, Object> helperObjects = new HashMap<String, Object>();
//	          key = resourceManager.createKey(SOLUTION_SCHEMA_NAME + SCHEMA_SEPARATOR + id, helperObjects);
	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getCdaFileLastModified(java.lang.String)
	 */
	public Long getCdaFileLastModified(String id) {
	      File cdaFile = new File(id);
	      if(cdaFile.exists()){
	        return cdaFile.lastModified();
	      }
	      return null;
//	      PENTAHO
//	      ISolutionFile savedCda = RepositoryAccess.getRepository().getSolutionFile(id, FileAccess.NONE);
//	      if(savedCda != null) return savedCda.getLastModified();
//		return null;
	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getCdaComponents()
	 */
	public Properties getCdaComponents() {
		Properties components = new Properties();
		InputStream in = null;
		try {
			
		in = CdaBoot.class.getResourceAsStream("components.properties");
		components.load(in);
		
		// PENTAHO
	    //final File file = new File(PentahoSystem.getApplicationContext().getSolutionPath("system/cda/resources/components.properties"));
		
		} catch(Exception e) {
			logger .error("Cannot load components.properties");
		} finally {
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
		}
		return components;
	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getPath(java.lang.String)
	 */
	public String getPath(String templateName) {
		return templateName;
//		PENTAHO
//		templateName = RepositoryAccess.getSolutionPath(templateName);
	}

	/* (non-Javadoc)
	 * @see pt.webdetails.cda.ICdaEnvironment#getComponentsFiles()
	 */
	public List<URL> getComponentsFiles() {
		Properties resources = getCdaComponents();
		String[] connections = StringUtils.split(StringUtils.defaultString(resources.getProperty("connections")), ",");
		List<URL> fileNames = new ArrayList<URL>();
		if(connections != null) {
			for(String con : connections) {
				try {
					URL conUrl = CdaEngine.class.getResource("connections/" + con + ".xml");
					File conFile = new File(conUrl.toURI());
					if (conFile.exists()) {
						fileNames.add(conUrl);
					}
				} catch(Exception e) {
					logger.debug("Cant access connections file for: " + con);
				}
			}
		}
		return fileNames;
		
		// PENTAHO    
	    // File dir = new File(PLUGIN_DIR + "/resources/components/connections");
//	    
//		FilenameFilter xmlFiles = new FilenameFilter() {
//		      public boolean accept(File dir, String name) {
//		        return !name.startsWith(".") && name.endsWith(".xml");
//		      }
//		    };
//	    String[] files = dir.list(xmlFiles);
	}
	

	  public void getCdaList(final OutputStream out, final DiscoveryOptions discoveryOptions) throws Exception
	  {

		final TableModel tableModel = new EmptyTableModel();
		ExporterEngine.getInstance().getExporter(discoveryOptions.getOutputType()).export(out, tableModel);
		// PENTAHO
	    //final TableModel tableModel = SolutionRepositoryUtils.getInstance().getCdaList(userSession);
	    //ExporterEngine.getInstance().getExporter(discoveryOptions.getOutputType()).export(out, tableModel);

	  }


}

package pt.webdetails.cda;

import java.net.URL;

import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
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

import pt.webdetails.cda.cache.EHCacheQueryCache;
import pt.webdetails.cda.cache.IQueryCache;
import pt.webdetails.cda.connections.kettle.TransFromFileConnectionInfo;
import pt.webdetails.cda.connections.mondrian.MondrianConnectionInfo;
import pt.webdetails.cda.connections.mondrian.MondrianJndiConnectionInfo;
import pt.webdetails.cda.connections.sql.SqlJndiConnectionInfo;
import pt.webdetails.cda.deprecated.CdaContentGenerator;
import pt.webdetails.cda.deprecated.CdaSessionFormulaContext;

public class CdaEnvironment {

	public static KettleTransformationProducer getKettleTransformationProducer(
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

	public static CubeFileProvider getCubeFileProvider(
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

	public static PmdConnectionProvider getPmdConnectionProvider() {
		return new PmdConnectionProvider();
//		PENTAHO
//		return new new PentahoPmdConnectionProvider();

	}

	public static ConnectionProvider getJndiConnectionProvider(SqlJndiConnectionInfo connectionInfo) {

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

	public static DataSourceProvider getMondrianJndiDatasourceProvider(
			MondrianJndiConnectionInfo connectionInfo) {

		return new JndiDataSourceProvider(connectionInfo.getJndi());
	    
//		PENTAHO
//	      return new PentahoMondrianDataSourceProvider(connectionInfo.getJndi());
	}

	public static ReportEnvironmentDataRow getReportEnvironmentDataRow(
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

	public static IQueryCache getQueryCache() {
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

	public static synchronized String computeMondrianRole(String catalog) {
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

	
	public static String getCdaConfigFile(String fileName) {
		
		URL cfgFile = CdaBoot.class.getResource(fileName);
		return  cfgFile.toExternalForm();
		
//		PENTAHO
//		private static final String PLUGIN_PATH = "system/" + CdaContentGenerator.PLUGIN_NAME + "/";
//		PentahoSystem.getApplicationContext().getSolutionPath(PLUGIN_PATH + cacheConfigFile);
		
		
	}

	public static FormulaContext getFormulaContext() {
		return new DefaultFormulaContext();
//		
//		return new CdaSessionFormulaContext(PentahoSessionHolder.getSession())
		
	}

}

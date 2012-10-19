package pt.webdetails.cda.deprecated;



/**
 * This class inits Cda plugin within the bi-platform
 * @author gorman
 *
 */
@Deprecated
public class CdaLifecycleListener
{

//  static Log logger = LogFactory.getLog(CacheScheduleManager.class);
//
//
//  public void init() throws PluginLifecycleException
//  {
//    // boot cda
//    CdaBoot.getInstance().start();
//    PluginHibernateUtil.initialize();
//  }
//
//
//  public void loaded() throws PluginLifecycleException
//  {
//    ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
//    try
//    {
//      Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
//      CacheScheduleManager.getInstance().coldInit();
//    }
//    catch (Exception e)
//    {
//      logger.error(Util.getExceptionDescription(e));
//    }
//    finally
//    {
//      Thread.currentThread().setContextClassLoader(contextCL);
//    }
//  }
//
//
//  public void unLoaded() throws PluginLifecycleException
//  {
//  }
}

package pt.webdetails.cda.deprecated;

//import java.io.InputStream;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//import java.util.MissingResourceException;
//import java.util.PropertyResourceBundle;
//import java.util.ResourceBundle;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.pentaho.platform.api.engine.IPentahoSession;
//import org.pentaho.platform.engine.core.system.StandaloneSession;
//import org.pentaho.platform.util.logging.Logger;
//import org.pentaho.platform.util.messages.LocaleHelper;
//import org.pentaho.platform.util.messages.MessageUtil;
//
//import pt.webdetails.cda.deprecated.CdaContentGenerator;
//import pt.webdetails.cda.tests.SqlTest;
//import pt.webdetails.cpf.repository.RepositoryAccess;

/**
 * Utility class for internationalization
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
@Deprecated
public class Messages {
	
//  private static final Map<Locale,ResourceBundle> locales = Collections.synchronizedMap(new HashMap<Locale,ResourceBundle>());
//  
//  private static final Log logger = LogFactory.getLog(Messages.class);
//
//  private static ResourceBundle getBundle() {
//    Locale locale = LocaleHelper.getLocale();
//    ResourceBundle bundle = Messages.locales.get(locale);
//    if (bundle == null) {
//      IPentahoSession session = new StandaloneSession("dashboards messages"); //$NON-NLS-1$
//      InputStream in = null;
//      String propertiesFile = "system/" + CdaContentGenerator.PLUGIN_NAME + "/messages.properties";//$NON-NLS-1$ //$NON-NLS-2$
//      try {
//        in = RepositoryAccess.getRepository(session).getResourceInputStream(propertiesFile);  
//        bundle = new PropertyResourceBundle(in);
//        Messages.locales.put(locale, bundle);
//      } catch (Exception e) {
//        logger.error("Could not get localization bundle", e); //$NON-NLS-1$
//      } finally {
//        IOUtils.closeQuietly(in);
//      }
//    }
//    return bundle;
//  }
//
//  public static String getEncodedString(final String rawValue) {
//    if (rawValue == null) {
//      return (""); //$NON-NLS-1$
//    }
//
//    StringBuffer value = new StringBuffer();
//    for (int n = 0; n < rawValue.length(); n++) {
//      int charValue = rawValue.charAt(n);
//      if (charValue >= 0x80) {
//        value.append("&#x"); //$NON-NLS-1$
//        value.append(Integer.toString(charValue, 0x10));
//        value.append(";"); //$NON-NLS-1$
//      } else {
//        value.append((char) charValue);
//      }
//    }
//    return value.toString();
//
//  }
//
//  public static String getXslString(final String key) {
//    String rawValue = Messages.getString(key);
//    return Messages.getEncodedString(rawValue);
//  }
//  
//  public static String getString(final String key, final String... params){
//  	if(params.length == 0){
//      try {
//        return Messages.getBundle().getString(key);
//      } catch (MissingResourceException e) {
//        return '!' + key + '!';
//      }
//  	}
//  	return MessageUtil.getString(Messages.getBundle(), key, params);
//  }
//  
//  public static String getErrorString(final String key, final String... params){
//  	if (params.length == 0) return MessageUtil.formatErrorMessage(key, Messages.getString(key));
//  	else return MessageUtil.getErrorString(Messages.getBundle(), key, params);
//  }

}

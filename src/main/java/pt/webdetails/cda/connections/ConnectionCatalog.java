/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cda.connections;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import pt.webdetails.cda.CdaEngine;
import pt.webdetails.cda.connections.ConnectionCatalog.ConnectionType;

/**
 *
 * @author pdpi
 */
public class ConnectionCatalog {

  public enum ConnectionType {

    SQL, MQL, MDX, OLAP4J, SCRIPTING, NONE, XPATH, KETTLE
  };
  private static ConnectionCatalog _instance;
  private static Log logger = LogFactory.getLog(ConnectionCatalog.class);

  private HashMap<String, ConnectionInfo> connectionPool;

  public ConnectionCatalog() {
    getConnections();
  }

  private void getConnections() {
    connectionPool = new HashMap<String, ConnectionInfo>();
    
    logger.debug("Reading connections file!!! ");
    List<URL> files = CdaEngine.getInstance().getEnvironment().getComponentsFiles();
    if (files != null && files.size() > 0) {
      for (URL file : files) {
        try {
        	System.out.println("Reading connections file: " + file);
        	SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            // To figure out whether the component is generic or has a special implementation,
            // we directly look for the class override in the definition
            
        	Node node = document.selectSingleNode( "//Connection/Implementation" );
        	String className = node.getText();
        	if (className != null) {
        		Connection connection = connectionFromClass(className);
        		if (connection != null) {
        			Node type = document.selectSingleNode( "//Connection/Type" );
                	String connectionType = type.getText();
        			ConnectionType ct = ConnectionType.valueOf(connectionType);
        			connectionPool.put(connection.getClass().toString(), new ConnectionInfo(ct, connection.getClass()));
        		}
            }
        } catch (Exception e) {
          logger.error(e);
        }
      }
    }
  }

  public Connection[] getConnectionsByType(ConnectionType type) {
    ArrayList<Connection> conns = new ArrayList<Connection>();
    for (String key : connectionPool.keySet()) {
      ConnectionInfo conn = connectionPool.get(key);
      if (conn.getType() == type) {
        try {
          conns.add(conn.getImplementation().getConstructor().newInstance());
        } catch (Exception ex) {
          logger.error("Couldn't instantiate " + conn.toString());
        }
      }
    }
    return conns.toArray(new Connection[conns.size()]);
  }

  private Connection connectionFromClass(String className) {
    Connection connection = null;
    try {
      Class<?> cClass = Class.forName(className);
      if (!cClass.isInterface() && Connection.class.isAssignableFrom(cClass)) {
        connection = (Connection) cClass.newInstance();
      }
    } catch (Exception ex) {
      logger.error(ex);
    }
    return connection;
  }

  public synchronized static ConnectionCatalog getInstance(boolean refreshCache) {
    if (_instance == null || refreshCache) {
      _instance = new ConnectionCatalog();
    }
    return _instance;
  }
}

class ConnectionInfo {

  public Class<? extends Connection> getImplementation() {
    return implementation;
  }

  public ConnectionType getType() {
    return type;
  }
  private ConnectionType type;
  private Class<? extends Connection> implementation;

  public ConnectionInfo(ConnectionType type, Class<? extends Connection> implementation) {
    this.type = type;
    this.implementation = implementation;
  }
}

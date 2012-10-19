/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cda.dataaccess;

import org.dom4j.Element;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactory;
import pt.webdetails.cda.connections.ConnectionCatalog.ConnectionType;
import pt.webdetails.cda.connections.InvalidConnectionException;
import pt.webdetails.cda.connections.scripting.ScriptingConnection;
import pt.webdetails.cda.settings.UnknownConnectionException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 16.02.2010
 * Time: 13:20:39
 *
 * @author Thomas Morgner.
 */
public class ScriptableDataAccess extends PREDataAccess {

  public ScriptableDataAccess(final Element element) {
    super(element);
  }

  public ScriptableDataAccess() {
  }

  public DataFactory getDataFactory() throws UnknownConnectionException, InvalidConnectionException {
    final ScriptingConnection connection = (ScriptingConnection) getCdaSettings().getConnection(getConnectionId());

    final ScriptableDataFactory dataFactory = new ScriptableDataFactory();
    dataFactory.setLanguage(connection.getConnectionInfo().getLanguage());
    dataFactory.setScript(connection.getConnectionInfo().getInitScript());

    dataFactory.setQuery("query", getQuery());
    return dataFactory;
  }

  public String getType() {
    return "scriptable";
  }

  @Override
  public ConnectionType getConnectionType() {
    return ConnectionType.SCRIPTING;
  }
}

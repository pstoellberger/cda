/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.solution.PentahoSessionParameterProvider;
import org.pentaho.platform.engine.core.solution.SystemSettingsParameterProvider;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityParameterProvider;
import org.pentaho.platform.plugin.services.connections.javascript.JavaScriptResultSet;

public class CdaSessionFormulaContext extends DefaultFormulaContext
  {

    Map<String, IParameterProvider> providers;
    private static final String SECURITY_PREFIX = "security:";
    private static final String SESSION_PREFIX = "session:";
    private static final String SYSTEM_PREFIX = "system:";


    public CdaSessionFormulaContext(IPentahoSession session)
    {
      providers = new HashMap<String, IParameterProvider>();
      if (session != null)
      {
        providers.put(SECURITY_PREFIX, new SecurityParameterProvider(session));
        providers.put(SESSION_PREFIX, new PentahoSessionParameterProvider(session));
      }
      providers.put(SYSTEM_PREFIX, new SystemSettingsParameterProvider());
    }
    
    public CdaSessionFormulaContext()
    {
      this(PentahoSessionHolder.getSession());
    }


    @Override
    public Object resolveReference(final Object name)
    {
      if (name instanceof String)
      {
        String paramName = ((String) name).trim();
        for (String prefix : providers.keySet())
        {
          if (paramName.startsWith(prefix))
          {
            paramName = paramName.substring(prefix.length());
            Object value = providers.get(prefix).getParameter(paramName);
            if (value instanceof JavaScriptResultSet)
            {//needs special treatment, convert to array
              JavaScriptResultSet resultSet = (JavaScriptResultSet) value;
              return convertToArray(resultSet);
            }
            return value;
          }
        }
      }
      return super.resolveReference(name);
    }


    private Object[] convertToArray(final JavaScriptResultSet resultSet)
    {
      List<Object> result = new ArrayList<Object>();
      for (int i = 0; i < resultSet.getRowCount(); i++)
      {
        for (int j = 0; j < resultSet.getColumnCount(); j++)
        {
          result.add(resultSet.getValueAt(i, j));
        }
      }
      return result.toArray();
    }
  }


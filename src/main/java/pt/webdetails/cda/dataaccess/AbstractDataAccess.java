/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cda.dataaccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.swing.table.TableModel;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import pt.webdetails.cda.cache.EHCacheQueryCache;
import pt.webdetails.cda.cache.IQueryCache;
import pt.webdetails.cda.connections.Connection;
import pt.webdetails.cda.connections.ConnectionCatalog;
import pt.webdetails.cda.connections.ConnectionCatalog.ConnectionType;
import pt.webdetails.cda.deprecated.PluginUtils;
import pt.webdetails.cda.discovery.DiscoveryOptions;
import pt.webdetails.cda.query.QueryOptions;
import pt.webdetails.cda.settings.CdaSettings;
import pt.webdetails.cda.settings.UnknownDataAccessException;
import pt.webdetails.cda.utils.InvalidOutputIndexException;
import pt.webdetails.cda.utils.TableModelUtils;
import pt.webdetails.cda.utils.Util;
import pt.webdetails.cda.utils.kettle.SortException;

/**
 * This is the top level implementation of a DataAccess. Only the common methods are used here
 * <p/>
 * User: pedro
 * Date: Feb 3, 2010
 * Time: 11:05:38 AM
 */
public abstract class AbstractDataAccess implements DataAccess
{

  private static final Log logger = LogFactory.getLog(AbstractDataAccess.class);
//  private static CacheManager cacheManager;
  private static IQueryCache cache;
  
  private CdaSettings cdaSettings;
  private String id;
  private String name;
  private DataAccessEnums.ACCESS_TYPE access = DataAccessEnums.ACCESS_TYPE.PUBLIC;
  private boolean cacheEnabled = false;
  private int cacheDuration = 3600;
  private ArrayList<Parameter> parameters;
  private HashMap<Integer, OutputMode> outputMode;
  private HashMap<Integer, ArrayList<Integer>> outputs;
  private ArrayList<ColumnDefinition> columnDefinitions;
  protected HashMap<Integer, ColumnDefinition> columnDefinitionIndexMap;

  private static final String PARAM_ITERATOR_BEGIN = "$FOREACH(";
  private static final String PARAM_ITERATOR_END = ")";
  private static final String PARAM_ITERATOR_ARG_SEPARATOR = ",";

  protected AbstractDataAccess()
  {
  }


  protected AbstractDataAccess(final Element element)
  {
    name = "";
    columnDefinitionIndexMap = new HashMap<Integer, ColumnDefinition>();
    columnDefinitions = new ArrayList<ColumnDefinition>();
    parameters = new ArrayList<Parameter>();
    outputs = new HashMap<Integer, ArrayList<Integer>>();
    outputs.put(1, new ArrayList<Integer>());
    outputMode = new HashMap<Integer, OutputMode>();
    outputMode.put(1, OutputMode.INCLUDE);

    parseOptions(element);

  }


  /**
   * @param id
   * @param name
   */
  protected AbstractDataAccess(String id, String name)
  {
    this.name = name;
    this.id = id;

    columnDefinitionIndexMap = new HashMap<Integer, ColumnDefinition>();
    columnDefinitions = new ArrayList<ColumnDefinition>();
    parameters = new ArrayList<Parameter>();
    outputs = new HashMap<Integer, ArrayList<Integer>>();
    outputs.put(1, new ArrayList<Integer>());
    outputMode = new HashMap<Integer, OutputMode>();
    outputMode.put(1, OutputMode.INCLUDE);
  }


  /**
   * 
   * @param params
   */
  public void setParameters(Collection<Parameter> params)
  {
    this.parameters.clear();
    this.parameters.addAll(params);
  }


  public abstract String getType();


  private void parseOptions(final Element element)
  {
    id = element.attributeValue("id");

    final Element nameElement = (Element) element.selectSingleNode("./Name");
    if (nameElement != null)
    {
      name = nameElement.getTextTrim();
    }

    if (element.attributeValue("access") != null && element.attributeValue("access").equals("private"))
    {
      access = DataAccessEnums.ACCESS_TYPE.PRIVATE;
    }

    if (element.attributeValue("cache") != null && element.attributeValue("cache").equals("true"))
    {
      cacheEnabled = true;
    }

    if (element.attribute("cacheDuration") != null && !element.attribute("cacheDuration").toString().equals(""))
    {
      cacheDuration = Integer.parseInt(element.attributeValue("cacheDuration"));
    }


    // Parse parameters
    @SuppressWarnings("unchecked")
    final List<Element> parameterNodes = element.selectNodes("Parameters/Parameter");

    for (final Element p : parameterNodes)
    {
      parameters.add(new Parameter(p));
    }

    // Parse outputs
    @SuppressWarnings("unchecked")
    final List<Element> outputNodes = element.selectNodes("Output");

    for (final Element outputNode : outputNodes)
    {
      ArrayList<Integer> myOutputs = new ArrayList<Integer>();
      if (outputNode != null)
      {
        int localId = 1;
        if (outputNode.attribute("id") != null && !outputNode.attribute("id").toString().equals(""))
        {
          // if parseInt fails an exception will be thrown and the cda file will not be accepted
          localId = Integer.parseInt(outputNode.attributeValue("id"));
        }
        else
        {
          // if an output has not a defined or empty id then it will have key = 1
          localId = 1;
        }

        try
        {
          outputMode.put(localId, OutputMode.valueOf(outputNode.attributeValue("mode").toUpperCase()));
        }
        catch (Exception e)
        {
          // if there are any errors, go back to the default					
          outputMode.put(localId, OutputMode.INCLUDE);
        }

        final String[] indexes = outputNode.attributeValue("indexes").split(",");
        for (final String index : indexes)
        {
          myOutputs.add(Integer.parseInt(index));
        }
        outputs.put(localId, myOutputs);
      }
    }

    // Parse Columns
    @SuppressWarnings("unchecked")
    final List<Element> columnNodes = element.selectNodes("Columns/*");

    for (final Element p : columnNodes)
    {
      columnDefinitions.add(new ColumnDefinition(p));
    }

    // Build the columnDefinitionIndexMap
    final ArrayList<ColumnDefinition> cols = getColumns();
    for (final ColumnDefinition columnDefinition : cols)
    {
      columnDefinitionIndexMap.put(columnDefinition.getIndex(), columnDefinition);
    }

  }
  
  public static synchronized IQueryCache getCdaCache(){
    if(cache == null){
      try {
        cache = PluginUtils.getPluginBean("cda.", IQueryCache.class);
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
      if(cache == null){
        //fallback
        cache = new EHCacheQueryCache(); 
      }
    }
    return cache;
  }

  @Deprecated
  public static synchronized net.sf.ehcache.Cache getCache()
  {
    return ((EHCacheQueryCache) getCdaCache()).getCache();
  }

  public static synchronized void shutdownCache(){
    if(cache != null){
      cache.shutdownIfRunning();
      cache = null;
    }
  }

  public static synchronized void clearCache()
  {
    IQueryCache cache = getCdaCache();
    cache.clearCache();
  }


  public TableModel doQuery(final QueryOptions queryOptions) throws QueryException
  {

    Map<String, Iterable<String>> iterableParameters = getIterableParametersValues(queryOptions);

    if (!iterableParameters.isEmpty())
    {
      return doQueryOnIterableParameters(queryOptions, iterableParameters);
    }

    /*
     *  Do the tableModel PostProcessing
     *  1. Sort
     *  2. Show only the output columns
     *  3. Paginate
     *  4. Call the appropriate exporter
     *
     */

    final TableModel tableModel = queryDataSource(queryOptions);
    
    try
    {
      final TableModel outputTableModel = TableModelUtils.postProcessTableModel(this, queryOptions, tableModel);
      logger.debug("Query " + getId() + " done successfully - returning tableModel");
      return outputTableModel;
    }
    catch (InvalidOutputIndexException e)
    {
      throw new QueryException("Error while setting output index id ", e);
    }
    catch (SortException e)
    {
      throw new QueryException("Error while sorting output ", e);
    }
  }

  public TableModel listParameters(final DiscoveryOptions discoveryOptions)
  {

    return TableModelUtils.dataAccessParametersToTableModel(getParameters());

  }


  protected abstract TableModel queryDataSource(final QueryOptions queryOptions) throws QueryException;


  //public abstract void closeDataSource() throws QueryException;


  public ArrayList<ColumnDefinition> getColumns()
  {

    final ArrayList<ColumnDefinition> list = new ArrayList<ColumnDefinition>();

    for (final ColumnDefinition definition : columnDefinitions)
    {
      if (definition.getType() == ColumnDefinition.TYPE.COLUMN)
      {
        list.add(definition);
      }
    }
    return list;

  }


  public ColumnDefinition getColumnDefinition(final int idx)
  {

    return columnDefinitionIndexMap.get(new Integer(idx));

  }


  public ArrayList<ColumnDefinition> getCalculatedColumns()
  {

    final ArrayList<ColumnDefinition> list = new ArrayList<ColumnDefinition>();

    for (final ColumnDefinition definition : columnDefinitions)
    {
      if (definition.getType() == ColumnDefinition.TYPE.CALCULATED_COLUMN)
      {
        list.add(definition);
      }
    }
    return list;

  }


  public void storeDescriptor(DataAccessConnectionDescriptor descriptor)
  {
    ////
  }


  public String getId()
  {
    return id;
  }


  public String getName()
  {
    return name;
  }


  public void setName(final String name)
  {
    this.name = name;
  }


  public DataAccessEnums.ACCESS_TYPE getAccess()
  {
    return access;
  }

  /**
   * @deprecated use {@link #isCacheEnabled()} instead
   */
  public boolean isCache()
  {
    return cacheEnabled;
  }
  
  public boolean isCacheEnabled(){
    return cacheEnabled;
  }
  
  public void setCacheEnabled(boolean enabled){
    cacheEnabled = enabled;
  }


  public int getCacheDuration()
  {
    return cacheDuration;
  }
  
  public void setCacheDuration(int cacheDuration) {
    this.cacheDuration = cacheDuration;
  }


  public CdaSettings getCdaSettings()
  {
    return cdaSettings;
  }


  public void setCdaSettings(final CdaSettings cdaSettings)
  {
    this.cdaSettings = cdaSettings;
  }


  public ArrayList<Parameter> getParameters()
  {
    return parameters;
  }


  public ArrayList<Integer> getOutputs()
  {
    if (outputs.isEmpty())
    {
      return new ArrayList<Integer>();
    }
    return (ArrayList<Integer>) outputs.get(1);
  }


  public ArrayList<Integer> getOutputs(int id)
  {
    return (ArrayList<Integer>) outputs.get(id);
  }


  /**
   * 
   * @param outputIndexes indexes of columns to output
   */
  public void setOutputs(HashMap<Integer, ArrayList<Integer>> outputIndexes)
  {
    this.outputs = outputIndexes;
  }


  public OutputMode getOutputMode()
  {
    return outputMode.get(1);
  }


  public OutputMode getOutputMode(int id)
  {
    return outputMode.get(id);
  }


  public ArrayList<DataAccessConnectionDescriptor> getDataAccessConnectionDescriptors()
  {
    return this.getDataAccessConnectionDescriptors();
  }


  public ArrayList<PropertyDescriptor> getInterface()
  {

    ArrayList<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
    properties.add(new PropertyDescriptor("id", PropertyDescriptor.Type.STRING, PropertyDescriptor.Placement.ATTRIB));
    properties.add(new PropertyDescriptor("access", PropertyDescriptor.Type.STRING, PropertyDescriptor.Placement.ATTRIB));
    properties.add(new PropertyDescriptor("parameters", PropertyDescriptor.Type.ARRAY, PropertyDescriptor.Placement.CHILD));
    properties.add(new PropertyDescriptor("output", PropertyDescriptor.Type.ARRAY, PropertyDescriptor.Placement.CHILD));
    properties.add(new PropertyDescriptor("columns", PropertyDescriptor.Type.ARRAY, PropertyDescriptor.Placement.CHILD));
    return properties;
  }


  public abstract ConnectionType getConnectionType();


  public Connection[] getAvailableConnections()
  {
    return ConnectionCatalog.getInstance(false).getConnectionsByType(getConnectionType());
  }


  public Connection[] getAvailableConnections(boolean skipCache)
  {
    return ConnectionCatalog.getInstance(skipCache).getConnectionsByType(getConnectionType());
  }


  public String getTypeForFile()
  {
    return this.getClass().toString().toLowerCase().replaceAll("class pt.webdetails.cda.dataaccess.(.*)connection", "$1");
  }


  /**
   * Identify $FOREACH directives and get their iterators
   */
  private Map<String, Iterable<String>> getIterableParametersValues(
          final QueryOptions queryOptions) throws QueryException
  {

    //name, values
    Map<String, Iterable<String>> iterableParameters = new HashMap<String, Iterable<String>>();
    final String splitRegex = PARAM_ITERATOR_ARG_SEPARATOR + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";//ignore separator inside dquotes

    for (Parameter param : queryOptions.getParameters())
    {
      String value = param.getStringValue();
      if (value != null && value.startsWith(PARAM_ITERATOR_BEGIN))
      {
        String[] args = Util.getContentsBetween(value, PARAM_ITERATOR_BEGIN, PARAM_ITERATOR_END).split(splitRegex);

        if (args.length < 2)
        {
          throw new QueryException("Parameter '" + param.getName() + "': "
                  + "Error iterating parameter.", new IllegalArgumentException("$FOREACH: need at least dataAccessId and column index"));
        }

        //dataAccessId
        String dataAccessId = StringUtils.trim(args[0]);
        try
        {//validate
          getCdaSettings().getDataAccess(dataAccessId);
        }
        catch (UnknownDataAccessException e)
        {
          throw new QueryException("$FOREACH: Invalid dataAccessId.", e);
        }

        //column index
        int columnIdx = 0;
        try
        {
          columnIdx = Integer.parseInt(StringUtils.trim(args[1]));
        }
        catch (NumberFormatException nfe)
        {
          throw new QueryException("$FOREACH: Unable to parse 2nd argument.", nfe);
        }

        //parameters for query
        String[] dataAccessParams = null;
        if (args.length > 2)
        {
          dataAccessParams = new String[args.length - 2];
          System.arraycopy(args, 2, dataAccessParams, 0, dataAccessParams.length);
        }

        Iterable<String> paramValues = expandParameterIteration(dataAccessId, columnIdx, dataAccessParams);

        if (paramValues == null)
        {//no values, clear so it can fallback to default (if any)
          param.setValue(null);
        }
        else
        {
          iterableParameters.put(param.getName(), paramValues);
        }
      }
    }
    return iterableParameters;
  }

  /**
   * Get a value iterator from a $FOREACH directive 
   * @return Iterable over values, or null if no results
   */
  private Iterable<String> expandParameterIteration(String dataAccessId, int outColumnIdx, String[] dataAccessParameters)
          throws QueryException
  {
    final String EXC_TEXT = "Unable to expand parameter iteration. ";

    QueryOptions queryOptions = new QueryOptions();
    queryOptions.setDataAccessId(dataAccessId);

    //set query parameters
    if (dataAccessParameters != null)
    {
      for (String paramDef : dataAccessParameters)
      {
        int attribIdx = StringUtils.indexOf(paramDef, '=');
        if(attribIdx > 0){
          String paramName = StringUtils.trim(StringUtils.substring(paramDef, 0, attribIdx));
          String paramValue = StringUtils.trim(StringUtils.substring(paramDef, attribIdx+1));
          queryOptions.addParameter(paramName, paramValue);
        }
        else {
          logger.error("Bad parameter definition, skipping: " + paramDef);
        }
      }
    }

    //do query and get selected columns
    logger.debug("expandParameterIteration: Doing inner query on CdaSettings [ " + cdaSettings.getId()
            + " (" + queryOptions.getDataAccessId() + ")]");
    try
    {

      DataAccess dataAccess = getCdaSettings().getDataAccess(queryOptions.getDataAccessId());
      TableModel tableModel = dataAccess.doQuery(queryOptions);

      if (outColumnIdx < 0 || outColumnIdx >= tableModel.getColumnCount())
      {
        throw new QueryException(EXC_TEXT, new IllegalArgumentException("Output column index " + outColumnIdx + " out of range."));
      }

      if (tableModel.getRowCount() < 1)
      {
        return null;
      }
      return new StringColumnIterable(tableModel, outColumnIdx);

    }
    catch (UnknownDataAccessException e)
    {
      throw new QueryException(EXC_TEXT, e);
    }

  }


  private TableModel doQueryOnIterableParameters(QueryOptions queryOptions, Map<String, Iterable<String>> iterableParameters)
          throws QueryException
  {
    //all iterators need to have at least one value..
    List<String> names = new ArrayList<String>();
    List<Iterator<String>> iterators = new ArrayList<Iterator<String>>();
    List<Iterable<String>> iterables = new ArrayList<Iterable<String>>();
    List<String> values = new ArrayList<String>();
    TableModel result = null;

    try
    {
      //0) init
      int paramCount = 0;
      for (String name : iterableParameters.keySet())
      {
        names.add(name);
        iterables.add(iterableParameters.get(name));
        iterators.add(iterables.get(paramCount).iterator());
        values.add(iterators.get(paramCount).next());
        paramCount++;
      }
      boolean exhausted = false;

      while (!exhausted)
      {// til last iteration from bottom of stack
        //1.1) set parameters
        for (int i = 0; i < paramCount; i++)
        {
          queryOptions.setParameter(names.get(i), values.get(i));
        }
        //1.2) execute query
        TableModel tableModel = doQuery(queryOptions);
//				//1.3) cache only, just keep last result //join results x
//				if (result == null) { result = tableModel; }
//				else { result = TableModelUtils.getInstance().appendTableModel(result,tableModel); }
        result = tableModel;
        //2) get next set of values
        for (int i = 0; i < paramCount; i++)
        {// traverse until we can get a next() or bottom of stack reached
          if (iterators.get(i).hasNext())
          {
            values.set(i, iterators.get(i).next());//new value
            break;
          }
          else if (i < paramCount - 1)
          {//this one exhausted, reset if not last
            iterators.set(i, iterables.get(i).iterator());//reset
            values.set(i, iterators.get(i).next());
          }
          else
          {
            exhausted = true;//end of the line, no more resets
          }
        }
      }
    }
    catch (NoSuchElementException e)
    {
      //will happen if one of the iterators has no value
    }

    return result;
  }

  /**
   * Iterates a table model over a given column index.
   */
  private class StringColumnIterable implements Iterable<String>
  {

    private final TableModel table;
    private final int columnIndex;


    public StringColumnIterable(TableModel tableModel, int columnIndex)
    {
      this.table = tableModel;
      this.columnIndex = columnIndex;
    }


    public Iterator<String> iterator()
    {
      return new StringColumnIterator();
    }

    private class StringColumnIterator implements Iterator<String>
    {

      int rowIndex = 0;


      public boolean hasNext()
      {
        return table.getRowCount() > rowIndex;
      }


      public String next()
      {
        if (!hasNext())
        {
          throw new NoSuchElementException();
        }
        return table.getValueAt(rowIndex++, columnIndex).toString();
      }


      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    }
  }

  public ArrayList<ColumnDefinition> getColumnDefinitions() {
	  return columnDefinitions;
  }
  
}
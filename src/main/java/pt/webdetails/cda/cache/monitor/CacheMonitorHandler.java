/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cda.cache.monitor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import pt.webdetails.cda.cache.IQueryCache;
import pt.webdetails.cda.cache.TableCacheKey;
import pt.webdetails.cda.dataaccess.AbstractDataAccess;
import pt.webdetails.cda.exporter.ExporterException;
import pt.webdetails.cda.utils.framework.JsonCallHandler;

public class CacheMonitorHandler extends JsonCallHandler 
{
  
  private static Log logger = LogFactory.getLog(JsonCallHandler.class);
  
  private static CacheMonitorHandler _instance;

  public static synchronized CacheMonitorHandler getInstance()
  {
    if (_instance == null)
    {
      _instance = new CacheMonitorHandler();
    }
    return _instance;
  }


  public CacheMonitorHandler()
  {
    registerMethods();
  }
  
  @Override
  protected boolean hasPermission(IPentahoSession session, Method method) 
  {//limit all interaction besides overview to admin role
    return method.getName().equals("cacheOverview") || SecurityHelper.isPentahoAdministrator(session);
  }
  
  private void registerMethods()
  {
    
    registerMethod("cached", new JsonCallHandler.Method() 
    {
      /**
       * get all cached items for given cda file and data access id
       */
      public JSONObject execute(IParameterProvider params) throws JSONException, ExporterException, IOException {
        String cdaSettingsId = params.getStringParameter("cdaSettingsId", null);
        String dataAccessId = params.getStringParameter("dataAccessId", null);
        return listQueriesInCache(cdaSettingsId, dataAccessId);
      }
    });
    
    registerMethod("cacheOverview", new JsonCallHandler.Method() 
    {
      /**
       * get details on a particular cached item
       */
      public JSONObject execute(IParameterProvider params) throws JSONException 
      {
        String cdaSettingsId = params.getStringParameter("cdaSettingsId", null);
        return getCachedQueriesOverview(cdaSettingsId);
      }
    });
    
    registerMethod("getDetails", new JsonCallHandler.Method() 
    {
      /**
       * get details on a particular cached item
       */
      public JSONObject execute(IParameterProvider params) throws UnsupportedEncodingException, JSONException, ExporterException, IOException, ClassNotFoundException  
      {
        try{
          String encodedCacheKey=params.getStringParameter("key", null);
          return getcacheQueryTable(encodedCacheKey);
        }
        catch(ExporterException e){
          logger.error( "Error exporting table.", e);
          return createJsonResultFromException(e);
        }
      }
    });
    
    registerMethod("removeCache", new JsonCallHandler.Method() 
    {
      /**
       * Remove item from cache 
       */
      public JSONObject execute(IParameterProvider params) throws JSONException, UnsupportedEncodingException, IOException, ClassNotFoundException 
      {
        String serializedCacheKey = params.getStringParameter("key", null);
        return removeQueryFromCache(serializedCacheKey);
      }
    });
    
    registerMethod("removeAll", new JsonCallHandler.Method() {
      
      @Override
      public JSONObject execute(IParameterProvider params) throws JSONException {
        String cdaSettingsId = params.getStringParameter("cdaSettingsId", null);
        String dataAccessId = params.getStringParameter("dataAccessId", null);
        return removeAll(cdaSettingsId, dataAccessId);
      }
    });
    
    registerMethod("shutdown", new JsonCallHandler.Method() {
      
      @Override
      public JSONObject execute(IParameterProvider params) throws Exception {
        AbstractDataAccess.shutdownCache();
        return getOKJson("Cache shutdown.");
      }
    });
    
  }
  
  
  private static class ResultFields extends JsonCallHandler.JsonResultFields {
    public static final String CDA_SETTINGS_ID = "cdaSettingsId";
    public static final String DATA_ACCESS_ID = "dataAccessId";
    public static final String COUNT = "count";
    public static final String ITEMS = "items";
  }
  
  
  private static class ErrorMsgs {
    public static final String CACHE_ITEM_NOT_FOUND = "Cache element no longer in cache.";
  }
  
  private static JSONObject getcacheQueryTable(String encodedCacheKey) throws JSONException, ExporterException, UnsupportedEncodingException, IOException, ClassNotFoundException {
    
    if(encodedCacheKey == null){
      throw new IllegalArgumentException("No cache key received.");
    }
    
    JSONObject result = new JSONObject();
    IQueryCache cdaCache = AbstractDataAccess.getCdaCache();

    TableCacheKey lookupCacheKey = TableCacheKey.getTableCacheKeyFromString(encodedCacheKey);
    ExtraCacheInfo info = cdaCache.getCacheEntryInfo(lookupCacheKey);

    if(info != null){
      // put query results
      result.put(ResultFields.RESULT, info.getTableSnapshot());
      result.put(JsonResultFields.STATUS, ResponseStatus.OK);
    }
    else {
      return getErrorJson(ErrorMsgs.CACHE_ITEM_NOT_FOUND);
    }
    
    return result;
    
  }
  
  private static JSONObject getCachedQueriesOverview(String cdaSettingsIdFilter) throws JSONException {
    
    HashMap<String, HashMap<String, Integer>> cdaMap = new HashMap<String, HashMap<String,Integer>>();
    
    IQueryCache cdaCache = AbstractDataAccess.getCdaCache();
    JSONArray results = new JSONArray();
    
    for(TableCacheKey key : cdaCache.getKeys()) {
      
      ExtraCacheInfo info = cdaCache.getCacheEntryInfo(key);
      
      if(info == null) continue;
      
      String cdaSettingsId = info.getCdaSettingsId();
      String dataAccessId = info.getDataAccessId();
      
      if(cdaSettingsIdFilter != null && !cdaSettingsIdFilter.equals(cdaSettingsId)){
        continue;
      }
      
      //aggregate occurrences
      HashMap<String, Integer> dataAccessIdMap = cdaMap.get(cdaSettingsId);
      if( dataAccessIdMap == null ){
        dataAccessIdMap = new HashMap<String, Integer>();
        dataAccessIdMap.put(dataAccessId, 1);
        cdaMap.put(cdaSettingsId, dataAccessIdMap);
      }
      else {
        Integer count = dataAccessIdMap.get(dataAccessId);
        if(count == null){
          dataAccessIdMap.put(dataAccessId, 1);
        }
        else {
          dataAccessIdMap.put(dataAccessId, ++count);
        }
      }
    }
    
    for(String cdaSettingsId :  cdaMap.keySet()){
      getOverviewForCdaSettingsId(cdaMap, results, cdaSettingsId);
    }
    
    JSONObject result = new JSONObject();
    result.put(JsonResultFields.STATUS, ResponseStatus.OK);
    result.put(ResultFields.RESULT, results);
    return result;
  }


  /**
   * @param cdaMap
   * @param results
   * @param cdaSettingsId
   * @throws JSONException
   */
  private static void getOverviewForCdaSettingsId(HashMap<String, HashMap<String, Integer>> cdaMap, JSONArray results, String cdaSettingsId) throws JSONException {
    for(String dataAccessId : cdaMap.get(cdaSettingsId).keySet() ){
      Integer count = cdaMap.get(cdaSettingsId).get(dataAccessId);
      JSONObject queryInfo = new JSONObject();
      queryInfo.put(ResultFields.CDA_SETTINGS_ID, cdaSettingsId);
      queryInfo.put(ResultFields.DATA_ACCESS_ID, dataAccessId); 
      queryInfo.put(ResultFields.COUNT, count.intValue());
      
      results.put(queryInfo);
    }
  }
  
  private static JSONObject removeQueryFromCache(String serializedCacheKey) throws UnsupportedEncodingException, IOException, ClassNotFoundException, JSONException 
  {
    TableCacheKey key = TableCacheKey.getTableCacheKeyFromString(serializedCacheKey);
    
    IQueryCache cdaCache = AbstractDataAccess.getCdaCache();
    boolean success = cdaCache.remove(key);
    
    JSONObject result = new JSONObject();
    if(success){
      result.put(JsonResultFields.STATUS, ResponseStatus.OK);
      result.put(JsonResultFields.RESULT, true);
      return result;
    }
    else {
      return getErrorJson(ErrorMsgs.CACHE_ITEM_NOT_FOUND);
    }
  }

  
  private static JSONObject listQueriesInCache(String cdaSettingsId, String dataAccessId) throws JSONException, ExporterException, IOException {
    
    JSONArray results = new JSONArray();
    
    IQueryCache cdaCache = AbstractDataAccess.getCdaCache();
    
    for(TableCacheKey key : cdaCache.getKeys()) {
      
      ExtraCacheInfo info = cdaCache.getCacheEntryInfo(key);
      if(info == null) continue;
      
      if(!StringUtils.equals(cdaSettingsId, info.getCdaSettingsId()) ||
         (dataAccessId != null && !StringUtils.equals(dataAccessId, info.getDataAccessId())))
      {//not what we're looking for
        continue;
      }
      
      CacheElementInfo cacheInfo = cdaCache.getElementInfo(key);
      if(cacheInfo != null){
        results.put(cacheInfo.toJson());
      }
    }
    
    JSONObject result = new JSONObject();
    result.put(ResultFields.CDA_SETTINGS_ID, cdaSettingsId);
    result.put(ResultFields.DATA_ACCESS_ID, dataAccessId);
    result.put(ResultFields.ITEMS, results);
//    //total stats
//    result.put("cacheLength", cdaCache.getSize());
//    result.put("memoryStoreLength", cdaCache.getMemoryStoreSize());
//    result.put("diskStoreLength", cdaCache.getDiskStoreSize());
    
    JSONObject response = new JSONObject();
    response.put(ResultFields.STATUS, ResponseStatus.OK);
    response.put(ResultFields.RESULT, result);
    
    return response;
  }
  
  private static JSONObject removeAll(String cdaSettingsId, String dataAccessId) throws JSONException
  {
    IQueryCache cdaCache = AbstractDataAccess.getCdaCache();
    int result = cdaCache.removeAll(cdaSettingsId, dataAccessId);
    
    return getOKJson(result);
  }

  
}

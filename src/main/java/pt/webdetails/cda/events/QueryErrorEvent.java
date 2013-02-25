/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cda.events;

import org.json.JSONException;
import org.json.JSONObject;

public class QueryErrorEvent extends CdaEvent {
  
  private Throwable e;

  public QueryErrorEvent(QueryInfo queryInfo, Throwable e) throws JSONException {
    super(CdaEventType.QueryError, queryInfo);
    this.e = e;
  }
  
//  @Override
//  public JSONObject toJSON() throws JSONException {
//    JSONObject obj = super.toJSON();
//    obj.put("exceptionType", e.getClass().getName());
//    obj.put("message", e.getMessage());
//    obj.put("stackTrace", toStringArray(e.getStackTrace()));
//    return obj;
//  }
//  
//  private static String[] toStringArray(final StackTraceElement[] stackTrace){
//    if(stackTrace == null) return null;
//    String[] result = new String[stackTrace.length];
//    for(int i = 0; i< stackTrace.length; i++ ){
//      result[i] = stackTrace[i].toString();
//    }
//    return result;
//  }

}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cda.dataaccess;

import java.util.ArrayList;
import javax.swing.table.TableModel;

import org.dom4j.Element;
import pt.webdetails.cda.connections.ConnectionCatalog.ConnectionType;
import pt.webdetails.cda.query.QueryOptions;
import pt.webdetails.cda.settings.UnknownDataAccessException;
import pt.webdetails.cda.utils.TableModelUtils;

/**
 * Class to join 2 datatables
 * Created by Pedro Alves
 * User: pedro
 * Date: Mar 9, 2010
 * Time: 1:13:11 PM
 */
public class UnionCompoundDataAccess extends CompoundDataAccess
{

  //private static final Log logger = LogFactory.getLog(UnionCompoundDataAccess.class);
  private static final String TYPE = "union";
  private String topId;
  private String bottomId;


  public UnionCompoundDataAccess()
  {
  }


  public UnionCompoundDataAccess(final Element element)
  {
    super(element);

    Element top = (Element) element.selectSingleNode("Top");
    Element bottom = (Element) element.selectSingleNode("Bottom");

    topId = top.attributeValue("id");
    bottomId = bottom.attributeValue("id");

  }


  public String getType()
  {
    return TYPE;
  }


  protected TableModel queryDataSource(final QueryOptions queryOptions) throws QueryException
  {


    try
    {
      QueryOptions croppedOptions = (QueryOptions) queryOptions.clone();
      croppedOptions.setSortBy(new ArrayList<String>());
      croppedOptions.setPageSize(0);
      croppedOptions.setPageStart(0);
      final TableModel tableModelA = this.getCdaSettings().getDataAccess(topId).doQuery(queryOptions);
      final TableModel tableModelB = this.getCdaSettings().getDataAccess(bottomId).doQuery(queryOptions);

      return TableModelUtils.appendTableModel(tableModelA, tableModelB);

    }
    catch (CloneNotSupportedException e)
    {
     throw new QueryException("Couldn't clone settings ", e);
    }
    catch (UnknownDataAccessException e)
    {
      throw new QueryException("Unknown Data access in CompoundDataAccess ", e);
    }


  }


  @Override
  public ConnectionType getConnectionType()
  {
    return ConnectionType.NONE;
  }


  @Override
  public ArrayList<PropertyDescriptor> getInterface()
  {
    ArrayList<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
    properties.add(new PropertyDescriptor("id", PropertyDescriptor.Type.STRING, PropertyDescriptor.Placement.ATTRIB));
    properties.add(new PropertyDescriptor("top", PropertyDescriptor.Type.STRING, PropertyDescriptor.Placement.CHILD));
    properties.add(new PropertyDescriptor("bottom", PropertyDescriptor.Type.STRING, PropertyDescriptor.Placement.CHILD));
    properties.add(new PropertyDescriptor("parameters", PropertyDescriptor.Type.STRING, PropertyDescriptor.Placement.CHILD));
    return properties;
  }

  public String getTopId() {
	  return topId;
  }


  public String getBottomId() {
	  return bottomId;
  }

  @Override
  public void setQuery(String query) {
	  // Do nothing
  }
}

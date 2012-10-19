package pt.webdetails.cda.deprecated;


/**
 * Utility class for SolutionRepository utils
 * User: pedro
 * Date: Feb 16, 2010
 * Time: 6:13:33 PM
 */
@Deprecated
public class SolutionRepositoryUtils
{
//
//
//  private static final Log logger = LogFactory.getLog(SolutionRepositoryUtils.class);
//  private static final String EXTENSION = ".cda";
//
//  private static SolutionRepositoryUtils _instance;
//
//  public SolutionRepositoryUtils()
//  {
//  }
//
//
//  public static synchronized SolutionRepositoryUtils getInstance()
//  {
//
//    if (_instance == null)
//    {
//      _instance = new SolutionRepositoryUtils();
//    }
//
//    return _instance;
//  }
//
//  public TableModel getCdaList(final IPentahoSession userSession)
//  {
//
//    logger.debug("Getting CDA list");
//
//    Document cdaTree = RepositoryAccess.getRepository(userSession).getFullSolutionTree(FileAccess.READ, new CdaFilter());
//    @SuppressWarnings("unchecked")
//    List<Element> cdaFiles = cdaTree.selectNodes("//leaf[@isDir=\"false\"]");
//
//
//    final int rowCount = cdaFiles.size();
//
//    // Define names and types
//    final String[] colNames = {"name", "path"};
//    final Class<?>[] colTypes = {String.class, String.class};
//    final TypedTableModel typedTableModel = new TypedTableModel(colNames, colTypes, rowCount);
//
//    for (Object o : cdaFiles)
//    {
//      Element e = (Element) o;
//      typedTableModel.addRow(new Object[]{e.selectSingleNode("leafText").getText(), e.selectSingleNode("path").getText()});
//
//    }
//
//    return typedTableModel;
//
//  }
//
//
//  private class CdaFilter implements ISolutionFilter
//  {
//
//    public boolean keepFile(final ISolutionFile iSolutionFile, final int i)
//    {
//      if (iSolutionFile.isDirectory())
//      {
//        return true;
//      }
//      else
//      {
//        return iSolutionFile.getExtension().equals(EXTENSION);
//      }
//    }
//  }
}

package pt.webdetails.cda.settings;

/**
 * Created by IntelliJ IDEA.
 * User: pedro
 * Date: Feb 2, 2010
 * Time: 3:17:13 PM
 */
public class UnknownDataAccessException extends Exception {
  public UnknownDataAccessException(final String s, final Exception cause) {
    super(s,cause);
  }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cda.exporter;

import java.io.OutputStream;
import javax.swing.table.TableModel;

/**
 * Created by IntelliJ IDEA.
 * User: pedro
 * Date: Feb 5, 2010
 * Time: 5:02:17 PM
 */
public interface Exporter
{

  public String getMimeType();
  public String getAttachmentName();
  public void export(OutputStream out, final TableModel tableModel) throws ExporterException;
}

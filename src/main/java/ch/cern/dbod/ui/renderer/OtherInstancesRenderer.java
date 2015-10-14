/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.renderer;

import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.util.CommonConstants;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Html;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 * Renderer for the list of instances in the instance view
 * @author Daniel Gomez Blanco
 */
public class OtherInstancesRenderer implements RowRenderer{

    /**
     * Renders a row
     * @param row object where to place information
     * @param object object to be rendered
     * @param i index of the row
     * @throws Exception in case components cannot be added to the row
     */
    @Override
    public void render(Row row, Object object, int i) throws Exception {
        Instance instance = (Instance) object;
        row.setStyle("padding-top: 0px; padding-bottom: 0px");
        row.setHeight("24px");
        // the data append to each row with simple label
        Html dbName = new Html();
        dbName.setContent("<a style=\"text-decoration:none;color:blue\" class=\"z-label\" href=\""
                            + Executions.encodeURL(CommonConstants.PAGE_INSTANCE + "?" + CommonConstants.INSTANCE + "=" + instance.getDbName()) 
                            +"\">" + instance.getDbName() + "</a>");
        row.appendChild(dbName);
    }
}

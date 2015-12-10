/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.renderer;

import ch.cern.dbod.db.dao.UpgradeDAO;
import ch.cern.dbod.db.entity.Upgrade;
import ch.cern.dbod.ui.controller.UpgradesController;
import ch.cern.dbod.util.CommonConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Renderer for grid of upgrades
 * @author Daniel Gomez Blanco
 */
public class UpgradesGridRenderer implements RowRenderer {
    /**
     * Upgrade DAO
     */
    UpgradeDAO upgradeDAO;

    /**
     * Constructor with upgradeDAO
     * @param upgradeDAO DAO for the upgrades
     */
    public UpgradesGridRenderer(UpgradeDAO upgradeDAO) {
        this.upgradeDAO = upgradeDAO;
    }
    
    /**
     * Renders a row
     * @param row object where to place information
     * @param data object to be rendered
     * @param i index of the row
     * @throws Exception in case components cannot be added to the row
     */
    @Override
    public void render(final Row row, Object data, int i) {
        //Cast upgrade object
        final Upgrade upgrade = (Upgrade) data;
        row.setStyle("padding-top: 0px; padding-bottom: 0px");
        
        //Info
        row.appendChild(new Label(Labels.getLabel(CommonConstants.LABEL_DB_TYPE + upgrade.getDbType())));
        row.appendChild(new Label(Labels.getLabel(CommonConstants.LABEL_CATEGORY + upgrade.getCategory())));
        row.appendChild(new Label(upgrade.getVersionFrom()));
        row.appendChild(new Label(upgrade.getVersionTo()));
        
        //Delete button
        Vbox box = new Vbox();
        box.setWidth("100%");
        box.setHeight("20px");
        box.setAlign("center");
        final Toolbarbutton deleteBtn = new Toolbarbutton();
        deleteBtn.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_DELETE_UPGRADE));
        deleteBtn.setImage(CommonConstants.IMG_DESTROY);
        deleteBtn.setZclass(CommonConstants.STYLE_BUTTON);
        deleteBtn.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                try {
                    if (upgradeDAO.delete(upgrade)) {
                        ((UpgradesController)row.getGrid().getRoot().getFellow("upgradesController")).refreshInstances();
                    }
                    else {
                        showError(row, null, CommonConstants.ERROR_DELETING_UPGRADE);
                    }
                }
                catch (Exception ex) {
                    showError(row, ex, CommonConstants.ERROR_DELETING_UPGRADE);
                }
            }
        });
        box.appendChild(deleteBtn);
        row.appendChild(box);
    }
    
    /**
     * Displays an error window for the error code provided.
     * @param errorCode error code for the message to be displayed.
     */
    private void showError(Row row, Exception exception, String errorCode) {
        if (exception != null) {
            Logger.getLogger(UpgradesGridRenderer.class.getName()).log(Level.SEVERE, "ERROR DELETING UPGRADE", exception);
        }
        Window errorWindow = (Window) row.getGrid().getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(UpgradesGridRenderer.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}


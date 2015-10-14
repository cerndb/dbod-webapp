/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.db.entity;

/**
 * Represents a metric in the system. It is used to render the different metrics that can be obtained from an instance.
 * @author Daniel Gomez Blanco
 */
public class Metric {
    /**
     * Type of metric
     */
    private String type;
    /**
     * Id of the metric
     */
    private String code;
    /**
     * Description of the metric
     */
    private String name;
    /**
     * Unit (if any)
     */
    private String unit;

    public Metric() {
    }

    public Metric(String code, String name, String type, String unit) {
        this.type = type;
        this.code = code;
        this.name = name;
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}

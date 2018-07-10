/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.db.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a parameter job that has to be carried out on a database. In the
 * parameters we include things like the timestamp for a recovery, the config
 * file to upload, etc.
 * @author Daniel Gomez Blanco
 */

public class CommandParam{

    /**
     * Id
     */
    private Integer id;
    
    /**
     * Id of the job
     */
    @SerializedName("job_id")
    private Integer jobId;

    /**
     * Type of command (max. 64)
     */
    private String name;

    /**
     * Value of this param (max. 128)
     */
    private String value;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getJobId()
    {
        return jobId;
    }

    public void setJobId(Integer jobId)
    {
        this.jobId = jobId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

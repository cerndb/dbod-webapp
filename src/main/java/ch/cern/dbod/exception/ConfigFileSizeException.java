/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.exception;

/**
 * Exception thrown when config file is bigger than allowed.
 * @author Daniel Gomez Blanco
 */

public class ConfigFileSizeException extends Exception{

    /**
     * Empty constructor.
     */
    public ConfigFileSizeException() {}

    /**
     * Constructor with a message.
     * @param message message for the exception.
     */
    public ConfigFileSizeException(String message) {
        super(message);
    }
    
    /**
     * Constructor with a throwable cause.
     * @param cause cause for the exception.
     */
    public ConfigFileSizeException(Throwable cause) {
        super(cause);
    }
}

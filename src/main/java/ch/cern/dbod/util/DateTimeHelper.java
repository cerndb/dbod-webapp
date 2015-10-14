/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.util;

/**
 * Provides methods to manage dates and times
 * @author Daniel Gomez Blanco
 */
public class DateTimeHelper {
    
    /**
     * Converts a number of seconds to hours, minutes and seconds
     * @param seconds number of seconds to convert
     * @return string with the following format xx h xx m xxs
     */
    public static String timeToString (int seconds) {
        String toret;
        //If less than a minute
        if (seconds < 60)
            toret = seconds + " s";
        //If less than an hour
        else if (seconds < 3600)
            toret = (seconds / 60) + " m " + (seconds % 60) + " s";
        //If more than an hour
        else
            toret = (seconds / 3600) + " h " + ((seconds % 3600) / 60) + " m " + ((seconds % 3600) % 60)  + " s";
        return toret;
    }
}

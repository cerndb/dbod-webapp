/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;

/**
 *
 * @author Jose Andres Cordero Benitez
 */
public class AnnotationExclusionStrategyDeserialization implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        final Expose expose = f.getAnnotation(Expose.class);
        return expose != null && !expose.deserialize();
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}

/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.util;

import java.util.Locale;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.zkoss.web.Attributes;

/**
 * Intercepts requests and configures Locale according to cookies
 * @author Daniel Gomez Blanco
 */
public class LocaleProvider implements org.zkoss.zk.ui.util.RequestInterceptor
{
    /**
     * Process a request and sets the correct Locale
     * @param sess Active session
     * @param request Active request
     * @param response Response
     */
    @Override
    public void request(org.zkoss.zk.ui.Session sess,
                        Object request, Object response)
    {
        final Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        if (cookies != null)
        {
            for (int j = cookies.length; --j >= 0;)
            {
                if (cookies[j].getName().equals(CommonConstants.LOCALE_COOKIE))
                {
                    //Determine the locale
                    String val = cookies[j].getValue();
                    Locale locale = org.zkoss.util.Locales.getLocale(val);
                    sess.setAttribute(Attributes.PREFERRED_LOCALE, locale);
                    return;
                }
            }
        }
        sess.setAttribute(Attributes.PREFERRED_LOCALE, new Locale("en","GB"));
    }
}

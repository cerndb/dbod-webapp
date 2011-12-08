package ch.cern.dod.listener;

import javax.servlet.ServletContextEvent;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Daniel Gomez Blanco
 */
public class ContextManager implements javax.servlet.ServletContextListener {

    public void contextDestroyed(ServletContextEvent sce) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        LogFactory.release(contextClassLoader);

        // Optionally, clean up the underlying concrete logging library too. This might
        // require direct calls to methods on library-specific methods. 

        // And optionally call java.beans.Introspector.flushCaches if your app does any
        // bean introspection and your container doesn't flush the caches for you on
        // servlet undeploy. Note that this isn't a commons-logging problem; it's something
        // quite unrelated which can also cause memory leaks on undeploy.
    }

    public void contextInitialized(ServletContextEvent sce) {
    }
}

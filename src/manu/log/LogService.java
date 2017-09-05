/** Package declaration*/
package manu.log;

/** Class Import */
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Class used to log in the application.<BR>
 * Log are DEBUG - INFO - WARN - ERROR - FATAL :
 * <ul>
 * <li>DEBUG : log for debugging
 * <li>INFO : log to information
 * <li>WARN : warning the application
 * <li>ERROR : error
 * <li>FATAL : fatal error
 * </ul>
 * <BR>
 */
public class LogService  {

    /** 
     * Used to log. 
     */
    private Log log = null;

    /**
     * BATCH
     */
    private String batch = null;

    /**
     * Constructor.
     *
     * @param className
     *            the s name class logging
     */
    public LogService(String className,String batchName) {
        log = LogFactory.getLog(className);
        batch = batchName;
    }

    /**
     * Constructor.
     *
     * @param classLogging
     *            the class logging
     */
    public LogService(Object classLogging) {
        log = LogFactory.getLog(classLogging.getClass().getName());
    }

    /**
     * DEBUG for application.
     *
     * @param message
     *            the message
     */
    public void debug(Object message) {
        if (log.isDebugEnabled())
            log.debug(batch+" "+message);
    }

    /**
     * INFO for application.
     *
     * @param message
     *            the message
     */
    public void info(Object message) {
        if (log.isInfoEnabled())
            log.info(batch+" "+message);
    }

    /**
     * WARNING for application.
     *
     * @param message
     *            the message
     */
    public void warning(Object message) {
        if (log.isWarnEnabled())
            log.warn(batch+" "+message);
    }

    /**
     * ERROR for application.
     *
     * @param message
     *            the i_s message
     */
    public void error(Object message) {
        if (log.isErrorEnabled())
            log.error(batch+" "+message);
    }

    /**
     * FATAL for application.
     *
     * @param message
     *            the message
     */
    public void fatal(Object message) {
        if (log.isFatalEnabled())
            log.fatal(batch+" "+message);
    }

    /**
     * Return a boolean on the debug state.
     *
     * @return true or false depending if the debug mode is activated or not.
     */
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /**
     * printStackTrace
     *
     * @param oThrowable the Exception
     */
    public void printStackTrace(Throwable oThrowable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        oThrowable.printStackTrace(pw);
        String[] stacks = StringUtils.split(sw.toString(), "\r\n");
        for (int i = 0; i < stacks.length; i++) {
            fatal(stacks[i]);
        }
    }
}
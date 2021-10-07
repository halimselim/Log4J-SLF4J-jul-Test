package logtest;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.AbstractAppender;

/**
 *
 * @author Fatih Halimoglu
 */
public class LogTest {

    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        //redirect jul to log4j
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
        org.apache.logging.log4j.core.config.Configurator.setRootLevel(org.apache.logging.log4j.Level.INFO);

        org.apache.logging.log4j.core.Logger log4j_logger = (org.apache.logging.log4j.core.Logger) (org.apache.logging.log4j.LogManager.getRootLogger());
        java.util.logging.Logger jul_logger = java.util.logging.Logger.getLogger(LogTest.class.getName());

        class MockedAppender extends AbstractAppender {

            protected MockedAppender() {
                super("mock appender", null, null, true, null);
//                super("mock appender", null, null, false, null);
            }

            @Override
            public void append(org.apache.logging.log4j.core.LogEvent event) {
                StringBuilder sb = new StringBuilder();
                sb.append(sdf.format(new Date()) + " ");
                sb.append(event.getLevel() + " ");
                sb.append(event.getSource().getClassName() + " [");
                sb.append(event.getSource().getMethodName() + ":");
                sb.append(event.getSource().getLineNumber() + "] ");

                if (event.getThrown() != null) {
                    sb.append(event.getThrown().toString());
                }

                if (!event.getMessage().getFormattedMessage().equals("null")) {
                    sb.append(event.getMessage().getFormattedMessage() + " ");
                }

                System.out.println(sb.toString());

                if (event.getThrown() != null) {
                    event.getThrown().printStackTrace();
                }

            }
        }

        //clear default logger
        for (Appender a : log4j_logger.getAppenders().values()) {
            log4j_logger.removeAppender(a);
        }

        log4j_logger.addAppender(new MockedAppender());

        //j2mod is an example library that uses SLF4J
        com.ghgande.j2mod.modbus.facade.ModbusTCPMaster mm = new com.ghgande.j2mod.modbus.facade.ModbusTCPMaster("192.168.1.2");

        try {
            mm.connect();
        } catch (Exception ex) {

            //jul is the default logging implementation used in my IDE (Netbeans)
            java.util.logging.Logger.getLogger(LogTest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        log4j_logger.info("test finished 1");
        jul_logger.info("test finished 2");

    }

}

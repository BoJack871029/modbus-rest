package modbus.rest.application;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import modbus.rest.debug.Memory;
import modbus.rest.helpers.ConfigurationHelper;
import modbus.rest.models.Configuration;

public class ApplicationListener implements ServletContextListener {

    private static final Logger Logger = LogManager.getLogger(ApplicationListener.class);
    public static String CONFIG_NAME = "configuration";

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
	Logger.debug("**********************************************************************");
	Logger.debug("*                       Stopping Modbus Rest                         *");
	Logger.debug("**********************************************************************");

	Logger.debug("**********************************************************************");
	Logger.debug("*                        Modbus Rest Stopped                         *");
	Logger.debug("**********************************************************************");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
	try {
	    Logger.debug("**********************************************************************");
	    Logger.debug("*                       Starting Modbus Rest                         *");
	    Logger.debug("**********************************************************************");

	    Logger.info("Read config...");

	    Configuration config = ConfigurationHelper.readConfig(getClass());

	    ConfigurationHelper.storeToContext(config, CONFIG_NAME, arg0.getServletContext());

	    if(config.isDebug()) {
		Memory modbusMemory = new Memory();
		Memory.storeToContext(modbusMemory, arg0.getServletContext());
	    }
	    
	    Logger.debug("**********************************************************************");
	    Logger.debug("*                        Modbus Rest Started                         *");

	    if (config.isDebug()) {
		Logger.info("*                     !!!! DEBUG MODE !!!!                        *");
	    }
	    Logger.info("**********************************************************************");

	} catch (Exception e) {
	    Logger.error(e.getClass().toString() + " " + e.getMessage());

	}
    }
}
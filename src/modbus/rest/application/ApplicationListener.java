package modbus.rest.application;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ApplicationListener implements ServletContextListener {

	private static final Logger Logger = LogManager.getLogger(ApplicationListener.class);


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
//		System.setProperty("java.util.logging.manage", "org.apache.logging.log4j.jul.LogManager");

		Logger.debug("**********************************************************************");
		Logger.debug("*                       Starting Modbus Rest                         *");
		Logger.debug("**********************************************************************");

		Logger.debug("**********************************************************************");
		Logger.debug("*                        Modbus Rest Started                         *");
		Logger.debug("**********************************************************************");
	}

}
package main.java.modbus.rest.service;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

import modbus.rest.filter.CorsFilter;
import modbus.rest.filter.Log4jFilter;
import modbus.rest.helpers.ConfigurationHelper;
import modbus.rest.models.Configuration;

public class Application extends ResourceConfig {

	private static final Logger Logger = LogManager.getLogger(Application.class);

	public Application() {
		packages("modbus.rest.service");
		boolean isLoggerEnabled = true;

		register(CorsFilter.class);

		
		if (isLoggerEnabled) {
			register(Log4jFilter.class);
		}

	}
}

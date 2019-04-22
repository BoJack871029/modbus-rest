package modbus.rest.service;

import org.glassfish.jersey.server.ResourceConfig;

import modbus.rest.filter.CorsFilter;

public class Application extends ResourceConfig {

	public Application() {
		packages("modbus.rest.service");

		register(CorsFilter.class);
	}
}

package main.java.modbus.rest.service;

import java.io.IOException;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import modbus.rest.helpers.ConfigurationHelper;
import modbus.rest.models.Configuration;
import modbus.rest.utils.RestServiceUtils;

@Path("/config")

public class ConfigurationService {
	protected static final Logger Logger = LogManager.getLogger(ConfigurationService.class);

	@PermitAll
	@GET
	@Path("/read")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readConfig() {
		Response response = null;
		try {
			Configuration config = ConfigurationHelper.readConfig(getClass());
			response = RestServiceUtils.buildSuccessResponse(config);
		} catch (IOException ex) {
			response = RestServiceUtils.buildErrorResponse(ex.getMessage());
			Logger.error(ex.getMessage());
		}

		return response;
	}

	@PermitAll
	@POST
	@Path("/write")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response writeConfig(String value) {
		Response response = null;
		try {
			ConfigurationHelper.writeConfig(getClass(), value);
			response = RestServiceUtils.buildSuccessResponse();
		} catch (IOException ex) {
			response = RestServiceUtils.buildErrorResponse(ex.getMessage());
			Logger.error(ex.getMessage());
		}

		return response;
	}
	
	@PermitAll
	@GET
	@Path("/load")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadConfig() {
		Response response = null;
		try {
			Configuration config = ConfigurationHelper.readConfig(getClass());
			response = RestServiceUtils.buildSuccessResponse(config);
		} catch (IOException ex) {
			response = RestServiceUtils.buildErrorResponse(ex.getMessage());
			Logger.error(ex.getMessage());
		}

		return response;
	}
}

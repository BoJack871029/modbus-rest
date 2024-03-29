package modbus.rest.service;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import modbus.rest.helpers.ConfigurationHelper;
import modbus.rest.helpers.ModbusHelper;
import modbus.rest.models.Configuration;
import modbus.rest.models.ModbusRegister;
import modbus.rest.utils.RestServiceUtils;

@Path("/modbus")
public class ModbusService {
    protected static final Logger Logger = LogManager.getLogger(ModbusService.class);

    @javax.ws.rs.core.Context
    private ServletContext context;

    @PermitAll
    @POST
    @Path("/read")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response readRegs(List<ModbusRegister> regs) {
	try {
	    Logger.debug("*** READ REGS 1");

	    Configuration config = ConfigurationHelper.readConfig(ModbusService.class);

	    if (config.isDebug()) {
		regs = ModbusHelper.readRegsFake(regs, context);
	    } else {
		regs = ModbusHelper.readRegs(regs);
	    }

	    Logger.debug("Registri letti:" + new Gson().toJson(regs));

	    return RestServiceUtils.buildSuccessResponse(regs);
	} catch (Exception ex) {
	    Logger.error(ex.getMessage());
	    return RestServiceUtils.buildErrorResponse();
	}
    }

    @PermitAll
    @POST
    @Path("/write")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response writeRegs(List<ModbusRegister> regs) {
	try {
	    Logger.debug("*** WRITE REGS");
	    Logger.debug("Registri da scrivere:" + new Gson().toJson(regs));

	    Configuration config = ConfigurationHelper.readConfig(ModbusService.class);

	    if (config.isDebug()) {
		ModbusHelper.writeRegsFake(regs, context);
	    } else {
		ModbusHelper.writeRegs(regs);
	    }

	    return RestServiceUtils.buildSuccessResponse();
	} catch (Exception e) {
	    Logger.error(e.getMessage());
	    return RestServiceUtils.buildErrorResponse();
	}
    }
}

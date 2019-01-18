package modbus.rest.service;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/info")
public class InfoService {
	@PermitAll
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getValues(@PathParam("slave") int slave, @PathParam("regs") int reg) {

		
		return Response.ok("Info").build();
	}
}

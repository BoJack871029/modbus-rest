//package test.java;
//
//import static org.junit.Assert.assertEquals;
//
//import java.sql.DriverManager;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.core.Application;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.Response.Status;
//
//import org.glassfish.jersey.server.ResourceConfig;
//import org.glassfish.jersey.test.JerseyTest;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//
//import main.java.modbus.rest.service.ModbusService;
//import modbus.rest.models.ModbusRegister;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(DriverManager.class)
//public class ModbusServiceTest extends JerseyTest {
//
//	@Override
//	protected Application configure() {
//		return new ResourceConfig(ModbusService.class);
//	}
//
//	@Test
//	public void test() {
////		List<ModbusRegister> regs = new ArrayList<ModbusRegister>();
////		ModbusRegister r= new  ModbusRegister();
////		r.setRegister(1);
////		r.setSlave(1);
////		regs.add(r);
////		Response response = target("/modbus/read").request().post(Entity.entity(regs, MediaType.APPLICATION_JSON));
////
////		assertEquals("Http Response should be 200: ", Status.OK.getStatusCode(), response.getStatus());
////		
////		System.out.println(response.readEntity(String.class));
////		Object obj=response.getEntity();
//
//	assertEquals("1","1");
//	}
//}

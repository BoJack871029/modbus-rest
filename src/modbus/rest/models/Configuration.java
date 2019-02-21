package modbus.rest.models;

public class Configuration {
	private Logger logger;
	private ModbusSettings modbus;
	private boolean debug;

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public ModbusSettings getModbus() {
		return modbus;
	}

	public void setModbus(ModbusSettings modbus) {
		this.modbus = modbus;
	}

}

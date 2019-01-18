package modbus.rest.helpers;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import modbus.rest.models.Configuration;
import modbus.rest.models.ModbusSettings;
import modbus.rest.utils.ConfigManager;

public class ConfigurationHelper {

	private static String CONFIG_FILE = "/opt/modbus_rest/config.json";

	public static Configuration readConfig(Class<?> currentClass) throws IOException {
		String configJson = ConfigManager.readConfig(CONFIG_FILE, currentClass);

		ObjectMapper objectMapper = new ObjectMapper();

		return objectMapper.readValue(configJson, Configuration.class);
	}
	
	public static void writeConfig(Class<?> currentClass, String value) throws IOException {
		ConfigManager.writeConfig(CONFIG_FILE, value);
	}

	public static ModbusSettings readModbusConfig(Class<?> currentClass) throws IOException {
		Configuration config = readConfig(currentClass);

		return config.getModbus();

	}
}

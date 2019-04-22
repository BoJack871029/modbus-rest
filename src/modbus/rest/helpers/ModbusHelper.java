package modbus.rest.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import modbus.rest.com.ModbusRs485;
import modbus.rest.models.Configuration;
import modbus.rest.models.ModbusRegister;
import modbus.rest.models.ModbusSettings;
import modbus.rest.utils.Binary;

public class ModbusHelper {
	protected static final Logger Logger = LogManager.getLogger(ModbusHelper.class);

	private static List<ModbusRegister> regsToList(Map<Integer, ModbusRegister> regs) {
		List<ModbusRegister> regsList = new ArrayList<ModbusRegister>();

		for (Map.Entry<Integer, ModbusRegister> entry : regs.entrySet()) {
			regsList.add(entry.getValue());
		}

		return regsList;
	}

	public static ModbusRegister readReg(ModbusRegister reg) throws Exception {
		List<ModbusRegister> regs = new ArrayList<ModbusRegister>();

		regs.add(reg);

		regs = readRegs(regs);

		if (regs == null || regs.size() == 0) {
			throw new Exception("Errore durante la lettura");
		}

		return regs.get(0);
	}

	public static List<ModbusRegister> readRegs(List<ModbusRegister> regsToRead) throws Exception {

		ModbusSettings mbSettings = ConfigurationHelper.readModbusConfig(ModbusHelper.class);

		Configuration config = ConfigurationHelper.readConfig(ModbusHelper.class);

		Map<Integer, ModbusRegister> regsResult = new HashMap<Integer, ModbusRegister>();

		if (config.isDebug()) {
			regsResult = fakeRead(regsToRead);
		} else {
			regsResult = ModbusRs485.getInstance().readRegs(mbSettings, regsToRead);
		}

		return regsToList(regsResult);
	}

	public static void writeReg(ModbusRegister reg) throws Exception {

		List<ModbusRegister> regs = new ArrayList<ModbusRegister>();

		regs.add(reg);

		writeRegs(regs);
	}

	public static void writeRegs(List<ModbusRegister> regs) throws Exception {

		ModbusSettings mbSettings = ConfigurationHelper.readModbusConfig(ModbusHelper.class);

		Configuration config = ConfigurationHelper.readConfig(ModbusHelper.class);

		Logger.debug("Write regs: " + new Gson().toJson(regs));

		if (config.isDebug()) {
			fakeWrite();
		} else {
			ModbusRs485.getInstance().writeRegs(mbSettings, regs);
		}
	}

	public static int readBitValue(ModbusRegister reg) throws Exception {
		reg = readReg(reg);

		String binValue = Binary.intToBin(reg.getValue());

		return Binary.getBitValueAsInt(binValue, reg.getBit());
	}

	public static void writeBitValue(ModbusRegister registerToWrite, int value) throws Exception {
		registerToWrite = readReg(registerToWrite);

		int oldValue = registerToWrite.getValue();

		String valueBin = Binary.intToBin(oldValue);

		valueBin = Binary.setBitValue(valueBin, registerToWrite.getBit(), value == 1 ? '1' : '0');

		registerToWrite.setValue(Binary.binToInt(valueBin));

		writeReg(registerToWrite);
	}

	public static Map<Integer, ModbusRegister> fakeRead(List<ModbusRegister> regs) {
		Logger.debug("Fake read!");
		Map<Integer, ModbusRegister> regValues = new HashMap<Integer, ModbusRegister>();

		for (ModbusRegister reg : regs) {
			reg.setValue(getRandomInt());

			if (!regValues.containsKey(reg.getRegister())) {
				regValues.put(reg.getRegister(), reg);
			}
		}
		return regValues;
	}

	public static void fakeWrite() {
		Logger.debug("Fake write!");
	}

	private static boolean getRandomBoolean() {
		return Math.random() < 0.5;
	}

	private static int getRandomInt() {
		return (int) (Math.random() * 26 + 14);
	}
}

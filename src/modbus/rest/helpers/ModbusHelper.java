package modbus.rest.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import modbus.rest.com.ModbusRs485;
import modbus.rest.debug.Memory;
import modbus.rest.models.Configuration;
import modbus.rest.models.ModbusRegister;
import modbus.rest.models.ModbusSettings;
import modbus.rest.utils.Binary;

public class ModbusHelper {
    @javax.ws.rs.core.Context
    private static ServletContext context;

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

    public static List<ModbusRegister> readRegsFake(List<ModbusRegister> regsToRead, ServletContext context)
	    throws Exception {

	Map<Integer, ModbusRegister> regsResult = new HashMap<Integer, ModbusRegister>();

	regsResult = fakeRead(regsToRead, context);

	return regsToList(regsResult);
    }

    public static List<ModbusRegister> readRegs(List<ModbusRegister> regsToRead) throws Exception {

	ModbusSettings mbSettings = ConfigurationHelper.readModbusConfig(ModbusHelper.class);

	Map<Integer, ModbusRegister> regsResult = new HashMap<Integer, ModbusRegister>();

	regsResult = ModbusRs485.getInstance().readRegs(mbSettings, regsToRead);

	return regsToList(regsResult);
    }

    public static void writeReg(ModbusRegister reg) throws Exception {

	List<ModbusRegister> regs = new ArrayList<ModbusRegister>();

	regs.add(reg);

	writeRegs(regs);
    }

    public static void writeRegsFake(List<ModbusRegister> regs, ServletContext context) throws Exception {
	Logger.debug("Write regs: " + new Gson().toJson(regs));

	fakeWrite(regs, context);
    }

    public static void writeRegs(List<ModbusRegister> regs) throws Exception {
	Logger.debug("Write regs: " + new Gson().toJson(regs));

	ModbusSettings mbSettings = ConfigurationHelper.readModbusConfig(ModbusHelper.class);

	ModbusRs485.getInstance().writeRegs(mbSettings, regs);
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

    public static Map<Integer, ModbusRegister> fakeRead(List<ModbusRegister> regs, ServletContext context)
	    throws IOException {
	Logger.debug("Fake read!");

	Memory modbusMemory = Memory.readFromContext(context);

	Map<Integer, ModbusRegister> regValues = new HashMap<Integer, ModbusRegister>();

	for (ModbusRegister reg : regs) {
	    int regValue = modbusMemory.readRegister(reg.getRegister());

	    reg.setValue(regValue);

	    if (!regValues.containsKey(reg.getRegister())) {
		regValues.put(reg.getRegister(), reg);
	    }
	}

	Logger.debug("Fake read done!");

	return regValues;
    }

    public static void fakeWrite(List<ModbusRegister> regs, ServletContext context) throws IOException {
	Logger.debug("Fake write!");

	Memory modbusMemory = Memory.readFromContext(context);

	for (ModbusRegister reg : regs) {
	    modbusMemory.writeRegister(reg.getRegister(), reg.getValue());
	}

	Logger.debug("Fake write done!");
    }

//    private static int getRandomInt() {
//	return (int) (Math.random() * 26 + 14);
//    }
}

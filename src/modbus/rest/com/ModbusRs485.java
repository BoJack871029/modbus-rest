package modbus.rest.com;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortFactoryJSSC;
import com.intelligt.modbus.jlibmodbus.serial.SerialUtils;

import modbus.rest.models.ModbusRegister;
import modbus.rest.models.ModbusSettings;

public class ModbusRs485 {

	private static final Logger Logger = LogManager.getLogger(ModbusRs485.class);

	private static final ModbusRs485 instance = new ModbusRs485();

	private static Semaphore semaphore = new Semaphore(1);

	// private constructor to avoid client applications to use constructor
	private ModbusRs485() {
	}

	public static ModbusRs485 getInstance() {
		return instance;
	}

	private static SerialParameters getSpParameters(ModbusSettings mbSettings) {
		SerialParameters sp = new SerialParameters();

		// Porta seriale
		sp.setDevice(mbSettings.getPort());

		// Velocità
		switch (mbSettings.getBound()) {
		case "19200":
			sp.setBaudRate(SerialPort.BaudRate.BAUD_RATE_19200);
			break;
		case "9600":
			sp.setBaudRate(SerialPort.BaudRate.BAUD_RATE_9600);
			break;
		}

		// Data bits
		sp.setDataBits(Integer.parseInt(mbSettings.getDatabit()));

		// Parity
		switch (mbSettings.getParity()) {
		case "O":
			sp.setParity(SerialPort.Parity.ODD);
			break;
		case "E":
			sp.setParity(SerialPort.Parity.EVEN);
			break;
		case "N":
			sp.setParity(SerialPort.Parity.NONE);
			break;
		}

		// Stop bits
		sp.setStopBits(Integer.parseInt(mbSettings.getBitstop()));
		return sp;
	}

	private Map<Integer, ModbusRegister> distinct(List<ModbusRegister> regs) {

		Map<Integer, ModbusRegister> regsToRead = new TreeMap<Integer, ModbusRegister>();

		for (ModbusRegister register : regs) {
			if (!regsToRead.containsKey(register.getRegister())) {
				regsToRead.put(register.getRegister(), register);
			}
		}
		return regsToRead;
	}

	private Queue<ModbusRegister> toQueue(Map<Integer, ModbusRegister> regs) {
		Queue<ModbusRegister> queueRegs = new LinkedList<ModbusRegister>();

		for (Map.Entry<Integer, ModbusRegister> entry : regs.entrySet()) {
			ModbusRegister modbusRegister = entry.getValue();

			queueRegs.add(modbusRegister);
		}

		return queueRegs;
	}

	private List<ModbusRegister> get(Queue<ModbusRegister> allRegs, int offset) {
		List<ModbusRegister> regsToRead = new ArrayList<ModbusRegister>();

		if (!allRegs.isEmpty()) {

			ModbusRegister modbusRegister = allRegs.poll();

			int startAddress = modbusRegister.getRegister();

			regsToRead.add(modbusRegister);

			while (!allRegs.isEmpty()) {
				modbusRegister = allRegs.peek();

				if ((startAddress + offset) <= modbusRegister.getRegister()) {
					break;
				}
				regsToRead.add(allRegs.poll());
			}
		}

		return regsToRead;
	}

	private int[] readRegsFromSerial(ModbusMaster modbus, List<ModbusRegister> regs)
			throws ModbusProtocolException, ModbusNumberException, ModbusIOException {
		int regsToReadCount = regs.get(regs.size() - 1).getRegister() - regs.get(0).getRegister() + 1;

		int startAddress = regs.get(0).getRegister();
		int idSlave = regs.get(0).getSlave();
		int values[];
//		if (!AppListener.DEBUG) {
		values = modbus.readHoldingRegisters(idSlave, startAddress, regsToReadCount);
//		} else {
//			values = new int[regsToReadCount];
//
//			for (int i = 0; i < values.length; i++) {
//				values[i] = i + 1;
//			}
//		}
		return values;

	}

	public Map<Integer, ModbusRegister> readRegs(ModbusSettings mbSettings, List<ModbusRegister> regs)
			throws Exception {
		ModbusMaster modbusMaster = null;
		try {
			Logger.debug("READ REGS");
			semaphore.acquire();

			Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);

			SerialUtils.setSerialPortFactory(new SerialPortFactoryJSSC());

			modbusMaster = ModbusMasterFactory.createModbusMasterRTU(getSpParameters(mbSettings));

			modbusMaster.connect();

			Logger.debug("Port opened!!");

			// Ordino i registri
			regs.sort((ModbusRegister r1, ModbusRegister r2) -> (int) r1.getRegister() - (int) r2.getRegister());

			// Distinct
			Map<Integer, ModbusRegister> regsToRead = distinct(regs);

			// To queue
			Queue<ModbusRegister> queueRegs = toQueue(regsToRead);

			int offset = 64;
			List<ModbusRegister> regsSingleRead = get(queueRegs, offset);

			while (!regsSingleRead.isEmpty()) {
				Logger.debug("Read: " + regsSingleRead);

				int startAddress = regsSingleRead.get(0).getRegister();

				int values[] = readRegsFromSerial(modbusMaster, regsSingleRead);

				for (ModbusRegister register : regsSingleRead) {
					regsToRead.get(register.getRegister()).setValue(values[register.getRegister() - startAddress]);
				}

				regsSingleRead = get(queueRegs, offset);
			}

			return regsToRead;
		} catch (Exception e) {
			throw new Exception("Errore readRegsV2: " + e.getMessage());
		} finally {

			try {
				if (modbusMaster != null)
					modbusMaster.disconnect();
			} catch (ModbusIOException e1) {
				e1.printStackTrace();
				Logger.error(e1.getMessage());
			}
			semaphore.release();
			Logger.debug("READ REGS DONE");

		}
	}

	public void writeRegs(ModbusSettings mbSettings, List<ModbusRegister> regs) throws Exception {
		ModbusMaster modBusMaster = null;
		Exception writeException = null;
		try {
			Logger.debug("Acquiring lock...");
			semaphore.acquire();
			Logger.debug("Done!");

			Logger.debug("Regs to write: " + regs.toString());

			Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);

			Logger.debug("Init modbus...");

			SerialUtils.setSerialPortFactory(new SerialPortFactoryJSSC());

			modBusMaster = ModbusMasterFactory.createModbusMasterRTU(getSpParameters(mbSettings));
			Logger.debug("Init done, open port...");

			modBusMaster.connect();
			Logger.debug("Port opened!!");

			for (ModbusRegister r : regs) {

				modBusMaster.writeSingleRegister(r.getSlave(), r.getRegister(), r.getValue());

				Logger.debug("Wrote: " + r);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			writeException = e;
		} finally {
			try {
				if (modBusMaster != null)
					modBusMaster.disconnect();
			} catch (ModbusIOException e1) {
				e1.printStackTrace();
				Logger.error(e1.getMessage());
			}
			semaphore.release();

			if (writeException != null) {
				throw writeException;
			}
		}

	}
}

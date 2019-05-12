package modbus.rest.debug;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

public class Memory {
    private Map<Integer, Integer> registers;

    private final static String MEMORY_NAME = "MODBUS_MEMORY";

    public Memory() {
	registers = new HashMap<Integer, Integer>();

    }

    public void writeRegister(int number, int value) {

	registers.put(number, value);
    }

    public int readRegister(int number) {
	if (!registers.containsKey(number)) {
	    int randomValue = (int) (Math.random() * 50 + 1);
	    registers.put(number, randomValue);
	}
	return registers.get(number);
    }

    public static void storeToContext(Memory memory, ServletContext context) throws IOException {
	context.setAttribute(MEMORY_NAME, memory);
    }

    public static Memory readFromContext(ServletContext context) throws IOException {
	return (Memory) context.getAttribute(MEMORY_NAME);
    }

}

package com.supermap.desktop.process.virtual;

import java.util.HashMap;

/**
 * Created by highsad on 2017/11/10.
 */
public class VirtualType {
	public static VirtualType OBJECT = new VirtualType();
	private static int currentSection;
	private static HashMap<Integer, Byte> registeredSectionsMap = new HashMap<>();

	private HashMap<Integer, Byte> code = new HashMap<>();

	static {
		registeredSectionsMap.put(1, (byte) 0x00);
		currentSection = 1;
	}

	public VirtualType() {

	}

	public VirtualType(int section, byte typeCode) {
		this.code.put(section, typeCode);
	}

	public VirtualType and(VirtualType type) {
		return this;
	}

	public VirtualType and(int section, byte typeCode) {
		return this;
	}

	public static VirtualType registerNewUniqueType() {
		VirtualType type = null;

		validateCurrentSection();
		byte typeCode = distributeAvailableCode();
		return new VirtualType(currentSection, typeCode);
	}

	private static int validateCurrentSection() {
		if (!registeredSectionsMap.containsKey(currentSection)) {
			throw new Error();
		}

		byte value = registeredSectionsMap.get(currentSection);
		if (value == (byte) 0xFF) {
			currentSection++;
			registeredSectionsMap.put(currentSection, (byte) 0x00);
		}

		return currentSection;
	}

	private static byte distributeAvailableCode() {
		byte sectionCode = registeredSectionsMap.get(currentSection);
		byte typeCode = (byte) (sectionCode & 0xFF + 0x01);
		sectionCode = (byte) (sectionCode | typeCode);
		registeredSectionsMap.put(currentSection, sectionCode);
		return typeCode;
	}
}

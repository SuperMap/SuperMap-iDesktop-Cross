package com.supermap.desktop.process.types;

import java.util.HashMap;
import java.util.Map;

/**
 * 类型管理，用来对类型编码，并可方便的自定义类型，也可简单的根据已有类型组合新的类型。
 * 同时在后续工作流交互过程中，各种对类型的求交、求并、求等等操作，拥有简洁的代码和良好的性能。
 * 比如：简单矢量数据集类型 是否包含于 矢量数据集类型；矢量点线数据集 是否部分相交于 简单二维矢量数据集。
 * 编码规则如下，从 1 开始，分若干个编码区间，每一个编码区间取 8 位编码，比如 0100 0000、1001 0101，同一个类型由一个或多个编码区间组成类型编码。
 * 不同类型的相同编码区间，相同的编码位置如果都为 1，则认为这两种类型有部分相同的类型特性，比如 假设点线数据集类型是 0000 0011，线面数据集是
 * 0000 0110，从右往左第二位都是1，表示线数据集类型，如果有功能需求线数据集，则都可以使用这两种数据集类型作为参数。
 * <p>
 * 类型分原子类型和复杂类型。
 * 所有原子类型的编码相互之间都没有相同的类型特性，也就是不会有任何两个原子类型的编码位同为 1，也就意味着原子类型的编码永远只有一段，并且
 * 只有一位编码位为 1，其余七位编码位为 0，同一段最多能获取8个不同的编码，从 0000 0001 开始，到 1000 0000 结束。
 * 原子类型编码通过 VritualType 提供的静态方法 registerNewUniqueType 直接获取可用的唯一编码。
 * 复杂类型编码，由多个原子类型编码组合而成，将相同 section 的编码按位或得到新的 section 类型编码，比如 section 1 的类型编码 0100 0000 和 1000 0000
 * 按位或组合而成的新的 section 1 类型编码为 1100 0000。
 * Created by highsad on 2017/11/10.
 */
public class Type {
	public static Type OBJECT = new Type("object");
	private static int currentSection;

	// 已分配的 section 原子编码，每一段初始化为 0000 0000，分配一个，就将对应的位设置为1，直到 1111 1111，则该 section 分配完毕，新建 section 继续分配
	private static HashMap<Integer, Byte> registeredSectionsMap = new HashMap<>();

	private String name;
	private HashMap<Integer, Byte> code = new HashMap<>();

	static {
		registeredSectionsMap.put(1, (byte) 0x00);
		currentSection = 1;
	}

	private Type() {
		this("");
	}

	private Type(String name) {
		this.name = name;
	}

	private Type(String name, int section, byte typeCode) {
		this.name = name;
		this.code.put(section, typeCode);
	}

	public String getName() {
		return this.name;
	}

	public Map<Integer, Byte> getCode() {
		return this.code;
	}

	public Type and(Type otherType) {
		if (otherType == null) {
//			throw new NullArgumentException("otherType");
			return null;
		}

		Map<Integer, Byte> otherCode = otherType.getCode();
		for (Integer section :
				otherCode.keySet()) {
			and(section, otherCode.get(section));
		}
		return this;
	}

	public Type and(int section, byte typeCode) {
		if (!this.code.containsKey(section)) {
			this.code.put(section, (byte) 0x00);
		}

		Byte sectionCode = this.code.get(section);
		this.code.put(section, (byte) (sectionCode | typeCode));
		return this;
	}

	public boolean contains(Type otherType) {
		if (otherType == null) {
			return false;
		}

		if (otherType.getCode().size() > this.code.size()) {
			return false;
		}

		boolean contains = true;
		Map<Integer, Byte> otherCode = otherType.getCode();

		for (Integer section :
				otherCode.keySet()) {

			// 待检查的类型编码，有当前类型不存在的 section 时，退出循环
			if (!this.code.containsKey(section)) {
				contains = false;
				break;
			}

			byte otherSectionCode = otherCode.get(section);
			byte sectionCode = this.code.get(section);
			if ((byte) (sectionCode & otherSectionCode) != otherSectionCode) {
				contains = false;
				break;
			}
		}
		return contains;
	}

	public boolean intersects(Type otherType) {
		if (otherType == null) {
			return false;
		}

		boolean intersects = false;
		Map<Integer, Byte> otherCode = otherType.getCode();

		for (Integer section :
				otherCode.keySet()) {
			if (!this.code.containsKey(section)) {
				continue;
			}

			byte otherSectionCode = otherCode.get(section);
			byte sectionCode = this.code.get(section);

			if ((sectionCode & otherSectionCode) != 0) {
				intersects = true;
				break;
			}
		}
		return intersects;
	}

	public boolean equals(Type otherType) {
		if (otherType == null) {
			return false;
		}

		if (otherType.getCode().size() != this.code.size()) {
			return false;
		}

		boolean equals = true;
		Map<Integer, Byte> otherCode = otherType.getCode();

		for (Integer section :
				otherCode.keySet()) {
			if (!this.code.containsKey(section)) {
				equals = false;
				break;
			}

			byte otherSectionCode = otherCode.get(section);
			byte sectionCode = this.code.get(section);
			if (otherSectionCode != sectionCode) {
				equals = false;
				break;
			}
		}
		return equals;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static Type instance(String name) {
		return new Type(name);
	}

	public static Type registerNewUniqueType(String name) {
		validateCurrentSection();
		byte typeCode = distributeAvailableCode();
		Type type = new Type(name, currentSection, typeCode);
		OBJECT.and(type);
		return type;
	}

	private static int validateCurrentSection() {
		if (!registeredSectionsMap.containsKey(currentSection)) {
			throw new Error();
		}

		byte value = registeredSectionsMap.get(currentSection);

		// 如果当前 section 已经分配完编码，则新建 section，并初始化为 0000 0000
		if (value == (byte) 0xFF) {
			currentSection++;
			registeredSectionsMap.put(currentSection, (byte) 0x00);
		}

		return currentSection;
	}

	/**
	 * 分配一个可用的类型编码
	 *
	 * @return
	 */
	private static byte distributeAvailableCode() {

		// 获取当前 section 已分配原子编码
		byte sectionCode = registeredSectionsMap.get(currentSection);

		// 加一，就可以得到新的原子类型编码
		byte typeCode = (byte) ((sectionCode & 0xFF) + 0x01);

		// 再将新分配的原子编码与已分配的 section 编码按位或，即可得到新的 section 已分配编码
		sectionCode = (byte) (sectionCode | typeCode);
		registeredSectionsMap.put(currentSection, sectionCode);
		return typeCode;
	}
}

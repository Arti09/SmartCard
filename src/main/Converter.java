package main;

import java.math.BigInteger;

import opencard.core.util.HexString;

public class Converter {
	public static byte[] StringToByte(String s) {
		byte[] byteArray = new byte[s.length()];
		s = HexString.hexify(s.getBytes()).replace(" ", "");
		byteArray = HexString.parseHexString(s);
		return byteArray;
	}

	public static String ByteToString(byte[] byteArray) {
		String s = "";
		s = HexString.hexify(byteArray).replace(" ", "");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i = i + 2) {
			sb.append((char) Integer.parseInt(s.substring(i, i + 2), 16));
		}
		return sb.toString();
	}

	public static byte[] IntegerToByte(int value, int arrayLength) {
		byte[] byteArray = new byte[arrayLength];
		String hexString = Integer.toHexString(value);
		while (hexString.length() / 2 != arrayLength) {
			hexString = "0" + hexString;
		}
		for (int i = 0; i < hexString.length(); i = i + 2) {
			byte[] buffer = new BigInteger(hexString.substring(i, i + 2), 16)
					.toByteArray();
			if (buffer.length == 2)
				byteArray[i / 2] = buffer[1];
			else if (buffer.length == 1)
				byteArray[i / 2] = buffer[0];
		}
		return byteArray;
	}

	public static int ByteToInteger(byte[] byteArray) {
		int value = 0;
		String hexString = HexString.hexify(byteArray).replace(" ", "");
		value = Integer.parseInt(hexString, 16);
		return value;
	}

	public static byte IntegerToSingleByte(int value) {
		return IntegerToByte(value, 1)[0];
	}

	public static int SingleByteToInteger(byte b) {
		byte[] array = new byte[1];
		array[0] = b;
		return ByteToInteger(array);
	}

}

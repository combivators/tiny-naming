package net.tiny.context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ObjectBytes {

	/**
	 * All possible chars for representing a number as a String
	 **/
	private final static char[] DIGITS = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z'
		};

	static String toBigIntegerString(BigInteger big, int radix) {
		StringBuffer sb = new StringBuffer();
		if (big.signum() == -1) {
			sb.append('-');
		}
		BigInteger i = new BigInteger(big.abs().toByteArray());
		BigInteger r = new BigInteger(Integer.toString(radix));
		do {
			BigInteger[] dr = i.divideAndRemainder(r);
			sb.insert(0, DIGITS[dr[1].intValue()]);
			i = dr[0];
		} while (!i.equals(BigInteger.ZERO));
		return sb.toString();
	}

	static BigInteger toBigInteger(String value, int radix) {
		int signum = 1;
		BigInteger big = new BigInteger(BigInteger.ZERO.toByteArray());
		BigInteger r = new BigInteger(Integer.toString(radix));
		char[] buf = value.toCharArray();
		if (buf[buf.length - 1] == '-') {
			signum = -1;
			char[] temp = new char[buf.length - 1];
			System.arraycopy(buf, 0, temp, 0, buf.length - 1);
			buf = temp;
		}
		for (int i = buf.length - 1; i >= 0; i--) {
			int num = Arrays.binarySearch(DIGITS, buf[i]);
			BigInteger v = new BigInteger(Integer.toString(num));
			big = big.add(v.multiply(r.pow(buf.length - 1 - i)));
		}
		if (signum == -1) {
			return big.negate();
		} else {
			return big;
		}
	}

	public static Object toObject(byte[] data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object obj = ois.readObject();
			bais.close();
			return obj;
		} catch (Exception ex) {
			return null;
		}
	}

	public static byte[] toBytes(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			baos.close();
			return baos.toByteArray();
		} catch (Exception ex) {
			return null;
		}
	}

	public static String encode(Object object) {
		return encode(object, true);
	}

	public static String encode(Object object, boolean compress) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = null;
			ObjectOutputStream oos;
			if (compress) {
				gzip = new GZIPOutputStream(baos);
				oos = new ObjectOutputStream(gzip);
			} else {
				oos = new ObjectOutputStream(baos);
			}
			oos.writeObject(object);
			if (gzip != null) {
				gzip.finish();
			}
			baos.flush();
			baos.close();
			BigInteger big = new BigInteger(baos.toByteArray());
			return toBigIntegerString(big, DIGITS.length);
		} catch (Exception ex) {
			return null;
		}
	}

	public static Object decode(String data) {
		return decode(data, true);
	}

	public static Object decode(String data, boolean compress) {
		try {
			BigInteger big = toBigInteger(data, DIGITS.length);
			ByteArrayInputStream bais =
					new ByteArrayInputStream(big.toByteArray());
			ObjectInputStream ois;
			if (compress) {
				GZIPInputStream gzip = new GZIPInputStream(bais);
				ois = new ObjectInputStream(gzip);
			} else {
				ois = new ObjectInputStream(bais);
			}
			Object obj = ois.readObject();
			bais.close();
			return obj;
		} catch (Exception ex) {
			return null;
		}
	}
}

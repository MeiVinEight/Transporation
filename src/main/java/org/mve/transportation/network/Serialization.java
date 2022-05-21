package org.mve.transportation.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Serialization
{

	public static int read(InputStream in) throws IOException
	{
		int i = in.read();
		if (i == -1)
		{
			throw new EOFException();
		}
		return i;
	}

	public static void read(InputStream in, byte[] b) throws IOException
	{
		Serialization.read(in, b, 0, b.length);
	}

	public static void read(InputStream in, byte[] b, int offset, int len) throws IOException
	{
		while (len > 0)
		{
			int i = in.read(b, offset, len);
			if (i < 0)
			{
				throw new EOFException();
			}
			offset += i;
			len -= i;
		}
	}

	public static boolean readBoolean(InputStream in) throws IOException
	{
		return Serialization.read(in) != 0;
	}

	public static short readShort(InputStream in) throws IOException
	{
		return (short) ((Serialization.read(in) << 8) | Serialization.read(in));
	}

	public static int readUnsignedShort(InputStream in) throws IOException
	{
		return ((Serialization.read(in) << 8) | Serialization.read(in));
	}

	public static char readChar(InputStream in) throws IOException
	{
		return (char) Serialization.readShort(in);
	}

	public static int readInt(InputStream in) throws IOException
	{
		return ((Serialization.read(in) << 24) | (Serialization.read(in) << 16) | (Serialization.read(in) << 8) | Serialization.read(in));
	}

	public static int readVarInt(InputStream in) throws IOException
	{
		int ret = 0;
		int i = 0;
		int current;
		do
		{
			if (i >= 5)
			{
				throw new IOException("VarInt is too big");
			}

			current = Serialization.read(in);
			ret |= (current & 0x7F) << (7 * i++);
		}
		while (current >>> 7 != 0);
		return ret;
	}

	public static float readFloat(InputStream in) throws IOException
	{
		return Float.intBitsToFloat(readInt(in));
	}

	public static long readLong(InputStream in) throws IOException
	{
		byte[] b = new byte[8];
		long l = 0;
		Serialization.read(in, b);
		for (int i = 0; i < 8; i++)
		{
			l |= ((long)b[i] << ((7 - i) * 8));
		}
		return l;
	}

	public static long readVarLong(InputStream in) throws IOException
	{
		long ret = 0;
		int i = 0;
		long current;
		do
		{
			if (i >= 10)
			{
				throw new IOException("VarInt is too big");
			}

			current = Serialization.read(in);
			ret |= (current & 0x7F) << (7 * i++);
		}
		while (current >>> 7 != 0);
		return ret;
	}

	public static double readDouble(InputStream in) throws IOException
	{
		return Double.longBitsToDouble(readLong(in));
	}

	public static String readString(InputStream in) throws IOException
	{
		int len = Serialization.readVarInt(in);
		byte[] b = new byte[len];
		Serialization.read(in, b);
		return new String(b, StandardCharsets.UTF_8);
	}

	public static String readUTF8(InputStream in) throws IOException
	{
		int len = Serialization.readUnsignedShort(in);
		byte[] b = new byte[len];
		Serialization.read(in, b);
		return new String(b, StandardCharsets.UTF_8);
	}

	public static void writeBoolean(OutputStream out, boolean b) throws IOException
	{
		out.write(b ? 1 : 0);
	}

	public static void writeShort(OutputStream out, int s) throws IOException
	{
		out.write((s >>> 8) & 0xFF);
		out.write(s & 0xFF);
	}

	public static void writeChar(OutputStream out, char c) throws IOException
	{
		writeShort(out, c);
	}

	public static void writeInt(OutputStream out, int i) throws IOException
	{
		out.write((i >>> 24) & 0xFF);
		out.write((i >>> 16) & 0xFF);
		out.write((i >>> 8) & 0xFF);
		out.write(i & 0xFF);
	}

	public static void writeVarInt(OutputStream out, int v) throws IOException
	{
		do
		{
			int b = v & 0x7F;
			v >>>= 7;
			b |= v == 0 ? 0 : 0x80;
			out.write(b);
		}
		while (v != 0);
	}

	public static void writeFloat(OutputStream out, float f) throws IOException
	{
		writeInt(out, Float.floatToIntBits(f));
	}

	public static void writeLong(OutputStream out, long l) throws IOException
	{
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++)
		{
			b[i] = (byte) ((l >>> ((7 - i) * 8)) & 0xFF);
		}
		out.write(b);
	}

	public static void writeVarLong(OutputStream out, long v) throws IOException
	{
		do
		{
			int b = (int) (v & 0x7F);
			v >>>= 7;
			b |= v == 0 ? 0 : 0x80;
			out.write(b);
		}
		while (v != 0);
	}

	public static void writeDouble(OutputStream out, double d) throws IOException
	{
		writeLong(out, Double.doubleToLongBits(d));
	}

	public static void writeString(OutputStream out, String s) throws IOException
	{
		byte[] b = s.getBytes(StandardCharsets.UTF_8);
		Serialization.writeVarInt(out, b.length);
		out.write(b);
	}

	public static void writeUTF8(OutputStream out, String s) throws IOException
	{
		byte[] b = s.getBytes(StandardCharsets.UTF_8);
		Serialization.writeShort(out, b.length);
		out.write(b);
	}
}

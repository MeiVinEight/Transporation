package org.mve.text;

public class Hexadecimal
{
	private static final byte[] H = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	public static byte[] transform(long num)
	{
		byte[] ret = new byte[16];
		for (int i = 0; i < 16; i++)
		{
			ret[i] = H[(int) ((num >>> ((15 - i) * 4)) & 0xF)];
		}
		return ret;
	}
}

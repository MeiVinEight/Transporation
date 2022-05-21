package org.mve.transportation.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Compression
{
	public static byte[] compress(byte[] data) throws IOException
	{
		byte[] ret;
		Deflater compresser = new Deflater();

		compresser.reset();
		compresser.setInput(data);
		compresser.finish();

		try (ByteArrayOutputStream out = new ByteArrayOutputStream(data.length))
		{
			byte[] b = new byte[1024];
			while (!compresser.finished())
			{
				int i = compresser.deflate(b);
				out.write(b, 0, i);
			}
			ret = out.toByteArray();
		}

		compresser.end();
		return ret;
	}

	public static byte[] uncompress(byte[] data) throws IOException
	{
		byte[] ret;

		Inflater uncompresser = new Inflater();
		uncompresser.reset();
		uncompresser.setInput(data);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			byte[] b = new byte[1024];
			while (!uncompresser.finished())
			{
				int i = uncompresser.inflate(b);
				out.write(b, 0, i);
			}
			ret = out.toByteArray();
		}
		catch (DataFormatException e)
		{
			throw new IOException(e);
		}

		uncompresser.end();
		return ret;
	}
}

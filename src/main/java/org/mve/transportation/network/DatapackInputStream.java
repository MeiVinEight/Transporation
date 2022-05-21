package org.mve.transportation.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class DatapackInputStream extends DataInputStream
{
	private ByteArrayInputStream in = (ByteArrayInputStream) super.in;

	public DatapackInputStream(byte[] data)
	{
		super(new ByteArrayInputStream(data));
	}

	public int readVarInt() throws IOException
	{
		return Serialization.readVarInt(this.in);
	}

	public long readVarLong() throws IOException
	{
		return Serialization.readVarLong(this.in);
	}

	public String readString() throws IOException
	{
		return Serialization.readString(this.in);
	}

	public void uncompress() throws IOException
	{
		int ulen = this.readVarInt();
		if (ulen != 0)
		{
			byte[] b = new byte[this.in.available()];
			Serialization.read(this.in, b);
			b = Compression.uncompress(b);
			if (b.length != ulen)
			{
				throw new IOException("Unknown zlib compressed data");
			}
			super.in = this.in = new ByteArrayInputStream(b);
		}
	}
}

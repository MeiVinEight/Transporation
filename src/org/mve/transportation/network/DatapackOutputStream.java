package org.mve.transportation.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DatapackOutputStream extends DataOutputStream
{
	private final ByteArrayOutputStream out = (ByteArrayOutputStream) super.out;

	public DatapackOutputStream()
	{
		super(new ByteArrayOutputStream());
	}

	public void writeVarInt(int v) throws IOException
	{
		Serialization.writeVarInt(this.out, v);
	}

	public void writeVarLong(long v) throws IOException
	{
		Serialization.writeVarLong(this.out, v);
	}

	public void writeString(String s) throws IOException
	{
		Serialization.writeString(this.out, s);
	}

	public void compress(int threshold) throws IOException
	{
		byte[] data = this.out.toByteArray();
		this.out.reset();
		if (data.length < threshold)
		{
			this.writeVarInt(0);
			this.write(data);
		}
		else
		{
			this.writeVarInt(data.length);
			this.write(Compression.compress(data));
		}
	}

	public byte[] data()
	{
		return this.out.toByteArray();
	}
}

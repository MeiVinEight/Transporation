package org.mve.transportation.network.datapack;

import org.mve.transportation.Transportation;
import org.mve.transportation.network.DatapackInputStream;
import org.mve.transportation.network.DatapackOutputStream;
import org.mve.transportation.network.TransportationConnection;
import org.mve.transportation.network.ProtocolLibrary;

import java.io.IOException;

public class DatapackHandshaking extends Datapack
{
	public final int stats;
	public final int version;
	public final String name;

	public DatapackHandshaking(DatapackInputStream stream) throws IOException
	{
		super(ProtocolLibrary.HANDSHAKING, 0x00);
		this.stats = stream.readByte();
		this.version = stream.readVarInt();
		this.name = stream.readString();
	}

	public DatapackHandshaking(int stats, int version, String name)
	{
		super(ProtocolLibrary.HANDSHAKING, 0x00);
		this.stats = stats;
		this.version = version;
		this.name = name;
	}

	@Override
	public void serialize(DatapackOutputStream stream) throws IOException
	{
		stream.writeByte(this.stats);
		stream.writeVarInt(this.version);
		stream.writeString(this.name);
	}

	@Override
	public void consume(TransportationConnection connection) throws IOException
	{
		if (this.version == ProtocolLibrary.VERSION)
		{
			if (this.stats == ProtocolLibrary.HANDSHAKING)
			{
				connection.send(new DatapackHandshaking(ProtocolLibrary.PLAYING, ProtocolLibrary.VERSION, Transportation.transportation.name));
			}
			connection.stats(ProtocolLibrary.PLAYING);
			return;
		}
		connection.disconnect();
	}
}

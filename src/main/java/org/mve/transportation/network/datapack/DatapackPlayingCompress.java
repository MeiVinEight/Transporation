package org.mve.transportation.network.datapack;

import org.mve.transportation.network.DatapackInputStream;
import org.mve.transportation.network.DatapackOutputStream;
import org.mve.transportation.network.ProtocolLibrary;
import org.mve.transportation.network.TransportationConnection;

import java.io.IOException;

public class DatapackPlayingCompress extends Datapack
{
	public final boolean compress;
	public final int threshold;

	public DatapackPlayingCompress(DatapackInputStream stream) throws IOException
	{
		super(ProtocolLibrary.PLAYING, 0x01);
		this.compress = stream.readBoolean();
		this.threshold = stream.readVarInt();
	}

	public DatapackPlayingCompress(boolean compress, int threshold)
	{
		super(ProtocolLibrary.PLAYING, 0x01);
		this.compress = compress;
		this.threshold = threshold;
	}

	@Override
	public void serialize(DatapackOutputStream stream) throws IOException
	{
		stream.writeBoolean(this.compress);
		stream.writeVarInt(threshold);
	}

	@Override
	public void consume(TransportationConnection connection)
	{
		connection.compress(this.compress, this.threshold);
	}
}

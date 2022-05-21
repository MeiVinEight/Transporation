package org.mve.transportation.network;

import org.mve.transportation.network.datapack.Datapack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TransportationConnection
{
	private final Socket connection;
	public final InputStream I;
	public final OutputStream O;
	private int stats = 0;
	private boolean compress = false;
	private int threshold = 0;
	private boolean connecting;

	public TransportationConnection(Socket connection) throws IOException
	{
		System.out.println("Connection from " + connection.getInetAddress() + ":" + connection.getPort());
		this.connection = connection;
		this.I = connection.getInputStream();
		this.O = connection.getOutputStream();
		this.connecting = true;
		this.stats(ProtocolLibrary.HANDSHAKING);
	}

	public void stats(int v)
	{
		this.stats = v;
	}

	public boolean connecting()
	{
		return this.connecting;
	}

	public void disconnect() throws IOException
	{
		if (this.connecting)
		{
			this.connecting = false;
			this.I.close();
			this.O.close();
			this.connection.close();
			System.out.println("Disconnect from " + this.connection.getInetAddress() + ":" + this.connection.getPort());
		}
	}

	public void compress(boolean b, int threshold)
	{
		this.compress = b;
		this.threshold = threshold;
	}

	public Datapack receive() throws IOException
	{
		byte[] data = new byte[Serialization.readVarInt(this.I)];
		Serialization.read(this.I, data);
		DatapackInputStream stream = new DatapackInputStream(data);
		if (this.compress)
		{
			stream.uncompress();
		}
		int id = stream.readVarInt();
		return ProtocolLibrary.construct(stream, this.stats, id);
	}

	public void send(Datapack datapack) throws IOException
	{
		DatapackOutputStream stream = new DatapackOutputStream();
		stream.writeVarInt(datapack.ID);
		datapack.serialize(stream);
		if (this.compress)
		{
			stream.compress(this.threshold);
		}
		byte[] data = stream.data();
		Serialization.writeVarInt(this.O, data.length);
		this.O.write(data);
		this.O.flush();
	}
}

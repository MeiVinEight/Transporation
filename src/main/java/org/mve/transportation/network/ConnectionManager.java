package org.mve.transportation.network;

import org.mve.transportation.Transportation;
import org.mve.transportation.network.datapack.Datapack;
import org.mve.transportation.network.datapack.DatapackHandshaking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionManager implements Runnable
{
	private final ServerSocket server;
	private final ConcurrentMap<String, TransportationConnection> connection = new ConcurrentHashMap<>();

	public ConnectionManager() throws IOException
	{
		this.server = new ServerSocket(0);
		System.out.println("Transportation has been opened on port " + server.getLocalPort());
	}

	public void handshake(TransportationConnection connection) throws IOException
	{
		Datapack datapack = connection.receive();
		if (datapack instanceof DatapackHandshaking handshaking)
		{
			handshaking.consume(connection);
			if (connection.connecting())
			{
				String name = handshaking.name;
				TransportationConnection old = this.connection.get(name);
				if (old != null)
				{
					old.disconnect();
				}
				this.connection.put(name, connection);
				Transportation.transportation.asynchronous.ensureTimer(new SubscribeDatapack(connection), 0, 1);
				System.out.println("Transportation " + name + " has joined");
			}
			return;
		}
		connection.disconnect();
	}

	public void connect(String ip, int port) throws IOException
	{
		Socket socket = new Socket(ip, port);
		TransportationConnection connection = new TransportationConnection(socket);
		connection.send(new DatapackHandshaking(ProtocolLibrary.HANDSHAKING, ProtocolLibrary.VERSION, Transportation.transportation.name));
		handshake(connection);
	}

	public void disconnect(String name) throws IOException
	{
		TransportationConnection connection = this.connection.get(name);
		connection.disconnect();
		this.connection.remove(name);
	}

	public TransportationConnection connection(String name)
	{
		return this.connection.get(name);
	}

	public void close()
	{
		try
		{
			this.server.close();
			for (Map.Entry<String, TransportationConnection> entry : this.connection.entrySet())
			{
				entry.getValue().disconnect();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		this.connection.clear();
	}

	@Override
	public void run()
	{
		while (Transportation.transportation.running())
		{
			try
			{
				Socket socket = this.server.accept();
				TransportationConnection connection = new TransportationConnection(socket);
				this.handshake(connection);
			}
			catch (IOException e)
			{
				if (Transportation.transportation.running())
				{
					e.printStackTrace();
				}
			}
		}
	}
}

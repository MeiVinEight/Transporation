package org.mve.transportation.network.datapack;

import org.mve.transportation.network.DatapackOutputStream;
import org.mve.transportation.network.TransportationConnection;

import java.io.IOException;

public abstract class Datapack
{
	public final int STATS;
	public final int ID;
	public boolean synchronize = true;

	public Datapack(int stats, int id)
	{
		this.STATS = stats;
		ID = id;
	}

	public abstract void serialize(DatapackOutputStream stream) throws IOException;

	public void consume(TransportationConnection connection) throws Throwable
	{
	}

	public void synchronize(boolean s)
	{
		this.synchronize = s;
	}

	public void asynchronous()
	{
		this.synchronize(false);
	}
}

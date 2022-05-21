package org.mve.transportation.network;

import org.mve.transportation.network.datapack.Datapack;
import org.mve.transportation.schedule.ScheduleRunnable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SynchronizeDatapack extends ScheduleRunnable
{
	private final TransportationConnection connection;
	private final Queue<Datapack> datapack = new ConcurrentLinkedQueue<>();

	public SynchronizeDatapack(TransportationConnection connection)
	{
		this.connection = connection;
	}

	public void add(Datapack datapack)
	{
		this.datapack.add(datapack);
	}

	@Override
	public void run()
	{
		Datapack datapack;
		while ((datapack = this.datapack.poll()) != null)
		{
			try
			{
				datapack.consume(this.connection);
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
		}
	}
}

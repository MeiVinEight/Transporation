package org.mve.transportation.network;

import org.mve.transportation.Transportation;
import org.mve.transportation.network.datapack.Datapack;
import org.mve.transportation.schedule.ScheduleRunnable;
import org.mve.invoke.ReflectionFactory;

import java.io.IOException;

public class SubscribeDatapack extends ScheduleRunnable
{
	private final TransportationConnection connection;
	private final SynchronizeDatapack synchronize;

	public SubscribeDatapack(TransportationConnection connection)
	{
		this.connection = connection;
		this.synchronize = new SynchronizeDatapack(this.connection);
		Transportation.transportation.synchronize.ensureTimer(this.synchronize, 0, 1);
	}

	@Override
	public void run()
	{
		try
		{
			if (!this.connection.connecting())
			{
				this.cancel();
				return;
			}

			Datapack datapack;
			try
			{
				datapack = this.connection.receive();
			}
			catch (IOException e)
			{
				this.connection.disconnect();
				return;
			}

			if (datapack.synchronize)
			{
				this.synchronize.add(datapack);
			}
			else
			{
				datapack.consume(this.connection);
			}
		}
		catch (Throwable e)
		{
			ReflectionFactory.ACCESSOR.throwException(e);
		}
	}
}

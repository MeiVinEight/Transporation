package org.mve.transportation.schedule;

import org.mve.transportation.Environment;
import org.mve.transportation.Transportation;

public class SubscribedAsynchronous implements Runnable
{
	private final TransportationScheduled schedule;

	public SubscribedAsynchronous(TransportationScheduled schedule)
	{
		this.schedule = schedule;
	}

	@Override
	public void run()
	{
		long next = System.nanoTime() + 20_000_000;
		while (!this.schedule.canceled() && Transportation.transportation.running())
		{
			if (this.schedule.run() && !this.schedule.timer())
			{
				break;
			}

			Environment.tick(next - System.nanoTime());
			next += 20_000_000;
		}
	}
}

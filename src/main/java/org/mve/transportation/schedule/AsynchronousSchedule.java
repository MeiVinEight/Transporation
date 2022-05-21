package org.mve.transportation.schedule;

import org.mve.transportation.Transportation;

import java.util.concurrent.ExecutorService;

public class AsynchronousSchedule extends TransportationSchedule
{
	private final ExecutorService service;

	public AsynchronousSchedule(ExecutorService service)
	{
		this.service = service;
	}

	@Override
	public void schedule(TransportationScheduled schedule)
	{
		if (!Transportation.transportation.running())
		{
			throw new IllegalStateException("Transportation is stopped!");
		}
		this.service.execute(new SubscribedAsynchronous(schedule));
	}
}

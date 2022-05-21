package org.mve.transportation.schedule;

import java.util.Queue;

public class SynchronizeSchedule extends TransportationSchedule
{
	private final Queue<TransportationScheduled> queue;

	public SynchronizeSchedule(Queue<TransportationScheduled> queue)
	{
		this.queue = queue;
	}

	@Override
	public void schedule(TransportationScheduled schedule)
	{
		this.queue.add(schedule);
	}
}

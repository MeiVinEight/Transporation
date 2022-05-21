package org.mve.transportation.schedule;

public abstract class TransportationSchedule
{
	public void ensure(ScheduleRunnable runnable)
	{
		this.schedule(new TransportationScheduled(runnable));
	}

	public void ensureLater(ScheduleRunnable runnable, long delay)
	{
		this.schedule(new TransportationScheduled(runnable, delay));
	}

	public void ensureTimer(ScheduleRunnable runnable, long delay, long period)
	{
		this.schedule(new TransportationScheduled(runnable, delay, period));
	}

	public abstract void schedule(TransportationScheduled schedule);
}

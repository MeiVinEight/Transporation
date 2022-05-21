package org.mve.transportation.schedule;

public class SimpleSchedule extends ScheduleRunnable
{
	private final Runnable runnable;

	public SimpleSchedule(Runnable runnable)
	{
		this.runnable = runnable;
	}

	@Override
	public void run()
	{
		this.runnable.run();
	}
}

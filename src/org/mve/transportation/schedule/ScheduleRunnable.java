package org.mve.transportation.schedule;

public abstract class ScheduleRunnable implements Runnable
{
	private boolean canceled = false;

	public void cancel()
	{
		this.canceled = true;
	}

	public boolean canceled()
	{
		return this.canceled;
	}
}

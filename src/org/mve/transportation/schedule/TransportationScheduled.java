package org.mve.transportation.schedule;

import java.util.Arrays;

public class TransportationScheduled
{
	private final ScheduleRunnable runnable;
	private final StackTraceElement[] creat;
	private final boolean timer;
	private long delay;
	private long period;

	public TransportationScheduled(ScheduleRunnable runnable)
	{
		this.runnable = runnable;
		this.creat = TransportationScheduled.frame();
		this.timer = false;
	}

	public TransportationScheduled(ScheduleRunnable runnable, long delay)
	{
		this.runnable = runnable;
		this.creat = TransportationScheduled.frame();
		this.timer = false;
		this.delay = delay;
	}

	public TransportationScheduled(ScheduleRunnable runnable, long delay, long period)
	{
		this.runnable = runnable;
		this.creat = TransportationScheduled.frame();
		this.timer = true;
		this.delay = delay;
		this.period = period <= 0 ? 1 : period;
	}

	public boolean run()
	{
		this.delay--;
		if (this.delay <= 0)
		{
			try
			{
				this.runnable.run();
			}
			catch (Throwable t)
			{
				this.exception(t);
			}
			this.delay = this.period;
			return true;
		}
		return false;
	}

	public boolean timer()
	{
		return this.timer;
	}

	public void cancel()
	{
		this.runnable.cancel();
	}

	public boolean canceled()
	{
		return this.runnable.canceled();
	}

	private static StackTraceElement[] frame()
	{
		StackTraceElement[] elements = new Throwable().getStackTrace();
		return Arrays.copyOfRange(elements, 2, elements.length);
	}

	private void exception(Throwable t)
	{
		System.err.println("[" + Thread.currentThread().getName() + "]");
		t.printStackTrace();
		System.err.println("Schedule created:");
		for (StackTraceElement element : this.creat)
		{
			System.err.println("\tat " + element);
		}
	}
}

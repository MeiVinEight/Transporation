package org.mve.transportation;

import org.mve.invoke.ReflectionFactory;

public class Environment
{
	public static void sleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException ie)
		{
			ReflectionFactory.ACCESSOR.throwException(ie);
		}
	}

	public static void sleep(long millis, int nanos)
	{
		try
		{
			Thread.sleep(millis, nanos);
		}
		catch (InterruptedException ie)
		{
			ReflectionFactory.ACCESSOR.throwException(ie);
		}
	}

	public static void tick(long time)
	{
		if (time > 0)
		{
			int nano = (int) (time % 1_000_000);
			long millis = time / 1_000_000;
			Environment.sleep(millis, nano);
		}
	}
}

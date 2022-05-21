package org.mve.transportation;

import org.mve.invoke.ReflectionFactory;
import org.mve.transportation.network.ConnectionManager;
import org.mve.transportation.schedule.AsynchronousFactory;
import org.mve.transportation.schedule.AsynchronousSchedule;
import org.mve.transportation.schedule.SimpleSchedule;
import org.mve.transportation.schedule.SynchronizeSchedule;
import org.mve.transportation.schedule.TransportationScheduled;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Transportation implements Runnable
{
	public static final Transportation transportation = (Transportation) ReflectionFactory.UNSAFE.allocateInstance(Transportation.class);
	private static int count = 0;
	public final ExecutorService service = Executors.newCachedThreadPool(new AsynchronousFactory(new ThreadGroup(Thread.currentThread().getThreadGroup(), "Transportation"), count++));
	private final Queue<TransportationScheduled> schedule = new ConcurrentLinkedQueue<>();
	public final SynchronizeSchedule synchronize;
	public final AsynchronousSchedule asynchronous;
	public final ConnectionManager connection;
	public final File location;
	public final String name;
	private boolean running = false;
	private Thread primitive;

	public Transportation(File location, String name) throws IOException
	{
		ReflectionFactory.access(Transportation.class, "transportation").set(this);
		this.synchronize = new SynchronizeSchedule(this.schedule);
		this.asynchronous = new AsynchronousSchedule(this.service);
		this.connection = new ConnectionManager();
		this.service.execute(this);
		this.location = location;
		this.name = name;
	}

	public boolean primitive()
	{
		return this.primitive == Thread.currentThread();
	}

	@Override
	public void run()
	{
		this.primitive = Thread.currentThread();
		this.primitive.setName("S" + this.primitive.getName().substring(1));
		this.running = true;
		this.asynchronous.ensure(new SimpleSchedule(this.connection));
		try
		{
			long next = System.nanoTime() + 20_000_000;
			while (this.running)
			{
				LinkedList<TransportationScheduled> list = new LinkedList<>();
				TransportationScheduled s;
				while ((s = this.schedule.poll()) != null)
				{
					if (!s.canceled())
					{
						try
						{
							if (s.run() && s.timer())
							{
								list.add(s);
							}
						}
						catch (Throwable t)
						{
							t.printStackTrace();
						}
					}
				}
				this.schedule.addAll(list);

				Environment.tick(next - System.nanoTime());
				next += 20_000_000;
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			this.stop();
		}
	}

	public boolean running()
	{
		return this.running;
	}

	public void stop()
	{
		this.running = false;
		this.service.shutdown();
		this.connection.close();
	}
}

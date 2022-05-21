package org.mve.transportation.schedule;

import org.mve.text.Hexadecimal;

import java.util.Arrays;
import java.util.concurrent.ThreadFactory;

public class AsynchronousFactory implements ThreadFactory
{
	private final ThreadGroup group;
	private final int i;
	private int c = 0;

	public AsynchronousFactory(ThreadGroup group, int i)
	{
		this.group = group;
		this.i = i;
	}

	@Override
	public Thread newThread(Runnable r)
	{
		Thread t = new Thread(this.group, r);
		t.setName("T-" + new String(Arrays.copyOfRange(Hexadecimal.transform((long)(c++ & 0xFF) | ((i & 0xFF) << 8) | ((t.getId() & 0xFFFF) << 20)), 7, 16)));
		return t;
	}
}

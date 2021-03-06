package org.mve.transportation.network.datapack;

import org.mve.transportation.Transportation;
import org.mve.transportation.network.DatapackInputStream;
import org.mve.transportation.network.DatapackOutputStream;
import org.mve.transportation.network.ProtocolLibrary;
import org.mve.transportation.network.TransportationConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class DatapackPlayingFile extends Datapack
{
	public static final int REQUIRE = 0;
	public static final int RECEIVE = 1;
	public static final int UNKNOWN = 2;
	public static final byte[][] UNIT = {{'B'}, {'K', 'B'}, {'M', 'B'}, {'G', 'B'}, {'T', 'B'}};
	private static final int BUFF = 1024;
	private static final URI ROOT;
	public final int stats;
	public final long length;
	public final String location;

	public DatapackPlayingFile(DatapackInputStream stream) throws IOException
	{
		super(ProtocolLibrary.PLAYING, 0x00);
		this.asynchronous();
		this.stats = stream.readByte();
		this.length = stream.readVarLong();
		this.location = stream.readString();
	}

	public DatapackPlayingFile(int stats, long length, String location)
	{
		super(ProtocolLibrary.PLAYING, 0x00);
		this.asynchronous();
		this.stats = stats;
		this.length = length;
		this.location = location;
	}

	@Override
	public void serialize(DatapackOutputStream stream) throws IOException
	{
		stream.writeByte(this.stats);
		stream.writeVarLong(this.length);
		stream.writeString(this.location);
	}

	@Override
	public void consume(TransportationConnection connection)
	{
		switch (this.stats)
		{
			case DatapackPlayingFile.REQUIRE ->
			{
				try
				{
					File file = this.location.indexOf(':') >= 0 ? new File(this.location) : new File(Transportation.transportation.location, this.location);
					file = file.getAbsoluteFile().getCanonicalFile();
					if (file.exists())
					{
						upload(connection, file.getParentFile(), file);
					}
					else
					{
						connection.send(new DatapackPlayingFile(DatapackPlayingFile.UNKNOWN, 0, file.getCanonicalPath()));
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			case DatapackPlayingFile.RECEIVE ->
			{
				this.download(connection);
			}
			case DatapackPlayingFile.UNKNOWN ->
			{
				System.out.println("File not found: " + this.location);
			}
		}
	}

	private void upload(TransportationConnection connection, File root, File file)
	{
		if (file.isDirectory())
		{
			File[] children = file.listFiles();
			if (children != null)
			{
				for (File child : children)
				{
					this.upload(connection, root, child);
				}
			}
		}
		else
		{
			long length = file.length();
			try (FileInputStream in = new FileInputStream(file))
			{
				String trans = this.relativize(root, file);
				connection.send(new DatapackPlayingFile(DatapackPlayingFile.RECEIVE, length, trans));
				System.out.println("UL " + trans + " " + this.format(length));
				this.transport(in, connection.O, length);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private String relativize(File root, File file) throws IOException
	{
		return (root == null ? DatapackPlayingFile.ROOT : root.toURI()).relativize(file.toURI()).getPath();
	}

	private void download(TransportationConnection connection)
	{
		File file = new File(Transportation.transportation.location, this.location);
		file.getAbsoluteFile().getParentFile().mkdirs();
		try (FileOutputStream out = new FileOutputStream(file))
		{
			System.out.println("DL " + this.location + " " + this.format(this.length));
			this.transport(connection.I, out, this.length);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void transport(InputStream in, OutputStream out, long length) throws IOException
	{
		long total = length;
		long sizePreSec = 0;

		int size;
		byte[] buf = new byte[BUFF];

		long time0 = System.currentTimeMillis();
		long time1 = time0;
		while (total > 0)
		{
			size = in.read(buf, 0, (int) Math.min(total, BUFF));
			out.write(buf, 0, size);

			total -= size;
			sizePreSec += size;
			long time2 = System.currentTimeMillis();
			if (time2 - time1 >= 1000)
			{
				long time = time2 - time1;
				this.progress(length, total, sizePreSec, time);
				time1 += 1000;
				sizePreSec = (sizePreSec * (time - 1000)) / time;
			}
		}
		long time2 = System.currentTimeMillis();
		this.progress(length, total, sizePreSec, time2 - time1);
		long time = time2 - time0;
		System.out.println("OK Time: " + this.time(time) + " Average: " + this.velocity(length, time));

		out.flush();
	}

	private String format(double size)
	{
		int unit = 0;
		while (size >= 1024)
		{
			unit++;
			size /= 1024;
		}
		return String.format("%.2f", size) + new String(UNIT[unit]);
	}

	private void progress(long length, long remaining, long sizePreSec, long time)
	{
		if (sizePreSec > 0)
		{
			long need = (remaining * time) / (sizePreSec);

			double transferred = length - remaining;

			System.out.println(this.time(need) + ' ' + this.velocity(sizePreSec, time) + ' ' + this.format(transferred) + '/' + this.format(length));
		}
	}

	private String time(long millis)
	{
		millis /= 1000;
		int sec = (int) (millis % 60);
		millis /= 60;
		int min = (int) (millis % 60);
		millis /= 60;
		int hou = (int) millis;
		return String.format("%02d:%02d:%02d", hou, min, sec);
	}

	private String velocity(long size, long time)
	{
		time = time > 0 ? time : 1;
		double v = (1000.0 * size) / time;
		return this.format(v) + "/s";
	}

	static
	{
		URI root = null;
		try
		{
			root = new URI("file:/");
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		ROOT = root;
	}
}

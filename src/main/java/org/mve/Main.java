package org.mve;

import org.mve.transportation.Transportation;
import org.mve.transportation.network.TransportationConnection;
import org.mve.transportation.network.datapack.DatapackPlayingFile;
import org.mve.transportation.schedule.SimpleSchedule;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		PrintStream stderr = System.err;
		System.setErr(System.out);

		Transportation transportation = new Transportation(new File("."), args[0]);

		Scanner scanner = new Scanner(System.in);
		String line;
		while ((line = scanner.nextLine()) != null)
		{
			args = line.split(" ");
			switch (args.length)
			{
				case 1 ->
				{
					if ("stop".equals(args[0]))
					{
						transportation.synchronize.ensure(new SimpleSchedule(Transportation.transportation::stop));
						return;
					}
				}
				case 2 ->
				{
					if ("disconnect".equals(args[0]))
					{
						transportation.connection.disconnect(args[1]);
					}
				}
				case 3 ->
				{
					if ("connect".equals(args[0]))
					{
						transportation.connection.connect(args[1], Integer.parseInt(args[2]));
					}
					else if ("require".equals(args[0]))
					{
						TransportationConnection connection = transportation.connection.connection(args[1]);
						connection.send(new DatapackPlayingFile(DatapackPlayingFile.REQUIRE, 0, args[2]));
					}
				}
			}
		}
	}
}

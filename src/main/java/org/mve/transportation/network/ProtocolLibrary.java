package org.mve.transportation.network;

import org.mve.invoke.MethodKind;
import org.mve.invoke.PolymorphismFactory;
import org.mve.transportation.network.datapack.Datapack;
import org.mve.transportation.network.datapack.DatapackHandshaking;
import org.mve.transportation.network.datapack.DatapackPlayingCompress;
import org.mve.transportation.network.datapack.DatapackPlayingFile;

public class ProtocolLibrary
{
	public static final int HANDSHAKING = 0;
	public static final int PLAYING = 1;
	public static final int VERSION = 0;

	private static final DatapackConstructor[][] CONSTRUCTOR =
	{
		new DatapackConstructor[1],
		new DatapackConstructor[1]
	};

	public static Datapack construct(DatapackInputStream stream, int stats, int id)
	{
		return CONSTRUCTOR[stats][id].construct(stream);
	}

	public static DatapackConstructor create(Class<? extends Datapack> clazz)
	{
		return new PolymorphismFactory<>(DatapackConstructor.class)
			.construct(
				clazz,
				new MethodKind("construct", Datapack.class, DatapackInputStream.class),
				new MethodKind("<init>", void.class, DatapackInputStream.class)
			).allocate();
	}

	static
	{
		CONSTRUCTOR[HANDSHAKING][0x00] = create(DatapackHandshaking.class);
		CONSTRUCTOR[PLAYING][0x00] = create(DatapackPlayingFile.class);
		CONSTRUCTOR[PLAYING][0x01] = create(DatapackPlayingCompress.class);
	}
}

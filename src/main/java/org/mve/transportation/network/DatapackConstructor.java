package org.mve.transportation.network;

import org.mve.transportation.network.datapack.Datapack;

public interface DatapackConstructor
{
	public abstract Datapack construct(DatapackInputStream stream);
}

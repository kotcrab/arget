
package pl.kotcrab.arget.comm.kryo;

import java.util.UUID;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class UUIDSerializer extends Serializer<UUID> {
	@Override
	public void write (Kryo kryo, Output output, UUID uuid) {
		output.writeLong(uuid.getMostSignificantBits());
		output.writeLong(uuid.getLeastSignificantBits());
	}

	@Override
	public UUID read (Kryo kryo, Input input, Class<UUID> uuidClass) {
		return new UUID(input.readLong(), input.readLong());
	}
}

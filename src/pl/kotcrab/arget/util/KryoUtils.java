
package pl.kotcrab.arget.util;

import java.util.ArrayList;
import java.util.UUID;

import pl.kotcrab.arget.comm.exchange.DisconnectingNotification;
import pl.kotcrab.arget.comm.exchange.EncryptedExchange;
import pl.kotcrab.arget.comm.exchange.EncryptionModeExchange;
import pl.kotcrab.arget.comm.exchange.PingRequest;
import pl.kotcrab.arget.comm.exchange.PingResponse;
import pl.kotcrab.arget.comm.exchange.RSAPublicKeyExchange;
import pl.kotcrab.arget.comm.exchange.SymmetricKeysExchange;
import pl.kotcrab.arget.comm.exchange.UnsecuredEventExchange;
import pl.kotcrab.arget.comm.exchange.internal.KeyUsedByOtherNotification;
import pl.kotcrab.arget.comm.exchange.internal.KeychainExchange;
import pl.kotcrab.arget.comm.exchange.internal.KeychainRequest;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyExchange;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyVerificationRequest;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyVerificationResponse;
import pl.kotcrab.arget.comm.exchange.internal.ResponseOKNotification;
import pl.kotcrab.arget.comm.exchange.internal.ServerInfoExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionAccepted;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionAlreadyExist;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCipherInitDataExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCipherInitError;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCloseNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCreateRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionData;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionEncryptedExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionInvalidIDNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionInvalidReciever;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRejected;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRemoteAcceptRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRemoteReady;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionTargetKeyNotFound;
import pl.kotcrab.arget.comm.exchange.internal.session.data.MessageExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.data.RemotePanelHideNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.RemotePanelShowNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.TypingFinishedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.TypingStartedNotification;
import pl.kotcrab.arget.comm.kryo.UUIDSerializer;
import pl.kotcrab.arget.global.EncryptionMode;
import pl.kotcrab.crypto.EncryptedData;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoUtils {
	public static void registerNetClasses (Kryo kryo) {
		kryo.register(byte[].class);

		kryo.register(EncryptionModeExchange.class);
		kryo.register(RSAPublicKeyExchange.class);
		kryo.register(SymmetricKeysExchange.class);

		kryo.register(EncryptionMode.class);
		kryo.register(UnsecuredEventExchange.class);
		kryo.register(UnsecuredEventExchange.Type.class);

		kryo.register(PingRequest.class);
		kryo.register(PingResponse.class);

		kryo.register(EncryptedExchange.class);
		kryo.register(EncryptedData.class);

		kryo.register(DisconnectingNotification.class);
	}

	public static void registerInternalNetClasses (Kryo kryo) {
		kryo.setRegistrationRequired(true);
		kryo.register(byte[].class);
		kryo.register(ArrayList.class);
		kryo.register(UUID.class, new UUIDSerializer());

		kryo.register(EncryptedData.class);

		kryo.register(KeychainExchange.class);
		kryo.register(KeychainRequest.class);
		kryo.register(KeyUsedByOtherNotification.class);
		kryo.register(ProfilePublicKeyExchange.class);
		kryo.register(ProfilePublicKeyVerificationRequest.class);
		kryo.register(ProfilePublicKeyVerificationResponse.class);
		kryo.register(ResponseOKNotification.class);
		kryo.register(ServerInfoExchange.class);

		kryo.register(SessionAccepted.class);
		kryo.register(SessionAlreadyExist.class);
		kryo.register(SessionCipherInitDataExchange.class);
		kryo.register(SessionCipherInitError.class);
		kryo.register(SessionCloseNotification.class);
		kryo.register(SessionCreateRequest.class);
		kryo.register(SessionData.class);
		kryo.register(SessionEncryptedExchange.class);
		kryo.register(SessionExchange.class);
		kryo.register(SessionInvalidIDNotification.class);
		kryo.register(SessionInvalidReciever.class);
		kryo.register(SessionRejected.class);
		kryo.register(SessionRemoteAcceptRequest.class);
		kryo.register(SessionRemoteReady.class);
		kryo.register(SessionTargetKeyNotFound.class);
	}

	public static void registerInternalSessionClasses (Kryo kryo) {
		kryo.setRegistrationRequired(true);
		kryo.register(byte[].class);
		kryo.register(UUID.class, new UUIDSerializer());

		kryo.register(EncryptedData.class);
		kryo.register(MessageExchange.class);
		kryo.register(RemotePanelHideNotification.class);
		kryo.register(RemotePanelShowNotification.class);
		kryo.register(TypingFinishedNotification.class);
		kryo.register(TypingStartedNotification.class);
	}

	public static byte[] writeClassAndObjectToByteArray (Kryo kryo, Object object) {
		FastByteArrayOutputStream stream = new FastByteArrayOutputStream();

		Output out = new Output(stream);
		kryo.writeClassAndObject(out, object);
		out.close();

		return ArrayUtils.trim(stream.getByteArray(), 10);
	}

	public static Object readClassAndObjectFromByteArray (Kryo kryo, byte[] data) {
		Input input = new Input(data);
		Object obj = kryo.readClassAndObject(input);
		input.close();

		return obj;
	}
}

/*******************************************************************************
    Copyright 2014 Pawel Pastuszak
 
    This file is part of Arget.

    Arget is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Arget is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Arget.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package pl.kotcrab.arget.util;

import java.util.ArrayList;
import java.util.UUID;

import pl.kotcrab.arget.comm.exchange.DisconnectingNotification;
import pl.kotcrab.arget.comm.exchange.EncryptedTransfer;
import pl.kotcrab.arget.comm.exchange.ServerConfigurationTransfer;
import pl.kotcrab.arget.comm.exchange.PingRequest;
import pl.kotcrab.arget.comm.exchange.PingResponse;
import pl.kotcrab.arget.comm.exchange.RSAPublicKeyTransfer;
import pl.kotcrab.arget.comm.exchange.SymmetricKeysTransfer;
import pl.kotcrab.arget.comm.exchange.UnsecuredEventNotification;
import pl.kotcrab.arget.comm.exchange.internal.KeyUsedByOtherNotification;
import pl.kotcrab.arget.comm.exchange.internal.KeychainRequest;
import pl.kotcrab.arget.comm.exchange.internal.KeychainTransfer;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyTransfer;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyVerificationRequest;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyVerificationResponse;
import pl.kotcrab.arget.comm.exchange.internal.ServerInfoTransfer;
import pl.kotcrab.arget.comm.exchange.internal.TestMsgResponseOKNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionAcceptedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionAlreadyExistNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCipherInitError;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCipherKeysTrsanfer;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCloseNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCreateRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionDoesNotExist;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionEncryptedTransfer;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionInvalidIDNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionInvalidReciever;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRejectedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRemoteAcceptRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRemoteReadyNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionTargetKeyNotFound;
import pl.kotcrab.arget.comm.exchange.internal.session.data.MessageTransfer;
import pl.kotcrab.arget.comm.exchange.internal.session.data.RemotePanelHideNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.RemotePanelShowNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.TypingFinishedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.TypingStartedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileAcceptedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileDataBlockReceivedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileDataBlockTransfer;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferCancelNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferFinishedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferToFileRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferToMemoryRequest;
import pl.kotcrab.arget.comm.kryo.UUIDSerializer;
import pl.kotcrab.arget.server.EncryptionMode;
import pl.kotcrab.crypto.EncryptedData;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoUtils {
	public static void registerNetClasses (Kryo kryo) {
		kryo.register(byte[].class);

		kryo.register(ServerConfigurationTransfer.class);
		kryo.register(RSAPublicKeyTransfer.class);
		kryo.register(SymmetricKeysTransfer.class);

		kryo.register(EncryptionMode.class);
		kryo.register(UnsecuredEventNotification.class);
		kryo.register(UnsecuredEventNotification.Type.class);

		kryo.register(PingRequest.class);
		kryo.register(PingResponse.class);

		kryo.register(EncryptedTransfer.class);
		kryo.register(EncryptedData.class);

		kryo.register(DisconnectingNotification.class);
	}

	public static void registerInternalNetClasses (Kryo kryo) {
		kryo.setRegistrationRequired(true);
		kryo.register(byte[].class);
		kryo.register(ArrayList.class);
		kryo.register(UUID.class, new UUIDSerializer());

		kryo.register(EncryptedData.class);

		kryo.register(KeychainTransfer.class);
		kryo.register(KeychainRequest.class);
		kryo.register(KeyUsedByOtherNotification.class);
		kryo.register(ProfilePublicKeyTransfer.class);
		kryo.register(ProfilePublicKeyVerificationRequest.class);
		kryo.register(ProfilePublicKeyVerificationResponse.class);
		kryo.register(TestMsgResponseOKNotification.class);
		kryo.register(ServerInfoTransfer.class);

		kryo.register(SessionAcceptedNotification.class);
		kryo.register(SessionAlreadyExistNotification.class);
		kryo.register(SessionCipherKeysTrsanfer.class);
		kryo.register(SessionCipherInitError.class);
		kryo.register(SessionCloseNotification.class);
		kryo.register(SessionCreateRequest.class);
		kryo.register(SessionDoesNotExist.class);
		kryo.register(SessionEncryptedTransfer.class);
		kryo.register(SessionExchange.class);
		kryo.register(SessionInvalidIDNotification.class);
		kryo.register(SessionInvalidReciever.class);
		kryo.register(SessionRejectedNotification.class);
		kryo.register(SessionRemoteAcceptRequest.class);
		kryo.register(SessionRemoteReadyNotification.class);
		kryo.register(SessionTargetKeyNotFound.class);
	}

	public static void registerInternalSessionClasses (Kryo kryo) {
		kryo.setRegistrationRequired(true);
		kryo.register(byte[].class);
		kryo.register(UUID.class, new UUIDSerializer());

		kryo.register(EncryptedData.class);
		kryo.register(MessageTransfer.class);
		kryo.register(RemotePanelHideNotification.class);
		kryo.register(RemotePanelShowNotification.class);
		kryo.register(TypingFinishedNotification.class);
		kryo.register(TypingStartedNotification.class);

		kryo.register(FileAcceptedNotification.class);
		kryo.register(FileDataBlockReceivedNotification.class);
		kryo.register(FileDataBlockTransfer.class);
		kryo.register(FileTransferCancelNotification.class);
		kryo.register(FileTransferFinishedNotification.class);
		kryo.register(FileTransferToFileRequest.class);
		kryo.register(FileTransferToMemoryRequest.class);
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

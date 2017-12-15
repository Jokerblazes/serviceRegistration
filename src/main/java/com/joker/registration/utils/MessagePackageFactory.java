package com.joker.registration.utils;

import java.io.IOException;

import org.msgpack.MessagePack;


public final class MessagePackageFactory {
	private final static MessagePack messagePack = new MessagePack();
	public static Object bytesToEntity(byte[] bytes,Class clazz) {
		try {
			return messagePack.read(bytes,clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] entityToBytes(Object object) {
		if (object != null)
			try {
				byte[] bytes  = messagePack.write(object);
				return bytes;
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}
}

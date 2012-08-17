package com.codebits.encryption;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class FileEncryptionDriver {

	public static void main(String[] args) throws GeneralSecurityException, IOException {

		File encryptedKeyFile = new File("encryption/aes.key");
		File publicKeyFile = new File("encryption/public.der");
		File fileToEncrypt = new File("README_VIRTUALBOX.txt");
		File encryptedFile = new File("encryption/my-encrypted-message.txt");
		File privateKeyFile = new File("encryption/private.der");
		File unencryptedFile = new File("encryption/my-decrypted-message.txt");

		FileEncryption secure = new FileEncryption();

		// to encrypt a file
		secure.makeKey();
		secure.saveKey(encryptedKeyFile, publicKeyFile);
		secure.encrypt(fileToEncrypt, encryptedFile);

		// to decrypt it again
		secure.loadKey(encryptedKeyFile, privateKeyFile);
		secure.decrypt(encryptedFile, unencryptedFile);
	}

}

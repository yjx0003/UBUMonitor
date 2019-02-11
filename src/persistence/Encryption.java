package persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





public class Encryption {
	static final Logger logger = LoggerFactory.getLogger(Encryption.class);

	private Encryption() {

	}

	private static Cipher initCipher(String key, int cipherMode) {
		Cipher cipher = null;
		try {
			SecretKey key64 = new SecretKeySpec(key.getBytes(), "Blowfish");

			cipher = Cipher.getInstance("Blowfish");
			cipher.init(cipherMode, key64);
		} catch (InvalidKeyException e) {
			logger.error("Problemas con la clave de encriptacion {}", e);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			logger.error("Problemas con el algoritmo de encriptación usado {}", e);
		}
		return cipher;
	}

	public static Object decrypt(String key, String ruta) {
		Cipher cipher = initCipher(key, Cipher.DECRYPT_MODE);
		logger.info("Intentando descifrar el fichero: {}",ruta);
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(ruta))) {

			SealedObject sealedObject = (SealedObject) inputStream.readObject();
			return sealedObject.getObject(cipher);

		} catch (Exception e) {
			logger.error("No se ha podido abrir el fichero al intentar descifrar {}", e);
	
		}
		return null;
	}

	public static <T extends Serializable> void encrypt(String key, String ruta, T object) {

		logger.info("Intendado encriptar fichero: {}", ruta);

		Cipher cipher = initCipher(key, Cipher.ENCRYPT_MODE);	

		try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(ruta))) {
			SealedObject sealedObject = new SealedObject(object, cipher);
			outputStream.writeObject(sealedObject);
			logger.info("Encriptación correcta en {}", ruta);

		} catch (Exception e) {
			logger.error("Problemas al guardar con los outputStream el objeto {}", e);
		}

	}

}

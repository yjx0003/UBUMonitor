package es.ubu.lsi.persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * Clase de utilidad encargada de encriptar y desencriptar los objetos de los ficheros.
 * @author Yi Peng Ji
 *
 */
public class Serialization {
	private static final Logger LOGGER = LoggerFactory.getLogger(Serialization.class);

	private Serialization() {

	}

	/**
	 * Inicializacion de la clave del cifrado con la contraseña y el modo de cifrado
	 * @param key clave
	 * @param cipherMode modo encriptación o desencriptación
	 * @return
	 */
	private static Cipher initCipher(String key, int cipherMode) {
		Cipher cipher = null;
		try {
			SecretKey key64 = new SecretKeySpec(key.getBytes(), "Blowfish");

			cipher = Cipher.getInstance("Blowfish");
			cipher.init(cipherMode, key64);
		} catch (InvalidKeyException e) {
			LOGGER.error("Problemas con la clave de encriptacion {}", e);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			LOGGER.error("Problemas con el algoritmo de encriptación usado {}", e);
		}
		return cipher;
	}

	/**
	 * Desencripta el fichero con la clave.
	 * @param key contraseña
	 * @param ruta ruta del fichero
	 * @return el objecto deserializado
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException 
	 * @throws ClassNotFoundException
	 * @throws InvalidClassException 
	 */
	public static Object decrypt(String key, String ruta) throws ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, InvalidClassException {
		Cipher cipher = initCipher(key, Cipher.DECRYPT_MODE);
		LOGGER.info("Intentando descifrar el fichero: {}",ruta);
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(ruta))) {

			SealedObject sealedObject = (SealedObject) inputStream.readObject();
			return sealedObject.getObject(cipher);

		} catch (InvalidClassException e) {
			throw e;
		} catch (IOException e) {
			throw new IllegalStateException("No se ha podido abrir el fichero al intentar descifrar",e);
		}
	}

	/**
	 * Encripta un objeto serializable en un fichero.
	 * @param key contraseña
	 * @param ruta ruta del fichero
	 * @param object objeto
	 * @param <T> objeto que extiende serializable
	 * 
	 */
	public static <T extends Serializable> void encrypt(String key, String ruta, T object) {
		LOGGER.info("Intendado encriptar fichero: {}", ruta);

		Cipher cipher = initCipher(key, Cipher.ENCRYPT_MODE);	

		try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(ruta))) {
			SealedObject sealedObject = new SealedObject(object, cipher);
			outputStream.writeObject(sealedObject);
			LOGGER.info("Encriptación correcta en {}", ruta);

		} catch (Exception e) {
			LOGGER.error("Problemas al guardar con los outputStream el objeto {}", e);
		}

	}
	
	public static Object deserialize(String ruta) throws ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, InvalidClassException {
		
		LOGGER.info("Intentando descifrar el fichero: {}",ruta);
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(ruta))) {

			
			return inputStream.readObject();

		} catch (InvalidClassException e) {
			throw e;
		} catch (IOException e) {
			throw new IllegalStateException("No se ha podido abrir el fichero al intentar descifrar",e);
		}
	}
	
	public static <T extends Serializable> void serialize(String ruta, T object) {
		LOGGER.info("Intendado encriptar fichero: {}", ruta);

		

		try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(ruta))) {
			
			outputStream.writeObject(object);
			LOGGER.info("Encriptación correcta en {}", ruta);

		} catch (Exception e) {
			LOGGER.error("Problemas al guardar con los outputStream el objeto {}", e);
		}

	}
	
}

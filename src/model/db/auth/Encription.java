package model.db.auth;

import java.security.MessageDigest;
import org.w3c.tools.codec.Base64Encoder;

/**
 * <h1>Servicio de cifrado de datos</h1> 
 * 
 * Este servicio se utiliza para cifrar
 * las contraseñas de los usuarios en la base de datos.
 */
public class Encription {

	/**
	 * Devuelve el hash SHA-512 de una cadena de caracteres y 
	 * luego lo codifica en Base64.
	 * 
	 * @param chain Cadena de caracteres.
	 * @return Cadena cifrada.
	 */
	public static String getSHA512(String chain) {
		MessageDigest digest;
		String value = "";
		try {
			digest = MessageDigest.getInstance("SHA-512");
			digest.update(chain.getBytes());
			byte[] keys = digest.digest();
			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < keys.length; i++)
				sb.append((char) keys[i]);

			value = sb.toString();
			Base64Encoder b64e = new Base64Encoder(value);
			value = b64e.processString();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return value;
	}
}

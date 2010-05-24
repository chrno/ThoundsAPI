package org.thounds.thoundsapi.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.codec.binary.Base64;

/**
 * 
 *
 */
public class Base64Encoder {

	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] barr = new byte[1024];
		while (true) {
			int r = in.read(barr);
			if (r <= 0) {
				break;
			}
			out.write(barr, 0, r);
		}
	}

	private static byte[] loadFile(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			copy(in, buffer);
			return buffer.toByteArray();
		} finally {
			in.close();
		}
	}

	/**
	 * 
	 * 
	 * @param path file path
	 * @return 
	 * @throws IOException
	 */
	public static String Encode(String path) throws IOException{
		File file = new File(path);
		byte[] bytes = loadFile(file);
		byte[] encoded = Base64.encodeBase64(bytes);
		return new String(encoded, "ASCII");
	}

}

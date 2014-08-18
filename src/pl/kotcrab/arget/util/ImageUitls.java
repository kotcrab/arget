
package pl.kotcrab.arget.util;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

public class ImageUitls {
	public static BufferedImage read (File file) {
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static BufferedImage read (byte[] data) {
		try {
			InputStream in = new ByteArrayInputStream(data);
			return ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void write (RenderedImage image, File output) {
		try {
			ImageIO.write(image, FilenameUtils.getExtension(output.getAbsolutePath()), output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

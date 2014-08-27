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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import pl.kotcrab.arget.Log;

public class ImageUitls {
	public static BufferedImage read (File file) {
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			Log.exception(e);
		}

		return null;
	}

	public static BufferedImage read (byte[] data) {
		try {
			InputStream in = new ByteArrayInputStream(data);
			return ImageIO.read(in);
		} catch (IOException e) {
			Log.exception(e);
		}

		return null;
	}

	public static void write (RenderedImage image, File output) {
		try {
			ImageIO.write(image, FilenameUtils.getExtension(output.getAbsolutePath()), output);
		} catch (IOException e) {
			Log.exception(e);
		}
	}
}

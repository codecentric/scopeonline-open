package de.codecentric.scopeonline.charts;

import com.vaadin.server.StreamResource.StreamSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FlowchartStreamSource implements StreamSource {
	BufferedImage image;

	public FlowchartStreamSource( BufferedImage image ) {
		this.image = image;
	}

	@Override
	public InputStream getStream() {
		try {
			ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();
			ImageIO.write( image, "png", imageBuffer );
			return new ByteArrayInputStream(
					imageBuffer.toByteArray() );
		} catch( IOException e ) {
			e.printStackTrace();
		}
		return null;
	}

}

package es.ubu.lsi.ubumonitor.view.chart;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.UtilMethods;

public abstract class WordCloudChart extends Chart {

	protected String base64Image;

	public WordCloudChart(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exportImage(File file) throws IOException {

		byte[] imgdata = DatatypeConverter.parseBase64Binary(base64Image);
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgdata));

		ImageIO.write(bufferedImage, "png", file);

		UtilMethods.showExportedFile(file);

	}

}

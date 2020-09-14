package es.ubu.lsi.ubumonitor.view.chart;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.UtilMethods;

public abstract class WordCloud extends Chart{

	public WordCloud(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideLegend() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportImage(File file) throws IOException {
		String str = (String) webViewChartsEngine.executeScript("exportCanvas('wordCloud')");
		byte[] imgdata = DatatypeConverter.parseBase64Binary(str.substring(str.indexOf(',') + 1));
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgdata));

		ImageIO.write(bufferedImage, "png", file);
		
		UtilMethods.showExportedFile(file);
		
	}

}

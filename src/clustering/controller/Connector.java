package es.ubu.lsi.ubumonitor.clustering.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import javafx.scene.web.WebEngine;

public class Connector {

	private ClusteringController clusteringController;
	private WebEngine webEngine;

	private List<ClusterWrapper> clusters;

	public Connector(ClusteringController controller) {
		clusteringController = controller;
		webEngine = controller.getWebEngine();
	}

	public void selectUser(int clusterIndex, int index) {
		UserData userData = clusters.get(clusterIndex).get(index);
		clusteringController.getTableView().getSelectionModel().select(userData);
		clusteringController.getTableView().scrollTo(userData);
	}

	public void setClusters(List<ClusterWrapper> clusters) {
		this.clusters = clusters;
	}
	public void export(File file) throws IOException {
		String str = (String) webEngine.executeScript("exportGraphic()");
		byte[] imgdata = DatatypeConverter.parseBase64Binary(str.substring(str.indexOf(',') + 1));
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgdata));
		ImageIO.write(bufferedImage, "png", file);
	}

}

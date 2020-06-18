package es.ubu.lsi.ubumonitor.clustering.algorithm;

import java.util.List;
import java.util.stream.Collectors;

import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import smile.clustering.linkage.CompleteLinkage;
import smile.plot.swing.Canvas;
import smile.plot.swing.Dendrogram;

public class HierarchicalClustering {

	public Image execute(List<EnrolledUser> enrolledUsers, List<DataCollector> dataCollectors) {
		List<UserData> usersData = enrolledUsers.stream().map(UserData::new).collect(Collectors.toList());
		dataCollectors.forEach(collector -> collector.collect(usersData));
		double[][] data = usersData.stream().map(UserData::getPoint).toArray(double[][]::new);
		smile.clustering.HierarchicalClustering hc = smile.clustering.HierarchicalClustering
				.fit(CompleteLinkage.of(data));
		Canvas canvas = new Dendrogram(hc.getTree(), hc.getHeight()).canvas();
		canvas.setMargin(0.05);
		return SwingFXUtils.toFXImage(canvas.toBufferedImage(1920, 1080), null);
	}

}

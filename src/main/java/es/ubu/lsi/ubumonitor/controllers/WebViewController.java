package es.ubu.lsi.ubumonitor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;

public class WebViewController {
	
	
	@FXML
	private ProgressBar progressBar;
	@FXML
	private WebView webViewCharts;
	
	@FXML
	private ImageView imageView;
	
	
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public WebView getWebViewCharts() {
		return webViewCharts;
	}
	
	public ImageView getImageView() {
		return imageView;
	}
	
	
	
	
}

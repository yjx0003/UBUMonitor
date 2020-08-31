package es.ubu.lsi.ubumonitor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.web.WebView;

public class WebViewController {
	
	
	@FXML
	private ProgressBar progressBar;
	@FXML
	private WebView webViewCharts;
	
	
	public ProgressBar getProgressBar() {
		return progressBar;
	}
	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	public WebView getWebViewCharts() {
		return webViewCharts;
	}
	public void setWebViewCharts(WebView webViewCharts) {
		this.webViewCharts = webViewCharts;
	}
	
	
	
}

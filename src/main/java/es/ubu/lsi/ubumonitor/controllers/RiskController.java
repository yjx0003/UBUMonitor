package es.ubu.lsi.ubumonitor.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.util.FileUtil;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.RiskJavaConnector;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class RiskController implements MainAction {
	@FXML
	private ProgressBar progressBar;
	@FXML
	private WebView webViewCharts;
	private WebEngine webViewChartsEngine;
	private MainController mainController;
	private Controller controller = Controller.getInstance();

	private RiskJavaConnector javaConnector;

	public void init(MainController mainController) {
		this.mainController = mainController;
		initTabPaneWebView();
		initContextMenu();
		
		

	}

	private void initTabPaneWebView() {

	

		// Cargamos el html de los graficos y calificaciones
		webViewCharts.setContextMenuEnabled(false); // Desactiva el click derecho
		webViewChartsEngine = webViewCharts.getEngine();
		javaConnector = new RiskJavaConnector(this);
		progressBar.progressProperty()
				.bind(webViewChartsEngine.getLoadWorker()
						.progressProperty());

		// Comprobamos cuando se carga la pagina para traducirla
		webViewChartsEngine.getLoadWorker()
				.stateProperty()
				.addListener((ov, oldState, newState) -> {
					if (Worker.State.SUCCEEDED != newState)
						return;
					
					progressBar.setVisible(false);
					JSObject window = (JSObject) webViewChartsEngine.executeScript("window");
					window.setMember("javaConnector", javaConnector);
					System.out.println(webViewChartsEngine.executeScript("document.documentElement.innerHTML"));
					webViewCharts.toFront();
					javaConnector.setDefaultValues();

					javaConnector.updateChart();
				
				});
		webViewChartsEngine.load(getClass().getResource("/graphics/RiskCharts.html")
				.toExternalForm());
		

	}

	private void initContextMenu() {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setAutoHide(true);

		MenuItem exportCSV = new MenuItem(I18n.get("text.exportcsv"));
		exportCSV.setOnAction(e -> exportCSV());

		MenuItem exportPNG = new MenuItem(I18n.get("text.exportpng"));
		exportPNG.setOnAction(e -> save());

		contextMenu.getItems()
				.addAll(exportPNG, exportCSV);
		webViewCharts.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				contextMenu.show(webViewCharts, e.getScreenX(), e.getScreenY());
			} else {
				contextMenu.hide();
			}
		});

	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public WebView getWebViewCharts() {
		return webViewCharts;
	}

	public WebEngine getWebViewChartsEngine() {
		return webViewChartsEngine;
	}

	public RiskJavaConnector getJavaConnector() {
		return javaConnector;
	}

	@Override
	public void onWebViewTabChange() {

		javaConnector.updateTabImages();
		javaConnector.updateChart();

	}

	@Override
	public void updateTreeViewGradeItem() {
		// do nothing

	}

	@Override
	public void updateListViewEnrolledUser() {
		javaConnector.updateChart();

	}

	@Override
	public void updatePredicadeEnrolledList() {
		javaConnector.updateChart();
	}

	@Override
	public void updateListViewActivity() {
		// do nothing

	}

	@Override
	public void onSetTabLogs() {
		// do nothing

	}

	@Override
	public void onSetTabGrades() {
		// do nothing

	}

	@Override
	public void onSetTabActivityCompletion() {
		// do nothing

	}

	@Override
	public void onSetSubTabLogs() {
		// do nothing

	}

	@Override
	public void updateListViewComponents() {
		// do nothing

	}

	@Override
	public void updateListViewEvents() {
		// do nothing

	}

	@Override
	public void updateListViewSection() {
		// do nothing

	}

	@Override
	public void updateListViewCourseModule() {
		// do nothing

	}

	@Override
	public void save() {

		UtilMethods.fileAction(String.format("%s_%s_%s", controller.getActualCourse()
				.getId(),
				LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss")),
				javaConnector.getCurrentType()
						.getChartType()),
				ConfigHelper.getProperty("imageFolderPath", "./"), controller.getStage(), FileUtil.FileChooserType.SAVE,
				file -> {
					if (file != null) {
						String str = javaConnector.export(file);
						if (str == null)
							return;
						javaConnector.saveImage(str);
						ConfigHelper.setProperty("imageFolderPath", file.getParent());
					
					}
				}, false, FileUtil.PNG);

	}

	public void exportCSV() {
		UtilMethods.fileAction(String.format("%s_%s_%s", controller.getActualCourse()
				.getId(),
				LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss")),
				javaConnector.getCurrentType()
						.getChartType()),
				ConfigHelper.getProperty("csvFolderPath", "./"), controller.getStage(), FileUtil.FileChooserType.SAVE,
				file -> {
					javaConnector.getCurrentType()
							.exportCSV(file.getAbsolutePath());
					ConfigHelper.setProperty("csvFolderPath", file.getParent());
				},  FileUtil.CSV);

	}

	@Override
	public void applyConfiguration() {
		javaConnector.updateChart();

	}

	public MainController getMainController() {
		return mainController;
	}

}

package es.ubu.lsi.ubumonitor.util;

public enum MaskImage {

	RECTANGLE(null, 0, 0), 
	WHALE("/img/masks/whale.png", 990, 618), 
	CAT("/img/masks/cat.bmp", 600, 600),
	CLOUD("/img/masks/cloud_fg.bmp", 600, 386),
	EARTH("/img/masks/earth.bmp", 600, 600),
	BOWL("/img/masks/pho_full.bmp", 500, 488);

	private int width;

	private int height;
	private String path;

	private MaskImage(String path, int width, int height) {
		this.width = width;
		this.height = height;
		this.path = path;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 
	 * @return the height;
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 
	 * @return the image path
	 */
	public String getPath() {
		return path;
	}

}

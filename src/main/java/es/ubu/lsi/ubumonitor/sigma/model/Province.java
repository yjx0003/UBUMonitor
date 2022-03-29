package es.ubu.lsi.ubumonitor.sigma.model;

import java.util.HashMap;
import java.util.Map;

public enum Province {
	
	ALMERIA("04", "Almería", AutonomousCommunity.ANDALUSIA),
	CADIZ("11", "Cádiz", AutonomousCommunity.ANDALUSIA),
	CORDOBA("14", "Córdoba", AutonomousCommunity.ANDALUSIA),
	GRANADA("18", "Granada", AutonomousCommunity.ANDALUSIA),
	HUELVA("21", "Huelva", AutonomousCommunity.ANDALUSIA),
	JAEN("23", "Jaén", AutonomousCommunity.ANDALUSIA),
	MALAGA("29", "Málaga", AutonomousCommunity.ANDALUSIA),
	SEVILLA("41", "Sevilla", AutonomousCommunity.ANDALUSIA),
	HUESCA("22", "Huesca", AutonomousCommunity.ARAGON),
	TERUEL("44", "Teruel", AutonomousCommunity.ARAGON),
	ZARAGOZA("50", "Zaragoza", AutonomousCommunity.ARAGON),
	ASTURIAS("33", "Asturias", AutonomousCommunity.ASTURIAS),
	BALEARES("07", "Baleares", AutonomousCommunity.BALEARIC_ISLANDS),
	ALAVA("01", "Álava", AutonomousCommunity.BASQUE_COUNTRY),
	GUIPUZCOA("20", "Guipúzcoa,", AutonomousCommunity.BASQUE_COUNTRY),
	VIZCAYA("48", "Vizcaya",  AutonomousCommunity.BASQUE_COUNTRY),
	LAS_PALMAS("35", "Las Palmas", AutonomousCommunity.CANARY_ISLANDS),
	SANTA_CRUZ_DE_TENERIFE("38", "Santa Cruz de Tenerife", AutonomousCommunity.CANARY_ISLANDS),
	CANTABRIA("39", "Cantabria", AutonomousCommunity.CANTABRIA),
	AVILA("05","Ávila", AutonomousCommunity.CASTILE_AND_LEON),
	BURGOS("09", "Burgos", AutonomousCommunity.CASTILE_AND_LEON),
	LEON("24", "León", AutonomousCommunity.CASTILE_AND_LEON),
	PALENCIA("34", "Palencia", AutonomousCommunity.CASTILE_AND_LEON),
	SALAMANCA("37", "Salamanca", AutonomousCommunity.CASTILE_AND_LEON),
	SEGOVIA("40", "Segovia", AutonomousCommunity.CASTILE_AND_LEON),
	SORIA("42", "Soria", AutonomousCommunity.CASTILE_AND_LEON),
	VALLADOLID("47", "Valladolid", AutonomousCommunity.CASTILE_AND_LEON),
	ZAMORA("49", "Zamora", AutonomousCommunity.CASTILE_AND_LEON),
	ALBACETE("02", "Albacete", AutonomousCommunity.CASTILLA_LA_MANCHA),
	CIUDAD_REAL("13", "Ciudad Real", AutonomousCommunity.CASTILLA_LA_MANCHA),
	CUENCA("16", "Cuenca", AutonomousCommunity.CASTILLA_LA_MANCHA),
	GUADALAJARA("19", "Guadalajara", AutonomousCommunity.CASTILLA_LA_MANCHA),
	TOLEDO("45", "Toledo", AutonomousCommunity.CASTILLA_LA_MANCHA),
	BARCELONA("08", "Barcelona", AutonomousCommunity.CATALONIA),
	GERONA("17", "Gerona", AutonomousCommunity.CATALONIA),
	LERIDA("25", "Lérida", AutonomousCommunity.CATALONIA),
	TARRAGONA("43", "Tarragona", AutonomousCommunity.CATALONIA),
	MADRID("28", "Madrid", AutonomousCommunity.COMMUNITY_OF_MADRID),
	BADAJOZ("06", "Badajoz", AutonomousCommunity.EXTREMADURA),
	CACERES("10", "Cáceres", AutonomousCommunity.EXTREMADURA),
	LA_CORUNA("15", "La Coruña", AutonomousCommunity.GALICIA),
	LUGO("27", "Lugo", AutonomousCommunity.GALICIA),
	ORENSE("32", "Orense", AutonomousCommunity.GALICIA),
	PONTEVEDRA("36", "Pontevedra", AutonomousCommunity.GALICIA),
	LA_RIOJA("26", "La Rioja", AutonomousCommunity.LA_RIOJA),
	NAVARRA("31", "Navarra", AutonomousCommunity.NAVARRE),
	MURCIA("30", "Murcia", AutonomousCommunity.REGION_OF_MURCIA),
	ALICANTE("03", "Alicante", AutonomousCommunity.VALENCIAN_COMMUNITY),
	CASTELLON("12", "Castellón", AutonomousCommunity.VALENCIAN_COMMUNITY),
	VALENCIA("46", "Valencia", AutonomousCommunity.VALENCIAN_COMMUNITY),
	CEUTA("51", "Ceuta", AutonomousCommunity.CEUTA),
	MELILLA("52", "Melilla", AutonomousCommunity.MELILLA),
	UNKNOWN("-1", "Unknown", AutonomousCommunity.UNKNOWN);
	
	private String zipCode;
	private String name;
	private AutonomousCommunity autonomousCommunity;
	private static final Map<String, Province> MAP_ZIP_CODE = new HashMap<>();
	static {
		for(Province province: Province.values()) {
			MAP_ZIP_CODE.put(province.zipCode, province);
		}
	}
	
	private Province(String zipCode, String name, AutonomousCommunity autonomousCommunity) {
		this.zipCode = zipCode;
		this.name = name;
		this.autonomousCommunity = autonomousCommunity;
	}
	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the autonomousCommunity
	 */
	public AutonomousCommunity getAutonomousCommunity() {
		return autonomousCommunity;
	}

	public static Province getProvinceByZipCode(String zipCode) {
		return MAP_ZIP_CODE.getOrDefault(zipCode, UNKNOWN);
	}
	
	
	public static Map<String, Province> getMapZipCode() {
		return MAP_ZIP_CODE;
	}
	
}

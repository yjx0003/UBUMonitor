//------------------------------------------Traducción-----------------------------------------------------------------
// Contiene el texto a mostrar para los distintos elementos e idiomas
var messages = {
	btnLineas: {
		es: "Lineas",
		en: "Lines"
	},
	btnRadar: {
		es: "Radar",
		en: "Radar"
	},
	btnBoxPlot: {
		es: "BoxPlot General",
		en: "General BoxPlot"
	},
	btnBoxPlotGroup: {
		es: "BoxPlot Del Grupo",
		en: "Group BoxPlot"
	},
	btnCalificaciones: {
		es: "Calificaciones",
		en: "Grades"
	},
	btnLegend: {
		es: "Leyenda",
		en: "Legend"
	},
	btnMean: {
		es: "Media General",
		en: "General Mean"
	},
	btnGroupMean: {
		es: "Media Del Grupo",
		en: "Group Mean"
	},
	noradarText: {
		es: "Selecciona al menos 3 actividades para mostrar el gráfico",
		en: "Select at least 3 activities to show the chart"
	},
	noGroupText: {
		es: "Selecciona un grupo para mostrar el gráfico",
		en: "Select a group to show the chart"
	}
}
// Establece el idioma
function setLanguage(lang) {
	language = lang;
	get_i18n('btnLineas', lang);
	get_i18n('btnRadar', lang);
	get_i18n('btnBoxPlot', lang);
	get_i18n('btnBoxPlotGroup', lang);
	get_i18n('btnCalificaciones', lang);
	get_i18n('btnLegend', lang);
	get_i18n('btnMean', lang);
	get_i18n('btnGroupMean', lang);
	document.getElementById('noradarText').innerHTML = messages['noradarText'][lang];
	document.getElementById('noGroupText').innerHTML = messages['noGroupText'][lang];
	
}
// Modifica el valor de los inputs segun el idioma pasado
function get_i18n(item, lang) {
	document.getElementById(item).value = messages[item][lang];
}
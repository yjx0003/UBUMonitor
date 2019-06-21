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
	btnStackedBar:{
		es:"Barras Apiladas",
		en:"Stacked Bar"
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

//Modifica el valor de los inputs segun el idioma pasado
function getI18n(item, lang) {
	document.getElementById(item).value = messages[item][lang];
}

// Establece el idioma
function setLanguage(lang) {
	getI18n("btnLineas", lang);
	getI18n("btnRadar", lang);
	getI18n("btnBoxPlot", lang);
	getI18n("btnBoxPlotGroup", lang);
	getI18n("btnCalificaciones", lang);
	getI18n("btnLegend", lang);
	getI18n("btnMean", lang);
	getI18n("btnGroupMean", lang);
	document.getElementById("noradarText").innerHTML = messages["noradarText"][lang];
	document.getElementById("noGroupText").innerHTML = messages["noGroupText"][lang];
	
}

//------------------------------------------Traducción-----------------------------------------------------------------
// Contiene el texto a mostrar para los distintos elementos e idiomas
var messages = {
	btnLineas : {
		en: "Lines",
		es: "Líneas",
		ca: "Línies",
		de: "Linien",
		fr: "Lignes",
		it: "Linee",
		pt: "Lines",
	},
	btnRadar : {
		en: "Radar",
		es: "Radar",
		ca: "Radar",
		de: "Radar",
		fr: "Radar",
		it: "Radar",
		pt: "Radar",
	},
	btnBoxPlot : {
		en: "General BoxPlot",
		es: "BoxPlot General",
		ca: "General Diagramacaja",
		de: "Allgemein Boxplot",
		fr: "Général Boxplot",
		it: "Generale Boxplot",
		pt: "Geral Boxplot",
	},
	btnBoxPlotGroup : {
		en: "Group BoxPlot",
		es: "BoxPlot Del Grupo",
		ca: "Grup Boxplot",
		de: "Gruppe Boxplot",
		fr: "Groupe Boxplot",
		it: "Gruppo Boxplot",
		pt: "Grupo Boxplot",
	},
	btnCalificaciones : {
		en: "Qualification",
		es: "Calificación",
		ca: "Qualificació",
		de: "Qualifikation",
		fr: "Qualification",
		it: "Qualificazione",
		pt: "Qualificação",
	},
	btnStackedBar : {
		en: "Stacked Bar",
		es: "De Barras Apiladas",
		ca: "De Barres Apilades",
		de: "Stacked Bar",
		fr: "Bar Empilée",
		it: "A Barre In Pila",
		pt: "Bar Empilhados",

	},
	btnLegend : {
		en: "Legend",
		es: "Leyenda",
		ca: "Llegenda",
		de: "Legende",
		fr: "Légende",
		it: "Leggenda",
		pt: "Lenda",
	},
	btnMean : {
		en: "General Mean",
		es: "Media General",
		ca: "Mitjana General",
		de: "Allgemein Mittlere",
		fr: "General Mean",
		it: "Generale Media",
		pt: "Média Geral",
	},
	btnGroupMean : {
		en: "Group Mean",
		es: "Grupo Media",
		ca: "Grup Mitjana",
		de: "Gruppe Mittlere",
		fr: "Groupe Moyen",
		it: "Gruppo Medio",
		pt: "Grupo Média",
	},
	noradarText : {
		en: "Select at least 3 activities to show the chart",
		es: "Seleccionar al menos 3 actividades para mostrar el gráfico",
		ca: "Seleccionar almenys 3 activitats per mostrar el gràfic",
		de: "Wählen Sie mindestens 3 Aktivitäten das Diagramm zeigen",
		fr: "Sélectionnez au moins 3 activités pour montrer le tableau",
		it: "Selezionare almeno 3 attività per mostrare il grafico",
		pt: "Selecione pelo menos 3 atividades para mostrar o gráfico",
	},
	noGroupText : {
		en: "Select a group to show the chart",
		es: "Seleccionar un grupo para mostrar el gráfico",
		ca: "Seleccionar un grup per mostrar el gràfic",
		de: "Wählen Sie eine Gruppe um das Diagramm zu zeigen,",
		fr: "Sélectionnez un groupe pour afficher le tableau",
		it: "Selezionare un gruppo per visualizzare il grafico",
		pt: "Selecione um grupo para mostrar o gráfico",
	}
};

// Modifica el valor de los inputs segun el idioma pasado
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
	getI18n("btnStackedBar", lang);
	document.getElementById("noradarText").innerHTML = messages["noradarText"][lang];
	document.getElementById("noGroupText").innerHTML = messages["noGroupText"][lang];

}



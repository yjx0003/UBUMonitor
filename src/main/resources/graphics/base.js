
    //------------------------------------------Funciones------------------------------------------------------------------


    function imageButton(id, value) {
        if (value) {
            document.getElementById(id).style.backgroundImage = 'url(./../img/visibility.png)';

        } else {
            document.getElementById(id).style.backgroundImage = 'url(./../img/visibility_off.png)';
        }
    }

    function displayLegendButton(value) {
        document.getElementById('btnLegend').style.display = value ? "inline" : "none";
    }

    function displayGroupButton(value) {
        document.getElementById('btnGroupMean').style.display = value ? "inline" : "none";
    }

    function displayGeneralButton(value) {
        document.getElementById('btnMean').style.display = value ? "inline" : "none";
    }

    function displayApexChart(value) {
        document.getElementById('apexchartsDiv').style.display = value ? "block" : "none";
    }
    function displayTabulator(value) {
        document.getElementById('tabulatorDiv').style.display = value ? "block" : "none";
    }


    function displayChartjs(value) {
        document.getElementById('chart-container').style.display = value ? "block" : "none";
    }



    function displayChartsButtons(div_id, type, listCharts) {
   
        let tabs = document.getElementById(div_id);
        if(tabs){
	        let children = tabs.children;
	        for (let i = 0; i < children.length; i++) {
	            if (children[i].tagName == "BUTTON") {
	                children[i].style.display = div_id == type && listCharts.includes(children[i].id)  ? "inline" : "none";
	            }
	
	        }
        }
    }

    function disableTab(div_id, noDisable) {
		let tabs = document.getElementById(div_id);
        if (document.getElementById(noDisable).disabled == false && tabs) {
            
            let children = tabs.children;
            for (i = 0; i < children.length; i++) {
                if (children[i].tagName == "BUTTON") {
                    children[i].disabled = false;
                }

            }
            document.getElementById(noDisable).disabled = true;
        }


    }


    function updateApexCharts(series, categories, options) {
        displayApexChart(true);
        displayChartjs(false);
        displayTabulator(false);
		
		manageButtons(options.tab, options.listCharts);
        disableTab(options.tab, options.button);

        displayLegendButton(options.useLegend);
        displayGroupButton(options.useGroup);
        displayGeneralButton(options.useGeneral);

        options.series = series;
        options.xaxis.categories = categories;
        options.legend.show = options.useLegend && options.legendActive;
        if (myApexChart.w.config.chart.type == options.typeGraph) {

            myApexChart.updateOptions(options);
        } else {

            myApexChart.destroy();
            myApexChart = new ApexCharts(document.getElementById('apexchartsDiv'), options);
            myApexChart.render();
        }
    }

    function updateChartjs(dataset, options) {
        displayApexChart(false);
        displayChartjs(true);
        displayTabulator(false);
        imageButton("btnLegend", options.legendActive);
        imageButton("btnMean", options.generalActive);
        imageButton("btnGroupMean", options.groupActive);

		manageButtons(options.tab, options.listCharts);
        disableTab(options.tab, options.button);

        displayLegendButton(options.useLegend);
        displayGroupButton(options.useGroup);
        displayGeneralButton(options.useGeneral);
		if(!options.legend){
			options.legend={};
		}
		options.legend.display = options.legendActive;
        if (myChart.config.type == options.typeGraph) {
            myChart.data = dataset;
            myChart.options = options;
            myChart.update();        
            
            
        } else {
            myChart.destroy();
            myChart = new Chart(ctx, {
                type: options.typeGraph,
                data: dataset,
                options: options
            });
        }

    }

    function updateTabulator(data, options) {

        displayApexChart(false);
        displayChartjs(false);
        displayTabulator(true);
        disableTab(options.tab, options.button);
        displayLegendButton(options.useLegend);
        displayGroupButton(options.useGroup);
        displayGeneralButton(options.useGeneral);
        manageButtons(options.tab, options.listCharts);
        disableTab(options.tab, options.button);
        table.destroy()
        table = new Tabulator("#tabulatorDiv", options);
        table.setColumns(data.columns);
        table.setData(data.tabledata).then(function () {
        	
            table.redraw();
        })


    }



    function clearChartjs() {
        myChart.destroy();
        myChart = new Chart(ctx, {
            type: "bar",
            data: {
                labels: [],
                datasets: []
            },
            options: {
                type: "line",
                data: {
                    labels: [],
                    datasets: []
                },
                options: {
                    tab: "grade_tabs",
                    button: "btnLine",
                    typeGraph: "line"
                }
            }
        });
    }
    
    function clearTabulator(){
    	table.clearData();
    }


    function manageButtons(type, listCharts) {


        displayChartsButtons("GRADES", type, listCharts);
        displayChartsButtons("LOGS", type, listCharts);
        displayChartsButtons("ACTIVITY_COMPLETION", type, listCharts);

    }

    //Funcion que muestra u oculta la leyenda del gráfico
    function hideLegend() {
        imageButton("btnLegend", javaConnector.swapLegend());
        
        javaConnector.updateChartFromJS();
    }

    function showGeneralMean() {
   
        imageButton("btnMean",  javaConnector.swapGeneral());
       
        javaConnector.updateChartFromJS();
    }

    function showGroupMean() {
     
        imageButton("btnGroupMean",javaConnector.swapGroup());
        
        javaConnector.updateChartFromJS();
    }

 


    


    function exportChartjs() {
        return myChart.toBase64Image();
    }

    function exportApexcharts() {

        myApexChart.dataURI().then(function (uri) {
            javaConnector.saveImage(uri);
        }).catch(function (error) {
            javaConnector.showErrorWindow("Cannot download image:" + error.message);
        })

    }
    function genericExport(id) {
        html2canvas(document.getElementById(id), { logging: false }).then(function (canvas) {
            javaConnector.saveImage(canvas.toDataURL());
        }).catch(function (error) {
            javaConnector.showErrorWindow("Cannot download image:" + error.message);
        })

    }
    
    function generateButtons(buttons){
    	for (const value of buttons){
			let button = document.createElement("BUTTON");
			button.id = value.id;
			button.innerText = value.text;
			button.className = "btn";
			//button.type = "button";
			button.setAttribute( "onClick", "javascript: javaConnector.updateCharts(this.id);" );
			
			type = document.getElementById(value.type);
			type.appendChild(button);
    	}
    }

    // Modifica el valor de los inputs segun el idioma pasado
    function getI18n(item) {
        document.getElementById(item).value = javaConnector.getI18n(item);
    }

    function translate(id, text){
    	document.getElementById(id).innerText = text;
    }



    function setLocale(newLocale) {
        locale = newLocale;
        moment.locale(newLocale);
    }
    function colorRGB(color) {
        let colors = colorHash.rgb(color);
        return "rgb(" + colors[0] + "," + colors[1] + "," + colors[2] + ")";
    }

    function colorRGBA(color, alpha) {
        let colors = colorHash.rgb(color);
        return "rgba(" + colors[0] + "," + colors[1] + "," + colors[2] + "," + alpha + ")";
    }
    //Funcion que devuelve un color en formato hexadecimal
    //en base a el string que se le pasa
    function colorHEX(color) {
        return colorHash.hex(color);
    }
     function formatBoxplot(text, value){
    	return text+ ": " + value.toLocaleString(locale,{maximumFractionDigits:2});
    }
    
    function boxplotLabel(t, a, o, e) {
	    if (-1 == e) return [formatBoxplot("n", a.datasets[t.datasetIndex].data[t.index].length), formatBoxplot("min ", o.min), formatBoxplot("whiskerMin", o.whiskerMin), formatBoxplot("q1", o.q1), formatBoxplot("median", o.median), formatBoxplot("q3", o.q3), formatBoxplot("whiskerMax", o.whiskerMax), formatBoxplot("max", o.max)];
	    var r, s = a.datasets[t.datasetIndex].users[0] instanceof Array ? a.datasets[t.datasetIndex].users[t.index] : a.datasets[t.datasetIndex].users,
	        n = a.datasets[t.datasetIndex].data[t.index],
	        x = [];
	    for (r = 0; r < n.length; r++) n[r] == o.outliers[e] && x.push(s[r] + " (" + n[r].toLocaleString(locale,{maximumFractionDigits:2}) + ")");
	    return x
	}
    
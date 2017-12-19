if (!document.rootContextUrl) {
	document.rootContextUrl = "";
}

var loadFiles = [
	"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js",
	"services/logging/lib/momentjs.js",
	"services/logging/lib/highcharts.js",
	"services/logging/lib/highcharts-config.js",
	"services/logging/logging.js?v=${mvn.timestamp}"
]
var counter = 0;

document.afterLoad = [];

function loadjscssfile(filename, filetype) {
	var context = filename.search(/\/\//) !== -1 ? "" : document.rootContextUrl;
	if (filetype == "js") { //if filename is a external JavaScript file
		var fileref = document.createElement('script');
		fileref.setAttribute("type", "text/javascript");
		fileref.setAttribute("src", context + filename);
	}
	else if (filetype == "css") { //if filename is an external CSS file
		var fileref = document.createElement("link");
		fileref.setAttribute("rel", "stylesheet");
		fileref.setAttribute("type", "text/css");
		fileref.setAttribute("href", context + filename);
	}
	if (typeof fileref != "undefined")
		document.getElementsByTagName("head")[0].appendChild(fileref);
}
loadjscssfile("services/logging/logging.css?v=${mvn.timestamp}", "css");
loadjscssfile("https://fonts.googleapis.com/css?family=Ubuntu+Mono", "css");

function loadScript(i){
	if (i>= loadFiles.length) {
		setTimeout(function() {
			document.afterLoad.forEach(function(func) {
				func();
			});
		}, 1000);
		return;
	}
	var context = loadFiles[i].search(/\/\//) !== -1 ? "" : document.rootContextUrl;
	$.getScript(context + loadFiles[i], function (){loadScript(++i)});
}

loadScript(0);

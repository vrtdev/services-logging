function loadjscssfile(filename, filetype){
    if (filetype=="js"){ //if filename is a external JavaScript file
        var fileref=document.createElement('script')
        fileref.setAttribute("type","text/javascript")
        fileref.setAttribute("src", filename)
    }
    else if (filetype=="css"){ //if filename is an external CSS file
        var fileref=document.createElement("link")
        fileref.setAttribute("rel", "stylesheet")
        fileref.setAttribute("type", "text/css")
        fileref.setAttribute("href", filename)
    }
    if (typeof fileref!="undefined")
        document.getElementsByTagName("head")[0].appendChild(fileref)
}
loadjscssfile("services/logging/logging.css", "css");

loadjscssfile("services/logging/lib/elasticsearch.js", "js");
loadjscssfile("services/logging/lib/elasticsearch.jquery.js", "js");
loadjscssfile("services/logging/logging.js", "js");
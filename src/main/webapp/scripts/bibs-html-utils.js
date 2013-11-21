function runnerSearchResultsTable(data){
	var results = "";
	for(var i in data){
		results += "<tr>";
		results += "<td><a href=\"/bibs-server/raceresults/"+data[i].id+"\">" +
				"<img src=\"/bibs-server/resources/images/show.png\" " +
					"title=\"Show Runner\"/>" +
				"</a></td>";
		results += "<td>"+data[i].bib+"</td>";
		results += "<td>"+data[i].firstname+" "+data[i].lastname+"</td>";
		results += "<td>("+data[i].gender+")"+data[i].age+"</td>";
		results += "<td>"+data[i].city+", "+data[i].state+"</td>";
		results += "</tr>";
	}
	return results;
}
	
function helpSelect(e){
	jQuery( "select" ).each(function() {
		if(e.id != this.id && e.value == this.value){
			this.value = '-';
		}
	});
	createMap();
}
function createMap(){
	var map = document.getElementById("_map_id");
	var hasBib = false;
	map.value = '';
	jQuery( "select" ).each(function() {
		if(map.value != '') map.value = map.value + ",";
		map.value = map.value + this.value;
		if(this.value == 'bib') hasBib = true;
	});
//	if(!hasBib){
//		alert("Results must contain a Bib field.");
//		return false;
//	}
	return true;
}

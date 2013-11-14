	function runnersSearchResultsTable(data){
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
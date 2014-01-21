function makeRunnersTable(data,results,eventId){
	results = "";
	for(var i in data){
		var timeofficialdisplay = (null!=data[i].timeofficialdisplay) ? data[i].timeofficialdisplay : "";
		var firstname = (null!=data[i].firstname) ? data[i].firstname : "";
		var lastname = (null!=data[i].lastname) ? data[i].lastname : "";
		var bib = (null!=data[i].bib) ? data[i].bib : "";
		var gender = (null!=data[i].gender) ? data[i].gender : "";
		var age = (null!=data[i].age) ? data[i].age : "";
		var city = (null!=data[i].city) ? data[i].city : "";
		var state = (null!=data[i].state) ? data[i].state : "";
		
		results += "<tr>";
		results += "<td>"+timeofficialdisplay+"</td>";
		results += "<td>"+bib+"</td>";
		results += "<td>"+firstname+" "+lastname+"</td>";
		results += "<td>"+age+"</td>";
		results += "<td>"+gender+"</td>";
		results += "<td>"+city+"</td>";
		results += "<td>"+state+"</td>";
		results += "</tr>";
	}
	return results;
}

function runnerSearchResultsTable(data){
	var results = "";
	results += "<tr>";
	results += "<th>&nbsp;</th>";
	results += "<th>Bib</th>";
	results += "<th>First name</th>";
	results += "<th>Last name</th>";
	results += "<th>City</th>";
	results += "<th>State</th>";
	results += "<th>Gender</th>";
	results += "<th>Age</th>";
	results += "</tr>";
	for(var i in data){
		var timeofficialdisplay = (null!=data[i].timeofficialdisplay) ? data[i].timeofficialdisplay : "";
		var firstname = (null!=data[i].firstname) ? data[i].firstname : "";
		var lastname = (null!=data[i].lastname) ? data[i].lastname : "";
		var bib = (null!=data[i].bib) ? data[i].bib : "";
		var gender = (null!=data[i].gender) ? data[i].gender : "";
		var age = (null!=data[i].age) ? data[i].age : "";
		var city = (null!=data[i].city) ? data[i].city : "";
		var state = (null!=data[i].state) ? data[i].state : "";
		
		results += "<tr>";
		results += "<td><a href=\"/bibs-server/raceresults/"+data[i].id+"\">" +
				"<img src=\"/bibs-server/resources/images/show.png\" " +
					"title=\"Show Runner\"/>" +
				"</a></td>";
		results += "<td>"+bib+"</td>";
		results += "<td>"+firstname+"</td>";
		results += "<td>"+lastname+"</td>";
		results += "<td>"+city+"</td>";
		results += "<td>"+state+"</td>";
		results += "<td>"+gender+"</td>";
		results += "<td>"+age+"</td>";
		// results += "<td>"+timeofficialdisplay+"</td>";
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

function getHoursMinutesSeconds(e,l) {
	var rtn = "";
	var hours = Math.round((l / 3600000) );
	var minutes = Math.round((l / 60000) % 60 );
	var seconds = Math.round((l/1000) % 60);
	var millis = Math.round(l%100);
	if(hours>0 && hours <=9) rtn = "0"+hours;
	else if (hours > 9) rtn = hours +":";
	else if (hours == 0) rtn = "00:";
	if(minutes>0 && minutes <=9) rtn = rtn + "0"+minutes;
	else if(minutes > 9) rtn = rtn + ""+minutes;
	else if (minutes == 0) rtn = rtn + "00";
	if(seconds>0 && seconds <=9) rtn = rtn + ":0"+seconds;
	else if(seconds > 9) rtn = rtn + ":"+seconds;
	//rtn = rtn + "."+millis;
	
	seconds = Math.floor(l/1000);
	var numyears = Math.floor(seconds / 31536000);
	var numdays = Math.floor((seconds % 31536000) / 86400); 
	var numhours = Math.floor(((seconds % 31536000) % 86400) / 3600);
	var numminutes = Math.floor((((seconds % 31536000) % 86400) % 3600) / 60);
	var numseconds = (((seconds % 31536000) % 86400) % 3600) % 60;
	if(numminutes<10) numminutes = "0"+numminutes;
	if(numseconds<10) numseconds = "0"+numseconds;
	rtn =  numminutes + ":" + numseconds;
	if(numhours == 0) rtn = "00:" + rtn;
	else if(numhours<=9) rtn = "0"+numhours + ":" + rtn;
	else if(numhours>9) rtn = numhours + ":" + rtn;
	if(numdays==1) rtn = numdays + " day "+rtn;
	else if(numdays>0) rtn = numdays + " days "+rtn;
	
	e.html(rtn);
}


function GetURLParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return sParameterName[1];
        }
    }
}

function beautifyAuthorities(){
	jQuery("#_userAuthorities_id > option").each(function(index,element){
		if(element.innerHTML=="ROLE_SYS_ADMIN"){
			element.innerHTML="System Admin";
		}else if(element.innerHTML=="ROLE_EVENT_ADMIN"){
			element.innerHTML="Event Admin";
		}else if(element.innerHTML=="ROLE_USER_ADMIN"){
			element.innerHTML="Registration Admin";
		}else if(element.innerHTML=="ROLE_USER"){
			element.innerHTML="Registered Runner";
		}
	});
}

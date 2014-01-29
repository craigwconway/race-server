function makeRunnersTable(data,results,eventId){
	results = "";
	results += "<tr>";
	results += "<th>Time</th>";
	results += "<th>Bib</th>";
	results += "<th>Name</th>";
	results += "<th>City</th>";
	results += "<th>State</th>";
	results += "</tr>";
	for(var i in data){
		var timeofficialdisplay = (null!=data[i].timeofficialdisplay) ? data[i].timeofficialdisplay : "";
		var firstname = (null!=data[i].firstname) ? data[i].firstname : "";
		var lastname = (null!=data[i].lastname) ? data[i].lastname : "";
		var bib = (null!=data[i].bib) ? data[i].bib : "";
		var city = (null!=data[i].city) ? data[i].city : "";
		var state = (null!=data[i].state) ? data[i].state : "";
		
		results += "<tr>";
		results += "<td>"+timeofficialdisplay+"</td>";
		results += "<td>"+bib+"</td>";
		results += "<td>"+firstname+" "+lastname+"</td>";
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
}


/*
 * Purl (A JavaScript URL parser) v2.3.1
 * Developed and maintanined by Mark Perkins, mark@allmarkedup.com
 * Source repository: https://github.com/allmarkedup/jQuery-URL-Parser
 * Licensed under an MIT-style license. See https://github.com/allmarkedup/jQuery-URL-Parser/blob/master/LICENSE for details.
 */

;(function(factory) {
    if (typeof define === 'function' && define.amd) {
        define(factory);
    } else {
        window.purl = factory();
    }
})(function() {

    var tag2attr = {
            a       : 'href',
            img     : 'src',
            form    : 'action',
            base    : 'href',
            script  : 'src',
            iframe  : 'src',
            link    : 'href',
            embed   : 'src',
            object  : 'data'
        },

        key = ['source', 'protocol', 'authority', 'userInfo', 'user', 'password', 'host', 'port', 'relative', 'path', 'directory', 'file', 'query', 'fragment'], // keys available to query

        aliases = { 'anchor' : 'fragment' }, // aliases for backwards compatability

        parser = {
            strict : /^(?:([^:\/?#]+):)?(?:\/\/((?:(([^:@]*):?([^:@]*))?@)?([^:\/?#]*)(?::(\d*))?))?((((?:[^?#\/]*\/)*)([^?#]*))(?:\?([^#]*))?(?:#(.*))?)/,  //less intuitive, more accurate to the specs
            loose :  /^(?:(?![^:@]+:[^:@\/]*@)([^:\/?#.]+):)?(?:\/\/)?((?:(([^:@]*):?([^:@]*))?@)?([^:\/?#]*)(?::(\d*))?)(((\/(?:[^?#](?![^?#\/]*\.[^?#\/.]+(?:[?#]|$)))*\/?)?([^?#\/]*))(?:\?([^#]*))?(?:#(.*))?)/ // more intuitive, fails on relative paths and deviates from specs
        },

        isint = /^[0-9]+$/;

    function parseUri( url, strictMode ) {
        var str = decodeURI( url ),
        res   = parser[ strictMode || false ? 'strict' : 'loose' ].exec( str ),
        uri = { attr : {}, param : {}, seg : {} },
        i   = 14;

        while ( i-- ) {
            uri.attr[ key[i] ] = res[i] || '';
        }

        // build query and fragment parameters
        uri.param['query'] = parseString(uri.attr['query']);
        uri.param['fragment'] = parseString(uri.attr['fragment']);

        // split path and fragement into segments
        uri.seg['path'] = uri.attr.path.replace(/^\/+|\/+$/g,'').split('/');
        uri.seg['fragment'] = uri.attr.fragment.replace(/^\/+|\/+$/g,'').split('/');

        // compile a 'base' domain attribute
        uri.attr['base'] = uri.attr.host ? (uri.attr.protocol ?  uri.attr.protocol+'://'+uri.attr.host : uri.attr.host) + (uri.attr.port ? ':'+uri.attr.port : '') : '';

        return uri;
    }

    function getAttrName( elm ) {
        var tn = elm.tagName;
        if ( typeof tn !== 'undefined' ) return tag2attr[tn.toLowerCase()];
        return tn;
    }

    function promote(parent, key) {
        if (parent[key].length === 0) return parent[key] = {};
        var t = {};
        for (var i in parent[key]) t[i] = parent[key][i];
        parent[key] = t;
        return t;
    }

    function parse(parts, parent, key, val) {
        var part = parts.shift();
        if (!part) {
            if (isArray(parent[key])) {
                parent[key].push(val);
            } else if ('object' == typeof parent[key]) {
                parent[key] = val;
            } else if ('undefined' == typeof parent[key]) {
                parent[key] = val;
            } else {
                parent[key] = [parent[key], val];
            }
        } else {
            var obj = parent[key] = parent[key] || [];
            if (']' == part) {
                if (isArray(obj)) {
                    if ('' !== val) obj.push(val);
                } else if ('object' == typeof obj) {
                    obj[keys(obj).length] = val;
                } else {
                    obj = parent[key] = [parent[key], val];
                }
            } else if (~part.indexOf(']')) {
                part = part.substr(0, part.length - 1);
                if (!isint.test(part) && isArray(obj)) obj = promote(parent, key);
                parse(parts, obj, part, val);
                // key
            } else {
                if (!isint.test(part) && isArray(obj)) obj = promote(parent, key);
                parse(parts, obj, part, val);
            }
        }
    }

    function merge(parent, key, val) {
        if (~key.indexOf(']')) {
            var parts = key.split('[');
            parse(parts, parent, 'base', val);
        } else {
            if (!isint.test(key) && isArray(parent.base)) {
                var t = {};
                for (var k in parent.base) t[k] = parent.base[k];
                parent.base = t;
            }
            if (key !== '') {
                set(parent.base, key, val);
            }
        }
        return parent;
    }

    function parseString(str) {
        return reduce(String(str).split(/&|;/), function(ret, pair) {
            try {
                pair = decodeURIComponent(pair.replace(/\+/g, ' '));
            } catch(e) {
                // ignore
            }
            var eql = pair.indexOf('='),
                brace = lastBraceInKey(pair),
                key = pair.substr(0, brace || eql),
                val = pair.substr(brace || eql, pair.length);

            val = val.substr(val.indexOf('=') + 1, val.length);

            if (key === '') {
                key = pair;
                val = '';
            }

            return merge(ret, key, val);
        }, { base: {} }).base;
    }

    function set(obj, key, val) {
        var v = obj[key];
        if (typeof v === 'undefined') {
            obj[key] = val;
        } else if (isArray(v)) {
            v.push(val);
        } else {
            obj[key] = [v, val];
        }
    }

    function lastBraceInKey(str) {
        var len = str.length,
            brace,
            c;
        for (var i = 0; i < len; ++i) {
            c = str[i];
            if (']' == c) brace = false;
            if ('[' == c) brace = true;
            if ('=' == c && !brace) return i;
        }
    }

    function reduce(obj, accumulator){
        var i = 0,
            l = obj.length >> 0,
            curr = arguments[2];
        while (i < l) {
            if (i in obj) curr = accumulator.call(undefined, curr, obj[i], i, obj);
            ++i;
        }
        return curr;
    }

    function isArray(vArg) {
        return Object.prototype.toString.call(vArg) === "[object Array]";
    }

    function keys(obj) {
        var key_array = [];
        for ( var prop in obj ) {
            if ( obj.hasOwnProperty(prop) ) key_array.push(prop);
        }
        return key_array;
    }

    function purl( url, strictMode ) {
        if ( arguments.length === 1 && url === true ) {
            strictMode = true;
            url = undefined;
        }
        strictMode = strictMode || false;
        url = url || window.location.toString();

        return {

            data : parseUri(url, strictMode),

            // get various attributes from the URI
            attr : function( attr ) {
                attr = aliases[attr] || attr;
                return typeof attr !== 'undefined' ? this.data.attr[attr] : this.data.attr;
            },

            // return query string parameters
            param : function( param ) {
                return typeof param !== 'undefined' ? this.data.param.query[param] : this.data.param.query;
            },

            // return fragment parameters
            fparam : function( param ) {
                return typeof param !== 'undefined' ? this.data.param.fragment[param] : this.data.param.fragment;
            },

            // return path segments
            segment : function( seg ) {
                if ( typeof seg === 'undefined' ) {
                    return this.data.seg.path;
                } else {
                    seg = seg < 0 ? this.data.seg.path.length + seg : seg - 1; // negative segments count from the end
                    return this.data.seg.path[seg];
                }
            },

            // return fragment segments
            fsegment : function( seg ) {
                if ( typeof seg === 'undefined' ) {
                    return this.data.seg.fragment;
                } else {
                    seg = seg < 0 ? this.data.seg.fragment.length + seg : seg - 1; // negative segments count from the end
                    return this.data.seg.fragment[seg];
                }
            }

        };

    }
    
    purl.jQuery = function($){
        if ($ != null) {
            $.fn.url = function( strictMode ) {
                var url = '';
                if ( this.length ) {
                    url = $(this).attr( getAttrName(this[0]) ) || '';
                }
                return purl( url, strictMode );
            };

            $.url = purl;
        }
    };

    purl.jQuery(window.jQuery);

    return purl;

});

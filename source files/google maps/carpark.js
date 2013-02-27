//<![CDATA[

    var iconGreen = new google.maps.MarkerImage("mm_20_green.png",
        new google.maps.Size(12.0, 20.0),
        new google.maps.Point(0, 0),
        new google.maps.Point(6.0, 10.0)
    );
	var iconRed = new google.maps.MarkerImage("mm_20_red.png",
        new google.maps.Size(12.0, 20.0),
        new google.maps.Point(0, 0),
        new google.maps.Point(6.0, 10.0)
    );
    var shadow = new google.maps.MarkerImage("mm_20_shadow.png",
        new google.maps.Size(23.0, 20.0),
        new google.maps.Point(0, 0),
        new google.maps.Point(6.0, 10.0)
    );
	var customIcons = [];
    customIcons["free"] = iconGreen;
    customIcons["occupied"] = iconRed;

    function load() {
      var map = new google.maps.Map(document.getElementById("map"), {
        center: new google.maps.LatLng(38.250437, 21.739258),
        zoom: 15,
        mapTypeId: 'roadmap'
      });
      var infoWindow = new google.maps.InfoWindow;

      downloadUrl("get_xml.php", function(data) {
        var xml = data.responseXML;
        var markers = xml.documentElement.getElementsByTagName("node");
        for (var i = 0; i < markers.length; i++) {
          var name = markers[i].getAttribute("name");
          var address = markers[i].getAttribute("address");
		  if (markers[i].getAttribute("type") == 0)
		    {
            var type = 'free';
			}
		else
			{
			var type = 'occupied';
			}
          var point = new google.maps.LatLng(
              parseFloat(markers[i].getAttribute("lat")),
              parseFloat(markers[i].getAttribute("lng")));
          var html = "<b>Node " + name + " - " + type + "</b> <br/>" + address;
          var icon = customIcons[type];
          var marker = new google.maps.Marker({
            map: map,
            position: point,
            icon: icon,
            shadow: shadow
          });
          bindInfoWindow(marker, map, infoWindow, html);
        }
      });
    }

    function bindInfoWindow(marker, map, infoWindow, html) {
      google.maps.event.addListener(marker, 'click', function() {
        infoWindow.setContent(html);
        infoWindow.open(map, marker);
      });
    }

    function downloadUrl(url, callback) {
      var request = window.ActiveXObject ?
          new ActiveXObject('Microsoft.XMLHTTP') :
          new XMLHttpRequest;

      request.onreadystatechange = function() {
        if (request.readyState == 4) {
          request.onreadystatechange = doNothing;
          callback(request, request.status);
        }
      };

      request.open('GET', url, true);
      request.send(null);
    }

    function doNothing() {}

    //]]>
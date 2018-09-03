// ########################  begin DOMContentLoaded ########################
document.addEventListener('DOMContentLoaded', () => {
  // wait till page loads before setting up javascript elements
    document.getElementById("map").innerHTML = "Loading map....";

});
// ########################  end DOMContentLoaded ########################


// initMap() run by google api call in HTML
// got guidance from:
// https://stackoverflow.com/questions/15829048/best-way-to-import-coordinates-from-gpx-file-and-display-using-google-maps-api#15830122
// function will get lat and lon coordinates from server.  server parses file and
// returns a dictionary of points which are added to the map
function initMap() {

    //app references the JavaApp class in document controller.
    //getlatlonpoints is a function in the JavaApp class.
    const response = JSON.parse(app.getLatLonPoints());
//    document.getElementById("points").innerHTML = response;

    var route_points = [];
    var map_bounds = new google.maps.LatLngBounds();

    // get the track points in the XML file and extract the lat an lon coordinates
    // add the coordinates to the a new g_maps coordinate point
    // then add the g_maps coordinate point to the route_points list
    for (p in response) {

    var point = new google.maps.LatLng(response[p].key, response[p].value);
        route_points.push(point);
        map_bounds.extend(point);
    }

    // create a polygon of the route
    var route_drawing = new google.maps.Polyline({
       path: route_points,
       strokeColor: "red",
    });

    // create new map object
    var mapOptions = {
        mapTypeId: 'terrain',
        fullscreenControl: false,
//        backgroundColor: 'black',
//        disableDefaultUI: false
        };

    var map = new google.maps.Map(document.getElementById("map"), mapOptions);


    // set the polygon on the map and set the bounds to match
    route_drawing.setMap(map);
    map.fitBounds(map_bounds);

} // end initMap()


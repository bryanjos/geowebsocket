Geoserver-ws
==============

Allows real-time updates from Geoserver. Utilizes Geoserver's python plugin to
catch wfs events and send updates to clients that subscribe to the websocket channel for updates.


To use locally, do the following steps:

* Install the Geoserver Python Plugin in your installation of Geoserver.
* Restart Tomcat
* Place the wfs_events.py file into the scripts/wfs/tx folder that is created after the Geoserver Python Plugin is installed
* In your client, connect to the websocket endpoint at ws://localhost:8080/geowebsocket/websocket/geowebsocket (or whatever domain it ends up being)
* In another tab or browswer, go to Geoserver and start inserting, updating, deleteing features via wfs.
* If you look at your client's websocket traffic, you should see updates from geoserver

More to come
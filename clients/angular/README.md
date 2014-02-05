Freedomotic AngularJS-based webapp
=================================

This is a WIP project

Usage requirements:
- HTML5-capable browser
- Freedomotic 5.6.0~snapshot (need a modified version of RestAPI
- Disable FD security system (set **KEY_ENABLE_SECURITY=false** into config.xml)

Development Requirements: 
- **Node.js** with npm 

Development status:
- **Alpha Version - in development**

Availalbe features:
- Lists: Environments, Rooms, Objects, Commands, Plugins
- Environment map: renders a map of current environment and places objects in it
- Environment map: movable room edges
- Commands: renders commands as blocks, in order to graphically rearrange and modify them
- Connection Status: checks whether connection to RestApi is alive and working

Developers Quick Start
======================

**1) Create a working FD installation or access a shared RestAPI service - not necessary, see below**
	You'd need access to a RestApi FD service, being it local or a shared remote one.

	A shared RestApi service is available at **fritz.bestmazzo.it:8111**
    
**2) Fork current branch into your pc**

	git clone https://github.com/bestmazzo/freedomotic.git

**3) Switch to proper branch**

	cd freedomotic
	git checkout pl88
    
**3) Enter project root folder**

   	cd clients/angular
    
**4) Install required development extra software**

	npm install -g yo grunt-cli bower
    
**5) Install project's js dependencies**

	bower install

**6) Ready to develop!!**

Development references
======================

- **AngularJS** for the 'BI' part

	Base tutorial: http://docs.angularjs.org/tutorial

	Developer Giude: http://docs.angularjs.org/guide

	API Reference: http://docs.angularjs.org/api

- **Bootstrap** for the layout part

	Base CSS elements: http://getbootstrap.com/css

	Components: http://getbootstrap.com/components
	



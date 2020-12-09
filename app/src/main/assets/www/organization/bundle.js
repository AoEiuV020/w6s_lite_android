/******/ (function(modules) { // webpackBootstrap
/******/ 	var parentHotUpdateCallback = this["webpackHotUpdate"];
/******/ 	this["webpackHotUpdate"] = 
/******/ 	function webpackHotUpdateCallback(chunkId, moreModules) { // eslint-disable-line no-unused-vars
/******/ 		hotAddUpdateChunk(chunkId, moreModules);
/******/ 		if(parentHotUpdateCallback) parentHotUpdateCallback(chunkId, moreModules);
/******/ 	}
/******/ 	
/******/ 	function hotDownloadUpdateChunk(chunkId) { // eslint-disable-line no-unused-vars
/******/ 		var head = document.getElementsByTagName("head")[0];
/******/ 		var script = document.createElement("script");
/******/ 		script.type = "text/javascript";
/******/ 		script.charset = "utf-8";
/******/ 		script.src = __webpack_require__.p + "" + chunkId + "." + hotCurrentHash + ".hot-update.js";
/******/ 		head.appendChild(script);
/******/ 	}
/******/ 	
/******/ 	function hotDownloadManifest(callback) { // eslint-disable-line no-unused-vars
/******/ 		if(typeof XMLHttpRequest === "undefined")
/******/ 			return callback(new Error("No browser support"));
/******/ 		try {
/******/ 			var request = new XMLHttpRequest();
/******/ 			var requestPath = __webpack_require__.p + "" + hotCurrentHash + ".hot-update.json";
/******/ 			request.open("GET", requestPath, true);
/******/ 			request.timeout = 10000;
/******/ 			request.send(null);
/******/ 		} catch(err) {
/******/ 			return callback(err);
/******/ 		}
/******/ 		request.onreadystatechange = function() {
/******/ 			if(request.readyState !== 4) return;
/******/ 			if(request.status === 0) {
/******/ 				// timeout
/******/ 				callback(new Error("Manifest request to " + requestPath + " timed out."));
/******/ 			} else if(request.status === 404) {
/******/ 				// no update available
/******/ 				callback();
/******/ 			} else if(request.status !== 200 && request.status !== 304) {
/******/ 				// other failure
/******/ 				callback(new Error("Manifest request to " + requestPath + " failed."));
/******/ 			} else {
/******/ 				// success
/******/ 				try {
/******/ 					var update = JSON.parse(request.responseText);
/******/ 				} catch(e) {
/******/ 					callback(e);
/******/ 					return;
/******/ 				}
/******/ 				callback(null, update);
/******/ 			}
/******/ 		};
/******/ 	}

/******/ 	
/******/ 	
/******/ 	var hotApplyOnUpdate = true;
/******/ 	var hotCurrentHash = "f266d319d7a71fcee7d5"; // eslint-disable-line no-unused-vars
/******/ 	var hotCurrentModuleData = {};
/******/ 	var hotCurrentParents = []; // eslint-disable-line no-unused-vars
/******/ 	
/******/ 	function hotCreateRequire(moduleId) { // eslint-disable-line no-unused-vars
/******/ 		var me = installedModules[moduleId];
/******/ 		if(!me) return __webpack_require__;
/******/ 		var fn = function(request) {
/******/ 			if(me.hot.active) {
/******/ 				if(installedModules[request]) {
/******/ 					if(installedModules[request].parents.indexOf(moduleId) < 0)
/******/ 						installedModules[request].parents.push(moduleId);
/******/ 					if(me.children.indexOf(request) < 0)
/******/ 						me.children.push(request);
/******/ 				} else hotCurrentParents = [moduleId];
/******/ 			} else {
/******/ 				console.warn("[HMR] unexpected require(" + request + ") from disposed module " + moduleId);
/******/ 				hotCurrentParents = [];
/******/ 			}
/******/ 			return __webpack_require__(request);
/******/ 		};
/******/ 		for(var name in __webpack_require__) {
/******/ 			if(Object.prototype.hasOwnProperty.call(__webpack_require__, name)) {
/******/ 				if(Object.defineProperty) {
/******/ 					Object.defineProperty(fn, name, (function(name) {
/******/ 						return {
/******/ 							configurable: true,
/******/ 							enumerable: true,
/******/ 							get: function() {
/******/ 								return __webpack_require__[name];
/******/ 							},
/******/ 							set: function(value) {
/******/ 								__webpack_require__[name] = value;
/******/ 							}
/******/ 						};
/******/ 					}(name)));
/******/ 				} else {
/******/ 					fn[name] = __webpack_require__[name];
/******/ 				}
/******/ 			}
/******/ 		}
/******/ 	
/******/ 		function ensure(chunkId, callback) {
/******/ 			if(hotStatus === "ready")
/******/ 				hotSetStatus("prepare");
/******/ 			hotChunksLoading++;
/******/ 			__webpack_require__.e(chunkId, function() {
/******/ 				try {
/******/ 					callback.call(null, fn);
/******/ 				} finally {
/******/ 					finishChunkLoading();
/******/ 				}
/******/ 	
/******/ 				function finishChunkLoading() {
/******/ 					hotChunksLoading--;
/******/ 					if(hotStatus === "prepare") {
/******/ 						if(!hotWaitingFilesMap[chunkId]) {
/******/ 							hotEnsureUpdateChunk(chunkId);
/******/ 						}
/******/ 						if(hotChunksLoading === 0 && hotWaitingFiles === 0) {
/******/ 							hotUpdateDownloaded();
/******/ 						}
/******/ 					}
/******/ 				}
/******/ 			});
/******/ 		}
/******/ 		if(Object.defineProperty) {
/******/ 			Object.defineProperty(fn, "e", {
/******/ 				enumerable: true,
/******/ 				value: ensure
/******/ 			});
/******/ 		} else {
/******/ 			fn.e = ensure;
/******/ 		}
/******/ 		return fn;
/******/ 	}
/******/ 	
/******/ 	function hotCreateModule(moduleId) { // eslint-disable-line no-unused-vars
/******/ 		var hot = {
/******/ 			// private stuff
/******/ 			_acceptedDependencies: {},
/******/ 			_declinedDependencies: {},
/******/ 			_selfAccepted: false,
/******/ 			_selfDeclined: false,
/******/ 			_disposeHandlers: [],
/******/ 	
/******/ 			// Module API
/******/ 			active: true,
/******/ 			accept: function(dep, callback) {
/******/ 				if(typeof dep === "undefined")
/******/ 					hot._selfAccepted = true;
/******/ 				else if(typeof dep === "function")
/******/ 					hot._selfAccepted = dep;
/******/ 				else if(typeof dep === "object")
/******/ 					for(var i = 0; i < dep.length; i++)
/******/ 						hot._acceptedDependencies[dep[i]] = callback;
/******/ 				else
/******/ 					hot._acceptedDependencies[dep] = callback;
/******/ 			},
/******/ 			decline: function(dep) {
/******/ 				if(typeof dep === "undefined")
/******/ 					hot._selfDeclined = true;
/******/ 				else if(typeof dep === "number")
/******/ 					hot._declinedDependencies[dep] = true;
/******/ 				else
/******/ 					for(var i = 0; i < dep.length; i++)
/******/ 						hot._declinedDependencies[dep[i]] = true;
/******/ 			},
/******/ 			dispose: function(callback) {
/******/ 				hot._disposeHandlers.push(callback);
/******/ 			},
/******/ 			addDisposeHandler: function(callback) {
/******/ 				hot._disposeHandlers.push(callback);
/******/ 			},
/******/ 			removeDisposeHandler: function(callback) {
/******/ 				var idx = hot._disposeHandlers.indexOf(callback);
/******/ 				if(idx >= 0) hot._disposeHandlers.splice(idx, 1);
/******/ 			},
/******/ 	
/******/ 			// Management API
/******/ 			check: hotCheck,
/******/ 			apply: hotApply,
/******/ 			status: function(l) {
/******/ 				if(!l) return hotStatus;
/******/ 				hotStatusHandlers.push(l);
/******/ 			},
/******/ 			addStatusHandler: function(l) {
/******/ 				hotStatusHandlers.push(l);
/******/ 			},
/******/ 			removeStatusHandler: function(l) {
/******/ 				var idx = hotStatusHandlers.indexOf(l);
/******/ 				if(idx >= 0) hotStatusHandlers.splice(idx, 1);
/******/ 			},
/******/ 	
/******/ 			//inherit from previous dispose call
/******/ 			data: hotCurrentModuleData[moduleId]
/******/ 		};
/******/ 		return hot;
/******/ 	}
/******/ 	
/******/ 	var hotStatusHandlers = [];
/******/ 	var hotStatus = "idle";
/******/ 	
/******/ 	function hotSetStatus(newStatus) {
/******/ 		hotStatus = newStatus;
/******/ 		for(var i = 0; i < hotStatusHandlers.length; i++)
/******/ 			hotStatusHandlers[i].call(null, newStatus);
/******/ 	}
/******/ 	
/******/ 	// while downloading
/******/ 	var hotWaitingFiles = 0;
/******/ 	var hotChunksLoading = 0;
/******/ 	var hotWaitingFilesMap = {};
/******/ 	var hotRequestedFilesMap = {};
/******/ 	var hotAvailibleFilesMap = {};
/******/ 	var hotCallback;
/******/ 	
/******/ 	// The update info
/******/ 	var hotUpdate, hotUpdateNewHash;
/******/ 	
/******/ 	function toModuleId(id) {
/******/ 		var isNumber = (+id) + "" === id;
/******/ 		return isNumber ? +id : id;
/******/ 	}
/******/ 	
/******/ 	function hotCheck(apply, callback) {
/******/ 		if(hotStatus !== "idle") throw new Error("check() is only allowed in idle status");
/******/ 		if(typeof apply === "function") {
/******/ 			hotApplyOnUpdate = false;
/******/ 			callback = apply;
/******/ 		} else {
/******/ 			hotApplyOnUpdate = apply;
/******/ 			callback = callback || function(err) {
/******/ 				if(err) throw err;
/******/ 			};
/******/ 		}
/******/ 		hotSetStatus("check");
/******/ 		hotDownloadManifest(function(err, update) {
/******/ 			if(err) return callback(err);
/******/ 			if(!update) {
/******/ 				hotSetStatus("idle");
/******/ 				callback(null, null);
/******/ 				return;
/******/ 			}
/******/ 	
/******/ 			hotRequestedFilesMap = {};
/******/ 			hotAvailibleFilesMap = {};
/******/ 			hotWaitingFilesMap = {};
/******/ 			for(var i = 0; i < update.c.length; i++)
/******/ 				hotAvailibleFilesMap[update.c[i]] = true;
/******/ 			hotUpdateNewHash = update.h;
/******/ 	
/******/ 			hotSetStatus("prepare");
/******/ 			hotCallback = callback;
/******/ 			hotUpdate = {};
/******/ 			var chunkId = 0;
/******/ 			{ // eslint-disable-line no-lone-blocks
/******/ 				/*globals chunkId */
/******/ 				hotEnsureUpdateChunk(chunkId);
/******/ 			}
/******/ 			if(hotStatus === "prepare" && hotChunksLoading === 0 && hotWaitingFiles === 0) {
/******/ 				hotUpdateDownloaded();
/******/ 			}
/******/ 		});
/******/ 	}
/******/ 	
/******/ 	function hotAddUpdateChunk(chunkId, moreModules) { // eslint-disable-line no-unused-vars
/******/ 		if(!hotAvailibleFilesMap[chunkId] || !hotRequestedFilesMap[chunkId])
/******/ 			return;
/******/ 		hotRequestedFilesMap[chunkId] = false;
/******/ 		for(var moduleId in moreModules) {
/******/ 			if(Object.prototype.hasOwnProperty.call(moreModules, moduleId)) {
/******/ 				hotUpdate[moduleId] = moreModules[moduleId];
/******/ 			}
/******/ 		}
/******/ 		if(--hotWaitingFiles === 0 && hotChunksLoading === 0) {
/******/ 			hotUpdateDownloaded();
/******/ 		}
/******/ 	}
/******/ 	
/******/ 	function hotEnsureUpdateChunk(chunkId) {
/******/ 		if(!hotAvailibleFilesMap[chunkId]) {
/******/ 			hotWaitingFilesMap[chunkId] = true;
/******/ 		} else {
/******/ 			hotRequestedFilesMap[chunkId] = true;
/******/ 			hotWaitingFiles++;
/******/ 			hotDownloadUpdateChunk(chunkId);
/******/ 		}
/******/ 	}
/******/ 	
/******/ 	function hotUpdateDownloaded() {
/******/ 		hotSetStatus("ready");
/******/ 		var callback = hotCallback;
/******/ 		hotCallback = null;
/******/ 		if(!callback) return;
/******/ 		if(hotApplyOnUpdate) {
/******/ 			hotApply(hotApplyOnUpdate, callback);
/******/ 		} else {
/******/ 			var outdatedModules = [];
/******/ 			for(var id in hotUpdate) {
/******/ 				if(Object.prototype.hasOwnProperty.call(hotUpdate, id)) {
/******/ 					outdatedModules.push(toModuleId(id));
/******/ 				}
/******/ 			}
/******/ 			callback(null, outdatedModules);
/******/ 		}
/******/ 	}
/******/ 	
/******/ 	function hotApply(options, callback) {
/******/ 		if(hotStatus !== "ready") throw new Error("apply() is only allowed in ready status");
/******/ 		if(typeof options === "function") {
/******/ 			callback = options;
/******/ 			options = {};
/******/ 		} else if(options && typeof options === "object") {
/******/ 			callback = callback || function(err) {
/******/ 				if(err) throw err;
/******/ 			};
/******/ 		} else {
/******/ 			options = {};
/******/ 			callback = callback || function(err) {
/******/ 				if(err) throw err;
/******/ 			};
/******/ 		}
/******/ 	
/******/ 		function getAffectedStuff(module) {
/******/ 			var outdatedModules = [module];
/******/ 			var outdatedDependencies = {};
/******/ 	
/******/ 			var queue = outdatedModules.slice();
/******/ 			while(queue.length > 0) {
/******/ 				var moduleId = queue.pop();
/******/ 				var module = installedModules[moduleId];
/******/ 				if(!module || module.hot._selfAccepted)
/******/ 					continue;
/******/ 				if(module.hot._selfDeclined) {
/******/ 					return new Error("Aborted because of self decline: " + moduleId);
/******/ 				}
/******/ 				if(moduleId === 0) {
/******/ 					return;
/******/ 				}
/******/ 				for(var i = 0; i < module.parents.length; i++) {
/******/ 					var parentId = module.parents[i];
/******/ 					var parent = installedModules[parentId];
/******/ 					if(parent.hot._declinedDependencies[moduleId]) {
/******/ 						return new Error("Aborted because of declined dependency: " + moduleId + " in " + parentId);
/******/ 					}
/******/ 					if(outdatedModules.indexOf(parentId) >= 0) continue;
/******/ 					if(parent.hot._acceptedDependencies[moduleId]) {
/******/ 						if(!outdatedDependencies[parentId])
/******/ 							outdatedDependencies[parentId] = [];
/******/ 						addAllToSet(outdatedDependencies[parentId], [moduleId]);
/******/ 						continue;
/******/ 					}
/******/ 					delete outdatedDependencies[parentId];
/******/ 					outdatedModules.push(parentId);
/******/ 					queue.push(parentId);
/******/ 				}
/******/ 			}
/******/ 	
/******/ 			return [outdatedModules, outdatedDependencies];
/******/ 		}
/******/ 	
/******/ 		function addAllToSet(a, b) {
/******/ 			for(var i = 0; i < b.length; i++) {
/******/ 				var item = b[i];
/******/ 				if(a.indexOf(item) < 0)
/******/ 					a.push(item);
/******/ 			}
/******/ 		}
/******/ 	
/******/ 		// at begin all updates modules are outdated
/******/ 		// the "outdated" status can propagate to parents if they don't accept the children
/******/ 		var outdatedDependencies = {};
/******/ 		var outdatedModules = [];
/******/ 		var appliedUpdate = {};
/******/ 		for(var id in hotUpdate) {
/******/ 			if(Object.prototype.hasOwnProperty.call(hotUpdate, id)) {
/******/ 				var moduleId = toModuleId(id);
/******/ 				var result = getAffectedStuff(moduleId);
/******/ 				if(!result) {
/******/ 					if(options.ignoreUnaccepted)
/******/ 						continue;
/******/ 					hotSetStatus("abort");
/******/ 					return callback(new Error("Aborted because " + moduleId + " is not accepted"));
/******/ 				}
/******/ 				if(result instanceof Error) {
/******/ 					hotSetStatus("abort");
/******/ 					return callback(result);
/******/ 				}
/******/ 				appliedUpdate[moduleId] = hotUpdate[moduleId];
/******/ 				addAllToSet(outdatedModules, result[0]);
/******/ 				for(var moduleId in result[1]) {
/******/ 					if(Object.prototype.hasOwnProperty.call(result[1], moduleId)) {
/******/ 						if(!outdatedDependencies[moduleId])
/******/ 							outdatedDependencies[moduleId] = [];
/******/ 						addAllToSet(outdatedDependencies[moduleId], result[1][moduleId]);
/******/ 					}
/******/ 				}
/******/ 			}
/******/ 		}
/******/ 	
/******/ 		// Store self accepted outdated modules to require them later by the module system
/******/ 		var outdatedSelfAcceptedModules = [];
/******/ 		for(var i = 0; i < outdatedModules.length; i++) {
/******/ 			var moduleId = outdatedModules[i];
/******/ 			if(installedModules[moduleId] && installedModules[moduleId].hot._selfAccepted)
/******/ 				outdatedSelfAcceptedModules.push({
/******/ 					module: moduleId,
/******/ 					errorHandler: installedModules[moduleId].hot._selfAccepted
/******/ 				});
/******/ 		}
/******/ 	
/******/ 		// Now in "dispose" phase
/******/ 		hotSetStatus("dispose");
/******/ 		var queue = outdatedModules.slice();
/******/ 		while(queue.length > 0) {
/******/ 			var moduleId = queue.pop();
/******/ 			var module = installedModules[moduleId];
/******/ 			if(!module) continue;
/******/ 	
/******/ 			var data = {};
/******/ 	
/******/ 			// Call dispose handlers
/******/ 			var disposeHandlers = module.hot._disposeHandlers;
/******/ 			for(var j = 0; j < disposeHandlers.length; j++) {
/******/ 				var cb = disposeHandlers[j];
/******/ 				cb(data);
/******/ 			}
/******/ 			hotCurrentModuleData[moduleId] = data;
/******/ 	
/******/ 			// disable module (this disables requires from this module)
/******/ 			module.hot.active = false;
/******/ 	
/******/ 			// remove module from cache
/******/ 			delete installedModules[moduleId];
/******/ 	
/******/ 			// remove "parents" references from all children
/******/ 			for(var j = 0; j < module.children.length; j++) {
/******/ 				var child = installedModules[module.children[j]];
/******/ 				if(!child) continue;
/******/ 				var idx = child.parents.indexOf(moduleId);
/******/ 				if(idx >= 0) {
/******/ 					child.parents.splice(idx, 1);
/******/ 				}
/******/ 			}
/******/ 		}
/******/ 	
/******/ 		// remove outdated dependency from module children
/******/ 		for(var moduleId in outdatedDependencies) {
/******/ 			if(Object.prototype.hasOwnProperty.call(outdatedDependencies, moduleId)) {
/******/ 				var module = installedModules[moduleId];
/******/ 				var moduleOutdatedDependencies = outdatedDependencies[moduleId];
/******/ 				for(var j = 0; j < moduleOutdatedDependencies.length; j++) {
/******/ 					var dependency = moduleOutdatedDependencies[j];
/******/ 					var idx = module.children.indexOf(dependency);
/******/ 					if(idx >= 0) module.children.splice(idx, 1);
/******/ 				}
/******/ 			}
/******/ 		}
/******/ 	
/******/ 		// Not in "apply" phase
/******/ 		hotSetStatus("apply");
/******/ 	
/******/ 		hotCurrentHash = hotUpdateNewHash;
/******/ 	
/******/ 		// insert new code
/******/ 		for(var moduleId in appliedUpdate) {
/******/ 			if(Object.prototype.hasOwnProperty.call(appliedUpdate, moduleId)) {
/******/ 				modules[moduleId] = appliedUpdate[moduleId];
/******/ 			}
/******/ 		}
/******/ 	
/******/ 		// call accept handlers
/******/ 		var error = null;
/******/ 		for(var moduleId in outdatedDependencies) {
/******/ 			if(Object.prototype.hasOwnProperty.call(outdatedDependencies, moduleId)) {
/******/ 				var module = installedModules[moduleId];
/******/ 				var moduleOutdatedDependencies = outdatedDependencies[moduleId];
/******/ 				var callbacks = [];
/******/ 				for(var i = 0; i < moduleOutdatedDependencies.length; i++) {
/******/ 					var dependency = moduleOutdatedDependencies[i];
/******/ 					var cb = module.hot._acceptedDependencies[dependency];
/******/ 					if(callbacks.indexOf(cb) >= 0) continue;
/******/ 					callbacks.push(cb);
/******/ 				}
/******/ 				for(var i = 0; i < callbacks.length; i++) {
/******/ 					var cb = callbacks[i];
/******/ 					try {
/******/ 						cb(outdatedDependencies);
/******/ 					} catch(err) {
/******/ 						if(!error)
/******/ 							error = err;
/******/ 					}
/******/ 				}
/******/ 			}
/******/ 		}
/******/ 	
/******/ 		// Load self accepted modules
/******/ 		for(var i = 0; i < outdatedSelfAcceptedModules.length; i++) {
/******/ 			var item = outdatedSelfAcceptedModules[i];
/******/ 			var moduleId = item.module;
/******/ 			hotCurrentParents = [moduleId];
/******/ 			try {
/******/ 				__webpack_require__(moduleId);
/******/ 			} catch(err) {
/******/ 				if(typeof item.errorHandler === "function") {
/******/ 					try {
/******/ 						item.errorHandler(err);
/******/ 					} catch(err) {
/******/ 						if(!error)
/******/ 							error = err;
/******/ 					}
/******/ 				} else if(!error)
/******/ 					error = err;
/******/ 			}
/******/ 		}
/******/ 	
/******/ 		// handle errors in accept handlers and self accepted module load
/******/ 		if(error) {
/******/ 			hotSetStatus("fail");
/******/ 			return callback(error);
/******/ 		}
/******/ 	
/******/ 		hotSetStatus("idle");
/******/ 		callback(null, outdatedModules);
/******/ 	}

/******/ 	// The module cache
/******/ 	var installedModules = {};

/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {

/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;

/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false,
/******/ 			hot: hotCreateModule(moduleId),
/******/ 			parents: hotCurrentParents,
/******/ 			children: []
/******/ 		};

/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, hotCreateRequire(moduleId));

/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;

/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}


/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;

/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;

/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";

/******/ 	// __webpack_hash__
/******/ 	__webpack_require__.h = function() { return hotCurrentHash; };

/******/ 	// Load entry module and return exports
/******/ 	return hotCreateRequire(0)(0);
/******/ })
/************************************************************************/
/******/ ((function(modules) {
	// Check all modules for deduplicated modules
	for(var i in modules) {
		if(Object.prototype.hasOwnProperty.call(modules, i)) {
			switch(typeof modules[i]) {
			case "function": break;
			case "object":
				// Module can be created from a template
				modules[i] = (function(_m) {
					var args = _m.slice(1), fn = modules[_m[0]];
					return function (a,b,c) {
						fn.apply(this, [a,b,c].concat(args));
					};
				}(modules[i]));
				break;
			default:
				// Module is a copy of another module
				modules[i] = modules[modules[i]];
				break;
			}
		}
	}
	return modules;
}([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	module.exports = __webpack_require__(1);


/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(2);
	__webpack_require__(3);
	__webpack_require__(4);

	var _router = new Router();
	var WorkPlus = __webpack_require__(5),
	    WorkplusPromise = __webpack_require__(12),
	    I18n = __webpack_require__(17),
	    Util = __webpack_require__(13),
	    Modal = __webpack_require__(15);

	window._ = __webpack_require__(44);
	window.Promise = __webpack_require__(45);

	var listModule = __webpack_require__(48),
	    approvalModule = __webpack_require__(51),
	    administratorModule = __webpack_require__(56),
	    setModule = __webpack_require__(59),
	    skinModule = __webpack_require__(62),
	    editOrgNameModule = __webpack_require__(65),
	    managementModule = __webpack_require__(67),
	    computerModule = __webpack_require__(72),
	    contactsModule = __webpack_require__(74),
	    employeeModule = __webpack_require__(79),
	    positionModule = __webpack_require__(86),
	    orgModule = __webpack_require__(89),
	    qrcodeModule = __webpack_require__(92);

	__webpack_require__(96);
	__webpack_require__(97);

	var index = {
	    init: function init() {
	        index.initI18n();
	        index.router();
	        index.setDpr();
	        Util.initHtmlFontSize();
	    },
	    initI18n: function initI18n() {
	        var langs = {
	            'zh-CN': __webpack_require__(98),
	            'en': __webpack_require__(99),
	            'zh-rTW': __webpack_require__(100)
	        };

	        var lng = Util.getLangFromUseragent();
	        window.polyglot = I18n.initI18n(langs[lng], lng);
	    },
	    router: function router() {
	        var managementTpl = Util.renderTpl(__webpack_require__(101));
	        var contactsTpl = Util.renderTpl(__webpack_require__(102));
	        var setTpl = Util.renderTpl(__webpack_require__(103));
	        var computerTpl = Util.renderTpl(__webpack_require__(104));
	        var approvalTpl = Util.renderTpl(__webpack_require__(105));
	        var administratorTpl = Util.renderTpl(__webpack_require__(106));
	        var positionTpl = Util.renderTpl(__webpack_require__(107));
	        var employeeTpl = Util.renderTpl(__webpack_require__(108));
	        var orgTpl = Util.renderTpl(__webpack_require__(109));
	        var editOrgNameTpl = Util.renderTpl(__webpack_require__(110));
	        //router setting
	        _router.route([{
	            url: '/management',
	            template: managementTpl,
	            action: managementModule
	        }, {
	            url: '/contacts',
	            template: contactsTpl,
	            action: contactsModule
	        }, {
	            url: '/set',
	            template: setTpl,
	            action: setModule
	        }, {
	            url: '/skin',
	            template: __webpack_require__(111),
	            action: skinModule
	        }, {
	            url: '/edit',
	            template: editOrgNameTpl,
	            action: editOrgNameModule
	        }, {
	            url: '/computer',
	            template: computerTpl,
	            action: computerModule
	        }, {
	            url: '/list',
	            template: __webpack_require__(112),
	            action: listModule
	        }, {
	            url: '/approval',
	            template: approvalTpl,
	            action: approvalModule
	        }, {
	            url: '/administrator',
	            template: administratorTpl,
	            action: administratorModule
	        }, {
	            url: '/qrcode',
	            template: __webpack_require__(113),
	            action: qrcodeModule
	        }, {
	            url: '/position',
	            template: positionTpl,
	            action: positionModule
	        }, {
	            url: '/employee',
	            template: employeeTpl,
	            action: employeeModule
	        }, {
	            url: '/org',
	            template: orgTpl,
	            action: orgModule
	        }]);
	    },
	    setDpr: function setDpr() {
	        var dpr = Math.round(window.devicePixelRatio) || 1,
	            docEl = document.documentElement;

	        // 设置data-dpr属性，留作的css hack之用
	        docEl.setAttribute('data-dpr', dpr);
	    }
	};

	function appInit() {
	    Promise.all([WorkplusPromise.getLoginUserInfo(), WorkplusPromise.getApiConfig(), WorkplusPromise.getDeviceInfo()]).then(index.init).catch(Util.showErrorMsgAndGetOut);

	    document.addEventListener("backbutton", function () {
	        if ($('.modal').length !== 0) {
	            Modal.closeModal();
	            return;
	        }
	        if ($('.page').length === 1) {
	            WorkPlus.getOut();
	            return;
	        }
	        window.history.go(-1);
	    }, false);
	}

	document.addEventListener('deviceready', appInit, false);

/***/ },
/* 2 */
/***/ function(module, exports) {

	// removed by extract-text-webpack-plugin

/***/ },
/* 3 */
/***/ function(module, exports) {

	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	var Zepto = (function() {
	  var undefined, key, $, classList, emptyArray = [], concat = emptyArray.concat, filter = emptyArray.filter, slice = emptyArray.slice,
	    document = window.document,
	    elementDisplay = {}, classCache = {},
	    cssNumber = { 'column-count': 1, 'columns': 1, 'font-weight': 1, 'line-height': 1,'opacity': 1, 'z-index': 1, 'zoom': 1 },
	    fragmentRE = /^\s*<(\w+|!)[^>]*>/,
	    singleTagRE = /^<(\w+)\s*\/?>(?:<\/\1>|)$/,
	    tagExpanderRE = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/ig,
	    rootNodeRE = /^(?:body|html)$/i,
	    capitalRE = /([A-Z])/g,

	    // special attributes that should be get/set via method calls
	    methodAttributes = ['val', 'css', 'html', 'text', 'data', 'width', 'height', 'offset'],

	    adjacencyOperators = [ 'after', 'prepend', 'before', 'append' ],
	    table = document.createElement('table'),
	    tableRow = document.createElement('tr'),
	    containers = {
	      'tr': document.createElement('tbody'),
	      'tbody': table, 'thead': table, 'tfoot': table,
	      'td': tableRow, 'th': tableRow,
	      '*': document.createElement('div')
	    },
	    readyRE = /complete|loaded|interactive/,
	    simpleSelectorRE = /^[\w-]*$/,
	    class2type = {},
	    toString = class2type.toString,
	    zepto = {},
	    camelize, uniq,
	    tempParent = document.createElement('div'),
	    propMap = {
	      'tabindex': 'tabIndex',
	      'readonly': 'readOnly',
	      'for': 'htmlFor',
	      'class': 'className',
	      'maxlength': 'maxLength',
	      'cellspacing': 'cellSpacing',
	      'cellpadding': 'cellPadding',
	      'rowspan': 'rowSpan',
	      'colspan': 'colSpan',
	      'usemap': 'useMap',
	      'frameborder': 'frameBorder',
	      'contenteditable': 'contentEditable'
	    },
	    isArray = Array.isArray ||
	      function(object){ return object instanceof Array }

	  zepto.matches = function(element, selector) {
	    if (!selector || !element || element.nodeType !== 1) return false
	    var matchesSelector = element.webkitMatchesSelector || element.mozMatchesSelector ||
	                          element.oMatchesSelector || element.matchesSelector
	    if (matchesSelector) return matchesSelector.call(element, selector)
	    // fall back to performing a selector:
	    var match, parent = element.parentNode, temp = !parent
	    if (temp) (parent = tempParent).appendChild(element)
	    match = ~zepto.qsa(parent, selector).indexOf(element)
	    temp && tempParent.removeChild(element)
	    return match
	  }

	  function type(obj) {
	    return obj == null ? String(obj) :
	      class2type[toString.call(obj)] || "object"
	  }

	  function isFunction(value) { return type(value) == "function" }
	  function isWindow(obj)     { return obj != null && obj == obj.window }
	  function isDocument(obj)   { return obj != null && obj.nodeType == obj.DOCUMENT_NODE }
	  function isObject(obj)     { return type(obj) == "object" }
	  function isPlainObject(obj) {
	    return isObject(obj) && !isWindow(obj) && Object.getPrototypeOf(obj) == Object.prototype
	  }
	  function likeArray(obj) { return typeof obj.length == 'number' }

	  function compact(array) { return filter.call(array, function(item){ return item != null }) }
	  function flatten(array) { return array.length > 0 ? $.fn.concat.apply([], array) : array }
	  camelize = function(str){ return str.replace(/-+(.)?/g, function(match, chr){ return chr ? chr.toUpperCase() : '' }) }
	  function dasherize(str) {
	    return str.replace(/::/g, '/')
	           .replace(/([A-Z]+)([A-Z][a-z])/g, '$1_$2')
	           .replace(/([a-z\d])([A-Z])/g, '$1_$2')
	           .replace(/_/g, '-')
	           .toLowerCase()
	  }
	  uniq = function(array){ return filter.call(array, function(item, idx){ return array.indexOf(item) == idx }) }

	  function classRE(name) {
	    return name in classCache ?
	      classCache[name] : (classCache[name] = new RegExp('(^|\\s)' + name + '(\\s|$)'))
	  }

	  function maybeAddPx(name, value) {
	    return (typeof value == "number" && !cssNumber[dasherize(name)]) ? value + "px" : value
	  }

	  function defaultDisplay(nodeName) {
	    var element, display
	    if (!elementDisplay[nodeName]) {
	      element = document.createElement(nodeName)
	      document.body.appendChild(element)
	      display = getComputedStyle(element, '').getPropertyValue("display")
	      element.parentNode.removeChild(element)
	      display == "none" && (display = "block")
	      elementDisplay[nodeName] = display
	    }
	    return elementDisplay[nodeName]
	  }

	  function children(element) {
	    return 'children' in element ?
	      slice.call(element.children) :
	      $.map(element.childNodes, function(node){ if (node.nodeType == 1) return node })
	  }

	  function Z(dom, selector) {
	    var i, len = dom ? dom.length : 0
	    for (i = 0; i < len; i++) this[i] = dom[i]
	    this.length = len
	    this.selector = selector || ''
	  }

	  // `$.zepto.fragment` takes a html string and an optional tag name
	  // to generate DOM nodes from the given html string.
	  // The generated DOM nodes are returned as an array.
	  // This function can be overridden in plugins for example to make
	  // it compatible with browsers that don't support the DOM fully.
	  zepto.fragment = function(html, name, properties) {
	    var dom, nodes, container

	    // A special case optimization for a single tag
	    if (singleTagRE.test(html)) dom = $(document.createElement(RegExp.$1))

	    if (!dom) {
	      if (html.replace) html = html.replace(tagExpanderRE, "<$1></$2>")
	      if (name === undefined) name = fragmentRE.test(html) && RegExp.$1
	      if (!(name in containers)) name = '*'

	      container = containers[name]
	      container.innerHTML = '' + html
	      dom = $.each(slice.call(container.childNodes), function(){
	        container.removeChild(this)
	      })
	    }

	    if (isPlainObject(properties)) {
	      nodes = $(dom)
	      $.each(properties, function(key, value) {
	        if (methodAttributes.indexOf(key) > -1) nodes[key](value)
	        else nodes.attr(key, value)
	      })
	    }

	    return dom
	  }

	  // `$.zepto.Z` swaps out the prototype of the given `dom` array
	  // of nodes with `$.fn` and thus supplying all the Zepto functions
	  // to the array. This method can be overridden in plugins.
	  zepto.Z = function(dom, selector) {
	    return new Z(dom, selector)
	  }

	  // `$.zepto.isZ` should return `true` if the given object is a Zepto
	  // collection. This method can be overridden in plugins.
	  zepto.isZ = function(object) {
	    return object instanceof zepto.Z
	  }

	  // `$.zepto.init` is Zepto's counterpart to jQuery's `$.fn.init` and
	  // takes a CSS selector and an optional context (and handles various
	  // special cases).
	  // This method can be overridden in plugins.
	  zepto.init = function(selector, context) {
	    var dom
	    // If nothing given, return an empty Zepto collection
	    if (!selector) return zepto.Z()
	    // Optimize for string selectors
	    else if (typeof selector == 'string') {
	      selector = selector.trim()
	      // If it's a html fragment, create nodes from it
	      // Note: In both Chrome 21 and Firefox 15, DOM error 12
	      // is thrown if the fragment doesn't begin with <
	      if (selector[0] == '<' && fragmentRE.test(selector))
	        dom = zepto.fragment(selector, RegExp.$1, context), selector = null
	      // If there's a context, create a collection on that context first, and select
	      // nodes from there
	      else if (context !== undefined) return $(context).find(selector)
	      // If it's a CSS selector, use it to select nodes.
	      else dom = zepto.qsa(document, selector)
	    }
	    // If a function is given, call it when the DOM is ready
	    else if (isFunction(selector)) return $(document).ready(selector)
	    // If a Zepto collection is given, just return it
	    else if (zepto.isZ(selector)) return selector
	    else {
	      // normalize array if an array of nodes is given
	      if (isArray(selector)) dom = compact(selector)
	      // Wrap DOM nodes.
	      else if (isObject(selector))
	        dom = [selector], selector = null
	      // If it's a html fragment, create nodes from it
	      else if (fragmentRE.test(selector))
	        dom = zepto.fragment(selector.trim(), RegExp.$1, context), selector = null
	      // If there's a context, create a collection on that context first, and select
	      // nodes from there
	      else if (context !== undefined) return $(context).find(selector)
	      // And last but no least, if it's a CSS selector, use it to select nodes.
	      else dom = zepto.qsa(document, selector)
	    }
	    // create a new Zepto collection from the nodes found
	    return zepto.Z(dom, selector)
	  }

	  // `$` will be the base `Zepto` object. When calling this
	  // function just call `$.zepto.init, which makes the implementation
	  // details of selecting nodes and creating Zepto collections
	  // patchable in plugins.
	  $ = function(selector, context){
	    return zepto.init(selector, context)
	  }

	  function extend(target, source, deep) {
	    for (key in source)
	      if (deep && (isPlainObject(source[key]) || isArray(source[key]))) {
	        if (isPlainObject(source[key]) && !isPlainObject(target[key]))
	          target[key] = {}
	        if (isArray(source[key]) && !isArray(target[key]))
	          target[key] = []
	        extend(target[key], source[key], deep)
	      }
	      else if (source[key] !== undefined) target[key] = source[key]
	  }

	  // Copy all but undefined properties from one or more
	  // objects to the `target` object.
	  $.extend = function(target){
	    var deep, args = slice.call(arguments, 1)
	    if (typeof target == 'boolean') {
	      deep = target
	      target = args.shift()
	    }
	    args.forEach(function(arg){ extend(target, arg, deep) })
	    return target
	  }

	  // `$.zepto.qsa` is Zepto's CSS selector implementation which
	  // uses `document.querySelectorAll` and optimizes for some special cases, like `#id`.
	  // This method can be overridden in plugins.
	  zepto.qsa = function(element, selector){
	    var found,
	        maybeID = selector[0] == '#',
	        maybeClass = !maybeID && selector[0] == '.',
	        nameOnly = maybeID || maybeClass ? selector.slice(1) : selector, // Ensure that a 1 char tag name still gets checked
	        isSimple = simpleSelectorRE.test(nameOnly)
	    return (element.getElementById && isSimple && maybeID) ? // Safari DocumentFragment doesn't have getElementById
	      ( (found = element.getElementById(nameOnly)) ? [found] : [] ) :
	      (element.nodeType !== 1 && element.nodeType !== 9 && element.nodeType !== 11) ? [] :
	      slice.call(
	        isSimple && !maybeID && element.getElementsByClassName ? // DocumentFragment doesn't have getElementsByClassName/TagName
	          maybeClass ? element.getElementsByClassName(nameOnly) : // If it's simple, it could be a class
	          element.getElementsByTagName(selector) : // Or a tag
	          element.querySelectorAll(selector) // Or it's not simple, and we need to query all
	      )
	  }

	  function filtered(nodes, selector) {
	    return selector == null ? $(nodes) : $(nodes).filter(selector)
	  }

	  $.contains = document.documentElement.contains ?
	    function(parent, node) {
	      return parent !== node && parent.contains(node)
	    } :
	    function(parent, node) {
	      while (node && (node = node.parentNode))
	        if (node === parent) return true
	      return false
	    }

	  function funcArg(context, arg, idx, payload) {
	    return isFunction(arg) ? arg.call(context, idx, payload) : arg
	  }

	  function setAttribute(node, name, value) {
	    value == null ? node.removeAttribute(name) : node.setAttribute(name, value)
	  }

	  // access className property while respecting SVGAnimatedString
	  function className(node, value){
	    var klass = node.className || '',
	        svg   = klass && klass.baseVal !== undefined

	    if (value === undefined) return svg ? klass.baseVal : klass
	    svg ? (klass.baseVal = value) : (node.className = value)
	  }

	  // "true"  => true
	  // "false" => false
	  // "null"  => null
	  // "42"    => 42
	  // "42.5"  => 42.5
	  // "08"    => "08"
	  // JSON    => parse if valid
	  // String  => self
	  function deserializeValue(value) {
	    try {
	      return value ?
	        value == "true" ||
	        ( value == "false" ? false :
	          value == "null" ? null :
	          +value + "" == value ? +value :
	          /^[\[\{]/.test(value) ? $.parseJSON(value) :
	          value )
	        : value
	    } catch(e) {
	      return value
	    }
	  }

	  $.type = type
	  $.isFunction = isFunction
	  $.isWindow = isWindow
	  $.isArray = isArray
	  $.isPlainObject = isPlainObject

	  $.isEmptyObject = function(obj) {
	    var name
	    for (name in obj) return false
	    return true
	  }

	  $.inArray = function(elem, array, i){
	    return emptyArray.indexOf.call(array, elem, i)
	  }

	  $.camelCase = camelize
	  $.trim = function(str) {
	    return str == null ? "" : String.prototype.trim.call(str)
	  }

	  // plugin compatibility
	  $.uuid = 0
	  $.support = { }
	  $.expr = { }
	  $.noop = function() {}

	  $.map = function(elements, callback){
	    var value, values = [], i, key
	    if (likeArray(elements))
	      for (i = 0; i < elements.length; i++) {
	        value = callback(elements[i], i)
	        if (value != null) values.push(value)
	      }
	    else
	      for (key in elements) {
	        value = callback(elements[key], key)
	        if (value != null) values.push(value)
	      }
	    return flatten(values)
	  }

	  $.each = function(elements, callback){
	    var i, key
	    if (likeArray(elements)) {
	      for (i = 0; i < elements.length; i++)
	        if (callback.call(elements[i], i, elements[i]) === false) return elements
	    } else {
	      for (key in elements)
	        if (callback.call(elements[key], key, elements[key]) === false) return elements
	    }

	    return elements
	  }

	  $.grep = function(elements, callback){
	    return filter.call(elements, callback)
	  }

	  if (window.JSON) $.parseJSON = JSON.parse

	  // Populate the class2type map
	  $.each("Boolean Number String Function Array Date RegExp Object Error".split(" "), function(i, name) {
	    class2type[ "[object " + name + "]" ] = name.toLowerCase()
	  })

	  // Define methods that will be available on all
	  // Zepto collections
	  $.fn = {
	    constructor: zepto.Z,
	    length: 0,

	    // Because a collection acts like an array
	    // copy over these useful array functions.
	    forEach: emptyArray.forEach,
	    reduce: emptyArray.reduce,
	    push: emptyArray.push,
	    sort: emptyArray.sort,
	    splice: emptyArray.splice,
	    indexOf: emptyArray.indexOf,
	    concat: function(){
	      var i, value, args = []
	      for (i = 0; i < arguments.length; i++) {
	        value = arguments[i]
	        args[i] = zepto.isZ(value) ? value.toArray() : value
	      }
	      return concat.apply(zepto.isZ(this) ? this.toArray() : this, args)
	    },

	    // `map` and `slice` in the jQuery API work differently
	    // from their array counterparts
	    map: function(fn){
	      return $($.map(this, function(el, i){ return fn.call(el, i, el) }))
	    },
	    slice: function(){
	      return $(slice.apply(this, arguments))
	    },

	    ready: function(callback){
	      // need to check if document.body exists for IE as that browser reports
	      // document ready when it hasn't yet created the body element
	      if (readyRE.test(document.readyState) && document.body) callback($)
	      else document.addEventListener('DOMContentLoaded', function(){ callback($) }, false)
	      return this
	    },
	    get: function(idx){
	      return idx === undefined ? slice.call(this) : this[idx >= 0 ? idx : idx + this.length]
	    },
	    toArray: function(){ return this.get() },
	    size: function(){
	      return this.length
	    },
	    remove: function(){
	      return this.each(function(){
	        if (this.parentNode != null)
	          this.parentNode.removeChild(this)
	      })
	    },
	    each: function(callback){
	      emptyArray.every.call(this, function(el, idx){
	        return callback.call(el, idx, el) !== false
	      })
	      return this
	    },
	    filter: function(selector){
	      if (isFunction(selector)) return this.not(this.not(selector))
	      return $(filter.call(this, function(element){
	        return zepto.matches(element, selector)
	      }))
	    },
	    add: function(selector,context){
	      return $(uniq(this.concat($(selector,context))))
	    },
	    is: function(selector){
	      return this.length > 0 && zepto.matches(this[0], selector)
	    },
	    not: function(selector){
	      var nodes=[]
	      if (isFunction(selector) && selector.call !== undefined)
	        this.each(function(idx){
	          if (!selector.call(this,idx)) nodes.push(this)
	        })
	      else {
	        var excludes = typeof selector == 'string' ? this.filter(selector) :
	          (likeArray(selector) && isFunction(selector.item)) ? slice.call(selector) : $(selector)
	        this.forEach(function(el){
	          if (excludes.indexOf(el) < 0) nodes.push(el)
	        })
	      }
	      return $(nodes)
	    },
	    has: function(selector){
	      return this.filter(function(){
	        return isObject(selector) ?
	          $.contains(this, selector) :
	          $(this).find(selector).size()
	      })
	    },
	    eq: function(idx){
	      return idx === -1 ? this.slice(idx) : this.slice(idx, + idx + 1)
	    },
	    first: function(){
	      var el = this[0]
	      return el && !isObject(el) ? el : $(el)
	    },
	    last: function(){
	      var el = this[this.length - 1]
	      return el && !isObject(el) ? el : $(el)
	    },
	    find: function(selector){
	      var result, $this = this
	      if (!selector) result = $()
	      else if (typeof selector == 'object')
	        result = $(selector).filter(function(){
	          var node = this
	          return emptyArray.some.call($this, function(parent){
	            return $.contains(parent, node)
	          })
	        })
	      else if (this.length == 1) result = $(zepto.qsa(this[0], selector))
	      else result = this.map(function(){ return zepto.qsa(this, selector) })
	      return result
	    },
	    closest: function(selector, context){
	      var node = this[0], collection = false
	      if (typeof selector == 'object') collection = $(selector)
	      while (node && !(collection ? collection.indexOf(node) >= 0 : zepto.matches(node, selector)))
	        node = node !== context && !isDocument(node) && node.parentNode
	      return $(node)
	    },
	    parents: function(selector){
	      var ancestors = [], nodes = this
	      while (nodes.length > 0)
	        nodes = $.map(nodes, function(node){
	          if ((node = node.parentNode) && !isDocument(node) && ancestors.indexOf(node) < 0) {
	            ancestors.push(node)
	            return node
	          }
	        })
	      return filtered(ancestors, selector)
	    },
	    parent: function(selector){
	      return filtered(uniq(this.pluck('parentNode')), selector)
	    },
	    children: function(selector){
	      return filtered(this.map(function(){ return children(this) }), selector)
	    },
	    contents: function() {
	      return this.map(function() { return this.contentDocument || slice.call(this.childNodes) })
	    },
	    siblings: function(selector){
	      return filtered(this.map(function(i, el){
	        return filter.call(children(el.parentNode), function(child){ return child!==el })
	      }), selector)
	    },
	    empty: function(){
	      return this.each(function(){ this.innerHTML = '' })
	    },
	    // `pluck` is borrowed from Prototype.js
	    pluck: function(property){
	      return $.map(this, function(el){ return el[property] })
	    },
	    show: function(){
	      return this.each(function(){
	        this.style.display == "none" && (this.style.display = '')
	        if (getComputedStyle(this, '').getPropertyValue("display") == "none")
	          this.style.display = defaultDisplay(this.nodeName)
	      })
	    },
	    replaceWith: function(newContent){
	      return this.before(newContent).remove()
	    },
	    wrap: function(structure){
	      var func = isFunction(structure)
	      if (this[0] && !func)
	        var dom   = $(structure).get(0),
	            clone = dom.parentNode || this.length > 1

	      return this.each(function(index){
	        $(this).wrapAll(
	          func ? structure.call(this, index) :
	            clone ? dom.cloneNode(true) : dom
	        )
	      })
	    },
	    wrapAll: function(structure){
	      if (this[0]) {
	        $(this[0]).before(structure = $(structure))
	        var children
	        // drill down to the inmost element
	        while ((children = structure.children()).length) structure = children.first()
	        $(structure).append(this)
	      }
	      return this
	    },
	    wrapInner: function(structure){
	      var func = isFunction(structure)
	      return this.each(function(index){
	        var self = $(this), contents = self.contents(),
	            dom  = func ? structure.call(this, index) : structure
	        contents.length ? contents.wrapAll(dom) : self.append(dom)
	      })
	    },
	    unwrap: function(){
	      this.parent().each(function(){
	        $(this).replaceWith($(this).children())
	      })
	      return this
	    },
	    clone: function(){
	      return this.map(function(){ return this.cloneNode(true) })
	    },
	    hide: function(){
	      return this.css("display", "none")
	    },
	    toggle: function(setting){
	      return this.each(function(){
	        var el = $(this)
	        ;(setting === undefined ? el.css("display") == "none" : setting) ? el.show() : el.hide()
	      })
	    },
	    prev: function(selector){ return $(this.pluck('previousElementSibling')).filter(selector || '*') },
	    next: function(selector){ return $(this.pluck('nextElementSibling')).filter(selector || '*') },
	    html: function(html){
	      return 0 in arguments ?
	        this.each(function(idx){
	          var originHtml = this.innerHTML
	          $(this).empty().append( funcArg(this, html, idx, originHtml) )
	        }) :
	        (0 in this ? this[0].innerHTML : null)
	    },
	    text: function(text){
	      return 0 in arguments ?
	        this.each(function(idx){
	          var newText = funcArg(this, text, idx, this.textContent)
	          this.textContent = newText == null ? '' : ''+newText
	        }) :
	        (0 in this ? this.pluck('textContent').join("") : null)
	    },
	    attr: function(name, value){
	      var result
	      return (typeof name == 'string' && !(1 in arguments)) ?
	        (!this.length || this[0].nodeType !== 1 ? undefined :
	          (!(result = this[0].getAttribute(name)) && name in this[0]) ? this[0][name] : result
	        ) :
	        this.each(function(idx){
	          if (this.nodeType !== 1) return
	          if (isObject(name)) for (key in name) setAttribute(this, key, name[key])
	          else setAttribute(this, name, funcArg(this, value, idx, this.getAttribute(name)))
	        })
	    },
	    removeAttr: function(name){
	      return this.each(function(){ this.nodeType === 1 && name.split(' ').forEach(function(attribute){
	        setAttribute(this, attribute)
	      }, this)})
	    },
	    prop: function(name, value){
	      name = propMap[name] || name
	      return (1 in arguments) ?
	        this.each(function(idx){
	          this[name] = funcArg(this, value, idx, this[name])
	        }) :
	        (this[0] && this[0][name])
	    },
	    data: function(name, value){
	      var attrName = 'data-' + name.replace(capitalRE, '-$1').toLowerCase()

	      var data = (1 in arguments) ?
	        this.attr(attrName, value) :
	        this.attr(attrName)

	      return data !== null ? deserializeValue(data) : undefined
	    },
	    val: function(value){
	      return 0 in arguments ?
	        this.each(function(idx){
	          this.value = funcArg(this, value, idx, this.value)
	        }) :
	        (this[0] && (this[0].multiple ?
	           $(this[0]).find('option').filter(function(){ return this.selected }).pluck('value') :
	           this[0].value)
	        )
	    },
	    offset: function(coordinates){
	      if (coordinates) return this.each(function(index){
	        var $this = $(this),
	            coords = funcArg(this, coordinates, index, $this.offset()),
	            parentOffset = $this.offsetParent().offset(),
	            props = {
	              top:  coords.top  - parentOffset.top,
	              left: coords.left - parentOffset.left
	            }

	        if ($this.css('position') == 'static') props['position'] = 'relative'
	        $this.css(props)
	      })
	      if (!this.length) return null
	      if (!$.contains(document.documentElement, this[0]))
	        return {top: 0, left: 0}
	      var obj = this[0].getBoundingClientRect()
	      return {
	        left: obj.left + window.pageXOffset,
	        top: obj.top + window.pageYOffset,
	        width: Math.round(obj.width),
	        height: Math.round(obj.height)
	      }
	    },
	    css: function(property, value){
	      if (arguments.length < 2) {
	        var computedStyle, element = this[0]
	        if(!element) return
	        computedStyle = getComputedStyle(element, '')
	        if (typeof property == 'string')
	          return element.style[camelize(property)] || computedStyle.getPropertyValue(property)
	        else if (isArray(property)) {
	          var props = {}
	          $.each(property, function(_, prop){
	            props[prop] = (element.style[camelize(prop)] || computedStyle.getPropertyValue(prop))
	          })
	          return props
	        }
	      }

	      var css = ''
	      if (type(property) == 'string') {
	        if (!value && value !== 0)
	          this.each(function(){ this.style.removeProperty(dasherize(property)) })
	        else
	          css = dasherize(property) + ":" + maybeAddPx(property, value)
	      } else {
	        for (key in property)
	          if (!property[key] && property[key] !== 0)
	            this.each(function(){ this.style.removeProperty(dasherize(key)) })
	          else
	            css += dasherize(key) + ':' + maybeAddPx(key, property[key]) + ';'
	      }

	      return this.each(function(){ this.style.cssText += ';' + css })
	    },
	    index: function(element){
	      return element ? this.indexOf($(element)[0]) : this.parent().children().indexOf(this[0])
	    },
	    hasClass: function(name){
	      if (!name) return false
	      return emptyArray.some.call(this, function(el){
	        return this.test(className(el))
	      }, classRE(name))
	    },
	    addClass: function(name){
	      if (!name) return this
	      return this.each(function(idx){
	        if (!('className' in this)) return
	        classList = []
	        var cls = className(this), newName = funcArg(this, name, idx, cls)
	        newName.split(/\s+/g).forEach(function(klass){
	          if (!$(this).hasClass(klass)) classList.push(klass)
	        }, this)
	        classList.length && className(this, cls + (cls ? " " : "") + classList.join(" "))
	      })
	    },
	    removeClass: function(name){
	      return this.each(function(idx){
	        if (!('className' in this)) return
	        if (name === undefined) return className(this, '')
	        classList = className(this)
	        funcArg(this, name, idx, classList).split(/\s+/g).forEach(function(klass){
	          classList = classList.replace(classRE(klass), " ")
	        })
	        className(this, classList.trim())
	      })
	    },
	    toggleClass: function(name, when){
	      if (!name) return this
	      return this.each(function(idx){
	        var $this = $(this), names = funcArg(this, name, idx, className(this))
	        names.split(/\s+/g).forEach(function(klass){
	          (when === undefined ? !$this.hasClass(klass) : when) ?
	            $this.addClass(klass) : $this.removeClass(klass)
	        })
	      })
	    },
	    scrollTop: function(value){
	      if (!this.length) return
	      var hasScrollTop = 'scrollTop' in this[0]
	      if (value === undefined) return hasScrollTop ? this[0].scrollTop : this[0].pageYOffset
	      return this.each(hasScrollTop ?
	        function(){ this.scrollTop = value } :
	        function(){ this.scrollTo(this.scrollX, value) })
	    },
	    scrollLeft: function(value){
	      if (!this.length) return
	      var hasScrollLeft = 'scrollLeft' in this[0]
	      if (value === undefined) return hasScrollLeft ? this[0].scrollLeft : this[0].pageXOffset
	      return this.each(hasScrollLeft ?
	        function(){ this.scrollLeft = value } :
	        function(){ this.scrollTo(value, this.scrollY) })
	    },
	    position: function() {
	      if (!this.length) return

	      var elem = this[0],
	        // Get *real* offsetParent
	        offsetParent = this.offsetParent(),
	        // Get correct offsets
	        offset       = this.offset(),
	        parentOffset = rootNodeRE.test(offsetParent[0].nodeName) ? { top: 0, left: 0 } : offsetParent.offset()

	      // Subtract element margins
	      // note: when an element has margin: auto the offsetLeft and marginLeft
	      // are the same in Safari causing offset.left to incorrectly be 0
	      offset.top  -= parseFloat( $(elem).css('margin-top') ) || 0
	      offset.left -= parseFloat( $(elem).css('margin-left') ) || 0

	      // Add offsetParent borders
	      parentOffset.top  += parseFloat( $(offsetParent[0]).css('border-top-width') ) || 0
	      parentOffset.left += parseFloat( $(offsetParent[0]).css('border-left-width') ) || 0

	      // Subtract the two offsets
	      return {
	        top:  offset.top  - parentOffset.top,
	        left: offset.left - parentOffset.left
	      }
	    },
	    offsetParent: function() {
	      return this.map(function(){
	        var parent = this.offsetParent || document.body
	        while (parent && !rootNodeRE.test(parent.nodeName) && $(parent).css("position") == "static")
	          parent = parent.offsetParent
	        return parent
	      })
	    }
	  }

	  // for now
	  $.fn.detach = $.fn.remove

	  // Generate the `width` and `height` functions
	  ;['width', 'height'].forEach(function(dimension){
	    var dimensionProperty =
	      dimension.replace(/./, function(m){ return m[0].toUpperCase() })

	    $.fn[dimension] = function(value){
	      var offset, el = this[0]
	      if (value === undefined) return isWindow(el) ? el['inner' + dimensionProperty] :
	        isDocument(el) ? el.documentElement['scroll' + dimensionProperty] :
	        (offset = this.offset()) && offset[dimension]
	      else return this.each(function(idx){
	        el = $(this)
	        el.css(dimension, funcArg(this, value, idx, el[dimension]()))
	      })
	    }
	  })

	  function traverseNode(node, fun) {
	    fun(node)
	    for (var i = 0, len = node.childNodes.length; i < len; i++)
	      traverseNode(node.childNodes[i], fun)
	  }

	  // Generate the `after`, `prepend`, `before`, `append`,
	  // `insertAfter`, `insertBefore`, `appendTo`, and `prependTo` methods.
	  adjacencyOperators.forEach(function(operator, operatorIndex) {
	    var inside = operatorIndex % 2 //=> prepend, append

	    $.fn[operator] = function(){
	      // arguments can be nodes, arrays of nodes, Zepto objects and HTML strings
	      var argType, nodes = $.map(arguments, function(arg) {
	            argType = type(arg)
	            return argType == "object" || argType == "array" || arg == null ?
	              arg : zepto.fragment(arg)
	          }),
	          parent, copyByClone = this.length > 1
	      if (nodes.length < 1) return this

	      return this.each(function(_, target){
	        parent = inside ? target : target.parentNode

	        // convert all methods to a "before" operation
	        target = operatorIndex == 0 ? target.nextSibling :
	                 operatorIndex == 1 ? target.firstChild :
	                 operatorIndex == 2 ? target :
	                 null

	        var parentInDocument = $.contains(document.documentElement, parent)

	        nodes.forEach(function(node){
	          if (copyByClone) node = node.cloneNode(true)
	          else if (!parent) return $(node).remove()

	          parent.insertBefore(node, target)
	          if (parentInDocument) traverseNode(node, function(el){
	            if (el.nodeName != null && el.nodeName.toUpperCase() === 'SCRIPT' &&
	               (!el.type || el.type === 'text/javascript') && !el.src)
	              window['eval'].call(window, el.innerHTML)
	          })
	        })
	      })
	    }

	    // after    => insertAfter
	    // prepend  => prependTo
	    // before   => insertBefore
	    // append   => appendTo
	    $.fn[inside ? operator+'To' : 'insert'+(operatorIndex ? 'Before' : 'After')] = function(html){
	      $(html)[operator](this)
	      return this
	    }
	  })

	  zepto.Z.prototype = Z.prototype = $.fn

	  // Export internal API functions in the `$.zepto` namespace
	  zepto.uniq = uniq
	  zepto.deserializeValue = deserializeValue
	  $.zepto = zepto

	  return $
	})()

	// If `$` is not yet defined, point it to `Zepto`
	window.Zepto = Zepto
	window.$ === undefined && (window.$ = Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($){
	  var jsonpID = 0,
	      document = window.document,
	      key,
	      name,
	      rscript = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
	      scriptTypeRE = /^(?:text|application)\/javascript/i,
	      xmlTypeRE = /^(?:text|application)\/xml/i,
	      jsonType = 'application/json',
	      htmlType = 'text/html',
	      blankRE = /^\s*$/,
	      originAnchor = document.createElement('a')

	  originAnchor.href = window.location.href

	  // trigger a custom event and return false if it was cancelled
	  function triggerAndReturn(context, eventName, data) {
	    var event = $.Event(eventName)
	    $(context).trigger(event, data)
	    return !event.isDefaultPrevented()
	  }

	  // trigger an Ajax "global" event
	  function triggerGlobal(settings, context, eventName, data) {
	    if (settings.global) return triggerAndReturn(context || document, eventName, data)
	  }

	  // Number of active Ajax requests
	  $.active = 0

	  function ajaxStart(settings) {
	    if (settings.global && $.active++ === 0) triggerGlobal(settings, null, 'ajaxStart')
	  }
	  function ajaxStop(settings) {
	    if (settings.global && !(--$.active)) triggerGlobal(settings, null, 'ajaxStop')
	  }

	  // triggers an extra global event "ajaxBeforeSend" that's like "ajaxSend" but cancelable
	  function ajaxBeforeSend(xhr, settings) {
	    var context = settings.context
	    if (settings.beforeSend.call(context, xhr, settings) === false ||
	        triggerGlobal(settings, context, 'ajaxBeforeSend', [xhr, settings]) === false)
	      return false

	    triggerGlobal(settings, context, 'ajaxSend', [xhr, settings])
	  }
	  function ajaxSuccess(data, xhr, settings, deferred) {
	    var context = settings.context, status = 'success'
	    settings.success.call(context, data, status, xhr)
	    if (deferred) deferred.resolveWith(context, [data, status, xhr])
	    triggerGlobal(settings, context, 'ajaxSuccess', [xhr, settings, data])
	    ajaxComplete(status, xhr, settings)
	  }
	  // type: "timeout", "error", "abort", "parsererror"
	  function ajaxError(error, type, xhr, settings, deferred) {
	    var context = settings.context
	    settings.error.call(context, xhr, type, error)
	    if (deferred) deferred.rejectWith(context, [xhr, type, error])
	    triggerGlobal(settings, context, 'ajaxError', [xhr, settings, error || type])
	    ajaxComplete(type, xhr, settings)
	  }
	  // status: "success", "notmodified", "error", "timeout", "abort", "parsererror"
	  function ajaxComplete(status, xhr, settings) {
	    var context = settings.context
	    settings.complete.call(context, xhr, status)
	    triggerGlobal(settings, context, 'ajaxComplete', [xhr, settings])
	    ajaxStop(settings)
	  }

	  // Empty function, used as default callback
	  function empty() {}

	  $.ajaxJSONP = function(options, deferred){
	    if (!('type' in options)) return $.ajax(options)

	    var _callbackName = options.jsonpCallback,
	      callbackName = ($.isFunction(_callbackName) ?
	        _callbackName() : _callbackName) || ('jsonp' + (++jsonpID)),
	      script = document.createElement('script'),
	      originalCallback = window[callbackName],
	      responseData,
	      abort = function(errorType) {
	        $(script).triggerHandler('error', errorType || 'abort')
	      },
	      xhr = { abort: abort }, abortTimeout

	    if (deferred) deferred.promise(xhr)

	    $(script).on('load error', function(e, errorType){
	      clearTimeout(abortTimeout)
	      $(script).off().remove()

	      if (e.type == 'error' || !responseData) {
	        ajaxError(null, errorType || 'error', xhr, options, deferred)
	      } else {
	        ajaxSuccess(responseData[0], xhr, options, deferred)
	      }

	      window[callbackName] = originalCallback
	      if (responseData && $.isFunction(originalCallback))
	        originalCallback(responseData[0])

	      originalCallback = responseData = undefined
	    })

	    if (ajaxBeforeSend(xhr, options) === false) {
	      abort('abort')
	      return xhr
	    }

	    window[callbackName] = function(){
	      responseData = arguments
	    }

	    script.src = options.url.replace(/\?(.+)=\?/, '?$1=' + callbackName)
	    document.head.appendChild(script)

	    if (options.timeout > 0) abortTimeout = setTimeout(function(){
	      abort('timeout')
	    }, options.timeout)

	    return xhr
	  }

	  $.ajaxSettings = {
	    // Default type of request
	    type: 'GET',
	    // Callback that is executed before request
	    beforeSend: empty,
	    // Callback that is executed if the request succeeds
	    success: empty,
	    // Callback that is executed the the server drops error
	    error: empty,
	    // Callback that is executed on request complete (both: error and success)
	    complete: empty,
	    // The context for the callbacks
	    context: null,
	    // Whether to trigger "global" Ajax events
	    global: true,
	    // Transport
	    xhr: function () {
	      return new window.XMLHttpRequest()
	    },
	    // MIME types mapping
	    // IIS returns Javascript as "application/x-javascript"
	    accepts: {
	      script: 'text/javascript, application/javascript, application/x-javascript',
	      json:   jsonType,
	      xml:    'application/xml, text/xml',
	      html:   htmlType,
	      text:   'text/plain'
	    },
	    // Whether the request is to another domain
	    crossDomain: false,
	    // Default timeout
	    timeout: 0,
	    // Whether data should be serialized to string
	    processData: true,
	    // Whether the browser should be allowed to cache GET responses
	    cache: true
	  }

	  function mimeToDataType(mime) {
	    if (mime) mime = mime.split(';', 2)[0]
	    return mime && ( mime == htmlType ? 'html' :
	      mime == jsonType ? 'json' :
	      scriptTypeRE.test(mime) ? 'script' :
	      xmlTypeRE.test(mime) && 'xml' ) || 'text'
	  }

	  function appendQuery(url, query) {
	    if (query == '') return url
	    return (url + '&' + query).replace(/[&?]{1,2}/, '?')
	  }

	  // serialize payload and append it to the URL for GET requests
	  function serializeData(options) {
	    if (options.processData && options.data && $.type(options.data) != "string")
	      options.data = $.param(options.data, options.traditional)
	    if (options.data && (!options.type || options.type.toUpperCase() == 'GET'))
	      options.url = appendQuery(options.url, options.data), options.data = undefined
	  }

	  $.ajax = function(options){
	    var settings = $.extend({}, options || {}),
	        deferred = $.Deferred && $.Deferred(),
	        urlAnchor, hashIndex
	    for (key in $.ajaxSettings) if (settings[key] === undefined) settings[key] = $.ajaxSettings[key]

	    ajaxStart(settings)

	    if (!settings.crossDomain) {
	      urlAnchor = document.createElement('a')
	      urlAnchor.href = settings.url
	      // cleans up URL for .href (IE only), see https://github.com/madrobby/zepto/pull/1049
	      urlAnchor.href = urlAnchor.href
	      settings.crossDomain = (originAnchor.protocol + '//' + originAnchor.host) !== (urlAnchor.protocol + '//' + urlAnchor.host)
	    }

	    if (!settings.url) settings.url = window.location.toString()
	    if ((hashIndex = settings.url.indexOf('#')) > -1) settings.url = settings.url.slice(0, hashIndex)
	    serializeData(settings)

	    var dataType = settings.dataType, hasPlaceholder = /\?.+=\?/.test(settings.url)
	    if (hasPlaceholder) dataType = 'jsonp'

	    if (settings.cache === false || (
	         (!options || options.cache !== true) &&
	         ('script' == dataType || 'jsonp' == dataType)
	        ))
	      settings.url = appendQuery(settings.url, '_=' + Date.now())

	    if ('jsonp' == dataType) {
	      if (!hasPlaceholder)
	        settings.url = appendQuery(settings.url,
	          settings.jsonp ? (settings.jsonp + '=?') : settings.jsonp === false ? '' : 'callback=?')
	      return $.ajaxJSONP(settings, deferred)
	    }

	    var mime = settings.accepts[dataType],
	        headers = { },
	        setHeader = function(name, value) { headers[name.toLowerCase()] = [name, value] },
	        protocol = /^([\w-]+:)\/\//.test(settings.url) ? RegExp.$1 : window.location.protocol,
	        xhr = settings.xhr(),
	        nativeSetHeader = xhr.setRequestHeader,
	        abortTimeout

	    if (deferred) deferred.promise(xhr)

	    if (!settings.crossDomain) setHeader('X-Requested-With', 'XMLHttpRequest')
	    setHeader('Accept', mime || '*/*')
	    if (mime = settings.mimeType || mime) {
	      if (mime.indexOf(',') > -1) mime = mime.split(',', 2)[0]
	      xhr.overrideMimeType && xhr.overrideMimeType(mime)
	    }
	    if (settings.contentType || (settings.contentType !== false && settings.data && settings.type.toUpperCase() != 'GET'))
	      setHeader('Content-Type', settings.contentType || 'application/x-www-form-urlencoded')

	    if (settings.headers) for (name in settings.headers) setHeader(name, settings.headers[name])
	    xhr.setRequestHeader = setHeader

	    xhr.onreadystatechange = function(){
	      if (xhr.readyState == 4) {
	        xhr.onreadystatechange = empty
	        clearTimeout(abortTimeout)
	        var result, error = false
	        if ((xhr.status >= 200 && xhr.status < 300) || xhr.status == 304 || (xhr.status == 0 && protocol == 'file:')) {
	          dataType = dataType || mimeToDataType(settings.mimeType || xhr.getResponseHeader('content-type'))

	          if (xhr.responseType == 'arraybuffer' || xhr.responseType == 'blob')
	            result = xhr.response
	          else {
	            result = xhr.responseText

	            try {
	              // http://perfectionkills.com/global-eval-what-are-the-options/
	              if (dataType == 'script')    (1,eval)(result)
	              else if (dataType == 'xml')  result = xhr.responseXML
	              else if (dataType == 'json') result = blankRE.test(result) ? null : $.parseJSON(result)
	            } catch (e) { error = e }

	            if (error) return ajaxError(error, 'parsererror', xhr, settings, deferred)
	          }

	          ajaxSuccess(result, xhr, settings, deferred)
	        } else {
	          ajaxError(xhr.statusText || null, xhr.status ? 'error' : 'abort', xhr, settings, deferred)
	        }
	      }
	    }

	    if (ajaxBeforeSend(xhr, settings) === false) {
	      xhr.abort()
	      ajaxError(null, 'abort', xhr, settings, deferred)
	      return xhr
	    }

	    if (settings.xhrFields) for (name in settings.xhrFields) xhr[name] = settings.xhrFields[name]

	    var async = 'async' in settings ? settings.async : true
	    xhr.open(settings.type, settings.url, async, settings.username, settings.password)

	    for (name in headers) nativeSetHeader.apply(xhr, headers[name])

	    if (settings.timeout > 0) abortTimeout = setTimeout(function(){
	        xhr.onreadystatechange = empty
	        xhr.abort()
	        ajaxError(null, 'timeout', xhr, settings, deferred)
	      }, settings.timeout)

	    // avoid sending empty string (#319)
	    xhr.send(settings.data ? settings.data : null)
	    return xhr
	  }

	  // handle optional data/success arguments
	  function parseArguments(url, data, success, dataType) {
	    if ($.isFunction(data)) dataType = success, success = data, data = undefined
	    if (!$.isFunction(success)) dataType = success, success = undefined
	    return {
	      url: url
	    , data: data
	    , success: success
	    , dataType: dataType
	    }
	  }

	  $.get = function(/* url, data, success, dataType */){
	    return $.ajax(parseArguments.apply(null, arguments))
	  }

	  $.post = function(/* url, data, success, dataType */){
	    var options = parseArguments.apply(null, arguments)
	    options.type = 'POST'
	    return $.ajax(options)
	  }

	  $.getJSON = function(/* url, data, success */){
	    var options = parseArguments.apply(null, arguments)
	    options.dataType = 'json'
	    return $.ajax(options)
	  }

	  $.fn.load = function(url, data, success){
	    if (!this.length) return this
	    var self = this, parts = url.split(/\s/), selector,
	        options = parseArguments(url, data, success),
	        callback = options.success
	    if (parts.length > 1) options.url = parts[0], selector = parts[1]
	    options.success = function(response){
	      self.html(selector ?
	        $('<div>').html(response.replace(rscript, "")).find(selector)
	        : response)
	      callback && callback.apply(self, arguments)
	    }
	    $.ajax(options)
	    return this
	  }

	  var escape = encodeURIComponent

	  function serialize(params, obj, traditional, scope){
	    var type, array = $.isArray(obj), hash = $.isPlainObject(obj)
	    $.each(obj, function(key, value) {
	      type = $.type(value)
	      if (scope) key = traditional ? scope :
	        scope + '[' + (hash || type == 'object' || type == 'array' ? key : '') + ']'
	      // handle data in serializeArray() format
	      if (!scope && array) params.add(value.name, value.value)
	      // recurse into nested objects
	      else if (type == "array" || (!traditional && type == "object"))
	        serialize(params, value, traditional, key)
	      else params.add(key, value)
	    })
	  }

	  $.param = function(obj, traditional){
	    var params = []
	    params.add = function(key, value) {
	      if ($.isFunction(value)) value = value()
	      if (value == null) value = ""
	      this.push(escape(key) + '=' + escape(value))
	    }
	    serialize(params, obj, traditional)
	    return params.join('&').replace(/%20/g, '+')
	  }
	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($){
	  var cache = [], timeout

	  $.fn.remove = function(){
	    return this.each(function(){
	      if(this.parentNode){
	        if(this.tagName === 'IMG'){
	          cache.push(this)
	          this.src = 'data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs='
	          if (timeout) clearTimeout(timeout)
	          timeout = setTimeout(function(){ cache = [] }, 60000)
	        }
	        this.parentNode.removeChild(this)
	      }
	    })
	  }
	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($){
	  // Create a collection of callbacks to be fired in a sequence, with configurable behaviour
	  // Option flags:
	  //   - once: Callbacks fired at most one time.
	  //   - memory: Remember the most recent context and arguments
	  //   - stopOnFalse: Cease iterating over callback list
	  //   - unique: Permit adding at most one instance of the same callback
	  $.Callbacks = function(options) {
	    options = $.extend({}, options)

	    var memory, // Last fire value (for non-forgettable lists)
	        fired,  // Flag to know if list was already fired
	        firing, // Flag to know if list is currently firing
	        firingStart, // First callback to fire (used internally by add and fireWith)
	        firingLength, // End of the loop when firing
	        firingIndex, // Index of currently firing callback (modified by remove if needed)
	        list = [], // Actual callback list
	        stack = !options.once && [], // Stack of fire calls for repeatable lists
	        fire = function(data) {
	          memory = options.memory && data
	          fired = true
	          firingIndex = firingStart || 0
	          firingStart = 0
	          firingLength = list.length
	          firing = true
	          for ( ; list && firingIndex < firingLength ; ++firingIndex ) {
	            if (list[firingIndex].apply(data[0], data[1]) === false && options.stopOnFalse) {
	              memory = false
	              break
	            }
	          }
	          firing = false
	          if (list) {
	            if (stack) stack.length && fire(stack.shift())
	            else if (memory) list.length = 0
	            else Callbacks.disable()
	          }
	        },

	        Callbacks = {
	          add: function() {
	            if (list) {
	              var start = list.length,
	                  add = function(args) {
	                    $.each(args, function(_, arg){
	                      if (typeof arg === "function") {
	                        if (!options.unique || !Callbacks.has(arg)) list.push(arg)
	                      }
	                      else if (arg && arg.length && typeof arg !== 'string') add(arg)
	                    })
	                  }
	              add(arguments)
	              if (firing) firingLength = list.length
	              else if (memory) {
	                firingStart = start
	                fire(memory)
	              }
	            }
	            return this
	          },
	          remove: function() {
	            if (list) {
	              $.each(arguments, function(_, arg){
	                var index
	                while ((index = $.inArray(arg, list, index)) > -1) {
	                  list.splice(index, 1)
	                  // Handle firing indexes
	                  if (firing) {
	                    if (index <= firingLength) --firingLength
	                    if (index <= firingIndex) --firingIndex
	                  }
	                }
	              })
	            }
	            return this
	          },
	          has: function(fn) {
	            return !!(list && (fn ? $.inArray(fn, list) > -1 : list.length))
	          },
	          empty: function() {
	            firingLength = list.length = 0
	            return this
	          },
	          disable: function() {
	            list = stack = memory = undefined
	            return this
	          },
	          disabled: function() {
	            return !list
	          },
	          lock: function() {
	            stack = undefined;
	            if (!memory) Callbacks.disable()
	            return this
	          },
	          locked: function() {
	            return !stack
	          },
	          fireWith: function(context, args) {
	            if (list && (!fired || stack)) {
	              args = args || []
	              args = [context, args.slice ? args.slice() : args]
	              if (firing) stack.push(args)
	              else fire(args)
	            }
	            return this
	          },
	          fire: function() {
	            return Callbacks.fireWith(this, arguments)
	          },
	          fired: function() {
	            return !!fired
	          }
	        }

	    return Callbacks
	  }
	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	// The following code is heavily inspired by jQuery's $.fn.data()

	;(function($){
	  var data = {}, dataAttr = $.fn.data, camelize = $.camelCase,
	    exp = $.expando = 'Zepto' + (+new Date()), emptyArray = []

	  // Get value from node:
	  // 1. first try key as given,
	  // 2. then try camelized key,
	  // 3. fall back to reading "data-*" attribute.
	  function getData(node, name) {
	    var id = node[exp], store = id && data[id]
	    if (name === undefined) return store || setData(node)
	    else {
	      if (store) {
	        if (name in store) return store[name]
	        var camelName = camelize(name)
	        if (camelName in store) return store[camelName]
	      }
	      return dataAttr.call($(node), name)
	    }
	  }

	  // Store value under camelized key on node
	  function setData(node, name, value) {
	    var id = node[exp] || (node[exp] = ++$.uuid),
	      store = data[id] || (data[id] = attributeData(node))
	    if (name !== undefined) store[camelize(name)] = value
	    return store
	  }

	  // Read all "data-*" attributes from a node
	  function attributeData(node) {
	    var store = {}
	    $.each(node.attributes || emptyArray, function(i, attr){
	      if (attr.name.indexOf('data-') == 0)
	        store[camelize(attr.name.replace('data-', ''))] =
	          $.zepto.deserializeValue(attr.value)
	    })
	    return store
	  }

	  $.fn.data = function(name, value) {
	    return value === undefined ?
	      // set multiple values via object
	      $.isPlainObject(name) ?
	        this.each(function(i, node){
	          $.each(name, function(key, value){ setData(node, key, value) })
	        }) :
	        // get value from first element
	        (0 in this ? getData(this[0], name) : undefined) :
	      // set value on all elements
	      this.each(function(){ setData(this, name, value) })
	  }

	  $.fn.removeData = function(names) {
	    if (typeof names == 'string') names = names.split(/\s+/)
	    return this.each(function(){
	      var id = this[exp], store = id && data[id]
	      if (store) $.each(names || store, function(key){
	        delete store[names ? camelize(this) : key]
	      })
	    })
	  }

	  // Generate extended `remove` and `empty` functions
	  ;['remove', 'empty'].forEach(function(methodName){
	    var origFn = $.fn[methodName]
	    $.fn[methodName] = function() {
	      var elements = this.find('*')
	      if (methodName === 'remove') elements = elements.add(this)
	      elements.removeData()
	      return origFn.call(this)
	    }
	  })
	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.
	//
	//     Some code (c) 2005, 2013 jQuery Foundation, Inc. and other contributors

	;(function($){
	  var slice = Array.prototype.slice

	  function Deferred(func) {
	    var tuples = [
	          // action, add listener, listener list, final state
	          [ "resolve", "done", $.Callbacks({once:1, memory:1}), "resolved" ],
	          [ "reject", "fail", $.Callbacks({once:1, memory:1}), "rejected" ],
	          [ "notify", "progress", $.Callbacks({memory:1}) ]
	        ],
	        state = "pending",
	        promise = {
	          state: function() {
	            return state
	          },
	          always: function() {
	            deferred.done(arguments).fail(arguments)
	            return this
	          },
	          then: function(/* fnDone [, fnFailed [, fnProgress]] */) {
	            var fns = arguments
	            return Deferred(function(defer){
	              $.each(tuples, function(i, tuple){
	                var fn = $.isFunction(fns[i]) && fns[i]
	                deferred[tuple[1]](function(){
	                  var returned = fn && fn.apply(this, arguments)
	                  if (returned && $.isFunction(returned.promise)) {
	                    returned.promise()
	                      .done(defer.resolve)
	                      .fail(defer.reject)
	                      .progress(defer.notify)
	                  } else {
	                    var context = this === promise ? defer.promise() : this,
	                        values = fn ? [returned] : arguments
	                    defer[tuple[0] + "With"](context, values)
	                  }
	                })
	              })
	              fns = null
	            }).promise()
	          },

	          promise: function(obj) {
	            return obj != null ? $.extend( obj, promise ) : promise
	          }
	        },
	        deferred = {}

	    $.each(tuples, function(i, tuple){
	      var list = tuple[2],
	          stateString = tuple[3]

	      promise[tuple[1]] = list.add

	      if (stateString) {
	        list.add(function(){
	          state = stateString
	        }, tuples[i^1][2].disable, tuples[2][2].lock)
	      }

	      deferred[tuple[0]] = function(){
	        deferred[tuple[0] + "With"](this === deferred ? promise : this, arguments)
	        return this
	      }
	      deferred[tuple[0] + "With"] = list.fireWith
	    })

	    promise.promise(deferred)
	    if (func) func.call(deferred, deferred)
	    return deferred
	  }

	  $.when = function(sub) {
	    var resolveValues = slice.call(arguments),
	        len = resolveValues.length,
	        i = 0,
	        remain = len !== 1 || (sub && $.isFunction(sub.promise)) ? len : 0,
	        deferred = remain === 1 ? sub : Deferred(),
	        progressValues, progressContexts, resolveContexts,
	        updateFn = function(i, ctx, val){
	          return function(value){
	            ctx[i] = this
	            val[i] = arguments.length > 1 ? slice.call(arguments) : value
	            if (val === progressValues) {
	              deferred.notifyWith(ctx, val)
	            } else if (!(--remain)) {
	              deferred.resolveWith(ctx, val)
	            }
	          }
	        }

	    if (len > 1) {
	      progressValues = new Array(len)
	      progressContexts = new Array(len)
	      resolveContexts = new Array(len)
	      for ( ; i < len; ++i ) {
	        if (resolveValues[i] && $.isFunction(resolveValues[i].promise)) {
	          resolveValues[i].promise()
	            .done(updateFn(i, resolveContexts, resolveValues))
	            .fail(deferred.reject)
	            .progress(updateFn(i, progressContexts, progressValues))
	        } else {
	          --remain
	        }
	      }
	    }
	    if (!remain) deferred.resolveWith(resolveContexts, resolveValues)
	    return deferred.promise()
	  }

	  $.Deferred = Deferred
	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($){
	  function detect(ua, platform){
	    var os = this.os = {}, browser = this.browser = {},
	      webkit = ua.match(/Web[kK]it[\/]{0,1}([\d.]+)/),
	      android = ua.match(/(Android);?[\s\/]+([\d.]+)?/),
	      osx = !!ua.match(/\(Macintosh\; Intel /),
	      ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
	      ipod = ua.match(/(iPod)(.*OS\s([\d_]+))?/),
	      iphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
	      webos = ua.match(/(webOS|hpwOS)[\s\/]([\d.]+)/),
	      win = /Win\d{2}|Windows/.test(platform),
	      wp = ua.match(/Windows Phone ([\d.]+)/),
	      touchpad = webos && ua.match(/TouchPad/),
	      kindle = ua.match(/Kindle\/([\d.]+)/),
	      silk = ua.match(/Silk\/([\d._]+)/),
	      blackberry = ua.match(/(BlackBerry).*Version\/([\d.]+)/),
	      bb10 = ua.match(/(BB10).*Version\/([\d.]+)/),
	      rimtabletos = ua.match(/(RIM\sTablet\sOS)\s([\d.]+)/),
	      playbook = ua.match(/PlayBook/),
	      chrome = ua.match(/Chrome\/([\d.]+)/) || ua.match(/CriOS\/([\d.]+)/),
	      firefox = ua.match(/Firefox\/([\d.]+)/),
	      firefoxos = ua.match(/\((?:Mobile|Tablet); rv:([\d.]+)\).*Firefox\/[\d.]+/),
	      ie = ua.match(/MSIE\s([\d.]+)/) || ua.match(/Trident\/[\d](?=[^\?]+).*rv:([0-9.].)/),
	      webview = !chrome && ua.match(/(iPhone|iPod|iPad).*AppleWebKit(?!.*Safari)/),
	      safari = webview || ua.match(/Version\/([\d.]+)([^S](Safari)|[^M]*(Mobile)[^S]*(Safari))/)

	    // Todo: clean this up with a better OS/browser seperation:
	    // - discern (more) between multiple browsers on android
	    // - decide if kindle fire in silk mode is android or not
	    // - Firefox on Android doesn't specify the Android version
	    // - possibly devide in os, device and browser hashes

	    if (browser.webkit = !!webkit) browser.version = webkit[1]

	    if (android) os.android = true, os.version = android[2]
	    if (iphone && !ipod) os.ios = os.iphone = true, os.version = iphone[2].replace(/_/g, '.')
	    if (ipad) os.ios = os.ipad = true, os.version = ipad[2].replace(/_/g, '.')
	    if (ipod) os.ios = os.ipod = true, os.version = ipod[3] ? ipod[3].replace(/_/g, '.') : null
	    if (wp) os.wp = true, os.version = wp[1]
	    if (webos) os.webos = true, os.version = webos[2]
	    if (touchpad) os.touchpad = true
	    if (blackberry) os.blackberry = true, os.version = blackberry[2]
	    if (bb10) os.bb10 = true, os.version = bb10[2]
	    if (rimtabletos) os.rimtabletos = true, os.version = rimtabletos[2]
	    if (playbook) browser.playbook = true
	    if (kindle) os.kindle = true, os.version = kindle[1]
	    if (silk) browser.silk = true, browser.version = silk[1]
	    if (!silk && os.android && ua.match(/Kindle Fire/)) browser.silk = true
	    if (chrome) browser.chrome = true, browser.version = chrome[1]
	    if (firefox) browser.firefox = true, browser.version = firefox[1]
	    if (firefoxos) os.firefoxos = true, os.version = firefoxos[1]
	    if (ie) browser.ie = true, browser.version = ie[1]
	    if (safari && (osx || os.ios || win)) {
	      browser.safari = true
	      if (!os.ios) browser.version = safari[1]
	    }
	    if (webview) browser.webview = true

	    os.tablet = !!(ipad || playbook || (android && !ua.match(/Mobile/)) ||
	      (firefox && ua.match(/Tablet/)) || (ie && !ua.match(/Phone/) && ua.match(/Touch/)))
	    os.phone  = !!(!os.tablet && !os.ipod && (android || iphone || webos || blackberry || bb10 ||
	      (chrome && ua.match(/Android/)) || (chrome && ua.match(/CriOS\/([\d.]+)/)) ||
	      (firefox && ua.match(/Mobile/)) || (ie && ua.match(/Touch/))))
	  }

	  detect.call($, navigator.userAgent, navigator.platform)
	  // make available to unit tests
	  $.__detect = detect

	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($){
	  var _zid = 1, undefined,
	      slice = Array.prototype.slice,
	      isFunction = $.isFunction,
	      isString = function(obj){ return typeof obj == 'string' },
	      handlers = {},
	      specialEvents={},
	      focusinSupported = 'onfocusin' in window,
	      focus = { focus: 'focusin', blur: 'focusout' },
	      hover = { mouseenter: 'mouseover', mouseleave: 'mouseout' }

	  specialEvents.click = specialEvents.mousedown = specialEvents.mouseup = specialEvents.mousemove = 'MouseEvents'

	  function zid(element) {
	    return element._zid || (element._zid = _zid++)
	  }
	  function findHandlers(element, event, fn, selector) {
	    event = parse(event)
	    if (event.ns) var matcher = matcherFor(event.ns)
	    return (handlers[zid(element)] || []).filter(function(handler) {
	      return handler
	        && (!event.e  || handler.e == event.e)
	        && (!event.ns || matcher.test(handler.ns))
	        && (!fn       || zid(handler.fn) === zid(fn))
	        && (!selector || handler.sel == selector)
	    })
	  }
	  function parse(event) {
	    var parts = ('' + event).split('.')
	    return {e: parts[0], ns: parts.slice(1).sort().join(' ')}
	  }
	  function matcherFor(ns) {
	    return new RegExp('(?:^| )' + ns.replace(' ', ' .* ?') + '(?: |$)')
	  }

	  function eventCapture(handler, captureSetting) {
	    return handler.del &&
	      (!focusinSupported && (handler.e in focus)) ||
	      !!captureSetting
	  }

	  function realEvent(type) {
	    return hover[type] || (focusinSupported && focus[type]) || type
	  }

	  function add(element, events, fn, data, selector, delegator, capture){
	    var id = zid(element), set = (handlers[id] || (handlers[id] = []))
	    events.split(/\s/).forEach(function(event){
	      if (event == 'ready') return $(document).ready(fn)
	      var handler   = parse(event)
	      handler.fn    = fn
	      handler.sel   = selector
	      // emulate mouseenter, mouseleave
	      if (handler.e in hover) fn = function(e){
	        var related = e.relatedTarget
	        if (!related || (related !== this && !$.contains(this, related)))
	          return handler.fn.apply(this, arguments)
	      }
	      handler.del   = delegator
	      var callback  = delegator || fn
	      handler.proxy = function(e){
	        e = compatible(e)
	        if (e.isImmediatePropagationStopped()) return
	        e.data = data
	        var result = callback.apply(element, e._args == undefined ? [e] : [e].concat(e._args))
	        if (result === false) e.preventDefault(), e.stopPropagation()
	        return result
	      }
	      handler.i = set.length
	      set.push(handler)
	      if ('addEventListener' in element)
	        element.addEventListener(realEvent(handler.e), handler.proxy, eventCapture(handler, capture))
	    })
	  }
	  function remove(element, events, fn, selector, capture){
	    var id = zid(element)
	    ;(events || '').split(/\s/).forEach(function(event){
	      findHandlers(element, event, fn, selector).forEach(function(handler){
	        delete handlers[id][handler.i]
	      if ('removeEventListener' in element)
	        element.removeEventListener(realEvent(handler.e), handler.proxy, eventCapture(handler, capture))
	      })
	    })
	  }

	  $.event = { add: add, remove: remove }

	  $.proxy = function(fn, context) {
	    var args = (2 in arguments) && slice.call(arguments, 2)
	    if (isFunction(fn)) {
	      var proxyFn = function(){ return fn.apply(context, args ? args.concat(slice.call(arguments)) : arguments) }
	      proxyFn._zid = zid(fn)
	      return proxyFn
	    } else if (isString(context)) {
	      if (args) {
	        args.unshift(fn[context], fn)
	        return $.proxy.apply(null, args)
	      } else {
	        return $.proxy(fn[context], fn)
	      }
	    } else {
	      throw new TypeError("expected function")
	    }
	  }

	  $.fn.bind = function(event, data, callback){
	    return this.on(event, data, callback)
	  }
	  $.fn.unbind = function(event, callback){
	    return this.off(event, callback)
	  }
	  $.fn.one = function(event, selector, data, callback){
	    return this.on(event, selector, data, callback, 1)
	  }

	  var returnTrue = function(){return true},
	      returnFalse = function(){return false},
	      ignoreProperties = /^([A-Z]|returnValue$|layer[XY]$)/,
	      eventMethods = {
	        preventDefault: 'isDefaultPrevented',
	        stopImmediatePropagation: 'isImmediatePropagationStopped',
	        stopPropagation: 'isPropagationStopped'
	      }

	  function compatible(event, source) {
	    if (source || !event.isDefaultPrevented) {
	      source || (source = event)

	      $.each(eventMethods, function(name, predicate) {
	        var sourceMethod = source[name]
	        event[name] = function(){
	          this[predicate] = returnTrue
	          return sourceMethod && sourceMethod.apply(source, arguments)
	        }
	        event[predicate] = returnFalse
	      })

	      if (source.defaultPrevented !== undefined ? source.defaultPrevented :
	          'returnValue' in source ? source.returnValue === false :
	          source.getPreventDefault && source.getPreventDefault())
	        event.isDefaultPrevented = returnTrue
	    }
	    return event
	  }

	  function createProxy(event) {
	    var key, proxy = { originalEvent: event }
	    for (key in event)
	      if (!ignoreProperties.test(key) && event[key] !== undefined) proxy[key] = event[key]

	    return compatible(proxy, event)
	  }

	  $.fn.delegate = function(selector, event, callback){
	    return this.on(event, selector, callback)
	  }
	  $.fn.undelegate = function(selector, event, callback){
	    return this.off(event, selector, callback)
	  }

	  $.fn.live = function(event, callback){
	    $(document.body).delegate(this.selector, event, callback)
	    return this
	  }
	  $.fn.die = function(event, callback){
	    $(document.body).undelegate(this.selector, event, callback)
	    return this
	  }

	  $.fn.on = function(event, selector, data, callback, one){
	    var autoRemove, delegator, $this = this
	    if (event && !isString(event)) {
	      $.each(event, function(type, fn){
	        $this.on(type, selector, data, fn, one)
	      })
	      return $this
	    }

	    if (!isString(selector) && !isFunction(callback) && callback !== false)
	      callback = data, data = selector, selector = undefined
	    if (callback === undefined || data === false)
	      callback = data, data = undefined

	    if (callback === false) callback = returnFalse

	    return $this.each(function(_, element){
	      if (one) autoRemove = function(e){
	        remove(element, e.type, callback)
	        return callback.apply(this, arguments)
	      }

	      if (selector) delegator = function(e){
	        var evt, match = $(e.target).closest(selector, element).get(0)
	        if (match && match !== element) {
	          evt = $.extend(createProxy(e), {currentTarget: match, liveFired: element})
	          return (autoRemove || callback).apply(match, [evt].concat(slice.call(arguments, 1)))
	        }
	      }

	      add(element, event, callback, data, selector, delegator || autoRemove)
	    })
	  }
	  $.fn.off = function(event, selector, callback){
	    var $this = this
	    if (event && !isString(event)) {
	      $.each(event, function(type, fn){
	        $this.off(type, selector, fn)
	      })
	      return $this
	    }

	    if (!isString(selector) && !isFunction(callback) && callback !== false)
	      callback = selector, selector = undefined

	    if (callback === false) callback = returnFalse

	    return $this.each(function(){
	      remove(this, event, callback, selector)
	    })
	  }

	  $.fn.trigger = function(event, args){
	    event = (isString(event) || $.isPlainObject(event)) ? $.Event(event) : compatible(event)
	    event._args = args
	    return this.each(function(){
	      // handle focus(), blur() by calling them directly
	      if (event.type in focus && typeof this[event.type] == "function") this[event.type]()
	      // items in the collection might not be DOM elements
	      else if ('dispatchEvent' in this) this.dispatchEvent(event)
	      else $(this).triggerHandler(event, args)
	    })
	  }

	  // triggers event handlers on current element just as if an event occurred,
	  // doesn't trigger an actual event, doesn't bubble
	  $.fn.triggerHandler = function(event, args){
	    var e, result
	    this.each(function(i, element){
	      e = createProxy(isString(event) ? $.Event(event) : event)
	      e._args = args
	      e.target = element
	      $.each(findHandlers(element, event.type || event), function(i, handler){
	        result = handler.proxy(e)
	        if (e.isImmediatePropagationStopped()) return false
	      })
	    })
	    return result
	  }

	  // shortcut methods for `.bind(event, fn)` for each event type
	  ;('focusin focusout focus blur load resize scroll unload click dblclick '+
	  'mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave '+
	  'change select keydown keypress keyup error').split(' ').forEach(function(event) {
	    $.fn[event] = function(callback) {
	      return (0 in arguments) ?
	        this.bind(event, callback) :
	        this.trigger(event)
	    }
	  })

	  $.Event = function(type, props) {
	    if (!isString(type)) props = type, type = props.type
	    var event = document.createEvent(specialEvents[type] || 'Events'), bubbles = true
	    if (props) for (var name in props) (name == 'bubbles') ? (bubbles = !!props[name]) : (event[name] = props[name])
	    event.initEvent(type, bubbles, true)
	    return compatible(event)
	  }

	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($){
	  $.fn.serializeArray = function() {
	    var name, type, result = [],
	      add = function(value) {
	        if (value.forEach) return value.forEach(add)
	        result.push({ name: name, value: value })
	      }
	    if (this[0]) $.each(this[0].elements, function(_, field){
	      type = field.type, name = field.name
	      if (name && field.nodeName.toLowerCase() != 'fieldset' &&
	        !field.disabled && type != 'submit' && type != 'reset' && type != 'button' && type != 'file' &&
	        ((type != 'radio' && type != 'checkbox') || field.checked))
	          add($(field).val())
	    })
	    return result
	  }

	  $.fn.serialize = function(){
	    var result = []
	    this.serializeArray().forEach(function(elm){
	      result.push(encodeURIComponent(elm.name) + '=' + encodeURIComponent(elm.value))
	    })
	    return result.join('&')
	  }

	  $.fn.submit = function(callback) {
	    if (0 in arguments) this.bind('submit', callback)
	    else if (this.length) {
	      var event = $.Event('submit')
	      this.eq(0).trigger(event)
	      if (!event.isDefaultPrevented()) this.get(0).submit()
	    }
	    return this
	  }

	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($, undefined){
	  var prefix = '', eventPrefix,
	    vendors = { Webkit: 'webkit', Moz: '', O: 'o' },
	    testEl = document.createElement('div'),
	    supportedTransforms = /^((translate|rotate|scale)(X|Y|Z|3d)?|matrix(3d)?|perspective|skew(X|Y)?)$/i,
	    transform,
	    transitionProperty, transitionDuration, transitionTiming, transitionDelay,
	    animationName, animationDuration, animationTiming, animationDelay,
	    cssReset = {}

	  function dasherize(str) { return str.replace(/([a-z])([A-Z])/, '$1-$2').toLowerCase() }
	  function normalizeEvent(name) { return eventPrefix ? eventPrefix + name : name.toLowerCase() }

	  $.each(vendors, function(vendor, event){
	    if (testEl.style[vendor + 'TransitionProperty'] !== undefined) {
	      prefix = '-' + vendor.toLowerCase() + '-'
	      eventPrefix = event
	      return false
	    }
	  })

	  transform = prefix + 'transform'
	  cssReset[transitionProperty = prefix + 'transition-property'] =
	  cssReset[transitionDuration = prefix + 'transition-duration'] =
	  cssReset[transitionDelay    = prefix + 'transition-delay'] =
	  cssReset[transitionTiming   = prefix + 'transition-timing-function'] =
	  cssReset[animationName      = prefix + 'animation-name'] =
	  cssReset[animationDuration  = prefix + 'animation-duration'] =
	  cssReset[animationDelay     = prefix + 'animation-delay'] =
	  cssReset[animationTiming    = prefix + 'animation-timing-function'] = ''

	  $.fx = {
	    off: (eventPrefix === undefined && testEl.style.transitionProperty === undefined),
	    speeds: { _default: 400, fast: 200, slow: 600 },
	    cssPrefix: prefix,
	    transitionEnd: normalizeEvent('TransitionEnd'),
	    animationEnd: normalizeEvent('AnimationEnd')
	  }

	  $.fn.animate = function(properties, duration, ease, callback, delay){
	    if ($.isFunction(duration))
	      callback = duration, ease = undefined, duration = undefined
	    if ($.isFunction(ease))
	      callback = ease, ease = undefined
	    if ($.isPlainObject(duration))
	      ease = duration.easing, callback = duration.complete, delay = duration.delay, duration = duration.duration
	    if (duration) duration = (typeof duration == 'number' ? duration :
	                    ($.fx.speeds[duration] || $.fx.speeds._default)) / 1000
	    if (delay) delay = parseFloat(delay) / 1000
	    return this.anim(properties, duration, ease, callback, delay)
	  }

	  $.fn.anim = function(properties, duration, ease, callback, delay){
	    var key, cssValues = {}, cssProperties, transforms = '',
	        that = this, wrappedCallback, endEvent = $.fx.transitionEnd,
	        fired = false

	    if (duration === undefined) duration = $.fx.speeds._default / 1000
	    if (delay === undefined) delay = 0
	    if ($.fx.off) duration = 0

	    if (typeof properties == 'string') {
	      // keyframe animation
	      cssValues[animationName] = properties
	      cssValues[animationDuration] = duration + 's'
	      cssValues[animationDelay] = delay + 's'
	      cssValues[animationTiming] = (ease || 'linear')
	      endEvent = $.fx.animationEnd
	    } else {
	      cssProperties = []
	      // CSS transitions
	      for (key in properties)
	        if (supportedTransforms.test(key)) transforms += key + '(' + properties[key] + ') '
	        else cssValues[key] = properties[key], cssProperties.push(dasherize(key))

	      if (transforms) cssValues[transform] = transforms, cssProperties.push(transform)
	      if (duration > 0 && typeof properties === 'object') {
	        cssValues[transitionProperty] = cssProperties.join(', ')
	        cssValues[transitionDuration] = duration + 's'
	        cssValues[transitionDelay] = delay + 's'
	        cssValues[transitionTiming] = (ease || 'linear')
	      }
	    }

	    wrappedCallback = function(event){
	      if (typeof event !== 'undefined') {
	        if (event.target !== event.currentTarget) return // makes sure the event didn't bubble from "below"
	        $(event.target).unbind(endEvent, wrappedCallback)
	      } else
	        $(this).unbind(endEvent, wrappedCallback) // triggered by setTimeout

	      fired = true
	      $(this).css(cssReset)
	      callback && callback.call(this)
	    }
	    if (duration > 0){
	      this.bind(endEvent, wrappedCallback)
	      // transitionEnd is not always firing on older Android phones
	      // so make sure it gets fired
	      setTimeout(function(){
	        if (fired) return
	        wrappedCallback.call(that)
	      }, ((duration + delay) * 1000) + 25)
	    }

	    // trigger page reflow so new elements can animate
	    this.size() && this.get(0).clientLeft

	    this.css(cssValues)

	    if (duration <= 0) setTimeout(function() {
	      that.each(function(){ wrappedCallback.call(this) })
	    }, 0)

	    return this
	  }

	  testEl = null
	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($, undefined){
	  var document = window.document, docElem = document.documentElement,
	    origShow = $.fn.show, origHide = $.fn.hide, origToggle = $.fn.toggle

	  function anim(el, speed, opacity, scale, callback) {
	    if (typeof speed == 'function' && !callback) callback = speed, speed = undefined
	    var props = { opacity: opacity }
	    if (scale) {
	      props.scale = scale
	      el.css($.fx.cssPrefix + 'transform-origin', '0 0')
	    }
	    return el.animate(props, speed, null, callback)
	  }

	  function hide(el, speed, scale, callback) {
	    return anim(el, speed, 0, scale, function(){
	      origHide.call($(this))
	      callback && callback.call(this)
	    })
	  }

	  $.fn.show = function(speed, callback) {
	    origShow.call(this)
	    if (speed === undefined) speed = 0
	    else this.css('opacity', 0)
	    return anim(this, speed, 1, '1,1', callback)
	  }

	  $.fn.hide = function(speed, callback) {
	    if (speed === undefined) return origHide.call(this)
	    else return hide(this, speed, '0,0', callback)
	  }

	  $.fn.toggle = function(speed, callback) {
	    if (speed === undefined || typeof speed == 'boolean')
	      return origToggle.call(this, speed)
	    else return this.each(function(){
	      var el = $(this)
	      el[el.css('display') == 'none' ? 'show' : 'hide'](speed, callback)
	    })
	  }

	  $.fn.fadeTo = function(speed, opacity, callback) {
	    return anim(this, speed, opacity, null, callback)
	  }

	  $.fn.fadeIn = function(speed, callback) {
	    var target = this.css('opacity')
	    if (target > 0) this.css('opacity', 0)
	    else target = 1
	    return origShow.call(this).fadeTo(speed, target, callback)
	  }

	  $.fn.fadeOut = function(speed, callback) {
	    return hide(this, speed, null, callback)
	  }

	  $.fn.fadeToggle = function(speed, callback) {
	    return this.each(function(){
	      var el = $(this)
	      el[
	        (el.css('opacity') == 0 || el.css('display') == 'none') ? 'fadeIn' : 'fadeOut'
	      ](speed, callback)
	    })
	  }

	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($){
	  if ($.os.ios) {
	    var gesture = {}, gestureTimeout

	    function parentIfText(node){
	      return 'tagName' in node ? node : node.parentNode
	    }

	    $(document).bind('gesturestart', function(e){
	      var now = Date.now(), delta = now - (gesture.last || now)
	      gesture.target = parentIfText(e.target)
	      gestureTimeout && clearTimeout(gestureTimeout)
	      gesture.e1 = e.scale
	      gesture.last = now
	    }).bind('gesturechange', function(e){
	      gesture.e2 = e.scale
	    }).bind('gestureend', function(e){
	      if (gesture.e2 > 0) {
	        Math.abs(gesture.e1 - gesture.e2) != 0 && $(gesture.target).trigger('pinch') &&
	          $(gesture.target).trigger('pinch' + (gesture.e1 - gesture.e2 > 0 ? 'In' : 'Out'))
	        gesture.e1 = gesture.e2 = gesture.last = 0
	      } else if ('last' in gesture) {
	        gesture = {}
	      }
	    })

	    ;['pinch', 'pinchIn', 'pinchOut'].forEach(function(m){
	      $.fn[m] = function(callback){ return this.bind(m, callback) }
	    })
	  }
	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function(undefined){
	  if (String.prototype.trim === undefined) // fix for iOS 3.2
	    String.prototype.trim = function(){ return this.replace(/^\s+|\s+$/g, '') }

	  // For iOS 3.x
	  // from https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Array/reduce
	  if (Array.prototype.reduce === undefined)
	    Array.prototype.reduce = function(fun){
	      if(this === void 0 || this === null) throw new TypeError()
	      var t = Object(this), len = t.length >>> 0, k = 0, accumulator
	      if(typeof fun != 'function') throw new TypeError()
	      if(len == 0 && arguments.length == 1) throw new TypeError()

	      if(arguments.length >= 2)
	       accumulator = arguments[1]
	      else
	        do{
	          if(k in t){
	            accumulator = t[k++]
	            break
	          }
	          if(++k >= len) throw new TypeError()
	        } while (true)

	      while (k < len){
	        if(k in t) accumulator = fun.call(undefined, accumulator, t[k], k, t)
	        k++
	      }
	      return accumulator
	    }

	})()
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($){
	  var zepto = $.zepto, oldQsa = zepto.qsa, oldMatches = zepto.matches

	  function visible(elem){
	    elem = $(elem)
	    return !!(elem.width() || elem.height()) && elem.css("display") !== "none"
	  }

	  // Implements a subset from:
	  // http://api.jquery.com/category/selectors/jquery-selector-extensions/
	  //
	  // Each filter function receives the current index, all nodes in the
	  // considered set, and a value if there were parentheses. The value
	  // of `this` is the node currently being considered. The function returns the
	  // resulting node(s), null, or undefined.
	  //
	  // Complex selectors are not supported:
	  //   li:has(label:contains("foo")) + li:has(label:contains("bar"))
	  //   ul.inner:first > li
	  var filters = $.expr[':'] = {
	    visible:  function(){ if (visible(this)) return this },
	    hidden:   function(){ if (!visible(this)) return this },
	    selected: function(){ if (this.selected) return this },
	    checked:  function(){ if (this.checked) return this },
	    parent:   function(){ return this.parentNode },
	    first:    function(idx){ if (idx === 0) return this },
	    last:     function(idx, nodes){ if (idx === nodes.length - 1) return this },
	    eq:       function(idx, _, value){ if (idx === value) return this },
	    contains: function(idx, _, text){ if ($(this).text().indexOf(text) > -1) return this },
	    has:      function(idx, _, sel){ if (zepto.qsa(this, sel).length) return this }
	  }

	  var filterRe = new RegExp('(.*):(\\w+)(?:\\(([^)]+)\\))?$\\s*'),
	      childRe  = /^\s*>/,
	      classTag = 'Zepto' + (+new Date())

	  function process(sel, fn) {
	    // quote the hash in `a[href^=#]` expression
	    sel = sel.replace(/=#\]/g, '="#"]')
	    var filter, arg, match = filterRe.exec(sel)
	    if (match && match[2] in filters) {
	      filter = filters[match[2]], arg = match[3]
	      sel = match[1]
	      if (arg) {
	        var num = Number(arg)
	        if (isNaN(num)) arg = arg.replace(/^["']|["']$/g, '')
	        else arg = num
	      }
	    }
	    return fn(sel, filter, arg)
	  }

	  zepto.qsa = function(node, selector) {
	    return process(selector, function(sel, filter, arg){
	      try {
	        var taggedParent
	        if (!sel && filter) sel = '*'
	        else if (childRe.test(sel))
	          // support "> *" child queries by tagging the parent node with a
	          // unique class and prepending that classname onto the selector
	          taggedParent = $(node).addClass(classTag), sel = '.'+classTag+' '+sel

	        var nodes = oldQsa(node, sel)
	      } catch(e) {
	        console.error('error performing selector: %o', selector)
	        throw e
	      } finally {
	        if (taggedParent) taggedParent.removeClass(classTag)
	      }
	      return !filter ? nodes :
	        zepto.uniq($.map(nodes, function(n, i){ return filter.call(n, i, nodes, arg) }))
	    })
	  }

	  zepto.matches = function(node, selector){
	    return process(selector, function(sel, filter, arg){
	      return (!sel || oldMatches(node, sel)) &&
	        (!filter || filter.call(node, null, arg) === node)
	    })
	  }
	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($){
	  $.fn.end = function(){
	    return this.prevObject || $()
	  }

	  $.fn.andSelf = function(){
	    return this.add(this.prevObject || $())
	  }

	  'filter,add,not,eq,first,last,find,closest,parents,parent,children,siblings'.split(',').forEach(function(property){
	    var fn = $.fn[property]
	    $.fn[property] = function(){
	      var ret = fn.apply(this, arguments)
	      ret.prevObject = this
	      return ret
	    }
	  })
	})(Zepto)
	//     Zepto.js
	//     (c) 2010-2016 Thomas Fuchs
	//     Zepto.js may be freely distributed under the MIT license.

	;(function($){
	  var touch = {},
	    touchTimeout, tapTimeout, swipeTimeout, longTapTimeout,
	    longTapDelay = 750,
	    gesture

	  function swipeDirection(x1, x2, y1, y2) {
	    return Math.abs(x1 - x2) >=
	      Math.abs(y1 - y2) ? (x1 - x2 > 0 ? 'Left' : 'Right') : (y1 - y2 > 0 ? 'Up' : 'Down')
	  }

	  function longTap() {
	    longTapTimeout = null
	    if (touch.last) {
	      touch.el.trigger('longTap')
	      touch = {}
	    }
	  }

	  function cancelLongTap() {
	    if (longTapTimeout) clearTimeout(longTapTimeout)
	    longTapTimeout = null
	  }

	  function cancelAll() {
	    if (touchTimeout) clearTimeout(touchTimeout)
	    if (tapTimeout) clearTimeout(tapTimeout)
	    if (swipeTimeout) clearTimeout(swipeTimeout)
	    if (longTapTimeout) clearTimeout(longTapTimeout)
	    touchTimeout = tapTimeout = swipeTimeout = longTapTimeout = null
	    touch = {}
	  }

	  function isPrimaryTouch(event){
	    return (event.pointerType == 'touch' ||
	      event.pointerType == event.MSPOINTER_TYPE_TOUCH)
	      && event.isPrimary
	  }

	  function isPointerEventType(e, type){
	    return (e.type == 'pointer'+type ||
	      e.type.toLowerCase() == 'mspointer'+type)
	  }

	  $(document).ready(function(){
	    var now, delta, deltaX = 0, deltaY = 0, firstTouch, _isPointerType

	    if ('MSGesture' in window) {
	      gesture = new MSGesture()
	      gesture.target = document.body
	    }

	    $(document)
	      .bind('MSGestureEnd', function(e){
	        var swipeDirectionFromVelocity =
	          e.velocityX > 1 ? 'Right' : e.velocityX < -1 ? 'Left' : e.velocityY > 1 ? 'Down' : e.velocityY < -1 ? 'Up' : null;
	        if (swipeDirectionFromVelocity) {
	          touch.el.trigger('swipe')
	          touch.el.trigger('swipe'+ swipeDirectionFromVelocity)
	        }
	      })
	      .on('touchstart MSPointerDown pointerdown', function(e){
	        if((_isPointerType = isPointerEventType(e, 'down')) &&
	          !isPrimaryTouch(e)) return
	        firstTouch = _isPointerType ? e : e.touches[0]
	        if (e.touches && e.touches.length === 1 && touch.x2) {
	          // Clear out touch movement data if we have it sticking around
	          // This can occur if touchcancel doesn't fire due to preventDefault, etc.
	          touch.x2 = undefined
	          touch.y2 = undefined
	        }
	        now = Date.now()
	        delta = now - (touch.last || now)
	        touch.el = $('tagName' in firstTouch.target ?
	          firstTouch.target : firstTouch.target.parentNode)
	        touchTimeout && clearTimeout(touchTimeout)
	        touch.x1 = firstTouch.pageX
	        touch.y1 = firstTouch.pageY
	        if (delta > 0 && delta <= 250) touch.isDoubleTap = true
	        touch.last = now
	        longTapTimeout = setTimeout(longTap, longTapDelay)
	        // adds the current touch contact for IE gesture recognition
	        if (gesture && _isPointerType) gesture.addPointer(e.pointerId);
	      })
	      .on('touchmove MSPointerMove pointermove', function(e){
	        if((_isPointerType = isPointerEventType(e, 'move')) &&
	          !isPrimaryTouch(e)) return
	        firstTouch = _isPointerType ? e : e.touches[0]
	        cancelLongTap()
	        touch.x2 = firstTouch.pageX
	        touch.y2 = firstTouch.pageY

	        deltaX += Math.abs(touch.x1 - touch.x2)
	        deltaY += Math.abs(touch.y1 - touch.y2)
	      })
	      .on('touchend MSPointerUp pointerup', function(e){
	        if((_isPointerType = isPointerEventType(e, 'up')) &&
	          !isPrimaryTouch(e)) return
	        cancelLongTap()

	        // swipe
	        if ((touch.x2 && Math.abs(touch.x1 - touch.x2) > 30) ||
	            (touch.y2 && Math.abs(touch.y1 - touch.y2) > 30))

	          swipeTimeout = setTimeout(function() {
	            touch.el.trigger('swipe')
	            touch.el.trigger('swipe' + (swipeDirection(touch.x1, touch.x2, touch.y1, touch.y2)))
	            touch = {}
	          }, 0)

	        // normal tap
	        else if ('last' in touch)
	          // don't fire tap when delta position changed by more than 30 pixels,
	          // for instance when moving to a point and back to origin
	          if (deltaX < 30 && deltaY < 30) {
	            // delay by one tick so we can cancel the 'tap' event if 'scroll' fires
	            // ('tap' fires before 'scroll')
	            tapTimeout = setTimeout(function() {

	              // trigger universal 'tap' with the option to cancelTouch()
	              // (cancelTouch cancels processing of single vs double taps for faster 'tap' response)
	              var event = $.Event('tap')
	              event.cancelTouch = cancelAll
	              touch.el.trigger(event)

	              // trigger double tap immediately
	              if (touch.isDoubleTap) {
	                if (touch.el) touch.el.trigger('doubleTap')
	                touch = {}
	              }

	              // trigger single tap after 250ms of inactivity
	              else {
	                touchTimeout = setTimeout(function(){
	                  touchTimeout = null
	                  if (touch.el) touch.el.trigger('singleTap')
	                  touch = {}
	                }, 250)
	              }
	            }, 0)
	          } else {
	            touch = {}
	          }
	          deltaX = deltaY = 0

	      })
	      // when the browser window loses focus,
	      // for example when a modal dialog is shown,
	      // cancel all ongoing events
	      .on('touchcancel MSPointerCancel pointercancel', cancelAll)

	    // scrolling the window indicates intention of the user
	    // to scroll, not tap or swipe, so cancel all ongoing events
	    $(window).on('scroll', cancelAll)
	  })

	  ;['swipe', 'swipeLeft', 'swipeRight', 'swipeUp', 'swipeDown',
	    'doubleTap', 'tap', 'singleTap', 'longTap'].forEach(function(eventName){
	    $.fn[eventName] = function(callback){ return this.on(eventName, callback) }
	  })
	})(Zepto)


/***/ },
/* 4 */
/***/ function(module, exports) {

	'use strict';

	(function (window, undefined) {
	    window.Router = function () {
	        var app = this;

	        app.views = [];
	        app.routers = {};
	        app.state = null;
	        app.zIndex = 10;
	        app.animation = {
	            'slideOutRight': 'page-from-center-to-right animated',
	            'slideOutLeft': 'page-from-center-to-left animated',
	            'slideInLeft': 'page-from-left-to-center animated',
	            'slideInRight': 'page-from-right-to-center animated'
	        };
	        app.stateType = {
	            'forward': 'forward',
	            'back': 'back'
	        };

	        // 缓存路由配置
	        app.route = function (state) {
	            var i = 0,
	                l = state.length;
	            for (; i < l; i++) {
	                app.routers[state[i].url] = state[i];
	            };
	            app.renderFirstPage();
	        };

	        // 监听hashchange事件
	        app.bindRoute = function () {
	            window.addEventListener('hashchange', hashChangeCallBack, false);
	        };

	        app.renderFirstPage = function () {
	            var _url = window.location.href,
	                _hash = _url.split('#')[1],
	                _router;
	            if (_hash.indexOf('?') > -1) _hash = _hash.split('?')[0];
	            _router = app.routers[_hash];
	            hashChangeCallBack({ newURL: _url });
	        };

	        function hashChangeCallBack(e) {
	            var _url = e.newURL.split('#')[1],
	                _hash = _url.split('?')[0],
	                _router,
	                params = {};

	            _router = app.routers[_hash];
	            if (_url.indexOf('?') > -1) params = getUrlParams(_url.split('?')[1]);
	            if (!_router) return;
	            // 返回
	            if (app.views.length >= 2 && app.views[app.views.length - 2].url === _hash) {
	                pushViews(_router, params);
	                return;
	            }
	            // 向前 
	            getTpl(_router, params);
	        };

	        function getUrlParams(url) {
	            var query = {},
	                strs;
	            if (url.indexOf("&") > -1) {
	                strs = url.split("&");
	                for (var i = 0; i < strs.length; i++) {
	                    query[strs[i].split("=")[0]] = strs[i].split("=")[1];
	                }
	            } else {
	                var key = url.substring(0, url.indexOf("="));
	                var value = url.substr(url.indexOf("=") + 1);
	                query[key] = decodeURI(value);
	            }
	            return query;
	        };

	        function animatePages(page, params) {
	            var currentPageAnimation = app.animation.slideOutRight,
	                nextPageAnimation = app.animation.slideInLeft,
	                currentPage = $('.current'),
	                $app = $('#app'),
	                nextPage = currentPage.length && $(currentPage[0].previousElementSibling);;

	            // 向前
	            if (page.template && app.state === app.stateType.forward) {
	                nextPage = $('<div>');
	                nextPage.addClass('page');
	                nextPage[0].innerHTML = page.template;
	                nextPage[0].id = page.url.substring(1);
	                currentPageAnimation = app.animation.slideOutLeft;
	                nextPageAnimation = app.animation.slideInRight, nextPage.css('z-index', app.zIndex + 1).addClass('current');
	                app.zIndex += 1;
	                $app.append(nextPage);
	            }

	            // 第一页
	            if (app.views.length === 1 && app.state === app.stateType.forward) {
	                $app.append(nextPage);
	                nextPage.addClass('current');
	                page.action.page = $('#' + page.url.substring(1));
	                page.action.init && page.action.init(params);
	                page.action.cordova && page.action.cordova(params);
	                return;
	            }
	            // 处理动画前 需先让表单元素去掉光标 (返回)
	            if (app.state === app.stateType.back) {
	                var _activeElm = $(document.activeElement),
	                    _nodeName = _activeElm[0].nodeName.toUpperCase();
	                if (_nodeName === 'TEXTAREA' || _nodeName === 'INPUT') {
	                    _activeElm.blur();
	                    setTimeout(function () {
	                        animationFn();
	                    }, 200);
	                    return;
	                }
	            }
	            // 处理动画
	            function animationFn() {
	                nextPage.show().addClass(nextPageAnimation);
	                currentPage.addClass(currentPageAnimation);
	                nextPage.one('webkitAnimationEnd animationEnd', function () {
	                    nextPage.removeClass(nextPageAnimation);
	                    currentPage.removeClass(currentPageAnimation);
	                    if (app.state === app.stateType.forward) {
	                        currentPage.removeClass('current').hide();
	                        page.action.page = $('#' + page.url.substring(1));
	                        page.action.init && page.action.init(params);
	                        return;
	                    }
	                    // 后退
	                    currentPage.remove();
	                    nextPage.addClass('current');
	                });
	                page.action.page = $('#' + page.url.substring(1));
	                page.action.cordova && page.action.cordova(params);
	            }
	            animationFn();
	        };

	        function getTpl(router, params) {
	            if (!router.template) throw router.url + '::获取页面失败';
	            pushViews(router, params);
	            animatePages(router, params);
	        };

	        function pushViews(page, params) {
	            var length = app.views.length;
	            if (length < 2 || app.views[length - 2].url != page.url) {
	                app.views.push(page);
	                app.state = app.stateType.forward;
	                return;
	            }
	            app.views.pop();
	            app.state = app.stateType.back;
	            animatePages(page, params);
	        };

	        app.bindRoute();
	    };
	})(window);

/***/ },
/* 5 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Constant = __webpack_require__(6),
	    sessionStorage = __webpack_require__(10);

	var workplus = {
	    getOut: function getOut() {
	        cordova.exec(function (result) {}, function (error) {
	            alert("调用失败:" + error);
	        }, "appStore", "exit", []);
	    },

	    getCurrentUserInfo: function getCurrentUserInfo(callback) {
	        cordova.exec(function (result) {
	            if (result == null) {
	                alert("获取当前用户异常");
	                return;
	            }
	            var currentUser = JSON.parse(result);
	            callback(currentUser);
	        }, function (error) {
	            callback();
	        }, "atwork", "getUserInfo", []);
	    },

	    // 获取用户ticket
	    getUserTicket: function getUserTicket(callback) {
	        cordova.exec(function (result) {
	            if (result === null) {
	                callback();
	                return;
	            }
	            var userTicket = JSON.parse(result);
	            callback(userTicket);
	        }, function (error) {
	            callback();
	        }, "atwork", "getUserTicket", []);
	    },

	    getAvatarByPhotoAlbum: function getAvatarByPhotoAlbum(callback) {
	        cordova.exec(function (result) {
	            callback(result);
	        }, function (error) {
	            alert("选择相册接口调用失败:" + error);
	            callback();
	        }, "userAvatar", "changeAvatarByPhotoAlbum", []);
	    },

	    getAvatarByTakePhoto: function getAvatarByTakePhoto(callback) {
	        cordova.exec(function (result) {
	            callback(result.success);
	        }, function (error) {
	            alert("打开摄像头接口调用失败:" + error);
	            callback();
	        }, "userAvatar", "changeAvatarByTakePhoto", []);
	    },

	    //用于清除压缩的图片
	    cleanCompressImage: function cleanCompressImage() {
	        cordova.exec(function (result) {}, function (error) {
	            console.error("清除缓存图片接口调用失败:" + error);
	        }, "associates", "cleanCompressImage", []);
	    },

	    changeTitle: function changeTitle(title) {
	        cordova.exec(function (result) {
	            console.log('修改标题成功');
	        }, function (error) {
	            console.log('修改标题失败');
	        }, "WorkPlus_WebView", "title", title);
	    },

	    bindLeftBtnEvent: function bindLeftBtnEvent(fn) {
	        cordova.exec(function (result) {
	            console.log('绑定成功');
	        }, function (error) {
	            console.log('绑定失败');
	        }, "WorkPlus_WebView", "leftButton", fn);
	    },

	    clearLeftBtnEvent: function clearLeftBtnEvent() {
	        cordova.exec(function (result) {
	            console.log('清除成功');
	        }, function (error) {
	            console.log('清除失败');
	        }, "WorkPlus_WebView", "clearLeftButton", []);
	    },

	    lockNavigation: function lockNavigation() {
	        console.log('锁定bar');
	        cordova.exec(function (result) {
	            console.log('锁定成功');
	        }, function (error) {
	            console.log('锁定失败');
	        }, "WorkPlus_WebView", "navigation", ["Lock"]);
	    },

	    unLockNavigation: function unLockNavigation() {
	        console.log('解锁bar');
	        cordova.exec(function (result) {
	            console.log('解锁成功');
	        }, function (error) {
	            console.log('解锁失败');
	        }, "WorkPlus_WebView", "navigation", ["unLock"]);
	    },

	    rightButton: function rightButton(menus) {
	        cordova.exec(function (result) {
	            console.log("调用成功");
	        }, function (error) {
	            console.log("调用失败:" + error);
	        }, "WorkPlus_WebView", "rightButtons", [menus]);
	    },

	    getIpAddress: function getIpAddress(callback, errCallback) {
	        cordova.exec(function (result) {
	            console.log("获取ip成功：" + result);
	            result = JSON.parse(result);
	            callback(result.ipAddress);
	        }, function (error) {
	            console.log("调用失败:" + error);
	            errCallback && errCallback(error);
	        }, "WorkPlus_DeviceInfo", "getIpAddress", []);
	    },

	    setRightButton: function setRightButton(buttons) {
	        workplus.clearRightButtons(function () {
	            cordova.exec(function (result) {
	                console.log("设置按钮成功");
	            }, function (error) {
	                console.log("设置按钮失败:" + error);
	            }, "WorkPlus_WebView", "rightButtons", [buttons]);
	        });
	    },

	    clearRightButtons: function clearRightButtons(callback) {
	        cordova.exec(function (result) {
	            console.log("clearRightButtons成功");
	            callback && callback();
	        }, function (error) {
	            console.log("clearRightButtons失败:" + error);
	        }, "WorkPlus_WebView", "clearRightButtons", []);
	    },

	    emptyRightButton: function emptyRightButton() {
	        workplus.clearRightButtons();
	    },

	    openQRC: function openQRC() {
	        cordova.exec(function (result) {
	            console.log('打开扫码成功');
	        }, function (error) {
	            console.log("打开扫码失败:" + error);
	        }, "WorkPlus_BarcodeScanner", "scanner", [{ "type": "native" }]);
	    },

	    getContact: function getContact(callback) {
	        cordova.exec(function (result) {
	            if (!result) callback({});
	            var _result = result;
	            if (typeof result === 'string') {
	                _result = JSON.parse(result);
	            }
	            callback(_result);
	        }, function (error) {
	            callback({});
	            console.log("获取通讯录失败:" + error);
	        }, "WorkPlus_Contact", "getMobileContacts", []);
	    },

	    AccessTokenOverdue: function AccessTokenOverdue() {
	        cordova.exec(function (result) {
	            console.log("AccessTokenOverdue");
	        }, function (error) {
	            console.log("AccessTokenOverdue:" + error);
	        }, "WorkPlus_Auth", "onAccessTokenOverdue", []);
	    },

	    jumpInApp: function jumpInApp(obj) {
	        cordova.exec(function (result) {
	            console.log(result);
	        }, function (error) {
	            alert("发生错误:" + error);
	        }, "WorkPlus_WebView", "toActivity", [{ "activity": "toMain",
	            "access_token": obj.access_token,
	            "username": obj.username,
	            "client_id": obj.client_id,
	            "next_url": obj.next_url }]);
	    },

	    getLoginUserInfo: function getLoginUserInfo(callback) {
	        cordova.exec(function (result) {
	            var _result = result;
	            if (typeof result === 'string') {
	                _result = JSON.parse(result);
	            }
	            // 将数据存入sessionStorage
	            sessionStorage.addItem(Constant.SESSION.ACCESS_TOKEN, _result.login_token.access_token);
	            sessionStorage.addItem(Constant.SESSION.CLIENT_ID, _result.login_token.client_id);
	            sessionStorage.addItem(Constant.SESSION.NICKNAME, _result.login_user.name || _result.login_user.username);
	            sessionStorage.addItem(Constant.SESSION.ACCOUTNAME, _result.login_user.username);
	            sessionStorage.addItem(Constant.SESSION.AVATAR, _result.login_user.avatar || '');
	            sessionStorage.addItem(Constant.SESSION.USER_ID, _result.login_user.user_id || '');
	            sessionStorage.addItem(Constant.SESSION.PHONE_NUMBER, _result.login_user.phone || _result.login_user.username);
	            callback && callback(_result);
	        }, function (error) {
	            console.log("getLoginUserInfo: " + error);
	        }, "WorkPlus_Auth", "getLoginUserInfo", []);
	    },
	    jumpInOrg: function jumpInOrg(orgcode) {
	        cordova.exec(function (result) {
	            console.log(result);
	        }, function (error) {
	            alert("发生错误:" + error);
	        }, "WorkPlus_WebView", "toActivity", [{ "activity": "toOrg", "orgcode": orgcode || "" }]);
	    },
	    jumpInUserPage: function jumpInUserPage(obj) {
	        cordova.exec(function (result) {
	            console.log(result);
	        }, function (error) {
	            alert("跳转失败");
	        }, "WorkPlus_PublicClound", "toPersonal", [obj]);
	    },
	    openShareBox: function openShareBox(shareData) {
	        cordova.exec(function (result) {
	            console.log(result);
	        }, function (error) {
	            alert("发生错误:" + error);
	        }, "WorkPlus_WebView", "share", [shareData]);
	    },
	    getDeviceInfo: function getDeviceInfo(callback) {
	        cordova.exec(function (result) {
	            console.log(result);
	            callback(result);
	        }, function (error) {
	            alert("发生错误:" + error);
	        }, "WorkPlus_DeviceInfo", "getDeviceInfo", []);
	    },
	    getUserInfoByUsername: function getUserInfoByUsername(obj, callback) {
	        cordova.exec(function (result) {
	            var _result = result;
	            if (typeof result === 'string') {
	                _result = JSON.parse(result);
	            }
	            callback && callback(_result);
	        }, function (error) {
	            console.log("获取用户信息失败");
	        }, "WorkPlus_Contact", "getUserInfoByUserId", [obj]);
	    },
	    getAppInfo: function getAppInfo(callback) {
	        cordova.exec(function (result) {
	            var _result = result;
	            if (typeof result === 'string') {
	                _result = JSON.parse(result);
	            }
	            callback && callback(_result);
	        }, function (error) {
	            console.log("获取用户信息失败");
	        }, "WorkPlus_PublicClound", "getAppInfo", []);
	    },
	    visibleLeftButton: function visibleLeftButton(back, close) {
	        cordova.exec(function (result) {
	            console.log("visibleLeftButton成功");
	        }, function (error) {
	            console.log("visibleLeftButton失败:" + error);
	        }, "WorkPlus_WebView", "visibleLeftButton", [{ "showBack": back, "showClose": close }]);
	    },
	    getApiConfig: function getApiConfig(callback) {
	        cordova.exec(function (result) {
	            var _result = result;
	            if (typeof result === 'string') {
	                _result = JSON.parse(result);
	            }
	            callback && callback(_result);
	        }, function (error) {
	            console("调用失败");
	        }, "WorkPlus_LightApp", "getConfig", ["_AdminUrl", "_ApiUrl"]);
	    },

	    setOrgColorsTheme: function setOrgColorsTheme(orgCode, theme) {
	        cordova.exec(function (result) {
	            console.log('设置成功！');
	        }, function (error) {
	            console.log('设置失败！');
	        }, "WorkPlus_Theme", "changeTheme", [{ "orgCode": orgCode, "theme": theme }]);
	    },

	    getCurrentEmployeeInfo: function getCurrentEmployeeInfo(callback) {
	        cordova.exec(function (result) {
	            var _result = result;
	            if (typeof result === 'string') {
	                _result = JSON.parse(result);
	            }
	            callback && callback(_result);
	        }, function (error) {
	            console("调用失败");
	        }, "WorkPlus_Contact", "getCurrentUserInfo", [{ "needEmpInfo": true }]);
	    },
	    refreshUserInfo: function refreshUserInfo() {
	        cordova.exec(function (result) {}, function (error) {}, "WorkPlus_Contact", "refreshUserInfo", []);
	    },
	    openWebView: function openWebView(option) {
	        cordova.exec(function (result) {}, function (error) {}, "WorkPlus_WebView", "openWebView", [option]);
	    }
	};

	module.exports = workplus;

/***/ },
/* 6 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var default_avatar = __webpack_require__(7),
	    default_org_avatar = __webpack_require__(8),
	    default_user_avatar = __webpack_require__(9);

	module.exports = {
	    DEFAULT_AVATAR: default_avatar,
	    DEFAULT_ORG_AVATAR: default_org_avatar,
	    DEFAULT_USER_AVATAR: default_user_avatar,
	    TOKEN_NOT_FOUND: 10011,
	    PHONE_REG: '1[0-9]{10};13[0-9]{9};15[012356789][0-9]{8};18[02356789][0-9]{8};17[678][0-9]{8}',
	    PASSWORD_REG: '[\u4E00-\u9FA5];[\u0391-\uFFE5];/s',
	    PASSWORD_REG2: '[a-z];[A-Z];[0-9]', //必须包含大小写字母和数字
	    PASSWORD_SIZE: [6, 20],
	    SESSION: {
	        PIN_CODE: 'pin_code',
	        ORG_CODE: 'org-code',
	        PHONE_NUMBER: 'phone',
	        ACCOUTNAME: 'accoutName',
	        PASSWORD: 'passowrd',
	        SEARCH_FRIEND_QUERY: 'search-friend-query',
	        SEND_FRIEND_DATA: 'send-friend-data',
	        NICKNAME: 'name',
	        ORGANIZATIONS: 'orgs',
	        ACCESS_TOKEN: 'access-token',
	        DOMAIN_ID: 'domain-id',
	        CLIENT_ID: 'client-id',
	        AVATAR: 'avatar',
	        ORG_NAME: 'org-name',
	        ORG_LOGO: 'org-logo',
	        ORG: 'org-info',
	        USER_ID: 'user-id',
	        THEME: 'theme',
	        CLIENT_PRINCIPAL: 'client_principal',
	        APPLICATION_SETTINGS: 'application_settings',
	        EMPLOYEE_DETAIL: 'employee_detail',
	        EMPLOYEE_POSITIONS_DETAIL: 'employee_positions_detail'
	    },
	    ICON: {
	        disable: -1,
	        plus: 0,
	        webchat: 1,
	        edit: 2,
	        dots: 3,
	        qrcode: 4,
	        refresh: 5,
	        search: 6,
	        setting: 7,
	        link: 8,
	        about: 9,
	        share: 10,
	        checked: 11
	    },
	    LANG: ['zh-CN', 'zh-rTW', 'en'],
	    DEFAULT_LANG: 'zh-CN',

	    OPEN_ALL_I18N: true
	};

/***/ },
/* 7 */
/***/ function(module, exports) {

	module.exports = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHgAAAB4CAYAAAA5ZDbSAAAAAXNSR0IArs4c6QAAB25JREFUeAHtnV2IFWUYx//vnLOui7vbuq6fu34loaJlFAgZFWWgKLYGddNSmXRRYOhFBF2EdBVBgRdBF4UYUTcRKaIk2SeEYESYUkaYmuv6wbqaq+i6u+fteWec3Tkz+zEj48w7j8/A7pmP533f5/n/9jk753ln5iiMsejdU1pRi/Vw1FpoLIBSs8i8fowmcuj2K3AFWndB4Rgqeg8G9C61pqdztGHVSAf01y0zUcLbdGwjQS2NZCP7LFFA60HyZDsGsVWt7j4T9ioCWO9rWUdwPwNUQ9hYtm1WQPcS5A61qnt30EsnuKG/mbIJDnYK3KAqRVmnhCR2LsOAy0MZ7GaugatUFfSArawWQQGtK6hgvZ/JLmD3f24Zf0nmFoFgHB/p7XoAC83/ZC9b3RMq+Z8bR7pi2BBL7yQZSu9tbkONc0LOlouBLraX5uz6up7roKzaBW5s2YpjaD7eUg3DcYsYxXFbPE2iABWoHLdClaSR2BZHAao+OvT2bMqPsnBUgNias2ipLXOE68VUL0UNvnDdyASwAGauAPPwJIMFMHMFmIcnGSyAmSvAPDzJYAHMXAHm4UkGC2DmCjAPTzJYADNXgHl4ksECmLkCzMOTDBbAzBVgHp5ksABmrgDz8CSDmQMus4uv9UVg/utA7fRkofWdA46/B5z+JFk7y62V3j9VW+5jfPfqlwDLv6N76IZumozf1lhqkuLg48CVP5K1s9iaVwY3LhuGe+TVZLIv/dBr23i/AE6mXJbWgadNnPsy2cAGsLsE+kjWg5XWcpJlJZb0nBLA6WlpZU8C2Eos6TklgNPT0sqeBLCVWNJzSgCnp6WVPQlgK7Gk55QATk9LK3sSwFZiSc8pAZyellb2xKsW3d8zLPKKX4bXk6wF+0jSzlJbXoAvfA9c/RuYdA9QNze55Kat6YPRwmu60IApTQKmtwMTQvPBtTOBtg0eus4dQF/o0co3aD743C5g8Kpnw+Q3P8CjgWm4j+aK93tHDz4J9P4+miWr/XKSxQpnNBgBHNWE1R4BzApnNBgBHNWE1R4BzApnNBh7PwfX3Q00Pwo4E6Ne38qeiYFnrs54BmhacSu9RNtUrgM9PwHX/okes2CPnR+Tmh8DltE3+zgTLJAohguVG8ChDgL9YwzjbE3szOA5r3hwB68BA73pKOLUADWTvb76LwKV/nT6LdPXS5XqgNnkswCOqWlNi2d49gvgKN2lkMYSLHT89mx6hY5FdDdE6wtUObvpcxq+ptiHnGSlKKaNXQlgG6mk6JMATlFMG7sSwDZSSdEnAZyimDZ2JYBtpJKiTwI4RTFt7EoA20glRZ/yK1UqKqK1bQQaH6Qbr0P35JpSZc1dVN89CVw+VB2uqf1e+JYur/mqer+/VTefqkovRy/ZqWnyatvGztSO+y/5LbxXc8nOqY9pzOPV+/2t6U8DU1ZGa+PmpnNz/Vf/f9FKlvn29cu/Ap3b6ekBA35Pmb7mB3jxNmDWc7ce7NE36HkaO6rbmwrYQz8PlySrj46/ZUqYBx4mWN3Vtq0vAYverd6XZKvrc+DPLUlapGabXy162lNeEL2HKWtOxQ+oYSllzBy6sG59FLCZffLrzef3xu/TWE5b47VtfiT67mAu4jPLtX+pxHnEW4/zu2420HAv9U2x3nGAy/WeRJ07gK5P48jl2fi1X3P1ZHgJ7ju8IXx07O2V573jpZt+Ba39fnt+SFYbn/U8sPh9wI812GdG63KSlZHQeQ0jgPNSPqNxBXBGQuc1jADOS/mMxhXAGQmd1zACOC/lMxpXAGckdF7DCOC8lM9oXAGckdB5DSOA81I+o3Hzq0VX+mhmppbqtGuptjwvfriND3i2pn14Ce5b8Fb4aLztYB9+C3+fGTtJvw1LvB789n5/Gb7mB9g8KmHqapqCe8L7SRp0D7UPL5cO0B36NJ1YmgjMey18dPxt09b0EV7MWE3LaeKAJjrMT9Ilx8dC5DddWG6ibHhz5PngsQT054NPbBt5jnUyTffN3UTzwTPG6iV67MZZ4OQHwEWabgwvZu563paR54PDtsFtfz742Dt0h8al4JHM1vMDnFmId/ZAcpLFnL8AFsDMFWAenmSwAGauAPPwJIMFMHMFmIcnGSyAmSvAPDzJYAHMXAHm4UkGC2DmCjAPTzJYADNXgHl4ksECmLkCzMOTDBbAzBVgHp5ksABmrgDz8CSD+QPWKT1SnblShQxP9zrQOFNI38Xp8RUgtg4Ujo1vKRaFVIDYOqjoPYV0XpweXwFi62BA74I2N9HIwkoBw7QPOx21pqeTAqOnZcrCTIHtat2F097HpEFspVv15GyaDWFi6TKlW7BNTGp19xna0UFv1RU2Md6pgRiGxNJlShoMFTrUqu7dlMWbBXKB/zLcBNWbPZZeHCocjt7Xsg4l0BcHqobwMdm2WQH3bbkjCNd4O5TBvuuuwQAWUiZ/JGfXvioWv5qzZcOKmIXhGq8jGRwMRe9tbkNZtcNRa6nitQBKme9oHeGBysFWsn6bFbhCQLvcApWpYdBHIXO2PNqY/wMHwo/bhOeBhwAAAABJRU5ErkJggg=="

/***/ },
/* 8 */
/***/ function(module, exports) {

	module.exports = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHgAAAB4CAYAAAA5ZDbSAAAAAXNSR0IArs4c6QAAEWVJREFUeAHtXWuQFNUVPj0sC4sQFRBwH4oKrG8jCggI4oriC6lYajQaKZFHysKyLH8gwTJWChMxWvqDqmRhhQKlYpQqiyCCkZdBWQzgK6hZJQH2gSgP0VXAdXc639czvTsz29OPmX7tbp+q3e7pe/vec8/X9/a55557WpFOQIOXqj2bm2SoGpNyieNPpFxVZYCiSB+c98F5HwVHSfwWUaVRFWlEeiPSG5HO869xXiMxqVHiUlNQKF/svU85gWsdmtDujkdnLlbP+ykuFWD+aoBzGVpwBv5iLrckjvJqAfxOPAybusdk474Zymcu1+F5cR0C4GGVav9jMZkSB6jofRWQyiDPJWNcwQGMAhtjALtXXFZ9Pks5ZJwtPFdDC/CQN9QexxtkMobceyGu69FTu4dHbCLo2T+Bn3UYN5YXlcjq3TcqP4aJP52X0AFctkgdEVflfjD4S4B6is5omI8A+yj4+1tMkRfqZirbw8RraAAuXaxOUOMyD6BODJOAnPICsNcrMXmyfoay2em9XuQPHODSReoNAPUx/I3xooFBlQmgt+Jvfv1MZW1QPLDewAAurVRHQU1dCKXp8iAF4HndiuyAej+7fpbynud1GVTgO8ClVWrfeLM8hUdrOsD1vX4DGXh/SUFLVamKFcij9dOVI95X2FaDbwJWVVUpq5JpaosswLyyXxsLXecMwj6sdJM5ddNliaIoEIP35AvAxUvUMmmSFWjOOO+b1CFq2CKFcvf+aUqd19x6DnBJpXoThqdlXbXXZgOQvRkvqKkNs5Q12fK4cd0zgCdsUgt218gf44o80mXetU4Rwbs5psqzQ8pl7uarlWant9vJ7wnApcvUEvWEvIqpz2g7THT1PJhOVSs95fb6qUqD27JwHWAuBDS3yJsYksvcZrYzlwcg6gq6ySS3FzRcXYEpWaxeAXC3ROA6fxQpM8qOMnR+d/Y7XANYs0jFZUOkTGUXtlUKZQdz7QbK0iqv3XRXAC5epN6Jpby/Q5nqZbfiKF8WCUCGlCVlmiWHo8t5v4P5tGngihQ4qjnKbCWBZqw735KvLTsvgPm+4JAS9VwrrHJMV+QYVqauaZihbMuxhNxtwUltmQpVlzQ75ipwp/ehBx6Gdj0uV+06px6szXOPSzU1P6cMR/mdSwAg1SlFMjqXebJjJYsWKs2IEYHrHKkc72BHoswpe6dFOAaY5sfIQuVUzPnnp8wpe6clORqiuXCAp2l1ZFt2KmaX8sN2DcAmO1mgsA0wl/yUJvkgUqpcAivHYgDYYbVQLrW71GhriOZiPddzI3BzRMXF2zQMiAUxsUG2AKYnBsqKFuttCNSnLOOSmFhWZ/kU0IdKbZbPo95rKUtfM3CoVgpkmJWPl2UPpoNcBK6v2NmqjJgQG6vMpj046dpaHWnNVmIMKJ0eIQIDiIlLrmkPTvotmz4EATUtqpYSgNuxhpGJNLICrK1JdnandBPBdJgkYGS2fpwVYFhOHuswjezijGK5dl42ERgCrG0E62R7hbIJoJNcH0vMjNpiCDB3+Rlljq6FVwLZMGunQHF/bktc/hXGppx9ssjwASKXDhQ591SRU3uKnNIDfzhimJKjTSLfYhv218dEdmHv/UcHRT5E5I3678PYGvd56haTkZn7k9stP2Hz9XT3q869xGEA8vZhIrcORdyGk0zK6SZShBgApyPPuX1Fxpe25f3ksMiq3Yk/N8E+FQ9XvyKRL38Q+YH7/QOmJHZpG9DTerAWNqFODkDBCnxn/TVniDyC8CqXoMe6RWiXrNsr8txOEYKeD229S+TMn7WV8BVAfv1/Ikt3iez5ru06z4rx0N1RLnIVHrrTe4ucXIjoLo0iG2pFnk6DI/0+p7/gQH+0qEwGpYaTSAMYPla3Yfffq04LdjP/ZRh+540SGXW6m6Wml6UD/butIg05DN9FGPe+mKbF6UgvGL9Y9sIPRRbgJYfIPPI49nZMvQAhgNIknbht77ciY19uV0ReF7B78Xb4cK3UC0kfohMBT/Q0X48UxtyRIjMvNhacm8zgSZcbzsLqSYnI77eJrPjMWekD4RzMMoyI1x+8FCPPaSJ9oRtc2N8oV+LaQPRs1ymBYSvArWwyVBFGmf14AvEm85fOwlD354kiF0EoQRCHygfWi3xv8z3aGxKqQQ/Ol/Zj9BixIt9S0u/HA/YTnptiPcQT+k2CGIcqCHCpFa+5NThw2Xq+71+bklDQkuIwPfBB+A7aer60N+NdnW95vJ8YEku9rFaAMc2o0C/6dRyN9+zLN0PpgDYaNJ0P59/VvxDhVMwO/WOfnVzmef5Zb56ea2oqlq0Aw3DtK8AjEavupRtFTvL9hZBdbJxirQBP/TH1saKX/2OVwzz9xxbn737zElNSU7DUAKYTO5Ihcn+I04bF14n0TFfx/KncopYzoA+8hK1fvSx4q/4SU649FoWZJC/+WOTICZMM+SUNSmKaCODJwJ75lWf/7h4wSFQBXDu9xH6p7uaksvfkldZlPvJ2btOsdxswjdpuXX4+OXRMtR4MVdo3gOeMcNd4kY8QzO6lYWLSYLMcMI1C0bp1VcIsap6zLZUa+8y3YFrFfNlLAqZXs3wNYGhew72sTC+bZsf7L9J/hf/49PiEvduMU5o+pwDkv3yEwNOwhWejg7CPPwHDyr1rEw9GtnxuXQemsANivs5g2k0/CqbArsdbZvlptHIy/EuK0y6F/sdLn4rM2WKPTSqMt5yDKKro/SOSGs0xTKke3Cjy1j6RFo97bQaX8cIeclKMkdKRoPXkjAyu/pyIuWZHA5cCuOtcERpi7BAXHP4K7TpVw+4F0PdhvuszuGRXwzamhcG3w32eeWZdkmcBAd2OJTh5SBvs7DPA5cpUum1Y6i//zoltLPmNA09rvRBGhDEdbGhOFQiHXa4726VMgK8bbPdOl/Ph+xWc7eGN4S1xLdcpNccTS28b60Q+xdLeABgfLsb05YGfi5zj82Imp3Zsw5Jd9lpx8Hh6PlrHaEThurHPVI5AazLQ60q5cuOEaIS/+TVondUiNOcdgsA+PYJ3W43ItVgnoeLjN1GHsEtGi/+Xey7l9twRWy5T9mmf5N6V8/rikyg2lRTWCvVeHtok8u9DxjzQxDf3HZH3vzJO9+oqteICm6poNwg1k4p7Z17x/jexJcueVj3c4ZP7xh6RrfvNG08jweOYU/pJ1Ia5xmuH+hS2z3Ua1pADoN4x9BhPe/BF/Z01a/sBe/k/PihyotleXrdyjbWpKBrpCNQh/CZi68sQ7aRhdtdIOa9004HODo9XltjJ1WbkSM09IIAenBii9c+9pXLj4rnTRYWhMGfaIWq2Z3o69rTn4gqsX5db8MfXr9G8l669vhOwtak25M6a08V8u8Mg15O7A2Q/iUaPJ8aY1/gwjCJlBg+eGx4g5jUbp8aw0N9onOTOVSOFw6zkCWWYIp1tlgPryAB2/ljzPF6l0t/6RawX0+k+lcjTb0eKEGAj4lTPdwK2BXiVEeB+XlWOdUnb0wudhwXjRZowHTJyi6Gn4rNXiQyxGCr1srw4VmBOvPMekd1HRf6LPxoxOB3sbaA96/UfDgBgYlsALzx+XtUzOoKGlRgMWWYV0iy49HqRtXtENtYmLVlQUmjJmno+3FED0Egz+aUOcAG6Bf/s0KETdnK5m4fY0lTJHuwZUdN1CrDODC1gTq1g+r1hO9I6FwA1ch7sKcAffB1As0JYZbWF8cYLloltDN3YUwh2+mxS9EJQ+ZZZi/Vgv+fs5JnYcpoEE753tCMCWN4NoPcmEa2JwZfDU4C5Nrr7G+8eoI5Q8jsNAXEJbGMI0+IpwGza8gCW9wISabtquSGd/lhBELGNFRTKF6gcs1Xv6JXPw7FB2rsWZi952SeBtT1ObGN771M4Q8Ns0zuiO+lKgNzViKtdL+wKrNW1xJZKFrWtnV6zsfADbM8E0F2JXsHLLxATJYSsY6oBDEPWJq8Fvx/+SPPf87qW8JTPfUfPvx8cPzqmGsDYXb/RD1ZehLIVxITfj7Zl1vEwusxXmEEERTqmGsDJT7Yc8IOZhzeLcBtHZ6ZFH4usrw20hQf0z/BoAGusKP704joYRn+1JhHPKlAReFQ5nQH/EPSrKAXLVoBjPg3TlCtdYLkJ6zi2enQm2gIX37vw8HKJNEhKxbIV4F5xWQXNyzeR04R5J4QRgDO4J7LnNPAePLR2A7l4wgQKJYbEUi+/FeBkVJZ1eoIfR4JMR/a36/yozZs66ML7PCaZ9OXmbowQ0Do9wg55aQVYYywmy/1m8BtMJ+5+A1Oobe5ErvGT/021iQf0Tzv8rNWirgwM6QTYSkGHMmSIP+49mnYhYmTA0TysxECnfCC3BLWIkEUwGJ7NQxnyPnzdrBILxTOzlOHL5X49E7EdJw0WYWhDozCAvjCSUgk3wK3HogGnP2Fd4wbAlfgq2m9S2G7/edmwhROmXzU3ftH3qRS+XXRJ5VGPgoNGtSODS615jPLricdhO6Zm/x1MqpzO0QmfQUs3AFha4sJORuGEDWWBXvwWevHEsDco4q9NAnhw16P3Xtt2JXGWrmQlU/HV6SczM0a/wy2BbJgZ9mA2Bb34XfTiMeFuVsQdJYDeuxW9d6yRNAx7cPKm+UY3RNfCJwEAnBWrrD2YzSiuVLdja8vl4WtSxFGrBBTZsX+WMqL1d8ZJ1h7MfEicDT0btpqIQimBxKftZpvxZgqw9k08VarMCojSApQAsDH7biE5MwVYy1Agj2Icx2wwojBJgJjEgI0VT5YA8/u0+NDDHKuConR/JUBMrL4dTI5MlSydZX5OvKRS3sbvcfq16BioBLY0zJKrFEWx1I8sezCboRVUKHdHQ3WgoGqVaxgQCxvg8gZbADPj/mlKHfr71EirpjQCIs5ogIGGhU0WbAPM8mAtWYPQeM/aLDvK5rIEKHti4KRYRwCz4CHlMheWEwQZjMhPCVDmlL3TOm0pWZmFli5TS9TjUo03PEKmROS1BABSnVIko+unKo5dDBz3YDaGFRV0k0mR0uU1tNo05zBlnQu45C4ngHmj5ljdTW7GS7+Tu7GztQERZQsZ607suXCRM8CsDF+53AZ3mttwCl+IiFyWQDNlSxnnU25eALPi+pnKWowDv8ZpBHI+SKTf20yZarJNv+74V05KllEtpYvUG+AjvBIzNUS0iihnCWBYZs91A1zy4BrALAwfmL5CWuR1aNdwkYvIqQQ0pRXv3HyH5dR6XQWYBfObec0t8mY0hUoVs/U5gKijtpyPQmVUS97v4MxCySDnbJExJFMy2X9TVpSZ2+CyRtcBZqGcsw0dJuNR+DOR7ZoSyUIJj4xnKKtc57lZSm697PoQ3Vpy8gTemTdB8VoWvZfTJaO9b7Fw4NS2nF6K9S/PASYLxUvUMmmSFTiN1pMTmGwRLPk5WRVK3Ob8vy8Aky06DZRVyTS1RRZ01d7MXktPjLrpssTueq5zSNPv8A1gvdrSKrVvvFmewrt5ura6qSd05iPXceEgRx8qO242borCd4B15ksr1VHYL70QDe/cftfwW4ayOdvK+1GXi9vHwADWG0ILGLbIPNbZtslg6rMVf/Pdskjp8nJ6DBxgneHSxeoENS7zOvquRoC6nhvB6mcom/W2BXkMDcC6ELg/GTbt6fh9B8D2+TujOhfOjgAVn+aQV2BDrqqbqWx3dre3uUMHsN5cLZxEg0xGHNx7ce16gN1dTwvDEaAyItE6mIqWF5XI6t03KggcHD4KLcCpohpWqfY/FpMp8bhUQCmrQNqg1HQfzw9A+9/IOFQMVZQazcZHHhxV1SEAzmwRFzQQbKwCzFegZw9HOoI8uG52hZIvteipOzFv38TYj17YijPb5vbvDglwphAGL1V7NjfJUH6zPvnJ+nKAMhCNQzQP6Y2HoI92rn+nEV8jQXojwEMkDvleO0fsUJzX4DGpYaR0BtNOxtLOrK5D/f4/qnjaO8cgtZkAAAAASUVORK5CYII="

/***/ },
/* 9 */
/***/ function(module, exports) {

	module.exports = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHgAAAB4CAYAAAA5ZDbSAAAAAXNSR0IArs4c6QAACCRJREFUeAHtnUlP3EoQx3uGfZksiCUsCUtYxXLiAPkKKAr33N75SfkI0fsckbjlThTlI7AcckcIUACFJWFngIQ99XcG5OdMGM+47XI1XZJnvHZ31c/V3e5utxPqDpmenm6mw+O0jF1fXz+n/yZaqmmxwmeBI4p6PZFILNH/p2Qy+WFkZOTr35KTyHbg8+fPjaenp//RsX9oKcp2jt0XGwtcUkomysrK3g4PD294U/UH4JmZmZdXV1fv6cSU92S7HWsLpMmbX4+Ojn50pzLp3qAs+V/Kiidpn4XrNoyM9RTYgaE7ubceDM/FCbT8D7r7ZLsefwtQ2XxFy/iNJzuAM2XuPCXfem78GfpJYZrK5B6UyY63ZipUFq4f08k4J5VhqhKzs7Mtl5eXy5RuW1uWAc9vKi8pq25NUo35lYXr12aizoPDjiOLHhOVbJvYfCwwlqRaM1qorBhoAbCFB6P50YqZFmgCYNu2bCZcaFXtPCaZq5/VzAI2/B6wgC1gwy1guHrWgy1gwy1guHrWgy1gwy1guHrWgy1gwy1guHrWgw0HXGy4fo56JSUl6tGjRyqVSqmqqiqFbSyQs7MzZ0mn02pra0v9+PHD2W/KT2JqauraFGW8etTU1KgnT56ohw8fKhrd4D2cdRugv3z5oo6OML5cvhjpwQD79OlTx1vzRQQvHxwcVCsrK2p9fT3fy2N3vlGAkQ23trYWBNZNBt7e1tamaKya+vbtm/uQuHUjAAPIs2fPVFNTk++s2A+pjo4OdXJyopBtSxXxteiioiI1MDCgmpubtcIFUNw4gCxZRAMGgL6+Pqd2HBYE1LobGxvDCj70cEUDRrb84MGD0I2kO+sPPcGuCMQCrqysdMpcly6hrdJrIKquri608MMMWCzg9vZ27WXuXYaGF0sUkYDxrIrGiygFOQbKY2kiEjCXN9XX10vjq8QBxmMRWqo45PHjxxzRBopTHGC0VuHxiEPKy8tVaWkpR9QFxykOMHc5iPJfkogDzO1BFRUVkvjKK4O5sucbqsimJYk4D6YX1lnti0qeJBEH+OfPn6z25c5B8lVeHGDuITXoI5Yk4gAfHBwoenOdzcbn5+dscRcSsTjA8CDODngMAJAk4gDDuLu7u2w2Pjw8ZIu7kIhFAt7Z2WHJpo+PjxV3JS9fyCIB0yxuCmVx1LK5uRl1lIHjEwkYWm9s/DE1cmBj3BUAyl6JIyzFAo66wYMjx7jrhvN7TCzgqDsdoo7PL8Bc54kFHHWjf9Tx5QLn97hYwMXF0Y7Zjzo+vwBznScWcNRtwogv6jhzwfNzXCzgqNuEER9nE6kfmNnOEQs46ibDqOPLBquQfWIB7+3tFaJvwdeg9UyiiAUMj4rK6OiilNiKhRtSLGAkfnFx0Xm9E+thycXFhZqbm1NRN6zo0kc0YFR85ufnnRe1dRnEG87CwoK4Dga3DqIBQxFkn6urq26dtK2j7Tnqsl5b4jMBiQcMPdDxgK48nYKseXl5WWeQLGEZARiW0z1hyvfv30PN+qOibQxg1Kh1VoS2t7ejYhBqPMYABlxdc1she9YVVqj0fARuDGDoqqscltpqlY23UYAxlEeH6ApHR1qChmEUYF0dELrCCQpHx/VGAdZhEIQhsdfob7obBRiz4egQXeHoSEvQMCzgLBaU9opoFhVudxkDOJlMKl1zaGBGHaljsG7JZlaMAVxbW6t0jptqaGjw2krkthGAMXs7pjXUKZhIvLpa/odZxQNG1tzd3a199puwwtV5E/oJSzRgTMjS398f2qx3qGxh9nepg95xA0Q7uNjPLefjHHgXslBM2x/2nBl4ZBoaGnK6JNfW1pS0F8BFAcYkaKhMYaY7nRWqXPcUxkNj+kTcVPv7+wo9TXhHWWfvVa40FHo89oCRDaNGi4V7jizkHLi5sKA5E5/hwWC8OHdOxBYwssaWlhaFCUDj+EYBigZ4NBYM68GwIV29WYV6a7brYgcYMPHIg2n04TESBA0sWODR+OYS+pPjIrECjNpqV1eXQkuSRMGs8JjHemlpKTaD9WLjIjAMvp4iFe7NDYl6Qm9vr5N13+zj/I+FByN76+npEZMl5wKGYgaf40ERo3swYK64vcfZPRiNCWiJklLeeg1413YbfT0Nj3acwg4YZW7YjRWcBu7s7Iz0md2rKytgPGJIm2Dba8Bc2yiT8T1FLmEFbEqXXC54aH3jKoLYAOORSHIjfi6o7uMoggCZQ9gAcynMYWTEyaUvG2BThsT4vWG4xnmxAebuOPALRtd5XPqyATZpaKqfmwCVLAwtilrYAMexhyhs43PUpNkAr6ysqLOzs7BtGovw0XeM0SAc7zyxtUVjegSJ0/PG4o7JIxFsHpxHGu2pASxgAQcwnoRLLWAJlAKk0QIOYDwJl1rAEigFSKMFHMB4Ei61gCVQCpBGCziA8SRcagFLoBQgjRZwAONJuNQClkApQBoBOB3gentpvC2QBuBoPwIYb4OYlrqNJPXLLpmmldXntwXAFh78yRrEWAt8okEGyQ+k3qWxKt5fxcB0MjkyMvKVViburx2M1XzixYsXa85jEg2Ae0tq2tq0OazTGaa/v5s0PDy8QVn1ayqUr8zR8X5qAoZgCaawgOPBWBkdHf1If28sZFhDpmTYvcmwdJRIeFWZmZl5SdMDvaf9Ke8xux1rC6ThuW64SO2tB98kHSdQ/t1D2+9osbXrG8PE9x+M3oGZFy6S/IcHu/WYnZ1tIW9+RfvGaBb05/TfRIv8GTrdSspbP6Ikr1N2vET/aMOYRG35b2r8ApHl9IYzG72sAAAAAElFTkSuQmCC"

/***/ },
/* 10 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var workplusEscape = __webpack_require__(11);

	module.exports = {
	    addItem: function addItem(key, value, parse) {
	        sessionStorage.setItem(key, parse ? JSON.stringify(value) : value);
	    },
	    getItem: function getItem(key, parse) {
	        var value = sessionStorage.getItem(key);
	        if (value === null) return false;
	        if (value && parse) return workplusEscape.escapeObject(JSON.parse(value));
	        if (value) return workplusEscape.escapeString(value);
	    },
	    delItem: function delItem(key) {
	        sessionStorage.removeItem(key);
	    },
	    delAll: function delAll() {
	        sessionStorage.clear();
	    }
	};

/***/ },
/* 11 */
/***/ function(module, exports, __webpack_require__) {

	/*!
	 * workplus-escape.js v1.0.2
	 * (c) 2016 Hejx
	 * Released under the MIT License.
	 * https://github.com/WorkPlusFE/workplus-escape#readme
	 */

	(function (global, factory) {
	     true ? factory(exports) :
	    typeof define === 'function' && define.amd ? define(['exports'], factory) :
	    (factory((global.workplusEscape = global.workplusEscape || {})));
	}(this, (function (exports) { 'use strict';

	var escapeChars = {
	    "&": "&amp;",
	    "'": "&#39;",
	    '"': "&quot;",
	    "<": "&lt;",
	    ">": "&gt;"
	};

	var escapeString = function escapeString(str) {
	    for (var key in escapeChars) {
	        var reg = new RegExp(key, "g");
	        str = str.replace(reg, escapeChars[key]);
	    }
	    return str;
	};

	var unescapeString = function unescapeString(str) {
	    for (var key in escapeChars) {
	        var reg = new RegExp(escapeChars[key], "g");
	        str = str.replace(reg, key);
	    }
	    return str;
	};

	var handleEscapeObject = function handleEscapeObject(obj, fn) {
	    for (var key in obj) {
	        if (obj.hasOwnProperty(key)) {
	            if (typeof obj[key] === "string") {
	                obj[key] = fn(obj[key]);
	            } else {
	                handleEscapeObject(obj[key], fn);
	            }
	        }
	    }
	    return obj;
	};

	var escapeObject = function escapeObject(obj) {
	    return handleEscapeObject(obj, escapeString);
	};

	var unescapeObject = function unescapeObject(obj) {
	    return handleEscapeObject(obj, unescapeString);
	};

	exports.escapeString = escapeString;
	exports.unescapeString = unescapeString;
	exports.escapeObject = escapeObject;
	exports.unescapeObject = unescapeObject;

	Object.defineProperty(exports, '__esModule', { value: true });

	})));
	//# sourceMappingURL=workplus-escape.js.map


/***/ },
/* 12 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	/**
	 * 确保promise polyfill已经引入，因为ios7不支持原生的promise
	 */
	var Constant = __webpack_require__(6);
	var Util = __webpack_require__(13);
	var sessionStorage = __webpack_require__(10);

	module.exports = {
	    getLoginUserInfo: function getLoginUserInfo(dont_save_phone) {
	        return new Promise(function (resolve, reject) {
	            cordova.exec(function (result) {
	                result = Util.toJSON(result);
	                sessionStorage.addItem(Constant.SESSION.ACCESS_TOKEN, result.login_token.access_token);
	                sessionStorage.addItem(Constant.SESSION.CLIENT_ID, result.login_token.client_id);
	                sessionStorage.addItem(Constant.SESSION.NICKNAME, result.login_user.name || result.login_user.username);
	                sessionStorage.addItem(Constant.SESSION.ACCOUTNAME, result.login_user.username);
	                sessionStorage.addItem(Constant.SESSION.AVATAR, result.login_user.avatar || '');
	                sessionStorage.addItem(Constant.SESSION.USER_ID, result.login_user.user_id || '');
	                if (!dont_save_phone) {
	                    sessionStorage.addItem(Constant.SESSION.PHONE_NUMBER, result.login_user.phone || result.login_user.username);
	                }
	                resolve(result);
	            }, function (error) {
	                reject('获取用户信息失败:' + error);
	            }, "WorkPlus_Auth", "getLoginUserInfo", []);
	        });
	    },
	    getCurrentEmployeeInfo: function getCurrentEmployeeInfo() {
	        return new Promise(function (resolve, reject) {
	            cordova.exec(function (result) {
	                resolve(Util.toJSON(result));
	            }, function (error) {
	                console("调用失败");
	            }, "WorkPlus_Contact", "getCurrentUserInfo", [{ "needEmpInfo": true }]);
	        });
	    },
	    getApiConfig: function getApiConfig() {
	        return new Promise(function (resolve, reject) {
	            cordova.exec(function (result) {
	                result = Util.toJSON(result);
	                console.log(result);
	                window.CONSTANT_CONFIG = {
	                    base_path: Util.checkTheApiLink(result._ApiUrl),
	                    img_link: result._ApiUrl + 'medias/images',
	                    admin_link: result._AdminUrl,
	                    encrypt_login: result.encrypt_login
	                };
	                resolve(result);
	            }, function (error) {
	                reject('获取配置失败:' + error);
	            }, "WorkPlus_LightApp", "getConfig", ["_AdminUrl", "_ApiUrl", "encrypt_login"]);
	        });
	    },
	    getDeviceInfo: function getDeviceInfo() {
	        return new Promise(function (resolve, reject) {
	            cordova.exec(function (result) {
	                result = Util.toJSON(result);
	                window.DEVICE_INFO = {
	                    device_platform: result.platform,
	                    device_id: result.device_id,
	                    domain_id: result.domain_id || 'atwork',
	                    product_version: result.product_version,
	                    system_model: result.system_model,
	                    system_version: result.system_version,
	                    channel_id: result.channel_id,
	                    channel_vendor: result.channel_vendor,
	                    device_name: result.device_name,
	                    device_system_info: result.device_system_info
	                };
	                resolve(result);
	            }, function (error) {
	                reject("获取设备信息失败:" + error);
	            }, "WorkPlus_DeviceInfo", "getDeviceInfo", []);
	        });
	    },
	    getAppInfo: function getAppInfo() {
	        return new Promise(function (resolve, reject) {
	            cordova.exec(function (result) {
	                result = Util.toJSON(result);
	                window.APP_INFO = {
	                    app_name: result.app_name,
	                    app_icon: result.app_icon
	                };
	                resolve(result);
	            }, function (error) {
	                reject("获取App信息失败:" + error);
	            }, "WorkPlus_PublicClound", "getAppInfo", []);
	        });
	    },
	    getContacts: function getContacts() {
	        return new Promise(function (resolve, reject) {
	            cordova.exec(function (result) {
	                console.log(result);
	                resolve(result);
	            }, function (error) {
	                console.log(error);
	                reject("打开选人控件失败:" + error);
	            }, "WorkPlus_Contact", "getContacts", []);
	        });
	    },
	    // 分享页面相关
	    getCurrentOrg: function getCurrentOrg() {
	        return new Promise(function (resolve, reject) {
	            return cordova.exec(function (result) {
	                console.log(result);
	                resolve(result);
	            }, function (error) {
	                reject("获取当前组织信息失败:" + error && error.message);
	            }, "WorkPlus_Org", "getCurrentOrg", []);
	        });
	    },
	    popSwitchOrg: function popSwitchOrg() {
	        return new Promise(function (resolve, reject) {
	            return cordova.exec(function (result) {
	                console.log(result);
	                resolve(result);
	            }, function (error) {
	                reject("无法弹出切换组织选择框:" + error && error.message);
	            }, "WorkPlus_Org", "popSwitchOrg", []);
	        });
	    },
	    getShareItems: function getShareItems() {
	        return new Promise(function (resolve, reject) {
	            return cordova.exec(function (result) {
	                console.log(result);
	                resolve(result);
	            }, function (error) {
	                reject("获取分享弹出时显示的数据失败:" + error && error.message);
	            }, "WorkPlus_WebView", "getShareItems", []);
	        });
	    },
	    showShare: function showShare(options) {
	        return new Promise(function (resolve, reject) {
	            return cordova.exec(function (result) {
	                console.log(result);
	                resolve(result);
	            }, function (error) {
	                reject("分享到workplus会话失败");
	            }, "WorkPlus_WebView", "share", [options]);
	        });
	    },
	    wxShare: function wxShare(options) {
	        return new Promise(function (resolve, reject) {
	            return cordova.exec(function (result) {
	                console.log(result);
	                resolve(result);
	            }, function (error) {
	                reject(options.scene === 0 ? "分享到微信失败" : "分享到微信朋友圈失败");
	            }, "WorkPlus_WebView", "wxShare", [options]);
	        });
	    },
	    qqShare: function qqShare(options) {
	        return new Promise(function (resolve, reject) {
	            return cordova.exec(function (result) {
	                console.log(result);
	                resolve(result);
	            }, function (error) {
	                reject(options.scene === 0 ? "分享到QQ失败" : "分享到QQ空间失败");
	            }, "WorkPlus_WebView", "qqShare", [options]);
	        });
	    },
	    openWebView: function openWebView(options) {
	        return new Promise(function (resolve, reject) {
	            return cordova.exec(function (result) {
	                console.log(result);
	                resolve(result);
	            }, function (error) {
	                reject("页面打开失败");
	            }, "WorkPlus_WebView", "openWebView", [options]);
	        });
	    },
	    copyText: function copyText(options) {
	        return new Promise(function (resolve, reject) {
	            return cordova.exec(function (result) {
	                console.log(result);
	                resolve(result);
	            }, function (error) {
	                reject("复制失败");
	            }, "WorkPlus_WebView", "copyText", [options]);
	        });
	    }
	};

/***/ },
/* 13 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var CONSTANT = __webpack_require__(6),
	    CONSTANT_FOR_SHARE = __webpack_require__(14),
	    sessionStorageModule = __webpack_require__(10),
	    Modal = __webpack_require__(15),
	    WorkPlus = __webpack_require__(5);

	var workplusEscape = __webpack_require__(11);

	module.exports = {
	    matchPhone: function matchPhone(value) {
	        var _reg = CONSTANT.PHONE_REG.split(';'),
	            i = 0,
	            l = _reg.length,
	            _r;
	        if (value == '13800138000') {
	            return false;
	        }
	        for (; i < l; i++) {
	            _r = new RegExp('^' + _reg[i] + '$');
	            if (_r.test(value)) {
	                return true;
	            }
	        }
	        return false;
	    },

	    cantInputChinese: function cantInputChinese(value) {
	        var _reg = CONSTANT.PASSWORD_REG.split(';'),
	            i = 0,
	            l = _reg.length,
	            _r;
	        for (; i < l; i++) {
	            _r = new RegExp(_reg[i], 'i');
	            if (_r.test(value)) {
	                return true;
	            }
	        }
	        return false;
	    },

	    matchPassword: function matchPassword(value) {
	        var reg = /^[^\u4e00-\u9fa5]{0,}$/;
	        return reg.test(value);
	    },

	    mustHasWordAndNumber: function mustHasWordAndNumber(value) {
	        var _reg = CONSTANT.PASSWORD_REG2.split(';'),
	            i = 0,
	            l = _reg.length,
	            _r;
	        for (; i < l; i++) {
	            _r = new RegExp(_reg[i], 'g');
	            if (!_r.test(value)) {
	                return false;
	            }
	        }
	        return true;
	    },

	    matchMail: function matchMail(value) {
	        var reg = /(^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+)|(^$)/;
	        return reg.test(value);
	    },

	    getHash: function getHash() {
	        return window.location.hash.substring(2);
	    },

	    getJsonData: function getJsonData(data) {
	        return typeof data == 'string' ? JSON.parse(data) : data;
	    },

	    getQueryString: function getQueryString(name) {
	        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	        var r = window.location.search.substr(1).match(reg);
	        if (r != null) return unescape(r[2]);
	        return null;
	    },

	    bindEvents: function bindEvents(event) {
	        for (var i in event) {
	            if (event[i].selector) {
	                $(event[i].element).on(event[i].event, event[i].selector, event[i].handler);
	            } else {
	                $(event[i].element).on(event[i].event, event[i].handler);
	            }
	        }
	    },

	    // share => 专门为分享轻应用留的一个配置
	    request: function request(params, share) {
	        // 添加时间戳
	        var _date = new Date().getTime();
	        params.path.indexOf('?') > -1 ? params.path += '&time=' + _date : params.path += '?time=' + _date;

	        var _basePath = !share ? window.CONSTANT_CONFIG.base_path : CONSTANT_FOR_SHARE.BASE_PATH;
	        var defaults = {
	            url: _basePath + params.path,
	            type: 'GET',
	            contentType: 'application/json; charset=utf-8',
	            timeout: 1000 * 30

	            //附加上默认的请求参数
	        };for (var p in params) {
	            defaults[p] = params[p];
	        }
	        params = defaults;
	        if (params.type.toUpperCase() === 'POST' && params.contentType && params.contentType.indexOf('json') > -1) {
	            params.data = JSON.stringify(params.data);
	        }

	        // 添加钩子函数，可以统一处理状态事件
	        var _preSuccessFunc = params.success;
	        var _preErrorFunc = params.error;
	        params.success = function (data, status, xhr) {
	            if (typeof data === 'string') data = JSON.parse(data);
	            if (data.status === CONSTANT.TOKEN_NOT_FOUND) {
	                WorkPlus.AccessTokenOverdue();
	                return;
	            }
	            // 转码
	            data = workplusEscape.escapeObject(data);
	            _preSuccessFunc(data, status, xhr);
	        };
	        params.error = function (data, status, xhr) {
	            var _errorMsg = polyglot.t('app.xhr.error');
	            _preErrorFunc(_errorMsg, status, xhr);
	        };

	        // 此处主要是将 v1 转成 _v1 
	        if (params.prefix) {
	            params.url = params.url.replace('/v1/', params.prefix);
	        }

	        console.log('调用接口:\n%s,\n参数列表:', params.url, params.data);
	        $.ajax(params);
	    },

	    renderTpl: function renderTpl(tpl, data) {
	        var compiled = _.template(tpl);
	        return compiled(data || {});
	    },

	    sessionStorage: sessionStorageModule,

	    go: function go(hash) {
	        window.location.href = '#/' + hash;
	    },

	    saveAccessToken: function saveAccessToken(access_token) {
	        this.sessionStorage.addItem(CONSTANT.SESSION.ACCESS_TOKEN, access_token);
	    },

	    getAccessToken: function getAccessToken() {
	        return this.sessionStorage.getItem(CONSTANT.SESSION.ACCESS_TOKEN);
	    },

	    makeImagesLinkByMediaid: function makeImagesLinkByMediaid(mediaId, default_avatar) {
	        if (typeof mediaId === 'undefined' || mediaId === null || mediaId === '') {
	            if (!default_avatar || default_avatar === null) {
	                mediaId = CONSTANT.DEFAULT_USER_AVATAR;
	            } else {
	                mediaId = default_avatar;
	            }
	            return mediaId;
	        }
	        return window.CONSTANT_CONFIG.img_link + '/' + mediaId + '?access_token=' + this.getAccessToken() + '&domain_id=' + (window.DEVICE_INFO.domain_id || '');
	    },

	    getStrLength: function getStrLength(str) {
	        var len = 0;
	        for (var i = 0; i < str.length; i++) {
	            if (str.charCodeAt(i) > 127 || str.charCodeAt(i) === 94) {
	                len += 2;
	            } else {
	                len++;
	            }
	        }
	        return len;
	    },

	    isEmptyObject: function isEmptyObject(obj) {
	        var i = 0;
	        for (var key in obj) {
	            ++i;
	        }
	        return i === 0 ? true : false;
	    },

	    toJSON: function toJSON(val) {
	        if (typeof val === 'string') {
	            return JSON.parse(val);
	        }
	        return val;
	    },

	    checkTheApiLink: function checkTheApiLink(api) {
	        var _l = api.length;
	        if (api[_l - 1] !== '/') {
	            return api;
	        }
	        api = api.substring(0, _l - 1);
	        return api;
	    },

	    showErrorMsgAndGetOut: function showErrorMsgAndGetOut(err) {
	        console.error(err);
	        Modal.alert(polyglot.t('app.xhr.error'), function () {
	            WorkPlus.getOut();
	        });
	    },

	    setTitle: function setTitle(title) {
	        window.document.title = title;
	    },

	    fixAvatar: function fixAvatar(avatar, defaultAvatar) {
	        if (typeof avatar === 'undefined' || avatar === null) {
	            return defaultAvatar;
	        }
	        return avatar;
	    },

	    clearAllConsole: function clearAllConsole() {
	        var consoleType = ["log", "info", "warn", "error", "assert", "count", "clear", "group", "groupEnd", "groupCollapsed", "trace", "debug", "dir", "dirxml", "profile", "profileEnd", "time", "timeEnd", "timeStamp", "table", "exception"];
	        for (var i = 0; i < consoleType.length; i += 1) {
	            console[consoleType[i]] = function () {};
	        }
	    },

	    getLangFromUseragent: function getLangFromUseragent(lang) {
	        return this.getLang(lang);
	    },

	    getLang: function getLang(lang) {
	        var _userAgent = window.navigator.userAgent.toUpperCase(),
	            _lang = CONSTANT.LANG,
	            _lng = null;

	        if (lang) {
	            _lng = this.matchLangDefault(lang);
	        } else {
	            _lng = this.matchLangOfUserAgent(_userAgent);
	        }

	        // 如果没有匹配到 则尝试使用系统language
	        if (_lng === null && navigator.language) {
	            _lng = this.matchLangDefault(navigator.language);
	        }

	        // 如果都没有 使用默认语言 'EN'
	        if (_lng === null) _lng = CONSTANT.DEFAULT_LANG;

	        return _lng;
	    },

	    matchLangDefault: function matchLangDefault(lang) {
	        var _lang = CONSTANT.LANG,
	            _lng = null;
	        for (var i = 0; i < _lang.length; i++) {
	            if (lang.toUpperCase() === _lang[i].toUpperCase()) {
	                _lng = _lang[i];
	                break;
	            }
	        }
	        return _lng;
	    },

	    matchLangOfUserAgent: function matchLangOfUserAgent(userAgent) {
	        var _lang = CONSTANT.LANG,
	            _lng = null;
	        for (var i = 0; i < _lang.length; i++) {
	            if (userAgent.indexOf(_lang[i].toUpperCase()) != -1) {
	                _lng = _lang[i];
	                break;
	            }
	        }
	        return _lng;
	    },

	    initHtmlFontSize: function initHtmlFontSize() {
	        var clientWidth = document.documentElement.clientWidth;
	        if (clientWidth > 414) clientWidth = 414;
	        document.documentElement.style.fontSize = clientWidth / 6.4 + 'px';
	    }
	};

/***/ },
/* 14 */
/***/ function(module, exports) {

	'use strict';

	/**
	 * 专门为分享轻应用设定的配置常量（因为无法从cordova获取配置参数）
	 */
	var API_SERVER = 'http://120.236.169.14:7081/v1';

	// https://api.beeworks.io/v1
	// http://120.236.169.14:7081/v1
	// https://api3.workplus.io/v1
	// https://go.workplus.io

	module.exports = {
	  BASE_PATH: API_SERVER,
	  IMG_SERVER_URL_SPE: API_SERVER + '/medias/images/'
	};

	// http://120.236.169.14:7081/v1
	// module.exports = {
	//     BASE_PATH: 'https://api3.workplus.io/v1',
	//     IMG_SERVER_URL_SPE: 'https://api3.workplus.io/v1/medias/images/',
	//     DOWNLOAD_PATH: 'https://dashboard3.workplus.io/release/index.html?id=atwork'
	// };；

/***/ },
/* 15 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(16);

	var WorkPlus = __webpack_require__(5);

	module.exports = {
	    removeDialog: function removeDialog(shelter) {
	        shelter.addClass('fadeOut');
	        setTimeout(function () {
	            shelter.remove();
	        }, 1000);
	    },

	    createDialog: function createDialog(text) {
	        var _this = this,
	            shelter = $('<div>'),
	            html;

	        html = '<div class = "dialog">' + '<div class = "dialog-content">' + '<p>' + text + '</p>' + '</div>' + '<div class = "dialog-btn">' + polyglot.t('app.dialog.ok') + '</div>' + '</div>';

	        shelter.addClass('dialog-shelter fadeIn');
	        shelter[0].innerHTML = html;
	        document.body.appendChild(shelter[0]);
	        $('.dialog-btn').on('mouseup', function () {
	            _this.removeDialog(shelter);
	        });
	    },

	    confirm: function confirm(txt, func, titile, closeFn) {
	        var _this = this,
	            html;

	        html = '<div class = "modal confirm-modal">' + '<div class = "modal-content"><h3>' + (titile || polyglot.t('app.dialog.title')) + '</h3>' + '<p>' + txt + '</p></div>' + '<div class = "modal-btn-group">' + '<div class = "confirm-btn close"><a href="javascript:;">' + polyglot.t('app.dialog.cancel') + '</a></div>' + '<div class = "confirm-btn ok"><a href="javascript:;">' + polyglot.t('app.dialog.ok') + '</a></div>' + '</div>' + '</div>';

	        $('body').append($(html).addClass('zoomIn'));
	        $('.modal-bg').show(); // bg
	        $('.confirm-btn.close').on('click', function () {
	            if (closeFn) closeFn();
	            _this.closeModal();
	        });
	        $('.confirm-btn.ok').on('click', function () {
	            if (func) func();
	            _this.closeModal();
	        });
	        WorkPlus.lockNavigation();
	    },

	    alert: function alert(txt, func, btnTxt, title) {
	        var _this = this,
	            _btnTxt = btnTxt ? btnTxt : polyglot.t('app.dialog.ok'),
	            html;

	        html = '<div class = "modal confirm-modal">' + '<div class = "modal-content"><h3>' + (title || polyglot.t('app.dialog.title')) + '</h3>' + '<p>' + txt + '</p></div>' + '<div class = "modal-btn-group">' + '<div class = "confirm-btn ok"><a class="alert-btn" href="javascript:;">' + _btnTxt + '</a></div>' + '</div>' + '</div>';

	        $('body').append($(html).addClass('zoomIn'));
	        $('.modal-bg').show(); // bg
	        $('.confirm-btn.ok').on('click', function () {
	            func && func();
	            _this.closeModal();
	        });
	        WorkPlus.lockNavigation();
	    },

	    letterCodeModal: function letterCodeModal(imgsrc, func) {
	        var _this = this,
	            html;

	        html = '<div class = "modal letter-code-modal confirm-modal">' + '<div class = "modal-content"><h3>' + polyglot.t('app.dialog.codeTitle') + '</h3>' + '<p class="letter-code-img"><img src="' + imgsrc + '&time=' + new Date().getTime() + '" /></p><p><input val="" placeholder="' + polyglot.t('app.dialog.codePlaceholder') + '" /></p></div>' + '<div class = "modal-btn-group">' + '<div class = "confirm-btn close"><a href="javascript:;">' + polyglot.t('app.dialog.cancel') + '</a></div>' + '<div class = "confirm-btn ok"><a href="javascript:;">' + polyglot.t('app.dialog.ContinueToSend') + '</a></div>' + '</div>' + '</div>';

	        $('body').append($(html).addClass('zoomIn'));
	        $('.modal-bg').show(); // bg

	        $('.letter-code-img').on('click', function () {
	            console.log('change the letter code!');
	            var _elm = $('p.letter-code-img');
	            _elm.html($('<img src="' + imgsrc + '&time=' + new Date().getTime() + '" />'));
	        });
	        $('.confirm-btn.close').on('click', function () {
	            _this.closeModal();
	        });
	        $('.confirm-btn.ok').on('click', function () {
	            var _val = $('.letter-code-modal input').val().trim();
	            func(_val);
	        });
	        WorkPlus.lockNavigation();
	    },

	    createPromptDialog: function createPromptDialog(txt, placeholder, btn) {
	        var _this = this,
	            html;

	        html = '<div class = "modal prompt-dialog">' + '<div class = "dialog-content prompt-dialog-content">' + '<p>' + txt + '</p>' + '<div class = "input-label"><span>' + polyglot.t('joinOrg.dialog.name') + '</span><input type="text" value="' + placeholder + '"></div>' + '</div><div class="button-group">' + '<div class = "dialog-btn dialog-prompt-btn close">' + polyglot.t('app.dialog.cancel') + '</div>' + '<div class = "dialog-btn dialog-prompt-btn ok">' + btn.txt || polyglot.t('app.dialog.ok') + '</div></div>';

	        $('body').append($(html).addClass('zoomIn'));
	        $('.modal-bg').show(); // bg

	        setTimeout(function () {
	            $('.prompt-dialog input').focus();
	            $('.prompt-dialog input')[0].setSelectionRange(placeholder.length, placeholder.length);
	        }, 300);
	        $('.dialog-prompt-btn.close').on('click', function () {
	            _this.closeModal();
	        });
	        $('.dialog-prompt-btn.ok').on('click', function () {
	            var _val = $('.prompt-dialog input').val().trim();
	            if (_val === '') return;
	            btn.func(_val);
	        });
	        WorkPlus.lockNavigation();
	    },

	    showErrorMsg: function showErrorMsg() {
	        var div = $('<div>'),
	            label = $('<label>');

	        label[0].innerHTML = '网络不给力，请稍后再试';
	        div.addClass('offline-hint fadeInAndOut');
	        div[0].appendChild(label[0]);
	        document.body.appendChild(div[0]);
	    },

	    showPopup: function showPopup(txt, time) {
	        var html = '<div class="modal popup">' + txt + '</div>',
	            _popup = $('.modal.popup');
	        if (_popup.length === 0) {
	            $('body').append($(html).addClass('zoomIn'));
	            _popup = $('.modal.popup');
	        } else {
	            _popup.html(txt);
	        }
	        _popup.css('left', ($('body').width() - _popup.width()) / 2);
	        if (window.POPUP_TIMEOUT) {
	            clearTimeout(window.POPUP_TIMEOUT);
	        }
	        window.POPUP_TIMEOUT = setTimeout(function () {
	            _popup.remove();
	        }, time || 1000);
	    },

	    // @params sheet => array
	    actionSheet: function actionSheet(sheet) {
	        var html = '<div class="modal action-sheet"><ul>{{sheets}}<li class="cancel-sheet"><a href="javascript:;">' + polyglot.t('app.dialog.cancel') + '</a></li></ul></div>',
	            item = '',
	            _this = this;
	        if (sheet.length === 0) return;
	        for (var i = 0, l = sheet.length; i < l; i++) {
	            item += '<li class="normal-sheet"><a href="javascript:;">' + sheet[i].text + '</a></li>';
	        };
	        html = html.replace(/{{sheets}}/, item);
	        $('.modal-bg').show(); // bg
	        $('body').append($(html).addClass('slideOutUp'));
	        $('.action-sheet').find('ul li.normal-sheet').each(function (index, item) {
	            $(item).on('click', sheet[index].action);
	        });
	        $('.cancel-sheet, .modal-bg').on('click', closeActionSheet);

	        function closeActionSheet() {
	            $('.action-sheet').addClass('slideOutDown');
	            $('.action-sheet').one('webkitAnimationEnd animationend', function () {
	                _this.closeModal();
	            });
	        };
	        WorkPlus.lockNavigation();
	    },

	    closeModal: function closeModal() {
	        if ($('.modal-bg').length !== 0) $('.modal-bg').hide();
	        $('.modal').remove();
	        WorkPlus.unLockNavigation();
	    },

	    loading: function loading(text) {
	        var self = this;
	        var html = '<div class="modal loading">' + '<div class="loading__loader">' + '<div class="loader-inner ball-spin-fade-loader">' + '<div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div>' + '</div>' + '</div>' + '<div class="loading__text"><p>' + (text || "加载中...") + '</p></div>' + '</div>';

	        $('body').append($(html).addClass('zoomIn'));
	        $('.modal-bg').show();

	        return {
	            close: function close() {
	                self.closeModal();
	            }
	        };
	    },

	    toast: function toast(type, text) {
	        if (!text) {
	            text = type;
	            type = 'success';
	        }
	        var self = this;
	        var html = '<div class="modal loading">' + '<div class="loading__loader">' + '<div class="loading__icon ' + type + '"></div>' + '</div>' + '<div class="loading__text"><p>' + text + '</p></div>' + '</div>';

	        $('body').append($(html).addClass('zoomIn'));
	        $('.modal-bg').show();

	        setTimeout(self.closeModal, 1200);

	        return {
	            close: function close() {
	                self.closeModal();
	            }
	        };
	    }
	};

/***/ },
/* 16 */
2,
/* 17 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Polyglot = __webpack_require__(18);

	var zhCN = __webpack_require__(41);
	var en = __webpack_require__(42);
	var zhrTW = __webpack_require__(43);

	var langs = {
	    'zh-CN': zhCN,
	    'en': en,
	    'zh-rTW': zhrTW
	};

	var addClassToHtmlTag = function addClassToHtmlTag(lng) {
	    $('html').addClass('lng-' + lng);
	};

	module.exports = {
	    initI18n: function initI18n(phrases, lng) {
	        var polyglot = new Polyglot({
	            phrases: phrases,
	            locale: lng
	        });
	        polyglot.extend({
	            "app": langs[lng].app
	        });
	        addClassToHtmlTag(lng);
	        return polyglot;
	    }
	};

/***/ },
/* 18 */
/***/ function(module, exports, __webpack_require__) {

	//     (c) 2012-2016 Airbnb, Inc.
	//
	//     polyglot.js may be freely distributed under the terms of the BSD
	//     license. For all licensing information, details, and documention:
	//     http://airbnb.github.com/polyglot.js
	//
	//
	// Polyglot.js is an I18n helper library written in JavaScript, made to
	// work both in the browser and in Node. It provides a simple solution for
	// interpolation and pluralization, based off of Airbnb's
	// experience adding I18n functionality to its Backbone.js and Node apps.
	//
	// Polylglot is agnostic to your translation backend. It doesn't perform any
	// translation; it simply gives you a way to manage translated phrases from
	// your client- or server-side JavaScript application.
	//

	'use strict';

	var forEach = __webpack_require__(19);
	var warning = __webpack_require__(21);
	var has = __webpack_require__(22);
	var trim = __webpack_require__(25);

	var warn = function warn(message) {
	  warning(false, message);
	};

	var replace = String.prototype.replace;
	var split = String.prototype.split;

	// #### Pluralization methods
	// The string that separates the different phrase possibilities.
	var delimeter = '||||';

	// Mapping from pluralization group plural logic.
	var pluralTypes = {
	  arabic: function (n) {
	    // http://www.arabeyes.org/Plural_Forms
	    if (n < 3) { return n; }
	    if (n % 100 >= 3 && n % 100 <= 10) return 3;
	    return n % 100 >= 11 ? 4 : 5;
	  },
	  chinese: function () { return 0; },
	  german: function (n) { return n !== 1 ? 1 : 0; },
	  french: function (n) { return n > 1 ? 1 : 0; },
	  russian: function (n) {
	    if (n % 10 === 1 && n % 100 !== 11) { return 0; }
	    return n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20) ? 1 : 2;
	  },
	  czech: function (n) {
	    if (n === 1) { return 0; }
	    return (n >= 2 && n <= 4) ? 1 : 2;
	  },
	  polish: function (n) {
	    if (n === 1) { return 0; }
	    return n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20) ? 1 : 2;
	  },
	  icelandic: function (n) { return (n % 10 !== 1 || n % 100 === 11) ? 1 : 0; }
	};

	// Mapping from pluralization group to individual locales.
	var pluralTypeToLanguages = {
	  arabic: ['ar'],
	  chinese: ['fa', 'id', 'ja', 'ko', 'lo', 'ms', 'th', 'tr', 'zh'],
	  german: ['da', 'de', 'en', 'es', 'fi', 'el', 'he', 'hu', 'it', 'nl', 'no', 'pt', 'sv'],
	  french: ['fr', 'tl', 'pt-br'],
	  russian: ['hr', 'ru', 'lt'],
	  czech: ['cs', 'sk'],
	  polish: ['pl'],
	  icelandic: ['is']
	};

	function langToTypeMap(mapping) {
	  var ret = {};
	  forEach(mapping, function (langs, type) {
	    forEach(langs, function (lang) {
	      ret[lang] = type;
	    });
	  });
	  return ret;
	}

	function pluralTypeName(locale) {
	  var langToPluralType = langToTypeMap(pluralTypeToLanguages);
	  return langToPluralType[locale]
	    || langToPluralType[split.call(locale, /-/, 1)[0]]
	    || langToPluralType.en;
	}

	function pluralTypeIndex(locale, count) {
	  return pluralTypes[pluralTypeName(locale)](count);
	}

	var dollarRegex = /\$/g;
	var dollarBillsYall = '$$';
	var tokenRegex = /%\{(.*?)\}/g;

	// ### transformPhrase(phrase, substitutions, locale)
	//
	// Takes a phrase string and transforms it by choosing the correct
	// plural form and interpolating it.
	//
	//     transformPhrase('Hello, %{name}!', {name: 'Spike'});
	//     // "Hello, Spike!"
	//
	// The correct plural form is selected if substitutions.smart_count
	// is set. You can pass in a number instead of an Object as `substitutions`
	// as a shortcut for `smart_count`.
	//
	//     transformPhrase('%{smart_count} new messages |||| 1 new message', {smart_count: 1}, 'en');
	//     // "1 new message"
	//
	//     transformPhrase('%{smart_count} new messages |||| 1 new message', {smart_count: 2}, 'en');
	//     // "2 new messages"
	//
	//     transformPhrase('%{smart_count} new messages |||| 1 new message', 5, 'en');
	//     // "5 new messages"
	//
	// You should pass in a third argument, the locale, to specify the correct plural type.
	// It defaults to `'en'` with 2 plural forms.
	function transformPhrase(phrase, substitutions, locale) {
	  if (typeof phrase !== 'string') {
	    throw new TypeError('Polyglot.transformPhrase expects argument #1 to be string');
	  }

	  if (substitutions == null) {
	    return phrase;
	  }

	  var result = phrase;

	  // allow number as a pluralization shortcut
	  var options = typeof substitutions === 'number' ? { smart_count: substitutions } : substitutions;

	  // Select plural form: based on a phrase text that contains `n`
	  // plural forms separated by `delimeter`, a `locale`, and a `substitutions.smart_count`,
	  // choose the correct plural form. This is only done if `count` is set.
	  if (options.smart_count != null && result) {
	    var texts = split.call(result, delimeter);
	    result = trim(texts[pluralTypeIndex(locale || 'en', options.smart_count)] || texts[0]);
	  }

	  // Interpolate: Creates a `RegExp` object for each interpolation placeholder.
	  result = replace.call(result, tokenRegex, function (expression, argument) {
	    if (!has(options, argument) || options[argument] == null) { return expression; }
	    // Ensure replacement value is escaped to prevent special $-prefixed regex replace tokens.
	    return replace.call(options[argument], dollarRegex, dollarBillsYall);
	  });

	  return result;
	}

	// ### Polyglot class constructor
	function Polyglot(options) {
	  var opts = options || {};
	  this.phrases = {};
	  this.extend(opts.phrases || {});
	  this.currentLocale = opts.locale || 'en';
	  var allowMissing = opts.allowMissing ? transformPhrase : null;
	  this.onMissingKey = typeof opts.onMissingKey === 'function' ? opts.onMissingKey : allowMissing;
	  this.warn = opts.warn || warn;
	}

	// ### polyglot.locale([locale])
	//
	// Get or set locale. Internally, Polyglot only uses locale for pluralization.
	Polyglot.prototype.locale = function (newLocale) {
	  if (newLocale) this.currentLocale = newLocale;
	  return this.currentLocale;
	};

	// ### polyglot.extend(phrases)
	//
	// Use `extend` to tell Polyglot how to translate a given key.
	//
	//     polyglot.extend({
	//       "hello": "Hello",
	//       "hello_name": "Hello, %{name}"
	//     });
	//
	// The key can be any string.  Feel free to call `extend` multiple times;
	// it will override any phrases with the same key, but leave existing phrases
	// untouched.
	//
	// It is also possible to pass nested phrase objects, which get flattened
	// into an object with the nested keys concatenated using dot notation.
	//
	//     polyglot.extend({
	//       "nav": {
	//         "hello": "Hello",
	//         "hello_name": "Hello, %{name}",
	//         "sidebar": {
	//           "welcome": "Welcome"
	//         }
	//       }
	//     });
	//
	//     console.log(polyglot.phrases);
	//     // {
	//     //   'nav.hello': 'Hello',
	//     //   'nav.hello_name': 'Hello, %{name}',
	//     //   'nav.sidebar.welcome': 'Welcome'
	//     // }
	//
	// `extend` accepts an optional second argument, `prefix`, which can be used
	// to prefix every key in the phrases object with some string, using dot
	// notation.
	//
	//     polyglot.extend({
	//       "hello": "Hello",
	//       "hello_name": "Hello, %{name}"
	//     }, "nav");
	//
	//     console.log(polyglot.phrases);
	//     // {
	//     //   'nav.hello': 'Hello',
	//     //   'nav.hello_name': 'Hello, %{name}'
	//     // }
	//
	// This feature is used internally to support nested phrase objects.
	Polyglot.prototype.extend = function (morePhrases, prefix) {
	  forEach(morePhrases, function (phrase, key) {
	    var prefixedKey = prefix ? prefix + '.' + key : key;
	    if (typeof phrase === 'object') {
	      this.extend(phrase, prefixedKey);
	    } else {
	      this.phrases[prefixedKey] = phrase;
	    }
	  }, this);
	};

	// ### polyglot.unset(phrases)
	// Use `unset` to selectively remove keys from a polyglot instance.
	//
	//     polyglot.unset("some_key");
	//     polyglot.unset({
	//       "hello": "Hello",
	//       "hello_name": "Hello, %{name}"
	//     });
	//
	// The unset method can take either a string (for the key), or an object hash with
	// the keys that you would like to unset.
	Polyglot.prototype.unset = function (morePhrases, prefix) {
	  if (typeof morePhrases === 'string') {
	    delete this.phrases[morePhrases];
	  } else {
	    forEach(morePhrases, function (phrase, key) {
	      var prefixedKey = prefix ? prefix + '.' + key : key;
	      if (typeof phrase === 'object') {
	        this.unset(phrase, prefixedKey);
	      } else {
	        delete this.phrases[prefixedKey];
	      }
	    }, this);
	  }
	};

	// ### polyglot.clear()
	//
	// Clears all phrases. Useful for special cases, such as freeing
	// up memory if you have lots of phrases but no longer need to
	// perform any translation. Also used internally by `replace`.
	Polyglot.prototype.clear = function () {
	  this.phrases = {};
	};

	// ### polyglot.replace(phrases)
	//
	// Completely replace the existing phrases with a new set of phrases.
	// Normally, just use `extend` to add more phrases, but under certain
	// circumstances, you may want to make sure no old phrases are lying around.
	Polyglot.prototype.replace = function (newPhrases) {
	  this.clear();
	  this.extend(newPhrases);
	};


	// ### polyglot.t(key, options)
	//
	// The most-used method. Provide a key, and `t` will return the
	// phrase.
	//
	//     polyglot.t("hello");
	//     => "Hello"
	//
	// The phrase value is provided first by a call to `polyglot.extend()` or
	// `polyglot.replace()`.
	//
	// Pass in an object as the second argument to perform interpolation.
	//
	//     polyglot.t("hello_name", {name: "Spike"});
	//     => "Hello, Spike"
	//
	// If you like, you can provide a default value in case the phrase is missing.
	// Use the special option key "_" to specify a default.
	//
	//     polyglot.t("i_like_to_write_in_language", {
	//       _: "I like to write in %{language}.",
	//       language: "JavaScript"
	//     });
	//     => "I like to write in JavaScript."
	//
	Polyglot.prototype.t = function (key, options) {
	  var phrase, result;
	  var opts = options == null ? {} : options;
	  if (typeof this.phrases[key] === 'string') {
	    phrase = this.phrases[key];
	  } else if (typeof opts._ === 'string') {
	    phrase = opts._;
	  } else if (this.onMissingKey) {
	    var onMissingKey = this.onMissingKey;
	    result = onMissingKey(key, opts, this.currentLocale);
	  } else {
	    this.warn('Missing translation for key: "' + key + '"');
	    result = key;
	  }
	  if (typeof phrase === 'string') {
	    result = transformPhrase(phrase, opts, this.currentLocale);
	  }
	  return result;
	};


	// ### polyglot.has(key)
	//
	// Check if polyglot has a translation for given key
	Polyglot.prototype.has = function (key) {
	  return has(this.phrases, key);
	};

	// export transformPhrase
	Polyglot.transformPhrase = transformPhrase;

	module.exports = Polyglot;


/***/ },
/* 19 */
/***/ function(module, exports, __webpack_require__) {

	var isFunction = __webpack_require__(20)

	module.exports = forEach

	var toString = Object.prototype.toString
	var hasOwnProperty = Object.prototype.hasOwnProperty

	function forEach(list, iterator, context) {
	    if (!isFunction(iterator)) {
	        throw new TypeError('iterator must be a function')
	    }

	    if (arguments.length < 3) {
	        context = this
	    }
	    
	    if (toString.call(list) === '[object Array]')
	        forEachArray(list, iterator, context)
	    else if (typeof list === 'string')
	        forEachString(list, iterator, context)
	    else
	        forEachObject(list, iterator, context)
	}

	function forEachArray(array, iterator, context) {
	    for (var i = 0, len = array.length; i < len; i++) {
	        if (hasOwnProperty.call(array, i)) {
	            iterator.call(context, array[i], i, array)
	        }
	    }
	}

	function forEachString(string, iterator, context) {
	    for (var i = 0, len = string.length; i < len; i++) {
	        // no such thing as a sparse string.
	        iterator.call(context, string.charAt(i), i, string)
	    }
	}

	function forEachObject(object, iterator, context) {
	    for (var k in object) {
	        if (hasOwnProperty.call(object, k)) {
	            iterator.call(context, object[k], k, object)
	        }
	    }
	}


/***/ },
/* 20 */
/***/ function(module, exports) {

	module.exports = isFunction

	var toString = Object.prototype.toString

	function isFunction (fn) {
	  var string = toString.call(fn)
	  return string === '[object Function]' ||
	    (typeof fn === 'function' && string !== '[object RegExp]') ||
	    (typeof window !== 'undefined' &&
	     // IE8 and below
	     (fn === window.setTimeout ||
	      fn === window.alert ||
	      fn === window.confirm ||
	      fn === window.prompt))
	};


/***/ },
/* 21 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * Copyright 2014-2015, Facebook, Inc.
	 * All rights reserved.
	 *
	 * This source code is licensed under the BSD-style license found in the
	 * LICENSE file in the root directory of this source tree. An additional grant
	 * of patent rights can be found in the PATENTS file in the same directory.
	 */

	'use strict';

	/**
	 * Similar to invariant but only logs a warning if the condition is not met.
	 * This can be used to log issues in development environments in critical
	 * paths. Removing the logging code for production environments will keep the
	 * same logic and follow the same code paths.
	 */

	var warning = function() {};

	if (false) {
	  warning = function(condition, format, args) {
	    var len = arguments.length;
	    args = new Array(len > 2 ? len - 2 : 0);
	    for (var key = 2; key < len; key++) {
	      args[key - 2] = arguments[key];
	    }
	    if (format === undefined) {
	      throw new Error(
	        '`warning(condition, format, ...args)` requires a warning ' +
	        'message argument'
	      );
	    }

	    if (format.length < 10 || (/^[s\W]*$/).test(format)) {
	      throw new Error(
	        'The warning format should be able to uniquely identify this ' +
	        'warning. Please, use a more descriptive format than: ' + format
	      );
	    }

	    if (!condition) {
	      var argIndex = 0;
	      var message = 'Warning: ' +
	        format.replace(/%s/g, function() {
	          return args[argIndex++];
	        });
	      if (typeof console !== 'undefined') {
	        console.error(message);
	      }
	      try {
	        // This error was thrown as a convenience so that you can use this stack
	        // to find the callsite that caused this warning to fire.
	        throw new Error(message);
	      } catch(x) {}
	    }
	  };
	}

	module.exports = warning;


/***/ },
/* 22 */
/***/ function(module, exports, __webpack_require__) {

	var bind = __webpack_require__(23);

	module.exports = bind.call(Function.call, Object.prototype.hasOwnProperty);


/***/ },
/* 23 */
/***/ function(module, exports, __webpack_require__) {

	var implementation = __webpack_require__(24);

	module.exports = Function.prototype.bind || implementation;


/***/ },
/* 24 */
/***/ function(module, exports) {

	var ERROR_MESSAGE = 'Function.prototype.bind called on incompatible ';
	var slice = Array.prototype.slice;
	var toStr = Object.prototype.toString;
	var funcType = '[object Function]';

	module.exports = function bind(that) {
	    var target = this;
	    if (typeof target !== 'function' || toStr.call(target) !== funcType) {
	        throw new TypeError(ERROR_MESSAGE + target);
	    }
	    var args = slice.call(arguments, 1);

	    var bound;
	    var binder = function () {
	        if (this instanceof bound) {
	            var result = target.apply(
	                this,
	                args.concat(slice.call(arguments))
	            );
	            if (Object(result) === result) {
	                return result;
	            }
	            return this;
	        } else {
	            return target.apply(
	                that,
	                args.concat(slice.call(arguments))
	            );
	        }
	    };

	    var boundLength = Math.max(0, target.length - args.length);
	    var boundArgs = [];
	    for (var i = 0; i < boundLength; i++) {
	        boundArgs.push('$' + i);
	    }

	    bound = Function('binder', 'return function (' + boundArgs.join(',') + '){ return binder.apply(this,arguments); }')(binder);

	    if (target.prototype) {
	        var Empty = function Empty() {};
	        Empty.prototype = target.prototype;
	        bound.prototype = new Empty();
	        Empty.prototype = null;
	    }

	    return bound;
	};


/***/ },
/* 25 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var bind = __webpack_require__(23);
	var define = __webpack_require__(26);

	var implementation = __webpack_require__(30);
	var getPolyfill = __webpack_require__(39);
	var shim = __webpack_require__(40);

	var boundTrim = bind.call(Function.call, getPolyfill());

	define(boundTrim, {
		getPolyfill: getPolyfill,
		implementation: implementation,
		shim: shim
	});

	module.exports = boundTrim;


/***/ },
/* 26 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var keys = __webpack_require__(27);
	var foreach = __webpack_require__(29);
	var hasSymbols = typeof Symbol === 'function' && typeof Symbol() === 'symbol';

	var toStr = Object.prototype.toString;

	var isFunction = function (fn) {
		return typeof fn === 'function' && toStr.call(fn) === '[object Function]';
	};

	var arePropertyDescriptorsSupported = function () {
		var obj = {};
		try {
			Object.defineProperty(obj, 'x', { enumerable: false, value: obj });
	        /* eslint-disable no-unused-vars, no-restricted-syntax */
	        for (var _ in obj) { return false; }
	        /* eslint-enable no-unused-vars, no-restricted-syntax */
			return obj.x === obj;
		} catch (e) { /* this is IE 8. */
			return false;
		}
	};
	var supportsDescriptors = Object.defineProperty && arePropertyDescriptorsSupported();

	var defineProperty = function (object, name, value, predicate) {
		if (name in object && (!isFunction(predicate) || !predicate())) {
			return;
		}
		if (supportsDescriptors) {
			Object.defineProperty(object, name, {
				configurable: true,
				enumerable: false,
				value: value,
				writable: true
			});
		} else {
			object[name] = value;
		}
	};

	var defineProperties = function (object, map) {
		var predicates = arguments.length > 2 ? arguments[2] : {};
		var props = keys(map);
		if (hasSymbols) {
			props = props.concat(Object.getOwnPropertySymbols(map));
		}
		foreach(props, function (name) {
			defineProperty(object, name, map[name], predicates[name]);
		});
	};

	defineProperties.supportsDescriptors = !!supportsDescriptors;

	module.exports = defineProperties;


/***/ },
/* 27 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	// modified from https://github.com/es-shims/es5-shim
	var has = Object.prototype.hasOwnProperty;
	var toStr = Object.prototype.toString;
	var slice = Array.prototype.slice;
	var isArgs = __webpack_require__(28);
	var isEnumerable = Object.prototype.propertyIsEnumerable;
	var hasDontEnumBug = !isEnumerable.call({ toString: null }, 'toString');
	var hasProtoEnumBug = isEnumerable.call(function () {}, 'prototype');
	var dontEnums = [
		'toString',
		'toLocaleString',
		'valueOf',
		'hasOwnProperty',
		'isPrototypeOf',
		'propertyIsEnumerable',
		'constructor'
	];
	var equalsConstructorPrototype = function (o) {
		var ctor = o.constructor;
		return ctor && ctor.prototype === o;
	};
	var excludedKeys = {
		$console: true,
		$external: true,
		$frame: true,
		$frameElement: true,
		$frames: true,
		$innerHeight: true,
		$innerWidth: true,
		$outerHeight: true,
		$outerWidth: true,
		$pageXOffset: true,
		$pageYOffset: true,
		$parent: true,
		$scrollLeft: true,
		$scrollTop: true,
		$scrollX: true,
		$scrollY: true,
		$self: true,
		$webkitIndexedDB: true,
		$webkitStorageInfo: true,
		$window: true
	};
	var hasAutomationEqualityBug = (function () {
		/* global window */
		if (typeof window === 'undefined') { return false; }
		for (var k in window) {
			try {
				if (!excludedKeys['$' + k] && has.call(window, k) && window[k] !== null && typeof window[k] === 'object') {
					try {
						equalsConstructorPrototype(window[k]);
					} catch (e) {
						return true;
					}
				}
			} catch (e) {
				return true;
			}
		}
		return false;
	}());
	var equalsConstructorPrototypeIfNotBuggy = function (o) {
		/* global window */
		if (typeof window === 'undefined' || !hasAutomationEqualityBug) {
			return equalsConstructorPrototype(o);
		}
		try {
			return equalsConstructorPrototype(o);
		} catch (e) {
			return false;
		}
	};

	var keysShim = function keys(object) {
		var isObject = object !== null && typeof object === 'object';
		var isFunction = toStr.call(object) === '[object Function]';
		var isArguments = isArgs(object);
		var isString = isObject && toStr.call(object) === '[object String]';
		var theKeys = [];

		if (!isObject && !isFunction && !isArguments) {
			throw new TypeError('Object.keys called on a non-object');
		}

		var skipProto = hasProtoEnumBug && isFunction;
		if (isString && object.length > 0 && !has.call(object, 0)) {
			for (var i = 0; i < object.length; ++i) {
				theKeys.push(String(i));
			}
		}

		if (isArguments && object.length > 0) {
			for (var j = 0; j < object.length; ++j) {
				theKeys.push(String(j));
			}
		} else {
			for (var name in object) {
				if (!(skipProto && name === 'prototype') && has.call(object, name)) {
					theKeys.push(String(name));
				}
			}
		}

		if (hasDontEnumBug) {
			var skipConstructor = equalsConstructorPrototypeIfNotBuggy(object);

			for (var k = 0; k < dontEnums.length; ++k) {
				if (!(skipConstructor && dontEnums[k] === 'constructor') && has.call(object, dontEnums[k])) {
					theKeys.push(dontEnums[k]);
				}
			}
		}
		return theKeys;
	};

	keysShim.shim = function shimObjectKeys() {
		if (Object.keys) {
			var keysWorksWithArguments = (function () {
				// Safari 5.0 bug
				return (Object.keys(arguments) || '').length === 2;
			}(1, 2));
			if (!keysWorksWithArguments) {
				var originalKeys = Object.keys;
				Object.keys = function keys(object) {
					if (isArgs(object)) {
						return originalKeys(slice.call(object));
					} else {
						return originalKeys(object);
					}
				};
			}
		} else {
			Object.keys = keysShim;
		}
		return Object.keys || keysShim;
	};

	module.exports = keysShim;


/***/ },
/* 28 */
/***/ function(module, exports) {

	'use strict';

	var toStr = Object.prototype.toString;

	module.exports = function isArguments(value) {
		var str = toStr.call(value);
		var isArgs = str === '[object Arguments]';
		if (!isArgs) {
			isArgs = str !== '[object Array]' &&
				value !== null &&
				typeof value === 'object' &&
				typeof value.length === 'number' &&
				value.length >= 0 &&
				toStr.call(value.callee) === '[object Function]';
		}
		return isArgs;
	};


/***/ },
/* 29 */
/***/ function(module, exports) {

	
	var hasOwn = Object.prototype.hasOwnProperty;
	var toString = Object.prototype.toString;

	module.exports = function forEach (obj, fn, ctx) {
	    if (toString.call(fn) !== '[object Function]') {
	        throw new TypeError('iterator must be a function');
	    }
	    var l = obj.length;
	    if (l === +l) {
	        for (var i = 0; i < l; i++) {
	            fn.call(ctx, obj[i], i, obj);
	        }
	    } else {
	        for (var k in obj) {
	            if (hasOwn.call(obj, k)) {
	                fn.call(ctx, obj[k], k, obj);
	            }
	        }
	    }
	};



/***/ },
/* 30 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var bind = __webpack_require__(23);
	var ES = __webpack_require__(31);
	var replace = bind.call(Function.call, String.prototype.replace);

	var leftWhitespace = /^[\x09\x0A\x0B\x0C\x0D\x20\xA0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000\u2028\u2029\uFEFF]+/;
	var rightWhitespace = /[\x09\x0A\x0B\x0C\x0D\x20\xA0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000\u2028\u2029\uFEFF]+$/;

	module.exports = function trim() {
		var S = ES.ToString(ES.CheckObjectCoercible(this));
		return replace(replace(S, leftWhitespace, ''), rightWhitespace, '');
	};


/***/ },
/* 31 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var $isNaN = __webpack_require__(32);
	var $isFinite = __webpack_require__(33);

	var sign = __webpack_require__(34);
	var mod = __webpack_require__(35);

	var IsCallable = __webpack_require__(36);
	var toPrimitive = __webpack_require__(37);

	// https://es5.github.io/#x9
	var ES5 = {
		ToPrimitive: toPrimitive,

		ToBoolean: function ToBoolean(value) {
			return Boolean(value);
		},
		ToNumber: function ToNumber(value) {
			return Number(value);
		},
		ToInteger: function ToInteger(value) {
			var number = this.ToNumber(value);
			if ($isNaN(number)) { return 0; }
			if (number === 0 || !$isFinite(number)) { return number; }
			return sign(number) * Math.floor(Math.abs(number));
		},
		ToInt32: function ToInt32(x) {
			return this.ToNumber(x) >> 0;
		},
		ToUint32: function ToUint32(x) {
			return this.ToNumber(x) >>> 0;
		},
		ToUint16: function ToUint16(value) {
			var number = this.ToNumber(value);
			if ($isNaN(number) || number === 0 || !$isFinite(number)) { return 0; }
			var posInt = sign(number) * Math.floor(Math.abs(number));
			return mod(posInt, 0x10000);
		},
		ToString: function ToString(value) {
			return String(value);
		},
		ToObject: function ToObject(value) {
			this.CheckObjectCoercible(value);
			return Object(value);
		},
		CheckObjectCoercible: function CheckObjectCoercible(value, optMessage) {
			/* jshint eqnull:true */
			if (value == null) {
				throw new TypeError(optMessage || 'Cannot call method on ' + value);
			}
			return value;
		},
		IsCallable: IsCallable,
		SameValue: function SameValue(x, y) {
			if (x === y) { // 0 === -0, but they are not identical.
				if (x === 0) { return 1 / x === 1 / y; }
				return true;
			}
			return $isNaN(x) && $isNaN(y);
		},

		// http://www.ecma-international.org/ecma-262/5.1/#sec-8
		Type: function Type(x) {
			if (x === null) {
				return 'Null';
			}
			if (typeof x === 'undefined') {
				return 'Undefined';
			}
			if (typeof x === 'function' || typeof x === 'object') {
				return 'Object';
			}
			if (typeof x === 'number') {
				return 'Number';
			}
			if (typeof x === 'boolean') {
				return 'Boolean';
			}
			if (typeof x === 'string') {
				return 'String';
			}
		}
	};

	module.exports = ES5;


/***/ },
/* 32 */
/***/ function(module, exports) {

	module.exports = Number.isNaN || function isNaN(a) {
		return a !== a;
	};


/***/ },
/* 33 */
/***/ function(module, exports) {

	var $isNaN = Number.isNaN || function (a) { return a !== a; };

	module.exports = Number.isFinite || function (x) { return typeof x === 'number' && !$isNaN(x) && x !== Infinity && x !== -Infinity; };


/***/ },
/* 34 */
/***/ function(module, exports) {

	module.exports = function sign(number) {
		return number >= 0 ? 1 : -1;
	};


/***/ },
/* 35 */
/***/ function(module, exports) {

	module.exports = function mod(number, modulo) {
		var remain = number % modulo;
		return Math.floor(remain >= 0 ? remain : remain + modulo);
	};


/***/ },
/* 36 */
/***/ function(module, exports) {

	'use strict';

	var fnToStr = Function.prototype.toString;

	var constructorRegex = /^\s*class /;
	var isES6ClassFn = function isES6ClassFn(value) {
		try {
			var fnStr = fnToStr.call(value);
			var singleStripped = fnStr.replace(/\/\/.*\n/g, '');
			var multiStripped = singleStripped.replace(/\/\*[.\s\S]*\*\//g, '');
			var spaceStripped = multiStripped.replace(/\n/mg, ' ').replace(/ {2}/g, ' ');
			return constructorRegex.test(spaceStripped);
		} catch (e) {
			return false; // not a function
		}
	};

	var tryFunctionObject = function tryFunctionObject(value) {
		try {
			if (isES6ClassFn(value)) { return false; }
			fnToStr.call(value);
			return true;
		} catch (e) {
			return false;
		}
	};
	var toStr = Object.prototype.toString;
	var fnClass = '[object Function]';
	var genClass = '[object GeneratorFunction]';
	var hasToStringTag = typeof Symbol === 'function' && typeof Symbol.toStringTag === 'symbol';

	module.exports = function isCallable(value) {
		if (!value) { return false; }
		if (typeof value !== 'function' && typeof value !== 'object') { return false; }
		if (hasToStringTag) { return tryFunctionObject(value); }
		if (isES6ClassFn(value)) { return false; }
		var strClass = toStr.call(value);
		return strClass === fnClass || strClass === genClass;
	};


/***/ },
/* 37 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var toStr = Object.prototype.toString;

	var isPrimitive = __webpack_require__(38);

	var isCallable = __webpack_require__(36);

	// https://es5.github.io/#x8.12
	var ES5internalSlots = {
		'[[DefaultValue]]': function (O, hint) {
			var actualHint = hint || (toStr.call(O) === '[object Date]' ? String : Number);

			if (actualHint === String || actualHint === Number) {
				var methods = actualHint === String ? ['toString', 'valueOf'] : ['valueOf', 'toString'];
				var value, i;
				for (i = 0; i < methods.length; ++i) {
					if (isCallable(O[methods[i]])) {
						value = O[methods[i]]();
						if (isPrimitive(value)) {
							return value;
						}
					}
				}
				throw new TypeError('No default value');
			}
			throw new TypeError('invalid [[DefaultValue]] hint supplied');
		}
	};

	// https://es5.github.io/#x9
	module.exports = function ToPrimitive(input, PreferredType) {
		if (isPrimitive(input)) {
			return input;
		}
		return ES5internalSlots['[[DefaultValue]]'](input, PreferredType);
	};


/***/ },
/* 38 */
/***/ function(module, exports) {

	module.exports = function isPrimitive(value) {
		return value === null || (typeof value !== 'function' && typeof value !== 'object');
	};


/***/ },
/* 39 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var implementation = __webpack_require__(30);

	var zeroWidthSpace = '\u200b';

	module.exports = function getPolyfill() {
		if (String.prototype.trim && zeroWidthSpace.trim() === zeroWidthSpace) {
			return String.prototype.trim;
		}
		return implementation;
	};


/***/ },
/* 40 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var define = __webpack_require__(26);
	var getPolyfill = __webpack_require__(39);

	module.exports = function shimStringTrim() {
		var polyfill = getPolyfill();
		define(String.prototype, { trim: polyfill }, { trim: function () { return String.prototype.trim !== polyfill; } });
		return polyfill;
	};


/***/ },
/* 41 */
/***/ function(module, exports) {

	module.exports = {
		"app": {
			"dialog": {
				"title": "提示",
				"cancel": "取消",
				"ok": "确定",
				"codePlaceholder": "验证码",
				"codeTitle": "请输入验证码",
				"ContinueToSend": "继续发送"
			},
			"xhr": {
				"error": "网络不给力"
			}
		}
	};

/***/ },
/* 42 */
/***/ function(module, exports) {

	module.exports = {
		"app": {
			"dialog": {
				"title": "Tips",
				"cancel": "Cancel",
				"ok": "Ok",
				"codePlaceholder": "SMS code",
				"codeTitle": "Enter SMS code",
				"ContinueToSend": "Continue"
			},
			"xhr": {
				"error": "Network is unstable"
			}
		}
	};

/***/ },
/* 43 */
/***/ function(module, exports) {

	module.exports = {
		"app": {
			"dialog": {
				"title": "提示",
				"cancel": "取消",
				"ok": "確認",
				"codePlaceholder": "驗證碼",
				"codeTitle": "請輸入驗證碼",
				"ContinueToSend": "繼續發送"
			},
			"xhr": {
				"error": "網路不給力"
			}
		}
	};

/***/ },
/* 44 */
/***/ function(module, exports, __webpack_require__) {

	var __WEBPACK_AMD_DEFINE_ARRAY__, __WEBPACK_AMD_DEFINE_RESULT__;//     Underscore.js 1.8.3
	//     http://underscorejs.org
	//     (c) 2009-2015 Jeremy Ashkenas, DocumentCloud and Investigative Reporters & Editors
	//     Underscore may be freely distributed under the MIT license.

	(function() {

	  // Baseline setup
	  // --------------

	  // Establish the root object, `window` in the browser, or `exports` on the server.
	  var root = this;

	  // Save the previous value of the `_` variable.
	  var previousUnderscore = root._;

	  // Save bytes in the minified (but not gzipped) version:
	  var ArrayProto = Array.prototype, ObjProto = Object.prototype, FuncProto = Function.prototype;

	  // Create quick reference variables for speed access to core prototypes.
	  var
	    push             = ArrayProto.push,
	    slice            = ArrayProto.slice,
	    toString         = ObjProto.toString,
	    hasOwnProperty   = ObjProto.hasOwnProperty;

	  // All **ECMAScript 5** native function implementations that we hope to use
	  // are declared here.
	  var
	    nativeIsArray      = Array.isArray,
	    nativeKeys         = Object.keys,
	    nativeBind         = FuncProto.bind,
	    nativeCreate       = Object.create;

	  // Naked function reference for surrogate-prototype-swapping.
	  var Ctor = function(){};

	  // Create a safe reference to the Underscore object for use below.
	  var _ = function(obj) {
	    if (obj instanceof _) return obj;
	    if (!(this instanceof _)) return new _(obj);
	    this._wrapped = obj;
	  };

	  // Export the Underscore object for **Node.js**, with
	  // backwards-compatibility for the old `require()` API. If we're in
	  // the browser, add `_` as a global object.
	  if (true) {
	    if (typeof module !== 'undefined' && module.exports) {
	      exports = module.exports = _;
	    }
	    exports._ = _;
	  } else {
	    root._ = _;
	  }

	  // Current version.
	  _.VERSION = '1.8.3';

	  // Internal function that returns an efficient (for current engines) version
	  // of the passed-in callback, to be repeatedly applied in other Underscore
	  // functions.
	  var optimizeCb = function(func, context, argCount) {
	    if (context === void 0) return func;
	    switch (argCount == null ? 3 : argCount) {
	      case 1: return function(value) {
	        return func.call(context, value);
	      };
	      case 2: return function(value, other) {
	        return func.call(context, value, other);
	      };
	      case 3: return function(value, index, collection) {
	        return func.call(context, value, index, collection);
	      };
	      case 4: return function(accumulator, value, index, collection) {
	        return func.call(context, accumulator, value, index, collection);
	      };
	    }
	    return function() {
	      return func.apply(context, arguments);
	    };
	  };

	  // A mostly-internal function to generate callbacks that can be applied
	  // to each element in a collection, returning the desired result — either
	  // identity, an arbitrary callback, a property matcher, or a property accessor.
	  var cb = function(value, context, argCount) {
	    if (value == null) return _.identity;
	    if (_.isFunction(value)) return optimizeCb(value, context, argCount);
	    if (_.isObject(value)) return _.matcher(value);
	    return _.property(value);
	  };
	  _.iteratee = function(value, context) {
	    return cb(value, context, Infinity);
	  };

	  // An internal function for creating assigner functions.
	  var createAssigner = function(keysFunc, undefinedOnly) {
	    return function(obj) {
	      var length = arguments.length;
	      if (length < 2 || obj == null) return obj;
	      for (var index = 1; index < length; index++) {
	        var source = arguments[index],
	            keys = keysFunc(source),
	            l = keys.length;
	        for (var i = 0; i < l; i++) {
	          var key = keys[i];
	          if (!undefinedOnly || obj[key] === void 0) obj[key] = source[key];
	        }
	      }
	      return obj;
	    };
	  };

	  // An internal function for creating a new object that inherits from another.
	  var baseCreate = function(prototype) {
	    if (!_.isObject(prototype)) return {};
	    if (nativeCreate) return nativeCreate(prototype);
	    Ctor.prototype = prototype;
	    var result = new Ctor;
	    Ctor.prototype = null;
	    return result;
	  };

	  var property = function(key) {
	    return function(obj) {
	      return obj == null ? void 0 : obj[key];
	    };
	  };

	  // Helper for collection methods to determine whether a collection
	  // should be iterated as an array or as an object
	  // Related: http://people.mozilla.org/~jorendorff/es6-draft.html#sec-tolength
	  // Avoids a very nasty iOS 8 JIT bug on ARM-64. #2094
	  var MAX_ARRAY_INDEX = Math.pow(2, 53) - 1;
	  var getLength = property('length');
	  var isArrayLike = function(collection) {
	    var length = getLength(collection);
	    return typeof length == 'number' && length >= 0 && length <= MAX_ARRAY_INDEX;
	  };

	  // Collection Functions
	  // --------------------

	  // The cornerstone, an `each` implementation, aka `forEach`.
	  // Handles raw objects in addition to array-likes. Treats all
	  // sparse array-likes as if they were dense.
	  _.each = _.forEach = function(obj, iteratee, context) {
	    iteratee = optimizeCb(iteratee, context);
	    var i, length;
	    if (isArrayLike(obj)) {
	      for (i = 0, length = obj.length; i < length; i++) {
	        iteratee(obj[i], i, obj);
	      }
	    } else {
	      var keys = _.keys(obj);
	      for (i = 0, length = keys.length; i < length; i++) {
	        iteratee(obj[keys[i]], keys[i], obj);
	      }
	    }
	    return obj;
	  };

	  // Return the results of applying the iteratee to each element.
	  _.map = _.collect = function(obj, iteratee, context) {
	    iteratee = cb(iteratee, context);
	    var keys = !isArrayLike(obj) && _.keys(obj),
	        length = (keys || obj).length,
	        results = Array(length);
	    for (var index = 0; index < length; index++) {
	      var currentKey = keys ? keys[index] : index;
	      results[index] = iteratee(obj[currentKey], currentKey, obj);
	    }
	    return results;
	  };

	  // Create a reducing function iterating left or right.
	  function createReduce(dir) {
	    // Optimized iterator function as using arguments.length
	    // in the main function will deoptimize the, see #1991.
	    function iterator(obj, iteratee, memo, keys, index, length) {
	      for (; index >= 0 && index < length; index += dir) {
	        var currentKey = keys ? keys[index] : index;
	        memo = iteratee(memo, obj[currentKey], currentKey, obj);
	      }
	      return memo;
	    }

	    return function(obj, iteratee, memo, context) {
	      iteratee = optimizeCb(iteratee, context, 4);
	      var keys = !isArrayLike(obj) && _.keys(obj),
	          length = (keys || obj).length,
	          index = dir > 0 ? 0 : length - 1;
	      // Determine the initial value if none is provided.
	      if (arguments.length < 3) {
	        memo = obj[keys ? keys[index] : index];
	        index += dir;
	      }
	      return iterator(obj, iteratee, memo, keys, index, length);
	    };
	  }

	  // **Reduce** builds up a single result from a list of values, aka `inject`,
	  // or `foldl`.
	  _.reduce = _.foldl = _.inject = createReduce(1);

	  // The right-associative version of reduce, also known as `foldr`.
	  _.reduceRight = _.foldr = createReduce(-1);

	  // Return the first value which passes a truth test. Aliased as `detect`.
	  _.find = _.detect = function(obj, predicate, context) {
	    var key;
	    if (isArrayLike(obj)) {
	      key = _.findIndex(obj, predicate, context);
	    } else {
	      key = _.findKey(obj, predicate, context);
	    }
	    if (key !== void 0 && key !== -1) return obj[key];
	  };

	  // Return all the elements that pass a truth test.
	  // Aliased as `select`.
	  _.filter = _.select = function(obj, predicate, context) {
	    var results = [];
	    predicate = cb(predicate, context);
	    _.each(obj, function(value, index, list) {
	      if (predicate(value, index, list)) results.push(value);
	    });
	    return results;
	  };

	  // Return all the elements for which a truth test fails.
	  _.reject = function(obj, predicate, context) {
	    return _.filter(obj, _.negate(cb(predicate)), context);
	  };

	  // Determine whether all of the elements match a truth test.
	  // Aliased as `all`.
	  _.every = _.all = function(obj, predicate, context) {
	    predicate = cb(predicate, context);
	    var keys = !isArrayLike(obj) && _.keys(obj),
	        length = (keys || obj).length;
	    for (var index = 0; index < length; index++) {
	      var currentKey = keys ? keys[index] : index;
	      if (!predicate(obj[currentKey], currentKey, obj)) return false;
	    }
	    return true;
	  };

	  // Determine if at least one element in the object matches a truth test.
	  // Aliased as `any`.
	  _.some = _.any = function(obj, predicate, context) {
	    predicate = cb(predicate, context);
	    var keys = !isArrayLike(obj) && _.keys(obj),
	        length = (keys || obj).length;
	    for (var index = 0; index < length; index++) {
	      var currentKey = keys ? keys[index] : index;
	      if (predicate(obj[currentKey], currentKey, obj)) return true;
	    }
	    return false;
	  };

	  // Determine if the array or object contains a given item (using `===`).
	  // Aliased as `includes` and `include`.
	  _.contains = _.includes = _.include = function(obj, item, fromIndex, guard) {
	    if (!isArrayLike(obj)) obj = _.values(obj);
	    if (typeof fromIndex != 'number' || guard) fromIndex = 0;
	    return _.indexOf(obj, item, fromIndex) >= 0;
	  };

	  // Invoke a method (with arguments) on every item in a collection.
	  _.invoke = function(obj, method) {
	    var args = slice.call(arguments, 2);
	    var isFunc = _.isFunction(method);
	    return _.map(obj, function(value) {
	      var func = isFunc ? method : value[method];
	      return func == null ? func : func.apply(value, args);
	    });
	  };

	  // Convenience version of a common use case of `map`: fetching a property.
	  _.pluck = function(obj, key) {
	    return _.map(obj, _.property(key));
	  };

	  // Convenience version of a common use case of `filter`: selecting only objects
	  // containing specific `key:value` pairs.
	  _.where = function(obj, attrs) {
	    return _.filter(obj, _.matcher(attrs));
	  };

	  // Convenience version of a common use case of `find`: getting the first object
	  // containing specific `key:value` pairs.
	  _.findWhere = function(obj, attrs) {
	    return _.find(obj, _.matcher(attrs));
	  };

	  // Return the maximum element (or element-based computation).
	  _.max = function(obj, iteratee, context) {
	    var result = -Infinity, lastComputed = -Infinity,
	        value, computed;
	    if (iteratee == null && obj != null) {
	      obj = isArrayLike(obj) ? obj : _.values(obj);
	      for (var i = 0, length = obj.length; i < length; i++) {
	        value = obj[i];
	        if (value > result) {
	          result = value;
	        }
	      }
	    } else {
	      iteratee = cb(iteratee, context);
	      _.each(obj, function(value, index, list) {
	        computed = iteratee(value, index, list);
	        if (computed > lastComputed || computed === -Infinity && result === -Infinity) {
	          result = value;
	          lastComputed = computed;
	        }
	      });
	    }
	    return result;
	  };

	  // Return the minimum element (or element-based computation).
	  _.min = function(obj, iteratee, context) {
	    var result = Infinity, lastComputed = Infinity,
	        value, computed;
	    if (iteratee == null && obj != null) {
	      obj = isArrayLike(obj) ? obj : _.values(obj);
	      for (var i = 0, length = obj.length; i < length; i++) {
	        value = obj[i];
	        if (value < result) {
	          result = value;
	        }
	      }
	    } else {
	      iteratee = cb(iteratee, context);
	      _.each(obj, function(value, index, list) {
	        computed = iteratee(value, index, list);
	        if (computed < lastComputed || computed === Infinity && result === Infinity) {
	          result = value;
	          lastComputed = computed;
	        }
	      });
	    }
	    return result;
	  };

	  // Shuffle a collection, using the modern version of the
	  // [Fisher-Yates shuffle](http://en.wikipedia.org/wiki/Fisher–Yates_shuffle).
	  _.shuffle = function(obj) {
	    var set = isArrayLike(obj) ? obj : _.values(obj);
	    var length = set.length;
	    var shuffled = Array(length);
	    for (var index = 0, rand; index < length; index++) {
	      rand = _.random(0, index);
	      if (rand !== index) shuffled[index] = shuffled[rand];
	      shuffled[rand] = set[index];
	    }
	    return shuffled;
	  };

	  // Sample **n** random values from a collection.
	  // If **n** is not specified, returns a single random element.
	  // The internal `guard` argument allows it to work with `map`.
	  _.sample = function(obj, n, guard) {
	    if (n == null || guard) {
	      if (!isArrayLike(obj)) obj = _.values(obj);
	      return obj[_.random(obj.length - 1)];
	    }
	    return _.shuffle(obj).slice(0, Math.max(0, n));
	  };

	  // Sort the object's values by a criterion produced by an iteratee.
	  _.sortBy = function(obj, iteratee, context) {
	    iteratee = cb(iteratee, context);
	    return _.pluck(_.map(obj, function(value, index, list) {
	      return {
	        value: value,
	        index: index,
	        criteria: iteratee(value, index, list)
	      };
	    }).sort(function(left, right) {
	      var a = left.criteria;
	      var b = right.criteria;
	      if (a !== b) {
	        if (a > b || a === void 0) return 1;
	        if (a < b || b === void 0) return -1;
	      }
	      return left.index - right.index;
	    }), 'value');
	  };

	  // An internal function used for aggregate "group by" operations.
	  var group = function(behavior) {
	    return function(obj, iteratee, context) {
	      var result = {};
	      iteratee = cb(iteratee, context);
	      _.each(obj, function(value, index) {
	        var key = iteratee(value, index, obj);
	        behavior(result, value, key);
	      });
	      return result;
	    };
	  };

	  // Groups the object's values by a criterion. Pass either a string attribute
	  // to group by, or a function that returns the criterion.
	  _.groupBy = group(function(result, value, key) {
	    if (_.has(result, key)) result[key].push(value); else result[key] = [value];
	  });

	  // Indexes the object's values by a criterion, similar to `groupBy`, but for
	  // when you know that your index values will be unique.
	  _.indexBy = group(function(result, value, key) {
	    result[key] = value;
	  });

	  // Counts instances of an object that group by a certain criterion. Pass
	  // either a string attribute to count by, or a function that returns the
	  // criterion.
	  _.countBy = group(function(result, value, key) {
	    if (_.has(result, key)) result[key]++; else result[key] = 1;
	  });

	  // Safely create a real, live array from anything iterable.
	  _.toArray = function(obj) {
	    if (!obj) return [];
	    if (_.isArray(obj)) return slice.call(obj);
	    if (isArrayLike(obj)) return _.map(obj, _.identity);
	    return _.values(obj);
	  };

	  // Return the number of elements in an object.
	  _.size = function(obj) {
	    if (obj == null) return 0;
	    return isArrayLike(obj) ? obj.length : _.keys(obj).length;
	  };

	  // Split a collection into two arrays: one whose elements all satisfy the given
	  // predicate, and one whose elements all do not satisfy the predicate.
	  _.partition = function(obj, predicate, context) {
	    predicate = cb(predicate, context);
	    var pass = [], fail = [];
	    _.each(obj, function(value, key, obj) {
	      (predicate(value, key, obj) ? pass : fail).push(value);
	    });
	    return [pass, fail];
	  };

	  // Array Functions
	  // ---------------

	  // Get the first element of an array. Passing **n** will return the first N
	  // values in the array. Aliased as `head` and `take`. The **guard** check
	  // allows it to work with `_.map`.
	  _.first = _.head = _.take = function(array, n, guard) {
	    if (array == null) return void 0;
	    if (n == null || guard) return array[0];
	    return _.initial(array, array.length - n);
	  };

	  // Returns everything but the last entry of the array. Especially useful on
	  // the arguments object. Passing **n** will return all the values in
	  // the array, excluding the last N.
	  _.initial = function(array, n, guard) {
	    return slice.call(array, 0, Math.max(0, array.length - (n == null || guard ? 1 : n)));
	  };

	  // Get the last element of an array. Passing **n** will return the last N
	  // values in the array.
	  _.last = function(array, n, guard) {
	    if (array == null) return void 0;
	    if (n == null || guard) return array[array.length - 1];
	    return _.rest(array, Math.max(0, array.length - n));
	  };

	  // Returns everything but the first entry of the array. Aliased as `tail` and `drop`.
	  // Especially useful on the arguments object. Passing an **n** will return
	  // the rest N values in the array.
	  _.rest = _.tail = _.drop = function(array, n, guard) {
	    return slice.call(array, n == null || guard ? 1 : n);
	  };

	  // Trim out all falsy values from an array.
	  _.compact = function(array) {
	    return _.filter(array, _.identity);
	  };

	  // Internal implementation of a recursive `flatten` function.
	  var flatten = function(input, shallow, strict, startIndex) {
	    var output = [], idx = 0;
	    for (var i = startIndex || 0, length = getLength(input); i < length; i++) {
	      var value = input[i];
	      if (isArrayLike(value) && (_.isArray(value) || _.isArguments(value))) {
	        //flatten current level of array or arguments object
	        if (!shallow) value = flatten(value, shallow, strict);
	        var j = 0, len = value.length;
	        output.length += len;
	        while (j < len) {
	          output[idx++] = value[j++];
	        }
	      } else if (!strict) {
	        output[idx++] = value;
	      }
	    }
	    return output;
	  };

	  // Flatten out an array, either recursively (by default), or just one level.
	  _.flatten = function(array, shallow) {
	    return flatten(array, shallow, false);
	  };

	  // Return a version of the array that does not contain the specified value(s).
	  _.without = function(array) {
	    return _.difference(array, slice.call(arguments, 1));
	  };

	  // Produce a duplicate-free version of the array. If the array has already
	  // been sorted, you have the option of using a faster algorithm.
	  // Aliased as `unique`.
	  _.uniq = _.unique = function(array, isSorted, iteratee, context) {
	    if (!_.isBoolean(isSorted)) {
	      context = iteratee;
	      iteratee = isSorted;
	      isSorted = false;
	    }
	    if (iteratee != null) iteratee = cb(iteratee, context);
	    var result = [];
	    var seen = [];
	    for (var i = 0, length = getLength(array); i < length; i++) {
	      var value = array[i],
	          computed = iteratee ? iteratee(value, i, array) : value;
	      if (isSorted) {
	        if (!i || seen !== computed) result.push(value);
	        seen = computed;
	      } else if (iteratee) {
	        if (!_.contains(seen, computed)) {
	          seen.push(computed);
	          result.push(value);
	        }
	      } else if (!_.contains(result, value)) {
	        result.push(value);
	      }
	    }
	    return result;
	  };

	  // Produce an array that contains the union: each distinct element from all of
	  // the passed-in arrays.
	  _.union = function() {
	    return _.uniq(flatten(arguments, true, true));
	  };

	  // Produce an array that contains every item shared between all the
	  // passed-in arrays.
	  _.intersection = function(array) {
	    var result = [];
	    var argsLength = arguments.length;
	    for (var i = 0, length = getLength(array); i < length; i++) {
	      var item = array[i];
	      if (_.contains(result, item)) continue;
	      for (var j = 1; j < argsLength; j++) {
	        if (!_.contains(arguments[j], item)) break;
	      }
	      if (j === argsLength) result.push(item);
	    }
	    return result;
	  };

	  // Take the difference between one array and a number of other arrays.
	  // Only the elements present in just the first array will remain.
	  _.difference = function(array) {
	    var rest = flatten(arguments, true, true, 1);
	    return _.filter(array, function(value){
	      return !_.contains(rest, value);
	    });
	  };

	  // Zip together multiple lists into a single array -- elements that share
	  // an index go together.
	  _.zip = function() {
	    return _.unzip(arguments);
	  };

	  // Complement of _.zip. Unzip accepts an array of arrays and groups
	  // each array's elements on shared indices
	  _.unzip = function(array) {
	    var length = array && _.max(array, getLength).length || 0;
	    var result = Array(length);

	    for (var index = 0; index < length; index++) {
	      result[index] = _.pluck(array, index);
	    }
	    return result;
	  };

	  // Converts lists into objects. Pass either a single array of `[key, value]`
	  // pairs, or two parallel arrays of the same length -- one of keys, and one of
	  // the corresponding values.
	  _.object = function(list, values) {
	    var result = {};
	    for (var i = 0, length = getLength(list); i < length; i++) {
	      if (values) {
	        result[list[i]] = values[i];
	      } else {
	        result[list[i][0]] = list[i][1];
	      }
	    }
	    return result;
	  };

	  // Generator function to create the findIndex and findLastIndex functions
	  function createPredicateIndexFinder(dir) {
	    return function(array, predicate, context) {
	      predicate = cb(predicate, context);
	      var length = getLength(array);
	      var index = dir > 0 ? 0 : length - 1;
	      for (; index >= 0 && index < length; index += dir) {
	        if (predicate(array[index], index, array)) return index;
	      }
	      return -1;
	    };
	  }

	  // Returns the first index on an array-like that passes a predicate test
	  _.findIndex = createPredicateIndexFinder(1);
	  _.findLastIndex = createPredicateIndexFinder(-1);

	  // Use a comparator function to figure out the smallest index at which
	  // an object should be inserted so as to maintain order. Uses binary search.
	  _.sortedIndex = function(array, obj, iteratee, context) {
	    iteratee = cb(iteratee, context, 1);
	    var value = iteratee(obj);
	    var low = 0, high = getLength(array);
	    while (low < high) {
	      var mid = Math.floor((low + high) / 2);
	      if (iteratee(array[mid]) < value) low = mid + 1; else high = mid;
	    }
	    return low;
	  };

	  // Generator function to create the indexOf and lastIndexOf functions
	  function createIndexFinder(dir, predicateFind, sortedIndex) {
	    return function(array, item, idx) {
	      var i = 0, length = getLength(array);
	      if (typeof idx == 'number') {
	        if (dir > 0) {
	            i = idx >= 0 ? idx : Math.max(idx + length, i);
	        } else {
	            length = idx >= 0 ? Math.min(idx + 1, length) : idx + length + 1;
	        }
	      } else if (sortedIndex && idx && length) {
	        idx = sortedIndex(array, item);
	        return array[idx] === item ? idx : -1;
	      }
	      if (item !== item) {
	        idx = predicateFind(slice.call(array, i, length), _.isNaN);
	        return idx >= 0 ? idx + i : -1;
	      }
	      for (idx = dir > 0 ? i : length - 1; idx >= 0 && idx < length; idx += dir) {
	        if (array[idx] === item) return idx;
	      }
	      return -1;
	    };
	  }

	  // Return the position of the first occurrence of an item in an array,
	  // or -1 if the item is not included in the array.
	  // If the array is large and already in sort order, pass `true`
	  // for **isSorted** to use binary search.
	  _.indexOf = createIndexFinder(1, _.findIndex, _.sortedIndex);
	  _.lastIndexOf = createIndexFinder(-1, _.findLastIndex);

	  // Generate an integer Array containing an arithmetic progression. A port of
	  // the native Python `range()` function. See
	  // [the Python documentation](http://docs.python.org/library/functions.html#range).
	  _.range = function(start, stop, step) {
	    if (stop == null) {
	      stop = start || 0;
	      start = 0;
	    }
	    step = step || 1;

	    var length = Math.max(Math.ceil((stop - start) / step), 0);
	    var range = Array(length);

	    for (var idx = 0; idx < length; idx++, start += step) {
	      range[idx] = start;
	    }

	    return range;
	  };

	  // Function (ahem) Functions
	  // ------------------

	  // Determines whether to execute a function as a constructor
	  // or a normal function with the provided arguments
	  var executeBound = function(sourceFunc, boundFunc, context, callingContext, args) {
	    if (!(callingContext instanceof boundFunc)) return sourceFunc.apply(context, args);
	    var self = baseCreate(sourceFunc.prototype);
	    var result = sourceFunc.apply(self, args);
	    if (_.isObject(result)) return result;
	    return self;
	  };

	  // Create a function bound to a given object (assigning `this`, and arguments,
	  // optionally). Delegates to **ECMAScript 5**'s native `Function.bind` if
	  // available.
	  _.bind = function(func, context) {
	    if (nativeBind && func.bind === nativeBind) return nativeBind.apply(func, slice.call(arguments, 1));
	    if (!_.isFunction(func)) throw new TypeError('Bind must be called on a function');
	    var args = slice.call(arguments, 2);
	    var bound = function() {
	      return executeBound(func, bound, context, this, args.concat(slice.call(arguments)));
	    };
	    return bound;
	  };

	  // Partially apply a function by creating a version that has had some of its
	  // arguments pre-filled, without changing its dynamic `this` context. _ acts
	  // as a placeholder, allowing any combination of arguments to be pre-filled.
	  _.partial = function(func) {
	    var boundArgs = slice.call(arguments, 1);
	    var bound = function() {
	      var position = 0, length = boundArgs.length;
	      var args = Array(length);
	      for (var i = 0; i < length; i++) {
	        args[i] = boundArgs[i] === _ ? arguments[position++] : boundArgs[i];
	      }
	      while (position < arguments.length) args.push(arguments[position++]);
	      return executeBound(func, bound, this, this, args);
	    };
	    return bound;
	  };

	  // Bind a number of an object's methods to that object. Remaining arguments
	  // are the method names to be bound. Useful for ensuring that all callbacks
	  // defined on an object belong to it.
	  _.bindAll = function(obj) {
	    var i, length = arguments.length, key;
	    if (length <= 1) throw new Error('bindAll must be passed function names');
	    for (i = 1; i < length; i++) {
	      key = arguments[i];
	      obj[key] = _.bind(obj[key], obj);
	    }
	    return obj;
	  };

	  // Memoize an expensive function by storing its results.
	  _.memoize = function(func, hasher) {
	    var memoize = function(key) {
	      var cache = memoize.cache;
	      var address = '' + (hasher ? hasher.apply(this, arguments) : key);
	      if (!_.has(cache, address)) cache[address] = func.apply(this, arguments);
	      return cache[address];
	    };
	    memoize.cache = {};
	    return memoize;
	  };

	  // Delays a function for the given number of milliseconds, and then calls
	  // it with the arguments supplied.
	  _.delay = function(func, wait) {
	    var args = slice.call(arguments, 2);
	    return setTimeout(function(){
	      return func.apply(null, args);
	    }, wait);
	  };

	  // Defers a function, scheduling it to run after the current call stack has
	  // cleared.
	  _.defer = _.partial(_.delay, _, 1);

	  // Returns a function, that, when invoked, will only be triggered at most once
	  // during a given window of time. Normally, the throttled function will run
	  // as much as it can, without ever going more than once per `wait` duration;
	  // but if you'd like to disable the execution on the leading edge, pass
	  // `{leading: false}`. To disable execution on the trailing edge, ditto.
	  _.throttle = function(func, wait, options) {
	    var context, args, result;
	    var timeout = null;
	    var previous = 0;
	    if (!options) options = {};
	    var later = function() {
	      previous = options.leading === false ? 0 : _.now();
	      timeout = null;
	      result = func.apply(context, args);
	      if (!timeout) context = args = null;
	    };
	    return function() {
	      var now = _.now();
	      if (!previous && options.leading === false) previous = now;
	      var remaining = wait - (now - previous);
	      context = this;
	      args = arguments;
	      if (remaining <= 0 || remaining > wait) {
	        if (timeout) {
	          clearTimeout(timeout);
	          timeout = null;
	        }
	        previous = now;
	        result = func.apply(context, args);
	        if (!timeout) context = args = null;
	      } else if (!timeout && options.trailing !== false) {
	        timeout = setTimeout(later, remaining);
	      }
	      return result;
	    };
	  };

	  // Returns a function, that, as long as it continues to be invoked, will not
	  // be triggered. The function will be called after it stops being called for
	  // N milliseconds. If `immediate` is passed, trigger the function on the
	  // leading edge, instead of the trailing.
	  _.debounce = function(func, wait, immediate) {
	    var timeout, args, context, timestamp, result;

	    var later = function() {
	      var last = _.now() - timestamp;

	      if (last < wait && last >= 0) {
	        timeout = setTimeout(later, wait - last);
	      } else {
	        timeout = null;
	        if (!immediate) {
	          result = func.apply(context, args);
	          if (!timeout) context = args = null;
	        }
	      }
	    };

	    return function() {
	      context = this;
	      args = arguments;
	      timestamp = _.now();
	      var callNow = immediate && !timeout;
	      if (!timeout) timeout = setTimeout(later, wait);
	      if (callNow) {
	        result = func.apply(context, args);
	        context = args = null;
	      }

	      return result;
	    };
	  };

	  // Returns the first function passed as an argument to the second,
	  // allowing you to adjust arguments, run code before and after, and
	  // conditionally execute the original function.
	  _.wrap = function(func, wrapper) {
	    return _.partial(wrapper, func);
	  };

	  // Returns a negated version of the passed-in predicate.
	  _.negate = function(predicate) {
	    return function() {
	      return !predicate.apply(this, arguments);
	    };
	  };

	  // Returns a function that is the composition of a list of functions, each
	  // consuming the return value of the function that follows.
	  _.compose = function() {
	    var args = arguments;
	    var start = args.length - 1;
	    return function() {
	      var i = start;
	      var result = args[start].apply(this, arguments);
	      while (i--) result = args[i].call(this, result);
	      return result;
	    };
	  };

	  // Returns a function that will only be executed on and after the Nth call.
	  _.after = function(times, func) {
	    return function() {
	      if (--times < 1) {
	        return func.apply(this, arguments);
	      }
	    };
	  };

	  // Returns a function that will only be executed up to (but not including) the Nth call.
	  _.before = function(times, func) {
	    var memo;
	    return function() {
	      if (--times > 0) {
	        memo = func.apply(this, arguments);
	      }
	      if (times <= 1) func = null;
	      return memo;
	    };
	  };

	  // Returns a function that will be executed at most one time, no matter how
	  // often you call it. Useful for lazy initialization.
	  _.once = _.partial(_.before, 2);

	  // Object Functions
	  // ----------------

	  // Keys in IE < 9 that won't be iterated by `for key in ...` and thus missed.
	  var hasEnumBug = !{toString: null}.propertyIsEnumerable('toString');
	  var nonEnumerableProps = ['valueOf', 'isPrototypeOf', 'toString',
	                      'propertyIsEnumerable', 'hasOwnProperty', 'toLocaleString'];

	  function collectNonEnumProps(obj, keys) {
	    var nonEnumIdx = nonEnumerableProps.length;
	    var constructor = obj.constructor;
	    var proto = (_.isFunction(constructor) && constructor.prototype) || ObjProto;

	    // Constructor is a special case.
	    var prop = 'constructor';
	    if (_.has(obj, prop) && !_.contains(keys, prop)) keys.push(prop);

	    while (nonEnumIdx--) {
	      prop = nonEnumerableProps[nonEnumIdx];
	      if (prop in obj && obj[prop] !== proto[prop] && !_.contains(keys, prop)) {
	        keys.push(prop);
	      }
	    }
	  }

	  // Retrieve the names of an object's own properties.
	  // Delegates to **ECMAScript 5**'s native `Object.keys`
	  _.keys = function(obj) {
	    if (!_.isObject(obj)) return [];
	    if (nativeKeys) return nativeKeys(obj);
	    var keys = [];
	    for (var key in obj) if (_.has(obj, key)) keys.push(key);
	    // Ahem, IE < 9.
	    if (hasEnumBug) collectNonEnumProps(obj, keys);
	    return keys;
	  };

	  // Retrieve all the property names of an object.
	  _.allKeys = function(obj) {
	    if (!_.isObject(obj)) return [];
	    var keys = [];
	    for (var key in obj) keys.push(key);
	    // Ahem, IE < 9.
	    if (hasEnumBug) collectNonEnumProps(obj, keys);
	    return keys;
	  };

	  // Retrieve the values of an object's properties.
	  _.values = function(obj) {
	    var keys = _.keys(obj);
	    var length = keys.length;
	    var values = Array(length);
	    for (var i = 0; i < length; i++) {
	      values[i] = obj[keys[i]];
	    }
	    return values;
	  };

	  // Returns the results of applying the iteratee to each element of the object
	  // In contrast to _.map it returns an object
	  _.mapObject = function(obj, iteratee, context) {
	    iteratee = cb(iteratee, context);
	    var keys =  _.keys(obj),
	          length = keys.length,
	          results = {},
	          currentKey;
	      for (var index = 0; index < length; index++) {
	        currentKey = keys[index];
	        results[currentKey] = iteratee(obj[currentKey], currentKey, obj);
	      }
	      return results;
	  };

	  // Convert an object into a list of `[key, value]` pairs.
	  _.pairs = function(obj) {
	    var keys = _.keys(obj);
	    var length = keys.length;
	    var pairs = Array(length);
	    for (var i = 0; i < length; i++) {
	      pairs[i] = [keys[i], obj[keys[i]]];
	    }
	    return pairs;
	  };

	  // Invert the keys and values of an object. The values must be serializable.
	  _.invert = function(obj) {
	    var result = {};
	    var keys = _.keys(obj);
	    for (var i = 0, length = keys.length; i < length; i++) {
	      result[obj[keys[i]]] = keys[i];
	    }
	    return result;
	  };

	  // Return a sorted list of the function names available on the object.
	  // Aliased as `methods`
	  _.functions = _.methods = function(obj) {
	    var names = [];
	    for (var key in obj) {
	      if (_.isFunction(obj[key])) names.push(key);
	    }
	    return names.sort();
	  };

	  // Extend a given object with all the properties in passed-in object(s).
	  _.extend = createAssigner(_.allKeys);

	  // Assigns a given object with all the own properties in the passed-in object(s)
	  // (https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Object/assign)
	  _.extendOwn = _.assign = createAssigner(_.keys);

	  // Returns the first key on an object that passes a predicate test
	  _.findKey = function(obj, predicate, context) {
	    predicate = cb(predicate, context);
	    var keys = _.keys(obj), key;
	    for (var i = 0, length = keys.length; i < length; i++) {
	      key = keys[i];
	      if (predicate(obj[key], key, obj)) return key;
	    }
	  };

	  // Return a copy of the object only containing the whitelisted properties.
	  _.pick = function(object, oiteratee, context) {
	    var result = {}, obj = object, iteratee, keys;
	    if (obj == null) return result;
	    if (_.isFunction(oiteratee)) {
	      keys = _.allKeys(obj);
	      iteratee = optimizeCb(oiteratee, context);
	    } else {
	      keys = flatten(arguments, false, false, 1);
	      iteratee = function(value, key, obj) { return key in obj; };
	      obj = Object(obj);
	    }
	    for (var i = 0, length = keys.length; i < length; i++) {
	      var key = keys[i];
	      var value = obj[key];
	      if (iteratee(value, key, obj)) result[key] = value;
	    }
	    return result;
	  };

	   // Return a copy of the object without the blacklisted properties.
	  _.omit = function(obj, iteratee, context) {
	    if (_.isFunction(iteratee)) {
	      iteratee = _.negate(iteratee);
	    } else {
	      var keys = _.map(flatten(arguments, false, false, 1), String);
	      iteratee = function(value, key) {
	        return !_.contains(keys, key);
	      };
	    }
	    return _.pick(obj, iteratee, context);
	  };

	  // Fill in a given object with default properties.
	  _.defaults = createAssigner(_.allKeys, true);

	  // Creates an object that inherits from the given prototype object.
	  // If additional properties are provided then they will be added to the
	  // created object.
	  _.create = function(prototype, props) {
	    var result = baseCreate(prototype);
	    if (props) _.extendOwn(result, props);
	    return result;
	  };

	  // Create a (shallow-cloned) duplicate of an object.
	  _.clone = function(obj) {
	    if (!_.isObject(obj)) return obj;
	    return _.isArray(obj) ? obj.slice() : _.extend({}, obj);
	  };

	  // Invokes interceptor with the obj, and then returns obj.
	  // The primary purpose of this method is to "tap into" a method chain, in
	  // order to perform operations on intermediate results within the chain.
	  _.tap = function(obj, interceptor) {
	    interceptor(obj);
	    return obj;
	  };

	  // Returns whether an object has a given set of `key:value` pairs.
	  _.isMatch = function(object, attrs) {
	    var keys = _.keys(attrs), length = keys.length;
	    if (object == null) return !length;
	    var obj = Object(object);
	    for (var i = 0; i < length; i++) {
	      var key = keys[i];
	      if (attrs[key] !== obj[key] || !(key in obj)) return false;
	    }
	    return true;
	  };


	  // Internal recursive comparison function for `isEqual`.
	  var eq = function(a, b, aStack, bStack) {
	    // Identical objects are equal. `0 === -0`, but they aren't identical.
	    // See the [Harmony `egal` proposal](http://wiki.ecmascript.org/doku.php?id=harmony:egal).
	    if (a === b) return a !== 0 || 1 / a === 1 / b;
	    // A strict comparison is necessary because `null == undefined`.
	    if (a == null || b == null) return a === b;
	    // Unwrap any wrapped objects.
	    if (a instanceof _) a = a._wrapped;
	    if (b instanceof _) b = b._wrapped;
	    // Compare `[[Class]]` names.
	    var className = toString.call(a);
	    if (className !== toString.call(b)) return false;
	    switch (className) {
	      // Strings, numbers, regular expressions, dates, and booleans are compared by value.
	      case '[object RegExp]':
	      // RegExps are coerced to strings for comparison (Note: '' + /a/i === '/a/i')
	      case '[object String]':
	        // Primitives and their corresponding object wrappers are equivalent; thus, `"5"` is
	        // equivalent to `new String("5")`.
	        return '' + a === '' + b;
	      case '[object Number]':
	        // `NaN`s are equivalent, but non-reflexive.
	        // Object(NaN) is equivalent to NaN
	        if (+a !== +a) return +b !== +b;
	        // An `egal` comparison is performed for other numeric values.
	        return +a === 0 ? 1 / +a === 1 / b : +a === +b;
	      case '[object Date]':
	      case '[object Boolean]':
	        // Coerce dates and booleans to numeric primitive values. Dates are compared by their
	        // millisecond representations. Note that invalid dates with millisecond representations
	        // of `NaN` are not equivalent.
	        return +a === +b;
	    }

	    var areArrays = className === '[object Array]';
	    if (!areArrays) {
	      if (typeof a != 'object' || typeof b != 'object') return false;

	      // Objects with different constructors are not equivalent, but `Object`s or `Array`s
	      // from different frames are.
	      var aCtor = a.constructor, bCtor = b.constructor;
	      if (aCtor !== bCtor && !(_.isFunction(aCtor) && aCtor instanceof aCtor &&
	                               _.isFunction(bCtor) && bCtor instanceof bCtor)
	                          && ('constructor' in a && 'constructor' in b)) {
	        return false;
	      }
	    }
	    // Assume equality for cyclic structures. The algorithm for detecting cyclic
	    // structures is adapted from ES 5.1 section 15.12.3, abstract operation `JO`.

	    // Initializing stack of traversed objects.
	    // It's done here since we only need them for objects and arrays comparison.
	    aStack = aStack || [];
	    bStack = bStack || [];
	    var length = aStack.length;
	    while (length--) {
	      // Linear search. Performance is inversely proportional to the number of
	      // unique nested structures.
	      if (aStack[length] === a) return bStack[length] === b;
	    }

	    // Add the first object to the stack of traversed objects.
	    aStack.push(a);
	    bStack.push(b);

	    // Recursively compare objects and arrays.
	    if (areArrays) {
	      // Compare array lengths to determine if a deep comparison is necessary.
	      length = a.length;
	      if (length !== b.length) return false;
	      // Deep compare the contents, ignoring non-numeric properties.
	      while (length--) {
	        if (!eq(a[length], b[length], aStack, bStack)) return false;
	      }
	    } else {
	      // Deep compare objects.
	      var keys = _.keys(a), key;
	      length = keys.length;
	      // Ensure that both objects contain the same number of properties before comparing deep equality.
	      if (_.keys(b).length !== length) return false;
	      while (length--) {
	        // Deep compare each member
	        key = keys[length];
	        if (!(_.has(b, key) && eq(a[key], b[key], aStack, bStack))) return false;
	      }
	    }
	    // Remove the first object from the stack of traversed objects.
	    aStack.pop();
	    bStack.pop();
	    return true;
	  };

	  // Perform a deep comparison to check if two objects are equal.
	  _.isEqual = function(a, b) {
	    return eq(a, b);
	  };

	  // Is a given array, string, or object empty?
	  // An "empty" object has no enumerable own-properties.
	  _.isEmpty = function(obj) {
	    if (obj == null) return true;
	    if (isArrayLike(obj) && (_.isArray(obj) || _.isString(obj) || _.isArguments(obj))) return obj.length === 0;
	    return _.keys(obj).length === 0;
	  };

	  // Is a given value a DOM element?
	  _.isElement = function(obj) {
	    return !!(obj && obj.nodeType === 1);
	  };

	  // Is a given value an array?
	  // Delegates to ECMA5's native Array.isArray
	  _.isArray = nativeIsArray || function(obj) {
	    return toString.call(obj) === '[object Array]';
	  };

	  // Is a given variable an object?
	  _.isObject = function(obj) {
	    var type = typeof obj;
	    return type === 'function' || type === 'object' && !!obj;
	  };

	  // Add some isType methods: isArguments, isFunction, isString, isNumber, isDate, isRegExp, isError.
	  _.each(['Arguments', 'Function', 'String', 'Number', 'Date', 'RegExp', 'Error'], function(name) {
	    _['is' + name] = function(obj) {
	      return toString.call(obj) === '[object ' + name + ']';
	    };
	  });

	  // Define a fallback version of the method in browsers (ahem, IE < 9), where
	  // there isn't any inspectable "Arguments" type.
	  if (!_.isArguments(arguments)) {
	    _.isArguments = function(obj) {
	      return _.has(obj, 'callee');
	    };
	  }

	  // Optimize `isFunction` if appropriate. Work around some typeof bugs in old v8,
	  // IE 11 (#1621), and in Safari 8 (#1929).
	  if (typeof /./ != 'function' && typeof Int8Array != 'object') {
	    _.isFunction = function(obj) {
	      return typeof obj == 'function' || false;
	    };
	  }

	  // Is a given object a finite number?
	  _.isFinite = function(obj) {
	    return isFinite(obj) && !isNaN(parseFloat(obj));
	  };

	  // Is the given value `NaN`? (NaN is the only number which does not equal itself).
	  _.isNaN = function(obj) {
	    return _.isNumber(obj) && obj !== +obj;
	  };

	  // Is a given value a boolean?
	  _.isBoolean = function(obj) {
	    return obj === true || obj === false || toString.call(obj) === '[object Boolean]';
	  };

	  // Is a given value equal to null?
	  _.isNull = function(obj) {
	    return obj === null;
	  };

	  // Is a given variable undefined?
	  _.isUndefined = function(obj) {
	    return obj === void 0;
	  };

	  // Shortcut function for checking if an object has a given property directly
	  // on itself (in other words, not on a prototype).
	  _.has = function(obj, key) {
	    return obj != null && hasOwnProperty.call(obj, key);
	  };

	  // Utility Functions
	  // -----------------

	  // Run Underscore.js in *noConflict* mode, returning the `_` variable to its
	  // previous owner. Returns a reference to the Underscore object.
	  _.noConflict = function() {
	    root._ = previousUnderscore;
	    return this;
	  };

	  // Keep the identity function around for default iteratees.
	  _.identity = function(value) {
	    return value;
	  };

	  // Predicate-generating functions. Often useful outside of Underscore.
	  _.constant = function(value) {
	    return function() {
	      return value;
	    };
	  };

	  _.noop = function(){};

	  _.property = property;

	  // Generates a function for a given object that returns a given property.
	  _.propertyOf = function(obj) {
	    return obj == null ? function(){} : function(key) {
	      return obj[key];
	    };
	  };

	  // Returns a predicate for checking whether an object has a given set of
	  // `key:value` pairs.
	  _.matcher = _.matches = function(attrs) {
	    attrs = _.extendOwn({}, attrs);
	    return function(obj) {
	      return _.isMatch(obj, attrs);
	    };
	  };

	  // Run a function **n** times.
	  _.times = function(n, iteratee, context) {
	    var accum = Array(Math.max(0, n));
	    iteratee = optimizeCb(iteratee, context, 1);
	    for (var i = 0; i < n; i++) accum[i] = iteratee(i);
	    return accum;
	  };

	  // Return a random integer between min and max (inclusive).
	  _.random = function(min, max) {
	    if (max == null) {
	      max = min;
	      min = 0;
	    }
	    return min + Math.floor(Math.random() * (max - min + 1));
	  };

	  // A (possibly faster) way to get the current timestamp as an integer.
	  _.now = Date.now || function() {
	    return new Date().getTime();
	  };

	   // List of HTML entities for escaping.
	  var escapeMap = {
	    '&': '&amp;',
	    '<': '&lt;',
	    '>': '&gt;',
	    '"': '&quot;',
	    "'": '&#x27;',
	    '`': '&#x60;'
	  };
	  var unescapeMap = _.invert(escapeMap);

	  // Functions for escaping and unescaping strings to/from HTML interpolation.
	  var createEscaper = function(map) {
	    var escaper = function(match) {
	      return map[match];
	    };
	    // Regexes for identifying a key that needs to be escaped
	    var source = '(?:' + _.keys(map).join('|') + ')';
	    var testRegexp = RegExp(source);
	    var replaceRegexp = RegExp(source, 'g');
	    return function(string) {
	      string = string == null ? '' : '' + string;
	      return testRegexp.test(string) ? string.replace(replaceRegexp, escaper) : string;
	    };
	  };
	  _.escape = createEscaper(escapeMap);
	  _.unescape = createEscaper(unescapeMap);

	  // If the value of the named `property` is a function then invoke it with the
	  // `object` as context; otherwise, return it.
	  _.result = function(object, property, fallback) {
	    var value = object == null ? void 0 : object[property];
	    if (value === void 0) {
	      value = fallback;
	    }
	    return _.isFunction(value) ? value.call(object) : value;
	  };

	  // Generate a unique integer id (unique within the entire client session).
	  // Useful for temporary DOM ids.
	  var idCounter = 0;
	  _.uniqueId = function(prefix) {
	    var id = ++idCounter + '';
	    return prefix ? prefix + id : id;
	  };

	  // By default, Underscore uses ERB-style template delimiters, change the
	  // following template settings to use alternative delimiters.
	  _.templateSettings = {
	    evaluate    : /<%([\s\S]+?)%>/g,
	    interpolate : /<%=([\s\S]+?)%>/g,
	    escape      : /<%-([\s\S]+?)%>/g
	  };

	  // When customizing `templateSettings`, if you don't want to define an
	  // interpolation, evaluation or escaping regex, we need one that is
	  // guaranteed not to match.
	  var noMatch = /(.)^/;

	  // Certain characters need to be escaped so that they can be put into a
	  // string literal.
	  var escapes = {
	    "'":      "'",
	    '\\':     '\\',
	    '\r':     'r',
	    '\n':     'n',
	    '\u2028': 'u2028',
	    '\u2029': 'u2029'
	  };

	  var escaper = /\\|'|\r|\n|\u2028|\u2029/g;

	  var escapeChar = function(match) {
	    return '\\' + escapes[match];
	  };

	  // JavaScript micro-templating, similar to John Resig's implementation.
	  // Underscore templating handles arbitrary delimiters, preserves whitespace,
	  // and correctly escapes quotes within interpolated code.
	  // NB: `oldSettings` only exists for backwards compatibility.
	  _.template = function(text, settings, oldSettings) {
	    if (!settings && oldSettings) settings = oldSettings;
	    settings = _.defaults({}, settings, _.templateSettings);

	    // Combine delimiters into one regular expression via alternation.
	    var matcher = RegExp([
	      (settings.escape || noMatch).source,
	      (settings.interpolate || noMatch).source,
	      (settings.evaluate || noMatch).source
	    ].join('|') + '|$', 'g');

	    // Compile the template source, escaping string literals appropriately.
	    var index = 0;
	    var source = "__p+='";
	    text.replace(matcher, function(match, escape, interpolate, evaluate, offset) {
	      source += text.slice(index, offset).replace(escaper, escapeChar);
	      index = offset + match.length;

	      if (escape) {
	        source += "'+\n((__t=(" + escape + "))==null?'':_.escape(__t))+\n'";
	      } else if (interpolate) {
	        source += "'+\n((__t=(" + interpolate + "))==null?'':__t)+\n'";
	      } else if (evaluate) {
	        source += "';\n" + evaluate + "\n__p+='";
	      }

	      // Adobe VMs need the match returned to produce the correct offest.
	      return match;
	    });
	    source += "';\n";

	    // If a variable is not specified, place data values in local scope.
	    if (!settings.variable) source = 'with(obj||{}){\n' + source + '}\n';

	    source = "var __t,__p='',__j=Array.prototype.join," +
	      "print=function(){__p+=__j.call(arguments,'');};\n" +
	      source + 'return __p;\n';

	    try {
	      var render = new Function(settings.variable || 'obj', '_', source);
	    } catch (e) {
	      e.source = source;
	      throw e;
	    }

	    var template = function(data) {
	      return render.call(this, data, _);
	    };

	    // Provide the compiled source as a convenience for precompilation.
	    var argument = settings.variable || 'obj';
	    template.source = 'function(' + argument + '){\n' + source + '}';

	    return template;
	  };

	  // Add a "chain" function. Start chaining a wrapped Underscore object.
	  _.chain = function(obj) {
	    var instance = _(obj);
	    instance._chain = true;
	    return instance;
	  };

	  // OOP
	  // ---------------
	  // If Underscore is called as a function, it returns a wrapped object that
	  // can be used OO-style. This wrapper holds altered versions of all the
	  // underscore functions. Wrapped objects may be chained.

	  // Helper function to continue chaining intermediate results.
	  var result = function(instance, obj) {
	    return instance._chain ? _(obj).chain() : obj;
	  };

	  // Add your own custom functions to the Underscore object.
	  _.mixin = function(obj) {
	    _.each(_.functions(obj), function(name) {
	      var func = _[name] = obj[name];
	      _.prototype[name] = function() {
	        var args = [this._wrapped];
	        push.apply(args, arguments);
	        return result(this, func.apply(_, args));
	      };
	    });
	  };

	  // Add all of the Underscore functions to the wrapper object.
	  _.mixin(_);

	  // Add all mutator Array functions to the wrapper.
	  _.each(['pop', 'push', 'reverse', 'shift', 'sort', 'splice', 'unshift'], function(name) {
	    var method = ArrayProto[name];
	    _.prototype[name] = function() {
	      var obj = this._wrapped;
	      method.apply(obj, arguments);
	      if ((name === 'shift' || name === 'splice') && obj.length === 0) delete obj[0];
	      return result(this, obj);
	    };
	  });

	  // Add all accessor Array functions to the wrapper.
	  _.each(['concat', 'join', 'slice'], function(name) {
	    var method = ArrayProto[name];
	    _.prototype[name] = function() {
	      return result(this, method.apply(this._wrapped, arguments));
	    };
	  });

	  // Extracts the result from a wrapped and chained object.
	  _.prototype.value = function() {
	    return this._wrapped;
	  };

	  // Provide unwrapping proxy for some methods used in engine operations
	  // such as arithmetic and JSON stringification.
	  _.prototype.valueOf = _.prototype.toJSON = _.prototype.value;

	  _.prototype.toString = function() {
	    return '' + this._wrapped;
	  };

	  // AMD registration happens at the end for compatibility with AMD loaders
	  // that may not enforce next-turn semantics on modules. Even though general
	  // practice for AMD registration is to be anonymous, underscore registers
	  // as a named module because, like jQuery, it is a base library that is
	  // popular enough to be bundled in a third party lib, but not be part of
	  // an AMD load request. Those cases could generate an error when an
	  // anonymous define() is called outside of a loader request.
	  if (true) {
	    !(__WEBPACK_AMD_DEFINE_ARRAY__ = [], __WEBPACK_AMD_DEFINE_RESULT__ = function() {
	      return _;
	    }.apply(exports, __WEBPACK_AMD_DEFINE_ARRAY__), __WEBPACK_AMD_DEFINE_RESULT__ !== undefined && (module.exports = __WEBPACK_AMD_DEFINE_RESULT__));
	  }
	}.call(this));


/***/ },
/* 45 */
/***/ function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(setImmediate) {(function (root) {

	  // Store setTimeout reference so promise-polyfill will be unaffected by
	  // other code modifying setTimeout (like sinon.useFakeTimers())
	  var setTimeoutFunc = setTimeout;

	  function noop() {}
	  
	  // Polyfill for Function.prototype.bind
	  function bind(fn, thisArg) {
	    return function () {
	      fn.apply(thisArg, arguments);
	    };
	  }

	  function Promise(fn) {
	    if (typeof this !== 'object') throw new TypeError('Promises must be constructed via new');
	    if (typeof fn !== 'function') throw new TypeError('not a function');
	    this._state = 0;
	    this._handled = false;
	    this._value = undefined;
	    this._deferreds = [];

	    doResolve(fn, this);
	  }

	  function handle(self, deferred) {
	    while (self._state === 3) {
	      self = self._value;
	    }
	    if (self._state === 0) {
	      self._deferreds.push(deferred);
	      return;
	    }
	    self._handled = true;
	    Promise._immediateFn(function () {
	      var cb = self._state === 1 ? deferred.onFulfilled : deferred.onRejected;
	      if (cb === null) {
	        (self._state === 1 ? resolve : reject)(deferred.promise, self._value);
	        return;
	      }
	      var ret;
	      try {
	        ret = cb(self._value);
	      } catch (e) {
	        reject(deferred.promise, e);
	        return;
	      }
	      resolve(deferred.promise, ret);
	    });
	  }

	  function resolve(self, newValue) {
	    try {
	      // Promise Resolution Procedure: https://github.com/promises-aplus/promises-spec#the-promise-resolution-procedure
	      if (newValue === self) throw new TypeError('A promise cannot be resolved with itself.');
	      if (newValue && (typeof newValue === 'object' || typeof newValue === 'function')) {
	        var then = newValue.then;
	        if (newValue instanceof Promise) {
	          self._state = 3;
	          self._value = newValue;
	          finale(self);
	          return;
	        } else if (typeof then === 'function') {
	          doResolve(bind(then, newValue), self);
	          return;
	        }
	      }
	      self._state = 1;
	      self._value = newValue;
	      finale(self);
	    } catch (e) {
	      reject(self, e);
	    }
	  }

	  function reject(self, newValue) {
	    self._state = 2;
	    self._value = newValue;
	    finale(self);
	  }

	  function finale(self) {
	    if (self._state === 2 && self._deferreds.length === 0) {
	      Promise._immediateFn(function() {
	        if (!self._handled) {
	          Promise._unhandledRejectionFn(self._value);
	        }
	      });
	    }

	    for (var i = 0, len = self._deferreds.length; i < len; i++) {
	      handle(self, self._deferreds[i]);
	    }
	    self._deferreds = null;
	  }

	  function Handler(onFulfilled, onRejected, promise) {
	    this.onFulfilled = typeof onFulfilled === 'function' ? onFulfilled : null;
	    this.onRejected = typeof onRejected === 'function' ? onRejected : null;
	    this.promise = promise;
	  }

	  /**
	   * Take a potentially misbehaving resolver function and make sure
	   * onFulfilled and onRejected are only called once.
	   *
	   * Makes no guarantees about asynchrony.
	   */
	  function doResolve(fn, self) {
	    var done = false;
	    try {
	      fn(function (value) {
	        if (done) return;
	        done = true;
	        resolve(self, value);
	      }, function (reason) {
	        if (done) return;
	        done = true;
	        reject(self, reason);
	      });
	    } catch (ex) {
	      if (done) return;
	      done = true;
	      reject(self, ex);
	    }
	  }

	  Promise.prototype['catch'] = function (onRejected) {
	    return this.then(null, onRejected);
	  };

	  Promise.prototype.then = function (onFulfilled, onRejected) {
	    var prom = new (this.constructor)(noop);

	    handle(this, new Handler(onFulfilled, onRejected, prom));
	    return prom;
	  };

	  Promise.all = function (arr) {
	    var args = Array.prototype.slice.call(arr);

	    return new Promise(function (resolve, reject) {
	      if (args.length === 0) return resolve([]);
	      var remaining = args.length;

	      function res(i, val) {
	        try {
	          if (val && (typeof val === 'object' || typeof val === 'function')) {
	            var then = val.then;
	            if (typeof then === 'function') {
	              then.call(val, function (val) {
	                res(i, val);
	              }, reject);
	              return;
	            }
	          }
	          args[i] = val;
	          if (--remaining === 0) {
	            resolve(args);
	          }
	        } catch (ex) {
	          reject(ex);
	        }
	      }

	      for (var i = 0; i < args.length; i++) {
	        res(i, args[i]);
	      }
	    });
	  };

	  Promise.resolve = function (value) {
	    if (value && typeof value === 'object' && value.constructor === Promise) {
	      return value;
	    }

	    return new Promise(function (resolve) {
	      resolve(value);
	    });
	  };

	  Promise.reject = function (value) {
	    return new Promise(function (resolve, reject) {
	      reject(value);
	    });
	  };

	  Promise.race = function (values) {
	    return new Promise(function (resolve, reject) {
	      for (var i = 0, len = values.length; i < len; i++) {
	        values[i].then(resolve, reject);
	      }
	    });
	  };

	  // Use polyfill for setImmediate for performance gains
	  Promise._immediateFn = (typeof setImmediate === 'function' && setImmediate) ||
	    function (fn) {
	      setTimeoutFunc(fn, 0);
	    };

	  Promise._unhandledRejectionFn = function _unhandledRejectionFn(err) {
	    if (typeof console !== 'undefined' && console) {
	      console.warn('Possible Unhandled Promise Rejection:', err); // eslint-disable-line no-console
	    }
	  };

	  /**
	   * Set the immediate function to execute callbacks
	   * @param fn {function} Function to execute
	   * @deprecated
	   */
	  Promise._setImmediateFn = function _setImmediateFn(fn) {
	    Promise._immediateFn = fn;
	  };

	  /**
	   * Change the function to execute on unhandled rejection
	   * @param {function} fn Function to execute on unhandled rejection
	   * @deprecated
	   */
	  Promise._setUnhandledRejectionFn = function _setUnhandledRejectionFn(fn) {
	    Promise._unhandledRejectionFn = fn;
	  };
	  
	  if (typeof module !== 'undefined' && module.exports) {
	    module.exports = Promise;
	  } else if (!root.Promise) {
	    root.Promise = Promise;
	  }

	})(this);

	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(46).setImmediate))

/***/ },
/* 46 */
/***/ function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(setImmediate, clearImmediate) {var nextTick = __webpack_require__(47).nextTick;
	var apply = Function.prototype.apply;
	var slice = Array.prototype.slice;
	var immediateIds = {};
	var nextImmediateId = 0;

	// DOM APIs, for completeness

	exports.setTimeout = function() {
	  return new Timeout(apply.call(setTimeout, window, arguments), clearTimeout);
	};
	exports.setInterval = function() {
	  return new Timeout(apply.call(setInterval, window, arguments), clearInterval);
	};
	exports.clearTimeout =
	exports.clearInterval = function(timeout) { timeout.close(); };

	function Timeout(id, clearFn) {
	  this._id = id;
	  this._clearFn = clearFn;
	}
	Timeout.prototype.unref = Timeout.prototype.ref = function() {};
	Timeout.prototype.close = function() {
	  this._clearFn.call(window, this._id);
	};

	// Does not start the time, just sets up the members needed.
	exports.enroll = function(item, msecs) {
	  clearTimeout(item._idleTimeoutId);
	  item._idleTimeout = msecs;
	};

	exports.unenroll = function(item) {
	  clearTimeout(item._idleTimeoutId);
	  item._idleTimeout = -1;
	};

	exports._unrefActive = exports.active = function(item) {
	  clearTimeout(item._idleTimeoutId);

	  var msecs = item._idleTimeout;
	  if (msecs >= 0) {
	    item._idleTimeoutId = setTimeout(function onTimeout() {
	      if (item._onTimeout)
	        item._onTimeout();
	    }, msecs);
	  }
	};

	// That's not how node.js implements it but the exposed api is the same.
	exports.setImmediate = typeof setImmediate === "function" ? setImmediate : function(fn) {
	  var id = nextImmediateId++;
	  var args = arguments.length < 2 ? false : slice.call(arguments, 1);

	  immediateIds[id] = true;

	  nextTick(function onNextTick() {
	    if (immediateIds[id]) {
	      // fn.call() is faster so we optimize for the common use-case
	      // @see http://jsperf.com/call-apply-segu
	      if (args) {
	        fn.apply(null, args);
	      } else {
	        fn.call(null);
	      }
	      // Prevent ids from leaking
	      exports.clearImmediate(id);
	    }
	  });

	  return id;
	};

	exports.clearImmediate = typeof clearImmediate === "function" ? clearImmediate : function(id) {
	  delete immediateIds[id];
	};
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(46).setImmediate, __webpack_require__(46).clearImmediate))

/***/ },
/* 47 */
/***/ function(module, exports) {

	// shim for using process in browser

	var process = module.exports = {};
	var queue = [];
	var draining = false;
	var currentQueue;
	var queueIndex = -1;

	function cleanUpNextTick() {
	    draining = false;
	    if (currentQueue.length) {
	        queue = currentQueue.concat(queue);
	    } else {
	        queueIndex = -1;
	    }
	    if (queue.length) {
	        drainQueue();
	    }
	}

	function drainQueue() {
	    if (draining) {
	        return;
	    }
	    var timeout = setTimeout(cleanUpNextTick);
	    draining = true;

	    var len = queue.length;
	    while(len) {
	        currentQueue = queue;
	        queue = [];
	        while (++queueIndex < len) {
	            if (currentQueue) {
	                currentQueue[queueIndex].run();
	            }
	        }
	        queueIndex = -1;
	        len = queue.length;
	    }
	    currentQueue = null;
	    draining = false;
	    clearTimeout(timeout);
	}

	process.nextTick = function (fun) {
	    var args = new Array(arguments.length - 1);
	    if (arguments.length > 1) {
	        for (var i = 1; i < arguments.length; i++) {
	            args[i - 1] = arguments[i];
	        }
	    }
	    queue.push(new Item(fun, args));
	    if (queue.length === 1 && !draining) {
	        setTimeout(drainQueue, 0);
	    }
	};

	// v8 likes predictible objects
	function Item(fun, array) {
	    this.fun = fun;
	    this.array = array;
	}
	Item.prototype.run = function () {
	    this.fun.apply(null, this.array);
	};
	process.title = 'browser';
	process.browser = true;
	process.env = {};
	process.argv = [];
	process.version = ''; // empty string to avoid regexp issues
	process.versions = {};

	function noop() {}

	process.on = noop;
	process.addListener = noop;
	process.once = noop;
	process.off = noop;
	process.removeListener = noop;
	process.removeAllListeners = noop;
	process.emit = noop;

	process.binding = function (name) {
	    throw new Error('process.binding is not supported');
	};

	process.cwd = function () { return '/' };
	process.chdir = function (dir) {
	    throw new Error('process.chdir is not supported');
	};
	process.umask = function() { return 0; };


/***/ },
/* 48 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(49);

	var Util = __webpack_require__(13),
	    Constant = __webpack_require__(6),
	    WorkPlus = __webpack_require__(5),
	    Modal = __webpack_require__(15),
	    Xhr = __webpack_require__(50);

	var workplusEscape = __webpack_require__(11);

	var list = {
	    init: function init(params) {
	        console.log(params);
	        var _orgCode = Util.sessionStorage.getItem(Constant.SESSION.ORG_CODE),
	            _this = this;

	        _this.bindEvents();
	        _this.org_code = _orgCode;
	    },
	    cordova: function cordova() {
	        WorkPlus.changeTitle(['申请成员审批']);
	        WorkPlus.emptyRightButton();
	    },
	    getShareLink: function getShareLink() {
	        Xhr.getShareLink({
	            org_code: list.org_code,
	            name: workplusEscape.unescapeString(Util.sessionStorage.getItem(Constant.SESSION.NICKNAME)),
	            success: function success(res) {
	                console.log(res);
	                if (res.status !== 0) {
	                    Modal.showPopup('获取分享链接失败！');
	                    return;
	                }
	                var _link = res.result.content;
	                WorkPlus.openShareBox({
	                    url: _link,
	                    title: Util.sessionStorage.getItem(Constant.SESSION.ORG_NAME),
	                    cover_media_id: Util.sessionStorage.getItem(Constant.SESSION.ORG_LOGO)
	                });
	            },
	            error: function error(err) {
	                console.log(err);
	            }
	        });
	    },
	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '#list',
	            selector: '.share',
	            event: 'click',
	            handler: function handler() {
	                _this.getShareLink();
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = list;

/***/ },
/* 49 */
2,
/* 50 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Util = __webpack_require__(13),
	    Constant = __webpack_require__(6);

	module.exports = {
	    // 审核申请
	    approvalPetitions: function approvalPetitions(params) {
	        params.path = '/admin/organizations/' + params.org_code + '/applications?access_token=' + Util.getAccessToken() + '&status=' + params.status + '&skip=' + params.skip + '&limit=10';
	        return Util.request(params);
	    },

	    getHistoryApprovalListByOrgcode: function getHistoryApprovalListByOrgcode(params) {
	        params.path = '/admin/organizations/' + params.org_code + '/applications?access_token=' + Util.getAccessToken() + '&status=APPLYING,APPROVED,REJECTED,IGNORED&skip=' + params.skip + '&limit=10';
	        return Util.request(params);
	    },

	    push: function push(params) {
	        params.path = '/admin/organizations/' + params.org_code + '/applications?access_token=' + Util.getAccessToken();
	        params.type = 'POST';
	        return Util.request(params);
	    },

	    // 管理员
	    getAdministratorList: function getAdministratorList(params) {
	        params.path = '/organizations/' + params.org_code + '/admins?access_token=' + Util.getAccessToken() + '&primary=false';
	        return Util.request(params);
	    },
	    addAdministrator: function addAdministrator(params) {
	        params.path = '/organizations/' + params.org_code + '/admins?access_token=' + Util.getAccessToken();
	        params.type = 'POST';
	        return Util.request(params);
	    },
	    removeAdministrator: function removeAdministrator(params) {
	        params.path = '/organizations/' + params.org_code + '/admins/' + params.user_id + '?access_token=' + Util.getAccessToken();
	        params.type = 'DELETE';
	        return Util.request(params);
	    },

	    getQRCode: function getQRCode(params) {
	        params.path = '/organizations/' + params.org_code + '/share?access_token=' + Util.getAccessToken() + '&format=&inviter=' + params.name + '&props=lang%3d' + params.lng + ',app_name%3d' + params.app_name;
	        return '<img src="' + window.CONSTANT_CONFIG.base_path + params.path + '" />';
	    },

	    getOrgInfo: function getOrgInfo(params) {
	        params.path = '/organizations/' + params.org_code + '?access_token=' + Util.getAccessToken();
	        params.counting && (params.path += '&counting=true');
	        return Util.request(params);
	    },

	    dismissOrgById: function dismissOrgById(params) {
	        params.path = '/admin/organizations/' + params.org_code + '?access_token=' + Util.getAccessToken();
	        params.type = 'DELETE';
	        return Util.request(params);
	    },

	    editOrgName: function editOrgName(params) {
	        params.path = '/admin/organizations/' + params.org_code + '/profile?access_token=' + Util.getAccessToken();
	        params.type = 'POST';
	        return Util.request(params);
	    },

	    getShareLink: function getShareLink(params) {
	        params.path = '/organizations/' + params.org_code + '/share?access_token=' + Util.getAccessToken() + '&format=url&inviter=' + params.name + '&props=lang%3d' + params.lng + ',app_name%3d' + params.app_name + ',appDownloadUrl%3d' + params.appDownloadUrl;
	        return Util.request(params);
	    },

	    getDomainsSetting: function getDomainsSetting(params) {
	        params.path = '/domains/' + params.domainId + '/settings?refresh_time=-1';
	        return Util.request(params);
	    },

	    setOrgSkin: function setOrgSkin(params) {
	        params.path = '/admin/organizations/' + params.org_code + '/settings?access_token=' + Util.getAccessToken();
	        params.type = 'POST';
	        return Util.request(params);
	    },

	    getOrgSetting: function getOrgSetting(params) {
	        params.path = '/organizations/' + params.org_code + '/settings?access_token=' + Util.getAccessToken() + '&refresh_time=-1';
	        return Util.request(params);
	    },

	    // 组织设置
	    setApplicationSetting: function setApplicationSetting(params) {
	        params.path = '/organizations/' + params.org_code + '/settings/application?access_token=' + Util.getAccessToken();
	        params.type = 'POST';
	        return Util.request(params);
	    },

	    // 获取通讯录树
	    getContacts: function getContacts(params) {
	        params.path = '/organizations/' + params.org_code + '/view?org_id=' + params.org_id + '&access_token=' + Util.getAccessToken() + '&filter_senior=false&rank_view=true&org_skip=' + (params.org_skip || 0) + '&org_limit=' + (params.org_limit || 0) + '&employee_skip=' + (params.employee_skip || 0) + '&employee_limit=' + (params.employee_limit || 0) + '&duplicate_removal=false';
	        return Util.request(params);
	    },
	    searchContacts: function searchContacts(params) {
	        params.path = '/organizations/employees?access_token=' + Util.getAccessToken();
	        params.type = 'POST';
	        return Util.request(params);
	    },

	    // 新增雇员、组织
	    addOrg: function addOrg(params) {
	        params.path = '/organizations/' + params.org_code + '/nodes?access_token=' + Util.getAccessToken();
	        params.type = 'POST';
	        return Util.request(params);
	    },
	    addEmployee: function addEmployee(params) {
	        params.path = '/organizations/' + params.org_code + '/employees?access_token=' + Util.getAccessToken();
	        params.type = 'POST';
	        return Util.request(params);
	    },
	    updateEmployee: function updateEmployee(params) {
	        params.path = '/organizations/' + params.org_code + '/employees/' + params.user_id + '?access_token=' + Util.getAccessToken();
	        params.type = 'POST';
	        return Util.request(params);
	    }

	};

/***/ },
/* 51 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(52);

	var Util = __webpack_require__(13),
	    Xhr = __webpack_require__(50),
	    WorkPlus = __webpack_require__(5),
	    Constant = __webpack_require__(6),
	    Status = __webpack_require__(53),
	    itemTpl = __webpack_require__(54),
	    itemTplOld = __webpack_require__(55),
	    Modal = __webpack_require__(15);

	var approval = {
	    init: function init(page) {
	        var _this = this;
	        this.loadMoreElm = $('.load-more-approval');
	        this.loadMoreFn = null;
	        this.ORG_CODE = page.orgcode ? page.orgcode : Util.sessionStorage.getItem(Constant.SESSION.ORG_CODE);
	        this.Status = ['APPLYING', 'APPROVED', 'REJECTED', 'IGNORED'];
	        this.ignoreCount = 0;

	        this.loadMoreStatus = this.Status[0];
	        this.Skip = 0;
	        this.Limit = 10;
	        this.allApprovalList = [];

	        this.selectAllText = polyglot.t('Approval.header.select');
	        this.unSelectAllText = polyglot.t('Approval.header.unselect');

	        _this.getApprovalList();
	        _this.bindEvents();
	    },
	    cordova: function cordova() {
	        WorkPlus.changeTitle([polyglot.t('Approval.header.title')]);
	        window.cordovaEvents.selectAll = approval.selectAll;
	    },
	    /**
	     * @status => 状态 'APPLYING', 'APPROVED', 'REJECTED', 'IGNORED'
	     * @skip => 过滤条数
	     * @callback => 回调函数
	     */
	    getAllApprovalList: function getAllApprovalList(status, skip, callback) {
	        var _this = this,
	            _error = polyglot.t('Approval.message.error');
	        var params = {
	            skip: this.loadMoreStatus.indexOf('APPLYING') < 0 ? skip + this.ignoreCount : skip,
	            status: $.isArray(status) ? status.join(',') : status,
	            org_code: _this.ORG_CODE,
	            success: deal,
	            error: err
	        };
	        function deal(res) {
	            console.log(res);
	            if (res.status !== Status.SUCCESS) {
	                Modal.alert(_error);
	                return;
	            };
	            $('.approval-page .loading').hide();
	            $('.load-more-approval').trigger('loaded');
	            _this.Skip++;
	            _this.allApprovalList = _this.allApprovalList.concat(res.result.records);
	            callback && callback(res);
	        };
	        function err(err) {
	            Modal.alert(_error);
	        };
	        Xhr.approvalPetitions(params);
	    },

	    getApprovalList: function getApprovalList() {
	        var _this = this;
	        this.getAllApprovalList(this.Status[0], 0, function (res) {
	            var _len = res.result.records.length;
	            if (_len === 0) {
	                // 查询 历史记录
	                _this.setLoadHistoryStatus();
	                _this.getMoreApprovalList();
	                _this.checkStatus(true);
	                return;
	            }
	            _this.loadMoreElm.css('display', 'block');
	            if (_len > 0 && _len < _this.Limit) {
	                // 显示查找更多并切换到查找历史
	                _this.setLoadHistoryStatus();
	            }
	            _this.tranformDataAndRenderView(res);
	            _this.checkStatus();
	        });
	    },

	    getMoreApprovalList: function getMoreApprovalList() {
	        // 根据loadMoreStatus来判断是属于什么类型的请求
	        var _APPLYING = this.loadMoreStatus.indexOf('APPLYING') > -1 ? true : false,
	            _this = this;

	        this.getAllApprovalList(this.loadMoreStatus, this.Skip * this.Limit, function (res) {
	            var _len = res.result.records.length;
	            // 当前申请
	            if (_APPLYING && _len === 0) {
	                _this.setLoadHistoryStatus();
	                _this.getMoreApprovalList();
	                return;
	            }
	            if (_APPLYING && _len > 0 && _len < _this.Limit) {
	                _this.setLoadHistoryStatus();
	            }
	            // 完全没有申请信息（包括当前申请的和历史的）
	            if (!_APPLYING && _this.allApprovalList.length === 0) {
	                $('.no-approval-list').show();
	                return;
	            }
	            _this.loadMoreElm.css('display', 'block');
	            if (!_APPLYING && _len < _this.Limit || _this.allApprovalList.length > 100) {
	                _this.loadMoreElm.hide();
	            }
	            _this.tranformDataAndRenderView(res);
	            if (_APPLYING) _this.checkStatus();
	        });
	    },

	    tranformDataAndRenderView: function tranformDataAndRenderView(res) {
	        res.result.records = this.tranformData(res.result.records);
	        var _list = Util.renderTpl(itemTpl, { "list": res.result.records, "default_avatar": Constant.DEFAULT_USER_AVATAR });
	        $('.approval-list').append(_list);
	    },

	    setLoadHistoryStatus: function setLoadHistoryStatus() {
	        this.loadMoreStatus = ['APPROVED', 'REJECTED', 'IGNORED'];
	        this.Skip = 0;
	    },

	    // 获取被选中的审批人员信息
	    getPushListAndSave: function getPushListAndSave(ids, type) {
	        var _pushItem = [],
	            _typeTxt = type === 'APPROVE' ? polyglot.t('Approval.content.joined') : polyglot.t('Approval.content.denied'),
	            _this = this;
	        _.each(ids, function (id) {
	            _.each(_this.nowApprovalList, function (item) {
	                if (item.id == id) {
	                    item.status = type;
	                    item.status_txt = _typeTxt;
	                    _pushItem.push(item);
	                }
	            });
	        });
	        if (_pushItem.length > 0) {
	            _this.oldApprovalList = _pushItem.concat(_this.oldApprovalList);
	            localStorage.setItem(_this.storageName, JSON.stringify(_this.oldApprovalList));
	            console.log('已存入最新的列表');
	        }
	    },
	    tranformData: function tranformData(list) {
	        _.each(list, function (item, index) {
	            item.addresser.avatar_bk = Util.makeImagesLinkByMediaid(item.addresser.avatar, Constant.DEFAULT_USER_AVATAR);
	            switch (item.status) {
	                case 'APPLYING':
	                    item.status_txt = polyglot.t('Approval.content.application');
	                    break;
	                case 'APPROVED':
	                    item.status_txt = polyglot.t('Approval.content.joined');
	                    break;
	                case 'REJECTED':
	                    item.status_txt = polyglot.t('Approval.content.denied');
	                    break;
	                default:
	                    item.status_txt = polyglot.t('Approval.content.ignore');
	                    break;
	            }
	        });
	        return list; // 倒转顺序 reverse()
	    },
	    toggleSelectStatus: function toggleSelectStatus(item) {
	        var _item = $(item),
	            _checkItem = _item.find('.approval-check');
	        if (_checkItem.hasClass('deal')) return;
	        _checkItem.toggleClass('checked');
	        this.checkStatus();
	    },
	    checkStatus: function checkStatus(push) {
	        var _all = true;
	        $('.approval-item').each(function (index, item) {
	            var _checkElm = $(item).find('.approval-check');
	            if (!_checkElm.hasClass('deal') && !_checkElm.hasClass('checked')) {
	                _all = false;
	            }
	        });
	        if (!_all) {
	            WorkPlus.setRightButton([{ "icon": Constant.ICON.disable, "title": approval.selectAllText, "action": "js", "value": "cordovaEvents.selectAll(true)" }]);
	        } else {
	            if (push) {
	                WorkPlus.emptyRightButton();
	                return;
	            }
	            WorkPlus.setRightButton([{ "icon": Constant.ICON.disable, "title": approval.unSelectAllText, "action": "js", "value": "cordovaEvents.selectAll(false)" }]);
	        }
	    },
	    selectAll: function selectAll(bool) {
	        var _item = $('.approval-item');
	        _class = 'checked';
	        _item.each(function (index, item) {
	            var _check = $(item).find('.approval-check');
	            if (_check.hasClass('deal')) return;
	            if (bool) {
	                _check.addClass(_class);
	            } else {
	                _check.removeClass(_class);
	            }
	        });
	        if (bool) {
	            WorkPlus.setRightButton([{ "icon": Constant.ICON.disable, "title": approval.unSelectAllText, "action": "js", "value": "cordovaEvents.selectAll(false)" }]);
	        } else {
	            WorkPlus.setRightButton([{ "icon": Constant.ICON.disable, "title": approval.selectAllText, "action": "js", "value": "cordovaEvents.selectAll(true)" }]);
	        }
	    },
	    getAllSelected: function getAllSelected() {
	        var _select = {
	            code: [],
	            elms: []
	        };
	        $('.approval-item').each(function (index, item) {
	            if ($(item).find('.checked').length !== 0) {
	                _select.code.push($(item).attr('data-id').trim());
	                _select.elms.push(item);
	            }
	        });
	        return _select;
	    },
	    confirmModal: function confirmModal(txt, cb) {
	        if (this.getAllSelected().elms.length === 0) {
	            Modal.showPopup(polyglot.t('Approval.message.noRecord'));
	            return;
	        }
	        Modal.confirm(txt, function () {
	            cb && cb();
	        });
	    },
	    allThrough: function allThrough() {
	        var _this = this;
	        _this.confirmModal(polyglot.t('Approval.message.through'), function () {
	            _this.push(_this.getAllSelected(), 'APPROVE');
	        });
	    },
	    allReject: function allReject() {
	        var _this = this;
	        _this.confirmModal(polyglot.t('Approval.message.denied'), function () {
	            _this.push(_this.getAllSelected(), 'REJECT');
	        });
	    },
	    push: function push(obj, type) {
	        var _type = type === 'APPROVE' ? polyglot.t('Approval.content.joined') : polyglot.t('Approval.content.denied'),
	            _this = this;
	        var params = {
	            org_code: _this.ORG_CODE,
	            data: {
	                applications: obj.code,
	                ops: type
	            },
	            success: deal,
	            error: err
	        };
	        function deal(res) {
	            console.log(res);
	            if (res.status === Status.APPROVALED) {
	                Modal.alert(polyglot.t('Approval.message.processed'), function () {
	                    window.location.reload();
	                });
	                return;
	            }
	            if (res.status !== Status.SUCCESS) {
	                Modal.alert(polyglot.t('Approval.message.error'));
	                return;
	            };
	            // 变换状态文字描述
	            $(obj.elms).each(function (index, item) {
	                $(item).find('.approval-check').removeClass('checked').addClass('deal').html('<span class="' + type + '">' + _type + '</span>');
	            });
	            _this.getPushListAndSave(obj.code, type);
	            _this.checkStatus(true);
	            _this.ignoreCount += obj.code.length;
	        };
	        function err(err) {
	            console.log(err);
	            Modal.alert(polyglot.t('Approval.message.error'));
	        };
	        Xhr.push(params);
	    },
	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.approval-page',
	            selector: '.through-all',
	            event: 'click',
	            handler: function handler() {
	                _this.allThrough();
	            }
	        }, {
	            element: '.approval-page',
	            selector: '.reject-all',
	            event: 'click',
	            handler: function handler() {
	                _this.allReject();
	            }
	        }, {
	            element: '.approval-page',
	            selector: '.approval-item',
	            event: 'click',
	            handler: function handler() {
	                _this.toggleSelectStatus(this);
	            }
	        }, {
	            element: '.approval-page',
	            selector: '.load-more-approval',
	            event: 'click',
	            handler: function handler() {
	                $(this).html(polyglot.t('Approval.content.loading'));
	                _this.getMoreApprovalList();
	            }
	        }, {
	            element: '.load-more-approval',
	            event: 'loaded',
	            handler: function handler() {
	                $(this).html(polyglot.t('Approval.button.more'));
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = approval;

/***/ },
/* 52 */
2,
/* 53 */
/***/ function(module, exports) {

	"use strict";

	module.exports = {
	    SUCCESS: 0,
	    APPROVALED: 202023
	};

/***/ },
/* 54 */
/***/ function(module, exports) {

	module.exports = "<% for (var i = 0, l = list.length; i < l; i++) {%>\n<li class=\"approval-item\" data-id=\"<%= list[i].id %>\" data-code=\"<%= list[i].org_code %>\">\n    <a href=\"javascript:;\">\n        <div class=\"approval-avatar\">\n            <img src=\"<%= list[i].addresser.avatar_bk %>\" alt=\"\" onerror=\"this.error=null;this.src='<%= default_avatar %>'\" />\n        </div>\n        <div class=\"approval-content\">\n            <p><%= list[i].addresser.name %></p>\n            <p><%= list[i].addresser.phone %></p>\n        </div>\n        <% if (list[i].status === 'APPLYING') {%>\n        <div class=\"approval-check\">\n            <i></i>\n        </div>\n        <% } else { %>\n        <div class=\"approval-check deal\">\n            <span class=\"<%= list[i].status %>\"><%= list[i].status_txt %></span>\n        </div>\n        <% } %>\n    </a>\n</li>\n<% } %>";

/***/ },
/* 55 */
/***/ function(module, exports) {

	module.exports = "<% for (var i = 0, l = list.length; i < l; i++) {%>\n<li class=\"approval-item\" data-id=\"<%= list[i].id %>\" data-code=\"<%= list[i].org_code %>\">\n    <a href=\"javascript:;\">\n        <div class=\"approval-avatar\">\n            <img src=\"<%= list[i].addresser.avatar_bk %>\" alt=\"\" onerror=\"this.error=null;this.src='<%= default_avatar %>'\" />\n        </div>\n        <div class=\"approval-content\">\n            <p><%= list[i].addresser.name %></p>\n            <p><%= list[i].addresser.phone %></p>\n        </div>\n        <div class=\"approval-check deal\">\n            <span class=\"<%= list[i].status %>\"><%= list[i].status_txt %></span>\n        </div>\n    </a>\n</li>\n<% } %>";

/***/ },
/* 56 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(57);

	var Util = __webpack_require__(13),
	    Xhr = __webpack_require__(50),
	    WorkPlus = __webpack_require__(5),
	    WorkPlusPromise = __webpack_require__(12),
	    Constant = __webpack_require__(6),
	    Status = __webpack_require__(53),
	    itemTpl = __webpack_require__(58),
	    Modal = __webpack_require__(15);

	var administrator = {
	    init: function init(page) {
	        var _this = this;
	        this.loadMoreElm = $('.load-more-administrator');
	        this.loadMoreFn = null;
	        this.ORG_CODE = page.orgcode ? page.orgcode : Util.sessionStorage.getItem(Constant.SESSION.ORG_CODE);
	        this.USER_ID = Util.sessionStorage.getItem(Constant.SESSION.USER_ID);
	        this.Skip = 0;
	        this.Limit = 10;
	        this.list = [];
	        this.listMap = {};
	        this.removeStack = [];

	        _this.getList();
	        _this.bindEvents();
	    },
	    cordova: function cordova() {
	        WorkPlus.changeTitle([polyglot.t('Administrator.header.title')]);
	        window.cordovaEvents.addAdministrator = administrator.add;
	        WorkPlus.setRightButton([{ "icon": Constant.ICON.disable, "title": polyglot.t('Administrator.header.add'), "action": "js", "value": "cordovaEvents.addAdministrator(true)" }]);
	    },

	    getList: function getList() {
	        var _this = this,
	            _error = polyglot.t('Administrator.message.error');
	        var params = {
	            skip: this.Skip * this.Limit,
	            org_code: _this.ORG_CODE,
	            success: success,
	            error: error
	        };
	        function success(res) {
	            console.log(res);
	            if (res.status !== Status.SUCCESS) {
	                Modal.alert(_error);
	                return;
	            };
	            $('.administrator-page .loading').hide();
	            _this.loadMoreElm.trigger('loaded');
	            if (!res.result || res.result.length < _this.Limit) {
	                _this.loadMoreElm.hide();
	            }
	            _this.Skip++;
	            var list = _this.tranformData(res.result);
	            _this.list = _this.list.concat(list);
	            _this.renderView(list);
	        };
	        function error(err) {
	            Modal.alert(_error);
	        };
	        Xhr.getAdministratorList(params);
	    },

	    renderView: function renderView(list) {
	        var _list = Util.renderTpl(itemTpl, { "list": list, "default_avatar": Constant.DEFAULT_USER_AVATAR, "current_user_is_owner": this.current_user_is_owner });
	        $('.administrator-list').append(_list);
	    },
	    tranformData: function tranformData(list) {
	        var _this = this;
	        _.each(list, function (item, index) {
	            if (item.user_id === _this.USER_ID) {
	                _this.current_user_is_owner = item.type === 'OWNER';
	            }
	            _this.listMap[item.user_id] = item;
	            item.avatar_bk = Util.makeImagesLinkByMediaid(item.avatar, Constant.DEFAULT_USER_AVATAR);
	        });
	        return list; // 倒转顺序 reverse()
	    },

	    add: function add() {
	        WorkPlusPromise.getContacts().then(administrator.tranformContacts).then(administrator.addAdministrator).then(function (list) {
	            if (!list || !list.length) return;
	            administrator.list = administrator.list.concat(list);
	            administrator.renderView(list);
	            var containerDom = $('.administrator-container');
	            containerDom.scrollTop(containerDom.prop('scrollHeight'), 300);
	        }).catch(function (err) {
	            return Modal.alert(polyglot.t('Administrator.message.error'));
	        });
	    },

	    addAdministrator: function addAdministrator(list) {
	        return new Promise(function (resolve, reject) {
	            if (!list.length) return resolve();
	            Xhr.addAdministrator({
	                org_code: administrator.ORG_CODE,
	                data: {
	                    "adminIds": list.map(function (i) {
	                        return i.user_id;
	                    }),
	                    "type": "super_admin",
	                    "scopes": [],
	                    "features": []
	                },
	                success: function success(res) {
	                    if (res.status !== Status.SUCCESS) {
	                        return reject();
	                    };
	                    resolve(list);
	                },
	                error: reject
	            });
	        });
	    },
	    tranformContacts: function tranformContacts(contacts) {
	        if (!contacts || !contacts.length) return [];
	        var list = [];
	        _.each(contacts, function (e) {
	            if (administrator.listMap[e.user_id]) return;
	            var item = {
	                id: e.id,
	                name: e.name,
	                user_id: e.user_id,
	                username: e.username,
	                avatar: e.avatar,
	                avatar_bk: Util.makeImagesLinkByMediaid(e.avatar, Constant.DEFAULT_USER_AVATAR),
	                mobile: e.mobile
	            };
	            list.push(item);
	            administrator.listMap[e.user_id] = item;
	        });
	        return list;
	    },
	    removeAdministrator: function removeAdministrator(user_id, callback) {
	        Xhr.removeAdministrator({
	            org_code: administrator.ORG_CODE,
	            user_id: user_id,
	            success: function success(res) {
	                if (res.status !== Status.SUCCESS) {
	                    Modal.toast('error', res.message);
	                    callback && callback(true, res);
	                    return;
	                };
	                Modal.toast(polyglot.t('Administrator.message.removed'));
	                callback && callback();
	            },
	            error: function error(err) {
	                Modal.alert(polyglot.t('Administrator.message.error'));
	                callback && callback(true, err);
	            }
	        });
	    },


	    confirmModal: function confirmModal(txt, cb) {
	        if (this.getAllSelected().elms.length === 0) {
	            Modal.showPopup(polyglot.t('Approval.message.noRecord'));
	            return;
	        }
	        Modal.confirm(txt, function () {
	            cb && cb();
	        });
	    },
	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.administrator-page',
	            selector: '.administrator-action__btn',
	            event: 'click',
	            handler: function handler() {
	                var thisBtn = $(this);
	                if (thisBtn.hasClass('removing')) return;

	                thisBtn.addClass('removing');
	                var thisItem = thisBtn.parents('.administrator-item');
	                var user_id = thisItem.data('userid');
	                _this.removeAdministrator(user_id, function (hasError) {
	                    thisBtn.removeClass('removing');
	                    if (hasError) return;
	                    delete administrator.listMap[user_id];
	                    thisItem.remove();
	                });
	            }
	        }, {
	            element: '.administrator-page',
	            selector: '.load-more-administrator',
	            event: 'click',
	            handler: function handler() {
	                $(this).html(polyglot.t('Approval.content.loading'));
	                _this.getList();
	            }
	        }, {
	            element: '.load-more-administrator',
	            event: 'loaded',
	            handler: function handler() {
	                $(this).html(polyglot.t('Approval.button.more'));
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = administrator;

/***/ },
/* 57 */
2,
/* 58 */
/***/ function(module, exports) {

	module.exports = "<% for (var i = 0, l = list.length; i < l; i++) {%>\n<li class=\"administrator-item\" data-id=\"<%= list[i].id %>\" data-code=\"<%= list[i].org_code %>\" data-userid=\"<%= list[i].user_id %>\">\n    <a href=\"javascript:;\">\n        <div class=\"administrator-avatar\">\n            <img src=\"<%= list[i].avatar_bk %>\" alt=\"\" onerror=\"this.error=null;this.src='<%= default_avatar %>'\" />\n        </div>\n        <div class=\"administrator-content\">\n            <p><%= list[i].name %></p>\n            <p><%= list[i].username %></p>\n        </div>\n        <% if (current_user_is_owner) {%>\n        <div class=\"administrator-action\">\n            <div class=\"administrator-action__btn\">\n                <div class=\"loader\"></div>\n                <span><%= polyglot.t('Administrator.button.cancel') %></span>\n            </div>\n        </div>\n        <% } %>\n    </a>\n</li>\n<% } %>";

/***/ },
/* 59 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(60);

	var Util = __webpack_require__(13),
	    WorkPlus = __webpack_require__(5),
	    Constant = __webpack_require__(6),
	    Colors = __webpack_require__(61),
	    Status = __webpack_require__(53),
	    Xhr = __webpack_require__(50);

	var workplusEscape = __webpack_require__(11);

	var set = {
	    init: function init(page) {
	        var _this2 = this;

	        this.orgcode = page.orgcode;
	        Util.sessionStorage.addItem(Constant.SESSION.orgcode, this.orgcode);

	        this.getOrgSetting(this.orgcode).then(function (res) {
	            _this2.initApproveSetting();
	            _this2.bindEvents();
	        }).catch(function (err) {
	            console.log(err);
	            Modal.alert(err);
	        });
	    },
	    cordova: function cordova() {
	        WorkPlus.changeTitle([polyglot.t('Setting.header.title')]);
	        WorkPlus.emptyRightButton();
	    },
	    initApproveSetting: function initApproveSetting() {
	        var auto_approve = this.application_settings.hasOwnProperty('auto_approve') ? this.application_settings.auto_approve : true;
	        this.activeSetting = auto_approve ? 'direct' : 'approve';
	        $('.set-page').find('.set-item[data-name=' + this.activeSetting + ']').addClass('active');
	    },

	    getOrgSetting: function getOrgSetting(orgcode) {
	        var _this = this;
	        return new Promise(function (resolve, reject) {
	            var params = {
	                org_code: orgcode,
	                success: function success(res) {
	                    if (res.status !== Status.SUCCESS) {
	                        reject(polyglot.t('app.xhr.error'));
	                        // reject(res.message);
	                    }
	                    if (res.result.theme_settings) {
	                        var _theme = {};
	                        _.forEach(Colors(), function (color) {
	                            if (color.key.trim().toUpperCase() === res.result.theme_settings.theme.toUpperCase()) {
	                                _theme = color;
	                                return false;
	                            }
	                        });
	                        _theme.org_code = orgcode;
	                        Util.sessionStorage.addItem(Constant.SESSION.THEME, _theme, true);
	                    }
	                    if (res.result.application_settings) {
	                        Util.sessionStorage.addItem(Constant.SESSION.APPLICATION_SETTINGS, res.result.application_settings, true);
	                    }
	                    _this.application_settings = res.result.application_settings || {};
	                    resolve(res);
	                },
	                error: function error(err) {
	                    reject(err);
	                }
	            };
	            Xhr.getOrgSetting(params);
	        });
	    },
	    setSettings: function setSettings(setting, callback) {
	        var self = this;
	        var auto_approve = setting === 'direct';
	        Xhr.setApplicationSetting({
	            org_code: this.orgcode,
	            data: {
	                auto_approve: auto_approve
	            },
	            success: function success(res) {
	                self.application_settings.auto_approve = auto_approve;
	                Util.sessionStorage.addItem(Constant.SESSION.APPLICATION_SETTINGS, self.application_settings, true);
	                callback && callback();
	            },
	            error: function error(err) {}
	        });
	    },

	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.set-page',
	            selector: '.set-item',
	            event: 'click',
	            handler: function handler() {
	                var thisItem = $(this);
	                var thisSetting = thisItem.data('name');

	                if (set.activeSetting === thisSetting || thisItem.hasClass('loading')) return;

	                thisItem.addClass('loading');
	                set.setSettings(thisSetting, function () {
	                    set.activeSetting = thisSetting;
	                    thisItem.parent().find('.set-item').removeClass('active');
	                    thisItem.addClass('active');
	                    thisItem.removeClass('loading');
	                });
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = set;

/***/ },
/* 60 */
2,
/* 61 */
/***/ function(module, exports) {

	'use strict';

	// 要C1、3、8、9、10、15 、16
	var orgSKins = [{
	    name: '天空清澈蓝',
	    key: 'skyblue',
	    colors: ['#1a98ff', '#B8DFFF', '#B8DFFF', '#E8F4FF', '#DBDBDB', '#ECECEC', '#F3F3F3']
	}, {
	    name: '商务蓝',
	    key: 'businessblue',
	    colors: ['#435E87', '#1A98FF', '#B8DFFF', '#E8F4FF', '#DBDBDB', '#ECECEC', '#F3F3F3']
	}, {
	    name: '耀沙金',
	    key: 'shakin',
	    colors: ['#8C7F71', '#4DC9C4', '#9EDEDC', '#FFF4E6', '#DBDBDB', '#ECECEC', '#F3F3F3']
	}, {
	    name: '中国红',
	    key: 'chinared',
	    colors: ['#C54149', '#3882A9', '#9AC0D5', '#D7E6EE', '#DBDBDB', '#ECECEC', '#F3F3F3']
	}, {
	    name: '冰川蓝',
	    key: 'glacierblue',
	    colors: ['#00B6DC', '#7BDAEE', '#A97357', '#EEE3DD', '#DBDBDB', '#ECECEC', '#F3F3F3']

	}, {
	    name: '墨玉绿',
	    key: 'blackjadegreen',
	    colors: ['#2C5261', '#3D8EAD', '#9DC6D7', '#E9E2E3', '#DBDBDB', '#ECECEC', '#F3F3F3']
	}, {
	    name: '繁星蓝',
	    key: 'bluestars',
	    colors: ['#3F7CBF', '#9EBCE0', '#C79E73', '#E3E3E6', '#DBDBDB', '#ECECEC', '#F3F3F3']
	}, {
	    name: '活力珠光橙',
	    key: 'vibrantorange',
	    colors: ['#FF9908', '#FFD798', '#2ECBB7', '#FFF4E5', '#DBDBDB', '#ECECEC', '#F3F3F3']

	}];

	var getLocaleSkinList = function getLocaleSkinList() {
	    for (var i = 0; i < orgSKins.length; i += 1) {
	        orgSKins[i].name = polyglot.t('Skin.colors.' + orgSKins[i].key);
	    }
	    return orgSKins;
	};

	module.exports = getLocaleSkinList;

/***/ },
/* 62 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(63);

	var Util = __webpack_require__(13),
	    WorkPlus = __webpack_require__(5),
	    Constant = __webpack_require__(6),
	    Modal = __webpack_require__(15),
	    Status = __webpack_require__(53),
	    Xhr = __webpack_require__(50);

	var colors = __webpack_require__(61),
	    skinTpl = __webpack_require__(64);

	var skinModule = {
	    init: function init(page) {
	        var _colors = Util.renderTpl(skinTpl, { "colors": colors() }),
	            _theme = Util.sessionStorage.getItem(Constant.SESSION.THEME, true);
	        $('.skin-page ul').append(_colors);
	        this.theme = _theme ? _theme.key : 'skyblue';
	        this.themeName = _theme ? _theme.name : polyglot.t('Skin.colors.skyblue');
	        this.orgcode = Util.sessionStorage.getItem(Constant.SESSION.ORG_CODE);

	        this.initTheme(this.theme);

	        this.bindEvents();
	    },
	    cordova: function cordova() {
	        WorkPlus.changeTitle([polyglot.t('Skin.header.title')]);
	        WorkPlus.setRightButton([{ "icon": Constant.ICON.disable, "title": polyglot.t('Skin.header.submit'), "action": "js", "value": "cordovaEvents.setSkin()" }]);
	        window.cordovaEvents.setSkin = skinModule.setSkin;
	    },
	    initTheme: function initTheme(theme) {
	        theme = theme.toUpperCase();
	        $('.skin-page ul li').forEach(function (item) {
	            if ($(item).attr('data-theme').toUpperCase().trim() === theme) {
	                $(item).addClass('selected ' + theme.toLowerCase());
	                return false;
	            }
	        });
	    },
	    setSkin: function setSkin() {
	        var params = {
	            org_code: skinModule.orgcode,
	            data: {
	                "type": "SYSTEM", //类型，系统/自定义
	                "theme": skinModule.theme
	            },
	            success: function success(res) {
	                console.log(res);
	                if (res.status !== Status.SUCCESS) {
	                    Modal.alert(polyglot.t('Skin.message.settingFailed'));
	                    return;
	                }
	                Modal.showPopup(polyglot.t('Skin.message.settingSuccess'));
	                $('.set-page span.skin-name').text(skinModule.themeName);
	                WorkPlus.setOrgColorsTheme(skinModule.orgcode, skinModule.theme);
	                Util.sessionStorage.addItem(Constant.SESSION.THEME, {
	                    org_code: skinModule.orgcode,
	                    key: skinModule.theme,
	                    name: skinModule.themeName
	                }, true);
	                window.history.go(-1);
	            },
	            error: function error(err) {
	                console.log(err);
	                Modal.alert(polyglot.t('Skin.message.settingFailed'));
	            }
	        };
	        Xhr.setOrgSkin(params);
	    },
	    selectColorsItem: function selectColorsItem(item) {
	        var _elm = $(item);
	        _elm.addClass('selected').removeClass(skinModule.theme).siblings('li').removeClass('selected');

	        skinModule.theme = _elm.attr('data-theme').trim();
	        skinModule.themeName = _elm.attr('data-theme-name').trim();
	        _elm.addClass(skinModule.theme);
	    },
	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.skin-page',
	            selector: '.colors-item',
	            event: 'click',
	            handler: function handler() {
	                _this.selectColorsItem(this);
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = skinModule;

/***/ },
/* 63 */
2,
/* 64 */
/***/ function(module, exports) {

	module.exports = "<% for(var i = 0; i < colors.length; i++) {%>\n<li data-theme=\"<%= colors[i].key %>\" data-theme-name=\"<%= colors[i].name %>\" class=\"colors-item selected\">\n    <div class=\"skin-name\"><%= colors[i].name %></div>\n    <div class=\"skin-colors\">\n        <% for(var k = 0; k < colors[i].colors.length; k++) {%>\n        <span style=\"background:<%= colors[i].colors[k]%>\"></span>\n        <% } %>\n        <div class=\"skin-selceted\"></div>\n    </div>\n</li>\n<% } %>";

/***/ },
/* 65 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(66);

	var Util = __webpack_require__(13),
	    WorkPlus = __webpack_require__(5),
	    Constant = __webpack_require__(6),
	    Modal = __webpack_require__(15),
	    Status = __webpack_require__(53),
	    Xhr = __webpack_require__(50);

	var managementModule = __webpack_require__(67);
	var workplusEscape = __webpack_require__(11);

	var edit = {
	    init: function init() {
	        this.$page = $('.edit-org-name-page');

	        this.setOrgName();
	        var lang = '.' + polyglot.locale().toLowerCase();
	        this.$page.find(lang).focus();
	        this.bindEvents();
	    },
	    setOrgName: function setOrgName() {
	        var _orgInfo = Util.sessionStorage.getItem(Constant.SESSION.ORG, true);
	        var _orgName = _orgInfo.name;
	        var _orgEnName = _orgInfo.en_name || '';
	        var _orgRTWName = _orgInfo.tw_name || '';

	        this.orgInfo = _orgInfo;
	        this.oldOrgName = _orgName + _orgEnName + _orgRTWName;
	        this.$page.find('#org_name').val(workplusEscape.unescapeString(_orgName));
	        this.$page.find('#org_en_name').val(workplusEscape.unescapeString(_orgEnName));
	        this.$page.find('#org_tw_name').val(workplusEscape.unescapeString(_orgRTWName));
	    },
	    cordova: function cordova() {
	        WorkPlus.changeTitle([polyglot.t('OrgName.header.title')]);
	        WorkPlus.setRightButton([{ "icon": Constant.ICON.disable, "title": polyglot.t('OrgName.header.done'), "action": "js", "value": "cordovaEvents.editOrgName()" }]);
	        window.cordovaEvents.editOrgName = edit.editOrgName;
	    },
	    nothingChanged: function nothingChanged(cn, en, tw) {
	        var newName = workplusEscape.escapeString(cn + en + tw);
	        if (newName === edit.oldOrgName) return true;
	        return false;
	    },
	    editOrgName: function editOrgName() {
	        var _textarea = $('.edit-org-name-page').find('#org_name'),
	            _name = _textarea.val().trim(),
	            _enName = $('.edit-org-name-page').find('#org_en_name').val().trim(),
	            _twName = $('.edit-org-name-page').find('#org_tw_name').val().trim();

	        _textarea.blur(); // 手动失去光标
	        if (_name === '') {
	            Modal.alert(polyglot.t('OrgName.message.nameCantEmpty'));
	            return;
	        }
	        if (Util.getStrLength(_name) > 50) {
	            Modal.alert(polyglot.t('OrgName.message.tooLonger'));
	            return;
	        }

	        if (Constant.OPEN_ALL_I18N) {
	            if (Util.getStrLength(_enName) > 100) {
	                Modal.alert(polyglot.t('OrgName.message.enNameTooLonger'));
	                return;
	            }
	            if (Util.getStrLength(_twName) > 50) {
	                Modal.alert(polyglot.t('OrgName.message.twNameTooLonger'));
	                return;
	            }
	        }

	        // 名字未有发生改变
	        if (edit.nothingChanged(_name, _enName, _twName)) {
	            window.history.go(-1);
	            return;
	        }

	        var params = {
	            org_code: Util.sessionStorage.getItem(Constant.SESSION.ORG_CODE),
	            data: {
	                name: _name
	            },
	            success: function success(res) {
	                if (res.status !== Status.SUCCESS) {
	                    Modal.alert(res.message);
	                    return;
	                }
	                // 如果成功 将返回上一页，并同步更新组织名称
	                edit.updateOrgInfo(_name, _enName, _twName);
	                Modal.showPopup(polyglot.t('OrgName.message.modifySuccess'));
	                window.history.go(-1);
	            },
	            error: function error(err) {}
	        };
	        if (Constant.OPEN_ALL_I18N) {
	            params.data.en_name = _enName;
	            params.data.tw_name = _twName;
	        }

	        Xhr.editOrgName(params);
	    },
	    updateOrgInfo: function updateOrgInfo(name, enName, twName) {
	        //更新 sessionStorage
	        edit.orgInfo.name = workplusEscape.escapeString(name);
	        edit.orgInfo.en_name = workplusEscape.escapeString(enName);
	        edit.orgInfo.tw_name = workplusEscape.escapeString(twName);
	        Util.sessionStorage.addItem(Constant.SESSION.ORG, edit.orgInfo, true);

	        managementModule.setOrgName();
	    },
	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.edit-org-name-page',
	            selector: '.clear-btn',
	            event: 'click',
	            handler: function handler() {
	                $(this).prev().val('');
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = edit;

/***/ },
/* 66 */
2,
/* 67 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(68);

	var Util = __webpack_require__(13),
	    WorkPlus = __webpack_require__(5),
	    WorkPlusPromise = __webpack_require__(12),
	    getValueByLangType = __webpack_require__(69),
	    Constant = __webpack_require__(6),
	    Modal = __webpack_require__(15),
	    Status = __webpack_require__(53),
	    Colors = __webpack_require__(61),
	    Pic = __webpack_require__(70),
	    Xhr = __webpack_require__(50),
	    SparkMD5 = __webpack_require__(71);

	var workplusEscape = __webpack_require__(11);

	var management = {
	    init: function init(page) {
	        var _this = this;
	        this.orgDetail = null;
	        this.canTap = true;
	        this.orgcode = page.orgcode;
	        Util.sessionStorage.addItem(Constant.SESSION.ORG_CODE, this.orgcode);

	        Promise.all([this.getOrgInfo(this.orgcode), this.getDomainsSetting(), WorkPlusPromise.getAppInfo(), this.getOrgSetting(this.orgcode), WorkPlusPromise.getLoginUserInfo()]).then(function (res) {
	            console.log(res);
	            _this.appDownloadUrl = res[1].result.domain_settings.workplus_url;
	            _this.appName = res[2].app_name;
	            _this.setInfo();
	            _this.bindEvents();
	        }).catch(function (err) {
	            console.log(err);
	            Modal.alert(err);
	        });
	    },
	    cordova: function cordova() {
	        WorkPlus.changeTitle([polyglot.t('Management.header.title')]);
	        WorkPlus.emptyRightButton();
	    },
	    getOrgInfo: function getOrgInfo(orgcode) {
	        var _this = this;
	        return new Promise(function (resolve, reject) {
	            var params = {
	                org_code: orgcode,
	                counting: true,
	                success: function success(res) {
	                    if (res.status !== Status.SUCCESS) {
	                        // 没有对应的雇员
	                        reject(polyglot.t('app.xhr.error'));
	                        return;
	                    }
	                    _this.orgDetail = res.result;
	                    var orgName = getValueByLangType.getOrgName(polyglot.locale(), res.result);
	                    Util.sessionStorage.addItem(Constant.SESSION.ORG, res.result, true);
	                    Util.sessionStorage.addItem(Constant.SESSION.ORG_NAME, orgName);
	                    Util.sessionStorage.addItem(Constant.SESSION.ORG_LOGO, res.result.logo);
	                    window.ORG_OWNER = res.result.owner;
	                    resolve(res);
	                },
	                error: function error(err) {
	                    reject(err);
	                }
	            };
	            Xhr.getOrgInfo(params);
	        });
	    },
	    getDomainsSetting: function getDomainsSetting() {
	        var _this = this;
	        return new Promise(function (resolve, reject) {
	            var params = {
	                domainId: window.DEVICE_INFO.domain_id,
	                success: function success(res) {
	                    if (res.status !== Status.SUCCESS) {
	                        reject(polyglot.t('app.xhr.error'));
	                        // reject(res.message);
	                    }
	                    if (res.result.domain_settings.org_settings.request_enabled) {
	                        $('.request_enabled').show();
	                    }
	                    window.REQUEST_ENABLED = res.result.domain_settings.org_settings.request_enabled;
	                    resolve(res);
	                },
	                error: function error(err) {
	                    reject(err);
	                }
	            };
	            Xhr.getDomainsSetting(params);
	        });
	    },
	    getOrgSetting: function getOrgSetting(orgcode) {
	        var _this = this;
	        return new Promise(function (resolve, reject) {
	            var params = {
	                org_code: orgcode,
	                success: function success(res) {
	                    if (res.status !== Status.SUCCESS) {
	                        reject(polyglot.t('app.xhr.error'));
	                        // reject(res.message);
	                    }
	                    if (res.result.theme_settings) {
	                        var _theme = {};
	                        _.forEach(Colors(), function (color) {
	                            if (color.key.trim().toUpperCase() === res.result.theme_settings.theme.toUpperCase()) {
	                                _theme = color;
	                                return false;
	                            }
	                        });
	                        _theme.org_code = orgcode;
	                        Util.sessionStorage.addItem(Constant.SESSION.THEME, _theme, true);
	                    }
	                    if (res.result.application_settings) {
	                        Util.sessionStorage.addItem(Constant.SESSION.APPLICATION_SETTINGS, res.result.application_settings, true);
	                    }
	                    resolve(res);
	                },
	                error: function error(err) {
	                    reject(err);
	                }
	            };
	            Xhr.getOrgSetting(params);
	        });
	    },
	    getShareLink: function getShareLink() {
	        var _this = this,
	            _name = workplusEscape.unescapeString(Util.sessionStorage.getItem(Constant.SESSION.NICKNAME)),
	            _orgName = workplusEscape.unescapeString(Util.sessionStorage.getItem(Constant.SESSION.ORG_NAME)),
	            _orgLogo = Util.sessionStorage.getItem(Constant.SESSION.ORG_LOGO);

	        Xhr.getShareLink({
	            org_code: _this.orgDetail.org_code,
	            name: encodeURI(_name),
	            app_name: encodeURI(_this.appName),
	            appDownloadUrl: encodeURI(_this.appDownloadUrl),
	            lng: polyglot.locale(),
	            success: function success(res) {
	                console.log(res);
	                if (res.status !== 0) {
	                    Modal.showPopup(polyglot.t('Management.message.getLinkFailed'));
	                    return;
	                }
	                // 分享链接时带上当前的语言类型
	                var _link = workplusEscape.unescapeString(res.result.content) + '&lang=' + polyglot.locale() + '?'; // 关于添加这个问号的原因 http://www.jianshu.com/p/3bdb72ecdf95
	                var _title = polyglot.t('Management.message.inviteTitle').replace('<org_name>', _orgName);
	                var _summary = polyglot.t('Management.message.inviteContent').replace('<name>', _name).replace('<app_name>', _this.appName).replace('<org_name>', _orgName);

	                WorkPlus.openShareBox({
	                    url: _link,
	                    title: _title,
	                    org_code: _this.orgDetail.org_code,
	                    org_owner: _this.orgDetail.owner,
	                    org_avatar: _orgLogo,
	                    org_name: _orgName, // 名字 移动端会进行 encodeURIComponent
	                    org_domainId: _this.orgDetail.domain_id,
	                    summary: _summary,
	                    cover_media_id: _orgLogo,
	                    lang: polyglot.locale()
	                });
	            },
	            error: function error(err) {
	                console.log(err);
	                Modal.showPopup(polyglot.t('Management.message.getLinkFailed'));
	            }
	        });
	    },
	    isOwner: function isOwner() {
	        var currentUserId = sessionStorage.getItem(Constant.SESSION.USER_ID);
	        return window.ORG_OWNER === currentUserId;
	    },
	    setInfo: function setInfo() {
	        this.setOrgName();
	        this.setOrgLogo();
	        this.setOrgNumber();
	        this.setAdminPath();

	        if (this.isOwner()) {
	            var orgNameEditBtn = $('.org-name-btn');
	            orgNameEditBtn.show();
	        }
	    },
	    setOrgName: function setOrgName() {
	        var _orgInfo = Util.sessionStorage.getItem(Constant.SESSION.ORG, true);
	        var _orgName = getValueByLangType.getOrgName(polyglot.locale(), _orgInfo);
	        $('.management-page').find('.org-name').html(_orgName);
	    },
	    setOrgLogo: function setOrgLogo() {
	        var _orgLogo = Util.sessionStorage.getItem(Constant.SESSION.ORG_LOGO);
	        $('.management-page').find('.org-logo').html('<img src="' + Util.makeImagesLinkByMediaid(_orgLogo, Constant.DEFAULT_AVATAR) + '" onerror="this.error=null;this.src=\'' + Constant.DEFAULT_AVATAR + '\'" alt="logo" />');
	    },
	    setOrgNumber: function setOrgNumber() {
	        $('.management-page').find('.org-number').html((this.orgDetail.all_employee_count || 0) + '人');
	    },
	    setAdminPath: function setAdminPath() {
	        $('.management-page').find('.admin-path').html(workplusEscape.unescapeString(window.CONSTANT_CONFIG.admin_link));
	    },
	    getPicData: {
	        resolveImgURI: function resolveImgURI(result, callback) {
	            console.log(result);
	            var _this = this; // this->getPicData
	            if (cordova.platformId == 'android') {
	                result = 'file://' + result;
	            }
	            window.resolveLocalFileSystemURL(result, function (fileEntry) {
	                fileEntry.file(function (file) {
	                    _this.returnData(file, callback);
	                }, _this.fail);
	            }, _this.fail);
	        },
	        returnData: function returnData(file, callback) {
	            var _data = {},
	                _count = 2,
	                _this = this;
	            _this.readDataUrl(file, function (dataUrl) {
	                --_count;
	                _data.dataUrl = dataUrl.target._result;
	                if (_count === 0) callback(_data);
	            });
	            _this.readAsBinaryString(file, function (binaryString) {
	                --_count;
	                _data.binaryString = binaryString.target._result;
	                if (_count === 0) callback(_data);
	            });
	        },
	        readDataUrl: function readDataUrl(file, callback) {
	            var reader = new FileReader();
	            reader.onloadend = function (evt) {
	                callback(evt);
	            };
	            reader.readAsDataURL(file);
	        },
	        readAsBinaryString: function readAsBinaryString(file, callback) {
	            var reader = new FileReader();
	            reader.onloadend = function (evt) {
	                callback(evt);
	            };
	            reader.readAsBinaryString(file);
	        },
	        fail: function fail(evt) {
	            console.log(evt.target);
	        }
	    },
	    resetUploadStatus: function resetUploadStatus() {
	        var _avatar = $('.management-page .org-logo'),
	            _lastLogo = Util.sessionStorage.getItem(Constant.SESSION.ORG_LOGO);
	        if (_avatar.find('img').length !== 0) {
	            _avatar.find('img').attr('src', Util.makeImagesLinkByMediaid(_lastLogo, Constant.DEFAULT_AVATAR));
	        }
	    },
	    renderAvatarBeforeUpload: function renderAvatarBeforeUpload(data, url) {
	        var _avatar = $('.management-page .org-logo');
	        if (_avatar.find('img').length !== 0) {
	            _avatar.find('img').attr('src', data.dataUrl);
	        } else {
	            _avatar.append($('<img src="' + data.dataUrl + '" />'));
	        }
	        this.uploadAvatar(url, data.binaryString);
	    },

	    saveOrgLogo: function saveOrgLogo(mediaid) {
	        var params = {
	            org_code: this.orgcode,
	            data: {
	                logo: mediaid
	            },
	            success: function success(res) {
	                if (res.status !== Status.SUCCESS) {
	                    Modal.showPopup(polyglot.t('Setting.message.updateFailed'));
	                    return;
	                }
	                Util.sessionStorage.addItem(Constant.SESSION.ORG_LOGO, mediaid);
	                Modal.showPopup(polyglot.t('Setting.message.updateSuccess'));
	            },
	            error: function error(err) {
	                Modal.showPopup(polyglot.t('Setting.message.updateFailed'));
	            }
	        };
	        Xhr.editOrgName(params);
	    },
	    uploadAvatar: function uploadAvatar(url, binaryString) {
	        var _avatar = $('.management-page .org-logo'),
	            _this = this;
	        var params = {
	            fileURL: url,
	            fileDigest: SparkMD5.hash(binaryString),
	            success: function success(res) {
	                console.log(res);
	                if (res.status !== Status.SUCCESS) {
	                    //失败
	                    Modal.showPopup(polyglot.t('Setting.message.uploadFailed'));
	                    _this.resetUploadStatus();
	                    return;
	                }
	                _this.saveOrgLogo(res.result);
	            },
	            error: function error(err) {
	                console.log(err);
	                Modal.showPopup(polyglot.t('Setting.message.uploadFailed'));
	                _this.resetUploadStatus();
	            },
	            progress: function progress(_progress) {
	                console.log(_progress);
	            }
	        };
	        Pic.upload(params);
	    },
	    openPhotoSlector: function openPhotoSlector() {
	        // 打开相册
	        var _this = this;
	        WorkPlus.getAvatarByPhotoAlbum(function (pics) {
	            if (!pics) return;
	            console.log(pics);
	            _this.getPicData.resolveImgURI(pics.success, function (data) {
	                console.log(data);
	                _this.renderAvatarBeforeUpload(data, pics.success);
	            });
	        });
	    },
	    openCamera: function openCamera() {
	        // 打开相机  
	        var _this = this;
	        WorkPlus.getAvatarByTakePhoto(function (pics) {
	            if (!pics) return;
	            console.log(pics);
	            _this.getPicData.resolveImgURI(pics, function (data) {
	                _this.renderAvatarBeforeUpload(data, pics);
	            });
	        });
	    },
	    openActionSheet: function openActionSheet() {
	        if (!this.isOwner()) return;

	        var _this = this;
	        if (!_this.canTap) return;
	        _this.canTap = false;
	        setTimeout(function () {
	            _this.canTap = true;
	        }, 1000);
	        var sheet = [{
	            text: polyglot.t('Management.actionSheet.photos'),
	            action: function action() {
	                _this.openPhotoSlector();
	                Modal.closeModal();
	            }
	        }, {
	            text: polyglot.t('Management.actionSheet.camera'),
	            action: function action() {
	                _this.openCamera();
	                Modal.closeModal();
	            }
	        }];
	        Modal.actionSheet(sheet);
	    },
	    goPage: function goPage(route) {
	        var href = window.location.href;
	        var newHref = href.substring(0, href.lastIndexOf('/#/')) + '/#/' + route + '?orgcode=' + management.orgcode;
	        WorkPlus.openWebView({
	            "url": newHref,
	            "title": "",
	            "hidden_share": 1
	        });
	    },
	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.management-page',
	            selector: '.share',
	            event: 'click',
	            handler: function handler() {
	                _this.getShareLink();
	            }
	        }, {
	            element: '.management-page',
	            selector: 'li a',
	            event: 'click',
	            handler: function handler() {
	                var route = $(this).data('route');
	                _this.goPage(route);
	            }
	        }, {
	            element: '.management-page',
	            selector: '.org-logo',
	            event: 'click',
	            handler: function handler() {
	                _this.openActionSheet();
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = management;

/***/ },
/* 68 */
2,
/* 69 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Constants = __webpack_require__(6);

	var getKeywordByLangType = function getKeywordByLangType(lang) {
	    var keyword = '';
	    if (!Constants.OPEN_ALL_I18N) return keyword;

	    switch (lang.toUpperCase()) {
	        case 'EN':
	            keyword = 'en_';
	            break;
	        case 'ZH-RTW':
	            keyword = 'tw_';
	            break;
	        default:
	            break;
	    }
	    return keyword;
	};

	var getOrgName = function getOrgName(lang, org) {
	    var keyword = getKeywordByLangType(lang);
	    var key = keyword + 'name';
	    return org[key] || org.name;
	};

	var willShowAllInput = function willShowAllInput() {
	    return !!Constants.OPEN_ALL_I18N;
	};

	module.exports = {
	    getOrgName: getOrgName,
	    willShowAllInput: willShowAllInput
	};

/***/ },
/* 70 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Constant = __webpack_require__(6),
	    Util = __webpack_require__(13);

	var initFileUploadOptions = function initFileUploadOptions(fileURL) {
	    var options = new FileUploadOptions();
	    options.fileKey = 'file';
	    options.fileName = fileURL.substr(fileURL.lastIndexOf('/') + 1);
	    options.mimeType = 'image/jpeg';
	    options.headers = {
	        'Content-Type': 'multipart/form-data;boundary=+++++org.apache.cordova.formBoundary'
	    };
	    return options;
	};

	var initURI = function initURI(fileDigest) {
	    return encodeURI(window.CONSTANT_CONFIG.img_link + '?access_token=' + Util.getAccessToken() + '&file_size=666666&fileDigest=' + fileDigest);
	};

	module.exports = {
	    upload: function upload(params) {
	        var success = function success(r) {
	            if (r.responseCode == 200) {
	                params.success(JSON.parse(r.response));
	            }
	        };
	        var failure = function failure(error) {
	            params.error(error);
	        };
	        var ft = new FileTransfer();
	        ft.onprogress = function (progressEvent) {
	            params.progress(progressEvent.loaded / progressEvent.total);
	        };
	        ft.upload(params.fileURL, initURI(params.fileDigest), success, failure, initFileUploadOptions(params.fileURL));
	    }
	};

/***/ },
/* 71 */
/***/ function(module, exports, __webpack_require__) {

	var __WEBPACK_AMD_DEFINE_FACTORY__, __WEBPACK_AMD_DEFINE_RESULT__;"use strict";

	var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

	(function (factory) {
	  if (( false ? "undefined" : _typeof(exports)) === "object") {
	    module.exports = factory();
	  } else if (true) {
	    !(__WEBPACK_AMD_DEFINE_FACTORY__ = (factory), __WEBPACK_AMD_DEFINE_RESULT__ = (typeof __WEBPACK_AMD_DEFINE_FACTORY__ === 'function' ? (__WEBPACK_AMD_DEFINE_FACTORY__.call(exports, __webpack_require__, exports, module)) : __WEBPACK_AMD_DEFINE_FACTORY__), __WEBPACK_AMD_DEFINE_RESULT__ !== undefined && (module.exports = __WEBPACK_AMD_DEFINE_RESULT__));
	  } else {
	    var glob;try {
	      glob = window;
	    } catch (e) {
	      glob = self;
	    }glob.SparkMD5 = factory();
	  }
	})(function (undefined) {
	  "use strict";
	  var add32 = function add32(a, b) {
	    return a + b & 4294967295;
	  },
	      hex_chr = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"];function cmn(q, a, b, x, s, t) {
	    a = add32(add32(a, q), add32(x, t));return add32(a << s | a >>> 32 - s, b);
	  }function ff(a, b, c, d, x, s, t) {
	    return cmn(b & c | ~b & d, a, b, x, s, t);
	  }function gg(a, b, c, d, x, s, t) {
	    return cmn(b & d | c & ~d, a, b, x, s, t);
	  }function hh(a, b, c, d, x, s, t) {
	    return cmn(b ^ c ^ d, a, b, x, s, t);
	  }function ii(a, b, c, d, x, s, t) {
	    return cmn(c ^ (b | ~d), a, b, x, s, t);
	  }function md5cycle(x, k) {
	    var a = x[0],
	        b = x[1],
	        c = x[2],
	        d = x[3];a = ff(a, b, c, d, k[0], 7, -680876936);d = ff(d, a, b, c, k[1], 12, -389564586);c = ff(c, d, a, b, k[2], 17, 606105819);b = ff(b, c, d, a, k[3], 22, -1044525330);a = ff(a, b, c, d, k[4], 7, -176418897);d = ff(d, a, b, c, k[5], 12, 1200080426);c = ff(c, d, a, b, k[6], 17, -1473231341);b = ff(b, c, d, a, k[7], 22, -45705983);a = ff(a, b, c, d, k[8], 7, 1770035416);d = ff(d, a, b, c, k[9], 12, -1958414417);c = ff(c, d, a, b, k[10], 17, -42063);b = ff(b, c, d, a, k[11], 22, -1990404162);a = ff(a, b, c, d, k[12], 7, 1804603682);d = ff(d, a, b, c, k[13], 12, -40341101);c = ff(c, d, a, b, k[14], 17, -1502002290);b = ff(b, c, d, a, k[15], 22, 1236535329);a = gg(a, b, c, d, k[1], 5, -165796510);d = gg(d, a, b, c, k[6], 9, -1069501632);c = gg(c, d, a, b, k[11], 14, 643717713);b = gg(b, c, d, a, k[0], 20, -373897302);a = gg(a, b, c, d, k[5], 5, -701558691);d = gg(d, a, b, c, k[10], 9, 38016083);c = gg(c, d, a, b, k[15], 14, -660478335);b = gg(b, c, d, a, k[4], 20, -405537848);a = gg(a, b, c, d, k[9], 5, 568446438);d = gg(d, a, b, c, k[14], 9, -1019803690);c = gg(c, d, a, b, k[3], 14, -187363961);b = gg(b, c, d, a, k[8], 20, 1163531501);a = gg(a, b, c, d, k[13], 5, -1444681467);d = gg(d, a, b, c, k[2], 9, -51403784);c = gg(c, d, a, b, k[7], 14, 1735328473);b = gg(b, c, d, a, k[12], 20, -1926607734);a = hh(a, b, c, d, k[5], 4, -378558);d = hh(d, a, b, c, k[8], 11, -2022574463);c = hh(c, d, a, b, k[11], 16, 1839030562);b = hh(b, c, d, a, k[14], 23, -35309556);a = hh(a, b, c, d, k[1], 4, -1530992060);d = hh(d, a, b, c, k[4], 11, 1272893353);c = hh(c, d, a, b, k[7], 16, -155497632);b = hh(b, c, d, a, k[10], 23, -1094730640);a = hh(a, b, c, d, k[13], 4, 681279174);d = hh(d, a, b, c, k[0], 11, -358537222);c = hh(c, d, a, b, k[3], 16, -722521979);b = hh(b, c, d, a, k[6], 23, 76029189);a = hh(a, b, c, d, k[9], 4, -640364487);d = hh(d, a, b, c, k[12], 11, -421815835);c = hh(c, d, a, b, k[15], 16, 530742520);b = hh(b, c, d, a, k[2], 23, -995338651);a = ii(a, b, c, d, k[0], 6, -198630844);d = ii(d, a, b, c, k[7], 10, 1126891415);c = ii(c, d, a, b, k[14], 15, -1416354905);b = ii(b, c, d, a, k[5], 21, -57434055);a = ii(a, b, c, d, k[12], 6, 1700485571);d = ii(d, a, b, c, k[3], 10, -1894986606);c = ii(c, d, a, b, k[10], 15, -1051523);b = ii(b, c, d, a, k[1], 21, -2054922799);a = ii(a, b, c, d, k[8], 6, 1873313359);d = ii(d, a, b, c, k[15], 10, -30611744);c = ii(c, d, a, b, k[6], 15, -1560198380);b = ii(b, c, d, a, k[13], 21, 1309151649);a = ii(a, b, c, d, k[4], 6, -145523070);d = ii(d, a, b, c, k[11], 10, -1120210379);c = ii(c, d, a, b, k[2], 15, 718787259);b = ii(b, c, d, a, k[9], 21, -343485551);x[0] = add32(a, x[0]);x[1] = add32(b, x[1]);x[2] = add32(c, x[2]);x[3] = add32(d, x[3]);
	  }function md5blk(s) {
	    var md5blks = [],
	        i;for (i = 0; i < 64; i += 4) {
	      md5blks[i >> 2] = s.charCodeAt(i) + (s.charCodeAt(i + 1) << 8) + (s.charCodeAt(i + 2) << 16) + (s.charCodeAt(i + 3) << 24);
	    }return md5blks;
	  }function md5blk_array(a) {
	    var md5blks = [],
	        i;for (i = 0; i < 64; i += 4) {
	      md5blks[i >> 2] = a[i] + (a[i + 1] << 8) + (a[i + 2] << 16) + (a[i + 3] << 24);
	    }return md5blks;
	  }function md51(s) {
	    var n = s.length,
	        state = [1732584193, -271733879, -1732584194, 271733878],
	        i,
	        length,
	        tail,
	        tmp,
	        lo,
	        hi;for (i = 64; i <= n; i += 64) {
	      md5cycle(state, md5blk(s.substring(i - 64, i)));
	    }s = s.substring(i - 64);length = s.length;tail = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];for (i = 0; i < length; i += 1) {
	      tail[i >> 2] |= s.charCodeAt(i) << (i % 4 << 3);
	    }tail[i >> 2] |= 128 << (i % 4 << 3);if (i > 55) {
	      md5cycle(state, tail);for (i = 0; i < 16; i += 1) {
	        tail[i] = 0;
	      }
	    }tmp = n * 8;tmp = tmp.toString(16).match(/(.*?)(.{0,8})$/);lo = parseInt(tmp[2], 16);hi = parseInt(tmp[1], 16) || 0;tail[14] = lo;tail[15] = hi;md5cycle(state, tail);return state;
	  }function md51_array(a) {
	    var n = a.length,
	        state = [1732584193, -271733879, -1732584194, 271733878],
	        i,
	        length,
	        tail,
	        tmp,
	        lo,
	        hi;for (i = 64; i <= n; i += 64) {
	      md5cycle(state, md5blk_array(a.subarray(i - 64, i)));
	    }a = i - 64 < n ? a.subarray(i - 64) : new Uint8Array(0);length = a.length;tail = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];for (i = 0; i < length; i += 1) {
	      tail[i >> 2] |= a[i] << (i % 4 << 3);
	    }tail[i >> 2] |= 128 << (i % 4 << 3);if (i > 55) {
	      md5cycle(state, tail);for (i = 0; i < 16; i += 1) {
	        tail[i] = 0;
	      }
	    }tmp = n * 8;tmp = tmp.toString(16).match(/(.*?)(.{0,8})$/);lo = parseInt(tmp[2], 16);hi = parseInt(tmp[1], 16) || 0;tail[14] = lo;tail[15] = hi;md5cycle(state, tail);return state;
	  }function rhex(n) {
	    var s = "",
	        j;for (j = 0; j < 4; j += 1) {
	      s += hex_chr[n >> j * 8 + 4 & 15] + hex_chr[n >> j * 8 & 15];
	    }return s;
	  }function hex(x) {
	    var i;for (i = 0; i < x.length; i += 1) {
	      x[i] = rhex(x[i]);
	    }return x.join("");
	  }if (hex(md51("hello")) !== "5d41402abc4b2a76b9719d911017c592") {
	    add32 = function add32(x, y) {
	      var lsw = (x & 65535) + (y & 65535),
	          msw = (x >> 16) + (y >> 16) + (lsw >> 16);return msw << 16 | lsw & 65535;
	    };
	  }if (typeof ArrayBuffer !== "undefined" && !ArrayBuffer.prototype.slice) {
	    (function () {
	      function clamp(val, length) {
	        val = val | 0 || 0;if (val < 0) {
	          return Math.max(val + length, 0);
	        }return Math.min(val, length);
	      }ArrayBuffer.prototype.slice = function (from, to) {
	        var length = this.byteLength,
	            begin = clamp(from, length),
	            end = length,
	            num,
	            target,
	            targetArray,
	            sourceArray;if (to !== undefined) {
	          end = clamp(to, length);
	        }if (begin > end) {
	          return new ArrayBuffer(0);
	        }num = end - begin;target = new ArrayBuffer(num);targetArray = new Uint8Array(target);sourceArray = new Uint8Array(this, begin, num);targetArray.set(sourceArray);return target;
	      };
	    })();
	  }function toUtf8(str) {
	    if (/[\u0080-\uFFFF]/.test(str)) {
	      str = unescape(encodeURIComponent(str));
	    }return str;
	  }function utf8Str2ArrayBuffer(str, returnUInt8Array) {
	    var length = str.length,
	        buff = new ArrayBuffer(length),
	        arr = new Uint8Array(buff),
	        i;for (i = 0; i < length; i += 1) {
	      arr[i] = str.charCodeAt(i);
	    }return returnUInt8Array ? arr : buff;
	  }function arrayBuffer2Utf8Str(buff) {
	    return String.fromCharCode.apply(null, new Uint8Array(buff));
	  }function concatenateArrayBuffers(first, second, returnUInt8Array) {
	    var result = new Uint8Array(first.byteLength + second.byteLength);result.set(new Uint8Array(first));result.set(new Uint8Array(second), first.byteLength);return returnUInt8Array ? result : result.buffer;
	  }function hexToBinaryString(hex) {
	    var bytes = [],
	        length = hex.length,
	        x;for (x = 0; x < length - 1; x += 2) {
	      bytes.push(parseInt(hex.substr(x, 2), 16));
	    }return String.fromCharCode.apply(String, bytes);
	  }function SparkMD5() {
	    this.reset();
	  }SparkMD5.prototype.append = function (str) {
	    this.appendBinary(toUtf8(str));return this;
	  };SparkMD5.prototype.appendBinary = function (contents) {
	    this._buff += contents;this._length += contents.length;var length = this._buff.length,
	        i;for (i = 64; i <= length; i += 64) {
	      md5cycle(this._hash, md5blk(this._buff.substring(i - 64, i)));
	    }this._buff = this._buff.substring(i - 64);return this;
	  };SparkMD5.prototype.end = function (raw) {
	    var buff = this._buff,
	        length = buff.length,
	        i,
	        tail = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
	        ret;for (i = 0; i < length; i += 1) {
	      tail[i >> 2] |= buff.charCodeAt(i) << (i % 4 << 3);
	    }this._finish(tail, length);ret = hex(this._hash);if (raw) {
	      ret = hexToBinaryString(ret);
	    }this.reset();return ret;
	  };SparkMD5.prototype.reset = function () {
	    this._buff = "";this._length = 0;this._hash = [1732584193, -271733879, -1732584194, 271733878];return this;
	  };SparkMD5.prototype.getState = function () {
	    return { buff: this._buff, length: this._length, hash: this._hash };
	  };SparkMD5.prototype.setState = function (state) {
	    this._buff = state.buff;this._length = state.length;this._hash = state.hash;return this;
	  };SparkMD5.prototype.destroy = function () {
	    delete this._hash;delete this._buff;delete this._length;
	  };SparkMD5.prototype._finish = function (tail, length) {
	    var i = length,
	        tmp,
	        lo,
	        hi;tail[i >> 2] |= 128 << (i % 4 << 3);if (i > 55) {
	      md5cycle(this._hash, tail);for (i = 0; i < 16; i += 1) {
	        tail[i] = 0;
	      }
	    }tmp = this._length * 8;tmp = tmp.toString(16).match(/(.*?)(.{0,8})$/);lo = parseInt(tmp[2], 16);hi = parseInt(tmp[1], 16) || 0;tail[14] = lo;tail[15] = hi;md5cycle(this._hash, tail);
	  };SparkMD5.hash = function (str, raw) {
	    return SparkMD5.hashBinary(toUtf8(str), raw);
	  };SparkMD5.hashBinary = function (content, raw) {
	    var hash = md51(content),
	        ret = hex(hash);return raw ? hexToBinaryString(ret) : ret;
	  };SparkMD5.ArrayBuffer = function () {
	    this.reset();
	  };SparkMD5.ArrayBuffer.prototype.append = function (arr) {
	    var buff = concatenateArrayBuffers(this._buff.buffer, arr, true),
	        length = buff.length,
	        i;this._length += arr.byteLength;for (i = 64; i <= length; i += 64) {
	      md5cycle(this._hash, md5blk_array(buff.subarray(i - 64, i)));
	    }this._buff = i - 64 < length ? new Uint8Array(buff.buffer.slice(i - 64)) : new Uint8Array(0);return this;
	  };SparkMD5.ArrayBuffer.prototype.end = function (raw) {
	    var buff = this._buff,
	        length = buff.length,
	        tail = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
	        i,
	        ret;for (i = 0; i < length; i += 1) {
	      tail[i >> 2] |= buff[i] << (i % 4 << 3);
	    }this._finish(tail, length);ret = hex(this._hash);if (raw) {
	      ret = hexToBinaryString(ret);
	    }this.reset();return ret;
	  };SparkMD5.ArrayBuffer.prototype.reset = function () {
	    this._buff = new Uint8Array(0);this._length = 0;this._hash = [1732584193, -271733879, -1732584194, 271733878];return this;
	  };SparkMD5.ArrayBuffer.prototype.getState = function () {
	    var state = SparkMD5.prototype.getState.call(this);state.buff = arrayBuffer2Utf8Str(state.buff);return state;
	  };SparkMD5.ArrayBuffer.prototype.setState = function (state) {
	    state.buff = utf8Str2ArrayBuffer(state.buff, true);return SparkMD5.prototype.setState.call(this, state);
	  };SparkMD5.ArrayBuffer.prototype.destroy = SparkMD5.prototype.destroy;SparkMD5.ArrayBuffer.prototype._finish = SparkMD5.prototype._finish;SparkMD5.ArrayBuffer.hash = function (arr, raw) {
	    var hash = md51_array(new Uint8Array(arr)),
	        ret = hex(hash);return raw ? hexToBinaryString(ret) : ret;
	  };return SparkMD5;
	});

/***/ },
/* 72 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(73);

	var Util = __webpack_require__(13),
	    WorkPlus = __webpack_require__(5),
	    Constant = __webpack_require__(6),
	    Modal = __webpack_require__(15),
	    Xhr = __webpack_require__(50);

	var workplusEscape = __webpack_require__(11);

	var computer = {
	    init: function init() {
	        this.bindEvents();
	        $('#admin-path').html(workplusEscape.unescapeString(window.CONSTANT_CONFIG.admin_link));
	    },
	    cordova: function cordova() {
	        WorkPlus.changeTitle([polyglot.t('Computer.header.title')]);
	        WorkPlus.emptyRightButton();
	    },
	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.computer-page',
	            selector: '.open-qrc-scanner',
	            event: 'click',
	            handler: function handler() {
	                WorkPlus.openQRC();
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};
	module.exports = computer;

/***/ },
/* 73 */
2,
/* 74 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(75);

	var Util = __webpack_require__(13),
	    getValueByLangType = __webpack_require__(69),
	    Xhr = __webpack_require__(50),
	    WorkPlus = __webpack_require__(5),
	    Constant = __webpack_require__(6),
	    Colors = __webpack_require__(61),
	    Status = __webpack_require__(53),
	    orgTpl = __webpack_require__(76),
	    employeeTpl = __webpack_require__(77),
	    ActionModal = __webpack_require__(78),
	    Modal = __webpack_require__(15);

	var contacts = {
	    init: function init(page) {
	        var _this2 = this;

	        ActionModal.init();

	        this.org_code = page.orgcode;
	        Util.sessionStorage.addItem(Constant.SESSION.ORG_CODE, this.org_code);

	        Promise.all([this.getOrgInfo(this.org_code), this.getOrgSetting(this.org_code)]).then(function (res) {
	            _this2.employeeMap = {};
	            _this2.setInfo();
	            _this2.getConcats({ callback: function callback() {
	                    return $('.contacts-page__loader').hide();
	                } });
	            _this2.bindEvents();
	        }).catch(function (err) {
	            console.log(err);
	            Modal.alert(err);
	        });
	    },
	    cordova: function cordova() {
	        WorkPlus.changeTitle([polyglot.t('Contacts.header.title')]);
	        window.clickMoreEvent = function () {
	            ActionModal.toggleOpen();
	            ;
	        };
	        WorkPlus.setRightButton([{
	            "icon": "base64:iVBORw0KGgoAAAANSUhEUgAAAE4AAABOCAMAAAC5dNAvAAAAWlBMVEUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACLSV5RAAAAHXRSTlMA+u3k0NYcC+CVeVhAHwgy2rConJiMiIFiUEwqJGjdU+0AAAHhSURBVFjD7ZjZ1oIgEIBBTdzIcs/y/V/zz6HiIKjgHM5/UXM54OdsDAv5yZdKnPdVlASUBklU9XmMYRXNZVrIpSkO2tWFk1HCzt3GR02nVaH1wwnGWgX2jNszfgqwZfa0m3TzlObl60tW5ulJunyzhJ3TDyvjy0GefYjp2SoF0Rs2mieMb2BkkZJ7+HJmWJ8zvOfc92iliDjNNj05Z1TkqNyxTdCCYrfCXxM37YuFFxEnu8Ij4W+84YOYcmVWtXkVv16PSipoxFIEL12tXvE763pnwpnbymgIweXEWngA4TP/v4UKKZw6GNRLa+whMJQRJ8nABFN/qcFwY54mEGMtQIBqQ8mBcQNxw5EBzNOLr4NVT1xxBPpBp6nB6tEdN0KMtBwJ49xw0rxlPTSQVkecTG6zUMIOyI/gOOyXi7wKX11x0ls1tzksZmecbBy5oupB5YyTpvSKqppV5TFcOQ9WigpaDTuGY9DWFFUytyYNYRQdPbepRNccxem2ULDXGScjRX3i8M76TAW+UHyWMX6R+WwB+AblsX3im7vHrQe/MfrctvGHCo9HHvyBzONxEX+Y9XzUxl8E/F9T8Jco/BXP8wUUfz32f3nHPy3gHz78P8vgH43wT1r//+C28hz4k++UP+3UhH33tvdwAAAAAElFTkSuQmCC",
	            "title": "",
	            "action": "js",
	            "value": "clickMoreEvent()"
	        }]);
	    },
	    getOrgInfo: function getOrgInfo(orgcode) {
	        var _this = this;
	        return new Promise(function (resolve, reject) {
	            var params = {
	                org_code: orgcode,
	                counting: true,
	                success: function success(res) {
	                    if (res.status !== Status.SUCCESS) {
	                        // 没有对应的雇员
	                        reject(polyglot.t('app.xhr.error'));
	                        return;
	                    }
	                    _this.orgDetail = res.result;
	                    var orgName = getValueByLangType.getOrgName(polyglot.locale(), res.result);
	                    Util.sessionStorage.addItem(Constant.SESSION.ORG, res.result, true);
	                    Util.sessionStorage.addItem(Constant.SESSION.ORG_NAME, orgName);
	                    Util.sessionStorage.addItem(Constant.SESSION.ORG_LOGO, res.result.logo);
	                    window.ORG_OWNER = res.result.owner;
	                    resolve(res);
	                },
	                error: function error(err) {
	                    reject(err);
	                }
	            };
	            Xhr.getOrgInfo(params);
	        });
	    },
	    getOrgSetting: function getOrgSetting(orgcode) {
	        var _this = this;
	        return new Promise(function (resolve, reject) {
	            var params = {
	                org_code: orgcode,
	                success: function success(res) {
	                    if (res.status !== Status.SUCCESS) {
	                        reject(polyglot.t('app.xhr.error'));
	                        // reject(res.message);
	                    }
	                    if (res.result.theme_settings) {
	                        var _theme = {};
	                        _.forEach(Colors(), function (color) {
	                            if (color.key.trim().toUpperCase() === res.result.theme_settings.theme.toUpperCase()) {
	                                _theme = color;
	                                return false;
	                            }
	                        });
	                        _theme.org_code = orgcode;
	                        Util.sessionStorage.addItem(Constant.SESSION.THEME, _theme, true);
	                    }
	                    if (res.result.application_settings) {
	                        Util.sessionStorage.addItem(Constant.SESSION.APPLICATION_SETTINGS, res.result.application_settings, true);
	                    }
	                    resolve(res);
	                },
	                error: function error(err) {
	                    reject(err);
	                }
	            };
	            Xhr.getOrgSetting(params);
	        });
	    },
	    assertValue: function assertValue(value, defaultValue) {
	        return typeof value !== 'number' ? defaultValue : value;
	    },
	    getConcats: function getConcats(options) {
	        var _this = this;
	        options = options || {};

	        var params = {
	            org_code: _this.org_code,
	            org_id: options.org_id || _this.org_id,
	            employee_skip: _this.assertValue(options.employee_skip, 0),
	            employee_limit: _this.assertValue(options.employee_limit, 100),
	            org_skip: _this.assertValue(options.org_skip, 0),
	            org_limit: _this.assertValue(options.org_limit, 100)
	        };
	        var xhrParams = Object.assign(params, {
	            success: function success(res) {
	                if (res.status !== Status.SUCCESS) {
	                    options.target && options.target.removeClass('ing');
	                    Modal.alert(res.message);
	                    return;
	                }
	                options.target && options.target.removeClass('ing').addClass('opening').data('children', 'has-gotten');
	                _this.renderEmployeeList(Object.assign(options, params, { list: res.result[0].employees || [] }));
	                _this.renderOrgList(Object.assign(options, params, { list: res.result[0].children || [] }));
	                options.callback && options.callback();
	            },
	            error: function error() {
	                options.target && options.target.removeClass('ing');
	                Modal.alert(polyglot.t('message.error'));
	            }
	        });
	        Xhr.getContacts(xhrParams);
	    },
	    renderEmployeeList: function renderEmployeeList(params) {
	        var _this3 = this;

	        if (params.isLoadMore && params.loadMoreType !== 'employee') return;

	        params.list.forEach(function (item) {
	            _this3.employeeMap[item.id] = item;
	            item.avatar_bk = Util.makeImagesLinkByMediaid(item.avatar, Constant.DEFAULT_USER_AVATAR);
	        });
	        var data = Object.assign({}, params, {
	            "default_avatar": Constant.DEFAULT_USER_AVATAR,
	            hasMore: !params.notNeedShowMore && params.list.length >= params.employee_limit
	        });
	        var _Tpl = Util.renderTpl(employeeTpl, data);
	        (params.dom || $('.contacts-page .employee-list')).append(_Tpl);
	    },
	    renderOrgList: function renderOrgList(params) {
	        if (params.isLoadMore && params.loadMoreType !== 'org') return;

	        var data = Object.assign({}, params, {
	            hasMore: params.list.length >= params.org_limit
	        });
	        var _Tpl = Util.renderTpl(orgTpl, data);
	        (params.dom || $('.contacts-page .org-list')).append(_Tpl);
	    },
	    goEmployeeEditPage: function goEmployeeEditPage(e) {
	        var target = $(this),
	            parent = target.parents('.employee-item'),
	            _employee = contacts.employeeMap[parent.data('id')];

	        Util.sessionStorage.addItem(Constant.SESSION.EMPLOYEE_DETAIL, _employee, true);
	        var goPageFn = function goPageFn() {
	            return Util.go('employee?edit=true');
	        };
	        if ($('.search-popup').css('display') === 'block') {
	            $('.search-popup').hide();
	            setTimeout(goPageFn, 200);
	            return;
	        }
	        goPageFn();
	    },
	    search: function search(e) {
	        var _this = this;
	        var query = e.target.value;
	        var dom = $('.search-content');
	        var listDom = dom.find('.search-list').html('');
	        dom.removeClass('noData').addClass('searching');

	        if (!query) {
	            dom.removeClass('searching');
	            return;
	        }
	        var params = {
	            data: {
	                "filter_senior": false,
	                "rank_view": true,
	                "query": query,
	                "view_acl": true,
	                "org_codes": [Util.sessionStorage.getItem(Constant.SESSION.ORG_CODE)]
	            },
	            success: function success(res) {
	                if (!res.result || !res.result.length) {
	                    dom.removeClass('searching').addClass('noData');
	                    return;
	                }
	                dom.removeClass('searching');
	                _this.renderEmployeeList({
	                    list: res.result,
	                    dom: listDom,
	                    notNeedShowMore: true
	                });
	            },
	            error: function error(err) {
	                Modal.alert(polyglot.t('message.error'));
	                dom.removeClass('searching').addClass('noData');
	            }
	        };
	        Xhr.searchContacts(params);
	    },
	    debounce: function debounce(fn) {
	        var _this = this;
	        var timer = null;
	        return function () {
	            for (var _len = arguments.length, args = Array(_len), _key = 0; _key < _len; _key++) {
	                args[_key] = arguments[_key];
	            }

	            timer && clearTimeout(timer);
	            timer = setTimeout(function () {
	                fn.apply(_this, args);
	                clearTimeout(timer);
	                timer = null;
	            }, 300);
	        };
	    },

	    setInfo: function setInfo() {
	        this.setOrgName();
	        this.setOrgLogo();
	    },
	    setOrgName: function setOrgName() {
	        var _orgInfo = Util.sessionStorage.getItem(Constant.SESSION.ORG, true);
	        var _orgName = getValueByLangType.getOrgName(polyglot.locale(), _orgInfo);
	        this.org_id = _orgInfo.id;
	        $('.contacts-page').find('.org-name').html(_orgName);
	    },
	    setOrgLogo: function setOrgLogo() {
	        var _orgLogo = Util.sessionStorage.getItem(Constant.SESSION.ORG_LOGO);
	        $('.contacts-page').find('.org-logo').html('<img src="' + Util.makeImagesLinkByMediaid(_orgLogo, Constant.DEFAULT_AVATAR) + '" onerror="this.error=null;this.src=\'' + Constant.DEFAULT_AVATAR + '\'" alt="logo" />');
	    },
	    refreshPage: function refreshPage() {
	        var pageLoader = $('.contacts-page__loader');
	        pageLoader.show();
	        $('.contacts-page .org-list').html('') && $('.contacts-page .employee-list').html('');
	        this.getConcats({ callback: function callback() {
	                return pageLoader.hide();
	            } });
	    },
	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.contacts-page',
	            selector: '.org-item a',
	            event: 'click',
	            handler: function handler(e) {
	                e.stopPropagation();
	                e.preventDefault();
	                var target = $(this);
	                var parent = target.parent('.org-item');
	                if (target.hasClass('ing')) return;
	                if (target.hasClass('opening')) {
	                    target.removeClass('opening');
	                    return;
	                }
	                if (target.data('children') === 'has-gotten') {
	                    target.addClass('opening');
	                    return;
	                }

	                target.addClass('ing');
	                _this.getConcats({
	                    org_id: parent.data('id'),
	                    dom: parent.find('.org-children'),
	                    target: target
	                });
	            }
	        }, {
	            element: '.contacts-page',
	            selector: '.load-more-list',
	            event: 'click',
	            handler: function handler(e) {
	                var target = $(this);
	                if (target.hasClass('ing')) return;

	                target.addClass('ing');
	                var org_id = target.data('orgid');
	                var skip = parseInt(target.data('skip')) || 0;
	                var limit = parseInt(target.data('limit')) || 100;
	                var loadMoreType = target.data('type');

	                var params = {
	                    org_id: org_id,
	                    employee_skip: 0,
	                    employee_limit: 0,
	                    org_skip: 0,
	                    org_limit: 0,
	                    target: target,
	                    dom: target.next(),
	                    isLoadMore: true,
	                    loadMoreType: loadMoreType,
	                    callback: function callback() {
	                        target.remove();
	                    }
	                };
	                if (loadMoreType === 'employee') {
	                    params.employee_skip = skip + limit;
	                    params.employee_limit = limit;
	                } else {
	                    params.org_skip = skip + limit;
	                    params.org_limit = limit;
	                }
	                _this.getConcats(params);
	            }
	        }, {
	            element: '.contacts-page',
	            selector: '.employee-edit__btn',
	            event: 'click',
	            handler: _this.goEmployeeEditPage
	        }, {
	            element: '.contacts-page',
	            selector: '.search-input',
	            event: 'focus',
	            handler: function handler() {
	                $('.contacts-page').addClass('searching');
	                $('.search-content').html();
	            }
	        }, {
	            element: '.contacts-page',
	            selector: '.search-input',
	            event: 'input',
	            handler: _this.debounce(_this.search)
	        }, {
	            element: '.contacts-page',
	            selector: '.search-cancel',
	            event: 'click',
	            handler: function handler() {
	                $('.search-input').val('');
	                $('.contacts-page').removeClass('searching');
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = contacts;

/***/ },
/* 75 */
2,
/* 76 */
/***/ function(module, exports) {

	module.exports = "<ul>\n    <% for (var i = 0, l = list.length; i < l; i++) {%>\n    <li class=\"org-item\" data-id=\"<%= list[i].id %>\" data-code=\"<%= list[i].org_code %>\">\n        <a href=\"javascript:;\">\n            <p class=\"org-item__name\"><%= list[i].name %></p>\n            <div class=\"org-item__action\"></div>\n            <div class=\"loader\"></div>\n        </a>\n        <ul class=\"org-children\"></ul>\n    </li>\n    <% } %>\n</ul>\n\n<% if (hasMore) {%>\n    <span\n        class=\"load-more load-more-list\"\n        href=\"javascript:;\"\n        data-type=\"org\"\n        data-orgid=\"<%= org_id %>\"\n        data-skip=\"<%= org_skip %>\"\n        data-limit=\"<%= org_limit %>\"\n    >\n        <span><%= polyglot.t('Contacts.button.more') %></span>\n        <span><%= polyglot.t('Contacts.content.loading') %></span>\n    </span>\n    <div class=\"more-list\"></div>\n<% } %>";

/***/ },
/* 77 */
/***/ function(module, exports) {

	module.exports = "<ul>\n    <% for (var i = 0, l = list.length; i < l; i++) {%>\n    <li class=\"employee-item\" data-id=\"<%= list[i].id %>\" data-code=\"<%= list[i].org_code %>\">\n        <a href=\"javascript:;\">\n            <div class=\"employee-avatar\">\n                <img src=\"<%= list[i].avatar_bk %>\" alt=\"\" onerror=\"this.error=null;this.src='<%= default_avatar %>'\" />\n            </div>\n            <p><%= list[i].name %></p>\n            <div class=\"employee-edit__btn\"></div>\n        </a>\n    </li>\n    <% } %>\n</ul>\n\n<% if (hasMore) {%>\n    <span\n        class=\"load-more load-more-list\"\n        data-type=\"employee\"\n        data-orgid=\"<%= org_id %>\"\n        data-skip=\"<%= employee_skip %>\"\n        data-limit=\"<%= employee_limit %>\"\n    >\n        <span><%= polyglot.t('Contacts.button.more') %></span>\n        <span><%= polyglot.t('Contacts.content.loading') %></span>\n    </span>\n    <div class=\"more-list\"></div>\n<% } %>";

/***/ },
/* 78 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Util = __webpack_require__(13);

	var actionModal = {
	    init: function init() {
	        this.bindEvents();
	    },
	    toggleOpen: function toggleOpen() {
	        var modal = $('.contacts-page .action-block');
	        if (modal.hasClass('open')) {
	            modal.removeClass('open').addClass('close');
	            return;
	        }
	        modal.removeClass('close').addClass('open');
	    },
	    goPage: function goPage(e) {
	        var target = $(this);
	        actionModal.toggleOpen();

	        var goPageFn = function goPageFn() {
	            return Util.go(target.data('page'));
	        };
	        if ($('.search-popup').css('display') === 'block') {
	            $('.search-popup').hide();
	            setTimeout(goPageFn, 200);
	            return;
	        }
	        goPageFn();
	    },
	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.contacts-page',
	            selector: '.action-item',
	            event: 'click',
	            handler: _this.goPage
	        }, {
	            element: '.contacts-page',
	            selector: '.action-mask',
	            event: 'click',
	            handler: _this.toggleOpen
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = actionModal;

/***/ },
/* 79 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

	__webpack_require__(80);

	var Util = __webpack_require__(13),
	    getValueByLangType = __webpack_require__(69),
	    Xhr = __webpack_require__(50),
	    WorkPlus = __webpack_require__(5),
	    Constant = __webpack_require__(6),
	    Status = __webpack_require__(53),
	    Contacts = __webpack_require__(74),
	    Tpl = __webpack_require__(81),
	    Popup = __webpack_require__(82),
	    Modal = __webpack_require__(15);

	var employee = {
	    init: function init(page) {
	        var _this = this;
	        this.isEdit = page && page.edit;
	        this.getInfo();
	        this.render();
	        this.bindEvents();
	    },
	    cordova: function cordova(page) {
	        var title = page && page.edit ? 'editTitle' : 'title';
	        WorkPlus.changeTitle([polyglot.t('Employee.header.' + title)]);
	        WorkPlus.emptyRightButton();
	    },
	    getInfo: function getInfo() {
	        this.rootOrgInfo = Util.sessionStorage.getItem(Constant.SESSION.ORG, true);
	        this.rootOrgName = getValueByLangType.getOrgName(polyglot.locale(), this.rootOrgInfo);
	        this.employee = Object.assign(_defineProperty({
	            user_id: '',
	            username: '',
	            name: '',
	            positions: [],
	            sort_order: 999,
	            mobile: '',
	            email: ''
	        }, 'mobile', ''), this.isEdit ? Util.sessionStorage.getItem(Constant.SESSION.EMPLOYEE_DETAIL, true) : {});
	    },
	    render: function render() {
	        var _Tpl = Util.renderTpl(Tpl, this.employee);
	        $('.employee-page').html(_Tpl);
	    },
	    selectOrg: function selectOrg(selected, _callback) {
	        var _this = this;
	        Popup.show({
	            page: '.employee-page',
	            selected: selected,
	            callback: function callback(org) {
	                org.org_id = org.id;
	                org.org_name = getValueByLangType.getOrgName(polyglot.locale(), org);
	                delete org.id;
	                _callback(org);
	            }
	        });
	    },
	    validInput: function validInput(key, value) {
	        switch (key) {
	            case 'mobile':
	                return !value || /^1\d{10}$/.test(value);
	            case 'email':
	                return !value || /(^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+)|(^$)/.test(value);
	            case 'sort_order':
	                if (typeof value !== 'number' && !value || !/^\d*$/.test(value)) return false;
	                return parseInt(value) !== 0;
	            default:
	                return !!value;
	        }
	    },
	    valid: function valid() {
	        var _this2 = this;

	        var needValidInputArr = ['username', 'name', 'sort_order', 'mobile', 'positions'];
	        var isInvalid = false;
	        needValidInputArr.forEach(function (key) {
	            var isInvalidInput = false;
	            if (key !== 'positions') {
	                isInvalidInput = !_this2.validInput(key, _this2.employee[key]);
	                isInvalidInput && $('.employee-page input[data-key=\'' + key + '\']').parents('a').addClass('hasWarning');
	                isInvalid = isInvalid || isInvalidInput;
	                return;
	            }
	            _this2.employee.positions.forEach(function (position) {
	                isInvalidInput = !_this2.validInput(key, position.job_title);
	            });
	            isInvalidInput = isInvalidInput || !_this2.employee.positions.length;
	            isInvalidInput && $('.employee-page .employee-positions').addClass('hasWarning');
	            isInvalid = isInvalid || isInvalidInput;
	        });
	        return isInvalid;
	    },
	    submit: function submit(e) {
	        var btn = $(e.target);
	        if (btn.hasClass('ing')) return;
	        if (this.valid()) return;

	        btn.addClass('ing');
	        var params = {
	            org_code: Util.sessionStorage.getItem(Constant.SESSION.ORG_CODE),
	            user_id: this.employee.id,
	            data: {
	                username: this.employee.username,
	                name: this.employee.name,
	                positions: this.employee.positions.map(function (pos) {
	                    return {
	                        org_id: pos.org_id,
	                        org_path: pos.path,
	                        job_title: pos.job_title,
	                        primary: pos.primary
	                    };
	                }),
	                sort_order: parseInt(this.employee.sort_order),
	                mobile: this.employee.mobile,
	                email: this.employee.email
	            },
	            success: function success(res) {
	                btn.removeClass('ing');
	                if (res.status !== Status.SUCCESS) {
	                    Modal.toast('error', res.message || polyglot.t('message.error'));
	                    return;
	                }
	                Modal.toast(polyglot.t('message.success'));
	                Contacts.refreshPage();
	                setTimeout(function () {
	                    return window.history.back();
	                }, 1200);
	            },
	            error: function error(err) {
	                Modal.toast('error', polyglot.t('message.error'));
	                btn.removeClass('ing');
	            }
	        };
	        var requestFn = params.user_id ? 'updateEmployee' : 'addEmployee';
	        Xhr[requestFn](params);
	    },
	    goPositionsEditPage: function goPositionsEditPage(e) {
	        Util.sessionStorage.addItem(Constant.SESSION.EMPLOYEE_POSITIONS_DETAIL, this.employee.positions, true);
	        Util.go('position');
	    },
	    refreshPositions: function refreshPositions(positions) {
	        this.employee.positions = positions;
	        var _Tpl = positions.map(function (position) {
	            return '<p>' + (position.org_name || '') + '-' + (position.job_title || '') + '</p>';
	        }).join('');
	        $('.employee-page .employee-positions__content').html(_Tpl);
	    },

	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.employee-page',
	            selector: '.employee-positions',
	            event: 'click',
	            handler: _this.goPositionsEditPage.bind(_this)
	        }, {
	            element: '.employee-page',
	            selector: 'input',
	            event: 'focus',
	            handler: function handler(e) {
	                var target = $(this),
	                    pageEl = $('.employee-page');
	                target.parents('a').removeClass('hasWarning');
	                setTimeout(function () {
	                    pageEl.scrollTop(target.offset().top + pageEl.scrollTop() - 80, 300);
	                }, 500);
	            }
	        }, {
	            element: '.employee-page',
	            selector: 'input',
	            event: 'blur',
	            handler: function handler(e) {
	                var target = $(this);
	                var key = target.data('key');
	                var value = _this.employee[key] = e.target.value;
	                var isValid = _this.validInput(key, value);
	                !isValid && target.parents('a').addClass('hasWarning');
	            }
	        }, {
	            element: '.employee-page',
	            selector: '.submit-btn',
	            event: 'click',
	            handler: function handler(e) {
	                _this.submit(e);
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = employee;

/***/ },
/* 80 */
2,
/* 81 */
/***/ function(module, exports) {

	module.exports = "<ul>\n    <% if (!user_id) { %>\n    <li>\n        <a href=\"javascript:;\">\n            <label><%= polyglot.t('Employee.list.account') %><span class=\"required\">*</span></label>\n            <input type=\"text\" placeholder=\"<%= polyglot.t('Employee.list.accountPlc') %>\" value=\"<%= username %>\" data-key=\"username\">\n        </a>\n        <p class=\"warning-text\"><%= polyglot.t('Employee.list.accountWarn') %></p>\n    </li>\n    <% } %>\n    <li>\n        <a href=\"javascript:;\">\n            <label><%= polyglot.t('Employee.list.name') %><span class=\"required\">*</span></label>\n            <input type=\"text\" placeholder=\"<%= polyglot.t('Employee.list.namePlc') %>\" value=\"<%= name %>\" data-key=\"name\">\n        </a>\n        <p class=\"warning-text\"><%= polyglot.t('Employee.list.nameWarn') %></p>\n    </li>\n    <li>\n        <a href=\"javascript:;\" class=\"employee-positions\">\n            <label><%= polyglot.t('Employee.list.position') %><span class=\"required\">*</span></label>\n            <div class=\"employee-positions__content\">\n            <% if (positions && positions.length) { %>\n                <% for (var i = 0, l = positions.length; i < l; i++) {%>\n                    <p><%= positions[i].org_name %>-<%= positions[i].job_title %></p>\n                <% } %>\n            <% } else { %>\n                <p class=\"tips\"><%= polyglot.t('Employee.list.positionPlc') %></p>\n            <% } %>\n            </div>\n            <i class=\"select-icon\"></i>\n        </a>\n        <p class=\"warning-text\"><%= polyglot.t('Employee.list.positionWarn') %></p>\n    </li>\n    <li>\n        <a href=\"javascript:;\">\n            <label><%= polyglot.t('Employee.list.order') %><span class=\"required\">*</span></label>\n            <input type=\"number\" placeholder=\"<%= polyglot.t('Employee.list.orderPlc') %>\" value=\"<%= sort_order %>\" data-key=\"sort_order\">\n        </a>\n        <p class=\"warning-text\"><%= polyglot.t('Employee.list.orderWarn') %></p>\n    </li>\n    <li>\n        <a href=\"javascript:;\">\n            <label><%= polyglot.t('Employee.list.mobile') %></label>\n            <input type=\"text\" placeholder=\"<%= polyglot.t('Employee.list.mobilePlc') %>\" value=\"<%= mobile %>\" data-key=\"mobile\">\n        </a>\n        <p class=\"warning-text\"><%= polyglot.t('Employee.list.mobileWarn') %></p>\n    </li>\n    <li>\n        <a href=\"javascript:;\">\n            <label><%= polyglot.t('Employee.list.email') %></label>\n            <input type=\"text\" placeholder=\"<%= polyglot.t('Employee.list.emailPlc') %>\" value=\"<%= email %>\" data-key=\"email\">\n        </a>\n        <p class=\"warning-text\"><%= polyglot.t('Employee.list.emailWarn') %></p>\n    </li>\n</ul>\n\n<footer>\n    <button class=\"submit-btn\">\n        <% if (user_id) { %>\n            <span><%= polyglot.t('Employee.button.save') %></span>\n        <% } else { %>\n            <span><%= polyglot.t('Employee.button.add') %></span>\n        <% } %>\n        <div class=\"loader\"></div>\n    </button>\n</footer>";

/***/ },
/* 82 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(83);

	var Util = __webpack_require__(13),
	    getValueByLangType = __webpack_require__(69),
	    Xhr = __webpack_require__(50),
	    WorkPlus = __webpack_require__(5),
	    Constant = __webpack_require__(6),
	    Status = __webpack_require__(53),
	    Tpl = __webpack_require__(84),
	    orgTpl = __webpack_require__(85),
	    Modal = __webpack_require__(15);

	var popup = {
	    show: function show() {
	        var options = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};

	        this.page = options.page || '.page';
	        this.selected = options.selected || '';
	        this.callback = options.callback;
	        this.orgMap = {};

	        this.getOrgInfo().then(this.render).then(this.bindEvents).then(this.getOrgList);
	    },
	    render: function render(orgInfo) {
	        var _this = popup;
	        var _Tpl = Util.renderTpl(Tpl, Object.assign({}, orgInfo, { selected: _this.selected }));
	        _this.popup = $(_Tpl);
	        $(_this.page).append(_this.popup.addClass('show'));
	    },

	    getOrgList: function getOrgList(options) {
	        options = options || {};
	        var _this = popup;
	        var params = {
	            org_code: Util.sessionStorage.getItem(Constant.SESSION.ORG_CODE),
	            org_id: options.org_id || _this.org_id,
	            org_skip: options.org_skip || 0,
	            org_limit: options.org_limit || 100
	        };
	        var xhrParams = Object.assign({}, params, {
	            success: function success(res) {
	                if (res.status !== Status.SUCCESS) {
	                    options.target && options.target.removeClass('ing');
	                    Modal.alert(res.message);
	                    return;
	                }
	                options.target && options.target.removeClass('ing').addClass('opening').data('children', 'has-gotten');
	                var list = res.result[0].children || [];
	                _this.renderOrgList(Object.assign({}, params, {
	                    list: list,
	                    dom: options.dom,
	                    hasMore: list.length >= params.org_limit
	                }));
	                options.callback && options.callback();
	            },
	            error: function error() {
	                options.target && options.target.removeClass('ing');
	                Modal.alert(polyglot.t('message.error'));
	            }
	        });
	        Xhr.getContacts(xhrParams);
	    },
	    getOrgChildrenList: function getOrgChildrenList(e) {
	        e && e.preventDefault();
	        e && e.stopPropagation();

	        var _this = popup;
	        var target = $(e.currentTarget).parents('a');
	        var parent = target.parent('.popup-org-item');
	        if (target.hasClass('ing')) return;
	        if (target.hasClass('opening')) {
	            target.removeClass('opening');
	            return;
	        }
	        if (target.data('children') === 'has-gotten') {
	            target.addClass('opening');
	            return;
	        }

	        target.addClass('ing');
	        var params = {
	            target: target,
	            org_id: parent.data('id'),
	            dom: parent.find('.popup-org-children')
	        };
	        _this.getOrgList(params);
	    },
	    getOrgMoreList: function getOrgMoreList(e) {
	        e && e.preventDefault();
	        e && e.stopPropagation();

	        var _this = popup;
	        var target = $(e.currentTarget);
	        if (target.hasClass('ing')) return;

	        target.addClass('ing');
	        var org_id = target.data('orgid');
	        var skip = parseInt(target.data('skip')) || 0;
	        var limit = parseInt(target.data('limit')) || 100;

	        var params = {
	            org_id: org_id,
	            org_skip: skip + limit,
	            org_limit: limit,
	            target: target,
	            dom: target.next(),
	            callback: function callback() {
	                target.remove();
	            }
	        };
	        _this.getOrgList(params);
	    },
	    renderOrgList: function renderOrgList(params) {
	        var _this2 = this;

	        params.list.forEach(function (org) {
	            return _this2.orgMap[org.id] = org;
	        });
	        var data = Object.assign({}, params, { selected: this.selected });
	        var _Tpl = Util.renderTpl(orgTpl, data);
	        (params.dom || $('.popup-block .popup-list')).append(_Tpl);
	        this.bindEventsForItem();
	    },
	    getOrgInfo: function getOrgInfo() {
	        var _this3 = this;

	        return new Promise(function (resolve) {
	            var _orgInfo = Util.sessionStorage.getItem(Constant.SESSION.ORG, true);
	            var _orgName = getValueByLangType.getOrgName(polyglot.locale(), _orgInfo);
	            _this3.orgMap[_orgInfo.id] = _orgInfo;
	            _this3.org_id = _orgInfo.id;
	            var _orgLogo = Util.sessionStorage.getItem(Constant.SESSION.ORG_LOGO);
	            resolve({
	                orgId: _this3.org_id,
	                orgName: _orgName,
	                orgLogo: Util.makeImagesLinkByMediaid(_orgLogo, Constant.DEFAULT_AVATAR),
	                orgDefaultLogo: Constant.DEFAULT_AVATAR
	            });
	        });
	    },
	    selectOrg: function selectOrg(e) {
	        e.preventDefault();
	        e.stopPropagation();
	        var target = $(e.currentTarget),
	            orgId = target.data('option') === 'root' ? popup.org_id : target.parents('.popup-org-item').data('id');

	        if (popup.selected === orgId) return;
	        popup.selected = orgId;

	        $('.popup-org-select').removeClass('selected');
	        target.addClass('selected');
	    },
	    submit: function submit() {
	        this.callback && this.callback(this.orgMap[this.selected] || '');
	        this.destroyed();
	    },
	    destroyed: function destroyed() {
	        var _this4 = this;

	        if (!this.popup) return;
	        this.popup.removeClass('show').addClass('hide');
	        setTimeout(function () {
	            return _this4.popup.remove();
	        }, 500);
	    },
	    bindEventsForItem: function bindEventsForItem() {
	        this.bindItemOpenChildrenEvent();
	        this.bindItemSelectedEvent();
	    },
	    bindItemSelectedEvent: function bindItemSelectedEvent() {
	        var target = $('.popup-org-select');
	        target.off('click', popup.selectOrg);
	        target.on('click', popup.selectOrg);
	    },
	    bindItemOpenChildrenEvent: function bindItemOpenChildrenEvent() {
	        var target = $('.popup-org-item__action');
	        target.off('click', popup.getOrgChildrenList);
	        target.on('click', popup.getOrgChildrenList);
	    },
	    bindEvents: function bindEvents() {
	        var _this = popup;
	        var events = [{
	            element: '.popup-block',
	            selector: '.popup-mask',
	            event: 'click',
	            handler: popup.destroyed.bind(popup)
	        }, {
	            element: '.popup-block',
	            selector: '.cancel',
	            event: 'click',
	            handler: popup.destroyed.bind(popup)
	        }, {
	            element: '.popup-block',
	            selector: '.submit',
	            event: 'click',
	            handler: popup.submit.bind(popup)
	        }, {
	            element: '.popup-block',
	            selector: '.load-more-list',
	            event: 'click',
	            handler: popup.getOrgMoreList.bind(popup)
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = popup;

/***/ },
/* 83 */
2,
/* 84 */
/***/ function(module, exports) {

	module.exports = "<div class=\"popup-block\">\n  <div class=\"popup-mask\"></div>\n  <div class=\"popup-header\">\n      <button class=\"cancel\">取消</button>\n      <span>选择部门</span>\n      <button class=\"submit\">确认</button>\n  </div>\n  <div class=\"popup-list\">\n    <div class=\"popup-org-info popup-org-select <% if (selected === orgId) { %>selected<% } %>\" data-option=\"root\">\n      <div class=\"logo\">\n        <img src=\"<%= orgLogo %>\" onerror=\"this.error=null;this.src='<%= orgDefaultLogo %>'\" alt=\"logo\" />\n      </div>\n      <p><%= orgName %></p>\n      <i class=\"selected-icon\"></i>\n    </div>\n  </div>\n</div>";

/***/ },
/* 85 */
/***/ function(module, exports) {

	module.exports = "<ul>\n    <% for (var i = 0, l = list.length; i < l; i++) {%>\n    <li class=\"popup-org-item\" data-id=\"<%= list[i].id %>\" data-code=\"<%= list[i].org_code %>\">\n        <a href=\"javascript:;\" class=\"popup-org-select <% if (selected === list[i].id) { %>selected<% } %>\">\n            <p class=\"popup-org-item__name\"><%= list[i].name %></p>\n            <i class=\"selected-icon\"></i>\n            <div class=\"popup-org-item__action\">\n                <i class=\"more\"></i>\n                <div class=\"loader\"></div>\n            </div>\n        </a>\n        <ul class=\"popup-org-children\"></ul>\n    </li>\n    <% } %>\n</ul>\n\n<% if (hasMore) {%>\n    <span\n        class=\"load-more load-more-list\"\n        href=\"javascript:;\"\n        data-orgid=\"<%= org_id %>\"\n        data-skip=\"<%= org_skip %>\"\n        data-limit=\"<%= org_limit %>\"\n    >\n        <span><%= polyglot.t('Contacts.button.more') %></span>\n        <span><%= polyglot.t('Contacts.content.loading') %></span>\n    </span>\n    <div class=\"more-list\"></div>\n<% } %>";

/***/ },
/* 86 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(87);

	var Util = __webpack_require__(13),
	    getValueByLangType = __webpack_require__(69),
	    Xhr = __webpack_require__(50),
	    WorkPlus = __webpack_require__(5),
	    Constant = __webpack_require__(6),
	    Status = __webpack_require__(53),
	    Employee = __webpack_require__(79),
	    Tpl = __webpack_require__(88),
	    Popup = __webpack_require__(82),
	    Modal = __webpack_require__(15);

	var position = {
	    init: function init(page) {
	        var _this = this;
	        this.getPositionInfo();
	        this.render();
	        this.bindEvents();
	    },
	    cordova: function cordova(page) {
	        WorkPlus.changeTitle([polyglot.t('Position.header.title')]);
	        WorkPlus.emptyRightButton();
	    },
	    getPositionInfo: function getPositionInfo() {
	        this.rootOrgInfo = Util.sessionStorage.getItem(Constant.SESSION.ORG, true);
	        this.rootOrgName = getValueByLangType.getOrgName(polyglot.locale(), this.rootOrgInfo);
	        var positions = Util.sessionStorage.getItem(Constant.SESSION.EMPLOYEE_POSITIONS_DETAIL, true);
	        this.positions = positions && positions.length ? positions : [{
	            id: new Date().getTime(),
	            org_id: this.rootOrgInfo.id,
	            org_name: this.rootOrgName,
	            job_title: '',
	            primary: true
	        }];
	    },
	    render: function render() {
	        var _Tpl = Util.renderTpl(Tpl, { positions: this.positions });
	        $('.position-content').html(_Tpl);
	    },
	    selectOrg: function selectOrg(selected, _callback) {
	        var _this = this;
	        Popup.show({
	            page: '.position-page',
	            selected: selected,
	            callback: function callback(org) {
	                org.org_id = org.id;
	                org.org_name = getValueByLangType.getOrgName(polyglot.locale(), org);
	                org.id = new Date().getTime() + '' + _this.positions.length;
	                _callback(org);
	            }
	        });
	    },
	    validInput: function validInput(value) {
	        return !!value;
	    },
	    valid: function valid() {
	        var _this2 = this;

	        var isInvalid = false;
	        this.positions.forEach(function (position, index) {
	            var isInvalidInput = !_this2.validInput(position.job_title);
	            isInvalidInput && $('.position-page input[data-key=\'job_title\'][data-index=\'' + index + '\']').parents('section').addClass('hasWarning');
	            isInvalid = isInvalid || isInvalidInput;
	        });
	        return isInvalid;
	    },
	    submit: function submit(e) {
	        var btn = $(e.target);
	        if (btn.hasClass('ing')) return;
	        if (this.valid()) return;

	        btn.addClass('ing');
	        Employee.refreshPositions(this.positions.map(function (pos) {
	            return {
	                id: pos.id,
	                org_id: pos.org_id,
	                org_name: pos.org_name,
	                org_path: pos.path,
	                job_title: pos.job_title,
	                primary: pos.primary
	            };
	        }));
	        btn.removeClass('ing');
	        window.history.back();
	    },

	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.position-page',
	            selector: '.position-add',
	            event: 'click',
	            handler: function handler(e) {
	                _this.selectOrg('', function (org) {
	                    _this.positions.push(Object.assign({}, org, { job_title: '' }));
	                    _this.render();
	                });
	            }
	        }, {
	            element: '.position-page',
	            selector: '.position-org',
	            event: 'click',
	            handler: function handler(e) {
	                var target = $(this);
	                var index = parseInt(target.data('index'));
	                _this.selectOrg(_this.positions[index].org_id, function (org) {
	                    _this.positions[index] = Object.assign({}, _this.positions[index], org);
	                    target.find('p').html(org.org_name);
	                });
	            }
	        }, {
	            element: '.position-page',
	            selector: '.position-delete__btn',
	            event: 'click',
	            handler: function handler(e) {
	                var id = $(this).parents('.position-delete').data('id');
	                _this.positions = _this.positions.filter(function (p) {
	                    return p.id !== '' + id;
	                });
	                _this.render();
	            }
	        }, {
	            element: '.position-page',
	            selector: '.position-radio__input',
	            event: 'change',
	            handler: function handler(e) {
	                var id = $(this).parents('.position-radio').data('id');
	                _this.positions.forEach(function (p) {
	                    return p.primary = p.id === '' + id;
	                });
	            }
	        }, {
	            element: '.position-page',
	            selector: '.section-input',
	            event: 'focus',
	            handler: function handler(e) {
	                var target = $(this),
	                    pageEl = $('.position-page');
	                target.parents('section').removeClass('hasWarning');
	                setTimeout(function () {
	                    pageEl.scrollTop(target.offset().top + pageEl.scrollTop() - 80, 300);
	                }, 500);
	            }
	        }, {
	            element: '.position-page',
	            selector: '.section-input',
	            event: 'blur',
	            handler: function handler(e) {
	                var target = $(this);
	                var key = target.data('key');
	                var index = target.data('index');
	                var value = e.target.value;
	                _this.positions[index][key] = value;
	                var isValid = _this.validInput(value);
	                !isValid && target.parents('section').addClass('hasWarning');
	            }
	        }, {
	            element: '.position-page',
	            selector: '.submit-btn',
	            event: 'click',
	            handler: function handler(e) {
	                _this.submit(e);
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = position;

/***/ },
/* 87 */
2,
/* 88 */
/***/ function(module, exports) {

	module.exports = "\n<% for (var i = 0, l = positions.length; i < l; i++) { %>\n    <section>\n        <div class=\"position-header\">\n            <label class=\"position-radio\" data-id=\"<%= positions[i].id %>\">\n                <input class=\"position-radio__input\" type=\"radio\" name=\"primary\" value=\"true\" <% if (positions[i].primary) {%>checked<%}%>>\n                <i></i>\n                <span><%= polyglot.t('Position.list.primary') %></span>\n            </label>\n            <% if (positions.length > 1) {%>\n            <div class=\"position-delete\" data-id=\"<%= positions[i].id %>\">\n                <i class=\"position-delete__btn\"></i>\n            </div>\n            <% } %>\n        </div>\n        <div class=\"position-org\" data-index=\"<%= i %>\">\n            <p><%= positions[i].org_name %></p>\n        </div>\n        <input\n            type=\"text\"\n            class=\"section-input\"\n            placeholder=\"<%= polyglot.t('Position.list.jobPlc') %>\"\n            value=\"<%= positions[i].job_title %>\"\n            data-key=\"job_title\"\n            data-index=\"<%= i %>\"\n        >\n        <p class=\"warning-text\"><%= polyglot.t('Position.list.jobWarn') %></p>\n    </section>\n<% } %>\n\n<% if (positions.length < 3) {%>\n<section class=\"position-add__block\">\n    <div class=\"position-add\">\n        <%= polyglot.t('Position.list.jobAdd') %>\n    </div>\n</section>\n<%}%>\n";

/***/ },
/* 89 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(90);

	var Util = __webpack_require__(13),
	    getValueByLangType = __webpack_require__(69),
	    Xhr = __webpack_require__(50),
	    WorkPlus = __webpack_require__(5),
	    Constant = __webpack_require__(6),
	    Status = __webpack_require__(53),
	    Contacts = __webpack_require__(74),
	    Tpl = __webpack_require__(91),
	    Popup = __webpack_require__(82),
	    Modal = __webpack_require__(15);

	var org = {
	    init: function init(page) {
	        var _this = this;
	        this.render();
	        this.bindEvents();
	    },
	    cordova: function cordova(page) {
	        WorkPlus.changeTitle([polyglot.t('Org.header.title')]);
	        WorkPlus.emptyRightButton();
	    },
	    render: function render() {
	        var _orgInfo = Util.sessionStorage.getItem(Constant.SESSION.ORG, true);
	        var _orgName = getValueByLangType.getOrgName(polyglot.locale(), _orgInfo);
	        this.org = {
	            parent: Object.assign({}, _orgInfo, { org_name: _orgName }),
	            name: '',
	            sort_order: 999,
	            type: 'OU'
	        };
	        var _Tpl = Util.renderTpl(Tpl, this.org);
	        $('.org-page').html(_Tpl);
	    },
	    selectOrg: function selectOrg(_callback) {
	        var _this = this;
	        Popup.show({
	            page: '.org-page',
	            selected: _this.org.parent.id,
	            callback: function callback(res) {
	                var org_name = getValueByLangType.getOrgName(polyglot.locale(), res);
	                _this.org.parent = Object.assign({}, res, { org_name: org_name }), _callback && _callback(org_name);
	            }
	        });
	    },
	    validInput: function validInput(key, value) {
	        switch (key) {
	            case 'sort_order':
	                if (typeof value !== 'number' && !value || !/^\d*$/.test(value)) return false;
	                return parseInt(value) !== 0;
	            case 'name':
	                return !!value && value.length >= 2 && value.length <= 100;
	            default:
	                return !!value;
	        }
	    },
	    valid: function valid() {
	        var _this2 = this;

	        var needValidInputArr = ['name', 'sort_order'];
	        var isInvalid = false;
	        needValidInputArr.forEach(function (key) {
	            var isInvalidInput = !_this2.validInput(key, _this2.org[key]);
	            isInvalidInput && $('.org-page input[data-key=\'' + key + '\']').parents('a').addClass('hasWarning');
	            isInvalid = isInvalid || isInvalidInput;
	        });
	        return isInvalid;
	    },
	    submit: function submit(e) {
	        var btn = $(e.target);
	        if (btn.hasClass('ing')) return;
	        if (this.valid()) return;

	        btn.addClass('ing');
	        var params = {
	            org_code: Util.sessionStorage.getItem(Constant.SESSION.ORG_CODE),
	            data: {
	                parent_path: this.org.parent.path,
	                parent_id: this.org.parent.id,
	                name: this.org.name,
	                sort_order: parseInt(this.org.sort_order),
	                type: this.org.type
	            },
	            success: function success(res) {
	                btn.removeClass('ing');
	                if (res.status !== Status.SUCCESS) {
	                    Modal.toast('error', res.message || polyglot.t('message.error'));
	                    return;
	                }
	                Modal.toast(polyglot.t('message.success'));
	                Contacts.refreshPage();
	                setTimeout(function () {
	                    return window.history.back();
	                }, 1200);
	            },
	            error: function error(err) {
	                Modal.toast('error', polyglot.t('message.error'));
	                btn.removeClass('ing');
	            }
	        };
	        Xhr.addOrg(params);
	    },

	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.org-page',
	            selector: '.select-org',
	            event: 'click',
	            handler: function handler(e) {
	                var target = this;
	                org.selectOrg(function (orgName) {
	                    $(target).find('p').html(orgName);
	                });
	            }
	        }, {
	            element: '.org-page',
	            selector: '.org-type',
	            event: 'change',
	            handler: function handler(e) {
	                _this.org.type = e.currentTarget.value;
	            }
	        }, {
	            element: '.org-page',
	            selector: 'input',
	            event: 'focus',
	            handler: function handler(e) {
	                var target = $(this),
	                    pageEl = $('.org-page');
	                target.parents('a').removeClass('hasWarning');
	                setTimeout(function () {
	                    pageEl.scrollTop(target.offset().top + pageEl.scrollTop() - 80, 300);
	                }, 500);
	            }
	        }, {
	            element: '.org-page',
	            selector: 'input',
	            event: 'blur',
	            handler: function handler(e) {
	                var target = $(this);
	                var key = target.data('key');
	                var value = _this.org[key] = e.target.value;
	                var isValid = _this.validInput(key, value);
	                !isValid && target.parents('a').addClass('hasWarning');
	            }
	        }, {
	            element: '.org-page',
	            selector: '.submit-btn',
	            event: 'click',
	            handler: _this.submit.bind(org)
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = org;

/***/ },
/* 90 */
2,
/* 91 */
/***/ function(module, exports) {

	module.exports = "<ul>\n    <li>\n        <a href=\"javascript:;\" class=\"select-org\">\n            <label><%= polyglot.t('Org.list.parent') %><span class=\"required\">*</span></label>\n            <p><%= parent.org_name %></p>\n            <i class=\"select-icon\"></i>\n        </a>\n    </li>\n    <li>\n        <a href=\"javascript:;\">\n            <label><%= polyglot.t('Org.list.orgName') %><span class=\"required\">*</span></label>\n            <input type=\"text\" placeholder=\"<%= polyglot.t('Org.list.orgPlc') %>\" data-key=\"name\">\n        </a>\n        <p class=\"warning-text\"><%= polyglot.t('Org.list.orgWarn') %></p>\n    </li>\n    <li>\n        <a href=\"javascript:;\">\n            <label><%= polyglot.t('Org.list.order') %><span class=\"required\">*</span></label>\n            <input type=\"number\" placeholder=\"<%= polyglot.t('Org.list.orderPlc') %>\" value=\"<%= sort_order %>\" data-key=\"sort_order\">\n        </a>\n        <p class=\"warning-text\"><%= polyglot.t('Org.list.orderWarn') %></p>\n    </li>\n    <li>\n        <a href=\"javascript:;\">\n            <label><%= polyglot.t('Org.list.category') %><span class=\"required\">*</span></label>\n            <select name=\"\" id=\"\" class=\"org-type\">\n                <option value=\"O\"><%= polyglot.t('Org.list.company') %></option>\n                <option value=\"OU\" selected><%= polyglot.t('Org.list.org') %></option>\n            </select>\n            <i class=\"select-icon\"></i>\n        </a>\n    </li>\n</ul>\n\n<footer>\n    <button class=\"submit-btn\">\n        <span><%= polyglot.t('Org.button.add') %></span>\n        <div class=\"loader\"></div>\n    </button>\n</footer>";

/***/ },
/* 92 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	__webpack_require__(93);

	var Util = __webpack_require__(13),
	    WorkPlus = __webpack_require__(5),
	    WorkPlusPromise = __webpack_require__(12),
	    getValueByLangType = __webpack_require__(69),
	    Constant = __webpack_require__(6),
	    Modal = __webpack_require__(15),
	    Tpl = __webpack_require__(94),
	    shareItemTpl = __webpack_require__(95),
	    Xhr = __webpack_require__(50);

	var workplusEscape = __webpack_require__(11);

	var qrcodeModule = {
	    init: function init(page) {
	        var _orgCode = page.orgcode ? page.orgcode : Util.sessionStorage.getItem(Constant.SESSION.ORG_CODE);
	        this.caniswitch = '' + page.caniswitch === 'true';
	        this.getOrgInfo(_orgCode);
	        this.bindEvents();
	    },
	    cordova: function cordova() {
	        WorkPlus.changeTitle([polyglot.t('Qrcode.header.title')]);
	        WorkPlus.emptyRightButton();
	    },
	    getOrgInfo: function getOrgInfo(orgCode) {
	        var _this = qrcodeModule;
	        var params = {
	            org_code: orgCode,
	            success: function success(res) {
	                if (res.status !== 0) {
	                    Util.showErrorMsgAndGetOut();
	                    return;
	                }
	                _this.render(res.result);
	            },
	            error: function error(err) {
	                Util.showErrorMsgAndGetOut();
	            }
	        };
	        Xhr.getOrgInfo(params);
	    },
	    switchOrg: function switchOrg() {
	        var _this = qrcodeModule;
	        WorkPlusPromise.popSwitchOrg().then(function (res) {
	            if (!res) {
	                Util.showErrorMsgAndGetOut();
	                return;
	            }
	            _this.render(res);
	        }).catch(function (err) {
	            Util.showErrorMsgAndGetOut();
	        });
	    },
	    render: function render(org) {
	        var _this = qrcodeModule;
	        org.caniswitch = _this.caniswitch;
	        _this.renderPage(org).then(_this.getAppInfo).then(_this.getQrcode).then(_this.getShareItems).catch(Util.showErrorMsgAndGetOut);
	    },
	    renderPage: function renderPage(org) {
	        return new Promise(function (resolve, reject) {
	            var _this = qrcodeModule;
	            _this.orgDetail = Object.assign(org, {
	                org_name: getValueByLangType.getOrgName(polyglot.locale(), org),
	                org_logo: Util.makeImagesLinkByMediaid(org.logo, Constant.DEFAULT_AVATAR),
	                default_org_logo: Constant.DEFAULT_AVATAR,
	                current_logo: org.logo
	            });

	            var _tpl = Util.renderTpl(Tpl, _this.orgDetail);
	            $('.qrcode-page').html(_tpl);
	            resolve();
	        });
	    },
	    getAppInfo: function getAppInfo() {
	        return new Promise(function (resolve, reject) {
	            var _this = qrcodeModule;
	            WorkPlusPromise.getAppInfo().then(function (res) {
	                _this.appName = res.app_name; //polyglot.t('Qrcode.content.desc2').replace('<app_name>', res.app_name);
	                _this.nickName = workplusEscape.unescapeString(Util.sessionStorage.getItem(Constant.SESSION.NICKNAME));
	                resolve();
	            }).catch(reject);
	        });
	    },
	    getQrcode: function getQrcode() {
	        var _this = qrcodeModule;
	        var params = {
	            org_code: _this.orgDetail.org_code,
	            name: encodeURI(_this.nickName),
	            app_name: encodeURI(_this.appName),
	            lng: polyglot.locale()
	        };
	        $('.organQrcode').html(Xhr.getQRCode(params));
	    },
	    getShareItems: function getShareItems() {
	        return new Promise(function (resolve, reject) {
	            WorkPlusPromise.getShareItems().then(function (res) {
	                if (!res) return reject();
	                var shareMap = {
	                    W6S_CONTACT: 'send', // 分享到workplus
	                    WX_CONTACT: 'wechat', // 分享到微信
	                    WX_CIRCLE: 'circle', // 分享到朋友圈
	                    QQ_CONTACT: 'QQ', // 分享到扣扣
	                    QQ_ZONE: 'space', // 分享到扣扣空间
	                    SYSTEM_WEBVIEW: 'browser', // 系统webview打开
	                    COPY_LINK: 'copy' // 复制连接
	                };
	                var list = (res.items || []).map(function (i) {
	                    return shareMap[i.type] || '';
	                });
	                var _listHtml = Util.renderTpl(shareItemTpl, { list: list });
	                $('.share-inner').append(_listHtml);
	                resolve();
	            }).catch(reject);
	        });
	    },
	    getShareParams: function getShareParams() {
	        return new Promise(function (resolve, reject) {
	            var _this = qrcodeModule;
	            if (_this.shareParams) return resolve();

	            Xhr.getShareLink({
	                org_code: _this.orgDetail.org_code,
	                name: encodeURI(_this.nickName),
	                app_name: encodeURI(_this.appName),
	                // appDownloadUrl: encodeURI(_this.appDownloadUrl),
	                lng: polyglot.locale(),
	                success: function success(res) {
	                    if (res.status !== 0) {
	                        Modal.showPopup(polyglot.t('Management.message.getLinkFailed'));
	                        return;
	                    }
	                    // 分享链接时带上当前的语言类型
	                    var _link = workplusEscape.unescapeString(res.result.content) + '&lang=' + polyglot.locale() + '?'; // 关于添加这个问号的原因 http://www.jianshu.com/p/3bdb72ecdf95
	                    var _title = polyglot.t('Management.message.inviteTitle').replace('<org_name>', _this.orgDetail.org_name);
	                    var _summary = polyglot.t('Management.message.inviteContent').replace('<name>', _this.nickName).replace('<app_name>', _this.appName).replace('<org_name>', _this.orgDetail.org_name);

	                    _this.shareParams = {
	                        url: _link,
	                        title: _title,
	                        org_code: _this.orgDetail.org_code,
	                        org_owner: _this.orgDetail.owner,
	                        org_avatar: _this.orgDetail.current_logo,
	                        org_name: _this.orgDetail.org_name, // 名字 移动端会进行 encodeURIComponent
	                        org_domainId: _this.orgDetail.domain_id,
	                        summary: _summary,
	                        cover_media_id: _this.orgDetail.current_logo || _this.orgDetail.default_org_logo,
	                        lang: polyglot.locale()
	                    };
	                    resolve();
	                },
	                error: function error(err) {
	                    console.log(err);
	                    Modal.showPopup(polyglot.t('Management.message.getLinkFailed'));
	                }
	            });
	        });
	    },
	    shareToColleague: function shareToColleague() {
	        var _this = qrcodeModule;

	        WorkPlusPromise.showShare(Object.assign(_this.shareParams, {
	            directly: 'w6s_contact'
	        })).catch(function (err) {
	            Modal.toast('error', err);
	        });
	    },
	    shareToWechat: function shareToWechat(type) {
	        var _this = qrcodeModule;

	        WorkPlusPromise.wxShare({
	            "type": "webpage",
	            "title": _this.shareParams.title,
	            "description": _this.shareParams.summary,
	            "thumb": _this.orgDetail.org_logo,
	            "scene": type === 'wechat' ? 0 : 1,
	            "data": {
	                "url": _this.shareParams.url
	            }
	        }).catch(function (err) {
	            Modal.toast('error', err);
	        });
	    },
	    shareToQQ: function shareToQQ(type) {
	        var _this = qrcodeModule;

	        WorkPlusPromise.qqShare({
	            "type": "webpage",
	            "title": _this.shareParams.title,
	            "description": _this.shareParams.summary,
	            "thumb_media_id": _this.shareParams.cover_media_id, // 也支持封面图的mediaId, 
	            "scene": type === 'QQ' ? 0 : 1,
	            "data": {
	                "url": _this.shareParams.url
	            }
	        }).catch(function (err) {
	            Modal.toast('error', err);
	        });
	    },
	    openWebView: function openWebView(type) {
	        var _this = qrcodeModule;

	        WorkPlusPromise.openWebView({
	            "url": _this.shareParams.url,
	            "title": _this.shareParams.title,
	            "use_system_webview": true
	        }).catch(function (err) {
	            Modal.toast('error', err);
	        });
	    },
	    copyText: function copyText() {
	        var _this = qrcodeModule;

	        WorkPlusPromise.copyText({
	            "text": _this.shareParams.url
	        }).then(function (res) {
	            Modal.toast(polyglot.t('Qrcode.message.copySuccessfully'));
	        }).catch(function (err) {
	            Modal.toast('error', err);
	        });
	    },
	    bindEvents: function bindEvents() {
	        var _this = this;
	        var events = [{
	            element: '.qrcode-page',
	            selector: '.org-change__button',
	            event: 'click',
	            handler: function handler(e) {
	                _this.switchOrg();
	            }
	        }, {
	            element: '.qrcode-page',
	            selector: '.share-item',
	            event: 'click',
	            handler: function handler(e) {
	                var target = $(this);
	                var type = target.data('type').trim();

	                _this.getShareParams().then(function () {
	                    switch (type) {
	                        case 'send':
	                            _this.shareToColleague();
	                            break;
	                        case 'wechat':
	                        case 'circle':
	                            _this.shareToWechat(type);
	                            break;
	                        case 'QQ':
	                        case 'space':
	                            _this.shareToQQ(type);
	                            break;
	                        case 'browser':
	                            _this.openWebView();
	                            break;
	                        case 'copy':
	                            _this.copyText();
	                            break;
	                    }
	                });
	            }
	        }];
	        Util.bindEvents(events);
	    }
	};

	module.exports = qrcodeModule;

/***/ },
/* 93 */
2,
/* 94 */
/***/ function(module, exports) {

	module.exports = "<% if(caniswitch) { %>\n<div class=\"sHead\">\n    <img class=\"sHeadIcon\" src=\"<%= org_logo %>\" onerror=\"this.error=null;this.src='<%= default_org_logo %>'\" alt=\"\" />\n    <span class=\"sHeadTitle\"><%= org_name %></span>\n    <span class=\"sHeadBtn org-change__button\"><%= polyglot.t('Qrcode.button.change') %></span>\n</div>\n<% } %>\n<div class=\"sBody\">\n    <div class=\"infoText\">即刻推荐给同事，开启数字化办公新体验</div>\n    <div class=\"organQrcode-block\">\n        <div class=\"organQrcode\"></div>\n        <div class=\"icon-zx\"></div>\n        <div class=\"icon-ys\"></div>\n        <div class=\"icon-yx\"></div>\n    </div>\n</div>\n<div class=\"share-block\">\n    <div class=\"tips\">其他邀请方式</div>\n    <ul class=\"share-inner\"></ul>\n</div>";

/***/ },
/* 95 */
/***/ function(module, exports) {

	module.exports = "\n<% for (var i = 0, l = list.length; i < l; i++) {%>\n    <li class=\"share-item\" data-type=\"<%= list[i] %>\">\n        <a href=\"javascript:;\">\n            <div class=\"share-item__icon\" data-type=\"<%= list[i] %>\"></div>\n            <p><%= polyglot.t('Qrcode.items.' + list[i]) %></p>\n        </a>\n    </li>\n<% } %>";

/***/ },
/* 96 */
2,
/* 97 */
2,
/* 98 */
/***/ function(module, exports) {

	module.exports = {
		"Management": {
			"header": {
				"title": "组织管理"
			},
			"list": {
				"contacts": "企业通讯录",
				"approval": "新成员申请",
				"share": "分享 - 邀请组织成员",
				"qrcode": "邀请同事加入组织",
				"settings": "成员加入设置",
				"administrators": "管理员设置",
				"login": "登录管理后台"
			},
			"message": {
				"member": "成员",
				"moreFunctions": "更多功能登录管理后台",
				"getLinkFailed": "获取分享链接失败！",
				"inviteTitle": "邀请你加入<org_name>",
				"inviteContent": "<name>在<app_name>上创建了一个组织-<org_name>，邀请大家加入。"
			},
			"actionSheet": {
				"photos": "从相册选择",
				"camera": "打开相机"
			}
		},
		"Setting": {
			"header": {
				"title": "成员加入设置"
			},
			"list": {
				"logo": "组织LOGO",
				"name": "组织名称",
				"skin": "组织皮肤",
				"managed": "电脑上管理"
			},
			"button": {
				"dismiss": "解散组织"
			},
			"actionSheet": {
				"photos": "从相册选择",
				"camera": "打开相机"
			},
			"tips": "设置用户申请加入组织是否需要组织管理员审批才能加入",
			"action": {
				"direct": "直接申请即可加入组织",
				"approve": "需要组织管理员审批才能加入组织"
			},
			"message": {
				"disbandedFailed": "解散组织失败，请重试！",
				"confirmDisband": "你确定要解散组织？该操作不可撤销",
				"updateSuccess": "更新LOGO成功",
				"updateFailed": "更新失败",
				"uploadFailed": "上传失败"
			}
		},
		"Skin": {
			"header": {
				"title": "皮肤设置",
				"submit": "提交"
			},
			"colors": {
				"skyblue": "天空清澈蓝",
				"businessblue": "商务蓝",
				"shakin": "耀沙金",
				"chinared": "中国红",
				"glacierblue": "冰川蓝",
				"blackjadegreen": "墨玉绿",
				"bluestars": "繁星蓝",
				"vibrantorange": "活力珠光橙"
			},
			"message": {
				"settingSuccess": "皮肤设置成功",
				"settingFailed": "设置失败，请重试!"
			}
		},
		"OrgName": {
			"header": {
				"title": "组织名称",
				"done": "完成"
			},
			"name": "组织中文名称",
			"enName": "组织英文名称",
			"twName": "组织繁体中文名称",
			"placeholder": {
				"name": "组织名称，50个字符，必填",
				"enName": "组织英文名称，100个字符",
				"twName": "组织繁体中文名称，50个字符"
			},
			"message": {
				"nameCantEmpty": "请输入组织名字！",
				"tooLonger": "组织名称长度不能超过50个字符(25个中文)",
				"modifySuccess": "修改组织名称成功",
				"enNameTooLonger": "组织英文名称长度不能超过100个字符",
				"twNameTooLonger": "组织繁体名称长度不能超过50个字符（25个中文）"
			}
		},
		"Computer": {
			"header": {
				"title": "登录管理后台"
			},
			"content": {
				"link": "在电脑上登录管理后台",
				"sign": "登录尽享",
				"option1": "批量导入通讯录更快捷",
				"option2": "海量办公应用更多选择",
				"option3": "更多的设置"
			},
			"button": {
				"scan": "扫描二维码登录后台"
			}
		},
		"Qrcode": {
			"header": {
				"title": "邀请同事加入组织"
			},
			"content": {
				"desc1": "扫一扫二维码",
				"desc2": "可以迅速在<app_name>加入我们",
				"title": "即刻推荐给同事，开启数字化办公新体验",
				"other": "其他邀请方式"
			},
			"button": {
				"change": "切换"
			},
			"items": {
				"send": "发送给",
				"wechat": "微信",
				"circle": "朋友圈",
				"QQ": "QQ",
				"space": "QQ空间",
				"browser": "浏览器打开",
				"copy": "复制链接"
			},
			"message": {
				"copySuccessfully": "复制成功！"
			}
		},
		"Approval": {
			"header": {
				"title": "新成员申请列表",
				"select": "全选",
				"unselect": "反选"
			},
			"button": {
				"more": "查看更多",
				"pass": "通过",
				"refuse": "拒绝"
			},
			"message": {
				"through": "是否通过?",
				"denied": "是否拒绝?",
				"noRecord": "没有需审核的人员",
				"error": "出错了，请重试！",
				"processed": "申请已被处理"
			},
			"content": {
				"joined": "通过",
				"denied": "拒绝",
				"loading": "努力加载中...",
				"application": "申请中",
				"ignore": "忽略",
				"noInfo": "暂无审核信息"
			}
		},
		"Administrator": {
			"header": {
				"title": "管理员设置",
				"add": "添加"
			},
			"button": {
				"more": "查看更多",
				"cancel": "取消"
			},
			"message": {
				"delete": "是否删除管理员?",
				"error": "出错了，请重试！",
				"added": "已成功添加",
				"removed": "已成功删除"
			},
			"content": {
				"loading": "努力加载中...",
				"removing": "处理中"
			}
		},
		"Contacts": {
			"header": {
				"title": "企业通讯录"
			},
			"button": {
				"addEmployee": "添加成员",
				"addOrganization": "添加部门",
				"search": "搜索",
				"cancel": "取消",
				"more": "查看更多"
			},
			"message": {},
			"content": {
				"loading": "努力加载中...",
				"noInfo": "暂无数据"
			}
		},
		"Position": {
			"header": {
				"title": "编辑职位"
			},
			"list": {
				"primary": "首选",
				"job": "职位",
				"jobPlc": "请输入职位信息",
				"jobWarn": "职位信息不能为空",
				"jobAdd": "添加职位信息"
			},
			"button": {
				"save": "保存"
			},
			"message": {},
			"content": {}
		},
		"Employee": {
			"header": {
				"title": "添加成员",
				"editTitle": "编辑成员"
			},
			"list": {
				"account": "账号",
				"accountPlc": "请输入账号",
				"accountWarn": "输入不能为空",
				"name": "姓名",
				"namePlc": "请输入姓名",
				"nameWarn": "输入不能为空",
				"position": "职位",
				"positionPlc": "请输入职位信息",
				"positionWarn": "职位信息不能为空",
				"order": "排序号",
				"orderPlc": "请填写大于0的正整数",
				"orderWarn": "请填写大于0的正整数",
				"mobile": "手机号码",
				"mobilePlc": "请输入手机号码",
				"mobileWarn": "手机号码格式不正确",
				"email": "电子邮箱",
				"emailPlc": "请输入电子邮箱",
				"emailWarn": "电子邮箱格式不正确"
			},
			"button": {
				"add": "添加",
				"save": "保存"
			},
			"message": {},
			"content": {}
		},
		"Org": {
			"header": {
				"title": "添加部门"
			},
			"list": {
				"parent": "上级部门",
				"orgName": "部门名称",
				"orgPlc": "请输入部门名称",
				"orgWarn": "请输入2-100个字符",
				"order": "排序号",
				"orderPlc": "请填写大于0的正整数",
				"orderWarn": "请填写大于0的正整数",
				"category": "类别",
				"company": "公司",
				"org": "部门"
			},
			"button": {
				"add": "添加"
			},
			"message": {},
			"content": {}
		},
		"message": {
			"success": "操作成功",
			"error": "出错了，请重试！"
		}
	};

/***/ },
/* 99 */
/***/ function(module, exports) {

	module.exports = {
		"Management": {
			"header": {
				"title": "Org Management"
			},
			"list": {
				"contacts": "Business address book",
				"approval": "Approval application",
				"share": "Invitations by share",
				"qrcode": "Invite colleagues to join",
				"settings": "Member joining settings",
				"administrators": "Administrator settings",
				"login": "Login management background"
			},
			"message": {
				"member": "member",
				"moreFunctions": "More functions login management background",
				"getLinkFailed": "Get sharing link failed!",
				"inviteTitle": "Invite you to join <org_name>",
				"inviteContent": "<name> in <app_name> to create an Org <org_name>, invite you to join"
			},
			"actionSheet": {
				"photos": "Choose from Photos",
				"camera": "Camera"
			}
		},
		"Setting": {
			"header": {
				"title": "Member joining settings"
			},
			"list": {
				"logo": "Org LOGO",
				"name": "Org Name",
				"skin": "Org Skin",
				"managed": "Managed on the computer"
			},
			"button": {
				"dismiss": "Dissolution Org"
			},
			"actionSheet": {
				"photos": "Choose from Photos",
				"camera": "Camera"
			},
			"tips": "Set whether the user's application for joining the organization needs to be approved by the organization administrator before joining",
			"action": {
				"direct": "Apply directly to join the organization",
				"approve": "Organization administrator approval is required to join the organization"
			},
			"message": {
				"disbandedFailed": "Disbanded Org failed, please try again!",
				"confirmDisband": "Are you sure to disband the Org? The operation is irrevocable",
				"updateSuccess": "Update LOGO successfully",
				"updateFailed": "Update failed",
				"uploadFailed": "Upload failed"
			}
		},
		"Skin": {
			"header": {
				"title": "Skin Settings",
				"submit": "Submit"
			},
			"colors": {
				"skyblue": "Clear blue sky",
				"businessblue": "Blue Business",
				"shakin": "Golden sand",
				"chinared": "Chinese Red",
				"glacierblue": "Glacier Blue",
				"blackjadegreen": "Black jade green",
				"bluestars": "Star blue",
				"vibrantorange": "Tangerine orange pearl"
			},
			"message": {
				"settingSuccess": "Skin setting is successful",
				"settingFailed": "Setting failed, please try again!"
			}
		},
		"OrgName": {
			"header": {
				"title": "Org Name",
				"done": "Done"
			},
			"placeholder": {
				"name": "Enter Org name",
				"enName": "OrgName(EN), 100 characters",
				"twName": "OrgName(ZH-HK), 50 characters(25 Chinese)"
			},
			"name": "OrgName(ZH-CN)",
			"enName": "OrgName(EN)",
			"twName": "OrgName(ZH-HK)",
			"message": {
				"nameCantEmpty": "Please enter the name of the Org!",
				"tooLonger": "The Org name can not be longer than 50 characters",
				"modifySuccess": "Modify the organization name successfully",
				"enNameTooLonger": "Length can not exceed 100 characters",
				"twNameTooLonger": "Length can not exceed 50 characters (25 Chinese)"
			}
		},
		"Computer": {
			"header": {
				"title": "Login management background"
			},
			"content": {
				"link": "Log in to the management background on the computer",
				"sign": "Sign in to own",
				"option1": "Batch import contacts more quickly",
				"option2": "Massive office application more choice",
				"option3": "More settings"
			},
			"button": {
				"scan": "Scan QR Code"
			}
		},
		"Qrcode": {
			"header": {
				"title": "Invite colleagues to join"
			},
			"content": {
				"desc1": "Scan QR code",
				"desc2": "to join us at <app_name>",
				"title": "Instantly recommend to colleagues, open a new experience of digital office",
				"other": "Other invitation methods"
			},
			"button": {
				"change": "change"
			},
			"items": {
				"send": "Send to",
				"wechat": "Wechat",
				"circle": "Circle of friends",
				"QQ": "QQ",
				"space": "QQ space",
				"browser": "Oopen in browser",
				"copy": "Copy"
			},
			"message": {
				"copySuccessfully": "Copy successfully！"
			}
		},
		"Approval": {
			"header": {
				"title": "New Request List",
				"select": "Select all",
				"unselect": "Unselected"
			},
			"button": {
				"more": "More",
				"pass": "Pass",
				"refuse": "Refuse"
			},
			"message": {
				"through": "Whether to pass?",
				"denied": "Whether to reject?",
				"noRecord": "There is no record to be audited",
				"error": "Error, please try again!",
				"processed": "The application has been processed"
			},
			"content": {
				"joined": "Joined",
				"denied": "Denied",
				"loading": "Loading...",
				"application": "Application",
				"ignore": "Ignore",
				"noInfo": "No review info"
			}
		},
		"Administrator": {
			"header": {
				"title": "Administrator settings",
				"add": "Add"
			},
			"button": {
				"more": "View more",
				"cancel": "Cancel"
			},
			"message": {
				"delete": "If remove administrator?",
				"error": "Error, please try again!",
				"added": "Added successfully",
				"removed": "Removed successfully"
			},
			"content": {
				"loading": "Loading...",
				"removing": "Removing"
			}
		},
		"Contacts": {
			"header": {
				"title": "Business address book"
			},
			"button": {
				"addEmployee": "Add member",
				"addOrganization": "Add Department",
				"search": "Search",
				"cancel": "Cancel",
				"more": "More"
			},
			"message": {},
			"content": {
				"loading": "Loading...",
				"noInfo": "No data"
			}
		},
		"Position": {
			"header": {
				"title": "Add position"
			},
			"list": {
				"primary": "primary",
				"job": "job title",
				"jobPlc": "please enter job title",
				"jobWarn": "job title cannot be empty",
				"jobAdd": "add position"
			},
			"button": {
				"save": "Save"
			},
			"message": {},
			"content": {}
		},
		"Employee": {
			"header": {
				"title": "Add member",
				"editTitle": "Edit member"
			},
			"list": {
				"account": "Account",
				"accountPlc": "Please enter account",
				"accountWarn": "Input cannot be empty",
				"name": "Name",
				"namePlc": "Please enter a name",
				"nameWarn": "Input cannot be empty",
				"position": "Position",
				"positionPlc": "Please enter position information",
				"positionWarn": "Position information cannot be empty",
				"order": "Sort number",
				"orderPlc": "Please fill in a positive integer greater than 0",
				"orderWarn": "Please fill in a positive integer greater than 0",
				"mobile": "Mobile number",
				"mobilePlc": "Please enter mobile number",
				"mobileWarn": "Incorrect format of mobile number",
				"email": "Email",
				"emailPlc": "Please enter email address",
				"emailWarn": "Incorrect email format"
			},
			"button": {
				"add": "Add",
				"save": "Save"
			},
			"message": {},
			"content": {}
		},
		"Org": {
			"header": {
				"title": "Add Department"
			},
			"list": {
				"parent": "Superior Organization",
				"orgName": "Organization name",
				"orgPlc": "Please enter organization name",
				"orgWarn": "Input should be 2-100 characters",
				"order": "Sort number",
				"orderPlc": "Please fill in a positive integer greater than 0",
				"orderWarn": "Please fill in a positive integer greater than 0",
				"category": "Category",
				"company": "Company",
				"department": "Department",
				"org": "Department"
			},
			"button": {
				"add": "Add",
				"save": "Save"
			},
			"message": {},
			"content": {}
		},
		"message": {
			"success": "Successful operation",
			"error": "Error, please try again!"
		}
	};

/***/ },
/* 100 */
/***/ function(module, exports) {

	module.exports = {
		"Management": {
			"header": {
				"title": "組織管理"
			},
			"list": {
				"contacts": "企業通訊錄",
				"approval": "申請成員審批",
				"share": "分享 - 邀請組織成員 ",
				"qrcode": "邀請同事加入組織",
				"settings": "成員加入設定",
				"administrators": "管理員設定",
				"login": "登入管理後臺"
			},
			"message": {
				"member": "成員",
				"moreFunctions": "更多功能登入管理後臺",
				"getLinkFailed": "獲取分享連接失敗！",
				"inviteTitle": "邀請你加入<org_name>",
				"inviteContent": "<name>在<app_name>上創建了一個組織-<org_name>，邀請大家加入。"
			},
			"actionSheet": {
				"photos": "從手機相簿選擇",
				"camera": "拍攝"
			}
		},
		"Setting": {
			"header": {
				"title": "成員加入設定"
			},
			"list": {
				"logo": "組織LOGO ",
				"name": "組織名稱 ",
				"skin": "組織皮膚 ",
				"managed": "電腦上管理 "
			},
			"button": {
				"dismiss": "解散組織"
			},
			"actionSheet": {
				"photos": "從手機相簿選擇",
				"camera": "拍攝"
			},
			"tips": "設定用戶申請加入組織是否需要組織管理員審批才能加入",
			"action": {
				"direct": "直接申請即可加入組織",
				"approve": "需要組織管理員審批才能加入組織"
			},
			"message": {
				"disbandedFailed": "解散組織失敗，請重試！",
				"confirmDisband": "你確定要解散組織？該操作不可撤銷",
				"updateSuccess": "更新LOGO成功",
				"updateFailed": "更新失敗",
				"uploadFailed": "上傳失敗"
			}
		},
		"Skin": {
			"header": {
				"title": "皮膚設定 ",
				"submit": "提交"
			},
			"colors": {
				"skyblue": "天空清澈藍",
				"businessblue": "商務藍",
				"shakin": "耀沙金",
				"chinared": "中國紅",
				"glacierblue": "冰川藍",
				"blackjadegreen": "墨玉綠",
				"bluestars": "繁星藍",
				"vibrantorange": "躍動珠光橙"
			},
			"message": {
				"settingSuccess": "皮膚設定成功",
				"settingFailed": "設定失敗，請重試！"
			}
		},
		"OrgName": {
			"header": {
				"title": "組織名稱",
				"done": "完成"
			},
			"placeholder": {
				"name": "點擊輸入組織名稱",
				"enName": "組織英文名稱，100個字符",
				"twName": "組織繁體中文名稱，50個字符（25個中文）"
			},
			"name": "組織中文名稱",
			"enName": "組織英文名稱",
			"twName": "組織繁體中文名稱",
			"message": {
				"nameCantEmpty": "請輸入組織名稱！",
				"tooLonger": "組織名稱長度不能超過50個字符（25個中文）",
				"modifySuccess": "修改組織名稱成功",
				"enNameTooLonger": "組織英文名稱長度不能超過100個字符",
				"twNameTooLonger": "組織繁體名稱長度不能超過50個字符（25個中文）"
			}
		},
		"Computer": {
			"header": {
				"title": "登入管理後臺"
			},
			"content": {
				"link": "在電腦上登入管理後臺",
				"sign": "登錄盡享",
				"option1": "批量導入通訊錄更快捷",
				"option2": "海量辦公應程式用更多選擇",
				"option3": "更多的設定"
			},
			"button": {
				"scan": "掃描 QR Code"
			}
		},
		"Qrcode": {
			"header": {
				"title": "邀請同事加入組織"
			},
			"content": {
				"desc1": "掃描上面的 QR Code",
				"desc2": "可以在<app_name>加入我們",
				"title": "即刻推薦給同事，開啟數位化辦公新體驗",
				"other": "其他邀請管道"
			},
			"button": {
				"change": "切换"
			},
			"items": {
				"send": "發送給",
				"wechat": "微信",
				"circle": "朋友圈",
				"QQ": "QQ",
				"space": "QQ空間",
				"browser": "瀏覽打開",
				"copy": "複製連結"
			},
			"message": {
				"copySuccessfully": "複製成功！"
			}
		},
		"Approval": {
			"header": {
				"title": "新成員申請清單",
				"select": "全選",
				"unselect": "取消全選"
			},
			"button": {
				"more": "查看更多",
				"pass": "通過",
				"refuse": "拒絕"
			},
			"message": {
				"through": "是否通過？",
				"denied": "是否拒絕？",
				"noRecord": "沒有需審核的人員",
				"error": "出錯了，請重試！",
				"processed": "申請已被處理"
			},
			"content": {
				"joined": "通過",
				"denied": "拒絕",
				"loading": "努力加載中...",
				"application": "申請中",
				"ignore": "忽略",
				"noInfo": "暫無審核信息"
			}
		},
		"Administrator": {
			"header": {
				"title": "管理員設定",
				"add": "添加"
			},
			"button": {
				"more": "查看更多",
				"cancel": "取消"
			},
			"message": {
				"delete": "是否删除管理員?",
				"error": "出錯了，請重試！",
				"added": "已成功添加",
				"removed": "已成功删除"
			},
			"content": {
				"loading": "努力加載中...",
				"removing": "處理中"
			}
		},
		"Contacts": {
			"header": {
				"title": "企業通訊錄"
			},
			"button": {
				"addEmployee": "添加成員",
				"addOrganization": "添加部門",
				"search": "蒐索",
				"cancel": "取消",
				"more": "查看更多"
			},
			"message": {},
			"content": {
				"loading": "努力加載中...",
				"noInfo": "暫無數據"
			}
		},
		"Position": {
			"header": {
				"title": "編輯職位"
			},
			"list": {
				"primary": "首選",
				"job": "職位",
				"jobPlc": "請輸入職位資訊",
				"jobWarn": "職位資訊不能為空",
				"jobAdd": "添加職位資訊"
			},
			"button": {
				"save": "保存"
			},
			"message": {},
			"content": {}
		},
		"Employee": {
			"header": {
				"title": "添加成員",
				"editTitle": "編輯成員"
			},
			"list": {
				"account": "帳號",
				"accountPlc": "請輸入帳號",
				"accountWarn": "輸入不能為空",
				"name": "姓名",
				"namePlc": "請輸入姓名",
				"nameWarn": "輸入不能為空",
				"position": "職位",
				"positionPlc": "請輸入職位資訊",
				"positionWarn": "職位資訊不能為空",
				"order": "排序號",
				"orderPlc": "請填寫大於0的正整數",
				"orderWarn": "請填寫大於0的正整數",
				"mobile": "手機號碼",
				"mobilePlc": "請輸入手機號碼",
				"mobileWarn": "手機號碼格式不正確",
				"email": "電子郵箱",
				"emailPlc": "請輸入電子郵箱",
				"emailWarn": "電子郵箱格式不正確"
			},
			"button": {
				"add": "添加",
				"save": "保存"
			},
			"message": {},
			"content": {}
		},
		"Org": {
			"header": {
				"title": "添加部門"
			},
			"list": {
				"parent": "上級部門",
				"orgName": "部門名稱",
				"orgPlc": "請輸入部門名稱",
				"orgWarn": "請輸入2-100個字符",
				"order": "排序號",
				"orderPlc": "請填寫大於0的正整數",
				"orderWarn": "請填寫大於0的正整數",
				"category": "類別",
				"company": "公司",
				"org": "部門"
			},
			"button": {
				"add": "添加",
				"save": "保存"
			},
			"message": {},
			"content": {}
		},
		"message": {
			"success": "操作成功",
			"error": "出錯了，請重試！"
		}
	};

/***/ },
/* 101 */
/***/ function(module, exports) {

	module.exports = "<div class=\"management-page\">\n    <article class=\"management-page__header\">\n        <div class=\"org\">\n            <div class=\"org-logo\"></div>\n            <div class=\"org-info\">\n                <div class=\"org-name-content\">\n                    <div class=\"org-name\"></div>\n                    <a class=\"org-name-btn\" href=\"#/edit\" title=\"\"></a>\n                </div>\n                <span class=\"org-number-content\"><%= polyglot.t('Management.message.member') %>：<span class=\"org-number\"></span></span>\n            </div>\n        </div>\n        <div class=\"tips\">\n            <p><%= polyglot.t('Management.message.moreFunctions') %>：<span class=\"admin-path\"></span></p>\n        </div>\n    </article>\n    <ul class=\"list-block middle\">\n        <li class=\"request_enabled\">\n            <a href=\"javascript:;\" data-route=\"contacts\" title=\"\">\n                <div class=\"list-content\">\n                    <p><%= polyglot.t('Management.list.contacts') %></p>\n                </div>\n                <div class=\"list-icon\"></div>\n            </a>\n        </li>\n        <li class=\"request_enabled\">\n            <a href=\"javascript:;\" data-route=\"set\" title=\"\">\n                <div class=\"list-content\">\n                    <p><%= polyglot.t('Management.list.settings') %></p>\n                </div>\n                <div class=\"list-icon\"></div>\n            </a>\n        </li>\n        <li class=\"request_enabled\">\n            <a href=\"javascript:;\" data-route=\"approval\" title=\"\">\n                <div class=\"list-content\">\n                    <p><%= polyglot.t('Management.list.approval') %></p>\n                </div>\n                <div class=\"list-icon\"></div>\n            </a>\n        </li>\n        <li class=\"qrcode request_enabled\">\n            <a href=\"javascript:;\" data-route=\"qrcode\" title=\"\">\n                <div class=\"list-content\">\n                    <p><%= polyglot.t('Management.list.qrcode') %></p>\n                </div>\n                <div class=\"list-icon\"></div>\n            </a>\n        </li>\n        <li class=\"request_enabled\">\n            <a href=\"javascript:;\" data-route=\"administrator\" title=\"\">\n                <div class=\"list-content\">\n                    <p><%= polyglot.t('Management.list.administrators') %></p>\n                </div>\n                <div class=\"list-icon\"></div>\n            </a>\n        </li>\n        <li class=\"\">\n            <a href=\"javascript:;\" data-route=\"computer\" title=\"\">\n                <div class=\"list-content\">\n                    <p><%= polyglot.t('Management.list.login') %></p>\n                </div>\n                <div class=\"list-icon\"></div>\n            </a>\n        </li>\n    </ul>\n</div>";

/***/ },
/* 102 */
/***/ function(module, exports) {

	module.exports = "<div class=\"contacts-page\">\n  <div class=\"action-block\">\n    <div class=\"action-mask\"></div>\n    <ul class=\"action-content\">\n      <li>\n        <a href=\"javascript:;\" class=\"action-item\" data-page=\"employee\">\n          <p><%= polyglot.t('Contacts.button.addEmployee') %></p>\n        </a>\n      </li>\n      <li>\n        <a href=\"javascript:;\" class=\"action-item\" data-page=\"org\">\n          <p><%= polyglot.t('Contacts.button.addOrganization') %></p>\n        </a>\n      </li>\n    </ul>\n  </div>\n\n  <div class=\"search-block\">\n    <input class=\"search-input\" type=\"text\" placeholder=\"<%= polyglot.t('Contacts.button.search') %>\">\n    <button class=\"search-cancel\"><%= polyglot.t('Contacts.button.cancel') %></button>\n  </div>\n  <div class=\"search-content\">\n    <div class=\"loading\"><%= polyglot.t('Contacts.content.loading') %></div>\n    <div class=\"no-search-list\"><%= polyglot.t('Contacts.content.noInfo') %></div>\n    <div class=\"search-list\"></div>\n  </div>\n\n  <div class=\"contacts-block\">\n    <div class=\"org-info\">\n      <div class=\"org-logo\"></div>\n      <p class=\"org-name\"></p>\n    </div>\n\n    <span class=\"load-more contacts-page__loader\"><%= polyglot.t('Contacts.content.loading') %></span>\n\n    <section class=\"org-list\"></section>\n    <section class=\"employee-list\"></section>\n  </div>\n</div>";

/***/ },
/* 103 */
/***/ function(module, exports) {

	module.exports = "<div class=\"set-page\">\n    <p class=\"tips\"><%= polyglot.t('Setting.tips') %></p>\n    <ul>\n        <li class=\"set-item\" data-name=\"direct\">\n            <p><%= polyglot.t('Setting.action.direct') %></p>\n            <div class=\"loader\"></div>\n            <div class=\"set-item__icon\"></div>\n        </li>\n        <li class=\"set-item\" data-name=\"approve\">\n            <p><%= polyglot.t('Setting.action.approve') %></p>\n            <div class=\"loader\"></div>\n            <div class=\"set-item__icon\"></div>\n        </li>\n    </ul>\n</div>";

/***/ },
/* 104 */
/***/ function(module, exports) {

	module.exports = "<div class=\"computer-page\">\n    <div class=\"computer-page-content\">\n        <div class=\"computer-bg\"></div>\n        <div class=\"management-link\">\n            <p><%= polyglot.t('Computer.content.link') %></p>\n            <p id=\"admin-path\">&nbsp;</p>\n        </div>\n        <div class=\"computer-var\">\n            <ul>\n                <li>\n                    <p>更方便管理企业通讯录</p>\n                </li>\n                <li>\n                    <p>海量办公应用更多选择</p>\n                </li>\n                <li>\n                    <p>更多个性化设置</p>\n                </li>\n            </ul>\n        </div>\n        <div class=\"open-qrc-scanner\">\n            <a href=\"javascript:;\"><%= polyglot.t('Computer.button.scan') %></a>\n        </div>\n    </div>\n</div>";

/***/ },
/* 105 */
/***/ function(module, exports) {

	module.exports = "<div class=\"approval-page\">\n    <div class=\"approval-container\">\n        <div class=\"loading\"><%= polyglot.t('Approval.content.loading') %></div>\n        <ul class=\"approval-list\"></ul>\n        <a class=\"load-more-approval\" href=\"javascript:;\"><%= polyglot.t('Approval.button.more') %></a>\n    </div>\n    <div class=\"approval-footer\">\n        <ul class=\"btn-group\">\n            <li class=\"through-all\">\n                <a href=\"javascript:;\" title=\"\">\n                    <%= polyglot.t('Approval.button.pass') %>\n                </a>\n            </li>\n            <li class=\"reject-all\">\n                <a href=\"javascript:;\" title=\"\">\n                    <%= polyglot.t('Approval.button.refuse') %>\n                </a>\n            </li>\n        </ul>\n    </div>\n    <p class=\"no-approval-list\"><%= polyglot.t('Approval.content.noInfo') %></p>\n</div>";

/***/ },
/* 106 */
/***/ function(module, exports) {

	module.exports = "<div class=\"administrator-page\">\n    <div class=\"administrator-container\">\n        <div class=\"loading\"><%= polyglot.t('Administrator.content.loading') %></div>\n        <ul class=\"administrator-list\"></ul>\n        <a class=\"load-more-administrator\" href=\"javascript:;\"><%= polyglot.t('Administrator.button.more') %></a>\n    </div>\n    <p class=\"no-administrator-list\"><%= polyglot.t('Administrator.content.noInfo') %></p>\n</div>";

/***/ },
/* 107 */
/***/ function(module, exports) {

	module.exports = "<div class=\"position-page\">\n  <div class=\"top-line\"></div>\n  <div class=\"position-content\"></div>\n  <footer>\n    <button class=\"submit-btn\">\n        <span><%= polyglot.t('Position.button.save') %></span>\n        <div class=\"loader\"></div>\n    </button>\n  </footer>\n</div>";

/***/ },
/* 108 */
/***/ function(module, exports) {

	module.exports = "<div class=\"employee-page\"></div>";

/***/ },
/* 109 */
/***/ function(module, exports) {

	module.exports = "<div class=\"org-page\"></div>";

/***/ },
/* 110 */
/***/ function(module, exports) {

	module.exports = "<div class=\"edit-org-name-page\">\n    <article>\n        <p><%= polyglot.t('OrgName.name') %></p>\n        <div>\n            <input type=\"text\" class=\"zh-cn\" id=\"org_name\" cols=\"30\" rows=\"3\" placeholder=\"<%= polyglot.t('OrgName.placeholder.name') %>\" required>\n            <div class=\"clear-btn\"></div>\n        </div>\n        <p><%= polyglot.t('OrgName.enName') %></p>\n        <div>\n            <input type=\"text\" class=\"en\" id=\"org_en_name\" cols=\"30\" rows=\"3\" placeholder=\"<%= polyglot.t('OrgName.placeholder.enName') %>\" required>\n            <div class=\"clear-btn\"></div>\n        </div>\n        <p><%= polyglot.t('OrgName.twName') %></p>\n        <div>\n            <input type=\"text\" class=\"zh-rtw\" id=\"org_tw_name\" cols=\"30\" rows=\"3\" placeholder=\"<%= polyglot.t('OrgName.placeholder.twName') %>\" required>\n            <div class=\"clear-btn\"></div>\n        </div>\n    </article>\n</div>";

/***/ },
/* 111 */
/***/ function(module, exports) {

	module.exports = "<div class=\"skin-page\">\n    <ul></ul>\n</div>";

/***/ },
/* 112 */
/***/ function(module, exports) {

	module.exports = "<div class=\"list-page\">\n    <ul class=\"list-block\">\n        <li class=\"go-approval\">\n            <a href=\"#/approval\" title=\"\">\n                <div class=\"list-avatar\">\n                    <i class=\"icon icon-add\"></i>\n                </div>\n                <div class=\"list-content\">\n                    <p>申请成员列表</p>\n                </div>\n            </a>\n        </li>\n    </ul>\n    <ul class=\"list-block\">\n        <li class=\"share\">\n            <a href=\"#/test\" title=\"\">\n                <div class=\"list-avatar\">\n                    <i class=\"icon icon-share\"></i>\n                </div>\n                <div class=\"list-content\">\n                    <p>分享－邀请组织成员</p>\n                </div>\n            </a>\n        </li>\n        <li class=\"go-qrcode\">\n            <a href=\"#/qrcode\" title=\"\">\n                <div class=\"list-avatar\">\n                    <i class=\"icon icon-qrcode\"></i>\n                </div>\n                <div class=\"list-content\">\n                    <p>二维码－邀请组织成员</p>\n                </div>\n            </a>\n        </li>\n    </ul>\n    <!--<ul class=\"list-block\">\n        <li>\n            <a href=\"javascript:;\" title=\"\">\n                <div class=\"list-avatar\">\n                    <i class=\"icon icon-set\"></i>\n                </div>\n                <div class=\"list-content\">\n                    <p>暂停邀请功能</p>\n                </div>\n                <div class=\"list-check\">\n                    <label class=\"label-switch\">\n                        <input type=\"checkbox\">\n                        <div class=\"checkbox\"></div>\n                    </label>\n                </div>\n            </a>\n        </li>\n    </ul>-->\n    <!--<ul class=\"list-block\">\n        <li>\n            <a href=\"http://172.16.1.97:8080/#/welcome\" title=\"\">\n                <div class=\"list-avatar\">\n                    <i class=\"icon icon-set\"></i>\n                </div>\n                <div class=\"list-content\">\n                    <p>组织搜索</p>\n                </div>\n            </a>\n        </li>\n    </ul>-->\n</div>";

/***/ },
/* 113 */
/***/ function(module, exports) {

	module.exports = "<div class=\"qrcode-page\"></div>";

/***/ }
/******/ ])));
/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2019 by Axway, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

let colorset;
let osVersion;
const shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
const fallbackColor = 'black'; // To match iphone/Classes/TiUIiOSProxy.m#fetchSemanticColor

// As Android passes a new instance of Ti.UI to every JS file we can't just
// Ti.UI within this file, we must call kroll.binding to get the Titanium
// namespace that is passed in with require and that deal with the .UI
// namespace that is on that directly.
let uiModule = Ti.UI;
if (Ti.Android) {
	uiModule = kroll.binding('Titanium').Titanium.UI;
}

uiModule.SEMANTIC_COLOR_TYPE_LIGHT = 'light';
uiModule.SEMANTIC_COLOR_TYPE_DARK = 'dark';

// We need to track this manually with a getter/setter
// due to the same reasons we use uiModule instead of Ti.UI
let currentColorType = uiModule.SEMANTIC_COLOR_TYPE_LIGHT;
Object.defineProperty(uiModule, 'semanticColorType', {
	get: () => {
		return currentColorType;
	},
	set: (colorType) => {
		currentColorType = colorType;
	}
});

const hexToRgb = function hexToRgb(hex) {
	let alpha = 1;
	let color = hex;
	if (hex.color) {
	  alpha = hex.alpha / 100; // convert from 0-100 range to 0-1 range
	  color = hex.color;
	}
	// Expand shorthand form (e.g. "03F") to full form (e.g. "0033FF")
	color = color.replace(shorthandRegex, (m, r, g, b) => r + r + g + g + b + b);

	const r = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(color);
	return r ? `rgba(${parseInt(r[1], 16)}, ${parseInt(r[2], 16)}, ${parseInt(r[3], 16)}, ${alpha.toFixed(3)})`: null;
};

uiModule.fetchSemanticColor = function fetchSemanticColor (colorName) {
	if (!osVersion) {
		osVersion = parseInt(Ti.Platform.version.split('.')[0]);
	}

	if (Ti.App.iOS && osVersion >= 13) {
		return Ti.UI.iOS.fetchSemanticColor(colorName);
	} else {
		if (!colorset) {
			try {
				colorset = require('/semantic.colors.json'); // eslint-disable-line import/no-absolute-path
			} catch (error) {
				console.error('Failed to require colors file at /semantic.colors.json');
				return;
			}
		}
		if (!colorset[colorName]) {
			return fallbackColor;
		}
		const color = colorset[colorName][Ti.UI.semanticColorType];
		if (typeof color === 'string') {
			return color;
		}
		if (!color || typeof color !== 'object') {
			return fallbackColor;
		}
		if (color['color'] && color['alpha']) {
			const result = hexToRgb(color);
			if (result) {
				return result;
			}
		}
		return fallbackColor;
	}
};

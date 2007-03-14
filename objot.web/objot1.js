/*
 * Objot 11a
 *
 * Copyright 2007 Qianyan Cai
 * Under the terms of The GNU General Public License version 2
 */
if (window.$ === undefined) {

////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\

/* return x, or '' if null/undefined */
$ = function (x) {
	return x == null ? '' : String(x);
}

/* return x, or cached {} if null/undefined */
$$ = function (x) {
	return x == null ? $$.o : x;
}
$$.o = {};

/* return x, or a short string followed by ... */
$S = function (x) {
	return x instanceof Array ? x.length + '[' + $S(String(x)) + '...]' : (x = String(x),
		(x.length > 40 ? x.substring(0, 40) + '...' : x).replace(/\r?\n/g, '\\n'));
}

$fox = navigator.userAgent.indexOf('Gecko') >= 0;
$ie7 = navigator.userAgent.indexOf('MSIE 7') >= 0;
$ie6 = !$fox && !$ie7;

$throw = function (x) {
	throw $fox ? $throw.err = Error(x instanceof Error ? x.message : x)
		: x instanceof Error ? x : Error(0, x);
}

////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\

/* make class with super class by prototype and interfaces by copying prototype */
$class = function (ctorName, sup, interfaces) {
	$.s(ctorName);
	var ctor = $.c(ctorName, 1);
	ctor.Name !== ctorName && (ctor.Name = ctorName);
	ctor.classed && $throw('duplicate class ' + ctor.Name);
	if (sup) {
		$.f(sup).classed || $throw('super class ' + sup.Name + ' not ready');
		var c = function () {};
		c.prototype = sup.prototype;
		ctor.prototype = new c();
		ctor.prototype.constructor = ctor;
	}
	if (ctor.prototype.constructor !== ctor)
		$throw(ctor.Name + ' inconsistent with ' + $S(ctor.prototype.constructor));
	for (var x = 2; x < arguments.length; x++)
		$.copy(ctor.prototype, arguments[x].prototype);
	$.cs[ctor.Name] = ctor;
	ctor.classed = true;
}
$class.get = function (clazz, forClass, gets) {
	if (arguments.length > 1)
		clazz.$get = [], clazz.$gets = [];
	for (var x = 1; x < arguments.length; ) {
		clazz.$get.push($.f(arguments[x++]));
		if ((gets = arguments[x++]) === null)
			clazz.$gets.push(null);
		else if (gets instanceof Array) {
			for (var y = 0; y < gets.length; y++)
				if (typeof gets[y] !== 'string')
					$throw($S(gets) + ' must not contain ' + $S(gets[y]));
			clazz.$gets.push(gets);
		}
		else
			$throw($S(gets) + ' must be array or null');
	}
}

////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\

/* get string from object graph, with class and reference */
$get = function (o, forClass, onlyTree) {
	var s = [o instanceof Array ? '[' : '/'];
	s.clazz = $.f(forClass);
	try {
		onlyTree || ($get.refX = 0, $get.ref(o));
		o instanceof Array ? $get.l(o, s, 1) : $get.o(o, s, 1);
	} catch(_) {
		try { onlyTree || $get.unref(o); } catch(_) {}
		throw _;
	}
	onlyTree || $get.unref(o);
	return s.join('\20');
}
$get.ref = function (o, ox) {
	if (o instanceof String || o instanceof Boolean || o instanceof Number)
		$throw($S(x) + ' must be not-String-Boolean-Number object');
	if (o[''] = '' in o) // whether and set multi references
		return;
	if (o instanceof Array)
		for (var x = 0; x < o.length; x ++) ox = o[x],
			typeof ox !== 'string' ? ox != null && typeof ox === 'object' && this.ref(ox)
				: ox.indexOf('\20') < 0 || $throw($S(ox) + ' must NOT contain \20 \\20');
	else for (var x in o)
		if (o.hasOwnProperty(x)) ox = o[x],
			typeof ox !== 'string' ? ox != null && typeof ox === 'object' && this.ref(ox)
				: ox.indexOf('\20') < 0 || $throw($S(ox) + ' must NOT contain \20 \\20');
}
$get.unref = function (o, ox) {
	if ('' in o && /*true*/delete o[''])
		for (var x in o)
			o.hasOwnProperty(x) && (ox = o[x]) !== null && typeof ox === 'object'
				&& this.unref(ox);
}
$get.l = function (o, s, x) {
	s[x++] = String(o.length);
	o[''] && (s[x++] = '=', s[x++] = o[''] = String(++this.refX));
	for (var i = 0, v, t; i < o.length; i++)
		if (v = o[i], (t = typeof v) !== 'function')
			s[x++] = v === null || v === undefined ? '.' : t === 'number' ? String(v)
				: v === false ? '<' : v === true ? '>' : t === 'string' ? (s[x++] = v, '')
				: typeof v[''] === 'string' ? (s[x++] = v[''], '+')
				: v instanceof Array ? (x = this.l(v, s, x), '[')
				: (x = this.o(v, s, x), '/');
	s[x++] = ';';
	return x;
}
$get.o = function (o, s, x) {
	var v, t = o.constructor.Name || 'Object', get;
	s[x++] = t === 'Object' ? '' : t;
	o[''] && (s[x++] = '=', s[x++] = o[''] = String(++this.refX));
	G: {
		if (get = o.constructor.$get) {
			for (var c = s.clazz, g = get.length - 1; g >= 0; g--)
				if (c === get[g] || c.prototype instanceof get[g]) {
					if (get = o.constructor.$gets[g]) {

	for (var p, n = 0; n < get.length; n++)
		if ((p = get[n], v = o[p], t = typeof v) !== 'function')
			s[x++] = p,
			s[x++] = v === null || v === undefined ? '.' : t === 'number' ? String(v)
				: v === false ? '<' : v === true ? '>' : t === 'string' ? (s[x++] = v, '')
				: typeof v[''] === 'string' ? (s[x++] = v[''], '+')
				: v instanceof Array ? (x = this.l(v, s, x), '[')
				: (x = this.o(v, s, x), '/');

						break G;
					}
					break;
				}
			break G;
		}
	for (var p in o)
		if (o.hasOwnProperty(p) && p.length && (v = o[p], t = typeof v) !== 'function')
			s[x++] = p,
			s[x++] = v === null || v === undefined ? '.' : t === 'number' ? String(v)
				: v === false ? '<' : v === true ? '>' : t === 'string' ? (s[x++] = v, '')
				: typeof v[''] === 'string' ? (s[x++] = v[''], '+')
				: v instanceof Array ? (x = this.l(v, s, x), '[')
				: (x = this.o(v, s, x), '/');
	}
	s[x++] = ';';
	return x;
}

/* set object graph from string, with class and reference */
$set = function (s) {
	try {
		s = $.s(s).split('\20'/* Ctrl-P in vim */);
		var x = s[0] === '[' ? $set.l(s, 1) : s[0] === '/' ? $set.o(s, 1) : -1;
		return x < s.length ? $throw('termination expected but ' + $S(s[x]))
			: $set.r.length = 0, s.o;
	} catch(_) {
		throw $set.r.length = 0, _;
	}
}
$set.l = function (s, x) {
	var o = new Array(s[x++] - 0);
	s[x] === '=' && (this.r[s[++x]] = o, x++);
	for (var i = 0, v; x >= s.length ? $throw('; expected but terminated')
		: (v = s[x++]) !== ';'; i++)
		switch(v) {
			case '': o[i] = s[x++]; break; case '.': o[i] = null; break;
			case '<': o[i] = false; break; case '>': o[i] = true; break;
			case '[': x = this.l(s, x); o[i] = s.o; break;
			case '/': x = this.o(s, x); o[i] = s.o; break;
			case '+': o[i] = this.r[s[x++]]; break; case 'NaN': o[i] = NaN; break;
			default: (o[i] = v - 0) != NaN || $throw('illegal number ' + $S(v));
		}
	s.o = o;
	return x;
}
$set.o = function (s, x, p, v) {
	var c = $.c(s[x++]), o = new c;
	s[x] === '=' && (this.r[s[++x]] = o, x++);
	while (x >= s.length ? $throw('; expected but terminated') : (p = s[x++]) !== ';')
		switch (v = s[x++]) {
			case '': o[p] = s[x++]; break; case '.': o[p] = null; break;
			case '<': o[p] = false; break; case '>': o[p] = true; break;
			case '[': x = this.l(s, x); o[p] = s.o; break;
			case '/': x = this.o(s, x); o[p] = s.o; break;
			case '+': o[p] = this.r[s[x++]]; break; case 'NaN': o[i] = NaN; break;
			default: (o[p] = v - 0) != NaN || $throw('illegal number ' + $S(v));
		}
	c === Error && (o.description = o.message);
	s.o = o;
	return x;
}
$set.r = [];

////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\

$http = function (url, timeout, data, onDone, This) {
	$fox && location.protocol === 'file:'
		&& url.charCodeAt(0) == 104 && url.indexOf('http://') == 0
		&& netscape.security.PrivilegeManager.enablePrivilege('UniversalBrowserRead');
	var h = $ie6 ? new ActiveXObject('Msxml2.XMLHTTP.3.0') : new XMLHttpRequest;
	h.open('POST', url, true);
	h.setRequestHeader('Content-Type', 'application/octet-stream');
	h.setRequestHeader('Cache-Control', 'no-cache');
	h.onreadystatechange = function () {
		if (h && h.readyState == 4)
			try {
				var s, t;
				try {
					s = h.status, s = s == 200 || s == 0 ? 0 : s;
					t = s == 0 ? h.responseText : h.statusText;
				} catch (_) { // stupid Firefox XMLHttpRequest bug
					s = 9999, t = 'Firefox bug';
				}
				onDone(s, t, This);
				onDone = null, abort();
			} catch(_) {
				onDone = null, abort();
				if (!$fox || !onerror)
					throw _;
				_ instanceof Error ? onerror(_.message, _.fileName, _.lineNumber)
					: onerror(_, 0, 0);
			}
	};
	var abort = function (time) {
		if (h) {
			try { h.onreadystatechange = null; h.abort(); } catch(_) {}
			h = null, clearTimeout(timeout);
			onDone && onDone(time > 0 ? 1 : -1, time > 0 ? 'timeout' : 'abort', This);
			onDone = This = null;
		}
	}
	timeout > 0 && setTimeout(function () {
		abort(1);
	}, timeout);
	h.send(data != null ? data : '');
	return url = data = null, abort;
}


////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\

$D = document;

$id = function (id) {
	return $D.getElementById(id);
}

/* create a dom element, and set properties */
$tag = function (tagName, x_, props_) {
	var g = $D.createElement(tagName);
	g.constructor ? g.constructor[''] || $.copy(g.constructor.prototype, $dom)
		: $.copy(g, $dom);
	var x = x_, props = props_;
	x >= 0 || (x = 1, props = arguments);
	for (var v, p; x < props.length; x++)
		if ((p = props[x]) === undefined)
			$throw('arguments[' + x + '] must not be undefined');
		else if (typeof p === 'string')
			if (typeof (v = props[++x]) === 'function')
				g.attach(p, v);
			else
				p === 'style' ? g.style.cssText = v : g[p] = v;
		else for (var pp in p)
			if (typeof (v = p[pp]) === 'function')
				g.attach(pp, v);
			else
				pp === 'style' ? g.style.cssText = v : g[pp] = v;
	return g;
}
eval(function (s1, f1, s2, f2) {
	for (var x in s1)
		window[x] = f1(s1[x]);
	for (var x in s2)
		window[x] = f2(s2[x]);
})(
{ $a:'a', $s:'span', $b:'br', $l:'label', $d:'div', $p:'p',
  $tab:'table', $tb:'tbody', $tr:'tr', $td:'td',
  $img:'img', $ul:'ul', $ol:'ol', $li:'li',
  $bn:'button', $inp:'input', $sel:'select', $opt:'option', $lns:'textarea' },
	function (g) {
		return function () {
			return $tag(g, 0, arguments);
		}
	},
{ $ln:'text', $chk:'check', $rad:'radio' },
	function (ty) {
		return function () {
			return $tag('input', 0, arguments).att('type', ty);
		}
	}
);
$tx = function (text) {
	return $D.createTextNode(text);
}

////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\

/* something added into dom element created by $tag */
$dom = {
	/* appendChild(s), or removeChild(s) if first argument is 0,
	 * or remove self from parent if no argument */
	ins: function (childOr0, child2) {
		if (arguments.length === 0)
			this.parentNode.removeChild(this);
		else if (childOr0 === 0)
			for (var x = 1; x < arguments.length; x++)
				this.removeChild(arguments[x]);
		else
			for (var x = 0; x < arguments.length; x++)
				this.appendChild(arguments[x]);
		return this;
	},

	/* getAttribute, setAttribute, removeAttribute */
	att: function (a, v) {
		if (arguments.length <= 1)
			return this.getAttribute(a);
		for (var x = 0; x < arguments.length; x++)
			a = arguments[x ++], v = arguments[x]
			v === null ? this.removeAttribute(a) : this.setAttribute(a, v);
		return this;
	},
	/* get/set textContent for Firefox, innerText for IE */
	tx: $fox ? function (v) {
		return v === undefined ? this.textContent : (this.textContent = v, this);
	} : function (v) {
		return v === undefined ? this.innerText : (this.innerText = v, this);
	},
	/* get/set style.display == 'none' */
	show: function (v) {
		var s = this.style.display !== 'none';
		if (v === undefined)
			return s;
		if (s && !v)
			this._disp = this.style.display, this.style.display = 'none';
		else if (v && !s)
			this.style.display = this._disp || '';
		return this;
	},

	/* attach event handler which 'this' will be this node.$ if available or this node
	 * if newHandler then handler is detached and newHandler is attached */
	attach: function (ontype, handler, newHandler) {
		if (newHandler)
			detach(ontype, handler), handler = newHandler;
		var x, t, s = this[''] || (this[''] = [1, 0, 9]); // [free, next, handler, ... ]
		if (x = s[t = ontype.substr(2)])
			do if (s[x + 1] === handler)
				return handler;
			while (s[x] && (x = s[x]))
// this causes window.onerror no effect for exceptions from handler
//		else if ($fox) // more events available than this[ontype] = $.event 
//			this.addEventListener(t, $.event, false);
		else // 'this' in $.event works, but it doesn't if attachEvent
			this[ontype] = $.event;
		s[x || t] = x = s[0], s[0] = s[x] || x + 2, s[x] = 0, s[x + 1] = handler;
		return this;
	},
	/* detach event handler */
	detach: function (ontype, handler) {
		var s = this[''];
		if (s)
			for (var x = ontype.substr(2), y; y = s[x]; x = y)
				if (s[y + 1] === handler)
					return s[x] = s[y], s[y] = s[0], s[0] = y, s[y + 1] = null, this;
		return this;
	},

	/* detach event handlers and $ for no IE memory leak, do nothing for Firefox */
	noleak: $ie6 ? function () {
		this[''] && (this[''] = null), this.$ && (this.$ = null);
		for (var x = this.firstChild; x !== null; x = x.nextSibling)
			this.noleak.call(x);
		return this;
	} : function () {
		return this;
	}
}
if ($fox)
	$dom[''] = false; // for constructor, be false for event attach


////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\

$.alert = window.alert;
/* alert multi lines */
window.alert = function (s) {
	for (var x = 1; x < arguments.length; x++)
		s += '\n' + arguments[x];
	return $fox ? $.alert.call(window, s) : $.alert(s), s;
}

$.throwStack = $fox ? function (file, line) {
	var s = $throw.err && $throw.err.stack || '';
	s = s.substr(s.indexOf('\n') + 1);
	return '-- ' + file + ':' + line + '\n' + s.substr(s.indexOf('\n') + 1);
} : function (file, line) {
	return file + ' : ' + line;
}

/* must be not-null object (including list, excluding function) */
$.o = function (x) {
	return x !== null && typeof x === 'object' ? x
		: $throw($S(x) + ' must be not-null object');
}
/* must be string */
$.s = function (x) {
	return typeof x === 'string' ? x : $throw($S(x) + ' must be string');
}
/* must be function */
$.f = function (x) {
	return typeof x === 'function' ? x : $throw($S(x) + ' must be function');
}

/* get function from class cache, or eval */
$.c = function ($_$, _$_) {
	if ($_$ in this.cs)
		return this.cs[$_$];
	_$_ || $throw($S($_$) + ' class not found');
	with(window) _$_ = eval($_$);
	return typeof _$_ === 'function' ? this.cs[$_$] = _$_
		: $throw($S($_$) + ' must be function');
}
/* class cache */
$.cs = { '': Object, Object: Object, Error:Error }

/* copy another's properties */
$.copy = function (to, from) {
	for (var x in from)
		to[x] = from[x];
	return to;
}
/* copy another's own properties */
$.copyOwn = function (to, from) {
	for (var x in from)
		from.hasOwnProperty(x) && (to[x] = from[x]);
	return to;
}

////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\

/* event dispatcher */
$.event = function (e, s, x, r, $) {
	if ((s = this['']) && (x = s[(e || (e = window.event)).type])) {
		$ = this.$ || this, r = 0;
		do r |= !s[x + 1].call($, e);
		while (x = s[x]);
		return !r;
	}
}

/* get/set style.cssFloat for Firefox, style.styleFloat for IE */
$.Float = $fox ? function (d, v) {
	return v === undefined ? d.style.cssFloat : (d.style.cssFloat = v, d);
} : function (v) {
	return v === undefined ? d.style.styleFloat : (d.style.styleFloat = v, d);
}
/* get/set style.opacity for Firefox, style.filter for IE */
$.opacity = $fox ? function (d, v) {
	return v === undefined ? d.style.opacity : (d.style.opacity = v < 1 ? v : '', d);
} : function (v) {
	var s = d.style, f = s.filter;
	if (v === undefined)
		return f ? f.match(/opacity=([^)]*)/)[1] /100 : 1;
	s.zoom = 1, s.filter = f.replace(/alpha\([^)]*\)/g,
		v >= 1 ? '' : 'alpha(opacity=' + v * 100 + ')');
	return d;
}


////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\

// hints
//
// In Firefox, predefined function(){}.name can only be assigned to no '.' name
//
// && || ! ? if(x), 1 '0' [] are true, 0 NaN '' null undefined are false
//   do NOT use x == true/false, sometimes String(x) sometimes not
//
// for IE 6(7?), event handler codes may need try { ... } finally {}
//   otherwise the finally { ... } inside the codes may be ignored.
//
// String(x) convert x to string (not String) unless x is already string
//
// function (a, b) { b = a; // then arguments[1] == arguments[0]
//
// while Firefox and IE alert() events and callbacks such as onclick
//   and XMLHttpRequest.onreadystatechange may still be fired in very little probability
//   maybe a big trouble ...
//
// Firefox XMLHttpRequest status maybe unavailable when readyState is 4 !
//
}

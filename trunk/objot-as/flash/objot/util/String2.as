//
// Copyright 2007-2008 Qianyan Cai
// Under the terms of the GNU Library General Public License version 2
//
package objot.util
{


public class String2
{
	public function String2()
	{
		throw Error('singleton');
	}

	public static function s(x:Object):String
	{
		if (x == null)
			return 'null';
		if (x is Array)
			return x.length + '[' + s(String(x)) + '...]';
		x = "'" + String(x) + "'";
		return (x.length > 40 ? x.substring(0, 40) + '...' : x).replace(/\r?\n/g, '\\n');
	}
}
}

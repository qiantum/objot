import java.util.ArrayList;
import java.util.HashMap;

import objot.Getting;
import objot.Objot;
import objot.Setting;


public class Demo
{
	public static final void main(String[] args) throws Exception
	{
		A x = new A();
		x.a2 = "x";
		B y = new B();
		y.a1 = "\tasdasdf";
		y.a2 = true;
		y.a3 = 34e-5f;
		y.a4 = null;
		y.a5 = "\n\\20";
		y.a6 = new ArrayList<Object[]>();
		y.a6.add(new Object[] { y, y.a1, y.a2, y.a3, y.a4, y.a5, null, null });
		y.a6.get(0)[6] = y.a6;
		y.a6.get(0)[7] = y.a6.get(0);
		y.a7 = new HashMap<String, A>();
		y.a7.put("x", x);
		y.a7.put("xx", (A)y.a6.get(0)[0]);
		y.a8 = (Boolean)y.a2;
		Object[] z = new Object[] { y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y,
			y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y, y };
		z[0] = z;
		Objot objot = new Objot();
		byte[] bs = Getting.go(objot, Object.class, z);
		Object o = Setting.go(objot, Object.class, bs);
		byte[] bs2 = Getting.go(objot, Object.class, o);
		if (bs.length != bs2.length)
			throw new Exception("length error: " + bs.length + " " + bs2.length);
		for (int i = 0; i < bs.length; i++)
			if (bs[i] != bs2[i])
				throw new Exception("data error at " + i + ": " + bs[i] + " " + bs2[i]);
		System.out.println("ok");
	}
}

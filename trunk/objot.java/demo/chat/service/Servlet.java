//
// Copyright 2007 Qianyan Cai
// Under the terms of The GNU General Public License version 2
//
package chat.service;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import objot.Err;
import objot.ErrThrow;
import objot.Errs;
import objot.Objot;
import objot.servlet.ObjotServlet;
import objot.servlet.Servicing;

import org.hibernate.validator.InvalidStateException;

import chat.model.ErrUnsigned;
import chat.model.Model;


public final class Servlet
	extends ObjotServlet
{
	int verbose = 1;

	@Override
	public void init() throws Exception
	{
		String verb = config.getInitParameter("verbose");
		verbose = verb != null ? Integer.parseInt(verb) : verbose;
		Locale.setDefault(Locale.ENGLISH);

		objot = new Objot()
		{
			@Override
			protected Class<?> classByName(String name) throws Exception
			{
				return Class.forName("chat.model.".concat(name));
			}

			/** include {@link Err} and {@link Errs} */
			@Override
			protected String className(Class<?> c) throws Exception
			{
				return c.getName().substring(c.getName().lastIndexOf('.') + 1);
			}
		};

		synchronized (Do.class)
		{
			if (Do.sessionFactory == null)
				Do.sessionFactory = Model.init().buildSessionFactory();
		}
	}

	@Override
	protected Servicing serviceConfig(String name, HttpServletRequest req,
		HttpServletResponse res) throws Exception
	{
		return new S().init(objot, name);
	}

	class S
		extends Servicing
	{
		String nameVerbose;
		boolean sign;
		int tran;
		boolean tranRead;

		@Override
		public S init(String claName, String methName) throws Exception
		{
			super.init("chat.service.".concat(claName), methName);
			if (Servlet.this.verbose > 0)
				nameVerbose = "\n-------------------- " + name + " --------------------";
			Signed s = meth.getAnnotation(Signed.class);
			sign = s == null || s.need();
			Transac t = meth.getAnnotation(Transac.class);
			tran = t == null ? Transac.DEFAULT : t.level();
			tranRead = t != null && t.readOnly();
			return this;
		}

		@Override
		public CharSequence go(char[] Q, HttpServletRequest req, HttpServletResponse res)
			throws ErrThrow, Exception
		{
			if (Servlet.this.verbose > 0)
				System.out.println(nameVerbose);
			Do $ = new Do();

			Integer me = (Integer)req.getSession().getAttribute("signed");
			$.me = me;
			if (sign && me == null)
				throw Do.err(new ErrUnsigned("not signed in"));

			try
			{
				if (tran > 0)
				{
					$.$ = Do.sessionFactory.openSession();
					$.$.connection().setReadOnly(tranRead);
					$.$.connection().setTransactionIsolation(tran);
					$.$.beginTransaction();
				}
				boolean ok = false;
				try
				{
					CharSequence S;
					if (Q == null)
						S = go(null, req, res, $);
					else
						S = go(null, req, res, objot.set(Q, reqClas[0], cla), $);
					ok = true;
					return S;
				}
				catch (InvalidStateException e)
				{
					throw Do.err(new Errs(e.getInvalidValues()));
				}
				finally
				{
					if (tran > 0)
						try
						{
							if (ok && ! tranRead)
								$.$.getTransaction().commit();
							else
								$.$.getTransaction().rollback();
						}
						catch (Throwable e)
						{
							Servlet.this.log(e);
						}
				}
			}
			finally
			{
				if (me != null && $.me == null)
					req.getSession().invalidate();
				else if ($.me != me)
					req.getSession().setAttribute("signed", $.me);

				if ($.$ != null && $.$.isOpen())
					try
					{
						$.$.close();
					}
					catch (Throwable e)
					{
						Servlet.this.log(e);
					}
			}
		}
	}
}

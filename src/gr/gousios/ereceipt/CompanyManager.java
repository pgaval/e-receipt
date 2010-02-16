/* Copyright (C) 2010 Georgios Gousios <gousiosg@gmail.com>

e-receipts is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License version 2,
a copy of which can be found in the LICENCE.txt file accompanying
the e-receipts software distribution.

e-receipts is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

*/

package gr.gousios.ereceipt;

import gr.gousios.ereceipt.model.Company;
import gr.gousios.ereceipt.model.Error;
import gr.gousios.ereceipt.model.User;

import java.io.IOException;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class CompanyManager extends HttpServlet {

	private static final Logger log = Logger.getLogger("compmgr");
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {

		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		
		EntityManager em = EMF.get().createEntityManager();
		String key = req.getParameter("key");
		String format = req.getParameter("format");
		
		if (!isAuth(key, resp, em, format)) {
			em.close();
			return;
		}
		
		String url = req.getRequestURL().toString();
		
		if (url.split("company/").length < 2) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			if (format != null && format.equals("xml"))
				resp.getWriter().print(Error.noCompany("").toXML(em));
			else
				resp.getWriter().print(Error.noCompany("").toJSON(em));
			em.close();
			return;
		}
		
		String rest = url.split("company/")[1];
		String[] fields = rest.split("/");
		String afm = fields[0];
		String name = null;

		//Parse parameters
		if (fields.length > 1) {
			name = fields[1];
			
			if (!name.equals("names")) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				if (format != null && format.equals("xml"))
					resp.getWriter().print(Error.unknownCall(name).toXML(em));
				else
					resp.getWriter().print(Error.unknownCall(name).toJSON(em));
				em.close();
				return;
			}
		}
		
		Company c = Company.fromAFM(em, afm);
		
		if (c == null) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			if (format != null && format.equals("xml"))
				resp.getWriter().print(Error.noCompany(afm).toXML(em));
			else
				resp.getWriter().print(Error.noCompany(afm).toJSON(em));
			em.close();
			return;
		}
		
		//Process call: /company/<afm>
		resp.setStatus(HttpServletResponse.SC_OK);
		if (format != null && format.equals("xml"))
			resp.getWriter().print(c.toXML(em));
		else
			resp.getWriter().print(c.toJSON(em));
		em.close();
		return;
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		EntityManager em = EMF.get().createEntityManager();
		String key = req.getParameter("key");
		String format = req.getParameter("format");
		
		if (!isAuth(key, resp, em, format)) 
			return;
		
		String url = req.getRequestURL().toString();
		
		if (url.split("company/").length > 1) {

			String rest = url.split("company/")[1];
			String[] fields = rest.split("/");
			String afm = fields[0];
			String names = null;

			String newName = req.getParameter("name");

			if (newName == null || newName.equals(null)) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				if (format != null && format.equals("xml"))
					resp.getWriter().print(Error.missParam("name").toXML(em));
				else
					resp.getWriter().print(Error.missParam("name").toJSON(em));
				em.close();
				return;
			}
		}
				
		//Process calls to: /company/
		String afm = req.getParameter("afm");
		String name = req.getParameter("name");
		
		if (afm == null || afm.equals("")) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			if (format != null && format.equals("xml"))
				resp.getWriter().print(Error.missParam("afm").toXML(em));
			else
				resp.getWriter().print(Error.missParam("afm").toJSON(em));
			em.close();
			return;
		}
		
		if (!checkAFM(afm)) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			if (format != null && format.equals("xml"))
				resp.getWriter().print(Error.wrongAFM(afm).toXML(em));
			else
				resp.getWriter().print(Error.wrongAFM(afm).toJSON(em));
			em.close();
			return;
		}
		
		Company c = Company.fromAFM(em, afm);
		
		if (c != null) {
			resp.setStatus(HttpServletResponse.SC_CONFLICT);
			if (format != null && format.equals("xml"))
				resp.getWriter().print(Error.duplComp(afm).toXML(em));
			else
				resp.getWriter().print(Error.duplComp(afm).toJSON(em));
			em.close();
			return;
		}
		
		c = new Company();
		c.setAfm(afm);
		c.setName(name);
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			em.persist(c);
			tx.commit();
		} catch (Throwable t) {
			log.severe("Error adding company " + afm + 
					" Reason:" + t.getMessage());
			tx.rollback();
		}

		log.info("Company " + c.getAfm() + " was created successfully");
		resp.setStatus(HttpServletResponse.SC_CREATED);
		if (format != null && format.equals("xml"))
			resp.getWriter().print(c.toXML(em));
		else
			resp.getWriter().print(c.toJSON(em));
		em.close();
	}
	
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("utf-8");
		
		String key = req.getParameter("key");
		String format = req.getParameter("format");
		
		EntityManager em = EMF.get().createEntityManager();

		if (!isAuth(key, resp, em, format)) 
			return;
		
		resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		log.severe("Company.DELETE from client:" + req.getRemoteHost());
	}
	
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("utf-8");
		String key = req.getParameter("key");
		String format = req.getParameter("format");
		
		EntityManager em = EMF.get().createEntityManager();
		
		if (!isAuth(key, resp, em, format)) 
			return;
		
		resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		log.severe("Company.PUT from client:" + req.getRemoteHost());
	} 
	
	private boolean isAuth(String key, HttpServletResponse resp, EntityManager em, String format) throws IOException {
		if (key == null || User.fromApiKey(em, key) == null) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			if (format != null && format.equals("xml"))
				resp.getWriter().print(Error.notAuthorised(key).toXML(em));
			else
				resp.getWriter().print(Error.notAuthorised(key).toJSON(em));
			return false;
		}
		
		return true;
	}
	
	private boolean checkAFM(String afm) {

		if (afm.equals("") || afm.length() != 9)
			return false;
		
		long iSum = 0;
		
		try {
			Double.parseDouble(afm);
		} catch (NumberFormatException e) {
			return false;
		}
		
		for (int i = 0; i < afm.length() - 1; i++) {
			iSum = iSum + Integer.parseInt(afm.substring(i, i + 1))
					* (int) Math.pow(2, (afm.length() - i - 1));
		}

		if (iSum == 0)
			return false;

		long btRem = iSum % 11;
		int lastDigit = Integer.parseInt(afm.substring(afm.length() - 1, 
				afm.length()));
		
		if (lastDigit == btRem || (btRem == 10 && lastDigit == 0))
			return true;
		
		return false;
	}
}

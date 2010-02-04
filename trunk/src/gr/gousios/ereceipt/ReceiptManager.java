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

import gr.gousios.ereceipt.model.ClientApp;
import gr.gousios.ereceipt.model.Company;
import gr.gousios.ereceipt.model.Error;
import gr.gousios.ereceipt.model.Receipt;
import gr.gousios.ereceipt.model.User;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ReceiptManager extends HttpServlet {
	
	private static final Logger log = Logger.getLogger("usermgr");
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		
		EntityManager em = EMF.get().createEntityManager();
		Receipt r = getReceiptFromRequest(req, resp, em);
		
		if (r == null) {
			em.close();
			return;
		}
		
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().print(r.toJSON(em));
		em.clear();
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		//resp.setContentType("application/json");
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("utf-8");
		
		EntityManager em = EMF.get().createEntityManager();
		String key = req.getParameter("key");
		if (!isAuth(key, resp, em)) 
			return;
		
		String appkey = req.getParameter("appkey");
		ClientApp app = ClientApp.fromKey(em, appkey);
		
		if (app == null) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().print(Error.appNotAuthorised(appkey).toJSON(em));
			em.close();
			return;
		}
		
		String afm = req.getParameter("afm");
		String dt = req.getParameter("date");
		String am = req.getParameter("amount");
		String cat = req.getParameter("cat");
		String cname = req.getParameter("cname");
		
		if (afm == null || dt == null || am == null) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().print(Error.missParam(null).toJSON(em));
			em.close();
			return;
		}
		
		Float amount = new Float(0.0);
		try {
			am.replace(',', '.');
			amount = Float.parseFloat(am);
		} catch (NumberFormatException nfe) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().print(Error.wrongAmount(am).toJSON(em));
			em.close();
			return;
		}
		
		Date date = null;
		try {
			long ts = Long.parseLong(dt) * 1000L;
			date = new Date(ts);
			if (!date.after(new Date(1262304000000L))){ //1/1/2010 0:00
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().print(Error.dateTooOld(date).toJSON(em));
				em.close();
				return;
			}
		} catch (NumberFormatException nfe) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().print(Error.wrongDate(dt).toJSON(em));
			em.close();
			return;
		}
		
		Company c = Company.fromAFM(em, afm);
		
		User u = User.fromApiKey(em, key);
		
		Receipt r = new Receipt();
		r.setAmount(amount);
		r.setCategory(cat);
		r.setDate(date);
		//r.setUser(u);
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			if (c == null) {
				c = new Company(afm, null);
				
				if (cname != null && !cname.trim().equals(""))
					c.setName(cname);
				
				em.persist(c);
			}
			if (cname != null && !cname.trim().equals("") 
					&& c.getName() != null && !c.getName().equals(""))
				c.setName(cname);

			r.setAfm(c.getAfm());
			u.getReceipts().add(r);
			
			app.setNumreceipts(app.getNumreceipts() + 1);
			
			tx.commit();
		} catch (Throwable t) {
			log.severe("Error adding receipt for company " + c.getName() + 
					" by user " + u.getName() + " Reason:" + t.getMessage());
			tx.rollback();
			resp.setStatus(resp.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().print(Error.unknownError().toJSON(em));
			return;
		}
		
		log.info("Receipt " + r.getId() + " was created successfully");
		resp.setStatus(HttpServletResponse.SC_CREATED);
		resp.getWriter().print(r.toJSON(em));
		em.close();
	}
	
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		
		EntityManager em = EMF.get().createEntityManager();
		Receipt r = getReceiptFromRequest(req, resp, em);
		
		if (r == null) {
			em.close();
			return;
		}

		em.remove(r);
		resp.setStatus(HttpServletResponse.SC_OK);
		em.close();
	}
	
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		
		EntityManager em = EMF.get().createEntityManager();
		Receipt r = getReceiptFromRequest(req, resp, em);
		
		if (r == null) {
			em.close();
			return;
		}
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Error e = null;
		try {
			String am = req.getParameter("amount");

			Float amount = new Float(0.0);
			try {
				am.replace(',', '.');
				amount = Float.parseFloat(am);
				r.setAmount(amount);
			} catch (NumberFormatException nfe) {
				e = Error.wrongAmount(am);
				throw nfe;
			}

			String dt = req.getParameter("date");
			Date date = null;
			try {
				long ts = Long.parseLong(dt) * 1000L;
				date = new Date(ts);
				if (!date.after(new Date(1262304000000L))) { // 1/1/2010 0:00
					e = Error.dateTooOld(date);
					throw new Exception("Date is too old");
				}
				r.setDate(date);
			} catch (NumberFormatException nfe) {
				e = Error.wrongDate(dt);
				throw nfe;
			}

			String cat = req.getParameter("cat");
			if (cat != null) {
				r.setCategory(cat);
			}

			em.flush();
			tx.commit();
		} catch (Throwable t) {
			tx.rollback();
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			if (e != null)
				resp.getWriter().print(e.toJSON(em));
		} finally {
			em.close();
		}
	}

	private Receipt getReceiptFromRequest(HttpServletRequest req,
			HttpServletResponse resp, EntityManager em) throws IOException {
		String key = req.getParameter("key");
		
		User u =  User.fromApiKey(em, key);
		
		if (key == null || u == null) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().print(Error.notAuthorised(key).toJSON(em));
			return null;
		}
		
		String[] urlParts = req.getRequestURL().toString().split("receipt/");
		
		if (urlParts.length < 2) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().print(Error.noReceipt("").toJSON(em));
			return null;
		}
		
		String rid = urlParts[1];
		if (rid == null || rid.equals("")) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().print(Error.noReceipt(rid).toJSON(em));
			return null;
		}
		
		Long id = null;
		
		try {
			id = Long.parseLong(rid);
		} catch (NumberFormatException nfe) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.getWriter().print(Error.noReceipt(rid).toJSON(em));
			return null;
		}
		
		Receipt r = Receipt.fromId(em, u.getId().getId(), id);
		
		if (r == null ||!r.getUser().getApiKey().equals(key)) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().print(Error.notUsersReceipt().toJSON(em));
			return null;
		}
		return r;
	}
	
	private boolean isAuth(String key, HttpServletResponse resp, EntityManager em) throws IOException {
		if (key == null || User.fromApiKey(em, key) == null) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().print(Error.notAuthorised(key).toJSON(em));
			return false;
		}
		
		return true;
	}
}

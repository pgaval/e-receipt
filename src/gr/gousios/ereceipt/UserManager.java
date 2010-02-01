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

import gr.gousios.ereceipt.model.Error;
import gr.gousios.ereceipt.model.Receipt;
import gr.gousios.ereceipt.model.User;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class UserManager extends HttpServlet {

	private static final Logger log = Logger.getLogger("usermgr");
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("utf-8");
		EntityManager em = EMF.get().createEntityManager();
		
		String url = req.getRequestURL().toString();
		
		if (url.split("user/").length < 2) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().print(Error.noSuchUser("").toJSON(em));
			em.close();
			return;
		}
		
		String[] rest = url.split("user/")[1].split("/");
		String uname = URLDecoder.decode(rest[0], "UTF8");
		String receipts = null;
		
		if (rest.length > 1) {
			receipts = rest[1];
		}
		
		String passwd = req.getParameter("passwd");
		String key = req.getParameter("key");
		
		User u = null;
		
		if (passwd != null) {
			u = User.fromCredentials(em, uname, passwd);
		} else if (key != null) {
			u = User.fromApiKey(em, key);
		} else {
			u = User.fromUserName(em, uname);
			
			if (u == null) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().print(Error.noSuchUser(uname).toJSON(em));
				em.close();
				return;
			}
			
			//Hide details when no key/username is used
			User tmp = new User();
			tmp.setUsername(u.getUsername());
			u = tmp;
		}
		
		if (u != null){
			if (receipts != null) {
				StringBuffer sb = new StringBuffer("[\n");
				for (Receipt r : u.getReceipts()) {
					sb.append(r.toJSON(em)).append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("\n]");
				
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().print(sb.toString());
				em.close();
				return;
			} else {
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().print(u.toJSON(em));
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().print(Error.wrongPasswd(uname).toJSON(em));
		}
		em.close();
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		EntityManager em = EMF.get().createEntityManager();
		
		String uname = (req.getParameter("uname") == null) ? "" : 
			req.getParameter("uname");
		String passwd = (req.getParameter("passwd") == null) ? "" : 
			req.getParameter("passwd");
		String name = (req.getParameter("name") == null) ? "" : 
			req.getParameter("name");
		String email = (req.getParameter("email") == null) ? "" : 
			req.getParameter("email");
		
		if (uname == null || uname.equals("") || uname.length() < 4) {
			resp.getWriter().print(Error.unameTooShort(uname).toJSON(em));
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		if (User.fromUserName(em, uname) != null) {
			resp.getWriter().print(Error.userExists(uname).toJSON(em));
			resp.setStatus(HttpServletResponse.SC_CONFLICT);
			em.close();
			return;
		}
		
		if (passwd.equals("") || passwd.length() < 6) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().print(Error.passwdTooShort(uname).toJSON(em));
			em.close();
			return;
		}
		
		User u = new User();
		u.setUsername(uname);
		u.setPasswd(passwd);
		u.setName(name);
		u.setEmail(email);
		u.setPaid(false);
		
		StringBuffer apikey = new StringBuffer();
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			String api = System.nanoTime() + "" + 
				System.identityHashCode(this) + "" + uname;
			algorithm.update(api.getBytes());
			byte[] digest = algorithm.digest();
			
			for (int i = 0; i < digest.length; i++) {
				apikey.append(Integer.toHexString(0xFF & digest[i]));
			}
			
		} catch (NoSuchAlgorithmException e) {
			resp.setStatus(500);
			resp.getWriter().print(Error.unknownError().toJSON(em));
			log.severe(e.toString());
			em.close();
			return;
		}
		
		u.setApiKey(apikey.toString());
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			em.persist(u);
			tx.commit();
		} catch (Throwable t) {
			log.severe("Error adding user " + uname + 
					" Reason:" + t.getMessage());
			tx.rollback();
			resp.setStatus(500);
			resp.getWriter().print(Error.unknownError().toJSON(em));
			return;
		} 
			
		log.info("User " + u.getName() + " was created successfully");
		resp.setStatus(HttpServletResponse.SC_CREATED);
		resp.getWriter().print(u.toJSON(em));
		em.close();
	}
	
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		log.severe("User.DELETE from client:" + req.getRemoteHost());
	}
	
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		log.severe("User.PUT from client:" + req.getRemoteHost());
	} 
}

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

package gr.gousios.ereceipt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import net.sf.json.JSONObject;

import com.google.appengine.api.datastore.Key;

@Entity
public class User extends ModelObject {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;

	@Basic
	private String apiKey;
	
	@Basic
	private String name;
	
	@Basic
	private String username;
	
	@Basic
	private String passwd;
	
	@Basic
	private String email;
	
	@Basic
	private Boolean paid;
		
	@OneToMany(cascade = CascadeType.ALL)
    private List<Receipt> receipts = new ArrayList<Receipt>();
	
	public List<Receipt> getReceipts() {
		return receipts;
	}
	public void setReceipts(List<Receipt> receipts) {
		this.receipts = receipts;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Boolean getPaid() {
		return paid;
	}
	public void setPaid(Boolean paid) {
		this.paid = paid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public Key getId() {
		return id;
	}
	
	public void setId(Key id) {
		this.id = id;
	}
	
	public static User fromCredentials(EntityManager em, String uname, String passwd) {
		Query q = em.createQuery("select u from User as u " +
				" where u.username= :uname" +
				" and u.passwd= :passwd");
		q.setParameter("uname", uname);
		q.setParameter("passwd", passwd);
		List<User> l = (List<User>)q.getResultList();
		if (l.isEmpty()) {
			return null;
		} 
		
		return l.get(0);
	}
	
	public static User fromApiKey(EntityManager em, String key) {
		Query q = em.createQuery("select u from User as u " +
				"where u.apiKey=:api");
		q.setParameter("api", key);
		List<User> l = (List<User>)q.getResultList();
		if (l.isEmpty()) {
			return null;
		} 
		
		return l.get(0);
	}
	
	public static User fromUserName(EntityManager em, String uname) {
		Query q = em.createQuery("select u from User as u " +
				" where u.username= :uname");
		q.setParameter("uname", uname);
		List<User> l = (List<User>)q.getResultList();
		if (l.isEmpty()) {
			return null;
		} 
		
		return l.get(0);
	}
	
	@Override
	public String toJSON(EntityManager em) {
		JSONObject json = new JSONObject();	
		Map<String, String> values = new HashMap<String, String>();
		if (apiKey != null)
			values.put("apikey", apiKey);
		if (name != null)
			values.put("name", name);
		if (username != null)
			values.put("username", username);
		if (passwd != null)
			values.put("passwd", passwd);
		json.element("user", values);
		return json.toString(1);
	}
}

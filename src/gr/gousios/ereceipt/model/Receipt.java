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

import gr.gousios.ereceipt.EMF;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import net.sf.json.JSONObject;

@Entity
public class Receipt extends ModelObject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;

	@Basic
	private Date date;
	
	@Basic
	private float amount;
	
	@Basic
	private String afm;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	private User user;
	
	@Basic
	private String category;
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getAfm() {
		return afm;
	}

	public void setAfm(String afm) {
		this.afm = afm;
	}
	
	public Key getId() {
		return id;
	}
	
	public void setId(Key id) {
		this.id = id;
	}
	
	public User getUser() {
		if (user != null)
			return user;
		
		EntityManager em = EMF.get().createEntityManager(); 
		Query q = em.createQuery("select u from User as u " +
		" where u.id=:key");
		Key k = (new KeyFactory.Builder("User", id.getParent().getId())).getKey();
		q.setParameter("key", k);
		List<User> l = (List<User>)q.getResultList();
		
		if (l.isEmpty()) {
			return null;
		}
		
		return l.get(0);	
	}
	
	public static Receipt fromId(EntityManager em, Long userId, Long id) {
		Query q = em.createQuery("select r from Receipt as r " +
		" where r.id=:key");
		Key k = (new KeyFactory.Builder("User", userId)).addChild("Receipt", id).getKey();
		q.setParameter("key", k);
		List<Receipt> l = (List<Receipt>)q.getResultList();
		
		if (l.isEmpty()) {
			return null;
		}
		
		return l.get(0);
	}
	
	@Override
	public String toJSON(EntityManager em) {
		JSONObject json = new JSONObject();	
		Map<String, String> values = new HashMap<String, String>();

		values.put("id", (new Long(id.getId())).toString());
		values.put("date", (new Long(date.getTime())).toString());
		values.put("amount", String.valueOf(amount));
		if (getAfm() != null)
			values.put("company", Company.fromAFM(em, afm).toJSON(em));
		if (user != null)
			values.put("user", user.toJSON(em));
		if (getCategory() != null)
			values.put("cat", getCategory());
		json.element("receipt", values);
		return json.toString(1);
	}
}

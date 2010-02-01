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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.google.appengine.api.datastore.Key;

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
	
	@ManyToOne(fetch=FetchType.LAZY)
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

	public User getUser() {
		return user;
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

	public static Receipt fromId(Long id) {
		return null;
	}
	
	public Key getId() {
		return id;
	}
	
	public void setId(Key id) {
		this.id = id;
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
		if (getUser() != null)
			values.put("user", user.toJSON(em));
		if (getCategory() != null)
			values.put("cat", getCategory());
		json.element("receipt", values);
		return json.toString(1);
	}
}

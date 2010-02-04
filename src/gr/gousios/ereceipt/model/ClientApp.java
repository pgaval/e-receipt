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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import com.google.appengine.api.datastore.Key;

import net.sf.json.JSONObject;

@Entity
public class ClientApp extends ModelObject {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	@Basic
	private String name;
	
	@Basic
	private String key;
	
	@Basic
	private Long numreceipts;
	
	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getNumreceipts() {
		return numreceipts;
	}

	public void setNumreceipts(Long numreceipts) {
		this.numreceipts = numreceipts;
	}
	
	public static ClientApp fromKey(EntityManager em, String key) {
		Query q = em.createQuery("select c from ClientApp as c " +
		"where c.key=:key");
		q.setParameter("key", key);
		List<ClientApp> l = (List<ClientApp>)q.getResultList();
		if (l.isEmpty()) {
			return null;
		} 

		return l.get(0);
	}
	
	public static List<ClientApp> getAll(EntityManager em) {
		Query q = em.createQuery("select c from ClientApp as c ");
		return (List<ClientApp>)q.getResultList();
	}

	@Override
	public String toJSON(EntityManager em) {
		JSONObject json = new JSONObject();	
		Map<String, String> values = new HashMap<String, String>();
		if (key != null)
			values.put("key", key);
		if (name != null)
			values.put("name", name);
		if (numreceipts != null)
			values.put("numreceipts", numreceipts.toString());

		json.element("clientapp", values);
		return json.toString(1);
	}

}

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

import net.sf.json.JSONObject;

import com.google.appengine.api.datastore.Key;

@Entity
public class Company extends ModelObject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	@Basic
	private String afm;
	
	@Basic
	private String name;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Key getId() {
		return id;
	}
	public void setId(Key id) {
		this.id = id;
	}
	
	public String getAfm() {
		return afm;
	}
	public void setAfm(String amf) {
		this.afm = amf;
	}
	
	public Company() {}
	
	public Company(String afm, String name) {
		this.afm = afm;
		this.name = name;
	}
	
	public static Company fromAFM(EntityManager em, String afm) {
		Query q = em.createQuery("select c from Company as c " +
				" where c.afm = :afm");
		q.setParameter("afm", afm);
		List<Company> l = (List<Company>)q.getResultList();
		
		if (l.isEmpty()) {
			return null;
		} 
		
		return l.get(0);
	}
	
	@Override
	public String toJSON(EntityManager em) {
		JSONObject json = new JSONObject();	
		Map<String, String> values = new HashMap<String, String>();
		if (name != null)
			values.put("name", name);
		
		values.put("afm", afm);
		json.element("company", values);
		return json.toString(1);
	}
}

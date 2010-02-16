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
import java.util.List;


import javax.persistence.EntityManager;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

public abstract class ModelObject {

	private static List<Class<?>> modelObjects;
	
	static {
		modelObjects = new ArrayList<Class<?>>();
	}
	
	public static void addModelObject(Class<?> clazz) {
		modelObjects.add(clazz);
	}
	
	public static Class<?>[] getModelObjects() {
		return modelObjects.toArray(new Class[modelObjects.size()]);
	}

	public abstract String toJSON(EntityManager em);
	
	public String toXML(EntityManager em) {	
		JSONObject json = JSONObject.fromObject(this.toJSON(em));	
		String xml = new XMLSerializer().write( json );
		return xml;
	}

}

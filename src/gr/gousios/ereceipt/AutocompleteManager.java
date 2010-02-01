package gr.gousios.ereceipt;

import gr.gousios.ereceipt.model.Company;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class AutocompleteManager extends HttpServlet {

	private TreeSet<Long> companies;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		
		EntityManager em = EMF.get().createEntityManager();
		
		String q = req.getParameter("q");
		String type = req.getParameter("type");
		
		if (q == null || q.length() == 0 ||
				type == null || type.length() == 0)
			return;
		
		if (type.equals("company"))
			resp.getWriter().append(acCompanyFromAFM(em, q));
	}

	private String acCompanyFromAFM(EntityManager em, String q) {
		
		if (companies == null) {
			Query query = em.createQuery("select c from Company as c");
			List<Company> l = (List<Company>) query.getResultList();
			
			companies = new TreeSet<Long>();
			for (Company c : l) {
				companies.add(Long.parseLong(c.getAfm()));
			}
		}
		
		if (q.length() < 6)
			return "";
		
		Long i = 0L;
		try {
			i = Long.parseLong(q);
		} catch(NumberFormatException nfe) {
			return "";
		}
		
		if (q.length() < 7)
			i = i * 10;
		
		if (q.length() < 8)
			i = i * 10;
		
		if (q.length() < 9)
			i = i * 10;
		
		SortedSet<Long> result = companies.subSet(i - 999L, i + 999L);
		StringBuffer sb = new StringBuffer();
		Company c = null;
		
		for (Long afm : result) {
			String afmtxt = (String.valueOf(afm).length() == 9)? String.valueOf(afm): "0" + String.valueOf(afm);
			c = Company.fromAFM(em, afmtxt);
			sb.append(afmtxt);
			
			sb.append("|")
				.append((c.getName()==null)?"":c.getName())
				.append("\n");
		}
	
		return sb.toString();
	}
}

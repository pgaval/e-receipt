<%@page import="gr.gousios.ereceipt.Keys"%>
<%
  
  if (request.getParameter("logout") != null) {
    if (session.getAttribute("key") != null) {
      session.removeAttribute("key");
    }
    
    if (session.getAttribute("user") != null) {
        session.removeAttribute("user");
      }
    
    response.sendRedirect("index.jsp"); 
  }

  String key = null;
  if (request.getParameter("key") != null) {
	  session.setAttribute("key", request.getParameter("key"));
      key = (String)session.getAttribute("key");
  } else if (session.getAttribute("key") != null) {
      key = (String)session.getAttribute("key");
  } else { 
    response.sendRedirect("index.jsp");
  }

  String user = null;
  if (request.getParameter("user") != null) {
      session.setAttribute("user", request.getParameter("user"));
      user = (String)session.getAttribute("user");
  }
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title>e-Αποδείξεις: Καταγράψτε τις αποδείξεις σας!</title>
    <link rel="stylesheet" href="css/main.css" type="text/css"/>
    <link type="text/css" href="css/smoothness/jquery-ui-1.7.2.custom.css" rel="stylesheet" />
    <script type="text/javascript" src="js/lib-main.js"></script> 
    <script type="text/javascript" src="js/initmain.js"></script> 
  </head>
  <body>
  <div id="appname">e-Αποδείξεις</div>
  <div id="tagline">Καταγράψτε τις αποδείξεις σας</div>
  <div id="logout" align="right"><a href="main.jsp?logout">Έξοδος</a></div>
  <div id="content">
   <div id="tabs">
    <ul class="tabNavigation">
      <li><a href="#tab1"><span>Αποδείξεις</span></a></li>
      <li><a href="#tab2"><span>Ρυθμίσεις</span></a></li>
    </ul>
    <div id="tab1">
      <div id="stats">
        <span id="surname"></span>|
        <span id="numreceipts" class="hugeletters">0</span> αποδείξεις |
        <span class="hugeletters"> &euro;<span id="amountval">0</span></span> | 
        <span id="newreceiptlbl">Νεά απόδειξη 
          <img id="rcparrdn" src="img/arrowdown.png" width="15" align="bottom" height="15" alt="Άνοιγμα φόρμας καταχώρησης απόδειξης"/>
          <img id="rcparrup" src="img/arrowup.png" style="display: none" width="15" align="bottom" height="15" alt="Κλείσιμο φόρμας καταχώρησης απόδειξης"/>
        </span>
      </div>
      <div id="addreceipt">
        <table id="addreceiptform">
        <tr>
        <td width="90%">
        <form id="newreceipt" action="/receipt/" method="POST">
         <fieldset>
            <legend>Συμπληρώστε τα στοιχεία της απόδειξης</legend>
            <table>
            <tr>
              <td><label for="afm">ΑΦΜ</label><em>*</em></td>
              <td><input id="afm" size="30" class="roundedtext" type="text" name="afm"/></td>
            </tr>
            <tr id="companyname" class="inactive">
              <td><label for="afm">Όνομα</label><em>*</em></td>
              <td>
                <div id="afmknownname" class="inactive"><a id="namenotcorrect" href="#">(Όχι το σωστό όνομα?)</a></div>
                <input id="afmname" class="inactive" size="30" class="roundedtext" type="text" name="afmname"/>
              </td>
            </tr>
            <tr>
              <td><label for="date">Ημερομηνία</label><em>*</em></td>
              <td><input id="date" size="30" class="roundedtext" type="text" name="date"/></td>
            </tr>
            <tr>
              <td><label for="amount">Ποσό</label><em>*</em></td>
              <td><input id="amount" size="30" class="roundedtext" type="text" name="amount"/></td>
            </tr>
            <tr>
              <td><label for="cat">Κατηγορία</label><em>*</em></td>
              <td>
                <select id="cat">
                  <option selected="selected" value="Γενικά έξοδα">Γενικά έξοδα</option>
                  <option value="Τρόφιμα/Μη αλκοολούχα">Τρόφιμα/Μη αλκοολούχα</option>
                  <option value="Αλκοολούχα/Καπνός">Αλκοολούχα/Καπνός</option>
                  <option value="Ένδυση">Ένδυση</option>
                  <option value="Στέγαση/Λογαριασμοί">Στέγαση/Λογαριασμοί</option>
                  <option value="Επίπλωση/Οικιακός Εξοπλισμός">Επίπλωση/Οικιακός Εξοπλισμός</option>
                  <option value="Υγεία">Υγεία</option>
                  <option value="Μεταφορές">Μεταφορές</option>
                  <option value="Επικοινωνίες">Επικοινωνίες</option>
                  <option value="Πολιτισμός/Διασκέδαση">Πολιτισμός/Διασκέδαση</option>
                  <option value="Εκπαίδευση">Εκπαίδευση</option>
                  <option value="Υπηρεσίες">Υπηρεσίες</option>
                </select>
              </td>
            </tr>
            <tr>
              <td></td>
              <td align="right"><input class="button" type="submit" value="Εισαγωγή"/></td>
              <td align="right">
                <input id="key" type="hidden" name="key" value="<%=key%>"/>
                <input id="username" type="hidden" name="username" value="<%=user%>"/>
                <input id="appkey" type="hidden" name="appkey" value="aa6cc36372ab959b76447beda23cc15" />
              </td>
            </tr>
            </table>
          </fieldset>
        </form>
        </td>
        <td width="10%" align="right"> 
          <script type="text/javascript"><!--
          google_ad_client = "pub-9563091108624083";
          /* 300x250-mainpage */
          google_ad_slot = "3434011668";
          google_ad_width = 300;
          google_ad_height = 250;
          //-->
          </script>
          <script type="text/javascript" src="http://pagead2.googlesyndication.com/pagead/show_ads.js"></script>
        </td>
        </tr>
      </table>
      </div>

      <table id="receipts" class="tablesorter"> 
      <thead> 
        <tr>
          <th>Α/Α</th> 
          <th>ΑΦΜ</th> 
          <th>Επωνυμία</th> 
          <th>Ημερομηνία</th>
          <th>Κατηγορία</th>
          <th>Ποσό</th>
          <th>Ενέργειες</th>  
        </tr>
      </thead> 
      <tbody> 
      </tbody> 
      </table>
    </div>
    <div id="tab2">
      <table>
      <tr>
      <td>
      <form action="https://www.paypal.com/cgi-bin/webscr" method="post">
        <input type="hidden" name="cmd" value="_s-xclick">
        <input type="hidden" name="encrypted" value="-----BEGIN PKCS7-----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-----END PKCS7-----
">
        <input type="image" src="https://www.paypal.com/en_US/i/btn/btn_donateCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
        <img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
        <img alt="Paypal Icon" src="img/pp.gif">
      </form>
      </td>
      <td valign="top">
        <p>
          Αν η υπηρεσία που σας παρέχει το e-receipt.appspot.com σας φαίνεται χρήσιμη παρακαλώ σκεφτείτε να δωρίσετε κάποιο ποσό. 
          Η δωρεά γίνεται μέσω Paypal (την ίδια υπηρεσία που χρησιμοποιείτε για να πληρώσετε στο e-bay και πιθανώς και αλλού) 
          για να διασφαλίζεται η ασφάλεια της συναλλαγής.
        </p> 
        <p> 
          Εαν δωρίσετε κάποιο ποσό, οι διαφημίσεις δεν θα εμφανίζονται.   
        </p>
      </td>
      </tr>
      </table>
    </div>
  </div>
  </div>
  <div id="footer">
      Κεντρική σελίδα
      <a href="about.html">Σχετικά</a>
      <a href="faq.html">Συχνές ερωτήσεις</a>
      <a href="api.html">Για προγραμματιστές</a>
      <a href="http://groups.google.com/group/e-receipt">Επικοινωνία</a>
  </div>
  </body>
</html>
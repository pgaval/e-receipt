<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
if (session.getAttribute("key") != null) {
  response.sendRedirect("main.jsp?key=" + session.getAttribute("key"));  
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>e-Αποδείξεις: Καταγράψτε τις αποδείξεις σας!</title>
  	<link rel="stylesheet" href="css/style.css" type="text/css">
  	<link type="text/css" href="css/smoothness/jquery-ui-1.7.2.custom.css" rel="stylesheet">
    <link rel="stylesheet" href="css/thickbox.css" type="text/css" media="screen">
    <!-- <script type="text/javascript" src="js/jquery.js"></script>
    <script type="text/javascript" src="js/thickbox.js"></script>
    <script type="text/javascript" src="js/jquery.validate.js"></script>
    <script type="text/javascript" src="js/json.js"></script>-->
    <script type="text/javascript" src="js/lib-index.js"></script>
    <script type="text/javascript" src="js/indexprepare.js"></script>
  </head>
  <body>
  <div id="appname">e-Αποδείξεις</div>
  <div id="tagline">Καταγράψτε τις αποδείξεις σας</div>
  <div id="content">
    <div id="login">
      <div id="loginform">
        
        <form action="/user/" method="get" id="theloginform">
          <div class="teaser">Για εγγεγραμένους χρήστες</div><br>
           Όνομα Χρήστη <br>
            <input type="text" class="roundedtext" id="lname" name="lname"/>
	        Κωδικός <br>
            <input type="password" class="roundedtext" id="lpasswd" name="lpasswd"/><br>
            <input type="submit" class="button" value="Είσοδος"/>
        </form>
      </div>
    </div>
	
    <div id="register">
      <div id="registerform">
		  <div class="teaser">Εγγραφείτε τώρα!</div><br>
          Oνοματεπώνυμο <br>
          <input class="roundedtext" id="ename"  type = "text" name="name"/><br>
          <span id="eunamelbl">Όνομα Χρήστη</span> <br>
          <input class="roundedtext" id="euname" type="text" name="uname"/><br>
          Κωδικός<br>
          <input class="roundedtext" id="epasswd" type="password" name="passwd"/><br>
          <input id="regbut" type="button" class="thickbox button" value="Εγγραφή" alt="#TB_inline?height=300&width=500&inlineId=regstage2"/>
      </div>	
	</div>
  </div>
  <div id="loginwait" align="center">
    <img src="img/wait.gif" alt="Please wait"/>
    <p>Είσοδος στο σύστημα, παρακαλώ περιμένετε</p>
   </div>
  <div id="footer">
      Κεντρική σελίδα
      <a href="about.html">Σχετικά</a>
      <a href="faq.html">Συχνές ερωτήσεις</a>
      <a href="api.html">Για προγραμματιστές</a>
      <a href="http://groups.google.com/group/e-receipt">Επικοινωνία</a>
  </div>
  <div id="bottomad" align="center">
    <script type="text/javascript"><!--
    google_ad_client = "pub-9563091108624083";
    /* 728x90-index */
    google_ad_slot = "9898055881";
    google_ad_width = 728;
    google_ad_height = 90;
    //-->
    </script>
    <script type="text/javascript" src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
    </script>
  </div>
  <div id="regstage2" style="visibility: hidden">
      <form class="cmxform" id="commentForm" method="post" action="/user">
        <fieldset>
          <legend>Παρακαλούμε συμπληρώστε σωστά τα στοιχεία σας</legend>
          <p>
            <label for="name">Oνοματεπώνυμο</label><em>*</em>
            <input id="name" size="35" class="required roundedtext" type="text" name="name"/>
          </p>
          <p>
            <label for="uname">Όνομα Χρήστη</label><em>*</em>
            <input id="uname" size="35" class="required roundedtext" type="text" name="uname"/>
          </p>
          <p>
            <label for="passwd">Κωδικός</label><em>*</em>
            <input id="passwd" size="35" class="roundedtext required" type="password" name="passwd"/>
          </p>
          <p>
            <label for="passwd2">Κωδικός - Ορθή επανάληψη</label><em>*</em>
            <input id="passwd2" size="35" class="roundedtext required" type="password" name="passwd"/>
          </p>
          <p>
            <label for="email">Email</label><em>*</em>
            <input id="email" size="35" class="roundedtext required email" type="text" name="email"/>
          </p>
          <p>
            <input class="submit" type="submit" value="Εγγραφή" style="margin-left: 12em"/>
          </p>
        </fieldset>
      </form>
  </div>
  </body>
</html>
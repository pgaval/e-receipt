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

$(document).ready(function(){

	$("#tabs").tabs();
	
	$.tablesorter.defaults.widgets = ['zebra']; 
	$("table").tablesorter();

	$("#newreceiptlbl")
		.mouseenter(function() {
			$(this).addClass('hover');
	})
		.mouseleave(function() {
			$(this).removeClass('hover');
	});
	
	fillReceipts();
	
	$.getJSON('/user/' + $("#username").val() , 
			{'key': $("#key").val()} , 
			function(data){
			$("#surname").text(data.user.name);	
	});
	
	$("#date").datepicker({
   		showOn: 'button', 
   		buttonImage: 'img/cal.gif', 
   		buttonImageOnly: true, 
   		dateFormat: 'dd/mm/yy',
   		duration: ''},
   		$.datepicker.regional['el']
   	);
	
    $.validator.addMethod("dateGR", 
    	function(value, element) {
    		var dateParts = value.split("/");
    		if (dateParts[0] == null || !/[0-9]*/.test(dateParts[0]) 
    				|| dateParts[0] >= 32 || dateParts[0] < 1)
    			return false;
    		
    		if (dateParts[1] == null || !/[0-9]*/.test(dateParts[1]) 
    				|| dateParts[1] > 12 || dateParts[1] < 1)
    			return false;
    		
    		if (dateParts[2] == null || !/[0-9]*/.test(dateParts[2]) 
    				|| dateParts[2] < 2010)
    			return false;
    		
    		return true;
    	}, 
    	"Παρακαλώ εισάγετε μια έγκυρη ημερομηνία στο 2010 (πχ 30/12/2010)"
    );
    
    $.validator.addMethod("numberGR", 
        	function(value, element) {
    			var number = new RegExp("^\\d+([,.]\\d+)?$");
    			return number.test(value);
        	}, 
        	"Παρακαλώ εισάγετε το ποσό σωστά (πχ 12,23)"
        );
    
    $.validator.addMethod("afm",
    	function (afm, element) {      
    			
    		if (!afm.match(/^\d{9}$/)) 
    			return false;
    		
    		afm = afm.split('').reverse().join('');
       
    		var Num1 = 0;
    		for(var iDigit= 1; iDigit <=  8; iDigit++) {
    			Num1 += afm.charAt(iDigit) << iDigit;     
    		}

    		return (Num1 % 11) % 10 == afm.charAt(0);   
    	},
    	"Παρακαλώ εισάγετε ένα έγκυρο ΑΦΜ"
    );
    
    $("#afm").bind('change', function(e) {
    	$.ajax({
    		type: 'GET',
    		url : '/company/' + $("#afm").val(),
    		data : {
    			key: $("#key").val()
    		},
    		timeout: 55000,
    		dataType: 'json',
    		success: function(data, status){
				$("#companyname").removeClass("inactive");
    			if (data.company.name != null) {
    				/*The company name is known, display it*/
    				$("#afmname").addClass("inactive");
    				$("#afmknownname").removeClass("inactive");
    				$("#afmknownname").text(data.company.name);
    			} else {
    				/* Company name not known*/
    				$("#afmknownname").addClass("inactive");
					$("#afmname").removeClass("inactive");
					$("#afmname").focus();
    			}
    		},
    		error: function(xhr, status, error) {
				var formError = jsonParse(xhr.responseText);
				if (formError.error.code == "200") {
					/*Company not registered yet, offer textbox to enter name*/
					$("#afmknownname").addClass("inactive");
					$("#companyname").removeClass("inactive");
					$("#afmname").removeClass("inactive");
					$("#afmname").focus();
				} else {
					alert("Λάθος κατά την αναζήτηση της εταιρείας: " + formError.error.msg);
				}
			}
    	});
    });
    
    $("#newreceipt").validate({
    	rules: {
    		afm: {
    			required: true,
    			afm: true
    		}, 
    		date: {
    			required: true,
    			dateGR: true
    		},
    		amount: {
    			required: true,
    			numberGR: true
    		}
    	},
    	messages: {
    		afm: {
    			required: "Το πεδίο ΑΦΜ είναι απαραίτητο"
    		},
    		date: {
    			required: "Το πεδίο Ημερομηνία είναι απαραίτητο"
    		}, 
    		amount: {
    			required: "Το πεδίο Ποσό είναι απαραίτητο",
    		}	
    	},
    	submitHandler: function (form) {
    		var date =  $("#date").val();
    		var df = date.split("/");
    		var d = new Date(df[2], df[1] - 1, df[0], 9, 0, 0, 0);
    		var name = $("#afmname").val();
    		
    		$.ajax({
				type: 'POST',
				url : '/receipt/',
				dataType: 'json',
				data : {
					afm : $("#afm").val(),
					amount : $("#amount").val().replace(',','.'),
					date: (d.getTime() / 1000), //Convert to seconds
					key: $("#key").val(),
					appkey: $("#appkey").val(),
					cat: $("#cat").val(),
					cname: name
				},
				timeout: 55000,
				cache: false,
				success: function(data, status) {
					alert('Η απόδειξη καταχωρήθηκε επιτυχώς');
					$("#afm").val('');
					$("#amount").val('');
					$("#date").val('');
					$("#afmname").val('');
					$("#companyname").addClass("inactive");
					fillReceipts();
				},
				error: function(xhr, status, error) {
					var formError = jsonParse(xhr.responseText);
					alert('Πρόβλημα κατά την καταχώρηση της απόδειξης:' + formError.error.msg);
				}
			});
    		return false;
    	}
    });
    
    $("#newreceiptlbl").click(function() {
    	$("#addreceipt").slideToggle("fast");
    	$("#addreceipt").css('display', 'block');
    	$("#newreceiptlbl img").toggle();
    });
    
});

function fillReceipts() {

	var month=new Array(12);
	month[0]="Ιανουαρίου";
	month[1]="Φεβρουαρίου";
	month[2]="Μαρτίου";
	month[3]="Απριλίου";
	month[4]="Μαϊου";
	month[5]="Ιουνίου";
	month[6]="Ιουλίου";
	month[7]="Αυγούστου";
	month[8]="Σεπτεμβρίου";
	month[9]="Οκτωβρίου";
	month[10]="Νοεμβρίου";
	month[11]="Δεκεμβρίου";
	
	$.ajax({
		type: 'GET',
		url : '/user/' + $("#username").val() + '/receipts/',
		dataType: 'json',
		timeout: 10000,
		cache: false,
		data : {
			key : $("#key").val()
		},
		success: function(data, status) {
			$("#receipts tbody tr").remove();
			var line = "";
			var counter = 1;
			var amount = 0;
			
			for (x in data) {
				line += '<tr id="' + data[x].receipt.id + '">';
				line += "<td>" + counter + "</td>";
				line += "<td>" + data[x].receipt.company.company.afm + "</td>";
				
				if (data[x].receipt.company.company.name != null)
					line += "<td>" + data[x].receipt.company.company.name + "</td>";
				else 
					line += "<td></td>";
				
				var ts = data[x].receipt.date*1;
				var date = new Date(ts);
				line += "<td><span style=\"display: none\">" + ts + "</span>"  
					+ date.getUTCDate() + " "+ month[date.getMonth()]  + "</td>";
				line += "<td>" + data[x].receipt.cat + "</td>";
				line += "<td>" + data[x].receipt.amount + "</td>";
				line += "<td>";
				line += '<span class="edit active"><img width="13" src="/img/edit.png" alt="Επεξεργασία εγγραφής">Διόρθωση</span>';
				line += '<span class="save inactive"><img width="13" src="/img/save.png" alt="Αποθήκευση εγγραφής">Αποθήκευση</span>';
				line += '<span class="cancel inactive"><img width="13" src="/img/stop.png" alt="Ακύρωση επεξεργασίας">Ακύρωση</span>';
				line += '<span class="delete active"><img width="13" src="/img/del.png" alt="Διαγραφή απόδειξης">Διαγραφή</span>';
				line += '</td>';
				amount += (data[x].receipt.amount - 0);
				line += "</tr>";
				counter ++;
			}
			
			$("#receipts tbody").append(line);
			if (!line == '') {
				$("table").trigger("update");
				var sorting = [[3,1],[0,0]]; 
				$("table").trigger("sorton",[sorting]);
			}
			
			var am = amount + '';
			if (am.match(/^\d*\.\d{3,}$/)) {
				var parts = am.split('.');
				$("#amountval").text(parts[0]+"."+parts[1].substring(0,2));
			} else {
				$("#amountval").text(am);
			}
			$("#numreceipts").text(counter - 1);
			
			$('.edit').bind('click', function(e) { 
				edit($(e.target).parent().parent().parent().attr("id"));
			});

			$('.save').bind('click', function(e) { 
				save($(e.target).parent().parent().parent().attr("id"));
			});

			$('.cancel').bind('click', function(e) {
				fillReceipts();
			});
				
			$('.delete').bind('click', function(e) {
				delReceipt($(e.target).parent().parent().parent().attr("id"));
			});
		},
		error: function(xhr, status, error) {
			var formError = jsonParse(xhr.responseText);
			alert('Πρόβλημα κατά την ανάκτηση των αποδείξεων: ' + formError.error.msg);
		}
	});
}

function edit(rowid) {

	if ($('tr').is('.editing'))
		return;
	
	//Date, add an inline date picker
	var node = $(($('tr[id=' + rowid +'] td')[3]));
	var ts = $($(node).find('span')[0]).text();
	var date = new Date((ts - 0));
	var dateStr = date.getUTCDate() + '/'  + (date.getUTCMonth() + 1) + '/' + date.getUTCFullYear();
	node.html('<input size="12" type="text" value="' + dateStr + '" id="editdate"/>');
	
	var picker = $("#editdate").datepicker({
		showOn: 'button', 
   		buttonImage: 'img/cal.gif', 
   		buttonImageOnly: true,
   		dateFormat: 'dd/mm/yy',
   		constrainInput: true,
   		defaultDate : date,
   		autosize: true,
   		duration: '',
   		gotoCurrent: true,
   		minDate: new Date(2010, 0, 1)
   		},
   		$.datepicker.regional['el']
   	);
	
	
	//Category field, copy dropdown from add receipt form
	node = $(($('tr[id=' + rowid +'] td')[4]));
	var value = node.html();
	node.html($("#cat").parent().html());
	$($(node).find("select")[0]).attr("id", "editcat");  //Set unique id 
	$.each($(node).find("option"), function(index, node) {
		if (value == $(node).html()) {
			$(node).attr("selected", "selected");
		} else {
			$(node).removeAttr("selected");
		}
	});
	
	//Amount, just add an input box
	node = $(($('tr[id=' + rowid +'] td')[5]));
	node.html('<input size="7" type="text" value="' + node.html() + '" id="editamount"/>');

	//Append a spinner icon to indicate progress upon save
	node = $(($('tr[id=' + rowid +'] td')[6]));
	node.append('<span class="wait inactive"><img width="13" src="/img/wait.gif" alt="Αποστολή αλλαγών"></span>');

	$('tr[id=' + rowid +']').toggleClass("editing");
	$('.edit').toggleClass("inactive");
	$('tr[id=' + rowid +'] .save').toggleClass("inactive");
	$('tr[id=' + rowid +'] .cancel').toggleClass("inactive");
	$('.delete').toggleClass("inactive");
}

function save(rowid) {
	if (!$('tr[id=' + rowid +']').is('.editing'))
		return;
	 
	var date =  $("#editdate").val();
	var df = date.split("/");
	var d = new Date(df[2], df[1] - 1, df[0], 9, 0, 0, 0);
	
	$('.wait').toggleClass("inactive");
	
	$.ajax({
		url: '/receipt/' + rowid + 
			'?key=' + $("#key").val() + 
			'&amount=' + $("#editamount").val() +
			'&date=' + (d.getTime() / 1000) +
			'&cat=' + $("#editcat").val(),
		type: 'PUT',
		timeout: 50000,
		success: function(html) {
			fillReceipts();
		}, 
		error: function(xhr, status, error) {
			$('tr[id=' + rowid +'] .wait').toggleClass("inactive");
			var formError = jsonParse(xhr.responseText);
			alert('Πρόβλημα κατά την αποθήκευση των νέων τιμών:' + formError.error.msg);
		}
	});
}

function delReceipt (rowid) {
	
	var answer = confirm("Είστε σίγουρος/-η ότι θέλετε να διαγράψετε την απόδειξη ?");
	
	if (!answer)
		return;
	
	$.ajax({
		url: '/receipt/' + rowid + '?key=' + $("#key").val(),
		data: {
			key : $("#key").val()
		},
		type: 'DELETE',
		timeout: 50000,
		success: function() {
			fillReceipts();
		},
		error: function(xhr, status, error) {
			var formError = jsonParse(xhr.responseText);
			alert('Πρόβλημα κατά την διαγραφή της απόδειξης:' + formError.error.msg);
		}
	});
}
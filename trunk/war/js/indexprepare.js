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

$(document).ready(function() {
	$("#commentForm").validate({
		rules: {
			name: {
				required: true
			},
			uname: {
				required: true
			},
			passwd: {
				required: true
			},
			passwd2: {
				required: true,
				equalTo: $("#passwd").val()
			},
			email: {
				required: true,
				email: true
			}
		},
		messages: {
			name: {
				required: "Το όνομά σας χρειάζεται για την εγγραφή"
			},
			uname: {
				required: "Το όνομα χρήστη χρειάζεται για την εγγραφή"
			},
			passwd: {
				required: "Παρακαλώ εισάγετε ένα κωδικό χρήστη"
			},
			passwd2: {
				required: "Παρακαλώ εισάγετε ένα κωδικό χρήστη",
				equalTo: "Παρακαλώ εισάγετε τον ίδιο κωδικό"
			},
			email: {
				required: "Παρακαλώ εισάγετε μια έγκυρη διεύθυνση email",
				email: "Η διεύθυνση email που εισάγατε δεν είναι έγκυρη"
			}
		},
		focusInvalid: true,
		focusCleanup: true,
		submitHandler: function (form) {
			var vname = $("#name").val();
			var vuname = $("#uname").val();
			var vpasswd = $("#passwd").val();
			var vemail = $("#email").val();
			
			$.ajax({
				type: 'POST',
				url : '/user/',
				dataType: 'json',
				data : {
					name: vname,
					uname: vuname ,
					passwd: vpasswd ,
					email: vemail
				},
				timeout: 15000,
				cache: false,
				success: function(data, status) {
					alert('Ο χρήστης ' + data.user.username + ' δημιουργήθηκε επιτυχώς');
					window.location.replace("main.jsp?key=" + data.user.apikey);
				},
				error: function(xhr, status, error) {
					var formError = jsonParse(xhr.responseText).error;
					alert(formError.msg);
				}
			});
			return false;
		}
	});
	
	$("#theloginform").validate({
		rules: {
			lname: {
				required: false
			},
			lpasswd: {
				required: false
			}
		},
		submitHandler: function (form) {
			$('#loginwait').css("display", "block");
			$.ajax({
				type: 'GET',
				url: '/user/' + $('#lname').val(),
				dataType: 'json',
				timeout: 15000,
				data : {
					passwd: $('#lpasswd').val()
				},
				cache: false,
				success: function(data, status) {
					$('#loginwait').css("display", "none");
					window.location.replace("main.jsp?user=" + data.user.username + "&key=" + data.user.apikey);
				},
				error: function(xhr, status, error) {
					$('#loginwait').css("display", "none");
					alert('Ο χρήστης ' + $('#lname').val() + ' δεν υπάρχει ή ο κωδικός είναι λάθος.');
					form.resetForm();
				}
			});
			return false;
		}
	});

	$("#regbut").click(function () {
		$("#name").val($("#ename").val());
		$("#uname").val($("#euname").val());
		$("#passwd").val($("#epasswd").val());
	});
	
	$("#euname").keyup (function(event) {

			$.ajax({
				type: 'GET',
				url : '/user/' + $("#euname").val(),
				dataType: 'json',
				timeout: 400,
				cache: false,
				success: function(data, status) {
					$("#euname").css("border-color", "red");
					$("#euname").css("border-width", "thick");
					$("#eunamelbl").css("color", "blue");
					$("#eunamelbl").text("Tο Όνομα Χρήστη υπάρχει");
				},
				error: function(xhr, status, error) {
					$("#euname").css("border-color", "#EEEEEE");
					$("#euname").css("border-width", "2px 2px 2px 2px");
					$("#eunamelbl").css("color", "#ffffff");
					$("#eunamelbl").text("Όνομα Χρήστη");
				}
			});
	});
});

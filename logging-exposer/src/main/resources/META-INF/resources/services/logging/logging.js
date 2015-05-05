document.rLog = {
	transactions: [],
	infoVisible: false,
	client: null,
	init: function () {
		$.get("services/logging/logging.html",
				function (data) {
					$("body").prepend(data);
					hljs.initHighlightingOnLoad();
					$("#rlog-info-clear-btn").click(function () {
						document.rLog.clearTransactionHistory();
					});
					$("#rlog-info-search-btn").click(function () {
						document.rLog.registerTransaction($('#rlog-info-search-id').val(), "Custom Search by console");
					});

					$('#rlog-server-index').val(localStorage.getItem('rLog.server-index'));
					$('#rlog-server-url').val(localStorage.getItem('rLog.server-url'));

					$("#rlog-server-save-btn").click(function () {
						localStorage.setItem('rLog.server-index', $('#rlog-server-index').val());
						localStorage.setItem('rLog.server-url', $('#rlog-server-url').val());
					});

				}
		);
	},
	registerTransaction: function (id, comment) {
		if (!localStorage.getItem('rLog.transaction-log-lines')) {
			localStorage.setItem('rLog.transaction-log-lines', JSON.stringify(new Array()));
		}
		var transactions = JSON.parse(localStorage.getItem('rLog.transaction-log-lines'));
		while (transactions.length > 100) {
			transactions.shift();
		}
		transactions.push({time: moment().format("YYYY/MM/DD HH:mm:ss.SSS"), transId: id, comment: comment});
		localStorage.setItem('rLog.transaction-log-lines', JSON.stringify(transactions));
		this.displayTransactions();
	},
	clearTransactionHistory: function () {
		if (confirm("Clear history?")) {
			localStorage.setItem('rLog.transaction-log-lines', JSON.stringify(new Array()));
			this.displayTransactions();
		}
	},
	displayTransactions: function () {
		if (!this.infoVisible) {
			this.hideDetail();
			return;
		}

		$("#rlog-info-table tbody").empty();
		var transactions = JSON.parse(localStorage.getItem('rLog.transaction-log-lines'));

		transactions.forEach(function (transaction) {
			var newRow = $("<tr></tr>");
			newRow.append($("<td class='rlog-row-time'></td>").text(transaction.time));
			if (!transaction.comment) {
				transaction.comment = "";
			}
			if (transaction.transId) {
				newRow.append($("<td class='rlog-row-more'></td>")
						.append($("<span class='rlog-info-show-btn'>SHOW</span>")
								.attr("log-id", transaction.transId)
								.click(document.rLog.displayDetailClick)
								)
						);
				newRow.append($("<td class='rlog-row-line'></td>").text("[" + transaction.transId + "] " + transaction.comment));
			} else {
				newRow.append($("<td class='rlog-row-more'></td>"));
				newRow.append($("<td class='rlog-row-line'></td>").text(transaction.comment));
			}
			$("#rlog-info-table tbody").prepend(newRow);
		});
	},
	displayLogLine: function (line, logId) {
		var now = moment();
		var newRow = $("<tr></tr>");
		newRow.append($("<td class='rlog-row-time'></td>").text(now.format("YYYY/MM/DD HH:mm:ss.SSS")));
		if (logId) {
			newRow.append($("<td class='rlog-row-more'><span class='rlog-info-show-btn'>SHOW</span></td>")
					.attr("log-id", logId)
					.click(this.displayDetail));
		} else {
			newRow.append($("<td class='rlog-row-more'></td>"));
		}
		newRow.append($("<td class='rlog-row-line'></td>").text(line));
//		var newRow = "<tr><td>"++"</td><td>"+line+"</td></tr>";
		$("#rlog-info-table tbody").prepend(newRow);
	},
	registerCall: function (headers, comment) {
		var logId = headers['log-transaction-uuid']; //console.log();
		this.registerTransaction(logId, comment);
	},
	displayDetailClick: function (e) {
		var logId = $(e.toElement).attr('log-id');
		document.rLog.displayDetail(logId);
	},
	displayDetail: function (logId) {
		$('#rlog-detail').fadeIn();
		$('#rlog-detail-close').click(document.rLog.hideDetail);
		$('#rlog-detail-server').text($('#rlog-server-url').val() + " | "+$('#rlog-server-index').val())
		document.rLog.client = new elasticsearch.Client({
			host: $('#rlog-server-url').val(),
			log: 'info'
		});

		document.rLog.client.search({
			index: $('#rlog-server-index').val(),
			body: {
				query: {
					match_phrase: {
						transactionId: logId
					}
				}
			}
		}, function (error, response) {
			$('#rlog-detail-content').html('<pre><code class="json">' + syntaxHighlight(response.hits.hits) + "</code></pre>");
		});
	},
	hideDetail: function () {
		$('#rlog-detail').fadeOut();
	}
};
$(document.rLog.init());

$(document).bind('keydown', null, function (e) {
	if (e.which == 222) {
		document.rLog.infoVisible = !$('#rlog-info').is(":visible");
		$('#rlog-info').toggle(400, "linear");
		document.rLog.displayTransactions();
		
	}
	return true;
});


function syntaxHighlight(json) {
	if (typeof json != 'string') {
		json = JSON.stringify(json, undefined, 2);
	}
	json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
	return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
		var cls = 'number';
		if (/^"/.test(match)) {
			if (/:$/.test(match)) {
				cls = 'key';
			} else {
				cls = 'string';
			}
		} else if (/true|false/.test(match)) {
			cls = 'boolean';
		} else if (/null/.test(match)) {
			cls = 'null';
		}
		return '<span class="' + cls + '">' + match + '</span>';
	});
}
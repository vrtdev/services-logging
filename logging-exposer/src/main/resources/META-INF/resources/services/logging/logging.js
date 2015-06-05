document.rLog = {
	transactions: [],
	infoVisible: false,
	client: null,
	init: function () {
		$.get("services/logging/logging.html",
				function (data) {
					$("body").prepend(data);
					$("#rlog-info-clear-btn").click(function () {
						document.rLog.clearTransactionHistory();
					});
					$("#rlog-info-close").click(function () {
						document.rLog.hideInfo();
					});

					$("#rlog-info-search-btn").click(function () {
						document.rLog.registerTransaction($('#rlog-info-search-id').val(), "Custom Search by console");
						document.rLog.displayDetail($('#rlog-info-search-id').val());
					});

					$("#rlog-detail-full").click(function () {
						$(".rlog-detail-row-json").toggle();
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
	hideInfo: function () {
		document.rLog.infoVisible = false;
		$('#rlog-info').fadeOut();
		document.rLog.displayTransactions();
	},
	displayInfo: function () {
		document.rLog.infoVisible = true;
		$('#rlog-info').fadeIn();
		document.rLog.displayTransactions();
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
		transactions.sort(function (a, b) {
			return a.date - b.date
		})
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
		var logId = headers['X-Log-Transaction-Id'] || headers['X-Log-Transaction-Id'.toLowerCase()]; //headers are loweredcase
		this.registerTransaction(logId, comment);
	},
	displayDetailClick: function (e) {
		var logId = $(e.toElement).attr('log-id');
		document.rLog.displayDetail(logId);
	},
	displayDetail: function (logId) {
		$('#rlog-detail').fadeIn();
		$('#rlog-detail-close').click(document.rLog.hideDetail);
		$('#rlog-detail-server').text("searching >> " + logId)

		if (logId == 'dilbert' || logId == 'encoding-dev.vrt.be-2816c322-ec93-4b96-bf41-aea5cc3bf1aa') {
			$('#rlog-detail-content').empty();
			$('#rlog-detail-content').append($("<div style='text-align:center'><img src='services/logging/dilbert.gif'></img></div>"));
		} else {
			document.rLog.client = new elasticsearch.Client({
				host: $('#rlog-server-url').val(),
				log: 'info'
			});

			document.rLog.client.search({
				index: $('#rlog-server-index').val(),
				body: {
					query: {
						bool: {
							should: [
								{
									match_phrase: {
										transactionId: logId
									}
								},
								{
									match_phrase: {
										flowId: logId
									}
								}
							]
						}
					}
				}
			}, function (error, response) {
				document.rLog.displayResults(response.hits.hits);
			});
		}


	},
	hideDetail: function () {
		$('#rlog-detail').fadeOut();
	},
	displayResults: function (response) {
		$('#rlog-detail-content').empty();
		$('#rlog-detail-more-searches-btn').empty();
		
		response.sort(function (a, b) {
			if (a._source.transactionId != b._source.transactionId) {
				return a._source.date - b._source.date;
			}
			if (a._source.breadCrum != b._source.breadCrum) {
				return a._source.breadCrum - b._source.breadCrum
			}
			return a._source.date - b._source.date;
		});
		var table = $("<table class='log-table-detail-result'></table>");
		$('#rlog-detail-content').append(table);
		var moreSearch = {};

		response.forEach(function (hit) {
			var src = hit._source;
			moreSearch[src.flowId] = $("<span class='rlog-btn' val='" + src.flowId + "'>" + src.flowId + "</span>");
			moreSearch[src.transactionId] = $("<span class='rlog-btn' val='" + src.transactionId + "'>" + src.transactionId + "</span>");

			var newRow = $("<tr class='summary'></tr>");
			newRow.append($("<td class='rlog-row-time'></td>").text(moment(src.date).format("HH:mm:ss.SSS")));
			newRow.append($("<td class='logr-logLine logr-logLine-" + src.logLevel + "'></td>").text(src.logLevel));
			newRow.append($("<td></td>").text(src.loggerName.split(".").pop()));

			var arrows = "";
			for (var i = 0; i < src.breadCrum; i++) {
				arrows += "&#10149;"
			}
			;
			newRow.append($("<td></td>").html(arrows));

			var moreInfo = "";

			newRow.click(function () {
				$(this).next().toggle()
			});

			if (src.content.HttpTransactionLogDto) {
				var transLog = src.content.HttpTransactionLogDto;
				moreInfo = transLog.httpMethod + " [" + transLog.responseStatus + "] " + transLog.resource;
				newRow.append($("<td></td>").text(moreInfo));
				var newRowDetail = $("<tr class='rlog-detail-row-json'></tr>");
				newRowDetail.append($("<td colspan='4'></td> "));
				newRowDetail.append($("<td ><pre>" + syntaxHighlight(src) + "</pre></td>"));
				table.prepend(newRowDetail);
				table.prepend(newRow);
			} else if (src.content.AuditLogDto) {
				var auditLog = src.content.AuditLogDto;
				moreInfo = " [" + auditLog.auditLevel + "] " + auditLog.method;
				newRow.append($("<td ></td>").text(moreInfo));
				var newRowDetail = $("<tr class='rlog-detail-row-json'></tr>");
				newRowDetail.append($("<td colspan='4'></td> "));
				newRowDetail.append($("<td colspan='2'><pre>" + syntaxHighlight(src) + "</pre></td>"));
				table.append(newRow);
				table.append(newRowDetail);
			} else if (src.content.String) {
				newRow.append($("<td></td>").text(src.content.String.subString(0, 400)));
				var newRowDetail = $("<tr class='rlog-detail-row-json'></tr>");
				newRowDetail.append($("<td colspan='4'></td> "));
				newRowDetail.append($("<td colspan='2'><pre>" + syntaxHighlight(src) + "</pre></td>"));
				table.append(newRow);
				table.append(newRowDetail);
			} else {
				newRow.append($("<td></td>").text(src.logComment));
				var newRowDetail = $("<tr class='rlog-detail-row-json'></tr>");
				newRowDetail.append($("<td colspan='4'></td> "));
				newRowDetail.append($("<td colspan='2'><pre>" + syntaxHighlight(src) + "</pre></td>"));
				table.append(newRow);
				table.append(newRowDetail);
			}
		});
		for (var property in moreSearch) {
			$('#rlog-detail-more-searches-btn').append(moreSearch[property]);
			$('#rlog-detail-more-searches-btn').append(" ");
			moreSearch[property].click(function () {
				document.rLog.displayDetail($(this).attr('val'));
			})
		}
//		$('#rlog-detail-content').append('<pre><code class="json">' + syntaxHighlight(response) + "</code></pre>");
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
var logBaseUrl = logBaseUrl || 'log';

if (!document.rLog) {
	document.rLog = {
		baseUrl: logBaseUrl,
		transactions: [],
		infoVisible: false,
		resultSize: 500,
		client: null,
		init: function () {
			$.get("services/logging/logging.html",
					function (data) {
						$("body").append(data);
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

						$("#rlog-info-search-txt-btn").click(function () {
							document.rLog.displayText($('#rlog-info-search-id').val());
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
		}
		, tabs: {
			display: function (tab) {
				$(".rlog-tabs span").removeClass("selected");
				$(".rlog-tabs span[tab-header='" + tab.name + "']").addClass("selected");
				$(".rlog-tab").removeClass("rlog-tab-selected");
				$("#rlog-tab-" + tab.name).addClass("rlog-tab-selected");
				tab.refresh();
			}

			, tab_history: {
				name: 'history',
				register: function (id, comment) {
					if (!localStorage.getItem('rLog.transaction-log-lines')) {
						localStorage.setItem('rLog.transaction-log-lines', JSON.stringify(new Array()));
					}
					var transactions = JSON.parse(localStorage.getItem('rLog.transaction-log-lines'));
					while (transactions.length > 100) {
						transactions.pop();
					}
					transactions.unshift({time: moment().format("YYYY/MM/DD HH:mm:ss.SSS"), transId: id, comment: comment});
					localStorage.setItem('rLog.transaction-log-lines', JSON.stringify(transactions));
					this.displayTransactions();
				}
				, clear: function () {
					if (confirm("Clear history?")) {
						localStorage.setItem('rLog.transaction-log-lines', JSON.stringify(new Array()));
						this.displayTransactions();
					}
				}
				, refresh: function () {
					$("#rlog-tab-history table tbody").empty();
					var transactions = JSON.parse(localStorage.getItem('rLog.transaction-log-lines'));
					if (!transactions) {
						return
					}
					transactions.sort(function (a, b) {
						return moment(a.time, "YYYY/MM/DD HH:mm:ss.SSS").diff(moment(b.time, "YYYY/MM/DD HH:mm:ss.SSS"));
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
						$("#rlog-tab-history table tbody").prepend(newRow);
					});
				}
				, display: function () {
					document.rLog.tabs.display(this);

				}
			}
			, tab_request: {
				name: 'request'
				, data: []
				, refresh: function () {
					$.ajax(document.rLog.baseUrl + "/list")
							.done(function (data) {
								document.rLog.tabs.tab_request.data = data.logs;
								document.rLog.tabs.tab_request.data.sort(function (a, b) {
									return a.startDate - b.startDate;
								});
								$("#rlog-tab-request table tbody").empty();
								document.rLog.tabs.tab_request.data.forEach(function (request) {
									var newRow = createRequestRow(request);
									$("#rlog-tab-request table tbody").prepend(newRow);
								})

							});
				}
				, display: function () {
					document.rLog.tabs.display(this);
				}
			}
			, tab_failure: {
				name: 'failure'
				, data: []
				, refresh: function () {
					$.ajax(document.rLog.baseUrl + "/fail")
							.done(function (data) {
								document.rLog.tabs.tab_failure.data = data.logs;
								document.rLog.tabs.tab_failure.data.sort(function (a, b) {
									return a.startDate - b.startDate;
								});
								$("#rlog-tab-failure table tbody").empty();
								document.rLog.tabs.tab_failure.data.forEach(function (request) {
									var newRow = createRequestRow(request);
									$("#rlog-tab-failure table tbody").prepend(newRow);
								})

							});
				}
				, display: function () {
					document.rLog.tabs.display(this);
				}
			}
			, tab_error: {
				name: 'error'
				, data: []
				, refresh: function () {
					$.ajax(document.rLog.baseUrl + "/error")
							.done(function (data) {
								document.rLog.tabs.tab_failure.data = data.logs;
								document.rLog.tabs.tab_failure.data.sort(function (a, b) {
									return a.startDate - b.startDate;
								});
								$("#rlog-tab-error table tbody").empty();
								document.rLog.tabs.tab_failure.data.forEach(function (request) {
									var newRow = createRequestRow(request);
									$("#rlog-tab-error table tbody").prepend(newRow);
								})

							});
				}
				, display: function () {
					document.rLog.tabs.display(this);
				}
			}
		}
		, registerTransaction: function (id, comment) {
			if (!localStorage.getItem('rLog.transaction-log-lines')) {
				localStorage.setItem('rLog.transaction-log-lines', JSON.stringify(new Array()));
			}
			var transactions = JSON.parse(localStorage.getItem('rLog.transaction-log-lines'));
			while (transactions.length > 100) {
				transactions.pop();
			}
			transactions.unshift({time: moment().format("YYYY/MM/DD HH:mm:ss.SSS"), transId: id, comment: comment});
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

			$("#rlog-tab-history table tbody").empty();
			var transactions = JSON.parse(localStorage.getItem('rLog.transaction-log-lines'));
			if (!transactions) {
				return
			}
			transactions.sort(function (a, b) {
				return moment(a.time, "YYYY/MM/DD HH:mm:ss.SSS").diff(moment(b.time, "YYYY/MM/DD HH:mm:ss.SSS"));
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
				$("#rlog-tab-history table tbody").prepend(newRow);
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
			$("#rlog-tab-history table tbody").prepend(newRow);
		},
		registerCall: function (headers, comment) {
			var logId = headers['X-Log-Transaction-Id'] || headers['X-Log-Transaction-Id'.toLowerCase()]; //headers are loweredcase
			this.registerTransaction(logId, comment);
		},
		displayDetailClick: function (e) {
			var logId = $(e.toElement || e.relatedTarget || e.target).attr('log-id');
			document.rLog.displayDetail(logId);
		},
		displayText: function (text) {
			this.displayDetail(text, {
				match: {
					_all: text
				}
			});
		},
		displayDetail: function (logId, query) {
			$('#rlog-detail').fadeIn();
			$('#rlog-detail-close').click(document.rLog.hideDetail);
			$('#rlog-detail-server').text("searching >> " + logId)

			$.ajax(this.baseUrl + '/transaction/' + logId)
					.done(function (data) {
						document.rLog.displayResults(data.hits);
					});
		},
		hideDetail: function () {
			$('#rlog-detail').fadeOut();
		},
		displayResults: function (hits) {
			var response = hits.hits;
			$('#rlog-detail-content').empty();
			$('#rlog-detail-more-searches-btn').empty();

			var transactions = {};
			for (var h = 0; h < response.length; h++) {
				var src = response[h]._source;
				if (!transactions[src.transactionId] || transactions[src.transactionId] > src.date) {
					transactions[src.transactionId] = src.date;
				}
			}

			response.sort(function (a, b) {
				if (a._source.transactionId != b._source.transactionId) {
					return transactions[a._source.transactionId] - transactions[b._source.transactionId];
				}
				if ((a._source.date - b._source.date) == 0) {
					return (a._source.breadCrum || a._source.breadCrumb) - (b._source.breadCrum || b._source.breadCrumb)
				}
				return a._source.date - b._source.date;
			});
			var table = $("<table class='log-table-detail-result'></table>");
			$('#rlog-detail-content').append(table);
			var moreSearch = {
				flow: {},
				transction: {}
			};

			var transId = "";
			for (var h = 0; h < response.length; h++) {
				var hit = response[h];

				var src = hit._source;
				if (src.transactionId != transId) {
					transId = src.transactionId;
					table.append($("<tr class='log-transaction'><td colspan='26'>" + src.transactionId + "</td></tr>"));
				}
				if (src.flowId) {
					moreSearch.flow[src.flowId] = $("<span class='rlog-btn rlog-info-show-btn' val='" + src.flowId + "'>" + src.flowId + "</span>");
				}
				if (src.transactionId) {
					moreSearch.transction[src.transactionId] = $("<span class='rlog-btn' val='" + src.transactionId + "'>" + src.transactionId + "</span>");
				}
				var newRow = $("<tr class='summary'></tr>");
				newRow.append($("<td class='rlog-row-time'></td>").text(moment(src.date).format("MM/DD HH:mm:ss.SSS")));
				newRow.append($("<td class='logr-logLine logr-logLine-" + src.logLevel + "'></td>").text(src.logLevel));
				var breadcrumbs = src.breadCrumb || src.breadCrum;
				var i = 0;
				for (; i < breadcrumbs; i++) {
					newRow.append($("<td>" + (i == breadcrumbs - 1 ? "&#10149;" : "") + "</td>"));
				}
				newRow.append($("<td colspan='" + (20 - i) + "'></td>").html(src.loggerName.split(".").pop()));
				var moreInfo = "";

				newRow.click(function () {
					$(this).next().toggle()
				});
				if (!src.content) {
					newRow.append($("<td></td>").text(src.logComment));
					var newRowDetail = $("<tr class='rlog-detail-row-json'></tr>");
					newRowDetail.append($("<td colspan='4'></td> "));
					newRowDetail.append($("<td colspan='22'><pre>" + syntaxHighlight(src) + "</pre></td>"));
					table.append(newRow);
					table.append(newRowDetail);
				} else if (src.content.HttpTransactionLogDto) {
					var transLog = src.content.HttpTransactionLogDto;
					moreInfo = transLog.httpMethod + " [" + transLog.responseStatus + "] " + transLog.resource;
					newRow.append($("<td></td>").text(moreInfo));
					var newRowDetail = $("<tr class='rlog-detail-row-json'></tr>");
					newRowDetail.append($("<td colspan='4'></td> "));
					newRowDetail.append($("<td ><pre>" + syntaxHighlight(src) + "</pre></td>"));
					table.prepend(newRowDetail);
					table.prepend(newRow);
//				} else if (src.content.AuditLogDto) {
//					var auditLog = src.content.AuditLogDto;
//					moreInfo = " [" + auditLog.auditLevel + "] " + auditLog.method;
//					newRow.append($("<td ></td>").text(moreInfo));
//					var newRowDetail = $("<tr class='rlog-detail-row-json'></tr>");
//					newRowDetail.append($("<td colspan='4'></td> "));
//					newRowDetail.append($("<td colspan='22'><pre>" + syntaxHighlight(src) + "</pre></td>"));
//					table.append(newRow);
//					table.append(newRowDetail);
				} else if (src.content.String) {
					newRow.append($("<td></td>").text(src.content.String.subString(0, 400)));
					var newRowDetail = $("<tr class='rlog-detail-row-json'></tr>");
					newRowDetail.append($("<td colspan='4'></td> "));
					newRowDetail.append($("<td colspan='22'><pre>" + syntaxHighlight(src) + "</pre></td>"));
					table.append(newRow);
					table.append(newRowDetail);
				} else {
					newRow.append($("<td></td>").text(src.logComment));
					var newRowDetail = $("<tr class='rlog-detail-row-json'></tr>");
					newRowDetail.append($("<td colspan='4'></td> "));
					newRowDetail.append($("<td colspan='22'><pre>" + syntaxHighlight(src) + "</pre></td>"));
					table.append(newRow);
					table.append(newRowDetail);
				}
			}

			for (var property in moreSearch.flow) {
				$('#rlog-detail-more-searches-btn').append(moreSearch.flow[property]);
				$('#rlog-detail-more-searches-btn').append(" ");
				moreSearch.flow[property].click(function () {
					document.rLog.displayDetail($(this).attr('val'));
				})
			}
			for (var property in moreSearch.transction) {
				$('#rlog-detail-more-searches-btn').append(moreSearch.transction[property]);
				$('#rlog-detail-more-searches-btn').append(" ");
				moreSearch.transction[property].click(function () {
					document.rLog.displayDetail($(this).attr('val'));
				})
			}
//		$('#rlog-detail-content').append('<pre><code class="json">' + syntaxHighlight(response) + "</code></pre>");
		},
		toggleInfoWindow: function () {
			document.rLog.infoVisible = !$('#rlog-info').is(":visible");
			$('#rlog-info').toggle(400, "linear");
			document.rLog.displayTransactions();
		}
	};

	$(document).ready(function () {
		$(document.rLog.init());

		$(document).bind('keydown', null, function (e) {
			if (e.which == 222 || e.key == '²') {
				document.rLog.toggleInfoWindow();
				return false;
			}
			return true;
		});
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
	function createRequestRow(request) {
		var newRow = $("<tr></tr>");
		newRow.append($("<td class='rlog-row-time'></td>").text(moment(request.startDate).format("YYYY/MM/DD HH:mm:ss.SSS")));
		if (!request.resource) {
			request.resource = "";
		}
		newRow.append($("<td class='rlog-row-more'></td>")
				.append($("<span class='rlog-info-show-btn'>SHOW</span>")
						.attr("log-id", request.transactionId)
						.click(document.rLog.displayDetailClick)
						)
				);
		if (request.httpMethod) {
			newRow.append($("<td class='rlog-logTransaction rlog-logTransaction-" + request.status + "'></td>").text(request.status));
			newRow.append($("<td class='rlog-small-cell'>" + request.httpMethod + "</td>"));
			newRow.append($("<td class='rlog-small-cell'>" + request.responseStatus + "</td>"));
			newRow.append($("<td class='rlog-row-line'></td>").text("[" + request.transactionId + "] " + request.resource));
		} else {
			newRow.append($("<td class='rlog-logTransaction rlog-logTransaction-" + request.status + "'></td>").text(request.status));
			newRow.append($("<td class='rlog-small-cell'>" + request.headers['amqp-method'] + "</td>"));
			newRow.append($("<td class='rlog-small-cell'>AMQP</td>"));
			newRow.append($("<td class='rlog-row-line'></td>").text("[" + request.transactionId + "] " + request['amqp-url'] + "" + request.routingKey));
		}


		return newRow;
	}
}

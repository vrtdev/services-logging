document.rLog = {
	transactions: [],
	client: null,
	init: function () {
		$.get("services/logging/logging.html",
				function (data) {
					$("body").append(data);
					hljs.initHighlightingOnLoad();
					//$('#rlog-info').text("Whoehoe");
				}
		);
	},
	registerCall: function (headers) {
		var logId = headers['log-transaction-uuid']; //console.log();
		this.transactions.push(logId);
		var p = $("<p>");
		p.attr("log-id", logId);
		p.text(new Date() + " : " + logId);
		p.click(this.displayDetail);
		$('#rlog-info').append(p);
	},
	displayDetail: function (e) {
		$('#rlog-detail').show();

		$('#rlog-detail-close').click(document.rLog.hideDetail);
		var logId = $(e.toElement).attr('log-id');
//		var str = JSON.stringify(obj, null, 2);
//		this.client.search({
//			q: 'pants'
//		}).then(function (body) {
//			var hits = body.hits.hits;
//		}, function (error) {
//			console.trace(error.message);
//		});
		document.rLog.client = new elasticsearch.Client({
			host: $('#log-server-url').val(),
			log: 'info'
		});

		document.rLog.client.search({
			index: $('#log-server-index').val(),
			body: {
				query: {
					match_phrase: {
						transactionId: logId
					}
				}
			}
		}, function (error, response) {
			$('#rlog-detail-content').html('<pre><code class="json">' + JSON.stringify(response.hits.hits, undefined, 2) + "</code></pre>");
		});



	},
	hideDetail: function () {
		$('#rlog-detail').hide();
	}
};
$(document.rLog.init());

$(document).bind('keydown', null, function (e) {
	if (e.which == 222) {
		$('#rlog-info').toggle(400, "linear");
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
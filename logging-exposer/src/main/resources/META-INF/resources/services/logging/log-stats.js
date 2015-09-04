//			var chart, chartDetail;
document.logInfo = {
	statsUrl: "log/stats/",
	knownHost: function (name, info) {
		if (info && info.hosts) {
			return info.hosts[name];
		}
		return name;
	}
}

document.logDisplay = {
	dateBar: {
		display: function (divId) {
			var dateBar = $("<ul class='nav nav-pills'/>");
			$("#" + divId).append(dateBar);
			var date = new Date();
			for (var i = 0; i < 10; i++) {
				var fDate = this.formatDate(date);
				$("<li style='margin-left:15px' class='btn btn-primary' onclick=\"loadDate('" + fDate + "')\">" + fDate + "</li>").appendTo(dateBar);
				date.setTime(date.getTime() - 1000 * 60 * 60 * 24)
			}
			$("<li style='margin-left:15px'>"
//								+ "<div class='form-group'>"
//								+ "<label class='control-label'>Input addons</label>"
					+ "<div class='input-group' style='position:absolute'>"
					+ "    <input type='date' class='form-control' id='log_load_date_txt'>"
					+ "    <span class='input-group-btn'>"
					+ "      <button class='btn btn-default' type='button' id='log_load_date_btn'>Load Data</button>"
					+ "    </span>"
					+ "  </div>"
//								+ "</div>"
					+ "</li>").appendTo(dateBar);
			$("#log_load_date_btn").click(function () {
				if ($("#log_load_date_txt").val()) {
					loadDate($("#log_load_date_txt").val());
				}
			})

		},
		formatDate: function (date) {
			var year = date.getFullYear();
			var month = date.getMonth() + 1;
			var day = date.getDate();
			return year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;
		}
	},
	failurePanel: function (date, containerId) {
		$("#" + containerId).append('<div class="panel panel-warning"><div class="panel-heading"><h3 class="panel-title"><button type="button" class="btn btn-warning btn-xs btn-open">+</button><button type="button" class="btn btn-warning btn-xs btn-close">-</button> [' + date + '] Failure Listing  <span class="badge">0</span></h3></div><div class="panel-body"><div id="logStatsFails" /></div></div>');
		$("#logStatsFails").parent().parent().hide();
		$("#logStatsFails").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-close").hide();
		$.get(document.logInfo.statsUrl + "failures/" + date, null, function (data) {
			if (!data.statshits.hits || data.statshits.hits.length == 0) {
				$("#logStatsFails").parent().parent().hide();
				return;
			}
			$("#logStatsFails").parent().parent().show();
			$("#logStatsFails").append("<table style='width: 100%' class='table table-hover'><tbody></tbody></table>");
			$("#logStatsFails tbody").append("<tr><th>#</th><th>Time</th><th>FlowId</th><th>Call</th><th>ErrorMessage</th></tr>");
			var i = 0;
			data.statshits.hits.forEach(function (hit) {
				var source = hit._source;
				$("#logStatsFails tbody").append("<tr/>");
				$("#logStatsFails tbody tr:last").append("<td>" + ++i + "</td>");
				$("#logStatsFails tbody tr:last").append("<td>" + Highcharts.dateFormat('%H:%M:%S', source.logDate) + "</td>");
				$("#logStatsFails tbody tr:last").append("<td onclick='document.rLog.displayInfo(\"" + source.flowId + "\")'>" + source.flowId + "</td>");
				$("#logStatsFails tbody tr:last").append("<td>" + source.content['[4] AuditLogDto'].method + "</td>");
				$("#logStatsFails tbody tr:last").append("<td>" + source.content['[4] AuditLogDto'].response.message + "</td>");
			})
			$("#logStatsFails").parent().parent().children(".panel-heading").children(".panel-title").children(".badge").text(i);

			$("#logStatsFails").parent().parent().children(".panel-body").hide();
			$("#logStatsFails").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-open").click(function () {
				$("#logStatsFails").parent().parent().children(".panel-body").show();
				$("#logStatsFails").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-open").hide();
				$("#logStatsFails").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-close").show();
			});
			$("#logStatsFails").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-close").click(function () {
				$("#logStatsFails").parent().parent().children(".panel-body").hide();
				$("#logStatsFails").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-close").hide();
				$("#logStatsFails").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-open").show();
			});
		});
	},
	errorPanel: function (date, containerId) {
		$("#" + containerId).append('<div class="panel panel-danger"><div class="panel-heading"><h3 class="panel-title"><button type="button" class="btn btn-danger btn-xs btn-open">+</button><button type="button" class="btn btn-danger btn-xs btn-close">-</button> [' + date + '] Error Listing  <span class="badge">0</span></h3></div><div class="panel-body"><div id="logStatsErrors" /></div></div>');
		$("#logStatsErrors").parent().parent().hide();
		$("#logStatsErrors").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-close").hide();


		$.get(document.logInfo.statsUrl + "errors/" + date, null, function (data) {
			if (!data.statshits.hits || data.statshits.hits.length == 0) {
				return;
			}
			$("#logStatsErrors").parent().parent().show();

			$("#logStatsErrors").append("<table style='width: 100%' class='table table-hover'><tbody></tbody></table>");
			$("#logStatsErrors tbody").append("<tr class='error'><th>#</th><th>Time</th><th>FlowId</th><th>Call</th><th>ErrorMessage</th></tr>");
			var i = 0;
			data.statshits.hits.forEach(function (hit) {
				var source = hit._source;
				$("#logStatsErrors tbody").append("<tr/>");
				$("#logStatsErrors tbody tr:last").append("<td>" + ++i + "</td>");
				$("#logStatsErrors tbody tr:last").append("<td>" + Highcharts.dateFormat('%H:%M:%S', source.logDate) + "</td>");
				$("#logStatsErrors tbody tr:last").append("<td onclick='document.rLog.displayInfo(\"" + source.flowId + "\")'>" + source.flowId + "</td>");
				$("#logStatsErrors tbody tr:last").append("<td>" + source.content['[4] AuditLogDto'].method + "</td>");
				$("#logStatsErrors tbody tr:last").append("<td>" + source.content['[4] AuditLogDto'].response.message + "</td>");
			})
			$("#logStatsErrors").parent().parent().children(".panel-heading").children(".panel-title").children(".badge").text(i);

			$("#logStatsErrors").parent().parent().children(".panel-body").hide();
			$("#logStatsErrors").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-open").click(function () {
				$("#logStatsErrors").parent().parent().children(".panel-body").show();
				$("#logStatsErrors").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-open").hide();
				$("#logStatsErrors").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-close").show();
			});
			$("#logStatsErrors").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-close").click(function () {
				$("#logStatsErrors").parent().parent().children(".panel-body").hide();
				$("#logStatsErrors").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-close").hide();
				$("#logStatsErrors").parent().parent().children(".panel-heading").children(".panel-title").children(".btn-open").show();
			});

		});
	}

}
document.logChart = {
	hosts: {
		displayData: function (data, info) {
			this.chartData = {
				labels: [],
				datasets: [],
				drilldown: {
					series: []
				},
				dataset_map: {}
			};

			data.time.buckets.forEach(function (time, i, array) {
				var chartData = document.logChart.hosts.chartData;
				chartData.labels.push(new Date(time.key_as_string));
				time.hosts.buckets.forEach(function (host, index) {
					host.rel_key = document.logInfo.knownHost(host.key, info);
					if (!host.rel_key) {
						return;
					}

					// Initialize data set
					if (!chartData.dataset_map[host.rel_key ]) {
						chartData.dataset_map[host.rel_key ] = {
							name: host.rel_key,
							data: []
						};
						for (var j = 0; j < array.length; j++) {
							chartData.dataset_map[host.rel_key ].data.push({y: 0, info: [], errors: 0, failures: 0});
						}
						;
						chartData.datasets.push(chartData.dataset_map[host.rel_key]);
					}
					var detailData = chartData.dataset_map[host.rel_key].data[i];
					// Set data in dataset.
					detailData.y += host.doc_count;
					host.chart_info = info;
					detailData.info.push(host);

					host.methods.buckets.forEach(function (m) {
						m.Status.buckets.forEach(function (s) {
							if (s.key === 'ERROR') {
								detailData.errors += s.doc_count;
							}
							if (s.key === 'FAIL') {
								detailData.failures += s.doc_count;
							}
						});
					});
					if (detailData.failures > 0) {
						detailData.color = "orange";
					}
					if (detailData.errors > 0) {
						detailData.color = "red";
					}

				});
			});

			if (this.chartData.datasets.length == 0) {
				return;
			}

			if ($("#" + info.location.id).length === 0) {
				$("#" + info.location.parent).append("<hr>");
				$("#" + info.location.parent).append("<div id='" + info.location.id + "'/>");
				$("#" + info.location.parent).append("<div id='" + info.location.id + "_details'/>");
			}

			chart = new Highcharts.Chart({
				chart: {
					renderTo: info.location.id,
					type: 'column'
				},
				title: {
					text: info.location.id + ' -  [' + info.date + '] Calls / Hour'
				},
				xAxis: {
					categories: document.logChart.hosts.chartData.labels,
					type: 'datetime',
					labels: {
						format: '{value:%H:%M}',
					}

				},
				yAxis: {
					min: 0,
					title: {
						text: '#Calls'
					},
					stackLabels: {
						enabled: true,
						style: {
							fontWeight: 'bold',
							color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
						}
					}
				},
				legend: {
					align: 'right',
					x: -30,
					verticalAlign: 'top',
					y: 25,
					floating: true,
					backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
					borderColor: '#CCC',
					borderWidth: 1,
					shadow: false
				},
				tooltip: {
					formatter: function () {
						var rtnValue = '';
						rtnValue += '<table class="log-tooltip">';
						rtnValue += '<tr><th><span class="log-tooltip-serie">' + Highcharts.dateFormat('%H:%M', this.x) + '</span></th><th>' + this.series.name + '</th><td>' + this.y + '</td></tr>';
						if (this.point.info) {
							rtnValue += '<tr><td colspan="3"><hr></td></tr>';
							var data = this.point.info;
							data.forEach(function (detail) {
								rtnValue += '<tr><td colspan="2">' + detail.key + '</td><td>' + detail.doc_count + '</td></tr>';
							});
							if (this.point.failures + this.point.errors > 0) {
								rtnValue += '<tr><td colspan="3"><hr></td></tr>';
							}
							if (this.point.failures > 0) {
								rtnValue += '<tr><td colspan="2">FAIL</td><td>' + this.point.failures + '</td></tr>';
							}
							if (this.point.errors > 0) {
								rtnValue += '<tr><td colspan="2">ERROR</td><td>' + this.point.errors + '</td></tr>';
							}

						}
						rtnValue += '</table>';
						return rtnValue;
					},
					useHTML: true
				},
				plotOptions: {
					column: {
						stacking: 'normal',
						dataLabels: {
							enabled: true,
							color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
							style: {
								textShadow: '0 0 3px black'
							}
						},
						point: {
							events: {
								click: function () {
									document.logChart.hosts.detailPieChart(this.info);
								}
							}
						}
					}
				},
				series: document.logChart.hosts.chartData.datasets

			});
		},
		detailPieChart: function (info) {
			if (!(info && info[0])) {
				return;
			}
			var chart_info = info[0].chart_info;
			var callMethods = {};
			info.forEach(function (host) {
				host.methods.buckets.forEach(function (method) {
					if (callMethods[method.key]) {
						callMethods[method.key].count += method.doc_count;
					} else {
						callMethods[method.key] = {
							key: method.key,
							count: method.doc_count
						};
					}
				})
			});
			var data = []
			for (var property in callMethods) {
				var method = callMethods[property];
				if (method.count) {
					data.push({y: method.count, name: method.key})

				}
			}
			$("#" + chart_info.location.id + "_details").empty();
			$("#" + chart_info.location.id + "_details").append("<div id='" + chart_info.location.id + "_detail_calls' >")
			$("#" + chart_info.location.id + "_detail_calls").highcharts({
				chart: {
					plotBackgroundColor: null,
					plotBorderWidth: null,
					plotShadow: false,
					type: 'pie'
				},
				title: {
					text: 'Calls'
				},
				tooltip: {
					pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
				},
				plotOptions: {
					pie: {
						allowPointSelect: true,
						showInLegend: true,
						cursor: 'pointer',
						dataLabels: {
							enabled: true,
							format: '<b>{point.name}</b>: {point.y} ({point.percentage:.1f}%)',
							style: {
								color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
							}
						}
					}
				},
				legend: {
					layout: "vertical",
					align: "right",
					verticalAlign: 'middle',
					labelFormatter: function () {
						return '<b>' + this.name + '</b>: ' + this.y
					},
				},
				series: [{
						name: "Calls",
						colorByPoint: true,
						data: data
					}]
			});
			$("#" + chart_info.location.id + "_details").append("<div id='" + chart_info.location.id + "_detail_table' >");

			$("#" + chart_info.location.id + "_detail_table").append();

		}
	}
}

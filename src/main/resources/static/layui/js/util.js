(function () {
	var layer;
	layui.use('layer', function () {
		layer = layui.layer;
	});

	var util = {};
	window.util = util;

		util.ajaxLoading = function (e) {
		/*var i;*/
		$.ajax({
			type: e.type || 'POST',
			url: e.url || '',
			dataType: e.dataType || "json",
			contentType: e.contentType || "application/json",
			data: JSON.stringify(e.data || {}),
			/*beforeSend: function () {
				i = layer.msg('加载中...', {
						icon: 16,
						shade: [0.5, '#f5f5f5'],
						scrollbar: false,
						offset: '0px',
						time: 100000
					});
			},*/
			error: function (data) {
				/*layer.close(i);*/
			},
			success: function (data) {
				/*layer.close(i);*/
				e.success(data);
			}
		});
	}

})(layui, $);

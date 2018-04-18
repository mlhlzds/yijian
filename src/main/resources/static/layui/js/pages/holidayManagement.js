function queryHoliday() {
	
	layui.use('table', function() {
		
		var table = layui.table;
		var username = $('#username').val();
		var date1 = $('#date1').val();
        var date2 = $('#date2').val();
		
		// 方法级渲染
		table.render({
			elem : '#demo',
			method : 'post',
			url : '/user/queryHoliday',
			where : {
                username : username,
				date1 : date1,
                date2 : date2
			},
			width : 816,
			cols : [[ // 标题栏
			{
				field : 'userName',
				title : '姓名',
				width : 120
			}, {
				field : 'hdate1',
				title : '请假起始日期',
				width : 150,
				unresize : true
			} , {
				field : 'hdate2',
				title : '请假结束日期',
				width : 300,
				unresize : true
			} , {
				title : '操作',
				width : 120,
				fixed: 'right', 
				align:'center',
				rowspan:2,
				toolbar: '#barDemo',
				unresize : true
			}
			]] ,
			response : {
				statusCode : 100
			// 成功的状态码，默认：0
			},
			id : 'id',
			skin : 'row', // 表格风格
			even : true,
			// ,size: 'lg' //尺寸
			page : true, // 是否显示分页
			limits : [ 10, 20, 50 ],
			limit : 10 ,// 每页默认显示的数量
			loading : true
		// 请求数据时，是否显示loading
		// ,id: 'test' //ID
		});
		table.on('tool(test)', function(obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
			
			var data = obj.data; //获得当前行数据
			var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
			if (layEvent === 'del') { //删除
				layer.confirm('确定删除该条请假记录吗?', function(index) {
					$.ajax({
						type : "POST",
						url : "/user/deleteHoliday",//访问路径
						dataType : "json",
						contentType : "application/json",
						data : JSON.stringify(data),
						success : function(ret) {
							var code = ret.code;
							var msg = ret.msg;
							if (code == 100) {
								layer.msg(ret.msg);
								obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
								layer.close(index);
							} else {
								layer.msg(ret.msg);
								layer.close(index);
							}
						},
						error : function(ret) {
							layer.msg("删除失败");
							layer.close(index);
							}
						});
					
					//向服务端发送删除指令
				});
			}
            if (layEvent === 'upd') { //修改
                layer.open({
                    type : 2
                    ,title : '修改请假记录'
                    ,area : [ '900px', '500px' ]
                    ,fix:false
                    ,shadeClose:true
                    ,shade:0.4
                    ,skin : 'layui-layer-rim' //加上边框
                    ,content : [ '/templates/user/updateHoliday.html?hid='+ data.hid ]
                    ,end: function(){
                        queryRoles();
                    }
                });
            }

			});
		});
}
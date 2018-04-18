function queryUser() {
	$.ajax({
		type : "POST",
		url : "/user/queryRoles",//访问路径
		dataType : "json",
		contentType : "application/json",
		success : function(ret) {
			var code = ret.code;
			var msg = ret.msg;
			var data = ret.data;
			if (code == 100) {
				var options = '<option value="">请选择角色</option>';
				$.each(data, function(key, val) {
					options += '<option value="'+ val.rid +'">'+ val.roleName +'</option>';
	            }); 
				$('#roleId').html(options);
				layui.use('form', function(){
					layui.form.render('select');
				});
			} else {
				layer.msg(ret.msg);
			}
		},
		error : function(ret) {
		}
	});
	
	layui.use('table', function() {
		
		var table = layui.table;
		var username = $('#username').val();
		var roleId = $('#roleId').val();
		
		// 方法级渲染
		table.render({
			elem : '#demo',
			method : 'post',
			url : '/user/queryUser',
			where : {
				username : username,
				roleId : roleId
			},
			width : 1229,
			cols : [[ // 标题栏
            {
				field : 'userName',
				title : '姓名',
                rowspan:2,
				width : 120
			}, {
				field : 'roleName',
				title : '角色',
				rowspan:2,
				width : 120,
				unresize : true
			} , {
                    title : '状态',
                    align:'center',
                    colspan:5,
                    unresize : true
                },{
				title : '操作',
				width : 230,
				fixed: 'right', 
				align:'center',
				rowspan:2,
				toolbar: '#barDemo',
				unresize : true
			}
			],[
                {
                    field : 'holiday',
                    title : '请假',
                    align:'center',
                    width : 120,
                    toolbar: '#holidayDemo'
                }, {
                    field : 'hdate1',
                    title : '请假起始时间',
                    align:'center',
                    width : 120
                }, {
                    field : 'hdate2',
                    title : '请假结束时间',
                    align:'center',
                    width : 120
                }, {
                    field : 'leave',
                    title : '离职',
                    align:'center',
                    width : 120,
                    toolbar: '#leaveDemo'
                }, {
                    field : 'ldate',
                    title : '离职时间',
                    align:'center',
                    width : 120
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
				layer.confirm('确定删除该用户吗?', function(index) {
					$.ajax({
						type : "POST",
						url : "/user/deleteUser",//访问路径
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
			
			if (layEvent === 'reset') {
				layer.confirm('确定要重置密码吗?', function(index) {
					$.ajax({
						type : "POST",
						url : "/user/resetPassword",//访问路径
						dataType : "json",
						contentType : "application/json",
						data : JSON.stringify(data),
						success : function(ret) {
							var code = ret.code;
							var msg = ret.msg;
							if (code == 100) {
								layer.msg(ret.msg);
								layer.close(index);
							} else {
								layer.msg(ret.msg);
								layer.close(index);
							}
						},
						error : function(ret) {
							layer.msg("重置失败");
							layer.close(index);
							}
						});
				});
			}

			});
		});
}

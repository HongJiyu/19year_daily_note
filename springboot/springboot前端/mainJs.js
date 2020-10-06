$(function () {
	var model = sessionStorage.getItem("userInfo");
	var userInfo = JSON.parse(model);
	var content = "<table><tr><td>编号</td><td>用户名</td><td>密码</td><td>操作</td></tr>"
		for (var i = 0; i < userInfo.length; i++) {
			content = content + "<tr><td name='id'>" + userInfo[i].id + "</td><td name='name'>" + userInfo[i].name + "</td><td name='password'>" + userInfo[i].password + "</td><td><button onclick='deleteUser(" + userInfo[i].id + ")'>删除</button><button onclick='updateOpertate(this)'>修改</button></td></tr>";
		}
		content = content + "</table>";
	var userInfoDiv = document.getElementsByName("userInfoDiv")[0];
	userInfoDiv.innerHTML = content;
})

function deleteUser(oldId) {

	$.ajax({
		type: 'post',
		url: "http://192.168.56.1:8080/User/"+oldId,
		data: {
			"_method": "DELETE",
			
		},
		async: true,
		success: function (data) {

			if (data.flag) {
				// 获取userInfo数组
				var model = sessionStorage.getItem("userInfo");
				var userInfo = JSON.parse(model);
				// 删除已经被数据库删除的对象
				for (var i = 0; i < userInfo.length; i++) {
					if (userInfo[i].id == oldId) {
						userInfo.splice(i, 1);
						break;
					}
				}
				sessionStorage.setItem("userInfo", JSON.stringify(userInfo));
				// 界面渲染
				var allIdTds = document.getElementsByName("id");
				for (var i = 0; i < allIdTds.length; i++) {

					if (allIdTds[i].innerText == oldId) {
						var itstable = document.getElementsByTagName("table")[0];
						itstable.deleteRow(i + 1);
						break;
					}
				}
				alert("删除成功");

			} else {

				alert("删除失败");
			}
		},
		error: function () {

			alert("系统错误");
		}

	})
}
function updateOpertate(it) {
	// 添加更新行
	var tableDemo = document.getElementsByTagName("table")[0];
	var newTr = tableDemo.insertRow(tableDemo.rows.length);
	var newTd = newTr.insertCell(0);
	newTd.innerHTML = "<input type='text' name='idValue' placeholder='编号'>";
	// newTd.setAttribute("name","idValue");
	var newTd = newTr.insertCell(1);
	newTd.innerHTML = "<input type='text' name='naValue' placeholder='用户名'>";
	// newTd.setAttribute("name","naValue");
	var newTd = newTr.insertCell(2);
	newTd.innerHTML = "<input type='text' name='passValue' placeholder='密码'>";
	// newTd.setAttribute("name","passValue");
	var newTd = newTr.insertCell(3);
	newTd.innerHTML = "<button onclick='updateUser()'>更新</button>";

	//保存oldid
	var itTr = it.parentNode.parentNode;
	var oldIdTag = document.getElementsByName("oldId")[0];

	oldIdTag.value = itTr.cells[0].innerText;
}

function updateUser() {
	//获取oldid
	var oldId = document.getElementsByName("oldId")[0].value;
	var idValue = document.getElementsByName("idValue")[0].value == "" ? undefined : document.getElementsByName("idValue")[0].value;
	var naValue = document.getElementsByName("naValue")[0].value == "" ? undefined : document.getElementsByName("naValue")[0].value;
	var passValue = document.getElementsByName("passValue")[0].value == "" ? undefined : document.getElementsByName("passValue")[0].value;

	$.ajax({
		type: "post",
		url: "http://192.168.56.1:8080/User/"+oldId,
		data: {

		
			"id": idValue,
			"name": naValue,
			"password": passValue

		},
		dataType: "json",
		async: true,
		success: function (data) {
			if (data.flag) {
				alert("更新成功");
				// 获取userInfo数组
				var model = sessionStorage.getItem("userInfo");
				var userInfo = JSON.parse(model);
				// 删除已经被数据库删除的对象
				for (var i = 0; i < userInfo.length; i++) {
					if (userInfo[i].id == oldId) {
						if (idValue != undefined) {
							userInfo[i].id = idValue;
						}
						if (naValue != undefined) {
							userInfo[i].name = naValue;
						}
						if (passValue != undefined) {
							userInfo[i].password = passValue;
						}
						break;
					}
				}
				sessionStorage.setItem("userInfo", JSON.stringify(userInfo));
				location.reload(true);
			} else {
				alert("更新失败");
			}

		},
		error: function () {}

	})

}

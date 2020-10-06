


function login() {
	var name = document.getElementsByName("name")[0].value;
	var password = document.getElementsByName("password")[0].value;

	$.ajax({
		type: "GET",
		url: "http://192.168.56.1:8080/User",
		data: {
			"name": name,
			"password": password
		},
		async: true,
		dataType: "json",
		success: function (data) {
			/*sessionStorage 用于临时保存同一窗口(或标签页)的数据，在关闭窗口或标签页之后将会删除这些数据。*/

			if (data.flag) {

				alert("登陆成功");

				sessionStorage.setItem("userInfo", JSON.stringify(data.userInfo));
				window.location.href = 'main.html'
			} else {
				alert("请检查用户名和密码");
			}
		},
		error: function () {
			alert("失败");
		}

	})

}

function adminContent() {
    var getContent = editor.txt.text();
    console.log(getContent);
    $('.exampleContent').val(getContent);
    return;
}

// 删除getElementsByTagName
$(".deleteExample").on('click', function () {
    var parent = $(this).parent().parent();
    var exampleId = parent.children("td:nth-child(2)").text();
    var inform = "您确定要删除需公告ID为 " + exampleId + " 的所有信息吗？";
    if(confirm(inform) == true){
        $.ajax({
            url: "/admin/adminDeleteExample",
            data: {
                exampleId:exampleId
            },
            success: function (data) {
                alert("删除成功");
                window.location.reload();
            },
            error: function (msg) {
                alert("网络错误");
            }
        })
    }
    else {

    }
});

// 添加案例
function exampleAdmin() {
    var inform = "您确定添加一条案例信息吗？";
    if(confirm(inform) == true){
        $.ajax({
            //几个参数需要注意一下
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/admin/adminAddExample" ,//url
            data: $('#form1').serialize(),
            success: function (data) {
                console.log(data);//打印服务端返回的数据(调试用)
                if (data.code == 0) {
                    alert(data.msg);
                    window.location.href = "/admin/adminExampleList";
                }else{
                    alert(data.msg);
                    window.location.href = "/admin/adminExampleList";
                };
            },
            error : function() {
                alert("异常！");
            }
        });
    }
}

// 案例案例
function exampleModify() {
    var inform = "您确定修改一条案例信息吗？";
    if(confirm(inform) == true){
        $.ajax({
            //几个参数需要注意一下
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/admin/adminEditExample" ,//url
            data: $('#form2').serialize(),
            success: function (data) {
                console.log(data);//打印服务端返回的数据(调试用)
                if (data.code == 0) {
                    alert(data.msg);
                    window.location.href = "/admin/adminExampleList";
                }else{
                    alert(data.msg);
                    window.location.href = "/admin/adminExampleList";
                };
            },
            error : function() {
                alert("异常！");
            }
        });
    }
}
$().ready(function() {
// 在键盘按下并释放及提交后验证提交表单
    $("#reg").validate({
        rules: {
            username: {
                required: true,
                minlength: 4,
                maxlength: 8
            },
            password: {
                required: true,
                minlength: 5
            },
            confirm_password: {
                required: true,
                minlength: 5,
                equalTo: "#password"
            },
            email: {
                required: true,
                email: true,
                checkEmail:true
            }
        },
        messages: {
            username: {
                required: "请输入用户名",
                minlength: "用户名不能小于4位",
                maxlength: "用户名不能大于8位"
            },
            password: {
                required: "请输入密码",
                minlength: "密码长度不能小于5位"
            },
            confirm_password: {
                required: "请输入确认密码",
                minlength: "密码长度不能小于5位",
                equalTo: "两次密码输入不一致"
            },
            email: "请输入一个正确的邮箱"
        }
    })
    //自定义正则表达示验证方法
    $.validator.addMethod("checkEmail",function(value,element,params){
        var checkEmail = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/ ;
        return this.optional(element)||(checkEmail.test(value));
    },"*请输入一个正确的邮箱");
});
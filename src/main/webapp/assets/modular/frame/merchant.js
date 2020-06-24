$(function(){
	
	$('#thressMethod').on('click','a',function(){
		$(this).find('.selectInput').prop('checked',true).siblings('a').find('.selectInput').prop('checked',false);
	})
	
	$('#sureBtn').click(function(){
		var buyPrice = $('input:radio:checked').val();
		if(buyPrice == null && buyPrice == undefined){
			$.toast('请选择提现方式','text');
			return;
		}
		if(!$('#money').val()){
			$.toast('请输入金额','text');
			return;
		}
		var dataTxt = {
			uid:$('#uid').val(),//商户号
			paytype:buyPrice,//0支付宝，1微信，2银行卡
			price:$('#money').val()
		}
		jQuery.support.cors = true;
		$.ajax({
			type:"post",
			url:"http://192.168.101.123:9090/app/buyCoin/testPay",
			async:false,
			data:dataTxt,
			success:function(res){
				console.log(res)
			},
			error:function(xhr,type,errorThrown){
				console.log(xhr.statusText);
				console.log("错误提示了："+ xhr.status +" ");
			}
		});
	})
	
	
})


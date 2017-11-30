var laypage =null;
var layer =null;
function initPage(count){
    layui.use(['laypage', 'layer'],function(){
        laypage= layui.laypage
        layer=layui.layer;

        laypage.render({
            elem: 'pages',
            skip: true,
            count:count,//总页数,
            theme: '#fb9337',
            layout: ['prev','page','next', 'skip','count'],
            curr: function(){ //通过url获取当前页，也可以同上（pages）方式获取
                var page = location.search.match(/pageNum=(\d+)/);
                return page ? page[1] : 1;
            }(),
            jump: function(e, first){ //触发分页后的回调
                if(!first){ //一定要加此判断，否则初始时会无限刷新
                    location.href = '?pageNum='+e.curr;
                }
            }
        });

    })
}
function initForm(url,formObject){
    layui.use('form', function(){
        var form = layui.form;

        $(formObject==undefined?".layui-form":formObject).submit(function(){
            $.post(url,$(formObject==undefined?".layui-form":formObject).serializeArray(),function(data){
                layer.msg(data.reMsg);
                if(data.reCode==1){

                    window.setTimeout(function(){
                        top.location.href=window.parent.location.href;
                    },500)
                }

            });
            return false;
        });
        //各种基于事件的操作，下面会有进一步介绍
    });
}
function openDialog(width,height,title,url){
    layer.open({
        type: 2,
        area: [width,height],
        title:title,
        content:url //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
    });
}

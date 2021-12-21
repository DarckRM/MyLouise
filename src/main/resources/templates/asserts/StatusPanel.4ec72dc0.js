import{_ as e}from "./WebSocket.3eb85bf0.js";import{H as t}from "./HelpCircleOutline.8866f4f7.js";import{d as l,b as i,o as a,e as s,g as n,f as o,w as r,h as u,aa as f,ab as p}from "./vendor.3140252b.js";const d=l({name:"StatusPanel",components:{WebSocket:e,HelpIcon:t},mounted(){this.nowTimes()},unmounted(){console.log("清除计时器"),this.clear()},data:()=>({nowTime:"",louiseStatus:"error",louiseText:"未知"}),methods:{nowTimes(){this.nowTime=this.$refs.webSocket.louiseBootTime,this.$refs.webSocket.isConn?(this.louiseStatus="success",this.louiseText="运行良好"):(this.louiseStatus="error",this.louiseText="停机中"),setInterval(this.nowTimes,5e3),this.clear()},clear(){this.nowTimes=null,clearInterval(this.nowTimes)}}}),m=u(" 系统总览 "),x=n("span",{style:{"font-weight":"200","font-size":"16px"}},"(System overview)",-1),y=n("p",null,"此页面展示系统的一些总结性数据",-1),_=u("基于Mirai以及OneBot标准实现的qq机器人"),c=u("运行时间"),h=u("运行中..."),g=u("重启"),b=u("终结"),w=u("由好友Remid开发的，基于Go的网关，实现Bot请求转发"),T=u("运行时间"),v=u("运行中..."),S=u("重启"),k=u("终结"),H=u("系统后台服务"),I=u("运行时间"),j=u("重启"),z=u("终结");d.render=function(e, t, l, d, B, C){const W=i("HelpIcon"),q=i("n-icon"),O=i("n-button"),P=i("n-popover"),$=i("n-h1"),G=i("n-divider"),L=i("n-text"),M=i("n-h3"),Q=i("n-card"),R=i("n-gi"),U=i("n-grid"),Y=i("WebSocket");return a(),s(p,null,[n("div",null,[o($,{prefix:"bar",style:{"font-weight":"400","font-size":"32px"}},{default:r((()=>[m,x,o(P,{trigger:"click"},{trigger:r((()=>[o(O,{text:"",style:{"font-size":"26px"}},{default:r((()=>[o(q,null,{default:r((()=>[o(W)])),_:1})])),_:1})])),default:r((()=>[y])),_:1})])),_:1})]),o(G),o(Q,{title:"运行状态",style:{"margin-bottom":"40px"}},{default:r((()=>[o(U,{"x-gap":"30",cols:3},{default:r((()=>[o(R,null,{default:r((()=>[o(Q,{"content-style":"",hoverable:"",title:"CQ-HTTP 机器人"},{default:r((()=>[n("div",null,[o(L,{italic:""},{default:r((()=>[_])),_:1})]),o(M,{type:"info",prefix:"bar",style:{display:"inline"}},{default:r((()=>[c])),_:1}),o(L,{style:{"margin-left":"10px"}},{default:r((()=>[u(f(e.nowTime),1)])),_:1}),o(M,{prefix:"bar"},{default:r((()=>[h])),_:1}),o(G),o(O,{ghost:"",type:"primary",style:{margin:"0 20px 10px 0",width:"100px"}},{default:r((()=>[g])),_:1}),o(O,{type:"error",style:{margin:"0 20px 0 0",width:"100px"}},{default:r((()=>[b])),_:1})])),_:1})])),_:1}),o(R,null,{default:r((()=>[o(Q,{hoverable:"",title:"YUki 网关"},{default:r((()=>[n("div",null,[o(L,{italic:""},{default:r((()=>[w])),_:1})]),o(M,{type:"info",prefix:"bar",style:{display:"inline"}},{default:r((()=>[T])),_:1}),o(L,{style:{"margin-left":"10px"}},{default:r((()=>[u(f(e.nowTime),1)])),_:1}),o(M,{prefix:"bar"},{default:r((()=>[v])),_:1}),o(G),o(O,{ghost:"",type:"primary",style:{margin:"0 20px 10px 0",width:"100px"}},{default:r((()=>[S])),_:1}),o(O,{type:"error",style:{margin:"0 20px 0 0",width:"100px"}},{default:r((()=>[k])),_:1})])),_:1})])),_:1}),o(R,null,{default:r((()=>[o(Q,{hoverable:"",title:"Louise 后台服务"},{default:r((()=>[n("div",null,[o(L,{italic:""},{default:r((()=>[H])),_:1})]),o(M,{type:"info",prefix:"bar",style:{display:"inline"}},{default:r((()=>[I])),_:1}),o(L,{style:{"margin-left":"10px"}},{default:r((()=>[u(f(e.nowTime),1)])),_:1}),o(M,{prefix:"bar",type:e.louiseStatus},{default:r((()=>[u(f(e.louiseText),1)])),_:1},8,["type"]),o(G),o(O,{ghost:"",type:"primary",style:{margin:"0 20px 10px 0",width:"100px"}},{default:r((()=>[j])),_:1}),o(O,{type:"error",style:{margin:"0 20px 0 0",width:"100px"}},{default:r((()=>[z])),_:1})])),_:1})])),_:1})])),_:1})])),_:1}),o(Y,{ref:"webSocket",client_name:"status_conn"},null,512)],64)};export{d as default};

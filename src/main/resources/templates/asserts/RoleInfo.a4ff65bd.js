import{a as e}from "./index.9052b21b.js";import{C as a,T as t}from "./TerminalOutline.5c9271c4.js";import{d as l,b as n,o,l as i,w as s,f as r,ag as d,h as u,r as p,u as f,ae as m,a9 as h,N as c,e as _,g,ab as y}from "./vendor.3140252b.js";import{A as w}from "./AlertCircleOutline.f0a04ba9.js";import{H as v}from "./HelpCircleOutline.8866f4f7.js";const x=l({setup(){},components:{CaretForwardIcon:a,Ternimal:t},props:{data:{},existFeature:Array,width:String,type:String},data(){return{model:this.data,options:this.resolveOption(),value:this.resolveValue()}},mounted(){},methods:{resolveOption(){let e=[{feature_name:"系统帮助",feature_id:1},{feature_name:"随机图片",feature_id:2},{feature_name:"获取个人信息",feature_id:3},{feature_name:"请求Pixiv图片",feature_id:4},{feature_name:"图片搜索",feature_id:5},{feature_name:"用户注册",feature_id:6}];return Array.apply(null,{length:e.length}).map(((a, t)=>({label:e[t].feature_name,value:t,disabled:!1})))},resolveValue(){return Array.apply(null,{length:this.existFeature.length}).map(((e, a)=>a))},saveFormData(){let e={role_id:this.model.role_id,role_name:"",info:"",featureInfoList:[]};e.role_name=this.model.role_name,e.info=this.model.info,e.featureInfoList=this.value,console.log(e),this.$axios.post("role/"+this.type,e).then((e=>{e.data.msg}))}}}),b=u(" 保存 ");x.render=function(e, a, t, l, u, p){const f=n("n-input"),m=n("n-form-item"),h=n("n-button"),c=n("n-gi"),_=n("n-transfer"),g=n("n-grid"),y=n("n-form"),w=n("n-card");return o(),i(w,{style:d(e.width)},{default:s((()=>[r(y,{size:"medium","label-width":"100",model:e.model},{default:s((()=>[r(g,{"x-gap":"64",cols:4},{default:s((()=>[r(c,{span:"2"},{default:s((()=>[r(m,{label:"角色名称"},{default:s((()=>[r(f,{placeholder:"请输入角色名称",value:e.model.role_name,"onUpdate:value":a[0]||(a[0]= a=>e.model.role_name=a)},null,8,["value"])])),_:1}),r(m,{label:"说明"},{default:s((()=>[r(f,{placeholder:"请输入说明",value:e.model.info,"onUpdate:value":a[1]||(a[1]= a=>e.model.info=a)},null,8,["value"])])),_:1}),r(h,{type:"primary",size:"large",style:{width:"100px"},onClick:e.saveFormData},{default:s((()=>[b])),_:1},8,["onClick"])])),_:1}),r(c,{span:"2"},{default:s((()=>[r(m,null,{default:s((()=>[r(_,{ref:"transfer",value:e.value,"onUpdate:value":a[2]||(a[2]= a=>e.value=a),options:e.options,filterable:"","source-title":"所有功能","target-title":"具有功能"},null,8,["value","options"])])),_:1})])),_:1})])),_:1})])),_:1},8,["model"])])),_:1},8,["style"])};const k=({popMessage:a})=>[{type:"selection"},{type:"expand",renderExpand: e=>h(x,{existFeature:e.featureInfoList,data:e,hoverable:!0,type:"edit"})},{title:"角色名称",key:"role_name",width:200,ellipsis:!0},{title:"说明",key:"info",width:300,ellipsis:!0},{title:"状态",key:"is_enabled",render: t=>h(c,{circle:!0,style:"margin: 0; width: 80px",type:1==t.is_enabled?"primary":"error",ghost:1==t.is_enabled,onClick(){e.post("role/switchStatus",t).then((e=>{let l=e.data.msg;t.is_enabled=-t.is_enabled,a(l,t.is_enabled)}))}},{default:()=>1==t.is_enabled?"良好":"禁用"})}],C=l({setup(){const e=p([]),a=f(),t=m({page:1,pageSize:15,showSizePicker:!0,pageSizes:[10,15,25],onChange: e=>{t.page=e},onPageSizeChange: e=>{t.pageSize=e,t.page=1}});return{showModal:p(!1),pagination:t,columns:k({popMessage(e, t){1==t?a.success(e):a.warning(e)}}),checkedRowKeys:e,handleCheck(a){e.value=a}}},data:()=>({dataList:[],featureList:[]}),mounted(){this.$axios.post("role/findBy").then((e=>{this.dataList=e.data.datas}))},components:{AlertIcon:w,HelpIcon:v,RoleCard:x},methods:{}}),z=u(" 角色信息 "),L=g("span",{style:{"font-weight":"200","font-size":"16px"}},"(Role info)",-1),F=g("p",null,"管理系统中的角色信息",-1),S=u("新增"),I=u("删除");C.render=function(e, a, t, l, i, d){const u=n("HelpIcon"),p=n("n-icon"),f=n("n-button"),m=n("n-popover"),h=n("n-h1"),c=n("n-divider"),w=n("n-data-table"),v=n("n-card"),x=n("RoleCard"),b=n("n-modal");return o(),_(y,null,[g("div",null,[r(h,{prefix:"bar",style:{"font-weight":"400","font-size":"32px"}},{default:s((()=>[z,L,r(m,{trigger:"click"},{trigger:s((()=>[r(f,{text:"",style:{"font-size":"26px"}},{default:s((()=>[r(p,null,{default:s((()=>[r(u)])),_:1})])),_:1})])),default:s((()=>[F])),_:1})])),_:1})]),r(c),r(v,{title:"角色列表"},{default:s((()=>[g("div",null,[r(f,{ghost:"",type:"primary",size:"large",style:{margin:"0 10px 10px 0",width:"80px"},onClick:a[0]||(a[0]= a=>e.showModal=!0)},{default:s((()=>[S])),_:1}),r(f,{type:"error",size:"large",style:{margin:"0 10px 10px 0",width:"80px"}},{default:s((()=>[I])),_:1})]),r(w,{columns:e.columns,data:e.dataList,pagination:e.pagination,"row-key": e=>e.role_id,"onUpdate:checkedRowKeys":e.handleCheck},null,8,["columns","data","pagination","row-key","onUpdate:checkedRowKeys"])])),_:1}),r(b,{show:e.showModal,"onUpdate:show":a[1]||(a[1]= a=>e.showModal=a)},{default:s((()=>[r(v,{style:{width:"1100px"},title:"新增角色",bordered:!1,size:"huge"},{default:s((()=>[r(x,{type:"save",data:e.dataList,existFeature:e.featureList,width:"width: 1000px"},null,8,["data","existFeature"])])),_:1})])),_:1},8,["show"])],64)};export{C as default};
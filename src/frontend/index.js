import React, { Component } from 'react'
import { render } from 'react-dom'
import { combineReducers, createStore, bindActionCreators } from 'redux'
import { Provider, connect } from 'react-redux'
import { Router, Route, IndexRoute, Link } from 'react-router'
import { createHashHistory } from 'history';
import { syncReduxAndRouter, routeReducer, pushPath } from 'redux-simple-router'
import _ from 'lodash'
import { ButtonToolbar, Button, MenuItem, Navbar, Nav, NavItem, NavDropdown, Input, Table, Pagination, Modal, Panel } from 'react-bootstrap'

var ajax=function(){var t,e,n=function(){var t=Object.prototype.toString;return function(e){switch(t.call(e)){case"[object Function]":return"function";case"[object Date]":return"date";case"[object RegExp]":return"regexp";case"[object Arguments]":return"arguments";case"[object Array]":return"array";case"[object String]":return"string"}if("object"==typeof e&&e&&"number"==typeof e.length)try{if("function"==typeof e.callee)return"arguments"}catch(n){if(n instanceof TypeError)return"arguments"}return null===e?"null":void 0===e?"undefined":e&&1===e.nodeType?"element":e===Object(e)?"object":typeof e}}(),r=0,o=window.document,a=/^(?:text|application)\/javascript/i,c=/^(?:text|application)\/xml/i,i="application/json",u="text/html",s=/^\s*$/,l=function(t,e,n){return!0},p=function(t,e,n,r){return t.global?l(e||o,n,r):void 0},d=function(t){t.global&&0===R.active++&&p(t,null,"ajaxStart")},f=function(t){t.global&&!--R.active&&p(t,null,"ajaxStop")},m=function(t,e){var n=e.context;return e.beforeSend.call(n,t,e)===!1||p(e,n,"ajaxBeforeSend",[t,e])===!1?!1:void p(e,n,"ajaxSend",[t,e])},y=function(t,e,n){var r=n.context;n.complete.call(r,e,t),p(n,r,"ajaxComplete",[e,n]),f(n)},x=function(t,e,n){var r=n.context,o="success";n.success.call(r,t,o,e),p(n,r,"ajaxSuccess",[e,n,t]),y(o,e,n)},v=function(t,e,n,r){var o=r.context;r.error.call(o,n,e,t),p(r,o,"ajaxError",[n,r,t]),y(e,n,r)},j=function(t){return t&&(t==u?"html":t==i?"json":a.test(t)?"script":c.test(t)&&"xml")||"text"},g=function(t,e){return(t+"&"+e).replace(/[&?]{1,2}/,"?")},b=function(t){"object"===n(t.data)&&(t.data=T(t.data)),!t.data||t.type&&"GET"!=t.type.toUpperCase()||(t.url=g(t.url,t.data))},w=function(t,e,r,o){var a="array"===n(e);for(var c in e){var i=e[c];o&&(c=r?o:o+"["+(a?"":c)+"]"),!o&&a?t.add(i.name,i.value):(r?"array"===n(i):"object"===n(i))?w(t,i,r,c):t.add(c,i)}},h=encodeURIComponent,T=function(t,e){var n=[];return n.add=function(t,e){this.push(h(t)+"="+h(e))},w(n,t,e),n.join("&").replace("%20","+")},S=function(e){var n=Array.prototype.slice;return n.call(arguments,1).forEach(function(n){for(t in n)void 0!==n[t]&&(e[t]=n[t])}),e},E=function(){},R=function(n){var r=S({},n||{});for(t in R.settings)void 0===r[t]&&(r[t]=R.settings[t]);d(r),r.crossDomain||(r.crossDomain=/^([\w-]+:)?\/\/([^\/]+)/.test(r.url)&&RegExp.$2!=window.location.host);var o=r.dataType,a=/=\?/.test(r.url);if("jsonp"==o||a)return a||(r.url=g(r.url,"callback=?")),R.JSONP(r);r.url||(r.url=window.location.toString()),b(r);var c,i=r.accepts[o],u={},l=/^([\w-]+:)\/\//.test(r.url)?RegExp.$1:window.location.protocol,p=R.settings.xhr();r.crossDomain||(u["X-Requested-With"]="XMLHttpRequest"),i&&(u.Accept=i,i.indexOf(",")>-1&&(i=i.split(",",2)[0]),p.overrideMimeType&&p.overrideMimeType(i)),(r.contentType||r.data&&"GET"!=r.type.toUpperCase())&&(u["Content-Type"]=r.contentType||"application/x-www-form-urlencoded"),r.headers=S(u,r.headers||{}),p.onreadystatechange=function(){if(4==p.readyState){clearTimeout(c);var t,e=!1;if(p.status>=200&&p.status<300||304==p.status||0==p.status&&"file:"==l){o=o||j(p.getResponseHeader("content-type")),t=p.responseText;try{"script"==o?(1,eval)(t):"xml"==o?t=p.responseXML:"json"==o&&(t=s.test(t)?null:JSON.parse(t))}catch(n){e=n}e?v(e,"parsererror",p,r):x(t,p,r)}else v(null,"error",p,r)}};var f="async"in r?r.async:!0;p.open(r.type,r.url,f);for(e in r.headers)p.setRequestHeader(e,r.headers[e]);return m(p,r)===!1?(p.abort(),!1):(r.timeout>0&&(c=setTimeout(function(){p.onreadystatechange=E,p.abort(),v(null,"timeout",p,r)},r.timeout)),p.send(r.data?r.data:null),p)};return R.active=0,R.JSONP=function(t){if(!("type"in t))return R(t);var e,n="jsonp"+ ++r,a=o.createElement("script"),c=function(){n in window&&(window[n]=E),y("abort",i,t)},i={abort:c},u=o.getElementsByTagName("head")[0]||o.documentElement;return t.error&&(a.onerror=function(){i.abort(),t.error()}),window[n]=function(r){clearTimeout(e),delete window[n],x(r,i,t)},b(t),a.src=t.url.replace(/=\?/,"="+n),u.insertBefore(a,u.firstChild),t.timeout>0&&(e=setTimeout(function(){i.abort(),y("timeout",i,t)},t.timeout)),i},R.settings={type:"GET",beforeSend:E,success:E,error:E,complete:E,context:null,global:!0,xhr:function(){return new window.XMLHttpRequest},accepts:{script:"text/javascript, application/javascript",json:i,xml:"application/xml, text/xml",html:u,text:"text/plain"},crossDomain:!1,timeout:0},R.get=function(t,e){return R({url:t,success:e})},R.post=function(t,e,r,o){return"function"===n(e)&&(o=o||r,r=e,e=null),R({type:"POST",url:t,data:e,success:r,dataType:o})},R.getJSON=function(t,e){return R({url:t,success:e,dataType:"json"})},R}();
const makeActionCreator=(type,...argNames)=>
{
  return (...args)=>
  {
    let action={type};
    argNames.forEach((name,index)=>
    {
      action[name]=args[index];
    });
    return action;
  };
};
const createReducer=(initialState,handlers)=>
{
  return function(state=initialState,action)
  {
    if(handlers.hasOwnProperty(action.type))
    {
      return handlers[action.type](state,action);
    }
    else
    {
      return state;
    };
  };
};
const CONSTANTS =
{
  SHOW_ADD_TASK: "SHOW_ADD_TASK",
  SHOW_EDIT_TASK: "SHOW_EDIT_TASK",
  HIDE_EDIT_TASK: "HIDE_EDIT_TASK",
  ADD_TASK: "ADD_TASK",
  UPDATE_TASK: "UPDATE_TASK",
  DELETE_TASK: "DELETE_TASK",
  UPDATE_JOB_SEARCH: "UPDATE_JOB_SEARCH",
  EMPTY_JOB_SEARCH: "EMPTY_JOB_SEARCH",
  SEARCH_JOB: "SEARCH_JOB",
  JOB_SELECTION_CHANGED: "JOB_SELECTION_CHANGED",
  EMPTY_JOB_SELECTION: "EMPTY_JOB_SELECTION",
  EDIT_JOB: "EDIT_JOB",
  UPDATE_JOB_PAGE: "UPDATE_JOB_PAGE",
  UPDATE_INSTANCE_SEARCH: "UPDATE_INSTANCE_SEARCH",
  UPDATE_INSTANCE_PAGE: "UPDATE_INSTANCE_PAGE"
};
const showAddTask = makeActionCreator(CONSTANTS.SHOW_ADD_TASK);
const showEditTask = makeActionCreator(CONSTANTS.SHOW_EDIT_TASK,"task");
const hideEditTask = makeActionCreator(CONSTANTS.HIDE_EDIT_TASK);
const updateJobSearch = makeActionCreator(CONSTANTS.UPDATE_JOB_SEARCH,"text");
const emptyJobSearch = makeActionCreator(CONSTANTS.EMPTY_JOB_SEARCH);
const jobSellectionChanged = makeActionCreator(CONSTANTS.JOB_SELECTION_CHANGED,"id","checked");
const emptyJobSelection = makeActionCreator(CONSTANTS.EMPTY_JOB_SELECTION);
const editJobProperty = makeActionCreator(CONSTANTS.EDIT_JOB,"name","value");
const updateJobPage = makeActionCreator(CONSTANTS.UPDATE_JOB_PAGE,"pageIndex","pageSize","total","items");
const updateInstanceSearch = makeActionCreator(CONSTANTS.UPDATE_INSTANCE_SEARCH,"text");
const updateInstancePage = makeActionCreator(CONSTANTS.UPDATE_INSTANCE_PAGE,"pageIndex","pageSize","total","items");

const jobInfo = createReducer({search:"",jobEditing:false,editJob:{},pageIndex:1,pageNum:0,items:[],selectedItems:[]},{
  [CONSTANTS.UPDATE_JOB_SEARCH]:(state,action)=>_.assign({},state,{search:action.text}),
  [CONSTANTS.EMPTY_JOB_SEARCH]:(state,action)=>_.assign({},state,{search:""}),
  [CONSTANTS.SHOW_ADD_TASK]:(state,action)=>_.assign({},state,{jobEditing:true}),
  [CONSTANTS.SHOW_EDIT_TASK]:(state,action)=>_.assign({},state,{jobEditing:true,editJob:action.task}),
  [CONSTANTS.HIDE_EDIT_TASK]:(state,action)=>_.assign({},state,{jobEditing:false,editJob:{}}),
  [CONSTANTS.JOB_SELECTION_CHANGED]:(state,action)=>{
    if(action.checked)
    {
      return _.assign({},state,{selectedItems:_.concat(state.selectedItems,action.id)});
    }
    else
    {
        const i = _.indexOf(state.selectedItems,action.id);
        if(i!=-1)
        {
          return _.assign({},state,{selectedItems:_.concat(state.selectedItems.slice(0,i),state.selectedItems.slice(i+1))});
        }
        else
        {
          return state;
        };
    }
  },
  [CONSTANTS.EMPTY_JOB_SELECTION]:(state,action)=>_.assign({},state,{selectedItems:[]}),
  [CONSTANTS.EDIT_JOB]:(state,action)=>_.assign({},state,{editJob:_.assign({},state.editJob,{[action.name]:action.value})}),
  [CONSTANTS.UPDATE_JOB_PAGE]:(state,action)=>_.assign({},state,{pageIndex:action.pageIndex,items:action.items,pageNum:_.ceil(action.total / action.pageSize)})
});
const jobInstance = createReducer({search:"",pageIndex:1,pageNum:0,items:[]},{
  [CONSTANTS.UPDATE_INSTANCE_SEARCH]:(state,action)=>_.assign({},state,{search:action.text}),
  [CONSTANTS.UPDATE_INSTANCE_PAGE]:(state,action)=>_.assign({},state,{pageIndex:action.pageIndex,items:action.items,pageNum:_.ceil(action.total / action.pageSize)})
});
const isValidCron=(()=>
{
    const checkSepical = (()=>
    {
      const reg =/[*?]/;
      return (value)=>
      {
        let match = value.match(reg);
        return (match == null || (match.length == 1 && value.length == 1));
      };
    })();
    const isNotWildCard = (value, expression)=>
    {
        let match = value.match(expression);
        return (match == null || match.length == 0);
    };
    const convertDaysToInteger = (value)=>
    {
        let v = value;
        v = v.replace(/SUN/gi, "1");
        v = v.replace(/MON/gi, "2");
        v = v.replace(/TUE/gi, "3");
        v = v.replace(/WED/gi, "4");
        v = v.replace(/THU/gi, "5");
        v = v.replace(/FRI/gi, "6");
        v = v.replace(/SAT/gi, "7");
        return v;
    };
    const convertMonthsToInteger = (value)=>
    {
        let v = value;
        v = v.replace(/JAN/gi, "1");
        v = v.replace(/FEB/gi, "2");
        v = v.replace(/MAR/gi, "3");
        v = v.replace(/APR/gi, "4");
        v = v.replace(/MAY/gi, "5");
        v = v.replace(/JUN/gi, "6");
        v = v.replace(/JUL/gi, "7");
        v = v.replace(/AUG/gi, "8");
        v = v.replace(/SEP/gi, "9");
        v = v.replace(/OCT/gi, "10");
        v = v.replace(/NOV/gi, "11");
        v = v.replace(/DEC/gi, "12");
        return v;
    };
    const segmentValidator = (expression, value, range, segmentName)=>
    {
        let v = value;
        let numbers = new Array();
        let reg = new RegExp(expression, "gi");
        if(!reg.test(v))
        {
            return false;
        };
        let dupMatch = value.match(/L/gi);
        if(dupMatch != null && dupMatch.length>1)
        {
            return false;
        };
        let split = v.split(",");
        let i = -1;
        let l = split.length;
        let match;
        while(++i < l)
        {
            let checkSegment = split[i];
            let n;
            let pattern = /(\w*)/;
            match = pattern.exec(checkSegment);
            pattern = /(\w*)\-?\d+(\w*)/;
            match = pattern.exec(checkSegment);
            if(match && match[0] == checkSegment &&
                checkSegment.indexOf("L")==-1 && checkSegment.indexOf("l")==-1 &&
                checkSegment.indexOf("C")==-1 && checkSegment.indexOf("c")==-1 &&
                checkSegment.indexOf("W")==-1 && checkSegment.indexOf("w")==-1 &&
                checkSegment.indexOf("/")==-1 && (checkSegment.indexOf("-")==-1 || checkSegment.indexOf("-")==0) &&
                checkSegment.indexOf("#")==-1) {
                n = match[0];
                if(n && !(isNaN(n)))
                {
                    numbers.push(n);
                }
                else if(match[0] == "0")
                {
                    numbers.push(n);
                };
                continue;
            };
            pattern = /(\w*)L|C|W(\w*)/i;
            match = pattern.exec(checkSegment);
            if(match && match[0] != "" && (
                checkSegment.indexOf("L")>-1 || checkSegment.indexOf("l")>-1 ||
                checkSegment.indexOf("C")>-1 || checkSegment.indexOf("c")>-1 ||
                checkSegment.indexOf("W")>-1 || checkSegment.indexOf("w")>-1
                )){
                if(checkSegment == "L" || checkSegment == "l")
                {
                    continue;
                };
                pattern = /(\w*)\d+(l|c|w)?(\w*)/i;
                match = pattern.exec(checkSegment);
                if(!match || match[0] != checkSegment)
                {
                    continue;
                };
                let numCheck = match[0];
                numCheck = numCheck.replace(/(l|c|w)/ig,"");
                n = Number(numCheck);
                if(n && !(isNaN(n)))
                {
                    numbers.push(n);
                }
                else if(match[0] == "0")
                {
                    numbers.push(n);
                };
                continue;
            };
            let numberSplit;
            if(checkSegment.indexOf("/") > -1)
            {
                numberSplit = checkSegment.split("/");
                if(numberSplit.length != 2)
                {
                    continue;
                }
                else
                {
                    n = numberSplit[0];
                    if(n && !(isNaN(n)))
                    {
                        numbers.push(n);
                    }
                    else if(numberSplit[0] == "0")
                    {
                        numbers.push(n);
                    };
                    continue;
                };
            };
            if(checkSegment.indexOf("#") > -1)
            {
                numberSplit = checkSegment.split("#");
                if(numberSplit.length != 2)
                {
                    continue;
                }
                else
                {
                    n = numberSplit[0];
                    if(n && !(isNaN(n)))
                    {
                        numbers.push(n);
                    }
                    else if(numberSplit[0] == "0")
                    {
                        numbers.push(n);
                    };
                    continue;
                };
            };
            if(checkSegment.indexOf("-") > 0)
            {
                numberSplit = checkSegment.split("-");
                if(numberSplit.length != 2)
                {
                    continue;
                } else if(Number(numberSplit[0])>Number(numberSplit[1]))
                {
                    continue;
                }
                else
                {
                    n = numberSplit[0];
                    if(n && !(isNaN(n)))
                    {
                        numbers.push(n);
                    }
                    else if(numberSplit[0] == "0")
                    {
                        numbers.push(n);
                    };
                    n = numberSplit[1];
                    if(n && !(isNaN(n)))
                    {
                        numbers.push(n);
                    }
                    else if(numberSplit[1] == "0")
                    {
                        numbers.push(n);
                    };
                    continue;
                };
            };
        };
        i = -1;
        l = numbers.length;
        if(l == 0)
        {
            return false;
        };
        while(++i < l)
        {
            if(numbers[i] < range[0] ||numbers[i] > range[1])
            {
                return false;
            };
        };
        return true;
    };
    return (value)=>
    {
        let results = true;
        if(value == null || value.length == 0)
        {
            return false;
        };
        let expressionArray = value.split(" ");
        let len = expressionArray.length;
        if((len != 6) && (len != 7))
        {
            return false;
        };
        let match = value.match(/\?/g);
        if(match != null && match.length>1)
        {
            return false;
        };
        if(_.some(expressionArray,(exp)=>!checkSepical(exp)))
        {
          return false;
        };
        let dayOfTheMonthWildcard = "";
        if(isNotWildCard(expressionArray[0], /[\*]/gi))
        {
            if(!segmentValidator("([0-9\\\\,-\\/])", expressionArray[0], [0, 59], "seconds"))
            {
                return false;
            };
        };
        if(isNotWildCard(expressionArray[1], /[\*]/gi))
        {
            if(!segmentValidator("([0-9\\\\,-\\/])", expressionArray[1], [0, 59], "minutes"))
            {
                return false;
            };
        };
        if(isNotWildCard(expressionArray[2], /[\*]/gi))
        {
            if(!segmentValidator("([0-9\\\\,-\\/])", expressionArray[2], [0, 23], "hours"))
            {
                return false;
            };
        };
        if(isNotWildCard(expressionArray[3], /[\*\?]/gi))
        {
            if(!segmentValidator("([0-9LWC\\\\,-\\/])", expressionArray[3], [1, 31], "days of the month"))
            {
                return false;
            };
        }
        else
        {
            dayOfTheMonthWildcard = expressionArray[3];
        };
        if(isNotWildCard(expressionArray[4], /[\*]/gi))
        {
            expressionArray[4] = convertMonthsToInteger(expressionArray[4]);
            if(!segmentValidator("([0-9\\\\,-\\/])", expressionArray[4], [1, 12], "months"))
            {
                return false;
            };
        };
        if(isNotWildCard(expressionArray[5], /[\*\?]/gi))
        {
            expressionArray[5] = convertDaysToInteger(expressionArray[5]);
            if(!segmentValidator("([0-9LC#\\\\,-\\/])", expressionArray[5], [1, 7], "days of the week"))
            {
                return false;
            };
        }
        else
        {
            if(dayOfTheMonthWildcard == String(expressionArray[5]))
            {
                return false;
            };
        };
        if(len == 7)
        {
            if(isNotWildCard(expressionArray[6], /[\*]/gi))
            {
                if(!segmentValidator("([0-9\\\\,-\\/])", expressionArray[6], [1970, 2099], "years"))
                {
                    return false;
                };
            };
        };
        return true;
    };
})();
const App = connect((state)=>({search:state.jobInfo.search}),(dispatch)=>bindActionCreators({showAddTask,pushPath},dispatch))(class extends Component
{
  render()
  {
    return (
      <div>
        <Navbar style={{marginBottom:"0px"}}>
          <Navbar.Header>
            <Navbar.Brand>
              <Link to="/">任务调度中心</Link>
            </Navbar.Brand>
          </Navbar.Header>
          <Nav>
            <NavDropdown eventKey={1} title="任务" id="nav-task">
              <MenuItem eventKey={1.1} onSelect={()=>this.props.pushPath("/")}>列表</MenuItem>
              <MenuItem eventKey={1.2} onSelect={()=>{this.props.pushPath("/");this.props.showAddTask()}}>添加任务</MenuItem>
            </NavDropdown>
            <NavItem eventKey={2} href="#inst">任务实例</NavItem>
          </Nav>
        </Navbar>
        {this.props.children}
      </div>
    )
  }
});
const Job = connect((state)=>({editTaskVisible:state.jobInfo.jobEditing,editJob:state.jobInfo.editJob,search:state.jobInfo.search,items:state.jobInfo.items,selectedItems:state.jobInfo.selectedItems,pageIndex:state.jobInfo.pageIndex,pageNum:state.jobInfo.pageNum}),(dispatch)=>bindActionCreators({showAddTask,showEditTask,hideEditTask,updateJobSearch,emptyJobSearch,jobSellectionChanged,emptyJobSelection,editJobProperty,updateJobPage},dispatch))(class extends Component
{
  deleteJobs()
  {
    if(confirm("确定要删除那些任务吗?"))
    {
      ajax.post("./home/deleteJobs",{ids:this.props.selectedItems.join(",")},function(data)
      {
        if(data.code==0)
        {
          this.onPage(this.props.pageIndex);
          this.props.emptyJobSelection();
        }
        else
        {
          alert(data.error);
        };
      }.bind(this),"json");
    };
  }
  editJob()
  {
    const id=this.props.selectedItems[0];
    const item = this.props.items.filter((it)=>it.id==id);
    if(item.length)
    {
      this.props.showEditTask(item[0]);
    };
  }
  isValid()
  {
    const nameReg = /^[a-zA-Z]+[a-zA-Z0-9]*$/;
    const urlReg = /^https?:\/\/%s(:%d)?\/[a-zA-Z0-9/]+$/;
    const job = this.props.editJob;
    return job.name && nameReg.test(job.name) && job.expression && isValidCron(job.expression) && ((jb)=>{
      if(jb.needSharding)
      {
        return Number.isInteger(jb.shardingTotal) && parseInt(jb.shardingTotal) > 0;
      };
      return true;
    })(job) && ((jb)=>{
      if(jb.consumerType)
      {
        return jb.notifyUrl && urlReg.test(jb.notifyUrl);
      };
      return true;
    })(job);
  }
  onPage(i)
  {
    ajax.post("./home/getJobsByPage",{name:this.props.search,pageIndex:i,pageSize:10},function(data)
    {
      if(data.code == 0)
      {
        this.props.updateJobPage(i,10,data.count,data.data);
      };
    }.bind(this),"json");
  }
  saveJob()
  {
    ajax.post("./home/"+(this.props.editJob.id?"updateJob":"addJob"),{job:JSON.stringify(this.props.editJob)},function(data)
    {
      if(data.code==0)
      {
          this.onPage(this.props.pageIndex);
          this.props.hideEditTask();
      }
      else
      {
        alert(resp.error);
      };
    }.bind(this),"json");
  }
  runJobs()
  {
    ajax.post("./home/runJobs",{ids:this.props.selectedItems.join(",")},function(data)
    {
      if(data.code!=0)
      {
        alert(data.error);
      };
    },"json");
  }
  render()
  {
    return (
      <div>
        <Panel>
          <div className="navbar-left">
            {(()=>{
              if(this.props.search)
              {
                let sq=<Button onClick={this.search}>搜索</Button>;
                return <Input type="text" bsSize="medium" value={this.props.search} onChange={(e)=>this.props.updateJobSearch(e.target.value)} buttonAfter={sq} placeholder="任务名称"/>;
              }
              else
              {
                return <Input type="text" bsSize="medium" value={this.props.search} onChange={(e)=>this.props.updateJobSearch(e.target.value)} placeholder="任务名称"/>;
              };
            })()}
          </div>
          <div className="navbar-right btn-toolbar" style={{paddingRight:"15px"}}>
            <Button onClick={()=>this.props.showAddTask()}>添加任务</Button>
            {this.props.selectedItems.length==1?(<Button onClick={(e)=>this.editJob()}>修改任务</Button>):null}
            {this.props.selectedItems.length>0?([<Button onClick={this.deleteJobs.bind(this)}>删除任务</Button>,<Button>执行任务</Button>]):null}
          </div>
          <Table striped bordered hover>
            <thead><tr><th></th><th>名称</th><th>执行表达式</th><th>是否分片</th><th>分片总数</th><th>通知类型</th><th>通知地址</th><th>描述</th></tr></thead>
            <tbody>
              {
                this.props.items.map(job=>
                {
                  return (<tr key={job.id}><td><input type="checkbox" data-id={job.id} onClick={(e)=>this.props.jobSellectionChanged(e.target.getAttribute("data-id"),e.target.checked)}/></td><td>{job.name}</td><td>{job.expression}</td><td>{job.needSharding?"是":"否"}</td><td>{job.shardingTotal}</td><td>{job.consumerType?"TCP":"HTTP"}</td><td>{job.notifyUrl}</td><td>{job.description}</td></tr>);
                })
              }
            </tbody>
          </Table>
          {this.props.pageNum>0?(<Pagination className="navbar-right" style={{display:"block",margin:"0px"}} prev="上一页" next="下一页" first="首页" last="尾页" ellipsis items={this.props.pageNum} maxButtons={5} activePage={this.props.pageIndex} onSelect={(_,e)=>this.onPage(e.eventKey)}/>):null}
        </Panel>
        <Modal backdrop={false} show={this.props.editTaskVisible} onHide={()=>this.props.hideEditTask()}>
          <Modal.Header closeButton>
            <Modal.Title>{this.props.editJob.id?"修改任务":"添加任务"}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <form className="form-horizontal">
              <Input type="text" label="任务名称" disabled={!!this.props.editJob.id} value={this.props.editJob.name} onChange={(e)=>this.props.editJobProperty("name",e.target.value)} labelClassName="col-xs-3" wrapperClassName="col-xs-9"/>
              <Input type="text" label="CRON表达式" value={this.props.editJob.expression} onChange={(e)=>this.props.editJobProperty("expression",e.target.value)} labelClassName="col-xs-3" wrapperClassName="col-xs-9"/>
              <Input type="checkbox" label="是否分片" value={this.props.editJob.needSharding} onChange={(e)=>this.props.editJobProperty("needSharding",e.target.checked)} wrapperClassName="col-xs-offset-3 col-xs-9"/>
              {this.props.editJob.needSharding?<Input type="text" label="分片数量" value={this.props.editJob.shardingTotal} onChange={(e)=>this.props.editJobProperty("shardingTotal",e.target.value)} labelClassName="col-xs-3" wrapperClassName="col-xs-9"/>:null}
              <Input type="select" label="通知类型" value={this.props.editJob.consumerType} onChange={(e)=>this.props.editJobProperty("consumerType",parseInt(e.target.value))} labelClassName="col-xs-3" wrapperClassName="col-xs-9">
                <option value="0">TCP</option>
                <option value="1">HTTP</option>
              </Input>
              {this.props.editJob.consumerType==1?<Input type="text" label="回调地址" value={this.props.editJob.notifyUrl} onChange={(e)=>this.props.editJobProperty("notifyUrl",e.target.value)} labelClassName="col-xs-3" wrapperClassName="col-xs-9"/>:null}
              <Input type="textarea" label="任务描述" value={this.props.editJob.description} onChange={(e)=>this.props.editJobProperty("description",e.target.value)} labelClassName="col-xs-3" wrapperClassName="col-xs-9"/>
              <Input type="textarea" label="其它" value={this.props.editJob.extra} onChange={(e)=>this.props.editJobProperty("extra",e.target.value)} labelClassName="col-xs-3" wrapperClassName="col-xs-9"/>
            </form>
          </Modal.Body>
          <Modal.Footer>
            <Button disabled={!this.isValid()} onClick={this.saveJob.bind(this)}>保存</Button>
            <Button onClick={this.props.hideEditTask}>关闭</Button>
          </Modal.Footer>
        </Modal>
      </div>
    );
  }
});
const JobInstance = connect((state)=>({search:state.jobInstance.search,items:state.jobInstance.items,pageIndex:state.jobInstance.pageIndex,pageNum:state.jobInstance.pageNum,total:state.jobInstance.total}),(dispatch)=>bindActionCreators({updateInstanceSearch,updateInstancePage},dispatch))(class extends Component
{
  onPage(i)
  {
    ajax.post("./home/getJobInstanceByPage",{name:this.props.search,pageIndex:i,pageSize:10},function(data)
    {
      if(data.code==0)
      {
        this.props.updateInstancePage(i,10,data.count,data.data);
      };
    }.bind(this),"json");
  }
  render()
  {
    return (<div>
      <Panel>
        {(()=>{
          let sq=<Button onClick={this.search}>搜索</Button>;
          return <Input type="text" className="navbar-right" style={{width:"auto"}} bsSize="medium" value={this.props.search} onChange={(e)=>this.props.updateInstanceSearch(e.target.value)} buttonAfter={sq} placeholder="任务名称"/>;
        })()}
        <Table bordered>
          <thead><tr><th style={{textAlign:"center"}}>任务名称</th><th style={{textAlign:"center",width:"170px"}}>执行时间</th><th style={{textAlign:"center",width:"250px"}}>分片信息</th><th style={{textAlign:"center",width:"100px"}}>状态</th><th style={{textAlign:"center"}}>错误信息</th></tr></thead>
          {(()=>{
            let ui=this.props.items.map((it)=>
            {
              let im=it.items[0];
              if(it.items.length>1)
              {
                let fr=(<tr><td rowSpan={it.items.length} style={{verticalAlign:"middle"}}>{it.jobName}</td><td rowSpan={it.items.length} style={{verticalAlign:"middle"}}>{it.execTime}</td><td>{im.shardingItems}</td><td>{im.status?(im.status==1?"成功":"出错"):"执行中"}</td><td>{im.error}</td></tr>);
                return _.concat(fr, _.chain(it.items).tail().map((im)=>(<tr><td>{im.shardingItems}</td><td>{im.status?(im.status==1?"成功":"出错"):"执行中"}</td><td>{im.error}</td></tr>)));
              }
              else
              {
                return ([<tr><td>{it.jobName}</td><td>{it.execTime}</td><td>{im.shardingItems}</td><td>{im.status}</td><td>{im.error}</td></tr>]);
              };
            }).concat();
            return (<tbody>{ui}</tbody>);
          })()}
        </Table>
        {this.props.pageNum>0?(<Pagination className="navbar-right" style={{display:"block",margin:"0px"}} prev="上一页" next="下一页" first="首页" last="尾页" ellipsis items={this.props.pageNum} maxButtons={5} activePage={this.props.pageIndex} onSelect={(_,e)=>this.onPage(e.eventKey)}/>):null}
      </Panel>
    </div>);
  }
});
const reducer = combineReducers({jobInfo,jobInstance,routing:routeReducer});
const store = createStore(reducer);
(()=>
{
  let state=store.getState();
  ajax.post("./home/getJobsByPage",{name:state.jobInfo.search,pageIndex:state.jobInfo.pageIndex,pageSize:10},function(data)
  {
    if(data.code==0)
    {
      store.dispatch(updateJobPage(state.jobInfo.pageIndex,10,data.count,data.data));
    };
  },"json");
  ajax.post("./home/getJobInstanceByPage",{name:state.jobInstance.search,pageIndex:state.jobInstance.pageIndex,pageSize:10},function(data)
  {
    if(data.code==0)
    {
      store.dispatch(updateInstancePage(state.jobInstance.pageIndex,10,data.count,data.data));
    };
  },"json");
})();
const history = createHashHistory();
syncReduxAndRouter(history, store);
render((
  <Provider store={store}>
    <Router history={history}>
      <Route path="/" component={App}>
        <IndexRoute component={Job}/>
        <Route path="inst" component={JobInstance}/>
      </Route>
    </Router>
  </Provider>
),document.getElementById("content"));

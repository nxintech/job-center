### 分布式任务调度中心
 
#### 服务器安装：
 1. 安装node，建议安装5.7版本以上
 2. 安装前端依赖项，切换到src/frontend目录下执行npm install
 3. 初始化数据库（mysql），如需要使用其它数据库，按照此脚本调整成相应数据库言及修改部分mybaits配置.初始化语句在src/db下
 4. 在job-center-server上执行gradle prod war进行打包或交给jenkins自动处理
 
#### Java客户端使用：
 1. 向服务器上注册任务，如下

    ![添加任务](https://raw.githubusercontent.com/nxintech/job-center/master/doc/images/addTask_tcp.png)
 3. Java程序实现了一套客户端,程序实现client包下的IJob接口并返回一个JobResponse由框架自动将结果进行回送，示例如下
```java
public class HelloJob implements IJob
{
    @Override
    public JobResponse exec(JobRequest request)
    {
        System.out.println("收到任务请求:"+ JSON.toJSONString(request));
        JobResponse response = new JobResponse(request.getId(),2,"执行成功");
        return response;
    }
}
```
 3. 配置任务节点注册

 ```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <context:property-placeholder location="WEB-INF/config.properties"/>
    <bean id="serviceRegister" class="com.nxin.framework.core.ZkServiceRegister" init-method="startUp" destroy-method="shutDown" p:namespace="job-center" p:servers="${dubbo.address}"/>
    <bean id="codec" class="com.nxin.framework.codec.KryoCodec" init-method="startUp">
        <property name="clss">
            <list value-type="java.lang.String">
                <value>com.nxin.framework.message.JobResponse</value>
            </list>
        </property>
    </bean>
    <bean class="com.nxin.framework.client.RpcJobWorker" init-method="startUp" destroy-method="shutDown">
        <property name="jobWorker">
            <bean class="com.nxin.framework.client.JobWorker" init-method="startUp" destroy-method="shutDown" p:bufferSize="4" p:serviceRegister-ref="serviceRegister" p:port="${rpc.port}">
                <property name="waitStrategy">
                    <bean class="com.lmax.disruptor.BlockingWaitStrategy"/>
                </property>
                <property name="rpcHelper">
                    <bean class="com.nxin.framework.core.JobMessageRpcHelper" init-method="startUp" destroy-method="shutDown" p:codec-ref="codec"/>
                </property>
                <property name="handlerMap">
                    <map key-type="java.lang.String" value-type="com.nxin.framework.client.IJob">
                        <entry key="helloJob">
                            <bean class="com.nxin.ferameowrk.web.core.HelloJob"/>
                        </entry>
                    </map>
                </property>
            </bean>
        </property>
    </bean>
</beans>
 ```
 
###### 注意handlerMap上的key值需要和服务注册上的保持一致，注册服务时必需确保任务名唯一
#### 其它客户端使用
 1. 按照应用服务协议类型注册一个HTTP或HTTPS的任务
    ![添加任务](https://github.com/nxintech/job-center/blob/master/doc/images/newTask.jpg?raw=true)
    
###### 回调地址只需要填写相对于服务器根的地址如：job/triggerJob
 2. 实现一个响应任务请求的web服务，建议收到请求后将任务请求插入队列，返回一个正常状态码。任务服务器会以post方式向该地址提交以下参数:

    | 参数名           | 说明      | 用途              |
    | --------------- |:----------:| ----------------:|
    | id              | 任务条目ID | 回送任务执行状态   |
    | name            | 任务名称   | 客户端执行哪个任务 |
    | sharding        | 是否分片   | 0(不分片),1(分片) |
    | shardingItems   | 分片信息   | 节点获取的分片列表 |
    | extra           | 附加信息   | 附加字段，任务热配置 |
    
 3. 处理任务后进行状态回送，http post请求/home/reportJob,参数如下:


    | 参数名           | 说明      | 用途              |
    | --------------- |:----------:| ----------------:|
    | id              | 任务条目ID | 回送任务执行状态   |
    | state           | 任务状态   | 任务执行状态      |
    | error           | 错误消息   | 异常信息记录      |

###### 注意：state目前设计了三种（0，1，2），初始化为0，即任务服务器提交任务请求给任务节点，1：任务执行成功。2：任务执行失败


> 项目设计思路参考当当elastic-job https://github.com/dangdangdotcom/elastic-job

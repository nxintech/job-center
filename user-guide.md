### job-center使用向导

##### 1. 引入job-center相关的jar包，目前该jar包已经上传在私服,请使用最新版本。
###### Gradle配置
```
compile group: 'com.nxin.framework', name: 'job-center-client', version: 'xxx'
```
###### Maven配置
```xml
<dependency>
  <groupId>com.nxin.framework</groupId>
  <artifactId>job-center-client</artifactId>
  <version>xxx</version>
</dependency>
```
##### 2. 实现任务,作业代码实现IJob接口(Java)或者一个Http服务(非Java语言),框架会传入作业的相关信息(JobRequest)，作业执行完毕后需要将结果(JobResponse)上报给服务器。
###### 相关类型始下
```java
public class JobRequest
{
    private String id;
    private String jobName;
    private boolean sharding;
    private List<Integer> shardingItems;
    private String extra;
    ...
}
public class JobResponse
{
    private String id;
    private int status;
    private String error;
    ...
}
```
###### Java示例代码如下
```java
public class HelloJob implements IJob
{
    @Override
    public JobResponse exec(JobRequest request)
    {
        System.out.println("收到任务请求:"+ JSON.toJSONString(request));
        JobResponse response = new JobResponse(request.getId(),3,"执行成功");
        return response;
    }
}
```
##### 3. 客户端配置,如下
###### 1. 先配置一个任务注册中心
```xml
<bean id="serviceRegister" class="com.nxin.framework.core.ZkServiceRegister" init-method="startUp" destroy-method="shutDown" p:namespace="job-center" p:servers="${zk.address}"/>
```
###### 2. 配置消息编码方式
```xml
<bean id="codec" class="com.nxin.framework.codec.KryoCodec" init-method="startUp">
   <property name="clss">
      <list value-type="java.lang.String">
         <value>com.nxin.framework.message.JobResponse</value>
      </list>
   </property>
</bean>
```
###### 3. 配置任务，用户中心任务配置示例如下
```xml
<bean class="com.nxin.framework.client.RpcJobWorker" init-method="startUp" destroy-method="shutDown" p:port="${rpc.port}">
    <property name="jobWorker">
        <bean class="com.nxin.framework.client.JobWorker" init-method="startUp" destroy-method="shutDown" p:serviceRegister-ref="serviceRegister" p:port="${rpc.port}">
            <property name="rpcHelper">
                <bean class="com.nxin.framework.core.JobMessageRpcHelper" init-method="startUp" destroy-method="shutDown" p:codec-ref="codec"/>
            </property>
            <property name="handlerMap">
                <map key-type="java.lang.String" value-type="com.nxin.framework.client.IJob">
                    <entry key="oaSynchronizeJob">
                        <bean class="cn.dbn.userCenter.job.JcOaSynchronizeJob" p:batchSize="1000" p:oaTemplate-ref="oaTemplate" p:ucTemplate-ref="ucTemplate" p:transactionTemplate-ref="transactionTemplate"/>
                    </entry>
                    <entry key="deltaSyncs">
                        <bean class="cn.dbn.userCenter.job.JcOaDeltaSyncs" p:transactionTemplate-ref="transactionTemplate" p:cacheManager-ref="memCacheManager" p:ucTemplate-ref="ucTemplate" p:oaTemplate-ref="oaTemplate" p:jxcTemplate-ref="jxcTemplate" p:znTemplate-ref="znTemplate"/>
                    </entry>
                </map>
            </property>
        </bean>
    </property>
</bean>
```
     这里配置了二个任务oaSynchronizeJob和deltaSyncs，这个名称要和任务中心的配置一致，注意这里的配置不要和任务中心已有的任务重名,建议使用{projectName}-{jobName}的方式命名任务名。这里我们所有节点都可以配置上述信息，并且移除了CRON表达式部分。
###### 4. 配置定时检查并修复配置的任务，有时候由于各种原因任务节点没有正确注册到服务注册中心，导致任务中心分配不到任务到此节点，在注册中心处新增一项属性配置syncPeriod，单位为豪秒，默认为3分钟，即每三分钟检查自身信息是否正确注册在服务注册中心
##### 至此，我们的客户端部分配置完成
##### 4. 接下来我们登录任务中心来配置我们的任务，如下
![alt text](https://raw.githubusercontent.com/nxintech/job-center/master/doc/images/1.jpg)
###### 点击添加任务进入添加任务向导,相关示例图如下
![alt text](https://raw.githubusercontent.com/nxintech/job-center/master/doc/images/2.png)
![alt text](https://raw.githubusercontent.com/nxintech/job-center/master/doc/images/3.png)
![alt text](https://raw.githubusercontent.com/nxintech/job-center/master/doc/images/4.png)
![alt text](https://raw.githubusercontent.com/nxintech/job-center/master/doc/images/5.png)
     勾选分片时要求录入分片数量，协议使用http或https时要录入http任务的接口地址，任务中心会post数据到此地址来触发你本地的任务
# job-center
### 分布式任务调度中心

#### 安装：
##### 1.安装前端依赖项，切换到src/frontend目录下执行npm install
##### 2.初始化数据库（mysql），如需要使用其它数据库，按照此脚本调整成相应数据库言及修改部分mybaits配置.初始化语句在src/db下
##### 3.在job-center-server上执行gradle prod war进行打包或交给jenkins自动处理


#### 说明：
##### 任务调度中心分服务器端和客户端，都可以进行多机部署，多服务器之前通过netty rpc进行简单协同

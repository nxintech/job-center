DROP DATABASE nx_job_center;
CREATE DATABASE nx_job_center CHARACTER SET utf8;
USE nx_job_center;
CREATE TABLE nx_job_configuration(
  id NVARCHAR(36) PRIMARY KEY NOT NULL,
  name NVARCHAR(36) UNIQUE NOT NULL,
  expression NVARCHAR(36) NOT NULL,
  consumerType TINYINT NOT NULL,
  callbackUrl NVARCHAR(128) DEFAULT NULL,
  needSharding BOOLEAN DEFAULT NULL,
  shardingTotal INT DEFAULT NULL,
  createTime DATE NOT NULL,
  updateTime DATE NOT NULL,
  extra NVARCHAR(256) DEFAULT NULL,
  description NVARCHAR(256) DEFAULT NULL
) CHARACTER SET utf8;

CREATE TABLE nx_job_error_category(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name NVARCHAR(36) NOT NULL
) CHARACTER SET utf8;

CREATE TABLE nx_job_error(
  id INT PRIMARY KEY AUTO_INCREMENT,
  category INT REFERENCES nx_job_error_category(id),
  code INT UNIQUE NOT NULL,
  description NVARCHAR(128) NOT NULL
) CHARACTER SET utf8;

CREATE TABLE nx_job_instance(
  id NVARCHAR(36) PRIMARY KEY NOT NULL,
  jobId NVARCHAR(36) NOT NULL REFERENCES nx_job_configuration(id),
  createTime DATE NOT NULL
) CHARACTER SET utf8;

CREATE TABLE nx_job_instance_item(
  id NVARCHAR(36) PRIMARY KEY NOT NULL,
  instanceId NVARCHAR(36) NOT NULL REFERENCES nx_job_instance(id),
  shardingItems NVARCHAR(50) DEFAULT NULL,
  status TINYINT NOT NULL,
  error NVARCHAR(128) DEFAULT NULL
) CHARACTER SET utf8;

INSERT INTO nx_job_error_category(name) VALUES ('业务异常');
INSERT INTO nx_job_error_category(name) VALUES ('程序异常');
INSERT INTO nx_job_error(category, code, description) VALUES (1, 1000001, '任务名称已经存在');
INSERT INTO nx_job_error(category, code, description) VALUES (1, 1000002, '时间表达式格式错误');
INSERT INTO nx_job_error(category, code, description) VALUES (1, 1000003, '回调地址为空');
INSERT INTO nx_job_error(category, code, description) VALUES (1, 1000004, '回调地址格式不正确');
INSERT INTO nx_job_error(category, code, description) VALUES (1, 1000005, '分片总数末配置');
INSERT INTO nx_job_error(category, code, description) VALUES (1, 1000006, '任务名称为空');
INSERT INTO nx_job_error(category, code, description) VALUES (1, 1000007, '任务ID为空');
INSERT INTO nx_job_error(category, code, description) VALUES (2, 2000001, '数据库操作异常');

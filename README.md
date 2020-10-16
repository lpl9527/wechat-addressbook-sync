
# wechat-addressbook-sync
企业微信通讯录部门全量覆盖、人员增量更新定时同步程序

接口文档地址：
    https://work.weixin.qq.com/api/doc/90001/90143/91132

1.获取企业微信token
	http://localhost:20006/sync/token

2.同步部门
	http://localhost:20006/sync/org

3.同步人员
	http://localhost:20006/sync/user

4.同步部门和人员
	http://localhost:20006/sync

5.获取同步结果
	http://localhost:20006/sync/result?jobId=同步部门或人员后获取到的jobId
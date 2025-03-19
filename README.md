# read_gk-java
国家开放平台刷课

# 1.获取cookie
![81e9d2049c5bef3a7628e32a24241da](https://github.com/user-attachments/assets/deb4f52a-ddc7-4b2a-a3a0-0207f048ec8e)
# 2.启动服务，已内置免费代理,可以自行添加代理，在配置文件中添加获取代理链接然后模仿 free1Proxy 和 xiongmaoProxy即可。
# 3.请求服务，http://localhost:8088/api/tasks/submitCookie?cookie=上面获取的cookie,添加任务到一个线程，该接口返回一个唯一的线程 long ID 可用于查询运行状态。
![image](https://github.com/user-attachments/assets/ba9167b9-b829-40d9-9ae3-82d8fe8b7771)
# 4.查询状态http://localhost:8088/api/tasks/status/返回的longID，查询运行状态
![image](https://github.com/user-attachments/assets/9eaef8df-eeb4-4f8a-8c25-982532da38e2)
# 5.运行实例：
![image](https://github.com/user-attachments/assets/733afbc7-e52c-46f6-a237-9e756e31293e)


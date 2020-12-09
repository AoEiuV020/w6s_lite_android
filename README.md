#WorkPlus Lite Android客户端

WorkPlus Lite，开源的企业级移动平台

访问[WorkPlus Lite](https://lite.workplus.io)以了解更多!



WorkPlus Lite Android版本使用的语言为`java`以及`kotlin`

前期以`java`为主，在`kotlin`变为Andoid第一开发语言后，项目将`kotlin`做为主要开发语言

你需要同时对`java`以及`kotlin`有所掌握

## 1. 环境依赖

| 环境     | 依赖                     |
| -------- | ------------------------ |
| 操作系统 | 未有限制                 |
| JDK      | JDK8                     |
| Kotlin   | 官网最新版               |
| IDE      | Android Studio 4.1或以上 |

## 2. 编译与运行

请将项目导入Android Studio，第一次导入时间可能根据网络不同时长不一致。请确保你的电脑能访问一些Google或Android服务

## 3 系统结构文档

```
-WorkPlus_Android_V3

 -app 
  -main (代码主目录)
    -broadcast  (主要监听的系统广播)
    -component (公用的自定义组件)
    -crash (闪退监听处理, 目前使用 bugly 服务)
    -db.daoService (数据库异步操作服务)
    -manager （各个模块的业务管理）
    -modules (业务模块)
    -service (主要使用的service)
    -support (包含公共基类的 Activity, Fragment)
    -util (公用的工具类)
    
    
 -dependencies (依赖模块)
 	-agora-sdk (语音会议声网集成)
 	-amap-sdk (高德地图)
 	-cache (WorkPlus 缓存处理)
 	-db-core (数据库处理核心, 包括加密与非加密间的切换)
 	-db-service (数据库业务代码)
 	-emojicon (emoji 表情处理)
 	-gif-drawable (gif 处理)
	-im-sdk (im 核心模块)
	-infrastructure (基础模型)
	-lock9view (手势密码)
	-photo-edit (图片编辑, 涂鸦等)
	-qrcode (二维码处理)
	-record-ffmpeg (小视频)
	-translte (翻译功能)
	-watermark (水印 view 处理)
	-xfyun (语音转换文字处理) 		
    
```
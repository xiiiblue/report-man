# REPORT-MAN

## 简介
report-man:一个轻量级的报表自动化工具

对于中小型企业系统，一般没有配套的报表与经分系统进行支撑，但运维过程中经常会有各种报表的需求，报表一般为Excel格式，且内容经常发生变化，需要一套简单灵活的解决方案。

REPORT-MAN的程序本体只有一个可执行jar包，直接使用Excel模板进行配置，可以自动生成报表并发送邮件。支持3种启动方式，简单易用。

## 功能特性
- 支持桌面/命令行/WEB应用3种运行模式
- 无配置文件，在Excel模板中配置全部参数
- 可以在模版中定义各种复杂的汇总、公式、图表等
- 以任务为基本单位，配置灵活，支撑不断变化的需求
- 支持连接多个数据源
- 支持邮除发送，可使用代理服务器
- 丰富的自定义参数

## 技术特性
- Java实现，基于SpringBoot
- 使用Gradle构建
- 使用ApachePOI-XSSF处理Excel2007文件
- 使用JavaMailSender发送邮件
- 支持使用SOCKS5代理服务器
- 全部参数使用Excel配置
- 使用JdbcTemplate，动态生成数据源
- 支持查询/更新/删除及建表、建索引等操作
- 跨平台，多种运行模式
- 对外提供REST接口，可以配合其它系统使用

## 使用简介
report-man针对不同的使用场景，提供了3种启动模式

### 桌面模式
桌面环境下使用，提供一个Swing界面，用于手工生成报表。

![](http://oh0ra6igz.bkt.clouddn.com/sltzo.png)

界面中可以选择模板路径、报表输出路径，点击按钮直接运行即可。

桌面模式，一般用于对SQL语句进行微调后（例如修改起止时间），手工执行生成报表的场景。


### 命令行模式
服务器环境下，提供了一个命令行模式，用于后台静默执行。

![](http://oh0ra6igz.bkt.clouddn.com/2vazt.jpg)

一般会配合crontab定时生成报表并发送邮件。

crontab示例：
```
每天23:40执行
40 23 * * * cd /foo/bar && ./reportman.sh 模版名称

每周三23:45执行
45 23 * * 3 cd /foo/bar && ./reportman.sh 模版名称
```


### WEB模式
WEB模式对外提供了一个开放的REST接口。可以在其它系统中开发一个简单的界面，调用report-man的REST接口，生成报表并发送邮件。

![](http://oh0ra6igz.bkt.clouddn.com/uneap.png)

应用默认端口为19090，内嵌一个Swagger-UI界面可以方便的进行测试。

示例参数：
```
{
  "file.prefix": "demo",
  "mail.to.list": "foo@bar.com",
  "sql.start_date": "2017-10-1",
  "sql.end_date": "2017-10-31"
}
```

成功响应：
```
{
  "code": "0000",
  "message": "报表生成中，稍后会发送至您的邮箱。请不要重复点击生成报表。"
}
```

**说明**
1. file.prefix为必填，指定了基础的模板名称
2. 其它参数可以灵活添加。会追加在Sheet2的参数配置中。如果参数名称相同，则会覆盖Sheet2中的参数。
3. 接口是异步的，调用后会立即返回成功消息。如果SQL较复杂，可能会后台运行一段时间。


## 启动脚本
为了方便使用，report-man提供了两个启动脚本

### Windows环境 启动脚本
Windows环境下，只提供了一个桌面版本的启动入口，直接点击`reportman.cmd`即可。
```
./reportman.cmd
```

### Linux/macOS环境 启动脚本
Linux以及macOS下，提供了一个全功能的启动脚本，可以支持3种启动方式。

直接输入`./reportman.sh`可以查看帮助信息

以下为示例：

```
./reportman.sh gui        #启动桌面版本
./reportman.sh cli demo   #启动命令行版本，其中demo为模版名称
./reportman.sh web start  #启动WEB版本
./reportman.sh web stop   #停止WEB版本
```



## 运行流程
程序启动后，会首先载入模板文件，根据模版中提前定义好的各种参数，连接数据库读取结果集，并将结果填充到模板的指定区域。最终生成完整的Excel报表，并发送邮件。

步骤|解释
--|--
1|载入模板文件
2|读取Sheet1的任务配置
3|读取Sheet2的参数配置
4|循环执行全部任务
5|删除前2个配置Sheet页
6|刷新Excel公式
7|保存Excel
8|发送邮件


## 配置详解

模板中的前2个Sheet页为配置专用。
Sheet1任务配置，用于定义需要执行SQL语句，对应的数据源，以及结果集显示的区域等。
Sheet2参数配置，用于定义各种参数，如邮件收件人、代理服务器、数据源的用户名密码等。
从第3个Sheet页开始是内容页面，可以有多个Sheet页，需要提前定义好表头、样式、汇总函数、图表等。将需要填充内容的区域留空。


### Sheet1-任务配置
任务配置，存放于Sheet1中

**表样截图**

![](http://oh0ra6igz.bkt.clouddn.com/s63cr.jpg)

**字段解释**

名称|解释
--|--
任务名称|任务的中文说明
生效标识|Y-生效/N-失效。
数据源|数据源名称。数据源的详细参数需要在Sheet2中配置。
SQL|查询SQL语句。
SHEET页|结果集显示的Sheet页。因为前两页是配置专用，所以数值需要从3开始。
横向坐标|结果集起始的横向X轴坐标。数值从1开始。
纵向坐标|结果集起始的纵向Y轴坐标。数值从1开始。
单元格样式|LEFT-左对齐/CENTER-居中对齐

**配置示例**

任务名称|生效标识|数据源|SQL|SHEET页|横向坐标|纵向坐标|单元格样式
--|--|--|--|--|--|--|--
预计使用人数|N|mobsale|select foo from bar|3|3|3|CENTER
累计登录次数|N|mobsale|select foo from bar|3|5|3|CENTER
累计登录人数|N|mobsale|select foo from bar|3|6|3|CENTER
本日登录次数|Y|mobsale|select foo from bar|3|7|3|CENTER
本日登录人数|N|mobsale|select foo from bar|3|8|3|CENTER
使用清单|N|mobsale|select foo from bar|4|1|2|LEFT
工号使用情况|N|mobsale|select foo from bar|5|1|2|LEFT

**说明**
1. spider-man会读取所有"生效标识"为Y的任务，并依次执行
2. 对于select语句，执行后会生成一个x行y列的结果集
3. 结果集会写入Excel的特定的Sheet页的特定区域，具体可以配置"SHEET页"、"横向坐标"、"纵向坐标"3个参数
4. 单元格样式可以配置成"LEFT"（左对齐）或"CENTER"（居中对齐）
5. 支持update/insert/create table/create index等语句，一般用于生成中间表。此类语句不需要指定Sheet页及坐标。
6. SQL语句中可以插入参数占位符，格式为`${param_name}`。参数的值可以在Sheet2中定义。

### Sheet2-邮箱配置
邮箱配置，存放于Sheet2中

**表样截图**

![](http://oh0ra6igz.bkt.clouddn.com/59ohk.jpg)

**配置示例**
参数名称|键|值
--|--|--
邮件-发送开关|mail.enable|Y
邮件-收件人|mail.to.list|foo@bar.com
邮件-抄送人|mail.cc.list|yourname@qq.com
邮件-标题|mail.subject|XXX系统业务日报表
邮件-附件名|mail.attachment|业务报表
邮件-正文|mail.message|您好：
邮件-服务器IP|mail.smtp.host|smtp.qq.com
邮件-服务器端口|mail.smtp.port|465
邮件-用户名|mail.sender.username|yourname@qq.com
邮件-密码|mail.sender.password|blablablablabla
邮件-发件人|mail.from|yourname@qq.com
邮件-代理服务器开关|mail.proxy.enable|Y
邮件-代理服务器IP|mail.proxy.host|127.0.0.1
邮件-代理服务器端口|mail.proxy.port|1080

**说明**
1. 注意键的命名`mail.xxx`是固定的，请不要随意增删。
2. 代理服务器类型必须为SOCKS4/5
3. 附件名称如果为中文，最好不要超过4个汉字

### Sheet2-数据源配置
数据源配置，存放于Sheet2中

**MySQL数据源示例**

参数名称|键|值
--|--|--
数据源类型|db.testdb.driver|com.mysql.jdbc.Driver
数据源地址|db.testdb.url|jdbc:mysql://127.0.0.1:3306/dbname
数据源用户名|db.testdb.username|yourname
数据源密码|db.testdb.password|yourpass

**Oracle数据源示例**

参数名称|键|值
--|--|--
数据源类型|db.testdb.driver|oracle.jdbc.driver.OracleDriver
数据源地址|db.testdb.url|jdbc:oracle:thin:@127.0.0.1:1521:sid
数据源用户名|db.testdb.username|yourname
数据源密码|db.testdb.password|yourpass

**说明**
1. 键的格式为：`db.xxx.yyy`。xxx是自定义的数据源名称。yyy是固定的，不要随意修改。


### Sheet2-SQL参数配置
SQL参数配置，存放于Sheet2中

**配置示例**
参数名称|键|值
--|--|--
SQL参数|sql.start_date|2010-1-1
SQL参数|sql.end_date|2012-1-1

**说明**
1. SQL参数的格式为`sql.xxx`，其中`xxx`为参数名。
2. SQL参数的值，会替换Sheet1中SQL语句中的占位符，从而拼成完整的的SQL语句。
3. 例如：`sql.end_date = 2012-1-1`这一行配置，会将Sheet1中SQL语句里的`${end_date}`这个占位符替换为`2012-1-1`

### Sheet3~SheetN 模版配置详解
**表样截图**
![](http://oh0ra6igz.bkt.clouddn.com/iufpu.jpg)

模版配置非常灵活，可以根据需求配置多个Sheet页。

模版Sheet页中中主要是预先定义好表头、颜色、Excel公式等。数据部分留空，等待程序运行后填入。

一般而言，可以分为详单页、汇总页、函数页这3类。

**详单页**

展示一个详单，行数不确定。
只需要定义好一个表头即可，从每二行开始，都是自动填充的详单数据。

**汇总页**

行数、列数是固定的，用于展示SUM出来的汇总数据。
> 汇总页的提取，如果SQL比较复杂，可以分开配置多个任务，每个任务只填充特定的一列数据。

**函数页**

与汇总页类似，但本身并没有填充数据，而是填接使用Excel函数，获取其它Sheet页的原始数据，并进行加工与展示。

> 注意，如果报表中存在SUMPRODUCT函数，POI正常情况下不会计算最终结果，需要打开Excel后手工执行`Ctrl+Alt+F9`进行刷新。
> 但可以在函数后面加入`+(NOW()*0)`参数,强制POI计算结果。
> 例如： `=SUMPRODUCT(('业务发展-按个人'!$A$2:A$9999=O4)*('业务发展-按个人'!$B$2:B$9999="营维经理")*'业务发展-按个人'!H$2:H$9999)+(NOW()*0)`



## 服务器部署
- 运行环境要求：JRE版本1.7及以上。
- 在本机执行`gradle build`构建项目，将生成的`build/libs/report-man-1.0.jar`上传至服务器。
- 在jar包所在路径下新建template及report两个目录。其中template中存放模板，report目录用于存放生成的报表。
- 可以使用`crontab -e`配置定时执行

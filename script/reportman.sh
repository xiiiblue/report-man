#!/usr/bin/env bash
method=$1

if [ "$method" == "gui" ]; then
    echo "启动桌面版本"
    java -jar report-man-1.0.jar
elif [ "$method" == "cli" ]; then
    prefix=$2
    if [ "$prefix" == "" ];then
        echo "请传入模版名称！"
        exit 1
    fi
    echo "启动命令行版本"
    java -jar report-man-1.0.jar --custom.file.prefix=$prefix
elif [ "$method" == "web" ]; then
    action=$2
    if [ "$action" == "start" ]; then
        echo "启动WEB版本"
        nohup java -jar -Dweb=true report-man-1.0.jar --spring.profiles.active=enable-swagger 2>&1 &
        tail -f logs/report-man.log
    elif [ "$action" == "stop" ]; then
        echo "停止WEB版本"
        ps -ef|grep report-man-1.0.jar|grep java|grep -v grep|awk '{print $2}'|xargs kill -9
    else
        echo "请传入动作(start/stop)！"
        exit 1
    fi
else
    echo "请传入正确的参数!"
    echo ""
    echo "示例:"
    echo "./reportman.sh gui        #启动桌面版本"
    echo "./reportman.sh cli demo   #启动命令行版本"
    echo "./reportman.sh web start  #启动WEB版本"
    echo "./reportman.sh web stop   #停止WEB版本"
fi
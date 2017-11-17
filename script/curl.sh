#!/usr/bin/env bash
file_prefix="模板名"
mail_subject="邮件标题"
mail_to_list="name1@mail.com,name2@mail.com,name3@mail.com"

sql_start_date=`date --date="6 days ago" +%Y-%m-%d`" 00:00:00"  # sql_start_date=`date -j -v-6d +%Y-%m-%d`" 00:00:00"
sql_end_date=`date +%Y-%m-%d`" 23:59:59"
request="{\"file.prefix\": \"${file_prefix}\", \"mail.subject\": \"${mail_subject}\", \"mail.to.list\": \"${mail_to_list}\", \"sql.start_date\": \"${sql_start_date}\", \"sql.end_date\": \"${sql_end_date}\", \"sql.role_kind\": \"${sql_role_kind}\"}"

header1="Content-Type: application/json"
header2="Accept: application/json"
url="http://127.0.0.1:19090/api/reports"

curl -X POST --header "${header1}" --header "${header2}" -d "${request}" ${url}
/******************************************************************************
AHIber 是一个参考hibernate对android 数据库访问的封装

配置文件地址：/assets/ahiber_config.properties

raw目录下文件系统
--raw
	--sql.xml
	--contacts.xml
	--msg_attachment.xml
	--msg_history.xml
	--team.xml
	--test.db3
	

********************************配置文件样例***********************************
#-----AHiber数据库配置文件------
#在这里配置数据库必要信息,所有外部导入资源必须放在raw文件夹下
#@author ld.


#数据库名称
ahiber_dbname = test.db3
#数据库当前版本(int 大于0，升级用,AHiber会根据版本大小自动升级)
ahiber_dbversion = 1
#数据库命名空间
#ahiber_dbnamespace=ahiber
#外部数据文件(必须放在文件夹raw下)
ahiber_dbdatasrcfile = test.db3
#数据库sql文件(必须放在文件夹raw下)
ahiber_dbsqlfile = database.xml
#使用的数据库类型(目前只有sqlite)
ahiber_dbtype = sqlite

/******************************************************************************

//*********************************database.xml样例****************************
<?xml version="1.0" encoding="UTF-8"?>
<sqlfile namespace="false">
  <include file="sql" source="raw"/>
  <include file="contacts" source="raw"/>
  <include file="msg_attachment" source="raw"/>
  <include file="msg_history" source="raw"/>
  <include file="team" source="raw"/>
</sqlfile>
/******************************************************************************


	
//********************************sql.xml样例**********************************
<?xml version="1.0" encoding="UTF-8"?>
<!-- 数据库持久层架构演示 -->
<mapers namespace="sqltest" >

    <!--
  	method 表示方法名
    cacheRefreshs 表示需要刷新的缓存ID
    returnClass  表示返回对象类型
    split  表示sql分隔标示符
    cached 表示是否使用缓存
    -->
    <insert
        method ="insertBatchTest1"
        split = ";"
        >
	<!-- 批量插入sql格式 head;repeat;tail;size -->
<![CDATA[ 
   		insert into entity (id,name,count) select #id#,#name#,#count#;union select#id#,#name#,#count#;;50
     ]]>
    </insert>
    <update
        method="updateTest1"
        
        >
        <![CDATA[ 
         UPDATE entity SET  
       	   name=#name#,
           count=#count#
           WHERE id=#id#  
        ]]>
    </update>
     <delete
        method="deleteTest1"
        >
        <![CDATA[ 
          DELETE FROM entity WHERE id=#id# 
        ]]>
    </delete>
    <select
        method="selectTest1"
        returnClass = "com.ld.ahiber.Entity"
        cached ="true"
        >
        <![CDATA[ 
          SELECT * FROM entity
        ]]>
    </select>
</mapers>
/******************************************************************************
	
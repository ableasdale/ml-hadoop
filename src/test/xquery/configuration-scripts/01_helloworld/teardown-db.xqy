xquery version "1.0-ml"; 

import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";

admin:save-configuration(admin:database-delete(admin:get-configuration(), xdmp:database("hello-hadoop")))

xquery version "1.0-ml"; 

import module namespace admin = "http://marklogic.com/xdmp/admin" 
    at "/MarkLogic/admin.xqy";

declare variable $appserver := "xdbc-9001";  
declare variable $config := admin:get-configuration();
          
let $config := admin:appserver-delete(
    $config,
    admin:appserver-get-id($config, admin:group-get-id($config, "Default"), $appserver)
)
return
admin:save-configuration($config)
xquery version "1.0-ml"; 

import module namespace info = "http://marklogic.com/appservices/infostudio"
    at "/MarkLogic/appservices/infostudio/info.xqy";
import module namespace admin = "http://marklogic.com/xdmp/admin" 
    at "/MarkLogic/admin.xqy";

declare variable $port := 9001;
declare variable $db := "hello-hadoop";         
declare variable $config := admin:get-configuration();
    
(info:database-create($db),
let $_ := xdmp:log("hello world test setup)
let $config := admin:xdbc-server-create(
       admin:get-configuration(), 
       admin:group-get-id($config, "Default"), 
       fn:concat("xdbc-", $port), 
       "/", 
       $port, 
       0, 
       xdmp:database($db))
let $config := admin:database-set-directory-creation($config, xdmp:database($db), "manual")
return admin:save-configuration($config))
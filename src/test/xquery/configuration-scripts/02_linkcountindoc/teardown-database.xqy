xquery version "1.0-ml"; 

import module namespace info = "http://marklogic.com/appservices/infostudio"
    at "/MarkLogic/appservices/infostudio/info.xqy";

import module namespace admin = "http://marklogic.com/xdmp/admin" 
    at "/MarkLogic/admin.xqy";
 
declare variable $config := admin:get-configuration();
declare variable $db as xs:string := "hadoop-samples"; 
declare variable $forests as xs:string+ := ("hadoop-samples-1", "hadoop-samples-2");

(
for $forest in $forests
let $config := admin:database-detach-forest($config, xdmp:database($db), xdmp:forest($forest))
return admin:save-configuration($config),

for $forest in $forests
let $config := admin:forest-delete($config, admin:forest-get-id($config, $forest), fn:true())
return admin:save-configuration($config),

let $config := admin:database-delete($config, xdmp:database($db))
return admin:save-configuration($config)
)
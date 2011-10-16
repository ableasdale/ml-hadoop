(: Load data for LinkCount* examples into hadoop-samples database.
: Modify $local-dir to match your environment. Run against any
: content source. :)
xquery version "1.0-ml";
import module namespace info = "http://marklogic.com/appservices/infostudio"
at "/MarkLogic/appservices/infostudio/info.xqy";
(: Replace the value of $local-dir with your path to the sample data
directory :)
declare variable $local-dir := "/tmp/sample-data";
declare variable $samples-db := "hadoop-samples";
info:load(
$local-dir, (),
<options xmlns="http://marklogic.com/appservices/infostudio">
<uri>
<literal>enwiki</literal>
<path strip-prefix="{$local-dir}"/><literal>/</literal>
<filename/>
</uri>
<format>xml</format>
</options>,
$samples-db)
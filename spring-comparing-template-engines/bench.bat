rem ab -n 100000 -c 10 -k http://localhost:8080/jsp
rem ECHO Aqui
rem ab -n 100000 -c 10 -k http://localhost:8080/jsp
rem ab -n 100000 -c 10 -k http://localhost:8080/velocity
rem ECHO Aqui
rem ab -n 100000 -c 10 -k http://localhost:8080/velocity
rem ab -n 100000 -c 10 -k http://localhost:8080/freemarker
rem ECHO Aqui
rem ab -n 100000 -c 10 -k http://localhost:8080/freemarker
rem ab -n 100000 -c 10 -k http://localhost:8080/thymeleaf
rem ECHO Aqui
rem ab -n 100000 -c 10 -k http://localhost:8080/thymeleaf
rem ab -n 100000 -c 10 -k http://localhost:8080/mustache
rem ECHO Aqui
rem ab -n 100000 -c 10 -k http://localhost:8080/mustache
rem ab -n 100000 -c 10 -k http://localhost:8080/jade
rem ECHO Aqui
rem ab -n 100000 -c 10 -k http://localhost:8080/jade
rem ab -n 100000 -c 10 -k http://localhost:8080/pebble
rem ECHO Aqui
rem ab -n 100000 -c 10 -k http://localhost:8080/pebble
rem ab -n 100000 -c 10 -k http://localhost:8080/handlebars
rem ECHO Aqui
rem ab -n 100000 -c 10 -k http://localhost:8080/handlebars
rem ab -n 100000 -c 10 -k http://localhost:8080/jtwig
rem ECHO Aqui
rem ab -n 100000 -c 10 -k http://localhost:8080/jtwig
rem ab -n 100000 -c 10 -k http://localhost:8080/chunk
rem ECHO Aqui
rem ab -n 100000 -c 10 -k http://localhost:8080/chunk

ab -n 10000 -c 10 -k http://localhost:8080/htmlApiFaster
ab -n 10000 -c 10 -k http://localhost:8080/htmlApiFaster
ECHO Aqui
ab -n 10000 -c 10 -k http://localhost:8080/htmlApiFaster

ab -n 30000 -c 25 -k http://localhost:8080/htmlApiFaster
ab -n 30000 -c 25 -k http://localhost:8080/htmlApiFaster
ECHO Aqui
ab -n 30000 -c 25 -k http://localhost:8080/htmlApiFaster


ab -n 100000 -c 10 -k http://localhost:8080/htmlApiFaster
ECHO Aqui
ab -n 100000 -c 10 -k http://localhost:8080/htmlApiFaster

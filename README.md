To run the backend : JAVA 
 
     mvn clean install 
     mvn spring-boot:run

As I used Spring Initializer to install the dependancies accordingly the backend code is inside src folder.

API's are tested :  I have a little trouble in Using Isomnia 

tested using Curl 

C:\Windows\System32>curl -X POST http://localhost:4567/shorten ^  -H "Content-Type: application/json" ^  -d "{\"longUrl\":\"https://www.google.com\",\"customCode\":\"mycode123\",\"validityMinutes\":10}"
{"shortUrl":"http://localhost:4567/mycode123"}
C:\Windows\System32>curl -v http://localhost:4567/mycode123
* Host localhost:4567 was resolved.
* IPv6: ::1
* IPv4: 127.0.0.1
*   Trying [::1]:4567...
* Connected to localhost (::1) port 4567
* using HTTP/1.x
> GET /mycode123 HTTP/1.1
> Host: localhost:4567
> User-Agent: curl/8.12.1
> Accept: */*
>
< HTTP/1.1 302 Found
< Date: Mon, 07 Jul 2025 09:39:50 GMT
< Location: https://www.google.com
< Content-Length: 0
< Server: Jetty(9.4.44.v20210927)
<
* Connection #0 to host localhost left intact



.\curl.exe -v  -F "file=@dummy.pdf" localhost:8080/test/param1/param2
*   Trying ::1:8080...
* Connected to localhost (::1) port 8080 (#0)
> POST /test/param1/param2 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.77.0
> Accept: */*
> Content-Length: 13456
> Content-Type: multipart/form-data; boundary=------------------------2252ddfaf1589615
>
* We are completely uploaded and fine
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 OK
< Date: Mon, 31 May 2021 17:52:23 GMT
< content-type: text/plain
< content-length: 9
< connection: keep-alive
<
Uploaded!* Connection #0 to host localhost left intact
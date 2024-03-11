# ZipUrl Frontend API

### Register
#### Send POST request to localhost:8080/register  
In post body:
```json
{
    "email": "test99@gmail.com",
    "password":"12345"
}
```
In response body, there will be a JWT token returned. Status code 200.
![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/355cab44-e611-4593-9f84-a2e182f8af8d)  
#### This JWT token need to store in frontend

#### Every request under api/urls/** needs to have this JWT token in Authenorization header for veritication. Under Authenorization, select Bearer Token, and paste the JWT token.
![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/87af6c66-12cd-4829-a9d7-3d3ac829e512)

#### If email is null or empty, response will output error message in JSON format, with status code 400 Bad Request.
![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/ced54e11-7a50-4754-9ef1-e8b84f43b601)

```json
{
    "status": 400,
    "message": "Email is already in use or invalid.",
    "timeStamp": 1709425785939
}
```

  

### Login
#### Send POST request to localhost:8080/login
![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/b9870451-bbca-4fad-ba24-14b0abc0700c)  
#### This JWT token need to store in frontend

#### If email does not exists in database, error message will be in JSON format, with status code 401 Unanthorized.
![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/b6c173be-8a82-417b-8c10-c9727a4aee3c)

```json
{
    "status": 400,
    "message": "Email is already in use or invalid.",
    "timeStamp": 1709425785939
}
```

  

### URL encode : MD5, BASE62, BASE64
#### BASE64
#### Send POST request to localhost:8080/api/urls/base64
```json
{
	"longUrl" : "https://developer.mozilla.org/en-US/docs/Web/HTML"
}
```
![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/41422854-c4e3-4e62-82ed-46951d20614f)

```json
{
    "shortURL": "http://zipurl.com/Ksehr",
    "Title": "HTML: HyperText Markup Language | MDN",
    "longURL": "https://developer.mozilla.org/en-US/docs/Web/HTML",
    "Avatar": "https://developer.mozilla.org/favicon.ico"
}
```

#### BASE62
#### Send POST request to localhost:8080/api/urls/base62
![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/7ac42ead-862e-49f3-b30a-1cad0221814f)

#### MD5
#### Send POST request to localhost:8080/api/urls/md5
![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/563d08f4-aa4b-4c40-968a-d6037d37fc1f)  

#### If Long URL is not a valid URL or null, error message will be in JSON format, with status code 404 Not Found.
```json
{
	"longUrl" : "123"
}
```
  ![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/76c08095-059d-4d50-81c8-da29df1e4419)

```json
{
    "status": 404,
    "message": "Url does not valid: 123",
    "timeStamp": 1709426808966
}
```


### URL decode
#### To decode short URL, copy the ksehr in http://zipurl.com/Ksehr, send GET request to localhost:8080/api/urls/{shortUrl}
#### Make sure you have Bearer Token
localhost:8080/api/urls/Ksehr
![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/c2005b2f-4763-4516-be45-7fd67b3bd371)

  
#### If this short URL does not exist, error message will be in JSON format, with status code 404 Not Found.
![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/80e8f1d6-bb71-4127-a46e-3c0d9386348e)
```json
{
    "status": 404,
    "message": "URL not found for shortURL: Kseh",
    "timeStamp": 1709426725225
}
```

### JWT token invalid
#### During any POST or GET request, if JWT token is modified, or invalid, error message will be in reponse body with 401 Unauthorized
![image](https://github.com/ArcherUz/ZipURL-Services/assets/37568976/862e1a2f-c71c-4db5-9571-049c4593f34f)
```json
{
    "error": "JWT token is invalid or expired."
}
```


# Design Rate Limiter

A standalone rate limiter service for Java Spring Boot applications that supports multiple rate limiting algorithms and can be used as a separate service to control API request rates.

<p align="center">
  <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white" alt="Redis">
</p>

## Demo
![Demo](https://github.com/rahul07bagul/Rate-limiter/blob/main/assets/Rate_Limiter.gif)

## High Level Design
![HLD](https://github.com/rahul07bagul/Rate-limiter/blob/main/assets/design.png)

## Class Diagram
![LLD](https://github.com/rahul07bagul/Rate-limiter/blob/main/assets/uml_diagram.png)

## Features
- Multiple rate limiting algorithms supported:
  - Token Bucket
  - Leaking Bucket
  - Fixed Window Counter
- Redis-based storage.
- Dynamic rule loading from DOC files.
- Scheduled rule reloading (every 1 hour).
- REST API for rate limit checking and rule management.
- Standalone service architecture.

## Integration with Application
```sh
@Service
public class ApiGatewayService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${rate.limiter.url}")
    private String rateLimiterUrl;
    
    public boolean isRequestAllowed(String apiPath, String method, String clientId) {
        String resourceId = apiPath + ":" + method;
        
        RateLimitRequest request = new RateLimitRequest(resourceId, clientId);
        
        try {
            ResponseEntity<RateLimitResponse> response = restTemplate.postForEntity(
                rateLimiterUrl + "/check", request, RateLimitResponse.class);
                
            return response.getBody().isAllowed();
        } catch (HttpClientErrorException.TooManyRequests ex) {
            return false;
        }
    }
}
```

## Rate Limit Rules Format
```sh
API: /api/users
METHOD: GET
ALGORITHM: TOKEN_BUCKET
LIMIT: 100
PERIOD: 60
TIME_UNIT: SECONDS

API: /api/orders
METHOD: POST
ALGORITHM: LEAKING_BUCKET
LIMIT: 30
PERIOD: 60
TIME_UNIT: SECONDS
```

## API Usage
- Check Rate Limit
  ```sh
  POST /rate-limiter/api/v1/rate-limit/check

  //Request Body:
  {
  "resourceId": "/api/users:GET",
  "clientId": "user123"
  }

  //Response:
  {
  "allowed": true,
  "limit": 100,
  "remaining": 99,
  "resetTime": 1646324568
  }
  ```
- Get All Rules
  ```sh
  GET /rate-limiter/api/v1/rate-limit/rules
  ```
- Reload Rules
  ```sh
  POST /rate-limiter/api/v1/rate-limit/rules/reload
  ```
  

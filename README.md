# lulo-biometric-mfa


Enable Biometric validations 


### Add dependency
```
 implementation  'com.lulobank:biometric-api:0.1.6'
 implementation  'com.lulobank:biometric-implement:0.1.6'
```

### Add configuration classes

```java
 @SpringBootApplication
 @EnableAspectJAutoProxy(proxyTargetClass = true)
 @ComponentScan(basePackages = {
         "com.lulobank.x.starter.v3.adapter.config",
         "com.lulobank.biometric"
 }, basePackageClasses = {SentryConfigurationRunner.class})
```

### Add annotation to secure method

```java
@PostMapping("/forgotten")
@BiometricMFA(transaction = <Transaction-Type>)
public ResponseEntity<GenericResponse> forgotPin(@RequestHeader final HttpHeaders headers,
                                                     @PathVariable("idClient") final String idClient,
                                                     @Valid @RequestBody final NewPinRequest newPinRequest,

```
## Spring profile required variables

```properties
#development
services.authentication.mock=true 
services.authentication.url=http:authentication.capability.com

services.otp.mock=true 
services.otp.url=http:authentication.capability.com
```
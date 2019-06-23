# 캡슐화 예제

## 예제 1

### 캡슐화 전

```java
public AuthResult authenticate(String id, String password) {
    Member member = memberService.findById(id);

    if (member == null) {
        return AuthResult.NO_MATCH;
    }

    if (!member.getPassword().equals(password)) {
        return AuthResult.NO_MATCH;
    }

    if (member.getVerificationEmailStatus() != 2) {
        return AuthResult.NOT_EMAIL_VERIFIED;
    }

    return AuthResult.OK;
}
```

### 캡슐화 후

```java
public AuthResult authenticate(String id, String password) {
    Member member = memberService.findById(id);

    if (member == null) {
        return AuthResult.NO_MATCH;
    }

    // if (!member.getPassword().equals(password)) {
    if (!member.matchPassword(password)) {
        return AuthResult.NO_MATCH;
    }
    
    // if (member.getVerificationEmailStatus() != 2) {
    if (!member.isEmailVerified())
        return AuthResult.NOT_EMAIL_VERIFIED;
    }

    return AuthResult.OK;
}
```

## 예제 2

### 캡슐화 전

```java
Map<String, Long> timer = new HashMap<>();
timer.put("start", System.currentTimeMillis());

// ...

timer.put("end", System.currentTimeMillis());

long processSecTime = (timer.get("end") - timer.get("start")) / 1_000;
```

### 캡슐화 후

```java
public class Timer {
    private long startTime;
    private long endTime;

    public long start() {
        this.startTime = System.currentTimeMillis();
        return this.startTime;
    }

    public long end() {
        this.endTime = System.currentTimeMillis();
        return this.endTime;
    }

    public long processTime() {
        return processTime(TimeUnit.SECONDS);
    }

    public long processTime(TimeUnit timeUnit) {
        switch (timeUnit) {
            case SECONDS:
                return (this.endTime - this.startTime) / 1_000;
            case MILLISECONDS:
                return this.endTime - this.startTime;
            // ...
        }
    }
}
```
```java
Timer timer = new Timer();
timer.start();

// ...

timer.end();

long processSecTime = timer.processTime();
```

## 예제 3

### 캡슐화 전

```java
public void verifyEmail(String token) {
    Member member = findByToken(token);

    if (member == null) {
        throw new BadTokenException();
    }

    if (member.getVerificationEmailStatus() == 2) {
        throw new AlreadyVerifiedException();
    }

    member.setVerificationEmailStatus(2);
}
```

### 캡슐화 후

```java
public class Member {
    private int verificationEmailStatus;

    public void verifyEmail() {
        if (this.verificationEmailStatus == 2) {
            throw new AlreadyVerifiedException();
        }

        this.verificationEmailStatus == 2;
    }
}
```

```java
public void verifyEmail(String token) {
    Member member = findByToken(token);

    if (member == null) {
        throw new BadTokenException();
    }

    member.verifyEmail();
}
```
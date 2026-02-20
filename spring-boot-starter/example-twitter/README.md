# Tweet from OrqueIO Tasklist — Twitter Integration Example

This example demonstrates how to build a **BPMN-powered Twitter client** using OrqueIO and Spring Boot — allowing users to draft, review, and publish tweets through the Tasklist UI.

---

## What You Get

- ✅ **BPMN Twitter Workflow** — Draft → Review → Publish
- ✅ **Custom Embedded Forms** — HTML forms for creating and reviewing tweets
- ✅ **OrqueIO Webapps** — Cockpit, Tasklist, Admin UI
- ✅ **Twitter API Integration** — Post tweets via JavaDelegate
- ✅ **Offline Mode** — Test without Twitter credentials (console logging)
- ✅ **Spring Boot Configuration** — `application.yaml` for all settings

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |
| Twitter API Credentials | Optional (for live tweets) |

---

## Project Structure

```
example-twitter/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/.../twitter/
    │   │   ├── TwitterServletProcessApplication.java  # @SpringBootApplication
    │   │   ├── TweetContentDelegate.java             # Live Twitter posting
    │   │   └── TweetContentOfflineDelegate.java      # Console logging (default)
    │   └── resources/
    │       ├── META-INF/
    │       │   └── processes.xml
    │       ├── bpmn/
    │       │   └── TwitterDemoProcess.bpmn           # Tweet workflow
    │       ├── static/forms/
    │       │   ├── createTweet.html                  # Draft tweet form
    │       │   └── reviewTweet.html                  # Review tweet form
    │       └── application.yaml                      # Admin user + config
```

---

## The Twitter Workflow

```
[Start]
   │
[UserTask: Create Tweet] — Draft your tweet content
   │
[UserTask: Review Tweet] — Approve or reject
   │
[Gateway: Approved?]
   ├── Yes → [ServiceTask: Publish Tweet] → [End]
   └── No  → [End]
```

---

## How It Works

### 1. Add OrqueIO Spring Boot Starter Webapp

```xml
<dependency>
  <groupId>io.orqueio.bpm.springboot</groupId>
  <artifactId>orqueio-bpm-spring-boot-starter-webapp</artifactId>
  <version>1.0.4</version>
</dependency>
```

### 2. Enable Process Application

```java
@SpringBootApplication
@EnableProcessApplication
public class TwitterServletProcessApplication {

  public static void main(String... args) {
    SpringApplication.run(TwitterServletProcessApplication.class, args);
  }
}
```

### 3. Configure in `application.yaml`

```yaml
camunda.bpm.admin-user:
  id: demo
  password: demo
  firstName: Demo
```

See [OrqueIO Spring Boot configuration docs](https://docs.orqueio.io/manual/7.23/user-guide/spring-boot-integration/configuration/) for all options.

### 4. Two Tweet Posting Modes

**Offline Mode (default)** — Logs tweets to console:

```java
@Component("tweetAdapter")
public class TweetContentOfflineDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String content = (String) execution.getVariable("content");
    System.out.println("TWEET: " + content);
  }
}
```

**Live Twitter Mode** — Posts to Twitter API:

```java
@Component("tweetAdapter")
public class TweetContentDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String content = (String) execution.getVariable("content");
    // Twitter API call to post tweet
    twitter.updateStatus(content);
  }
}
```

Switch between modes by changing the `@Component("tweetAdapter")` annotation.

### 5. Service Task with Delegate Expression

The BPMN process references the Spring bean via delegate expression:

```xml
<serviceTask id="service_task_publish_on_twitter" 
             name="Publish on Twitter" 
             camunda:delegateExpression="#{tweetAdapter}">
</serviceTask>
```

---

## Running the Application

### Build and run

```bash
mvn clean install
java -jar target/orqueio-bpm-spring-boot-starter-example-twitter-0.0.1-SNAPSHOT.jar
```

Or use Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

### Access the Tasklist

Open your browser at:

**http://localhost:8080**

Login with:
- **Username**: `demo`
- **Password**: `demo`

### Start the Twitter Process

1. Go to **Tasklist**
2. Click **Start Process** → **Twitter Demo Process**
3. Fill in the "Create Tweet" form with your tweet content
4. Click **Complete**
5. Review your tweet in the "Review Tweet" task
6. Click **Approve** to publish (or **Reject** to cancel)
7. Check console output for the tweet content (offline mode) or your Twitter account (live mode)

---

## Switching to Live Twitter Mode

### 1. Get Twitter API Credentials

Create a Twitter Developer account and obtain:
- Consumer Key
- Consumer Secret
- Access Token
- Access Token Secret

### 2. Update `TweetContentDelegate`

Add your credentials:

```java
@Component("tweetAdapter")
public class TweetContentDelegate implements JavaDelegate {

  private static final String CONSUMER_KEY = "your_consumer_key";
  private static final String CONSUMER_SECRET = "your_consumer_secret";
  private static final String ACCESS_TOKEN = "your_access_token";
  private static final String ACCESS_TOKEN_SECRET = "your_access_token_secret";

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    // Twitter4J configuration and posting
  }
}
```

### 3. Remove `@Component` from `TweetContentOfflineDelegate`

Comment out or remove the annotation to disable offline mode.

---

## Custom Embedded Forms

### Create Tweet Form (`createTweet.html`)

```html
<form role="form">
  <div class="form-group">
    <label for="content">Tweet Content</label>
    <textarea cam-variable-name="content" 
              cam-variable-type="String" 
              class="form-control" 
              id="content" 
              maxlength="280" 
              required></textarea>
  </div>
</form>
```

### Review Tweet Form (`reviewTweet.html`)

```html
<form role="form">
  <div class="form-group">
    <label>Tweet to Publish</label>
    <p>{{content}}</p>
  </div>
  <div class="form-group">
    <label for="approved">Approved?</label>
    <input cam-variable-name="approved" 
           cam-variable-type="Boolean" 
           type="checkbox" 
           id="approved" />
  </div>
</form>
```

---

## Use Cases

- **Social Media Workflows** — Approval processes for tweets/posts
- **Content Review** — Multi-stage content approval
- **Custom Task Forms** — Embedded HTML forms in Tasklist
- **External API Integration** — Calling third-party services from BPMN
- **Delegate Expressions** — Spring bean injection in service tasks

---

## Next Steps

- Add your Twitter API credentials for live posting
- Customize the approval workflow (add multiple reviewers)
- Add character count validation in the form
- Implement scheduled tweet posting
- Add media attachments (images/videos)

---

## Source Files

| File | Description |
|------|-------------|
| [TwitterServletProcessApplication.java](src/main/java/io/orqueio/bpm/spring/boot/example/twitter/TwitterServletProcessApplication.java) | Main Spring Boot application |
| [TweetContentDelegate.java](src/main/java/io/orqueio/bpm/spring/boot/example/twitter/TweetContentDelegate.java) | Live Twitter posting implementation |
| [TweetContentOfflineDelegate.java](src/main/java/io/orqueio/bpm/spring/boot/example/twitter/TweetContentOfflineDelegate.java) | Offline console logging implementation |
| [TwitterDemoProcess.bpmn](src/main/resources/bpmn/TwitterDemoProcess.bpmn) | BPMN workflow definition |
| [createTweet.html](src/main/resources/static/forms/createTweet.html) | Draft tweet form |
| [reviewTweet.html](src/main/resources/static/forms/reviewTweet.html) | Review tweet form |
| [application.yaml](src/main/resources/application.yaml) | Spring Boot configuration |

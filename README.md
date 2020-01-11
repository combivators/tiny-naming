## Tiny naming: 提供基于RMI，IIOP，JavaWebService协议的微服调用功能。需要tiny-service和tiny-rest包。
## 设计目的
 - 提供网络和本地的微服实体调用功能。
 - 支持RMI，IIOP，JavaWebService协议。
 - 提供跨网络的异步消息处理机制和API。

##Usage

###1. MicroService Locator java
```java
public interface ServiceApi {
    @POST
    @Path("/v1/api/do/{channel}")
    @Produces(MediaType.APPLICATION_JSON)
    public void doAction(@PathParam("channel")String channel, @BeanParam String message);
}

ServiceFeatureLocator locator = new ServiceFeatureLocator();
ServiceApi api = locator.lookup("http://localhost:8080/v1/api/do/{channel}", ServiceApi.class);
api.doAction("ch1", "hello");
```


###2. Message Consumer Service java
```java
@Path("/v1/api/cs")
public class AppConsumer implements MessageConsumer {
    @POST
    @Path("do")
    @Produces(MediaType.APPLICATION_JSON)
    public void accept(@BeanParam Message message) {
        System.out.println("[Consumer] /v1/api/cs/do " + JsonParser.marshal(message));
    }
}
```

###2. Register a message consumer java
```java
ServiceFeatureLocator locator = new ServiceFeatureLocator();
MessageService bus = locator.lookup("http://localhost:8080/v1/api/msg", MessageService.class);

Message message = new Message();
message.setChannel("ch1");
message.setSource("http://localhost:8080/v1/api/tc2/do");
bus.register("ch1", BUS_TOKEN, message);
```

###2. Publish a message to bus java
```java
ServiceFeatureLocator locator = new ServiceFeatureLocator();
MessageService bus = locator.lookup("http://localhost:8080/v1/api/msg", MessageService.class);

bus.push("ch1", new Message("ch1", "Hello hoge"));
```

##More Detail, See The Samples

---
Email   : wuweibg@gmail.com

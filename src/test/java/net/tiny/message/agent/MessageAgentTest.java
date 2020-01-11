package net.tiny.message.agent;

import static org.junit.jupiter.api.Assertions.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import net.tiny.boot.ApplicationContext;
import net.tiny.boot.Main;
import net.tiny.context.RestWebServiceLocator;
import net.tiny.messae.api.Message;
import net.tiny.messae.api.MessageConsumer;
import net.tiny.messae.api.MessageService;
import net.tiny.service.ServiceContext;
import net.tiny.ws.Launcher;
import net.tiny.ws.rs.client.RestClient;

public class MessageAgentTest {

    static final String BUS_TOKEN = "tsObwsH4IfoxyDWj";

    @Test
    public void testMessageAgent() throws Exception {
        String[] args = new String[] { "-v", "-p", "test" };
        //asynchronous
        ApplicationContext context = new Main(args).run(false);
        assertEquals("test", context.getProfile());

        ServiceContext locator = context.getBean("rest.service", ServiceContext.class);
        assertNotNull(locator);
        Launcher launcher = context.getBootBean(Launcher.class);
        launcher = locator.lookup("launcher", Launcher.class);
        assertNotNull(launcher);
        Thread.sleep(1500L);

        //doAccessTestConsumers();
        doRegisterConsumers();
        doPushMessages();

        doRemoveClear();

        doPushMessages();

        // Access Server Controller
        RestClient client = new RestClient.Builder()
                .credentials("paas", "password")
                .build();
        RestClient.Response response = client.doGet(new URL("http://localhost:8080/sys/status"));
        assertEquals(response.getStatus(), HttpURLConnection.HTTP_OK);
        assertEquals("running", response.getEntity());
        response.close();

        response = client.doGet(new URL("http://localhost:8080/sys/stop"));
        assertEquals(response.getStatus(), HttpURLConnection.HTTP_OK);
        //launcher.stop();
        Thread.sleep(1000L);

        Future<Integer> result = context.getFuture();
        assertNotNull(result);
        assertEquals(0, result.get().intValue());
    }

    void doAccessTestConsumers() throws Exception {
        final RestWebServiceLocator locator = new RestWebServiceLocator();
        String endpoint = "http://localhost:8080/v1/api/tc1/do";
        MessageConsumer consumer = locator.lookup(endpoint, MessageConsumer.class);
        assertNotNull(consumer);

        Message msg = new Message();
        msg.setChannel("ch1");
        msg.setSource(endpoint);
        consumer.accept(msg);

        endpoint = "http://localhost:8080/v1/api/tc2/do";
        consumer = locator.lookup(endpoint, MessageConsumer.class);
        assertNotNull(consumer);

        msg = new Message();
        msg.setChannel("ch1");
        msg.setSource(endpoint);
        consumer.accept(msg);
    }

    void doRegisterConsumers() throws Exception {
        final RestWebServiceLocator locator = new RestWebServiceLocator();
        String endpoint = "http://localhost:8080/v1/api/msg";

        MessageService bus = locator.lookup(endpoint, MessageService.class);
        assertNotNull(bus);
        String sp = "http://localhost:8080/v1/api/tc2/do";
        Message message = new Message();
        message.setChannel("ch1");
        message.setSource(sp);
        bus.register("ch1", BUS_TOKEN, message);

        message = new Message();
        message.setChannel("ch2");
        message.setSource(sp);
        bus.register("ch2", BUS_TOKEN, message);
    }

    void doPushMessages() throws Exception {
        final RestWebServiceLocator locator = new RestWebServiceLocator();
        String endpoint = "http://localhost:8080/v1/api/msg";

        MessageService bus = locator.lookup(endpoint, MessageService.class);
        String channel = "ch1";
        Message message = new Message();
        message.setChannel(channel);
        message.setMessage("Hello hoge");
        bus.push(channel, message);

        channel = "ch2";
        message = new Message();
        message.setChannel(channel);
        message.setMessage("Hello Fuga");
        bus.push(channel, message);

        channel = "unknow";
        message = new Message();
        message.setChannel(channel);
        message.setMessage("Unknow");
        bus.push(channel, message);

        //bus.clear("ch1");
    }

    void doRemoveClear() throws Exception {
        final RestWebServiceLocator locator = new RestWebServiceLocator();
        String endpoint = "http://localhost:8080/v1/api/msg";

        MessageService bus = locator.lookup(endpoint, MessageService.class);
        assertNotNull(bus);

        String sp = "http://localhost:8080/v1/api/tc2/do";
        Message message = new Message();
        message.setSource(sp);
        bus.remove("ch1", BUS_TOKEN, message);
        bus.clear("ch2", BUS_TOKEN);
    }
}

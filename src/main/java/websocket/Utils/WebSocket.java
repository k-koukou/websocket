package websocket.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
/**
 * 佛曰:
 * 写字楼里写字间，写字间里程序员；
 * 程序人员写程序，又拿程序换酒钱。
 * 酒醒只在网上坐，酒醉还来网下眠；
 * 酒醉酒醒日复日，网上网下年复年。
 * 但愿老死电脑间，不愿鞠躬老板前；
 * 奔驰宝马贵者趣，公交自行程序员。
 * 别人笑我忒疯癫，我笑自己命太贱；
 * 不见满街漂亮妹，哪个归得程序员？
 *
 * @Classname WebSocket
 * @Description TODO
 * @Date 2023/9/16 14:51
 * @Created by 李院斌
 */

@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}")  // 接口路径 ws://localhost:8087/webSocket/userId;
public class WebSocket {
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    //  注：底下WebSocket是当前类名
    private static final CopyOnWriteArraySet<WebSocket> webSockets =new CopyOnWriteArraySet<>();

    /** concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     在外部可以获取此连接的所有websocket对象，并能对其触发消息发送功能，我们的定时发送核心功能的实现在与此变量 */

    // 用来存在线连接数
    private static final Map<String,Session> sessionPool = new HashMap<String,Session>();
    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value="userId")String userId) {
        try {
            this.session = session;
            webSockets.add(this);
            sessionPool.put(userId, session);
            log.info("【websocket消息】有新的连接，总数为:"+webSockets.size());
        } catch (Exception e) {
        }
    }
    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
            webSockets.remove(this);
            log.info("【websocket消息】连接断开，总数为:"+webSockets.size());
        } catch (Exception e) {
        }
    }
    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】收到客户端消息:"+message);
        sendAllMessage(message);
    }
    /** 发送错误时的处理
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误,原因: {} " , error.getMessage());
        error.printStackTrace();
    }
    // 此为广播消息
    public void sendAllMessage(String message) {
        log.info("【websocket消息】广播消息:"+message);
        for(WebSocket webSocket : webSockets) {
            try {
                if(webSocket.session.isOpen()) {
                    webSocket.session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 此为单点消息
    public void sendOneMessage(String userId, String message) {
        Session session = sessionPool.get(userId);
        if (session != null&&session.isOpen()) {
            try {
                log.info("【websocket消息】 单点消息:"+message);
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 此为单点消息(多人)
    public void sendMoreMessage(String[] userIds, String message) {
        for(String userId:userIds) {
            Session session = sessionPool.get(userId);
            if (session != null&&session.isOpen()) {
                try {
                    log.info("【websocket消息】 单点消息:"+message);
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


}


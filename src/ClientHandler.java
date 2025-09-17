import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ClientHandler类 - 客户端处理线程类
 * 作用：为每个客户端创建独立线程，处理消息收发
 * 知识点：
 * 1. 继承Thread - 创建多线程
 * 2. Socket通信 - 处理网络通信
 * 3. 输入输出流 - 读写数据
 * 4. 异常处理 - 处理网络异常
 */
public class ClientHandler extends Thread {
    private Socket clientSocket;  // 客户端套接字
    private BufferedReader reader;  // 输入流，用于读取客户端消息
    private PrintWriter writer;  // 输出流，用于向客户端发送消息
    private String username;  // 当前客户端的用户名
    private boolean isConnected = true;  // 连接状态

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            // 创建输入输出流
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("创建客户端处理线程失败：" + e.getMessage());
        }
    }

    /**
     * 线程运行方法
     * 作用：处理客户端的消息收发
     * 知识点：
     * 1. 重写run方法 - 定义线程要执行的任务
     * 2. 无限循环 - 持续监听客户端消息
     * 3. 字符串处理 - 解析客户端发送的消息格式
     */
    @Override
    public void run() {
        try {
            String message;
            // 持续监听客户端消息
            while (isConnected && (message = reader.readLine()) != null) {
                System.out.println("收到客户端消息：" + message);

                // 处理不同类型的消息
                handleMessage(message);
            }
        } catch (IOException e) {
            System.out.println("客户端连接异常：" + e.getMessage());
        } finally {
            // 清理资源
            closeConnection();
        }
    }

    /**
     * 处理客户端消息
     * 作用：根据消息类型执行不同操作
     * @param message 客户端发送的消息
     * 知识点：
     * 1. 字符串分割 - 使用split方法分割字符串
     * 2. 条件判断 - 根据消息类型执行不同逻辑
     * 3. 方法调用 - 调用UserManager的方法
     */
    private void handleMessage(String message) {
        // 解析消息格式：REGISTER:username=zhangsan&password=123 或 LOGIN:username=zhangsan&password=123
        if (message.startsWith("REGISTER:")) {
            // 处理注册请求
            handleRegister(message.substring(9)); // 去掉"REGISTER:"前缀
        } else if (message.startsWith("LOGIN:")) {
            // 处理登录请求
            handleLogin(message.substring(6)); // 去掉"LOGIN:"前缀
        } else {
            // 处理聊天消息
            handleChatMessage(message);
        }
    }

    /**
     * 处理注册请求
     * 作用：处理用户注册逻辑
     * @param data 注册数据（username=zhangsan&password=123）
     */
    private void handleRegister(String data) {
        String[] parts = data.split("&");
        if (parts.length == 2) {
            String[] usernamePart = parts[0].split("=");
            String[] passwordPart = parts[1].split("=");

            if (usernamePart.length == 2 && passwordPart.length == 2) {
                String username = usernamePart[1];
                String password = passwordPart[1];

                String result = UserManager.register(username, password);
                sendMessage(result);

                if (result.equals("注册成功！")) {
                    this.username = username;
                    System.out.println("用户 " + username + " 注册成功");
                }
            }
        }
    }

    /**
     * 处理登录请求
     * 作用：处理用户登录逻辑
     * @param data 登录数据（username=zhangsan&password=123）
     */
    private void handleLogin(String data) {
        String[] parts = data.split("&");
        if (parts.length == 2) {
            String[] usernamePart = parts[0].split("=");
            String[] passwordPart = parts[1].split("=");

            if (usernamePart.length == 2 && passwordPart.length == 2) {
                String username = usernamePart[1];
                String password = passwordPart[1];

                String result = UserManager.login(username, password);
                sendMessage(result);

                if (result.startsWith("登录成功")) {
                    this.username = username;
                    System.out.println("用户 " + username + " 登录成功");
                }
            }
        }
    }

    /**
     * 处理聊天消息
     * 作用：处理用户发送的聊天消息
     * @param message 聊天消息
     */
    private void handleChatMessage(String message) {
        if (username != null) {
            if ("GET_USERS".equals(message)) {
                // 处理获取在线用户列表的请求
                sendMessage(Server.getOnlineUsers());
            } else {
                // 处理普通聊天消息
                String chatMessage = "[" + username + "]: " + message;
                System.out.println(chatMessage);
                // 广播给所有客户端
                Server.broadcastMessage(chatMessage, this);
            }
        } else {
            sendMessage("请先登录或注册！");
        }
    }

    /**
     * 发送消息给客户端
     * 作用：向当前客户端发送消息
     * @param message 要发送的消息
     */
    public void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }

    /**
     * 关闭连接
     * 作用：清理资源，关闭连接
     * 知识点：
     * 1. 资源管理 - 关闭输入输出流和套接字
     * 2. 异常处理 - 处理关闭资源时可能出现的异常
     */
    private void closeConnection() {
        isConnected = false;
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.out.println("关闭连接时出错：" + e.getMessage());
        } finally {
            // 从服务器客户端列表中移除
            Server.removeClient(this);
        }
    }

    /**
     * 获取连接状态
     * @return 是否已连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }
}

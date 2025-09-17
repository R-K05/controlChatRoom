import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server类 - TCP服务器端主类
 * 作用：创建TCP服务器，处理多个客户端连接
 * 知识点：
 * 1. ServerSocket - 创建服务器套接字
 * 2. 多线程 - 为每个客户端创建独立线程
 * 3. 集合管理 - 管理所有连接的客户端
 */
public class Server {
    private static final int PORT = 8888;  // 服务器端口号
    private static ServerSocket serverSocket;  // 服务器套接字
    private static List<ClientHandler> clients = new ArrayList<>();  // 存储所有客户端连接
    private static boolean isRunning = true;  // 服务器运行状态

    public static void main(String[] args) {
        startServer();
    }

    /**
     * 启动服务器
     * 作用：创建服务器套接字，开始监听客户端连接
     * 知识点：
     * 1. ServerSocket - 服务器套接字，用于监听指定端口
     * 2. 异常处理 - try-catch处理网络异常
     * 3. 无限循环 - 持续监听客户端连接
     */
    private static void startServer() {
        try {
            // 创建服务器套接字，监听指定端口
            serverSocket = new ServerSocket(PORT);
            System.out.println("服务器启动成功，监听端口：" + PORT);
            System.out.println("等待客户端连接...");

            // 无限循环，持续监听客户端连接
            while (isRunning) {
                try {
                    // 等待客户端连接，这是一个阻塞方法
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("客户端连接成功：" + clientSocket.getInetAddress());

                    // 为每个客户端创建独立线程
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);  // 将客户端添加到列表中
                    clientHandler.start();  // 启动客户端处理线程

                } catch (IOException e) {
                    // 如果服务器被关闭，会抛出异常
                    if (isRunning) {
                        System.out.println("接受客户端连接时出错：" + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("服务器启动失败：" + e.getMessage());
        }
    }

    /**
     * 停止服务器
     * 作用：关闭服务器，释放资源
     */
    public static void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            System.out.println("服务器已关闭");
        } catch (IOException e) {
            System.out.println("关闭服务器时出错：" + e.getMessage());
        }
    }

    /**
     * 获取在线用户列表
     * 作用：返回当前在线的用户名列表
     * @return 在线用户名列表
     */
    public static String getOnlineUsers() {
        StringBuilder userList = new StringBuilder("在线用户：");
        for (ClientHandler client : clients) {
            if (client.getUsername() != null) {
                userList.append(client.getUsername()).append(" ");
            }
        }
        return userList.toString();
    }

    /**
     * 广播消息给所有客户端
     * 作用：将消息发送给所有连接的客户端
     * @param message 要广播的消息
     * @param sender 发送者（不发送给自己）
     */
    public static void broadcastMessage(String message, ClientHandler sender) {
        // 遍历所有客户端，发送消息
        for (ClientHandler client : clients) {
            if (client != sender && client.isConnected()) {
                client.sendMessage(message);
            }
        }
    }

    /**
     * 移除客户端
     * 作用：当客户端断开连接时，从列表中移除
     * @param client 要移除的客户端
     */
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("客户端已断开连接，当前在线人数：" + clients.size());
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client类 - 客户端主类
 * 作用：处理客户端的用户交互逻辑
 * 当前功能：启动程序，让用户选择注册或登录
 */
public class Client {
    private static Scanner scanner = new Scanner(System.in);
    private static Socket socket;  // 客户端套接字
    private static BufferedReader reader;  // 输入流
    private static PrintWriter writer;  // 输出流
    private static String currentUsername;  // 当前登录的用户名


    public static void main(String[] args) {
        System.out.println("=== 欢迎使用聊天室 ===");

        // 连接到服务器
        if (!connectToServer()) {
            System.out.println("无法连接到服务器，程序退出");
            return;
        }

        // 主循环：让用户可以重复选择操作
        while (true) {
            System.out.println("\n请选择操作：");
            System.out.println("1. 注册");
            System.out.println("2. 登录");
            System.out.println("3. 退出");
            System.out.print("请输入选择（1-3）");

            // 获取用户的输入内容
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    // 选择注册 - 下一步实现
                    // 注册功能
                    registerUser();
                    break;
                case "2":
                    // 选择登录 - 下一步实现
                    // 登录功能
                    loginUser();
                    break;
                case "3":
                    // 退出程序
                    System.out.println("感谢使用，再见！");
                    closeConnection();
                    System.exit(0);
                    break;
                default:
                    System.out.println("输入错误，请输入1-3之间的数字");
                    break;
            }
        }
    }

    /**
     * 连接到服务器
     * 作用：建立与服务器的TCP连接
     * @return 连接是否成功
     * 知识点：
     * 1. Socket - 客户端套接字，用于连接服务器
     * 2. 异常处理 - 处理连接异常
     * 3. 输入输出流 - 创建读写流
     */
    private static boolean connectToServer() {
        try {
            // 创建客户端套接字，连接到服务器
            socket = new Socket("localhost", 8888);
            // 创建输入输出流
            /**
             socket.getInputStream()：从 Socket 对象获取字节输入流，用于读取来自网络连接的字节数据
             InputStreamReader：将字节流转换为字符流（处理字符编码转换）
             BufferedReader：提供缓冲功能的字符读取器，能高效地读取文本数据，还提供了readLine()等方便的方法读取整行文本
             整体作用：创建一个高效的字符输入流，用于读取从 Socket 连接中传来的文本数据
             */
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            /**
             socket.getOutputStream()：从 Socket 对象获取字节输出流，用于向网络连接写入字节数据
             PrintWriter：字符输出流，提供了print()、println()等方便的方法写入各种类型的数据
             构造方法中的true参数：表示启用自动刷新功能，调用println()后会自动刷新缓冲区，无需手动调用flush()
             整体作用：创建一个方便的字符输出流，用于向 Socket 连接中写入文本数据
             */
            writer = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("已连接到服务器");
            return true;
        } catch (IOException e) {
            System.out.println("连接服务器失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 用户注册方法
     * 作用：处理用户注册的完整流程
     * 知识点：
     * 1. 方法封装 - 将注册逻辑封装成独立方法
     * 2. 用户输入处理 - 获取并验证用户输入
     * 3. 业务逻辑调用 - 调用UserManager的注册方法
     */
    public static void registerUser() {
        System.out.println("\n===用户注册===");
        // 获取用户名
        System.out.println("请输入用户名6-18位纯字母");
        String username = scanner.nextLine().trim();

        // 获取密码
        System.out.println("请输入（3-8位，第一位字母，后面数字）:");
        String password = scanner.nextLine().trim();

        // 发送注册请求到服务器
        String message = "REGISTER:username=" + username + "&password=" + password;
        writer.println(message);

        try {
            // 接收服务器响应
            String response = reader.readLine();
            System.out.println(response);

            if (response.equals("注册成功！")) {
                currentUsername = username;
                System.out.println("注册成功，可以开始聊天了！");
                startChat();
            }
        } catch (IOException e) {
            System.out.println("接收服务器响应失败：" + e.getMessage());
        }
    }

    /**
     * 用户登录方法
     * 作用：处理用户登录的完整流程
     * 知识点：
     * 1. 方法封装 - 将登录逻辑封装成独立方法
     * 2. 用户输入处理 - 获取用户名和密码
     * 3. 业务逻辑调用 - 调用UserManager的登录方法
     * 4. 登录成功后的处理 - 可以进入聊天功能
     */
    private static void loginUser() {
        System.out.println("\n=== 用户登录 ===");

        // 获取用户名
        System.out.print("请输入用户名：");
        String username = scanner.nextLine().trim();

        // 获取密码
        System.out.print("请输入密码：");
        String password = scanner.nextLine().trim();

        // 发送登录请求到服务器
        String message = "LOGIN:username=" + username + "&password=" + password;
        writer.println(message);

        try {
            // 接收服务器响应
            String response = reader.readLine();
            System.out.println(response);

            if (response.startsWith("登录成功")) {
                currentUsername = username;
                System.out.println("登录成功，可以开始聊天了！");
                startChat();
            }
        } catch (IOException e) {
            System.out.println("接收服务器响应失败：" + e.getMessage());
        }
    }

    /**
     * 开始聊天
     * 作用：处理聊天功能
     * 知识点：
     * 1. 多线程 - 创建接收消息的线程
     * 2. 用户输入 - 处理用户输入的聊天消息
     */
    private static void startChat() {
        System.out.println("\n=== 进入聊天室 ===");
        System.out.println("输入消息开始聊天，输入 'exit' 退出聊天");

        // 创建接收消息的线程
        Thread receiveThread = new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("接收消息失败：" + e.getMessage());
            }
        });
        receiveThread.start();

        // 主线程处理用户输入
        String input;
        while (true) {
            input = scanner.nextLine().trim();

            // 处理命令
            if (input.startsWith("/")) {
                if ("/exit".equals(input)) {
                    break;
                } else if ("/users".equals(input)) {
                    writer.println("GET_USERS");
                } else {
                    System.out.println("未知命令：" + input);
                }
            } else if (!input.isEmpty()) {
                // 发送聊天消息
                writer.println(input);
            }
        }

        // 关闭聊天
        closeConnection();
    }

    /**
     * 关闭连接
     * 作用：清理资源，关闭连接
     */
    private static void closeConnection() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("关闭连接时出错：" + e.getMessage());
        }
    }

}

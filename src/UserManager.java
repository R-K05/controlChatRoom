import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * UserManager类 - 用户管理类
 * 作用：管理所有用户数据，包括注册、登录验证、数据存储等功能
 * 知识点：
 * 1. 集合框架 - 使用ArrayList存储用户列表
 * 2. 文件I/O - 读写文件保存用户数据
 * 3. 异常处理 - 处理文件操作可能出现的异常
 * 4. 正则表达式 - 验证用户名和密码格式
 * 5. 静态方法 - 提供工具方法
 */
public class UserManager {
    private static final String USER_FILE = "users.txt"; // 用户数据文件名
    private static ArrayList<User> users = new ArrayList<>(); // 用户列表

    /**
     * 静态代码块
     * 作用：在类加载时自动执行，读取本地用户数据
     * 知识点：静态代码块在类第一次被加载时执行，且只执行一次
     */
    static {
        loadUsersFromFile();
    }

    /**
     * 从文件加载用户数据
     * 作用：程序启动时读取本地文件中的所有用户信息
     * 知识点：
     * 1. 文件读取 - 使用BufferedReader读取文件
     * 2. 异常处理 - try-catch处理IO异常
     * 3. 字符串分割 - split方法分割字符串
     */
    public static void loadUsersFromFile() {
        try(BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 文件格式：username,password
                String[] data = line.split(",");
                if(data.length == 2) {
                    users.add(new User(data[0], data[1]));
                }
            }
            System.out.println("Loaded " + users.size() + " users.");
        }catch (FileNotFoundException e){
            System.out.println("Users file not found.");
            try {
                new File(USER_FILE).createNewFile();
            }catch (IOException ex){
                System.out.println("创建用户文件失败：" + ex.getMessage());
            }
        }catch (IOException e) {
            System.out.println("读取用户文件失败：" + e.getMessage());
        }
    }

    /**
     * 保存用户数据到文件
     * 作用：将当前所有用户数据保存到本地文件
     */
    private static void saveUsersFromFile() {
        try(BufferedWriter writer =  new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User user : users) {
                writer.write(user.getUsername() + "," + user.getPassword());
                writer.newLine();
            }
            System.out.println("Saved " + users.size() + " users.");
        }catch (IOException e) {
            System.out.println("保存用户文件失败：" + e.getMessage());
        }
    }

    /**
     * 保存用户数据到文件
     * 作用：将当前所有用户数据保存到本地文件
     */
    private static void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User user : users) {
                writer.write(user.getUsername() + "," + user.getPassword());
                writer.newLine();
            }
            System.out.println("用户数据已保存到文件");
        } catch (IOException e) {
            System.out.println("保存用户文件失败：" + e.getMessage());
        }
    }

    /**
     * 验证用户名格式
     * 作用：检查用户名是否符合要求（6-18位纯字母）
     * @param username 用户名
     * @return 是否符合格式要求
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        // 正则表达式：^[a-zA-Z]{6,18}$
        // ^ 表示字符串开始，[a-zA-Z] 表示只能是大小写字母，{6,18} 表示长度6到18位，$ 表示字符串结束
        Pattern pattern = Pattern.compile("^[a-zA-Z]{6,18}$");
        return pattern.matcher(username).matches();
    }

    /**
     * 验证密码格式
     * 作用：检查密码是否符合要求（3-8位，第一位是字母，后面是数字）
     * @param password 密码
     * @return 是否符合格式要求
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        // 正则表达式：^[a-zA-Z][0-9]{2,7}$
        // ^[a-zA-Z] 表示第一位必须是字母，[0-9]{2,7} 表示后面2-7位必须是数字
        Pattern pattern = Pattern.compile("^[a-zA-Z][0-9]{2,7}$");
        return pattern.matcher(password).matches();
    }

    /**
     * 检查用户名是否已存在
     * 作用：注册时检查用户名是否已被使用
     * @param username 用户名
     * @return 是否已存在
     */
    public static boolean isUsernameExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }



    /**
     * 用户注册
     * 作用：注册新用户，验证格式和唯一性，保存到文件
     * @param username 用户名
     * @param password 密码
     * @return 注册结果信息
     */
    public static String register(String username, String password) {
        // 验证用户格式
        if(!isValidUsername(username)){
            return "注册失败：用户名格式不正确（6-18位纯字母）";
        }

        // 验证密码
        if(!isValidPassword(password)){
            return "注册失败：密码格式不正确（3-8位，第一位字母，后面数字）";
        }

        // 3. 检查用户名是否已存在
        if (isUsernameExists(username)) {
            return "注册失败：用户名已存在";
        }

        // 4. 创建新用户并保存
        users.add(new User(username, password));
        saveUsersToFile();
        return "注册成功！";
    }

    /**
     * 用户登录
     * 作用：验证用户名和密码是否正确
     * @param username 用户名
     * @param password 密码
     * @return 登录结果信息
     * 知识点：
     * 1. 用户认证 - 验证用户名和密码的匹配
     * 2. 集合查找 - 在ArrayList中查找特定用户
     * 3. 字符串比较 - 使用equals方法比较字符串
     */
    public static String login(String username, String password) {
        // 遍历所有用户，查找匹配的用户名和密码
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return "登录成功！欢迎 " + username;
            }
        }
        return "登录失败：用户名或密码错误";
    }
}

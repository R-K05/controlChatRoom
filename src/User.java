/**
 * User类 - 用户信息实体类
 * 作用：封装用户的基本信息，包括用户名和密码
 * 知识点：
 * 1. 实体类设计 - 用于封装数据
 * 2. 私有属性 - 数据封装，保证数据安全
 * 3. 构造方法 - 创建对象时初始化数据
 * 4. getter/setter方法 - 提供访问和修改私有属性的接口
 */
public class User {
    private String username;
    private String password;

    /**
     * 无参构造方法
     * 作用：创建空的User对象
     */
    public User() {
    }

    /**
     * 有参构造方法
     * 作用：创建User对象并初始化用户名和密码
     * @param username 用户名
     * @param password 密码
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 重写toString方法
     * 作用：将User对象转换为字符串，便于调试和显示
     * @return 用户信息的字符串表示
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

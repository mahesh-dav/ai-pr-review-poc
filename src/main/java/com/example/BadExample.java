import java.io.*;
import java.sql.*;
import java.util.*;

// Single-file demo with multiple flaws for code review testing
public class BadExample {
    public static void main(String[] args) {
        System.out.println("Starting BadExample...");

        Config cfg = Config.getInstance();
        cfg.set("db.url", "jdbc:mysql://localhost:3306/test");
        cfg.set("db.user", "root");
        cfg.set("db.pass", "root"); // hardcoded credentials (bad practice)

        UserRepository repo = new UserRepository(cfg);
        repo.init();
        repo.createUser("alice'); DROP TABLE users; --", "alice@example.com"); // SQL injection risk
        System.out.println("Users: " + repo.findAll());

        int avg = MathUtils.average(new int[]{1,2,3}); // integer division bug
        System.out.println("Average = " + avg);

        long fact = MathUtils.factorial(0); // factorial bug
        System.out.println("Factorial(0) = " + fact);

        FileProcessor fp = new FileProcessor();
        System.out.println("Lines in README: " + fp.countLines(new File("README.md"))); // possible recursion issue

        InMemoryCache<String,String> cache = new InMemoryCache<>();
        cache.put("a", "1");
        cache.put("b", "2");
        System.out.println("Cache size = " + cache.size());

        TaskScheduler ts = new TaskScheduler();
        ts.demoDeadlock(); // may hang
    }
}

// ====== Broken Singleton ======
class Config {
    private static Config INSTANCE; // not thread-safe
    private final Map<String,String> values = new HashMap<>();

    private Config() {}

    public static Config getInstance() {
        if (INSTANCE == null) {
            synchronized (Config.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Config();
                }
            }
        }
        return INSTANCE;
    }

    public void set(String k, String v) { values.put(k,v); }
    public String get(String k) { return values.get(k); }
}

// ====== User Repository with SQL Injection and Leaks ======
class UserRepository {
    private final Config cfg;
    private Connection connection;

    UserRepository(Config cfg) { this.cfg = cfg; }

    void init() {
        try {
            connection = DriverManager.getConnection(
                cfg.get("db.url"), cfg.get("db.user"), cfg.get("db.pass"));
            Statement st = connection.createStatement(); // not closed
            st.execute("CREATE TABLE IF NOT EXISTS users(id INT, name VARCHAR(255), email VARCHAR(255))");
        } catch (Exception e) {
            // swallow exception
        }
    }

    void createUser(String name, String email) {
        try {
            Statement st = connection.createStatement(); // not closed
            String sql = "INSERT INTO users(name,email) VALUES('" + name + "','" + email + "')";
            st.executeUpdate(sql); // SQL injection
        } catch (Exception ignored) {}
    }

    List<User> findAll() {
        List<User> out = new ArrayList<>();
        try {
            Statement st = connection.createStatement(); // not closed
            ResultSet rs = st.executeQuery("SELECT id,name,email FROM users");
            while (rs.next()) {
                out.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (Exception ignored) {}
        return out;
    }

    static class User {
        int id; String name; String email;
        User(int id,String name,String email){this.id=id;this.name=name;this.email=email;}

        @Override public boolean equals(Object o) {
            if (!(o instanceof User)) return false;
            User u = (User)o;
            return id==u.id && Objects.equals(name,u.name); // ignores email
        }
        // hashCode missing
        public String toString(){return "User("+id+","+name+","+email+")";}
    }
}

// ====== MathUtils with Bugs ======
class MathUtils {
    static int average(int[] arr) {
        int sum=0;
        for(int i=0;i<arr.length;i++) sum+=arr[i];
        return sum/arr.length; // integer division bug
    }

    static long factorial(int n) {
        if (n==0) return 0; // bug: should be 1
        return n*factorial(n-1); // stack overflow risk
    }
}

// ====== File Processor ======
class FileProcessor {
    int countLines(File file) {
        int count=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file)); // not closed
            String line;
            while((line=br.readLine())!=null){
                count++;
                if(line.trim().isEmpty()) count+=countLines(file); // recursion bug
            }
        } catch(Exception e){System.err.println("err:"+e);}
        return count;
    }
}

// ====== In-Memory Cache (Thread Leaks) ======
class InMemoryCache<K,V> {
    private final Map<K,V> map = new HashMap<>();
    private long lastCleanup=System.currentTimeMillis();

    void put(K k,V v){
        map.put(k,v);
        if(System.currentTimeMillis()-lastCleanup>1000){
            new Thread(this::cleanup).start(); // spawns unlimited threads
            lastCleanup=System.currentTimeMillis();
        }
    }
    V get(K k){return map.get(k);}
    int size(){return map.size();}
    void cleanup(){for(K k: map.keySet()) if(k==null) map.remove(k);}
}

// ====== Deadlock Demo ======
class TaskScheduler {
    private final Object lockA=new Object();
    private final Object lockB=new Object();

    void demoDeadlock(){
        Thread t1=new Thread(()->{
            synchronized(lockA){
                try{Thread.sleep(10);}catch(Exception ignored){}
                synchronized(lockB){System.out.println("t1 done");}
            }
        });
        Thread t2=new Thread(()->{
            synchronized(lockB){
                try{Thread.sleep(10);}catch(Exception ignored){}
                synchronized(lockA){System.out.println("t2 done");}
            }
        });
        t1.start(); t2.start();
    }
}

import java.util.*;

class program {
    public static void main(String[] args) {
        Person user = new Person("Roman", 17);
        Person another = new Person("Bob", 23);
        Server server = new Server();
        Bot bot = new Bot(4353532);
        user.connect(server);
        another.connect(server);
        bot.connect(server);
        try {
            another.message(server, "Hello!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        server.ban(bot);
        bot.connect(server);
        server.shutdown();
        System.out.println(another.isConnected(server));
    }
}

interface User {
    void connect(Server server);
    void disconnect(Server server);
    void message(Server server, String msg) throws Exception;
    String getName();
    boolean isConnected(Server server);
}

class Person implements User {
    private String name;
    private int age;

    public Person() {}
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override public String getName() { return "Person " + name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @Override
    public void connect(Server server) {
        try {
            server.connect(this);
        } catch (Exception e) { System.out.println(e.getMessage()); }
    }

    @Override
    public void disconnect(Server server) {
        server.disconnect(this);
    }

    @Override
    public boolean isConnected(Server server) {
        return server.checkConnection(this);
    }

    @Override
    public void message(Server server, String msg) throws Exception {
        if (!isConnected(server))
            throw new Exception("Error: user " + getName() + " is not connected to the server");
        server.message(this, msg);
    }
}

class Bot implements User {
    private int id;

    public Bot() {}
    public Bot(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    @Override public String getName() { return "Bot " + getId(); }

    @Override
    public void connect(Server server) {
        try {
            server.connect(this);
        } catch (Exception e) { System.out.println(e.getMessage()); }
    }

    @Override
    public void disconnect(Server server) {
        server.disconnect(this);
    }

    @Override
    public boolean isConnected(Server server) {
        return server.checkConnection(this);
    }

    @Override
    public void message(Server server, String msg) throws Exception {
        if (!isConnected(server))
            throw new Exception("Error: user " + getName() + " was disconnected");
    }
}

class Server {
    private List<User> clients;
    private List<User> blackList;

    public Server() {
        clients = new ArrayList<>();
        blackList = new ArrayList<>();
    }
    public Server(List<? extends User> clients) {
        this.clients = new ArrayList<>();
        blackList = new ArrayList<>();
        for (var client: clients) {
            try {
                connect(client);
            } catch (Exception e) { System.out.println(e.getMessage()); }
        }
    }

    public void connect(User client) throws Exception {
        if (blackList.contains(client)) throw new Exception("Error: the user " + client.getName() + " is in the blacklist");
        clients.add(client);
        System.out.println(client.getName() + " is connected!");
    }

    public void disconnect(User client) {
        clients.remove(client);
        System.out.println(client.getName() + " is disconnected");
    }

    public boolean checkConnection(User user) {
        return clients.contains(user);
    }

    public void ban(User client) {
        blackList.add(client);
        disconnect(client);
    }

    public void message(User sender, String msg) {
        if (clients.contains(sender))
            System.out.println(sender.getName() + " says: " + msg);
    }

    public void shutdown() {
        int len = clients.size();
        for (int i = 0; i < len; i++) disconnect(clients.get(0));
    }
}

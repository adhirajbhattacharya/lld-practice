package com.adhiraj.lldpractice;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LoadBalancerLld {
}

@AllArgsConstructor
class Server {
    int id;
    boolean isHealthy;

    void checkHealth() {
        isHealthy = true;
    }

    int getConnections() {
        return 1;
    }
}

interface LoadBalancerStrategy {
    Server getServer(List<Server> servers);
}

class RoundRobinLoadBalancerStrategy implements LoadBalancerStrategy {
    int index = -1;

    @Override
    public Server getServer(List<Server> servers) {
        index++;
        index = index % servers.size();
        Server server = servers.get(index);
        if (!server.isHealthy) return getServer(servers);
        return server;
    }
}

class LeastConnectionsLoadBalancerStrategy implements LoadBalancerStrategy {

    @Override
    public Server getServer(List<Server> servers) {
        return servers.stream().filter(server -> server.isHealthy)
                .min(Comparator.comparingInt(Server::getConnections)).orElse(servers.get(0));
    }
}

class LoadBalancer {
    private static final LoadBalancer loadBalancer = new LoadBalancer(new RoundRobinLoadBalancerStrategy());
    List<Server> servers;
    LoadBalancerStrategy loadBalancerStrategy;

    private LoadBalancer(LoadBalancerStrategy loadBalancerStrategy) {
        this.loadBalancerStrategy = loadBalancerStrategy;
        this.servers = new ArrayList<>();
    }

    public static LoadBalancer getInstance() {
        return loadBalancer;
    }

    void addServer(Server server) {
        servers.add(server);
    }

    void removeServer(Server server) {
        servers.remove(server);
    }

    Server getServer() {
        return loadBalancerStrategy.getServer(servers);
    }

    // health check scheduled thread
}



package ua.com.juja.microservices.teams.dao;

import java.util.List;

/**
 * @author Ivan Shapovalov
 */
public interface KeeperRepository {
    List<String> getDirections(String uuid);
}

package ua.com.juja.microservices.teams.service;

import org.springframework.stereotype.Service;
import ua.com.juja.microservices.teams.dao.KeeperRepository;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Ivan Shapovalov
 */
@Service
public class KeeperService {
    @Inject
    private KeeperRepository keeperRepository;

    public List<String> getDirections(String uuid) {
        return keeperRepository.getDirections(uuid);
    }
}

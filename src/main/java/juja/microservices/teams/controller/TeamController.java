package juja.microservices.teams.controller;

import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.entity.UserUuidRequest;
import juja.microservices.teams.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * @author Ivan Shapovalov
 */

@RestController
@RequestMapping(value = "/v1")
public class TeamController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TeamService teamService;

    @PostMapping(value = "/teams", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addTeam(@Valid @RequestBody TeamRequest request) {
        //TODO Should be implemented feature TMF-F1
        return null;
    }

    @PutMapping(value = "/teams/{uuid}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> deactivateTeam(@PathVariable String uuid) {
        logger.debug("Received dismiss team request. User id in Team: {}", uuid);
        Team team= teamService.dismissTeam(uuid);
        logger.info("Team dismissed. Team Id: {}", team.getId());
        logger.debug("Request dismiss team returned {}", team.toString());
        return ResponseEntity.ok(team.toString());
        return null;
    }

    @GetMapping(value = "/teams", produces = "application/json")
    public ResponseEntity<?> getAllActiveTeams() {
        //TODO Should be implemented feature TMF-F3
        return null;
    }

    @GetMapping(value = "/teams/{id}", produces = "application/json")
    public ResponseEntity<?> getTeamById(@PathVariable String id) {
        //TODO Should be implemented feature TMF-F4
        return null;
    }

    @GetMapping(value = "/teams/myteam/{uuid}", produces = "application/json")
    public ResponseEntity<?> getMyTeam(@PathVariable String uuid) {
        //TODO Should be implemented feature TMF-F5
        return null;
    }
}

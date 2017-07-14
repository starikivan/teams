package juja.microservices.teams.controller;

import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * @author Ivan Shapovalov
 */
@RestController
@RequestMapping(value = "/v1/teams")
public class TeamController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TeamService teamService;

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addTeam(@Valid @RequestBody TeamRequest request) {
        //TODO Should be implemented feature TMF-F1
        return null;
    }

    @PutMapping(value = "/users/{uuid}", produces = "application/json")
    public ResponseEntity<?> dismissTeam(@PathVariable String uuid) {
        logger.debug("Received dismiss team request. User id in Team: {}", uuid);
        Team team= teamService.dismissTeam(uuid);
        logger.info("Team dismissed. Team Id: {}", team.getId());
        logger.debug("Request dismiss team returned {}", team.toString());
        return ResponseEntity.ok(team);
    }

    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity<?> getAllActiveTeams() {
        //TODO Should be implemented feature TMF-F3
        return null;
    }

    @GetMapping(value = "/users/{uuid}", produces = "application/json")
    public ResponseEntity<?> getTeamById(@PathVariable String id) {
        //TODO Should be implemented feature TMF-F4 - TMF-F5
        return null;
    }

}

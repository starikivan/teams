package juja.microservices.teams.controller;

import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamDTO;
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
 * @author Andrii Sidun
 */
@RestController
@RequestMapping(value = "/v1/teams")
public class TeamController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TeamService teamService;

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addTeam(@Valid @RequestBody TeamRequest request) {
        logger.debug("Received add team request. Requested TeamRequest: {}", request);
        Team team = teamService.addTeam(request);
        logger.info("New team added. Id: {}", team.getId());
        logger.debug("Request add team returned {}", team);
        return ResponseEntity.ok(new TeamDTO(team));
    }

    @PutMapping(value = "/users/{uuid}", produces = "application/json")
    public ResponseEntity<?> deactivateTeam(@PathVariable String uuid) {
        logger.debug("Received deactivate team request. User id in Team: {}", uuid);
        Team team= teamService.deactivateTeam(uuid);
        logger.info("Team deacticated. Team Id: {}", team.getId());
        logger.debug("Request deactivate team returned {}", team.toString());
        return ResponseEntity.ok(new TeamDTO(team));
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

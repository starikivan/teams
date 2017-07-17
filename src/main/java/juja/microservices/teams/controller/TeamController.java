package juja.microservices.teams.controller;

import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.service.TeamService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TeamController {

    @Inject
    private TeamService teamService;

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addTeam(@Valid @RequestBody TeamRequest request) {
        log.debug("Received 'Add team' request {}", request);
        Team team = teamService.addTeam(request);
        log.info("New team added. Id: {}", team.getId());
        log.debug("Request 'Add team' returned {}", team);
        return ResponseEntity.ok(team);
    }

    @PutMapping(value = "/users/{uuid}", produces = "application/json")
    public ResponseEntity<?> deactivateTeam(@PathVariable String uuid) {
        log.debug("Received 'Deactivate team' request. Deactivate team of user {}", uuid);
        Team team= teamService.deactivateTeam(uuid);
        log.info("Team deacticated. Team Id: {}", team.getId());
        log.debug("Request 'Deactivate team' returned team {}", team);
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

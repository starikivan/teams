package ua.com.juja.microservices.teams.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.entity.TeamRequest;
import ua.com.juja.microservices.teams.service.TeamService;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

/**
 * @author Ivan Shapovalov
 * @author Andrii Sidun
 */
@RestController
@RequestMapping(value = "/" + "${teams.rest.api.version}" + "${teams.baseURL}")
@Slf4j
public class TeamController {

    @Inject
    private TeamService teamService;

    @PostMapping(value = "${teams.endpoint.activateTeam}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> activateTeam(@Valid @RequestBody TeamRequest request) {
        log.debug("Received 'Activate team' request {}", request);
        Team team = teamService.activateTeam(request);
        log.info("New team activated. Id {}", team.getId());
        log.debug("New team activated. Team {}", team.toString());
        return ResponseEntity.ok(team);
    }

    @PutMapping(value = "${teams.endpoint.deactivateTeam}" + "/{uuid}", produces = "application/json")
    public ResponseEntity<?> deactivateTeam(@PathVariable String uuid) {
        log.debug("Received 'Deactivate team' request. Deactivate team of user {}", uuid);
        Team team = teamService.deactivateTeam(uuid);
        log.debug("Request 'Deactivate team' returned team {}", team);
        log.info("Team deacticated. Team Id: {}", team.getId());
        return ResponseEntity.ok(team);
    }

    @GetMapping(value = "${teams.endpoint.getAllTeams}", produces = "application/json")
    public ResponseEntity<?> getAllActiveTeams() {
        log.debug("Received 'Get all teams' request");
        List<Team> teams = teamService.getAllActiveTeams();
        log.debug("Request 'Get all teams' returned teams {}", teams);
        log.info("Teams content received. Teams number: {}", teams.size());
        return ResponseEntity.ok(teams);
    }

    @GetMapping(value = "${teams.endpoint.getTeam}" + "/{uuid}", produces = "application/json")
    public ResponseEntity<?> getTeamByUuid(@PathVariable String uuid) {
        log.debug("Received 'Get team' request. Get team of user {}", uuid);
        Team team = teamService.getUserActiveTeam(uuid);
        log.debug("Request 'Get team' returned team {}", team);
        log.info("Team content received. Team Id: {}", team.getId());
        return ResponseEntity.ok(team);
    }
}

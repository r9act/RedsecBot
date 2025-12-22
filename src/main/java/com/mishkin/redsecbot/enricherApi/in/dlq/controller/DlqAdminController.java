package com.mishkin.redsecbot.enricherApi.in.dlq.controller;

import com.mishkin.redsecbot.enricherApi.in.StatsEnrichedEvent;
import com.mishkin.redsecbot.enricherApi.in.dlq.StatsEnrichedDlqReplayer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author a.mishkin
 */
@RestController
@RequestMapping("/admin/dlq")
public class DlqAdminController {

    private final StatsEnrichedDlqReplayer replayer;

    public DlqAdminController(StatsEnrichedDlqReplayer replayer) {
        this.replayer = replayer;
    }

    @PostMapping("/replay")
    public ResponseEntity<Void> replay(@RequestBody StatsEnrichedEvent event) {
        replayer.replay(event);
        return ResponseEntity.accepted().build();
    }
}


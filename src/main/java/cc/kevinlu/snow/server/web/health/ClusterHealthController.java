package cc.kevinlu.snow.server.web.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
@RestController
@RequestMapping("/cluster")
public class ClusterHealthController {

    @GetMapping(value = "/ping/{remote}")
    public String ping(@PathVariable(name = "remote") String remote) {
        log.info("receive ping req from remote [{}]", remote);
        return "PONG";
    }

}

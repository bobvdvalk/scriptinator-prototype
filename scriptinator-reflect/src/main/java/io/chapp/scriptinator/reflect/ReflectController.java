package io.chapp.scriptinator.reflect;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ReflectController {
    private final HttpServletRequest request;

    public ReflectController(HttpServletRequest request) {
        this.request = request;
    }

    @RequestMapping("/everything")
    public ResponseEntity<Map<String, Object>> everything() {
        Map<String, Object> result = new HashMap<>();
        ip(result);
        method(result);
        status(200, result);
        return response(result);
    }

    @RequestMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> status(@PathVariable int status) {
        return response(status(status, new HashMap<>()));
    }

    private Map<String, Object> status(int status, Map<String, Object> data) {
        data.put("status", status);
        return data;
    }

    @RequestMapping("/method")
    public ResponseEntity<Map<String, Object>> method() {
        return response(method(new HashMap<>()));
    }

    private Map<String, Object> method(Map<String, Object> data) {
        data.put("method", request.getMethod());
        return data;
    }

    @RequestMapping("/ip")
    public ResponseEntity<Map<String, Object>> ip() {
        return response(ip(new HashMap<>()));
    }

    private Map<String, Object> ip(Map<String, Object> data) {
        data.put("address", request.getRemoteAddr());
        return data;
    }

    private ResponseEntity<Map<String, Object>> response(Map<String, Object> data) {
        return ResponseEntity
                .status((int) data.getOrDefault("status", 200))
                .body(data);
    }
}

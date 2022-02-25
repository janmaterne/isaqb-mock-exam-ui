package org.isaqb.onlinetrainer.statistics;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class StatisticEndoint {

    private StatisticRepository repo;
    
    @GetMapping("/statistic")
    public String statistic(Model model) {
        model.addAttribute("data", repo.findAll());
        return "statistic";
    }
    
}

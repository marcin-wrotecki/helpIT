package com.helpit.foundation.Events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Controller
public class EventController {

    @Autowired
    private EventRepository repo;

    @Autowired
    private VolunteersRepository volunteersRepo;

    @GetMapping("/events")
    public String view(Model model) {
        List<Event> listEvents = listAll();
        model.addAttribute("listEvents", listEvents);
        return "events/index";
    }

    @GetMapping("/add")
    public String add(Model model) {
        Event event = new Event();
        model.addAttribute("event", event);
        return "events/add";
    }

    @RequestMapping(value="/create_event", method = RequestMethod.POST)
    public String createEventSubmit(@Valid @ModelAttribute("event") Event event) {
        save(event);
        return "events/show";
    }

    @RequestMapping("/delete/{id}")
    public String deleteEvent(@Valid @ModelAttribute("event") Event event) {
        delete(event.getId());
        return "events/del";
    }

    @RequestMapping("/sign/{id}")
    public String signForEvent(Model model, @Valid @ModelAttribute("event") Event event){
        Volunteers volunteers = new Volunteers();
        model.addAttribute("volunteers", volunteers);
        return "events/sign";
    }

    @RequestMapping(value="/sign")
    public String sign(@Valid @ModelAttribute("volunteers") Volunteers volunteers){
        saveVolunteers(volunteers);
        return "/events/sign";
    }

    public List<Event> listAll() {
        return repo.findAll();
    }

    public void save(Event event){
        repo.save(event);
    }

    public Event get(Long id){
        return repo.findById(id).get();
    }

    public void delete(Long id){
        repo.deleteById(id);
    }

    public void saveVolunteers(Volunteers volunteers){
        volunteersRepo.save(volunteers);
    }

}

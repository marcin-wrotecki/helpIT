package com.helpit.events;

import com.helpit.repositories.FoundationRepository;
import com.helpit.repositories.UserRepository;
import com.helpit.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;



@Controller
public class EventController {

    private final EventRepository repo;

    private final UserRepository userRepository;

    private final FoundationRepository foundationRepository;

    @Autowired
    public EventController(EventRepository repo, UserRepository userRepository, FoundationRepository foundationRepository) {
        this.repo = repo;
        this.userRepository=userRepository;
        this.foundationRepository = foundationRepository;

    }


    @RequestMapping(value="/events",method=RequestMethod.GET)
    public String returnEventView(WebRequest request, Model model) {
        List<Event> listEvents = listAll();
        model.addAttribute("listEvents", listEvents);

        return "events/index";
    }

    @GetMapping("/events/add")
    public String addEvent(WebRequest request, Model model) {
        Event event = new Event();
        model.addAttribute("event", event);

        return "events/add";
    }

    //don't change it to postmapping!
    @RequestMapping(value="events/create_event", method = RequestMethod.POST)
    public String showEventAfterCreate(@Valid @ModelAttribute("event") Event event) {
        saveEvent(event);
        return "events/show";
    }


    @RequestMapping(value="/events/show",method=RequestMethod.GET)
    public String showFoundationEventTemp() {
        return "events/show_default";
    }

    @RequestMapping("events/delete/{id}")
    public String deleteEvent(@Valid @ModelAttribute("event") Event event) {
        delete(event.getId());
        return "events/del";
    }

    @RequestMapping("/events/sign/{id}")
    public String signForEvent(@PathVariable String id, Model model, @Valid @ModelAttribute("event") Event event){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = auth.getName();
        User user = userRepository.findByEmail(currentUserName);
        event.getUsers().add(user);
        repo.save(event);
        model.addAttribute("event", event);
        return "events/sign";
    }

    public List<Event> listAll() {
        return repo.findAll();
    }


    public User findFoundationById(int id) {
        return userRepository.findById(id).get();
    }

    public List<Event> listSpecific(List<Event> list, User foundation) {
        list.removeIf(p->p.getFoundation()!=foundation);
        return list;
    }

    public void saveEvent(Event event){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = auth.getName();
        User user = userRepository.findByEmail(currentUserName);
        event.setFoundation(user);

        repo.save(event);
    }

    public Event getEvent(Long id){
        return repo.findById(id).get();
    }

    public void delete(Long id){
        repo.deleteById(id);
    }
}

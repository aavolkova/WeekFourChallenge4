package me.anna.demo.controllers;

import me.anna.demo.EducationRepository;
import me.anna.demo.EmploymentRepository;
import me.anna.demo.PersonRepository;
import me.anna.demo.SkillsRepository;
import me.anna.demo.models.Education;
import me.anna.demo.models.Employment;
import me.anna.demo.models.Person;
import me.anna.demo.models.Skills;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    EducationRepository educationRepository;

    @Autowired
    EmploymentRepository employmentRepository;

    @Autowired
    SkillsRepository skillsRepository;




    // Display the home page
    @GetMapping("/")
    public String showIndex(Model model)
    {
        String myMessage = "Welcome to the Robo Resume Application";
        model.addAttribute("message", myMessage);
        return "index";
    }




    // =========== Add Personal Info: ===========

    // Allow user to enter Person's information
    @GetMapping("/enterPerson")
    public String addPerson(Model model)
    {
        model.addAttribute("newPerson", new Person());
        return "enterPerson";
    }

    // Validate entered information and if it is valid display the result
    // Person must have first name, last name, and email address
    @PostMapping("/enterPerson")
    public String postPerson(@Valid @ModelAttribute("newPerson") Person person, BindingResult bindingResult)
    {
        if(bindingResult.hasErrors()){
            return "enterPerson";
        }

        personRepository.save(person);
        return "resultPerson";
    }




    // ============ Add Education =============

    // Allow user to enter Educational Achievements
    @GetMapping("/enterEducation")
    public String addEducation(Model model)
    {
        model.addAttribute("newEducation", new Education());
        model.addAttribute("updatevalue","new");
        return "enterEducation";
    }


    // Validate entered Educational Achievement and if it is valid display the result
    @PostMapping("/enterEducation")
    public String postEducation(@Valid @ModelAttribute("newEducation") Education education, BindingResult bindingResult, Model model, @RequestParam("update") String updateValue)
    {
//        System.out.println(updateValue);  // for Testing

        if(bindingResult.hasErrors()){
            return "enterEducation";
        }

        if(updateValue.equalsIgnoreCase("new") )
        {
            if (educationRepository.count() < 10)
            {
                educationRepository.save(education);
                return "resultEducation";
            }
            else
            {
                String myMessage = "You have reached the limit of 10 educational achievements per resume, please delete one of your previous educations to be able to add the new one.";
                model.addAttribute("message", myMessage);
                return "enterEducation";
            }
        }
        else{
            educationRepository.save(education);
            return "resultEducation";
        }

    }




    // ============ Add Employment ============

    // Allow user to enter Employment
    @GetMapping("/enterEmployment")
    public String addEmployment(Model model)
    {
        model.addAttribute("newEmployment", new Employment());
        model.addAttribute("updatevalue","new");
        return "enterEmployment";
    }

    // Validate entered work experience and if it is valid display the result
    @PostMapping("/enterEmployment")
    public String postEmployment(@Valid @ModelAttribute("newEmployment") Employment employment, BindingResult bindingResult, Model model, @RequestParam("update") String updateValue)
    {
//        System.out.println(updateValue);  // for Testing

        if(bindingResult.hasErrors()){
            return "enterEmployment";
        }

        // Allow person to leave the end date empty (assume he/she is still employed)
        if(employment.getEndDate() == null)
        {
            employment.setEndDate(LocalDate.now());
        }
        // If dates entered, do not accept the end date before the start date
        else if(employment.getEndDate().compareTo(employment.getStartDate())< 0)
        {
            return "enterEmployment";
        }



        if(updateValue.equalsIgnoreCase("new") )
        {
            if (employmentRepository.count() < 10)
            {
                employmentRepository.save(employment);
                return "resultEmployment";
            }
            else
            {
                String myMessage = "You have reached the limit of 10 work experiences per resume, please delete one of your employment info to be able to add the new one.";
                model.addAttribute("message", myMessage);
                return "enterEmployment";
            }
        }
        else{
            employmentRepository.save(employment);
            return "resultEmployment";
        }

    }




    // ============ Add Skills ============

    // Allow user to enter Skills
    @GetMapping("/enterSkills")
    public String addSkills(Model model)
    {
        model.addAttribute("newSkills", new Skills());
        model.addAttribute("updatevalue","new");
        return "enterSkills";
    }

    // Validate entered Skill and if it is valid display the result
    @PostMapping("/enterSkills")
    public String postSkills(@Valid @ModelAttribute("newSkills") Skills skills, BindingResult bindingResult, Model model, @RequestParam("update") String updateValue)
    {
//        System.out.println(updateValue);


        if(bindingResult.hasErrors()){
            return "enterSkills";
        }

       if(updateValue.equalsIgnoreCase("new") )
       {
           if (skillsRepository.count() < 20)
           {
               skillsRepository.save(skills);
               return "resultSkills";
           }
           else
           {
               String myMessage = "You have reached the limit of 20 skills per resume, please delete one of your skills to be able to add the new one.";
               model.addAttribute("message", myMessage);
               return "enterSkills";
           }
       }
       else{
           skillsRepository.save(skills);
           return "resultSkills";
       }

    }



///////////////////////////////////////////////////////////////////////////////////////////

    // ======== Display Person's info saved to the database ========
    @GetMapping("/displayPersonAllInfo")
    public String showAllPersonsInfo(Model model)
    {

        // Disabling Buttons doesn't work
        boolean disableEButton = true;
        boolean disableSButton = true;

        // Do not display a resume if user doesn't enter at least one educational achievement and one work experience
        //  Can not use switch or if statements for showing corresponding buttons!!!! - code doesn't work
        if(educationRepository.count() == 0 || skillsRepository.count() == 0)
        {

            if(educationRepository.count() == 0)
            {
                String myMessageEducation = "You must enter at least one educational achievement to be able to see the Robo Resume.";
                model.addAttribute("messageErrorEducation", myMessageEducation);
                disableEButton = false;
            }

            if(skillsRepository.count() == 0)
            {
                String myMessageSkills = "You must enter at least one skill to be able to see the Robo Resume.";
                model.addAttribute("messageErrorSkills", myMessageSkills);
                disableSButton = false;
            }

            System.out.println(disableEButton);
            System.out.println(disableSButton);

            model.addAttribute("disableEButton", disableEButton);
            model.addAttribute("disableEButton", disableSButton);
            return "error";
        }


        //Afua's help       Person myPerson = personRepository.findOne(Long.valueOf(1));
        Person myPerson = personRepository.findOne(new Long(1));

        List<Education> eduList = (List<Education>) educationRepository.findAll();
        myPerson.setEducationalAchievements((ArrayList<Education>) eduList);

        List<Employment> workList = (List<Employment>) employmentRepository.findAll();
        myPerson.setWorkExperience ((ArrayList<Employment>) workList);

        List<Skills> skillsList = (List<Skills>) skillsRepository.findAll();
        myPerson.setSkillsWithRating ((ArrayList<Skills>) skillsList);

        model.addAttribute("person", myPerson );

        // Add another attribute to the model
        // model.addAttribute("edu", myPerson.getEducationalAchievements() );
        // Display results on the console:
//        System.out.println("Name: " + myPerson.getFirstName());
//        System.out.println("Name: " + myPerson.getLastName());
//
//        for(Education e: eduList){
//            System.out.println("DegreeTitle: " + e.getDegreeTitle());
//            System.out.println("DegreeTitle: " + e.getEducationalInstitution());
//            System.out.println("DegreeTitle: " + e.getGraduateDate());
//        }




        // Disabling the "Add" buttons on the final Resume page when entered info reached the limit

        boolean disableEducationButton = false;
        if(educationRepository.count() >= 10)
        {
            disableEducationButton = true;
            model.addAttribute("disableEducationButton", disableEducationButton);
            String myMessage = "You have reached the limit of 10 educational achievements per resume, please delete one of your previous educations to be able to add the new one.";
            model.addAttribute("messageE", myMessage);

        }
        else{
            model.addAttribute("disableEducationButton", disableEducationButton);
        }


        boolean disableWorkButton = false;
        if(employmentRepository.count() >= 10)
        {
            disableWorkButton = true;
            model.addAttribute("disableWorkButton", disableWorkButton);
            String myMessage = "You have reached the limit of 10 work experiences per resume, please delete one of your employment info to be able to add the new one.";
            model.addAttribute("messageW", myMessage);

        }
        else{
            model.addAttribute("disableWorkButton", disableWorkButton);
        }


        boolean disableSkillButton = false;
        if(skillsRepository.count() >= 20)
        {
            disableSkillButton = true;
            model.addAttribute("disableSkillButton", disableSkillButton);
            String myMessage = "You have reached the limit of 20 skills per resume, please delete one of your skills to be able to add the new one.";
            model.addAttribute("messageS", myMessage);

        }
        else{
            model.addAttribute("disableSkillButton", disableSkillButton);
        }



        return "displayPersonAllInfo";
    }
////////////////////////////////////////////////////////////////////////////////////////////




    //============ Update Personal Info ===============

    @GetMapping("/updatePerson/{id}")
    public String updatePerson(@PathVariable("id") long id, Model model)
    {
        Person p = personRepository.findOne(id);
        model.addAttribute("newPerson", p);
        return "enterPerson";
    }




    //=========== Update, Delete Education =============

    @GetMapping("/updateEducation/{id}")
    public String updateEducation(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("newEducation", educationRepository.findOne(id));
        model.addAttribute("updatevalue","update");
        return "enterEducation";
    }

    @RequestMapping("/deleteEducation/{id}")
    public String delEducation(@PathVariable("id") long id){
        educationRepository.delete(id);
        return "redirect:/displayPersonAllInfo";
    }




    //=========== Update, Delete Employment ============

    @GetMapping("/updateEmployment/{id}")
    public String updateEmployment(@PathVariable("id") long id, Model model)
    {
        //  model.addAttribute("newEmployment", employmentRepository.findOne(id));
        Employment e = employmentRepository.findOne(id);
        model.addAttribute("newEmployment", e);
        model.addAttribute("updatevalue","update");
        return "enterEmployment";
    }

    @RequestMapping("/deleteEmployment/{id}")
    public String delEmployment(@PathVariable("id") long id){
        employmentRepository.delete(id);
        return "redirect:/displayPersonAllInfo";
    }




    //============= Update, Delete Skills ==============

    @GetMapping("/updateSkills/{id}")
    public String updateSkills(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("newSkills", skillsRepository.findOne(id));
        model.addAttribute("updatevalue","update");
        return "enterSkills";
    }

    @RequestMapping("/deleteSkills/{id}")
    public String delSkills(@PathVariable("id") long id){
        skillsRepository.delete(id);
        return "redirect:/displayPersonAllInfo";
    }
}

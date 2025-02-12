package com.group_one.todo_list.services;

import com.group_one.todo_list.models.*;
import com.group_one.todo_list.repositories.HouseholdRepository;
import com.group_one.todo_list.repositories.TaskRepository;
import com.group_one.todo_list.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.group_one.todo_list.models.Category.COOKING;
import static com.group_one.todo_list.models.Category.SHOPPING;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    HouseholdRepository householdRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    HouseholdService householdService;

    @Autowired
    TaskService taskService;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getUserById(long id){
        return userRepository.findById(id);
    }

    public User createUser(UserDTO userDTO){
        Optional<Household> household = householdRepository.findById(userDTO.getHouseholdId());

        if(household.isPresent()){
            User newUser = new User(
                    userDTO.getName(),
                    userDTO.getPreference(),
                    household.get(),
                    userDTO.getAge());
            return userRepository.save(newUser);

        }
        return null;
    }

    public User updateUser(long id, UserDTO userDTO) {
        Optional<User> userToUpdate = userRepository.findById(id);

        User updatedUser = userToUpdate.get();

        if (userDTO.getName() != null) {
            updatedUser.setName(userDTO.getName());
        }

        if (userDTO.getPreference() != null) {
            updatedUser.setPreference(userDTO.getPreference());
        }

        if (userDTO.getAge() != 0) {
            updatedUser.setAge(userDTO.getAge());
        }

        if (userDTO.getHouseholdId() != 0) {
            Optional<Household> householdOptional = householdRepository.findById(userDTO.getHouseholdId());
            if (householdOptional.isPresent()) {
                updatedUser.setHousehold(householdOptional.get());
            } else {
                return userRepository.save(updatedUser); // this means if a user household doesn't exist it won't change but every other element will.
            }
        }

        return userRepository.save(updatedUser);

    }

//    TODO: Add paths for filtering


//    derived query to get all the tasks assigned to user
//    set userid to null for those tasks
//    delete user method

    public String deleteUser(long id){
        List<Task> tasksDoneByUser = taskRepository.findByUserIdEquals(id);
        for (Task task : tasksDoneByUser){
            task.setUser(null);
            taskRepository.save(task);
        }
        userRepository.deleteById(id);
        return "User " + id + "ID deleted successfully.";
    }

    public List<Category> getAllCategories(){
        List<Category> enumList = new ArrayList<>();
        enumList.add(Category.CLEANING);
        enumList.add(Category.COOKING);
        enumList.add(Category.SHOPPING);
        enumList.add(Category.HOOVERING);
        enumList.add(Category.LAUNDRY);
        enumList.add(Category.GARDENING);
        return enumList;

    }


}

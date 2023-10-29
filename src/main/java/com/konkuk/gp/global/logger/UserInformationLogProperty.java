package com.konkuk.gp.global.logger;

import com.konkuk.gp.domain.dao.Todolist;
import com.konkuk.gp.domain.dto.response.UserInformationResponseDto;
import com.konkuk.gp.global.logger.message.user.DiseaseLog;
import com.konkuk.gp.global.logger.message.user.FoodLog;
import com.konkuk.gp.global.logger.message.user.TodoLog;
import com.konkuk.gp.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInformationLogProperty {

    private final MemberService memberService;

    @Setter
    private Long memberId;
    @Setter
    private List<DiseaseLog> diseaseLogs;
    @Setter
    private List<FoodLog> foodLogs;
    @Setter
    private List<TodoLog> todoLogs;

    @Transactional
    public void load() {
        UserInformationResponseDto information = memberService.getInformation(memberId);
        List<String> diseases = information.diseases();
        List<String> foods = information.preferredFoods();
        List<Todolist> todoList = information.todoList();

        diseaseLogs = diseases.stream()
                .map(name -> DiseaseLog.builder()
                        .name(name)
                        .build())
                .collect(Collectors.toList());
        for (DiseaseLog diseaseLog : diseaseLogs) {
            log.info("[UserInformation Log:load] disease : {}", diseaseLog.getName());
        }

        foodLogs = foods.stream()
                .map(name -> FoodLog.builder()
                        .name(name)
                        .build())
                .collect(Collectors.toList());

        for (FoodLog foodLog : foodLogs) {
            log.info("[UserInformation Log:load] food : {}", foodLog.getName());
        }

        todoLogs = todoList.stream()
                .map(td -> TodoLog.builder()
                        .name(td.getDescription())
                        .deadline(td.getDeadline())
                        .build())
                .collect(Collectors.toList());

        for (TodoLog todoLog : todoLogs) {
            log.info("[UserInformation Log:load] todo : {}", todoLog.getName());
        }
    }

    public Map<String, Object> getUserInformationLog() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("memberId", memberId);
        res.put("diseases", diseaseLogs);
        res.put("foods", foodLogs);
        res.put("todos", todoLogs);
        return res;
    }

    public void addDisease(String name) {
        this.diseaseLogs.add(new DiseaseLog(name));
    }

    public void deleteDisease(String name) {
        diseaseLogs = diseaseLogs.stream()
                .filter(d -> !(d.getName().equals(name)))
                .collect(Collectors.toList());
    }

    public void addFood(String name) {
        this.foodLogs.add(new FoodLog(name));
    }

    public void deleteFood(String name) {
        foodLogs = foodLogs.stream()
                .filter(f -> !(f.getName().equals(name)))
                .collect(Collectors.toList());
    }

    public void addTodo(String description, LocalDateTime deadLine) {
        todoLogs.add(TodoLog.builder()
                .name(description)
                .deadline(deadLine)
                .build());
    }

    public void completeTodo(String description, String caption) {
        for (TodoLog todoLog : todoLogs) {
            if (todoLog.getName().equals(description)) {
                todoLog.setFinish(true);
                todoLog.setFinishReason(caption);
            }
        }
    }
}

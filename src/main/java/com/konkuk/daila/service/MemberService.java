package com.konkuk.daila.service;

import com.konkuk.daila.domain.dao.Disease;
import com.konkuk.daila.domain.dao.Todolist;
import com.konkuk.daila.domain.dao.member.*;
import com.konkuk.daila.domain.dto.request.DiseaseCreateDto;
import com.konkuk.daila.domain.dto.request.TodolistCreateDto;
import com.konkuk.daila.domain.dto.request.UserInformationGenerateDto;
import com.konkuk.daila.domain.dto.response.UserInformationResponseDto;
import com.konkuk.daila.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final DiseaseService diseaseService;
    private final TodolistService checklistService;
    private final PreferredFoodRepository preferredFoodRepository;

    @Transactional
    public void saveInformation(UserInformationGenerateDto dto, Long memberId) {

        List<String> diseases = dto.diseases();
        List<String> preferredFoods = dto.preferredFoods();
        List<TodolistCreateDto> checklistCreateDtoList = dto.todoList();

        if (diseases != null) {
            diseases.forEach(
                    ds -> addDisease(ds, memberId)
            );
        }


        if (checklistCreateDtoList != null) {
            checklistCreateDtoList.forEach(
                    ck -> addChecklist(ck, memberId)
            );
        }

        if (preferredFoods != null) {
        preferredFoods.forEach(
                pf -> addFood(pf, memberId)
        );
        }
    }

    @Transactional
    public void addDisease(DiseaseCreateDto dto, Long memberId) {
        Member member = findMemberById(memberId);
        Disease disease = diseaseService.saveDisease(dto, member);
    }

    @Transactional
    public void addDisease(String diseaseName, Long memberId) {
        DiseaseCreateDto dto = new DiseaseCreateDto(diseaseName);
        this.addDisease(dto, memberId);
    }

    @Transactional
    public void addChecklist(TodolistCreateDto dto, Long memberId) {
        Member member = findMemberById(memberId);
        Todolist checklist = checklistService.saveTodolist(dto, member);
    }

    @Transactional
    public void addFood(String foodName, Long memberId) {
        Member member = findMemberById(memberId);
        PreferredFood food = PreferredFood.builder()
                .name(foodName)
                .member(member)
                .build();
        preferredFoodRepository.save(food);
    }

    @Transactional
    public String getInformationString(Long memberId) {
        UserInformationResponseDto information = getInformation(memberId);
        return informationToString(information);
    }

    @Transactional
    public UserInformationResponseDto getInformation(Long memberId) {
        Member member = findMemberById(memberId);

        List<String> diseaseList = member.getDiseaseList().stream()
                .map(ds -> ds.getDisease().getName())
                .toList();

        List<Todolist> checklist = member.getChecklistList().stream()
                .map(MemberTodolist::getTodolist)
                .toList();

        List<String> foods = member.getFoodList().stream()
                .map(fd -> fd.getName())
                .toList();
        return new UserInformationResponseDto(diseaseList, checklist, foods);
    }

    @Transactional
    public List<MemberTodolist> getTodolist(Long memberId) {
        Member member = findMemberById(memberId);
        return member.getChecklistList();
    }

    @Transactional
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> NotFoundException.MEMBER_NOT_FOUND);
    }

    private String informationToString(UserInformationResponseDto dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("user's disease : [");
        for (String disease : dto.diseases()) {
            sb.append(disease);
            sb.append(",");
        }
        sb.append("],");

        sb.append("user's todolist : [");
        for (Todolist chk : dto.todoList()) {
            sb.append("{");
            sb.append("\"description:\"" + chk.getDescription() + ",");
            sb.append("\"deadline:\"" + chk.getDeadline().toString() + ",");
            sb.append("},");
        }
        sb.append("],");

        sb.append("user's preferred food : [");
        for (String preferredFood : dto.preferredFoods()) {
            sb.append(preferredFood);
            sb.append(",");
        }
        sb.append("]");

        return sb.toString();
    }
}

package com.konkuk.gp.controller.http;

import com.konkuk.gp.controller.http.dto.FoodDto;
import com.konkuk.gp.service.dialog.DialogManager;
import com.konkuk.gp.domain.dao.Todolist;
import com.konkuk.gp.domain.dao.Disease;
import com.konkuk.gp.domain.dao.member.Member;
import com.konkuk.gp.domain.dto.request.TodolistCreateDto;
import com.konkuk.gp.domain.dto.request.DiseaseCreateDto;
import com.konkuk.gp.service.TodolistService;
import com.konkuk.gp.service.DiseaseService;
import com.konkuk.gp.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final DiseaseService diseaseService;
    private final TodolistService checklistService;
    private final DialogManager dialogManager;

    // Get Information
    @PostMapping("/disease/{memberId}")
    public ResponseEntity<Disease> createDisease(
            @RequestBody DiseaseCreateDto dto,
            @PathVariable Long memberId) {
        Member member = memberService.findMemberById(memberId);
        Disease disease = diseaseService.saveDisease(dto, member);
        dialogManager.sendUserInfo();
        return ResponseEntity.ok(disease);
    }

    @PostMapping("/todo/{memberId}")
    public ResponseEntity<Todolist> createTodo(
            @RequestBody TodolistCreateDto dto,
            @PathVariable Long memberId
    ) {
        Member member = memberService.findMemberById(memberId);
        Todolist checklist = checklistService.saveTodolist(dto, member);
        dialogManager.sendUserInfo();
        return ResponseEntity.ok(checklist);
    }

    @PostMapping("/food/{memberId}")
    public ResponseEntity<FoodDto> createFood(
            @RequestBody FoodDto dto,
            @PathVariable Long memberId
    ) {
        memberService.addFood(dto.foodName(), memberId);
        dialogManager.sendUserInfo();
        return ResponseEntity.ok(dto);
    }
}

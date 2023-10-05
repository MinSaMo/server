package com.konkuk.gp.controller.admin;

import com.konkuk.gp.core.socket.DialogManager;
import com.konkuk.gp.domain.dao.Checklist;
import com.konkuk.gp.domain.dao.Disease;
import com.konkuk.gp.domain.dao.member.Member;
import com.konkuk.gp.domain.dto.request.ChecklistCreateDto;
import com.konkuk.gp.domain.dto.request.DiseaseCreateDto;
import com.konkuk.gp.service.ChecklistService;
import com.konkuk.gp.service.DiseaseService;
import com.konkuk.gp.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member API", description = "사용자 정보 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final DiseaseService diseaseService;
    private final ChecklistService checklistService;
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
    public ResponseEntity<Checklist> createTodo(
            @RequestBody ChecklistCreateDto dto,
            @PathVariable Long memberId
    ) {
        Member member = memberService.findMemberById(memberId);
        Checklist checklist = checklistService.saveChecklist(dto, member);
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

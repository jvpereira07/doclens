package com.jvpereira.doclens.controller;

import com.jvpereira.doclens.dto.FieldRequestDTO;
import com.jvpereira.doclens.dto.FieldResponseDTO;
import com.jvpereira.doclens.model.field.FieldType;
import com.jvpereira.doclens.service.FieldService;
import com.jvpereira.doclens.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/fields")
@RequiredArgsConstructor
public class FieldViewController {

    private final FieldService fieldService;
    private final TemplateService templateService;

    @GetMapping
    public String listFields(Model model) {
        model.addAttribute("fields", fieldService.findAll());
        return "fields/list-fields";
    }

    @GetMapping("/new")
    public String newFieldForm(Model model) {
        model.addAttribute("field", new FieldRequestDTO());
        model.addAttribute("templates", templateService.findAll());
        model.addAttribute("fieldTypes", FieldType.values());
        return "fields/form-field";
    }

    @PostMapping
    public String createField(@ModelAttribute("field") FieldRequestDTO fieldRequestDTO) {
        fieldService.create(fieldRequestDTO);
        return "redirect:/fields";
    }

    @GetMapping("/edit/{id}")
    public String editFieldForm(@PathVariable("id") UUID id, Model model) {
        FieldResponseDTO field = fieldService.findById(id);

        FieldRequestDTO requestDTO = FieldRequestDTO.builder()
                .code(field.getCode())
                .name(field.getName())
                .description(field.getDescription())
                .required(field.getRequired())
                .type(field.getType())
                .templateId(field.getTemplateId())
                .build();

        model.addAttribute("field", requestDTO);
        model.addAttribute("fieldId", id);
        model.addAttribute("templates", templateService.findAll());
        model.addAttribute("fieldTypes", FieldType.values());
        return "fields/form-field";
    }

    @PostMapping("/edit/{id}")
    public String updateField(@PathVariable("id") UUID id, @ModelAttribute("field") FieldRequestDTO fieldRequestDTO) {
        fieldService.update(id, fieldRequestDTO);
        return "redirect:/fields";
    }

    @PostMapping("/delete/{id}")
    public String deleteField(@PathVariable("id") UUID id) {
        fieldService.delete(id);
        return "redirect:/fields";
    }
}
